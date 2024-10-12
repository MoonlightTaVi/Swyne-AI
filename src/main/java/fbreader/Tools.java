package fbreader;

import org.mozilla.universalchardet.UniversalDetector;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tools {
    public static String detectCharset(String path) {
        try (InputStream inputStream = new FileInputStream(path)) {
            byte[] bytes = new byte[4096];
            UniversalDetector detector = new UniversalDetector(null);
            int nread;
            while ((nread = inputStream.read(bytes)) > 0 && !detector.isDone()) {
                detector.handleData(bytes, 0, nread);
            }
            detector.dataEnd();
            return detector.getDetectedCharset();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String readText(String path, String charset) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), charset))) {
            StringBuilder ret = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                ret.append(line).append("\n");
            }
            return ret.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static List<String> readToList(String content, int parts) {
        String regex = "<p>(.*?)</p>";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(content);
        int i = 0;
        List<String> ret = new ArrayList<>();
        while (m.find() && i < parts) {
            ret.add(m.group(1));
            i++;
        }
        return ret;
    }
}
