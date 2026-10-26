package org.openpatch.scratch.internal;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.openpatch.scratch.Layer;
import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;
import org.openpatch.scratch.extension.Canvas;
import org.openpatch.scratch.extension.Extension;
import org.openpatch.scratch.extension.ExtensionContext;
import org.openpatch.scratch.internal.draw.Item;

/**
 * The installed extensions' hooks and where the core calls them. One per game; each extension
 * gets a context of its own, so a hook that throws can name its extension.
 */
public final class ExtensionHost {

  /** A hook with the extension it belongs to; switched off after its first exception. */
  private static final class Hook<T> {
    final Extension owner;
    final T body;
    volatile boolean off;

    Hook(Extension owner, T body) {
      this.owner = owner;
      this.body = body;
    }

    void run(Consumer<T> call) {
      if (this.off) {
        return;
      }
      try {
        call.accept(this.body);
      } catch (LearnerCallFailed e) {
        throw e; // learner code's fault, not the extension's: the game stops as for any learner error
      } catch (RuntimeException e) {
        this.off = true;
        System.err.println("Scratch for Java: the extension \"" + this.owner.id() + "\" failed and one of its "
            + "parts is switched off: " + e);
        e.printStackTrace();
      }
    }
  }

  /** An exception from learner code an extension called through {@code dispatch}. */
  public static final class LearnerCallFailed extends RuntimeException {
    LearnerCallFailed(Throwable cause) {
      super(cause);
    }
  }

  private static final Map<String, ExtensionContext.ImageDecoder> IMAGE_FORMATS = new ConcurrentHashMap<>();

  private final Game game;
  private final List<Extension> installed = new CopyOnWriteArrayList<>();
  private final List<Hook<Consumer<Stage>>> stageCreated = new CopyOnWriteArrayList<>();
  private final List<Hook<Consumer<Stage>>> stageDisposed = new CopyOnWriteArrayList<>();
  private final List<Hook<Consumer<Stage>>> beforeTick = new CopyOnWriteArrayList<>();
  private final List<Hook<Consumer<ExtensionContext.KeyEvent>>> keys = new CopyOnWriteArrayList<>();
  private final List<Hook<Consumer<ExtensionContext.MouseEvent>>> mice = new CopyOnWriteArrayList<>();
  private final Map<Layer, List<Hook<ExtensionContext.LayerDrawer>>> layerDrawers = new EnumMap<>(Layer.class);
  private final List<Hook<ExtensionContext.LayerDrawer>> overlayDrawers = new CopyOnWriteArrayList<>();
  private final List<Hook<Consumer<ExtensionContext.InputSnapshot>>> polls = new CopyOnWriteArrayList<>();
  private final List<Hook<BiConsumer<Stage, ExtensionContext.Frame>>> afterFrame = new CopyOnWriteArrayList<>();
  // the input snapshot: the window thread's states and the game thread's copies
  private final Map<ExtensionContext.InputKey<?>, Object> polled = new IdentityHashMap<>();
  private final Map<ExtensionContext.InputKey<?>, Object> handedOver = new IdentityHashMap<>();
  private volatile boolean loading;

  public ExtensionHost(Game game) {
    this.game = game;
    for (Layer layer : Layer.values()) {
      this.layerDrawers.put(layer, new CopyOnWriteArrayList<>());
    }
  }

  /** Installs extensions; one that throws while installing is left out with a message. */
  public void install(List<Extension> extensions) {
    for (Extension e : extensions) {
      try {
        e.install(new Context(e));
        this.installed.add(e);
      } catch (RuntimeException ex) {
        System.err.println("Scratch for Java: the extension \"" + e.id() + "\" could not be installed: " + ex);
      }
    }
  }

  public List<Extension> getInstalled() {
    return List.copyOf(this.installed);
  }

  public void setLoading(boolean loading) {
    this.loading = loading;
  }

  /** The decoder an extension registered for a file name, or null. */
  public static ExtensionContext.ImageDecoder imageFormat(String path) {
    String lower = path.toLowerCase(java.util.Locale.ROOT);
    for (var e : IMAGE_FORMATS.entrySet()) {
      if (lower.endsWith(e.getKey())) {
        return e.getValue();
      }
    }
    return null;
  }

  // ---- where the core calls the hooks -------------------------------------------------------

  public void stageCreated(Stage stage) {
    for (var h : this.stageCreated) {
      h.run(c -> c.accept(stage));
    }
  }

  public void stageDisposed(Stage stage) {
    for (var h : this.stageDisposed) {
      h.run(c -> c.accept(stage));
    }
  }

  public void beforeTick(Stage stage) {
    for (var h : this.beforeTick) {
      h.run(c -> c.accept(stage));
    }
  }

  public void key(ExtensionContext.KeyEvent event) {
    for (var h : this.keys) {
      h.run(c -> c.accept(event));
    }
  }

  public void mouse(ExtensionContext.MouseEvent event) {
    for (var h : this.mice) {
      h.run(c -> c.accept(event));
    }
  }

  /** What the layer's drawers draw this frame, for the draw list. */
  public List<Item> drawLayer(Layer layer, Stage stage) {
    List<Hook<ExtensionContext.LayerDrawer>> drawers = this.layerDrawers.get(layer);
    if (drawers.isEmpty()) {
      return List.of();
    }
    List<Item> items = new ArrayList<>();
    for (var h : drawers) {
      Canvas canvas = CanvasAccess.create();
      try {
        h.run(d -> d.draw(stage, canvas));
      } catch (LearnerCallFailed e) {
        // made while building the draw list: report it here, the frame is drawn without the rest
        this.game.failLearner(e.getCause());
      }
      items.addAll(CanvasAccess.finish(canvas));
    }
    return items;
  }

