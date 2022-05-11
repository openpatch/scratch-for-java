package eu.barkmin.processing.scratch;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScratchText {
    public static final int BOX = 0;
    public static final int SPEAK = 1;
    public static final int THINK = 2;
    private float x;
    private float y;
    private final float width;
    private float height;
    private long lifetime;
    private boolean hasLifetime;
    private String text;
    private String originalText;

    private final int textSize;
    private final PFont mono;
    private boolean show;
    private int mode;

    public ScratchText(String text, float x, float y) {
        this(text, x, y, 242);
        this.mode = ScratchText.BOX;
    }

    public ScratchText(String text, float x, float y, float width, int mode) {
        this(text, x, y, width);
        this.mode = mode;
    }

    public ScratchText(ScratchText t) {
        this.x = t.x;
        this.y = t.y;
        this.lifetime = t.lifetime;
        this.hasLifetime = t.hasLifetime;
        this.textSize = 16;
        this.originalText = t.originalText;
        this.width = t.width;
        this.height = t.height;
        this.text = null;
        this.show = false;
        this.mono = t.mono;
        this.mode = t.mode;
    }

    public ScratchText(String text, float x, float y, float width) {
        this.x = x;
        this.y = y;
        this.textSize = 16;
        this.originalText = text;
        this.width = width;
        this.show = false;
        this.mono = ScratchStage.parent.createFont("UbuntuMono-Regular.ttf", this.textSize);

    }

    /**
     * Sets the position of the text
     *
     * @param x a x coordinate
     * @param y a y coordinate
     */
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setPosition(float x, float y) {
        this.setPosition(Math.round(x), Math.round(y));
    }

    public void setPosition(double x, double y) {
        this.setPosition((float) x, (float) y);
    }

    public void showText(String text) {
        this.show = true;
        this.hasLifetime = false;
        if (text == null || text.trim().length() == 0) {
            this.text = null;
        }
        this.originalText = text;
    }

    public void showText(String text, int millis) {
        showText(text);
        this.hasLifetime = true;
        this.lifetime = System.currentTimeMillis() + millis;
    }

    public void setMode(int mode) {
        if (mode <= 2 && mode > 0) {
            this.mode = mode;
        }
    }

    /**
     * This is copied from StackOverflow: https://stackoverflow.com/a/64582014
     * Wraps a source String into a series of lines having a maximum specified length.  The source is
     * wrapped at: spaces, horizontal tabs, system newLine characters, or a specified newLine character
     * sequence.  Existing newLine character sequences in the source string, whether they be the system
     * newLine or the specified newLine, are honored.  Existing whitespace (spaces and horizontal tabs)
     * is preserved.
     * <p>
     * When <tt>wrapLongWords</tt> is true, words having a length greater than the specified
     * <tt>lineLength</tt> will be broken, the specified <tt>longWordBreak</tt> terminator appended,
     * and a new line initiated with the text of the specified <tt>longWordLinePrefix</tt> string.  The
     * position of the break will be unceremoniously chosen such that <tt>ineLength</tt> is honored.
     * One use of <tt>longWordLinePrefix</tt> is to effect "hanging indents"  by specifying a series of
     * spaces for this parameter.  This parameter can contain the lineFeed character(s).  Although
     * <tt>longWordLinePrefix</tt> can contain the horizontal tab character, the results are not
     * guaranteed because no attempt is made to determine the quantity of character positions occupied by a
     * horizontal tab.</p>
     * <p>
     * Example usage:
     * <pre>
     * wrap( "  A very long word is Abracadabra in my book", 11, "\n", true, "-", "  ");</pre>
     * returns (note the effect of the single-character lineFeed):
     * <pre>
     *   A very
     * long word
     * is Abraca-
     *   dabra in
     * my book</pre>
     * Whereas, the following:
     * <pre>
     * wrap( "  A very long word is Abracadabra in my book", 11, null, true, null, "  ");</pre>
     * returns (due to the 2-character system linefeed):
     * <pre>
     *   A very
     * long
     * word is A
     *   bracada
     *   bra in
     * my book</pre></p>
     *
     * @param src                the String to be word wrapped, may be null
     * @param lineLength         the maximum line length, including the length of <tt>newLineStr</tt> and, when
     *                           applicable, <tt>longWordLinePrefix</tt>.  If the value is insufficient to accommodate
     *                           these two parameters + 1 character, it will be increased accordingly.
     * @param newLineStr         the string to insert for a new line, or <code>null</code> to use the value
     *                           reported as the system line separator by the JVM
     * @param wrapLongWords      when <tt>false</tt>, words longer than <tt>wrapLength</t> will not be broken
     * @param longWordBreak      string with which to precede <tt>newLineStr</tt> on each line of a broken word,
     *                           excepting the last line, or <tt>null</tt> if this feature is not to be used
     * @param longWordLinePrefix string with which to prefix each line of a broken word, subsequent
     *                           to the first line, or <tt>null</tt> if no prefix is to be used
     * @return a line with newlines inserted, or <code>null</code> if <tt>src</tt> is null
     */
    public static String wrap(String src, int lineLength, String newLineStr, boolean wrapLongWords,
                              String longWordBreak, String longWordLinePrefix) {
        // Trivial case
        if (src == null) return null;

        if (newLineStr == null)
            newLineStr = System.getProperty("line.separator");

        if (longWordBreak == null)
            longWordBreak = "";

        if (longWordLinePrefix == null)
            longWordLinePrefix = "";

        // Adjust maximum line length to accommodate the newLine string
        lineLength -= newLineStr.length();
        if (lineLength < 1)
            lineLength = 1;

        // Guard for long word break or prefix that would create an infinite loop
        if (wrapLongWords && lineLength - longWordBreak.length() - longWordLinePrefix.length() < 1)
            lineLength += longWordBreak.length() + longWordLinePrefix.length();

        int
                remaining = lineLength,
                breakLength = longWordBreak.length();

        Matcher m = Pattern.compile(".+?[ \\t]|.+?(?:" + newLineStr + ")|.+?$").matcher(src);

        StringBuilder cache = new StringBuilder();

        while (m.find()) {
            String word = m.group();

            // Breakup long word
            while (wrapLongWords && word.length() > lineLength) {
                cache
                        .append(word.substring(0, remaining - breakLength))
                        .append(longWordBreak)
                        .append(newLineStr);
                word = longWordLinePrefix + word.substring(remaining - breakLength);
                remaining = lineLength;
            } // if

            // Linefeed if word exceeds remaining space
            if (word.length() > remaining) {
                cache
                        .append(newLineStr)
                        .append(word);
                remaining = lineLength;
            } // if

            // Word fits in remaining space
            else
                cache.append(word);

            remaining -= word.length();
        } // while

        return cache.toString();
    } // wrap()

    public float getWidth() {
        return this.width;
    }

    public void draw() {
        if (!show || this.originalText == null) return;

        PApplet textBuffer = ScratchStage.parent;

        textBuffer.push();
        textBuffer.textSize(this.textSize);
        textBuffer.stroke(0, 0, 0);
        textBuffer.strokeWeight(2);
        textBuffer.fill(255, 255, 255);
        textBuffer.rectMode(PApplet.CORNER);
        textBuffer.textAlign(PApplet.LEFT, PApplet.TOP);
        textBuffer.textFont(this.mono);

        if (this.text == null) {
            float cw = textBuffer.textWidth("w");
            int lineLength = Math.round(width / cw);
            this.text = wrap(originalText, lineLength, "\n", true, "-", " ");
            this.height = 21 * this.text.split("\n").length + 8;
        }

        textBuffer.translate(this.x, this.y - height);
        if (this.mode == ScratchText.BOX) {
            textBuffer.rect(0, 0, this.width, this.height, 8, 8, 0, 0);
        } else {
            textBuffer.rect(0, 0, this.width, this.height, 8, 8, 8, 8);
            if (this.mode == ScratchText.SPEAK) {
                textBuffer.push();
                textBuffer.fill(255, 255, 255);
                textBuffer.translate(10, height);
                textBuffer.triangle(0, 20, 0, 0, 20, 0);
                textBuffer.stroke(255);
                textBuffer.strokeWeight(3);
                textBuffer.line(2, 0, 16, 0);
                textBuffer.pop();
            } else if (this.mode == ScratchText.THINK) {
                textBuffer.circle(20, this.height, 10);
                textBuffer.circle(7, this.height + 7, 6);
                textBuffer.circle(0, this.height + 10, 4);
            }
        }
        textBuffer.fill(0, 0, 0);
        textBuffer.text(this.text, 4, 4);
        textBuffer.pop();

        if (this.hasLifetime && this.lifetime < System.currentTimeMillis()) {
           this.show = false;
        }
    }
}
