/*
 * Created on 16.01.2005
 */
package de.df.jutils.io;

import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import org.openpdf.text.Document;
import org.openpdf.text.DocumentException;
import org.openpdf.text.FontFactory;
import org.openpdf.text.PageSize;
import org.openpdf.text.Rectangle;
import org.openpdf.text.pdf.BaseFont;
import org.openpdf.text.pdf.DefaultFontMapper;
import org.openpdf.text.pdf.FontMapper;
import org.openpdf.text.pdf.PdfContentByte;
import org.openpdf.text.pdf.PdfGraphics2D;
import org.openpdf.text.pdf.PdfTemplate;
import org.openpdf.text.pdf.PdfWriter;

import de.df.jutils.i18n.util.JUtilsI18n;
import de.df.jutils.util.Feedback;

/**
 * @author Dennis Fabri
 * @date 16.01.2005
 */
public final class PdfOutput {

    private PdfOutput() {
    }

    private static DefaultFontMapper fontMapper;

    /**
     * Fonts are registered with Unicode encoding: OpenPDF's default, CP1252, silently drops every character outside
     * of it, e.g. "ć" or "ł" in names.
     */
    private static synchronized FontMapper getFontMapper() {
        if (fontMapper == null) {
            fontMapper = new DefaultFontMapper();
            for (Path directory : fontDirectories()) {
                insertDirectoryTree(fontMapper, directory);
            }
            fontMapper.getMapper().values().forEach(p -> p.encoding = BaseFont.IDENTITY_H);
        }
        return fontMapper;
    }

    private static List<Path> fontDirectories() {
        String home = System.getProperty("user.home");
        List<Path> directories = new ArrayList<>();
        String windir = System.getenv("WINDIR");
        if (windir != null) {
            directories.add(Path.of(windir, "Fonts"));
        }
        String localAppData = System.getenv("LOCALAPPDATA");
        if (localAppData != null) {
            directories.add(Path.of(localAppData, "Microsoft", "Windows", "Fonts"));
        }
        directories.add(Path.of("/usr/share/fonts"));
        directories.add(Path.of("/usr/local/share/fonts"));
        directories.add(Path.of(home, ".local", "share", "fonts"));
        directories.add(Path.of(home, ".fonts"));
        directories.add(Path.of("/System/Library/Fonts"));
        directories.add(Path.of("/Library/Fonts"));
        directories.add(Path.of(home, "Library", "Fonts"));
        return directories;
    }

    /**
     * {@link DefaultFontMapper#insertDirectory(String)} only reads the given directory itself, but font directories
     * on Linux are nested.
     */
    private static void insertDirectoryTree(DefaultFontMapper mapper, Path root) {
        if (!Files.isDirectory(root)) {
            return;
        }
        try {
            Files.walkFileTree(root, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    mapper.insertDirectory(dir.toString());
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            // An unreadable font directory only means fewer fonts for the PDF
        }
    }

    public static boolean write(OutputStream out, Printable source, boolean portrait, Insets margin, Feedback fb) {
        FontFactory.defaultEmbedding = true;

        Document document;
        if (portrait) {
            document = new Document(PageSize.A4);
        } else {
            document = new Document(PageSize.A4.rotate());
        }
        Rectangle size = document.getPageSize();
        Paper paper = new Paper();
        paper.setSize(size.getWidth(), size.getHeight());
        paper.setImageableArea(margin.left, margin.top, size.getWidth() - margin.left - margin.right,
                size.getHeight() - margin.top - margin.bottom);
        PageFormat pf = new PageFormat();
        pf.setOrientation(PageFormat.PORTRAIT);
        pf.setPaper(paper);

        int pages = 0;
        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);
            document.open();
            PdfContentByte cb = writer.getDirectContent();

            while (true) {
                fb.showFeedback(JUtilsI18n.get("PageNr", pages + 1));

                // Whether a page exists is only known after printing it, so each
                // page is drawn into a template that is added only if it exists
                PdfTemplate page = cb.createTemplate(size.getWidth(), size.getHeight());
                Graphics2D g2 = new PdfGraphics2D(page, size.getWidth(), size.getHeight(), getFontMapper(), false,
                        false, 0);
                int result;
                try {
                    g2.clipRect(0, 0, (int) size.getWidth(), (int) size.getHeight());
                    result = source.print(g2, pf, pages);
                } finally {
                    g2.dispose();
                }
                if (result != Printable.PAGE_EXISTS) {
                    break;
                }

                if (pages > 0) {
                    document.newPage();
                }
                cb.addTemplate(page, 0, 0);
                pages++;
            }
        } catch (PrinterException | DocumentException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (pages > 0) {
                fb.showFeedback(JUtilsI18n.get("de.dm.io.pdf.WritingFile"));
                document.close();
            }
        }
        return pages > 0;
    }

    /**
     * Writes into a temporary file first so that an existing file is only
     * replaced when the new one has been written completely.
     */
    public static boolean write(String filename, Printable printable, boolean portrait, Feedback fb) {
        Path target = Path.of(filename).toAbsolutePath();
        Path temp = null;
        try {
            temp = Files.createTempFile(target.getParent(), "." + target.getFileName(), ".tmp");
            boolean result;
            try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(temp))) {
                result = write(out, printable, portrait, fb);
            }
            if (!result) {
                return false;
            }
            try {
                Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING);
            }
            temp = null;
            return true;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return false;
        } finally {
            if (temp != null) {
                try {
                    Files.deleteIfExists(temp);
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
    }

    public static boolean write(OutputStream filename, Printable printable, boolean portrait, Feedback fb) {
        return write(filename, printable, portrait, new Insets(30, 30, 30, 30), fb);
    }
}
