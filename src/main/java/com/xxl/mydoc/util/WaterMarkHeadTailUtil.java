package com.xxl.mydoc.util;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.apache.pdfbox.util.Matrix;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;

import java.io.*;

public class WaterMarkHeadTailUtil {
    public static void main(String[] args) {
        //File file = new File("G://正式资料//公司前台行为锚定分析 .docx");
        File file = new File("G://正式资料//2023届北京高考语文一轮复习：文学类阅读专练(散文类)(有答案).pdf");
        try {
            Append2DdfFile(file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void append(File file) {
        try {
            if(file.getName().endsWith(".doc")) {
                appendDocHeader(file);
            }
            if(file.getName().endsWith(".docx")) {
                appendDocxHeader(file);
            }
            if(file.getName().endsWith(".pdf")) {
                Append2DdfFile(file);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    static void appendDocxHeader(File file) throws Exception {
        FileInputStream in = new FileInputStream( file );

        OPCPackage open = OPCPackage.open( in );
        XWPFDocument doc = new XWPFDocument( open );

//        doc.getProperties().getCoreProperties().setCreator("go100.site");
//        doc.getProperties().getCoreProperties().setLastModifiedByUser("go100.site");
//        doc.getProperties().getCoreProperties().setDescription("更多品质资料，请访问go100.site获取");

        CTSectPr sectPr = doc.getDocument().getBody().addNewSectPr();
        XWPFHeaderFooterPolicy headerFooterPolicy = new XWPFHeaderFooterPolicy( doc, sectPr );
        XWPFHeader header = headerFooterPolicy.createHeader( headerFooterPolicy.DEFAULT );
        XWPFParagraph paragraph = header.createParagraph();
        paragraph.setAlignment( ParagraphAlignment.LEFT );
        paragraph.setBorderBottom( Borders.THICK );
        XWPFRun run = paragraph.createRun();

        run.setText( "更多品质资料，请访问 go100.site 获取" );
        FileOutputStream fos = new FileOutputStream( file );
        doc.write( fos );
        fos.close();
    }
    static void appendDocHeader(File file) throws Exception {
        FileInputStream fileInputStream = new FileInputStream(file);
        POIFSFileSystem pfs = new POIFSFileSystem( fileInputStream );
        HWPFDocument hwpf = new HWPFDocument(pfs);//用poifsfileSystem初始化hwpf

//        SummaryInformation info = hwpf.getSummaryInformation();
//        info.setAuthor("go100.site");
//        info.setLastAuthor("go100.site");
//        info.setComments("更多品质资料，请访问go100.site获取");

        //Range range = hwpf.getRange();//获取全文
        //range.insertAfter("abc");
        hwpf.getHeaderStoryRange().insertAfter("\r\r\r更多品质资料，请访问 go100.site 获取").setColor(IndexedColors.GREY_50_PERCENT.index);
        //range.insertAfter("Hello");
        OutputStream output = new FileOutputStream( file.getAbsolutePath() );//初始化输出流
        hwpf.write(output);//写输出
        output.close();

        System.out.println(file.getAbsolutePath() + " done");
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

        //第一页
        PDRectangle pageSize = pageTree.get(0).getMediaBox();
        PDPageContentStream contentStream = new PDPageContentStream(document, pageTree.get(0), true, true,true);
        //PDFont font = PDType1Font.TIMES_ROMAN;
        float fontSize0 = 12.0f;
        contentStream.beginText();
        // set font and font size
        contentStream.setFont( font, fontSize0);
        contentStream.moveTextPositionByAmount(10, pageTree.get(0).getMediaBox().getHeight() - 20);
        contentStream.drawString( waterMark);
        contentStream.endText();
        //contentStream.
        contentStream.close();



        //最后一页
        PDPage page = pageTree.get(pageTree.getCount()-1);

        PDPageContentStream stream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true);
        r.setNonStrokingAlphaConstant(0.6f);
        stream.setGraphicsStateParameters(r);

        // 设置水印字体颜色
        if (color.length == 3) {
            stream.setNonStrokingColor(color[0], color[1], color[2]);
        }
        stream.beginText();
        stream.setFont(font, fontSize);
        stream.newLineAtOffset(0, 600);

        // 获取PDF页面大小
        float pageHeight = page.getMediaBox().getHeight();
        float pageWidth = page.getMediaBox().getWidth();

        // 根据纸张大小添加水印，30度倾斜
        for (int h = 10; h < pageHeight; h = h + rowSpace) {
            for (int w = - 10; w < pageWidth; w = w + colSpace) {
                stream.setTextMatrix(Matrix.getRotateInstance(0.2, w, h));
                stream.showText(waterMark);
            }
        }

        // 结束渲染，关闭流
        stream.endText();
        stream.restoreGraphicsState();
        stream.close();

        document.save(tempFile);

        document.close();
    }

}
