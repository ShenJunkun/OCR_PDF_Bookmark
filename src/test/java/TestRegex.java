import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestRegex {
    @Test
    public void test() {
        String str = "The price is $";
        String digitsOnly = str.replaceAll("[^0-9]", "");
        if (digitsOnly.isEmpty()) {
            System.out.printf("ddd");
        }


    }

    @Test
    public void string() {
        String s1 ="string1";
        String s2 = "string2";
        String s = s1 + "\n" + s2;
        System.out.println(s);
    }

    @Test
    public void testCrawl() {
        String input = "The IP address is 192.168.1.1";
        Pattern pattern = Pattern.compile("\\d+\\.\\d+\\.\\d+");

        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            String result = matcher.group();
            System.out.println(result);  // 输出："192.168.1"
        } else {
            System.out.println("No match found.");
        }

    }
}
