package bd.edu.diu.cis.classroom.utils;

import java.util.ArrayList;
import java.util.List;

public class FileExtensionCheck {

    public static boolean imageCheck(String extension) {
        List<String> list = new ArrayList<String>();
        list.add("JPG");
        list.add("PNG");
        list.add("GIF");
        list.add("JPEG");
        return list.contains(extension);
    }

}
