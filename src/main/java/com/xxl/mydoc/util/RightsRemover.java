package com.xxl.mydoc.util;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class RightsRemover {
    public static void main(String[] args) {
        File file = new File("I:\\全国初中中考\\物理\\processing\\2020年福建省中考物理试题（原卷版）.docx");
        try {
            make(file.getAbsolutePath(), "本试卷的题干、答案和解析均由组卷网");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void doit(File file) {
        if(!file.getName().endsWith(".docx")) {
            return;
        }
        try {
            make(file.getAbsolutePath(), "本试卷的题干、答案和解析均由组卷网");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void make(String srcFile, String rightsSample) throws IOException {
        FileInputStream fis = new FileInputStream(srcFile);
        XWPFDocument xwpfDocument = new XWPFDocument(fis);
        fis.close();

        List<XWPFParagraph> xwpfParas = xwpfDocument.getParagraphs();
        int posOfRights = -1;
        for(int i=0;i<xwpfParas.size();i++) {
            XWPFParagraph xwpfParagraph = xwpfParas.get(i);
            String text = xwpfParagraph.getText();
            if(text.indexOf(rightsSample) != -1) {
                posOfRights = i;
                break;
            }
        }

        if(posOfRights != -1) {
            //尝试 ***前面的*** 小图片段落
            if(xwpfParas.get(posOfRights-1).getText().trim().equals("")) {
                //xwpfDocument.removeBodyElement(xwpfDocument.getPosOfParagraph(xwpfParas.get(posOfRights-1)));
                posOfRights--;
            }

            //删除：从rights开始到文档末尾
            for(int i = xwpfParas.size()-1; i >= posOfRights; i-- ) {
                xwpfDocument.removeBodyElement(xwpfDocument.getPosOfParagraph(xwpfParas.get(i)));
            }

            FileOutputStream out = new FileOutputStream(srcFile);
            xwpfDocument.write(out);
            out.close();
        }


        xwpfDocument.close();

    }
}
