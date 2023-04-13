import com.google.gson.Gson;
import org.example.ocr.OcrResponse;
import org.example.ocr.OcrUtils;
import org.junit.Test;

import java.io.*;
import java.util.Map;

public class TestOcr {
    @Test
    public  void testParseOcrResponce() {

        String ss;
        File file = new File("data/myjson.json");
        try {
            FileReader reader = new FileReader(file);
            BufferedReader br = new BufferedReader(reader);

            ss = br.readLine();

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        System.out.println(ss);
        Gson gson = new Gson();
        OcrResponse ocrResponse = null;
        Map rawJsonObj = gson.fromJson(ss, Map.class);
        if (rawJsonObj.get("data") instanceof String) {
            ocrResponse = new OcrResponse((int)Double.parseDouble(rawJsonObj.get("code").toString()), rawJsonObj.get("data").toString());
            System.out.println("here");
        }

        ocrResponse = gson.fromJson(ss, OcrResponse.class);

        System.out.println(OcrUtils.processOcrResponse(ocrResponse));

//        System.out.println(ocrResponse);


    }

}
