package com.harunaga.games.common.client.resource;

import com.harunaga.games.common.client.font.Font;
import com.harunaga.games.common.client.sound.Sound;
import com.harunaga.games.common.client.sound.SoundManager;
import com.harunaga.games.common.client.sound.MidiPlayer;
import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.sound.midi.Sequence;
import org.apache.log4j.Logger;

/**
The ResourceManager class loads resources like images and
sounds.
 */
public class ResourceManager {

    protected Logger log = Logger.getLogger(ResourceManager.class);
    private GraphicsConfiguration gc;
    private SoundManager soundManager;
    private MidiPlayer midiPlayer;
    /** Image cache*/
    private Map<String, Image> images = new Hashtable<String, Image>(20);
    private Map<String, Sound> sounds = new HashMap<String, Sound>(10);
    private Map<String, Sequence> sequences = new HashMap<String, Sequence>(10);

    /**
    Creates a new ResourceManager with the specified
    GraphicsConfiguration.
     */
    public ResourceManager(GraphicsConfiguration gc,
            SoundManager soundManager, MidiPlayer midiPlayer) {
        this.gc = gc;
        this.soundManager = soundManager;
        this.midiPlayer = midiPlayer;
        Font.init(gc);
    }

    /**
     * get the image from the ResourceManager
     * this method will put the image to the cache for the next use (if image not null)
     * @param name the name (may include path) of the image
     * @return the image with the specified name
     */
    public Image getImage(String name) {
        //get from cache first
        Image img = images.get(name);
        if (img == null) {
            //if not exist in cache then load
            img = loadImage(name);
            if (img != null) {
                images.put(name, img);
            }
        }
        return img;
    }

    /**
     * Gets an image from the images/ directory.
     * @param name the name (relative path) of an image in images/ directory
     * @return the image loaded (possibly null)
     */
    public Image loadImage(String name) {
        String filename = name;
        if (!name.startsWith("/")) {
            filename = "images/" + name;
        }
        Image img = null;
        try {
            img = ImageIO.read(getResourceAsStream(filename));
        } catch (IOException ex) {
            log.warn("can not load the image : " + name);
        }

        return img;
    }

    /**
     * release all image from cache
     */
    public void releaseAllCachedImages() {
        images.clear();
    }

    public Image getTranslucentImage(Image image, float alpha) {
        Image translucentimage = gc.createCompatibleImage(image.getWidth(null), image.getHeight(null),
                Transparency.TRANSLUCENT);
        Graphics2D g = (Graphics2D) translucentimage.getGraphics();
        Composite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
        g.setComposite(composite);
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return translucentimage;
    }

    public BufferedImage getCompatibleImage(int width, int height, int transparency) {
        return gc.createCompatibleImage(width, height,
                Transparency.TRANSLUCENT);
    }

    public Image getMirrorImage(Image image) {
        return getScaledImage(image, -1, 1);
    }

    public Image getFlippedImage(Image image) {
        return getScaledImage(image, 1, -1);
    }

    private Image getScaledImage(Image image, float x, float y) {

        // set up the transform
        AffineTransform transform = new AffineTransform();
        transform.scale(x, y);
        transform.translate(
                (x - 1) * image.getWidth(null) / 2,
                (y - 1) * image.getHeight(null) / 2);

        // create a transparent (not translucent) image
        Image newImage = gc.createCompatibleImage(
                image.getWidth(null),
                image.getHeight(null),
                Transparency.BITMASK);

        // draw the transformed image
        Graphics2D g = (Graphics2D) newImage.getGraphics();
        g.drawImage(image, transform, null);
        g.dispose();

        return newImage;
    }

    public URL getResource(String filename) {
        return getClass().getClassLoader().getResource(filename);
    }

    public InputStream getResourceAsStream(String filename) {
        return getClass().getClassLoader().
                getResourceAsStream(filename);
    }

    public Sound getSound(String name) {
        //get from cache first
        Sound snd = sounds.get(name);
        if (snd == null) {
            //if not exist in cache then load
            snd = loadSound(name);
            if (snd != null) {
                sounds.put(name, snd);
            }
        }
        return snd;
    }

    public Sequence getSequence(String name) {
        //get from cache first
        Sequence sequence = sequences.get(name);
        if (sequence == null) {
            //if not exist in cache then load
            sequence = loadSequence(name);
            if (sequence != null) {
                sequences.put(name, sequence);
            }
        }
        return sequence;
    }

    public Sound loadSound(String name) {
        //for web
        return soundManager.getSound(getResourceAsStream(name));
        //native compile
        //return soundManager.getSound(name);
    }

    public Sequence loadSequence(String name) {
        //for web
        return midiPlayer.getSequence(getResourceAsStream(name));
        //native compile
        //return midiPlayer.getSequence(name);
    }
}
