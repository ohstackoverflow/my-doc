package com.xxl.mydoc.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;

import java.io.File;
import java.io.IOException;

public class EncrptUtil {
    public static void main(String[] args) {
        encrpt(new File("G://正式资料//2020年北京市中考语文总复习：易考统编教材文学常识.pdf"));
    }

    public static void encrpt(File file) {

        if(!file.getName().endsWith(".pdf")) {
            return;
        }

        PDDocument document = null;
        try {
            document = PDDocument.load(file);

            //创建访问权限对象
            AccessPermission permissions = new AccessPermission();
            permissions.setCanModify(false);
            //创建标准化的支持policy对象
            StandardProtectionPolicy spp = new StandardProtectionPolicy("Qd5108232022@",null, permissions);
            //设置加密密钥的长度
            spp.setEncryptionKeyLength(128);
            //设置访问权限
            spp.setPermissions(permissions);
            //保护文件
            document.protect(spp);
            System.out.println("Document encrypted:" + file.getAbsolutePath());
            //保存文档
            document.save(file);
            //关闭文件

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                document.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
