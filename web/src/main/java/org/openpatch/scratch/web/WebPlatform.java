package org.openpatch.scratch.web;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.openpatch.scratch.internal.platform.AudioDevice;
import org.openpatch.scratch.internal.platform.Display;
import org.openpatch.scratch.internal.platform.Gl;
import org.openpatch.scratch.internal.platform.Platform;
import org.teavm.interop.Async;
import org.teavm.interop.AsyncCallback;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;
import org.teavm.jso.typedarrays.Int32Array;
import org.teavm.jso.typedarrays.Int8Array;

/**
 * Scratch for Java in a web page, compiled with TeaVM: WebGL2, the page's canvas, Web Audio and
 * the browser's decoders through s4j.js. The program's files are the files next to the page
 * ({@link WebFiles}). The browser decodes asynchronously; the calling thread waits, as TeaVM's
 * threads can.
 */
public final class WebPlatform implements Platform {

  private final Gl gl = new WebGl();
  private final Display display = new CanvasDisplay();
  private final WebFiles files;

  public WebPlatform() {
    this.files = WebFiles.install();
  }

  @Override
  public Gl gl() {
    return this.gl;
  }

  @Override
  public Display display() {
    return this.display;
  }

  @Override
  public AudioDevice openAudio() {
    return WebAudio.open();
  }

  @Override
  public Pixmap decodeImage(byte[] encoded, String name) throws IOException {
    DecodedImage image = decodeImage(Int8Array.copyFromJavaArray(encoded));
    if (image.getArgb() == null) {
      throw new IOException("Could not decode " + name + ": " + image.getError());
    }
    return new Pixmap(image.getWidth(), image.getHeight(), image.getArgb().copyToJavaArray());
  }

  @Override
  public void writePng(Path file, Pixmap image) throws IOException {
    throw new IOException("A web page cannot write files (" + file + ").");
  }

  @Override
  public Pcm decodeVorbis(byte[] encoded, String name) throws IOException {
    DecodedSound sound = decodeVorbis(Int8Array.copyFromJavaArray(encoded));
    if (sound.getSamples() == null) {
      throw new IOException("This browser could not decode " + name);
    }
    return new Pcm(sound.getChannels(), sound.getRate(), sound.getSamples().copyToJavaArray());
  }

  @Override
  public void showError(String title, String message) {
    message(title, message);
  }

  @Override
  public void exit(int status) {
    ended(status);
  }

  @Override
  public void halt(int status) {
    ended(status);
  }

  @Override
  public Path classpathFolder(String folder) {
    return null; // a page has no class path; its files are next to it
  }

  @Override
  public List<String> listResources(String folder) {
    return this.files.list(folder);
  }

  @Override
  public Object codeLocation(Class<?> type) {
    return null;
  }

  // ---- s4j.js -------------------------------------------------------------------------------

  interface DecodedImage extends JSObject {
    @JSProperty
    int getWidth();

    @JSProperty
    int getHeight();

    @JSProperty
    Int32Array getArgb();

    @JSProperty
    String getError();
  }

  interface DecodedSound extends JSObject {
    @JSProperty
    int getChannels();

    @JSProperty
    int getRate();

    @JSProperty
    Int8Array getSamples();
  }

  @JSFunctor
  interface ImageDone extends JSObject {
    void done(DecodedImage image);
  }

  @JSFunctor
  interface SoundDone extends JSObject {
    void done(DecodedSound sound);
  }

  @Async
  private static native DecodedImage decodeImage(Int8Array bytes);

  private static void decodeImage(Int8Array bytes, AsyncCallback<DecodedImage> callback) {
    decodeImage(bytes, (ImageDone) callback::complete);
  }

  @JSBody(params = {"bytes", "done"}, script = "S4J.decode.image(bytes, done);")
  private static native void decodeImage(Int8Array bytes, ImageDone done);

  @Async
  private static native DecodedSound decodeVorbis(Int8Array bytes);

  private static void decodeVorbis(Int8Array bytes, AsyncCallback<DecodedSound> callback) {
    decodeVorbis(bytes, (SoundDone) callback::complete);
  }

  @JSBody(params = {"bytes", "done"}, script = "S4J.decode.vorbis(bytes, done);")
  private static native void decodeVorbis(Int8Array bytes, SoundDone done);

  @JSBody(params = {"title", "text"}, script = "S4J.glfw.message(title, text);")
  private static native void message(String title, String text);

  @JSBody(params = "status", script = "S4J.glfw.ended(status);")
  private static native void ended(int status);
}
