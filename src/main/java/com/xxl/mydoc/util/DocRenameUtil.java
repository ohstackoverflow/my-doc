package com.xxl.mydoc.util;

import java.io.File;
import java.util.Arrays;

public class DocRenameUtil {
    public static void main(String[] args) {
        String srcFolder = "H:\\downloads\\zl\\高中第一学期期中试卷\\";
        File dir =  new File(srcFolder);
        Arrays.stream(dir.listFiles()).forEach(f -> {
            if(f.isFile() && f.getName().indexOf("_unlocked") != -1 ) {
                f.renameTo(new File(srcFolder + f.getName().replace("_unlocked","")) );
            }

        });
    }
}