  public List<Item> drawOverlay(Stage stage) {
    if (this.overlayDrawers.isEmpty()) {
      return List.of();
    }
    List<Item> items = new ArrayList<>();
    for (var h : this.overlayDrawers) {
      Canvas canvas = CanvasAccess.create();
      h.run(d -> d.draw(stage, canvas));
      items.addAll(CanvasAccess.finish(canvas));
    }
    return items;
  }

  /** Window thread, once per batch: the extensions poll their devices. */
  public void poll() {
    if (this.polls.isEmpty()) {
      return;
    }
    ExtensionContext.InputSnapshot snapshot = new ExtensionContext.InputSnapshot() {
      @Override
      @SuppressWarnings("unchecked")
      public <T> T get(ExtensionContext.InputKey<T> key) {
        return (T) ExtensionHost.this.polled.computeIfAbsent(key, k -> key.create());
      }
    };
    for (var h : this.polls) {
      h.run(c -> c.accept(snapshot));
    }
  }

  /**
   * Window thread, while the game thread waits for its batch: the polled states are copied into
   * the game thread's.
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  public void handOver() {
    for (var e : this.polled.entrySet()) {
      ExtensionContext.InputKey key = e.getKey();
      Object into = this.handedOver.computeIfAbsent(key, k -> key.create());
      key.copy(e.getValue(), into);
    }
  }

  public void afterFrame(Stage stage, ExtensionContext.Frame frame) {
    for (var h : this.afterFrame) {
      h.run(c -> c.accept(stage, frame));
    }
  }

  public boolean wantsFrames() {
    return !this.afterFrame.isEmpty();
  }

  // ---- one extension's view -------------------------------------------------------------------

  private final class Context implements ExtensionContext {
    private final Extension owner;

    Context(Extension owner) {
      this.owner = owner;
    }

    private <T> Hook<T> hook(T body) {
      return new Hook<>(this.owner, body);
    }

    @Override
    public void onStageCreated(Consumer<Stage> hook) {
      ExtensionHost.this.stageCreated.add(this.hook(hook));
    }

    @Override
    public void onStageDisposed(Consumer<Stage> hook) {
      ExtensionHost.this.stageDisposed.add(this.hook(hook));
    }

    @Override
    public void beforeTick(Consumer<Stage> hook) {
      ExtensionHost.this.beforeTick.add(this.hook(hook));
    }

    @Override
    public void onKey(Consumer<KeyEvent> hook) {
      ExtensionHost.this.keys.add(this.hook(hook));
    }

    @Override
    public void onMouse(Consumer<MouseEvent> hook) {
      ExtensionHost.this.mice.add(this.hook(hook));
    }

    @Override
    public void onExit(Runnable hook) {
      Hook<Runnable> h = this.hook(hook);
      ExtensionHost.this.game.onExit(() -> h.run(Runnable::run));
    }

    @Override
    public void onLearnerError(Consumer<LearnerError> hook) {
      Hook<Consumer<LearnerError>> h = this.hook(hook);
      ExtensionHost.this.game.onLearnerError(
          e -> h.run(c -> c.accept(new LearnerError(e.error(), e.learnerFrame()))));
    }

    @Override
    public void addLayerDrawer(Layer layer, LayerDrawer drawer) {
      ExtensionHost.this.layerDrawers.get(layer).add(this.hook(drawer));
    }

    @Override
    public void addOverlayDrawer(LayerDrawer drawer) {
      ExtensionHost.this.overlayDrawers.add(this.hook(drawer));
    }

    @Override
    public <T> void dispatch(Stage stage, Class<T> listener, Consumer<? super T> call) {
      requireGameThread("dispatch");
      // learner code: its exceptions stop the game like any learner exception
      try {
        if (listener.isInstance(stage)) {
          call.accept(listener.cast(stage));
        }
        for (Sprite s : Game.spritesOf(stage)) {
          if (listener.isInstance(s) && s.getStage() == stage) {
            call.accept(listener.cast(s));
          }
        }
      } catch (LearnerCallFailed e) {
        throw e;
      } catch (RuntimeException | Error e) {
        throw new LearnerCallFailed(e);
      }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T input(InputKey<T> key) {
      requireGameThread("input");
      return (T) ExtensionHost.this.handedOver.computeIfAbsent(key, k -> key.create());
    }

    @Override
    public long getTickCount() {
      return ExtensionHost.this.game.getTickCount();
    }

    @Override
    public boolean isLoading() {
      return ExtensionHost.this.loading;
    }

    @Override
    public void onInputPoll(Consumer<InputSnapshot> hook) {
      ExtensionHost.this.polls.add(this.hook(hook));
    }

    @Override
    public void afterFrame(BiConsumer<Stage, Frame> hook) {
      ExtensionHost.this.afterFrame.add(this.hook(hook));
    }

    @Override
    public void runOnGameThread(Runnable task) {
      ExtensionHost.this.game.runOnGameThread(task);
    }

    @Override
    public void registerImageFormat(String suffix, ImageDecoder decoder) {
      IMAGE_FORMATS.put(suffix.toLowerCase(java.util.Locale.ROOT), decoder);
    }

    private void requireGameThread(String method) {
      if (!ExtensionHost.this.game.isGameThread()) {
        throw new IllegalStateException(method + " belongs to the game thread, not to " + Thread.currentThread().getName()
            + " (extension \"" + this.owner.id() + "\").");
      }
    }
  }

  /** Attachments: an extension's state per stage or sprite, made on first use. */
  public static final class Attachments {
    private final Map<Class<?>, Object> values = new HashMap<>();

    public synchronized <T> T get(Class<T> type, java.util.function.Supplier<T> factory) {
      return type.cast(this.values.computeIfAbsent(type, t -> factory.get()));
    }
  }
}
