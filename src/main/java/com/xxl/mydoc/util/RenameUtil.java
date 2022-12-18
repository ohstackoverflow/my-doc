package com.xxl.mydoc.util;

import java.io.File;

public class RenameUtil {
    public static void doit(File file) {
        if(file.getName().indexOf("#") != -1 || file.getName().indexOf(" ") != -1 || file.getName().indexOf("　") != -1) {
            System.out.println(file.getPath());
            String destName = file.getName();
            destName = destName.replace("#","");
            destName = destName.replace("  ","-");
            destName = destName.replace(" ","-");
            destName = destName.replace("　 ","-");
            destName = destName.replace("　 ","-");
            destName = destName.replace("--","-");
            file.renameTo(new File(file.getParent() + "/" + destName));
        }
    }
}
