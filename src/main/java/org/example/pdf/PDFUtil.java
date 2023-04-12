package org.example.pdf;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by ifnoelse on 2017/2/25 0025.
 */
public class PDFUtil {
    private static Pattern bookmarkPattern = Pattern.compile("^[\t\\s　]*?([0-9.]+)?(.*?)/?[\t\\s　]*([0-9]+)[\t\\s　]*?$");
    private static String blankRegex = "[\t\\s　]+";

    public static String replaceBlank(String str) {
        return str.replaceAll(blankRegex, " ").trim();
    }

    public static void addBookmark(String bookmarks, String srcFile, String destFile, int pageIndexOffset) {

        if (bookmarks != null && !bookmarks.isEmpty()) {
            if (bookmarks.trim().startsWith("http")) {
                addBookmark(PDFContents.getContentsByUrl(bookmarks), srcFile, destFile, pageIndexOffset);
            } else {
                addBookmark(Arrays.asList(bookmarks.split("\n")), srcFile, destFile, pageIndexOffset);
            }
        }
    }


    public static List<Bookmark> generateBookmark(String bookmarks, int pageIndexOffset, int minLens, int maxLnes) {
        return generateBookmark(Arrays.asList(bookmarks.split("\n")), pageIndexOffset, minLens, maxLnes);
    }

    public static List<Bookmark> generateBookmark(String bookmarks, int pageIndexOffset) {
        return generateBookmark(Arrays.asList(bookmarks.split("\n")), pageIndexOffset, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Add a directory to the pdf file
     *
     * @param bookmarks       Directory content, each list element is a directory content, such as：“1.1 Functional vs. Imperative Data Structures 1”
     * @param pageIndexOffset The pdf file is really the offset between the page number and the directory page number.
     * @param minLens         Legal directory entry minimum length
     * @param maxLnes         Legal directory entry maximum length
     * @return Returns a list of bookmarked content
     */
    public static List<Bookmark> generateBookmark(List<String> bookmarks, int pageIndexOffset, int minLens, int maxLnes) {
        List<Bookmark> bookmarkList = new ArrayList<>();
        for (String ln : bookmarks) {
            ln = replaceBlank(ln);
            if (ln.length() < minLens || ln.length() > maxLnes) continue;
            Matcher matcher = bookmarkPattern.matcher(ln);
            if (matcher.find()) {
                String seq = matcher.group(1);
                String title = replaceBlank(matcher.group(2));
                int pageIndex = Integer.parseInt(matcher.group(3));
                if (seq != null && bookmarkList.size() > 0) {
                    Bookmark pre = bookmarkList.get(bookmarkList.size() - 1);
                    if (pre.getSeq() == null || seq.startsWith(pre.getSeq())) {
                        pre.addSubBookMarkBySeq(new Bookmark(seq, title, pageIndex + pageIndexOffset));
                    } else {
                        bookmarkList.add(new Bookmark(seq, title, pageIndex + pageIndexOffset));
                    }
                } else {
                    bookmarkList.add(new Bookmark(seq, title, pageIndex + pageIndexOffset));
                }

            } else {
                bookmarkList.add(new Bookmark(replaceBlank(ln)));
            }
        }
        return bookmarkList;
    }


    public static void addBookmark(List<String> bookmarks, String srcFile, String destFile, int pageIndexOffset, int minLens, int maxLnes) {
        addBookmark(generateBookmark(bookmarks, pageIndexOffset, minLens, maxLnes), srcFile, destFile);
    }

    public static void addBookmark(List<String> bookmarks, String srcFile, String destFile, int pageIndexOffset) {
        addBookmark(bookmarks, srcFile, destFile, pageIndexOffset, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public static void addBookmark(Bookmark bookmark, String srcFile, String destFile) {
        addOutlines(Arrays.asList(bookmark.outlines()), srcFile, destFile);
    }

    public static void addBookmark(List<Bookmark> bookmarks, String srcFile, String destFile) {
        addOutlines(bookmarks.stream().map(Bookmark::outlines).collect(Collectors.toList()), srcFile, destFile);
    }

    private static void addOutlines(List<HashMap<String, Object>> outlines, String srcFile, String destFile) {
        try {
            class MyPdfReader extends PdfReader {
                public MyPdfReader(String fileName) throws IOException {
                    super(fileName);
                    unethicalreading = true;
                    encrypted = false;
                }
            }
            PdfReader reader = new MyPdfReader(srcFile);
            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(destFile));
            stamper.setOutlines(outlines);
            stamper.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void pdfToImage(int start, int end, String fileName, List<String> imgList) {
        try {
            PDDocument document = PDDocument.load(new File(fileName));

            PDFRenderer renderer = new PDFRenderer(document);
            for (int i = start; i <= end; i++) {
                BufferedImage image = renderer.renderImageWithDPI(i - 1, 300);
                String currentDirectory = System.getProperty("user.dir");
                System.out.println(currentDirectory);
                String dictPath =currentDirectory + File.separator + "data1";
                File directory = new File(dictPath);
                if (!directory.exists()) {
                    boolean success = directory.mkdirs();
                    System.out.println(success);
                }
                String imgName = dictPath + File.separator  + i + ".png";
                ImageIO.write(image, "png", new File(imgName));
                imgList.add(imgName);
            }
            document.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

}
