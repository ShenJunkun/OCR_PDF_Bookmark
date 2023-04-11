import org.example.pdf.PDFUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TestPDF {

    public static void main(String[] args) {
        List<String> stringName = new ArrayList<>();
//        PDFUtil.pdfToImage(40, 43, "data/xin.pdf", stringName);
        PDFUtil.pdfToImage(1, 1, "data/1.pdf", stringName);
        //delete image
//        for (String imgName : stringName) {
//            File img = new File(imgName);
//            if (img.delete()) {
//                System.out.println("Image " +  imgName + "file deleted successfully");
//            } else {
//                System.out.println("Failed to delete image file");
//            }
//        }
    }
}
