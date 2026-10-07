import org.openpatch.scratch.*;
import org.openpatch.scratch.extensions.shader.Shaders;
import org.openpatch.scratch.extensions.sorting.Sorting;

public class LifecycleProbe {
    public static void main(String[] args) {
        new ProbeStage();
    }
}

class ProbeSprite extends Sprite {
    static int constructions = 0;
    int score = 7;
    int[] shared = { 1 };
    int starts = 0;
    int messages = 0;
    int removals = 0;
    ProbeSprite() {
        constructions++;
        addCostume("slimeGreen");
        setPosition(30, -20);
        getPen().setSize(5);
    }
    public void whenStartsAsClone() {
        if (!isClone() || getStage() == null) throw new RuntimeException("clone hook ordering");
        starts++;
        score++;
    }
    public void whenIReceive(String message) { messages++; }
    public void whenRemovedFromStage() {
        if (getStage() != null) throw new RuntimeException("removal hook ordering");
        removals++;
    }
    public void run() { changeX(1); }
}

class ProbeStage extends Stage {
    ProbeSprite original;
    ProbeSprite copy;
    int frames = 0;
    ProbeStage() {
        super(600, 240);
        Window.getInstance().pause();
        original = new ProbeSprite();
        add(original);
        copy = (ProbeSprite) original.clone();
        copy.getPen().setSize(9);
        copy.shared[0] = 2;
        System.out.println("clone.constructor=" + ProbeSprite.constructions);
        System.out.println("clone.score=" + copy.score);
        System.out.println("original.score=" + original.score);
        System.out.println("clone.starts=" + copy.starts);
        System.out.println("clone.shared=" + original.shared[0]);
        System.out.println("clone.timer=" + (copy.getTimer() != original.getTimer()));
        System.out.println("clone.pen=" + (original.getPen().getSize() == 5));
        System.out.println("clone.x=" + (copy.getX() == 30));
        System.out.println("clone.width=" + (copy.getWidth() == original.getWidth()));
        broadcast("first");
        System.out.println("broadcast.original=" + original.messages);
        System.out.println("broadcast.clone=" + copy.messages);
        copy.deleteThisClone();
        copy.deleteThisClone();
        original.deleteThisClone();
        broadcast("second");
        System.out.println("removed.messages=" + copy.messages);
        System.out.println("removed.hook=" + copy.removals);
        System.out.println("original.messages=" + original.messages);
        add(copy);
        broadcast("third");
        System.out.println("readded.messages=" + copy.messages);
        remove(copy);
        System.out.println("removed.again=" + copy.removals);
        Pen pen = new Pen(original);
        pen.setColor(255, 0, 0);
        Pen copiedPen = new Pen(pen);
        copiedPen.changeColor(30);
        System.out.println("pen.color=" + (pen.getColor().getRed() == 255));
        Text text = new Text("hello", 20, 30, 100);
        Text copiedText = new Text(text);
        copiedText.setX(40);
        System.out.println("text.copy=" + (text.getX() == 20 && copiedText.getY() == 30));
        new Text(original);
        Shaders shaders = new Shaders("probe");
        System.out.println("shaders.copy=" + (new Shaders(shaders).getCurrentIndex() == shaders.getCurrentIndex()));
        System.out.println("color.hash=" + (new Color(255, 128, 0).hashCode() == -1348971425));
        System.out.println("sorting=" + !new Sorting().isOn());
        new HtmlColor();
        text.remove();
        copiedText.remove();
        showVariable("frames", () -> frames);
        showVariable("decimal", () -> 1.234);
        showVariable("array", () -> original.shared);
        showVariable("broken", () -> { throw new RuntimeException("supplier failure"); });
        original.showVariable("score", () -> original.score);
        showVariable("timer", () -> original.getTimer("elapsed").afterMillis(100));
        showVariable("unused", () -> 99);
        hideVariable("unused");
        original.say("timed", 100);
        original.glide(0.1, 90, -20);
        System.out.println("ready");
    }
    public void run() { frames++; }
}
