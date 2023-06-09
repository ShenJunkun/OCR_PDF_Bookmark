package org.example.ocr;

import java.io.*;
import java.util.HashMap;
import java.util.Map;


public class Test {
    public static void main(String[] args) {
        // paddleocr_json 的可执行文件所在路径
        String exePath = "PaddleOCR-json.v1.2.1/PaddleOCR_json.exe";

        // 可选的配置项
        Map<String, Object> arguments = new HashMap<>();
        // arguments.put("use_angle_cls", true);
        String currentDirectory = System.getProperty("user.dir");
        System.out.println("当前目录路径为：" + currentDirectory);
        // 初始化 OCR
        try (Ocr ocr = new Ocr(new File(exePath), arguments)) {

            // 对一张图片进行 OCR
            String imgPath = "C:\\Users\\shenj\\Pictures\\1.jpg";
            String imgPath1 = currentDirectory + File.separator + "data" + File.separator + "1.png";
            OcrResponse resp = ocr.runOcr(new File(imgPath1));

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

    }
}