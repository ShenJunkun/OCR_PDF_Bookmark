package org.example.ocr;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OcrUtils {
    private static final String exePath = "PaddleOCR-json.v1.2.1/PaddleOCR_json.exe";
    public static OcrResponse orc(String imgPath) {
        Map<String, Object> arguments = new HashMap<>();
        OcrResponse resp = null;
        try (Ocr ocr = new Ocr(new File(exePath), arguments)) {

            resp = ocr.runOcr(new File(imgPath));

            // 或者直接识别剪贴板中的图片
            // OcrResponse resp = ocr.runOcrOnClipboard();

            // 读取结果
            if (resp.code == OcrCode.OK) {
                for (OcrEntry entry : resp.data) {
                    System.out.println(entry.text);
                }
            } else {
                System.out.println("error: code=" + resp.code + " msg=" + resp.msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resp;
    }

    public static String processOcrResponse(OcrResponse ocrResponse) {

        return null;
    }
}
