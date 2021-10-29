package tech.aomi.osshub.util;

import java.io.File;

/**
 * @author Sean createAt 2021/10/27
 */
public class FileUtil {


    public static String getName(String path) {
        return path.substring(path.lastIndexOf(File.separator) + 1);
    }

}
