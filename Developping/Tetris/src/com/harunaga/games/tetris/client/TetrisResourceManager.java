/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harunaga.games.tetris.client;

import com.harunaga.games.common.client.resource.ResourceManager;
import com.harunaga.games.common.client.sound.SoundManager;
import com.harunaga.games.tetris.client.piece.Piece;
import com.harunaga.games.tetris.client.piece.Rotation;
import com.harunaga.games.common.client.sound.MidiPlayer;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author Harunaga
 */
public class TetrisResourceManager extends ResourceManager {

    public TetrisResourceManager(GraphicsConfiguration gc,
            SoundManager soundManager, MidiPlayer midiPlayer) {
        super(gc, soundManager, midiPlayer);
    }

//    public InputStream getResourceAsStream(String path) {
//        return TetrisResourceManager.class.getClassLoader().getResourceAsStream(path);
//    }
    public Image[] getBoardImages() {
        Vector<Image> images = new Vector<Image>();
        int ch = 1;
        while (true) {
            String name = "tetrisBackgrounds/bg_" + ch + ".jpg";
            if (!new File(name).exists()) {
                break;
            }
            images.add(new ImageIcon(name).getImage());
            ch++;
        }
        return images.toArray(new Image[]{});
    }

    public Piece[] getPieceSet(String xmlFile) {
        try {
            Element rootElement = getXMLDocumentElement(xmlFile);
            NodeList childNodes = rootElement.getChildNodes();
            Vector piecesAll = new Vector();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node node = childNodes.item(i);
                int type = node.getNodeType();
                if (type == Node.ELEMENT_NODE) {
                    String nodeName = node.getNodeName();
                    if (nodeName.equals("piece")) {
                        //load the pieces
                        Piece piece = getPiece((Element) node);
                        piecesAll.add(piece);
                    }
                } //element
            } //traversing the configuration
            //convert vector of Pieces to array
            return (Piece[]) piecesAll.toArray(new Piece[]{});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Piece getPiece(Element aPieceElement) {
        String imgPath = "pieces/" + getXMLElementChildValue(aPieceElement, "image") + ".png";
        Vector rotations = new Vector();
        NodeList list = aPieceElement.getElementsByTagName("rotation");
        for (int i = 0; i < list.getLength(); i++) {
            Element rotationElement = (Element) list.item(i);
            int w = getXMLElementChildIntValue(rotationElement, "width");
            int h = getXMLElementChildIntValue(rotationElement, "height");
            int centerX = getXMLElementChildIntValue(rotationElement, "center_x");
            int centerY = getXMLElementChildIntValue(rotationElement, "center_y");
            String map = getXMLElementChildValue(rotationElement, "map");
            rotations.add(new Rotation(w, h, centerX, centerY, map));
        }
        return new Piece(rotations.size(), getImage(imgPath), (Rotation[]) rotations.toArray(new Rotation[]{}));
    }

    public Element getXMLDocumentElement(String aResourceName) throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document document = factory.newDocumentBuilder().parse(new InputSource(getResourceAsStream(aResourceName)));
        return document.getDocumentElement();
    }

    /**
     * returns the text of particular child element
     * throws NullPointerException if element is not found
     */
    public String getXMLElementChildValue(Element aParent, String aChildName) {
        NodeList childNodes = aParent.getElementsByTagName(aChildName).item(0).getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (node.getNodeType() == Node.TEXT_NODE) {
                return node.getNodeValue();
            }
        }
        return null;
    }

    /**
     * returns the text of particular child element converted to integer value
     * throws NullPointerException if element is not found
     */
    public int getXMLElementChildIntValue(Element aParent, String aChildName) {
        return Integer.parseInt(getXMLElementChildValue(aParent, aChildName));
    }
}
