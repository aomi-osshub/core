package tech.aomi.osshub.constant;

import tech.aomi.common.web.controller.Permission;

import java.util.Arrays;
import java.util.List;

/**
 * @author Sean createAt 2021/10/26
 */
public class Common {

    public static List<Permission> ALL_PERMISSION = Arrays.asList(
            Permission.get(new String[]{"/files"}, new Authority[]{Authority.FILE_ALL, Authority.FILE_BROWSE}),
            Permission.get(new String[]{"/files/{id}"}, new Authority[]{Authority.FILE_ALL, Authority.FILE_READ}),
            Permission.post(new String[]{"/files/**"}, new Authority[]{Authority.FILE_ALL, Authority.FILE_WRITE}),
            Permission.delete(new String[]{"/files/**"}, new Authority[]{Authority.FILE_ALL, Authority.FILE_WRITE})
    );
}
