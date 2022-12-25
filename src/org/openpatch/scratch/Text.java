package org.openpatch.scratch;

import processing.core.PApplet;
import processing.core.PFont;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Text implements Drawable {
    private float x;
    private float y;
    private final float width;
    private boolean fullWidth;
    private float height;
    private long lifetime;
    private boolean hasLifetime;
    private String text;
    private String originalText;

    private final int textSize;
    private final PFont mono;
    private boolean show;
    private TextStyle style;
    private Color textColor;
    private Color backgroundColor;

    public Text(String text, float x, float y, float width) {
        this.x = x;
        this.y = y;
        this.textSize = 16;
        this.originalText = text;
        this.width = width;
        this.show = false;
        this.mono = Stage.parent.createFont("UbuntuMono-Regular.ttf", this.textSize);
        this.backgroundColor = new Color(255,255,255);
        this.textColor = new Color(120, 120, 120);
    }

    public Text(String text, float x, float y, float width, TextStyle style) {
        this(text, x, y, width);
        this.style = style;
    }

    public Text(String text, float x, float y, boolean fullWidth, TextStyle style) {
        this(text, x, y, Stage.parent.width, style);
        this.fullWidth = fullWidth;
    }

    public Text(Text t) {
        this.fullWidth = t.fullWidth;
        this.width = t.width;
        this.originalText = t.originalText;
        this.show = t.show;
        this.lifetime = t.lifetime;
        this.hasLifetime = t.hasLifetime;
        this.height = t.height;
        this.mono = t.mono;
        this.textSize = t.textSize;
        this.text = t.text;
        this.x = t.x;
        this.y = t.y;
        this.style = t.style;
        this.textColor = t.textColor;
        this.backgroundColor = t.backgroundColor;
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

    public void setStyle(TextStyle style) {
            this.style = style;
    }

    public void setBackgroundColor(int r, int g, int b) {
        this.backgroundColor = new Color(r, g, b);
    }

    public void setBackgroundColor(float h) {
        this.backgroundColor = new Color(h);
    }

    public void setTextColor(int r, int g, int b) {
        this.textColor = new Color(r, g, b);
    }

    public void setTextColor(float h) {
        this.textColor = new Color(h);
    }

    /**
     * This is copied from StackOverflow: https://stackoverflow.com/a/64582014
     * Wraps a source String into a series of lines having a maximum specified
     * length. The source is
     * wrapped at: spaces, horizontal tabs, system newLine characters, or a
     * specified newLine character
     * sequence. Existing newLine character sequences in the source string, whether
     * they be the system
     * newLine or the specified newLine, are honored. Existing whitespace (spaces
     * and horizontal tabs)
     * is preserved.
     * <p>
     * When wrapLongWords is true, words having a length greater than the specified
     * lineLength will be broken, the specified longWordBreak terminator appended,
     * and a new line initiated with the text of the specified longWordLinePrefix
     * string. The
     * position of the break will be unceremoniously chosen such that ineLength is
     * honored.
     * One use of longWordLinePrefix is to effect "hanging indents" by specifying a
     * series of
     * spaces for this parameter. This parameter can contain the lineFeed
     * character(s). Although
     * longWordLinePrefix can contain the horizontal tab character, the results are
     * not
     * guaranteed because no attempt is made to determine the quantity of character
     * positions occupied by a
     * horizontal tab.
     * </p>
     *
     * Example usage:
     * 
     * <pre>
     * wrap("  A very long word is Abracadabra in my book", 11, "\n", true, "-", "  ");
     * </pre>
     * 
     * returns (note the effect of the single-character lineFeed):
     * 
     * <pre>
     *   A very
     * long word
     * is Abraca-
     *   dabra in
     * my book
     * </pre>
     * 
     * Whereas, the following:
     * 
     * <pre>
     * wrap("  A very long word is Abracadabra in my book", 11, null, true, null, "  ");
     * </pre>
     * 
     * returns (due to the 2-character system linefeed):
     * 
     * <pre>
     *   A very
     * long
     * word is A
     *   bracada
     *   bra in
     * my book
     * </pre>
     *
     * @param src                the String to be word wrapped, may be null
     * @param lineLength         the maximum line length, including the length of
     *                           newLineStr and, when
     *                           applicable, longWordLinePrefix. If the value is
     *                           insufficient to accommodate
     *                           these two parameters + 1 character, it will be
     *                           increased accordingly.
     * @param newLineStr         the string to insert for a new line, or
     *                           <code>null</code> to use the value
     *                           reported as the system line separator by the JVM
     * @param wrapLongWords      when false, words longer than wrapLength will not
     *                           be broken
     * @param longWordBreak      string with which to precede newLineStr on each
     *                           line of a broken word,
     *                           excepting the last line, or null if this feature is
     *                           not to be used
     * @param longWordLinePrefix string with which to prefix each line of a broken
     *                           word, subsequent
     *                           to the first line, or null if no prefix is to be
     *                           used
     * @return a line with newlines inserted, or <code>null</code> if src is null
     */
    public static String wrap(String src, int lineLength, String newLineStr, boolean wrapLongWords,
            String longWordBreak, String longWordLinePrefix) {
        // Trivial case
        if (src == null)
            return null;

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

        int remaining = lineLength,
                breakLength = longWordBreak.length();

        Matcher m = Pattern.compile(".+?[ \\t]|.+?(?:" + newLineStr + ")|.+?$").matcher(src);

        StringBuilder cache = new StringBuilder();

        while (m.find()) {
            String word = m.group();

            // Breakup long word
            while (wrapLongWords && word.length() > lineLength) {
                cache
                        .append(word, 0, remaining - breakLength)
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
        if (!show || this.originalText == null)
            return;

        PApplet textBuffer = Stage.parent;

        textBuffer.push();
        textBuffer.textSize(this.textSize);
        textBuffer.stroke(this.textColor.getRed(), this.textColor.getGreen(), this.textColor.getBlue());
        textBuffer.strokeWeight(2);
        textBuffer.fill(this.backgroundColor.getRed(), this.backgroundColor.getGreen(), this.backgroundColor.getBlue());
        textBuffer.rectMode(PApplet.CORNER);
        textBuffer.textAlign(PApplet.LEFT, PApplet.TOP);
        textBuffer.textFont(this.mono);

        float cw = textBuffer.textWidth("w");
        float maxWidth = Math.min(this.width, Stage.getInstance().getWidth() - this.x);
        if (this.fullWidth) {
            maxWidth = Stage.parent.width - 16;
        }
        int lineLength = Math.round((maxWidth - 16) / cw);
        this.text = wrap(originalText, lineLength, "\n", true, "-", " ");
        String[] lines = this.text.split("\n");

        float width = 0;
        if (this.fullWidth) {
            width = Stage.parent.width;
        } else {
            // get minimum width
            for (String l : lines) {
                width = Math.max(textBuffer.textWidth(l), width);
            }
            width = Math.min(width + 16, this.width);
        }

        this.height = (this.textSize + 4) * lines.length + 16;
        textBuffer.translate(this.x, this.y - height);
        textBuffer.stroke(this.textColor.getRed(), this.textColor.getGreen(), this.textColor.getBlue());
        if (this.style == TextStyle.PLAIN) {

        } else if (this.style == TextStyle.BOX) {
            textBuffer.rect(0, 0, width, this.height, 16, 16, 0, 0);
        } else {
            textBuffer.rect(0, 0, width, this.height, 16, 16, 16, 16);
            if (this.style == TextStyle.SPEAK) {
                textBuffer.push();
                textBuffer.fill(this.backgroundColor.getRed(), this.backgroundColor.getGreen(), this.backgroundColor.getBlue());
                textBuffer.translate(10, height);
                textBuffer.triangle(0, 20, 0, 0, 20, 0);
                textBuffer.stroke(this.backgroundColor.getRed(), this.backgroundColor.getGreen(), this.backgroundColor.getBlue());
                textBuffer.strokeWeight(3);
                textBuffer.line(2, 0, 16, 0);
                textBuffer.pop();
            } else if (this.style == TextStyle.THINK) {
                textBuffer.circle(20, this.height, 10);
                textBuffer.circle(7, this.height + 7, 6);
                textBuffer.circle(0, this.height + 10, 4);
            }
        }
        textBuffer.fill(this.textColor.getRed(), this.textColor.getGreen(), this.textColor.getBlue());
        textBuffer.textLeading(this.textSize + 4);
        textBuffer.text(this.text, 8, 8);
        textBuffer.pop();

        if (this.hasLifetime && this.lifetime < System.currentTimeMillis()) {
            this.show = false;
        }
    }
}
