package bd.edu.diu.cis.classroom.utils;

import java.util.ArrayList;
import java.util.List;

public class FileExtensionCheck {

    public static boolean imageCheck(String name) {
        String extension = name.substring(name.lastIndexOf(".")).replace(".", "").toUpperCase();
        List<String> list = new ArrayList<String>();
        list.add("JPG");
        list.add("PNG");
        list.add("GIF");
        list.add("JPEG");
        return list.contains(extension);
    }

}
