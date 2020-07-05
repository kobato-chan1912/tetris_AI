/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harunaga.games.common.client.font;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

/**
 *
 * @author Harunaga
 */
class PFont extends Font {

    private String[] map;
    private char midChar;
    private int midPos;
    private Image[] charImages;
    private byte[] charWidths;
    private int height;

    PFont() {
        map = new String[2];
    }

    boolean load(InputStream is) {
        try {
            DataInputStream dis = new DataInputStream(is);
            dis.readByte();//read base line
            byte charSpace = dis.readByte();
            String CharMap = dis.readUTF();
            //System.out.println(CharMap);
            int num = CharMap.length();
            // create map
            midChar = CharMap.charAt(num >> 1);
            midPos = CharMap.indexOf(midChar);
            map[0] = CharMap.substring(1, midPos);
            map[1] = CharMap.substring(midPos);
            //------------------Debug---------------
//            System.out.println("Mid char: " + midChar + "    Pos=" + midPos);
//            System.out.println(CharMap.indexOf('8'));
//            System.out.println(map[0]);
//            System.out.println(map[1]);
            //create font image
            charWidths = new byte[num];
            dis.read(charWidths);//read the widths[] of charSet
            int imgSize = dis.readInt();
            //System.out.println("Img size = " + imgSize);
//            byte[] data = new byte[imgSize];
//            dis.read(data);
//            InputStream in = new ByteArrayInputStream(data);
            BufferedImage charImg = ImageIO.read(dis);
            height = charImg.getHeight();
            charImages = cutImage(charImg, charWidths, charSpace);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            try {
                is.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public int getHeight() {
        return height;
    }

    public int charWidth(char ch) {
        return charWidths[indexOf(ch)];
    }

    public int stringWidth(String s) {
        if (s == null) {
            return -1;
        }
        char[] temchars = s.toCharArray();
        int n = temchars.length;
        int tempWidth = 0;
        for (int i = 0; i < n; i++) {
            tempWidth += charWidths[indexOf(temchars[i])];
        }
        return tempWidth;
    }

    public void drawString(String s, int x, int y, Graphics g) {
        if (s == null) {
            return;
        }
        char[] charsSet = s.toCharArray();
        int n = charsSet.length;
        int tempX = x;
        for (int i = 0; i < n; i++) {
            int index = indexOf(charsSet[i]);
            int temWidth = charWidths[index];
            g.drawImage(charImages[index], tempX, y, null);
            tempX += temWidth;
        }
    }

    protected int indexOf(char ch) {
        if (ch < midChar) {
            return 1 + map[0].indexOf(ch);
        } else {
            int pos = map[1].indexOf(ch);
            return pos == -1 ? 0 : (midPos + pos);
        }
    }

    private static Image[] cutImage(Image img, byte[] width, byte charSpace) throws IOException {
        Image[] charImage = new Image[width.length];
        int height = img.getHeight(null);
        int tempX = 0;
        for (int i = 0; i < width.length; i++) {
            int count = 0;
            int Charwidth = width[i];
//            BufferedImage charImg = new BufferedImage(Charwidth, height, BufferedImage.TYPE_INT_ARGB);
            BufferedImage charImg = gc.createCompatibleImage(Charwidth, height, Transparency.TRANSLUCENT);
            Graphics2D g = charImg.createGraphics();
            g.drawImage(img, 0, 0, Charwidth, height, tempX, 0, tempX + Charwidth, height, null);
            g.dispose();
            charImage[i] = charImg;
            tempX += width[i] + charSpace;
        }
        return charImage;
    }
}
