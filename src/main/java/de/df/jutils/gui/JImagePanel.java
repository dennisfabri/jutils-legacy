package de.df.jutils.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.df.jutils.gui.filefilter.SimpleFileFilter;
import de.df.jutils.gui.util.DialogUtils;
import de.df.jutils.gui.util.FileChooserUtils;
import de.df.jutils.i18n.util.JUtilsI18n;
import de.df.jutils.io.BytesOutputStream;
import de.df.jutils.io.FileUtils;
import de.df.jutils.util.StringTools;

public class JImagePanel extends JPanel {

    private static final long serialVersionUID = -7575410845373166492L;

    private BufferedImage     image;
    private byte[]            imagedata;

    private JIcon             icon;
    private JButton           noimage;

    public static long        MAX_DEFAULT      = 1024 * 1024;

    private final long        maxsize;

    public JImagePanel() {
        this(null, 200, 200, MAX_DEFAULT);
    }

    public JImagePanel(int maxwidth, int maxheight, long max) {
        this(null, maxheight, maxheight, max);
    }

    public JImagePanel(BufferedImage i, int maxwidth, int maxheight, long max) {
        this.image = i;

        maxsize = max;

        if (i != null) {
            if (maxwidth <= 0) {
                maxwidth = i.getWidth();
            }
            if (maxheight <= 0) {
                maxheight = i.getHeight();
            }
        }

        icon = new JIcon(image, false);
        icon.setPreferredSize(new Dimension(maxwidth, maxheight));

        JButton open = new JButton(JUtilsI18n.get("de.dm.gui.imagepanel.OpenImage"));
        open.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                readImage();
            }
        });

        noimage = new JButton(JUtilsI18n.get("de.dm.gui.imagepanel.NoImage"));
        noimage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setImage(null);
            }
        });
        noimage.setEnabled(false);

        FormLayout layout = new FormLayout("4dlu,0dlu:grow,fill:default,4dlu,fill:default,4dlu", "4dlu,fill:0dlu:grow,4dlu,fill:default,4dlu");
        setLayout(layout);

        add(icon, CC.xyw(2, 2, 4, "fill,fill"));
        add(noimage, CC.xy(3, 4, "fill,fill"));
        add(open, CC.xy(5, 4, "fill,fill"));
    }

    private LinkedList<ChangeListener> listeners = new LinkedList<ChangeListener>();

    public void addChangeListener(ChangeListener cl) {
        listeners.addLast(cl);
    }

    private void notifyChangeListeners() {
        for (ChangeListener cl : listeners) {
            try {
                cl.stateChanged(new ChangeEvent(this));
            } catch (RuntimeException re) {
                re.printStackTrace();
            }
        }
    }

    public BufferedImage getImage() {
        return image;
    }

    public byte[] getImageData() {
        return imagedata;
    }

    public void setImageData(byte[] data) {
        if (data == null) {
            imagedata = null;
            setImage(null, false);
            return;
        }
        try {
            BufferedImage i = ImageIO.read(new ByteArrayInputStream(data));
            imagedata = Arrays.copyOf(data, data.length);
            setImage(i, false);
        } catch (IOException io) {
            throw new RuntimeException("Could not read image data.");
        }
    }

    public void setImage(BufferedImage i) {
        setImage(i, true);
    }

    private void setImage(BufferedImage i, boolean set) {
        if (set) {
            if (i == null) {
                imagedata = null;
            } else {
                try {
                    BytesOutputStream bos = new BytesOutputStream();
                    ImageIO.write(i, "png", bos);
                    imagedata = bos.getData();
                } catch (IOException io) {
                    throw new RuntimeException("Could not set Imagedata");
                }
            }
        }
        image = i;
        icon.setImage(i);
        noimage.setEnabled(i != null);
        notifyChangeListeners();
    }

    void readImage() {
        String name = FileChooserUtils.chooseFile(JUtilsI18n.get("de.dm.gui.imagepanel.OpenImage"), JUtilsI18n.get("Ok"),
                new SimpleFileFilter[] { new SimpleFileFilter(JUtilsI18n.get("de.dm.gui.imagepanel.Images"), ImageIO.getReaderFileSuffixes()) },
                SwingUtilities.getWindowAncestor(JImagePanel.this));
        if (name != null) {
            try {
                File f = new File(name);
                if (maxsize > 0) {
                    if (f.length() > maxsize) {
                        DialogUtils.warn(SwingUtilities.getWindowAncestor(JImagePanel.this),
                                JUtilsI18n.get("de.dm.gui.imagepanel.FileTooBigText", name, StringTools.sizeToString(f.length()),
                                        StringTools.sizeToString(maxsize)),
                                JUtilsI18n.get("de.dm.gui.imagepanel.FileTooBigText.Note", name, StringTools.sizeToString(f.length()),
                                        StringTools.sizeToString(maxsize)));
                    }
                }

                imagedata = FileUtils.readFile(new FileInputStream(f));
                BufferedImage bi = ImageIO.read(new ByteArrayInputStream(imagedata));
                if (bi != null) {
                    setImage(bi, false);
                } else {
                    DialogUtils.warn(SwingUtilities.getWindowAncestor(JImagePanel.this), JUtilsI18n.get("de.dm.gui.imagepanel.ErrorText", name),
                            JUtilsI18n.get("de.dm.gui.imagepanel.ErrorText", name));
                }
            } catch (IOException io) {
                DialogUtils.warn(SwingUtilities.getWindowAncestor(JImagePanel.this), JUtilsI18n.get("de.dm.gui.imagepanel.ErrorText", name),
                        JUtilsI18n.get("de.dm.gui.imagepanel.ErrorText.Note", name));
                io.printStackTrace();
            }
        }
    }
}