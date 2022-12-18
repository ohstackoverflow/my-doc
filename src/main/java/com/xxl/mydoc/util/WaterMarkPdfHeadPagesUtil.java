package com.xxl.mydoc.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.apache.pdfbox.util.Matrix;

import java.io.*;

public class WaterMarkPdfHeadPagesUtil {
    public static void main(String[] args) {
        //File file = new File("G://正式资料//公司前台行为锚定分析 .docx");
        File file = new File("I:\\考研\\2023田静英语二《句句真研》高清版PDF【全网首发无水印】.pdf");
        try {
            Append2DdfFile(file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void append(File file) {
        try {
            if(file.getName().endsWith(".pdf")) {
                Append2DdfFile(file);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    static void Append2DdfFile(File file) {

        String waterMark = "更 多 品 质 资 料 ， 请 访 问  go100.site 获 取 ";
        float fontSize = 30;
        int[] color = {220, 220, 220};
        int rowSpace = 550;
        int colSpace = 150;
        try {
            make(file, waterMark, fontSize, color, rowSpace, colSpace);
        } catch (IOException e) {
            System.out.println("水印渲染异常：" + e);
        }
    }

    static void make(File tempFile, String waterMark, float fontSize, int[] color, int rowSpace, int colSpace) throws IOException {
        // 加载PDF文件
        PDDocument document = PDDocument.load(tempFile);
        document.setAllSecurityToBeRemoved(true);

        // 加载水印字体
        PDFont font = PDType0Font.load(document, new FileInputStream("C://Windows//Fonts//simfang.ttf"), true);

        PDPageTree pageTree = document.getPages();

        PDExtendedGraphicsState r = new PDExtendedGraphicsState();

        // 设置透明度
        r.setNonStrokingAlphaConstant(0.8f);
        r.setAlphaSourceFlag(true);

        int top = 5;
        if(pageTree.getCount() < 5) {
            top = pageTree.getCount();
        }

        for(int i=1; i <= top; i++) {
            //PDRectangle pageSize = pageTree.get(0).getMediaBox();
            PDPageContentStream contentStream = new PDPageContentStream(document, pageTree.get(i), true, true,true);
            //PDFont font = PDType1Font.TIMES_ROMAN;
            float fontSize0 = 12.0f;
            contentStream.beginText();
            // set font and font size
            contentStream.setFont( font, fontSize0);
            contentStream.moveTextPositionByAmount(10, pageTree.get(i).getMediaBox().getHeight() - 20);
            contentStream.drawString( waterMark);
            contentStream.endText();
            //contentStream.
            contentStream.close();
        }

        document.save(tempFile);

        document.close();
    }

}
