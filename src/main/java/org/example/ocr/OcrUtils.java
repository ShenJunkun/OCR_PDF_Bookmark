package org.example.ocr;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OcrUtils {
    private static final double OVERLAP_RADIO = 0.6;
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
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < ocrResponse.data.length; ) {
//            System.out.println(i);
            int start = i;
            OcrEntry startOcrEntry = ocrResponse.data[start];
            double top_y = (startOcrEntry.box[0][1] + startOcrEntry.box[1][1]) * 0.5;
            double down_Y = (startOcrEntry.box[2][1] + startOcrEntry.box[3][1]) * 0.5;
//            System.out.println(ocrResponse.data[i]);

            for(int j = i + 1; j <= ocrResponse.data.length; j++) {
                if (j == ocrResponse.data.length) {
                    i = j;
                    break;
                }

                OcrEntry endOcrEntry = ocrResponse.data[j];
                double top_tmp = (endOcrEntry.box[0][1] + endOcrEntry.box[1][1]) * 0.5;
                double down_tmp = (endOcrEntry.box[2][1] + endOcrEntry.box[3][1]) * 0.5;

                double top_ = Math.max(top_y, top_tmp);
                double down_ = Math.min(down_Y, down_tmp);

                if ( ((down_ - top_) / (down_Y - top_y)) >= OVERLAP_RADIO) {
//                    j++;
                    System.out.println((down_ - top_) / (down_Y - top_y));
                } else {
                    System.out.println((down_ - top_) / (down_Y - top_y));
                    //处理数据
                    TreeMap<Integer, Integer> map = new TreeMap<>();

                    for (int tt = start; tt < j; tt++) {
                        map.put(ocrResponse.data[tt].box[0][0], tt);
                    }

                    if (map.size()<=1) {
                        System.out.println( "ttt: "+ocrResponse.data[map.firstEntry().getValue()].text);
                        i = j;
                        break;
                    }

                    if (ocrResponse.data[map.lastEntry().getValue()].text.
                            replaceAll("[^0-9]", "").isEmpty()) {
                        // 最后一个没有数字，证明不是书签
                        i = j;
                        break;
                    }

                    String pageNum = ocrResponse.data[map.lastEntry().getValue()].text.
                            replaceAll("[^0-9]", "");

                    if (map.size() == 3) {
                        String first = null;
                        Map.Entry<Integer, Integer> entry = map.entrySet().stream().skip(1).findFirst().get();
                        String middle = ocrResponse.data[entry.getValue()].text;

                        //匹配 1.1.1 章节模式
                        Pattern pattern = Pattern.compile("\\d+\\.\\d+\\.\\d+");
                        Matcher matcher = pattern.matcher(ocrResponse.data[map.firstEntry().getValue()].text);
                        if (matcher.find()) {
                            first = matcher.group();
                            stringBuilder.append("iii ");
                            stringBuilder.append(first);
                            stringBuilder.append(" ");
                            stringBuilder.append(middle);
                            stringBuilder.append(" ");
                            stringBuilder.append(pageNum);
                            stringBuilder.append("\n");
                            i = j;
                            break;
                        }

                        //匹配1.1 模式
                        pattern = Pattern.compile("\\d+\\.\\d+");
                        matcher = pattern.matcher(ocrResponse.data[map.firstEntry().getValue()].text);
                        if(matcher.find()) {
                            first = matcher.group();
                            stringBuilder.append("ii ");
                            stringBuilder.append(first);
                            stringBuilder.append(" ");
                            stringBuilder.append(middle);
                            stringBuilder.append(" ");
                            stringBuilder.append(pageNum);
                            stringBuilder.append("\n");
                            i = j;
                            break;
                        }

                        //匹配 第x章 模式
                        String firstText = ocrResponse.data[map.firstEntry().getValue()].text;
                        if (firstText.contains("章")) {
                            stringBuilder.append("i ");
                            stringBuilder.append(firstText);
                            stringBuilder.append(" ");
                            stringBuilder.append(middle);
                            stringBuilder.append(" ");
                            stringBuilder.append(pageNum);
                            stringBuilder.append("\n");
                            i = j;
                            break;
                        } else if (firstText.contains("节")) {
                            stringBuilder.append("ii ");
                            stringBuilder.append(firstText);
                            stringBuilder.append(" ");
                            stringBuilder.append(middle);
                            stringBuilder.append(" ");
                            stringBuilder.append(pageNum);
                            stringBuilder.append("\n");
                            i = j;
                            break;
                        }

                    } else if (map.size() == 2) {
                        String firstText = ocrResponse.data[map.firstEntry().getValue()].text;
                        String secondText = ocrResponse.data[map.lastEntry().getValue()].text;
                        firstText.replaceAll(".", "");
                        secondText.replaceAll(".", "");

                        if (secondText.replaceAll("[^0-9]", "").isEmpty()) {
                            // 最后一个没有数字，证明不是书签
                            i = j;
                            break;
                        }

                        String firstPart = null;
                        String secondPart = null;
                        if ((!firstText.contains("章")) && (!firstText.contains("节"))) {
                            stringBuilder.append("ii ");
                            stringBuilder.append(firstText);
                            stringBuilder.append(" ");
                            stringBuilder.append(secondText);
                            stringBuilder.append("\n");
                            i = j;
                            break;
                        }
                        if (firstText.contains("章")) {
                            int index = firstText.indexOf("章");
                            firstPart = firstText.substring(0 ,index + 1);
                            secondPart = firstText.substring(index + 1);
                            stringBuilder.append("i ");
                        } else if(firstText.contains("节")) {
                            int index = firstText.indexOf("节");
                            firstPart = firstText.substring(0 ,index + 1);
                            secondPart = firstText.substring(index + 1);
                            stringBuilder.append("ii ");
                        }
                        stringBuilder.append(firstPart);
                        stringBuilder.append(" ");
                        stringBuilder.append(secondPart);
                        stringBuilder.append(" ");
                        stringBuilder.append(secondText);
                        stringBuilder.append("\n");
                        i = j;
                        break;

                    } else {
                        for(Map.Entry<Integer, Integer> entry : map.entrySet()) {
                            int idx = entry.getValue();
                            String txt = ocrResponse.data[idx].text;
                            txt.replaceAll(".", "");
                            stringBuilder.append(txt);
                            stringBuilder.append(" ");
                        }
                        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                        stringBuilder.append("\n");
                    }

                    i = j;
                    break;
                }
            }


        }
        return stringBuilder.toString();
    }
}
