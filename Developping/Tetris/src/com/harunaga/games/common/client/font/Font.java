/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harunaga.games.common.client.font;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.StringTokenizer;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import org.apache.log4j.Logger;

/**
 *
 * @author Harunaga
 */
public abstract class Font {

    protected static GraphicsConfiguration gc;
    protected static Logger log = Logger.getLogger(Font.class);
    public static final String DEFAULT_FONT_FOLDER = "/fonts/";
    public static final String DEFAULT_FONT_FILE_EXTENSION = ".hana";
    public static final byte STYLE_PLAIN = 0;
    public static final byte STYLE_BOLD = 1;
    public static final byte STYLE_ITALIC = 2;
    //------------------End pulic constant------------------
    private static Hashtable<String, Font> pfontSet;

    public static Font getDefaultFont() {
        return new SFont("System", STYLE_PLAIN, 18);
    }

    public static void init(GraphicsConfiguration graphicsConfiguration) {
        pfontSet = new Hashtable<String, Font>(6);
        gc = graphicsConfiguration;
        if (gc == null) {
            throw new IllegalArgumentException("gc null");
        }
    }

    protected Font() {
    }

    public static Font getFont(String face, int style, int size) {

        String font = face + style + size;
        Object o = pfontSet.get(font);
        if (o != null) {
            return ((PFont) o);
        } else {
            return new SFont(face, style, size);
        }
    }

    public static Font getFont(String fontName) {
        StringTokenizer st = new StringTokenizer(fontName, "_");
        String face = "system";
        int style = STYLE_PLAIN, size = 12;
        try {
            if (st.hasMoreTokens()) {
                face = st.nextToken();
            }
            if (st.hasMoreTokens()) {
                style = stringToStyle(st.nextToken());
            }
            if (st.hasMoreTokens()) {
                size = Integer.parseInt(st.nextToken());
            }
        } catch (Exception e) {
            log.warn("Font name wrong : " + fontName);
        }
        return getFont(face, style, size);
    }

//    public static Font removeFont(String fontName) {
//        Font font = getFont(fontName);
//        pfontSet.remove(fontName);
//        return font;
//    }
    public static Font loadFont(String fontName) {
        String font = parseFontName(fontName);
        Font f = pfontSet.get(font);
        if (f == null) {
            InputStream is = Font.class.getResourceAsStream(DEFAULT_FONT_FOLDER + fontName + DEFAULT_FONT_FILE_EXTENSION);
            if (is != null) {
                return loadFont(is, font);
            } else {
                log.warn("Not found the font " + fontName);
            }
        }
        return f;
    }

    protected static String parseFontName(String fontName) {
        StringTokenizer st = new StringTokenizer(fontName, "_");
        String face = "system";
        int style = STYLE_PLAIN, size = 12;
        try {
            if (st.hasMoreTokens()) {
                face = st.nextToken();
            }
            if (st.hasMoreTokens()) {
                style = stringToStyle(st.nextToken());
            }
            if (st.hasMoreTokens()) {
                size = Integer.parseInt(st.nextToken());
            }
        } catch (Exception e) {
            log.warn("Font name wrong");
        }
        return face + style + size;
    }

    private static Font loadFont(InputStream is, String font) {
        PFont pfont = new PFont();
        if (pfont.load(is)) {
            log.info("Font " + font + " was load in to memory");
            return pfontSet.put(font, pfont);
        } else {
            log.error("Wrong font inputstreem");
        }
        return null;
    }

    public static int stringToStyle(String s) {
        s = s.trim().toLowerCase();
        if (s.equals("italic")) {
            return STYLE_ITALIC;
        } else if (s.equals("bold")) {
            return STYLE_BOLD;
        } else {
            return STYLE_PLAIN;
        }
    }

    public abstract int getHeight();

    public abstract int charWidth(char ch);

    public abstract int stringWidth(String s);

    /**
     * draw string with TOP_LEFT alignment
     * @param s the string to draw
     * @param x
     * @param y
     * @param g
     * @see #drawString(java.lang.String, int, int, java.awt.Graphics, int, int, int, int)
     * @see #drawString(java.lang.String, int, int, java.awt.Graphics, int, char)
     */
    public abstract void drawString(String s, int x, int y, Graphics g);

    /**
     * 
     * @param s
     * @param x
     * @param y
     * @param g
     * @param numberPositions the number of char will be draw
     * @param replaceChar the char to draw if length of s less than the numberPositions
     * @see #drawString(java.lang.String, int, int, java.awt.Graphics)
     * @see #drawString(java.lang.String, int, int, java.awt.Graphics, int, int, int, int)
     */
    public void drawString(String s, int x, int y, Graphics g, int numberPositions, char replaceChar) {
        StringBuilder sb = new StringBuilder(numberPositions);
        int replaceCharNumber = numberPositions;
        if (s != null) {
            replaceCharNumber = numberPositions - s.length();
        }
        for (int i = 0; i < replaceCharNumber; i++) {
            sb.append(replaceChar);
        }
        if (s != null) {
            sb.append(s);
        }
        drawString(sb.toString(), x, y, g);
    }

    /**
     *  draw string with specified alignment
     * @param s the string to draw
     * @param x
     * @param y
     * @param g
     * @param width
     * @param height
     * @param alignmentX
     * alignmentX one of the following values:
     * <ul>
     * <li>{@code SwingConstants.RIGHT}
     * <li>{@code SwingConstants.LEFT}
     * <li>{@code SwingConstants.CENTER}
     * <li>{@code SwingConstants.LEADING}
     * <li>{@code SwingConstants.TRAILING} (the default)
     * </ul>
     * @param alignmentY
     * alignmentY one of the following values:
     * <ul>
     * <li>{@code SwingConstants.TOP} (the default)
     * <li>{@code SwingConstants.BOTTOM}
     * <li>{@code SwingConstants.CENTER} 
     * </ul>
     * @exception IllegalArgumentException if <code>alignment</code>
     *          is not one of the legal values listed above
     * @see #drawString(java.lang.String, int, int, java.awt.Graphics)
     * @see #drawString(java.lang.String, int, int, java.awt.Graphics, int, char) 
     */
    public void drawString(String s, int x, int y, Graphics g, int width, int height,
            int alignmentX, int alignmentY) {
        switch (alignmentX) {
            case SwingConstants.LEFT:
            case SwingConstants.TRAILING:
                break;
            case SwingConstants.CENTER:
                x += (width - stringWidth(s)) >> 1;
                break;
            case SwingConstants.RIGHT:
            case SwingConstants.LEADING:
                x += width - stringWidth(s);
                break;
            default:
                throw new IllegalArgumentException("Unknow alignmentX");
        }
        switch (alignmentY) {
            case SwingConstants.TOP:
                break;
            case SwingConstants.CENTER:
                y += (height - getHeight()) >> 1;
                break;
            case SwingConstants.BOTTOM:
                y += height - getHeight();
                break;
            default:
                throw new IllegalArgumentException("Unknow alignmentY");
        }
        drawString(s, x, y, g);
    }
}
