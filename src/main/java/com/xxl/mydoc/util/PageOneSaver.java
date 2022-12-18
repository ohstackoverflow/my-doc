package com.xxl.mydoc.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class PageOneSaver {

    public static void SavedAs(File file) {
        if(!file.getName().endsWith(".pdf")) {
            return;
        }

        String folder = "H:\\apache-tomcat-8.5.81\\webapps\\ROOT\\assets\\zip_preview\\";

        try {
            for(int i = 0; i <= 8; i++) {

                PDDocument doc = PDDocument.load(new FileInputStream(file));
                PDFRenderer pdfRenderer = new PDFRenderer(doc);
                int dpi = 300;
                BufferedImage buffImage = pdfRenderer.renderImageWithDPI(i, dpi, ImageType.RGB);

                int targetW = buffImage.getWidth();
                if(targetW >= 1500) {
                    targetW = 1500;
                }

                buffImage = resize(buffImage, targetW, targetW);//产生缩略图
                ImageIOUtil.writeImage(buffImage, file.getParent() + file.getName().replace(".pdf","-" + i + ".jpg"), dpi);

                doc.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static BufferedImage resize(BufferedImage source, int targetW,  int targetH) {
        int type=source.getType();
        BufferedImage target=null;
        double sx=(double)targetW/source.getWidth();
        double sy=(double)targetH/source.getHeight();
        if(sx>sy){
            sx=sy;
            targetW=(int)(sx*source.getWidth());
        }else{
            sy=sx;
            targetH=(int)(sy*source.getHeight());
        }
        if(type==BufferedImage.TYPE_CUSTOM){
            ColorModel cm=source.getColorModel();
            WritableRaster raster=cm.createCompatibleWritableRaster(targetW, targetH);
            boolean alphaPremultiplied=cm.isAlphaPremultiplied();
            target=new BufferedImage(cm,raster,alphaPremultiplied,null);
        }else{
            target=new BufferedImage(targetW, targetH,type);
        }
        Graphics2D g=target.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.drawRenderedImage(source, AffineTransform.getScaleInstance(sx, sy));
        g.dispose();
        return target;
    }

}
