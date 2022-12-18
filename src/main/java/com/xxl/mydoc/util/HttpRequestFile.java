package com.xxl.mydoc.util;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import static com.xxl.mydoc.myconst.MyConst.DOC_HOST;

public class HttpRequestFile {


    public static void getFile( HttpServletResponse response, String name, boolean isDownload) {

        if(name.indexOf("http") != -1) {   //替换：使用网盘打开，用户自行下载
            try {
                //response.setContentType("text/html;charset=utf-8");
                response.getWriter().append("<script>window.location.href='" + name + "';</script>");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            int pLash = name.indexOf("/");
            String folder = name.substring(0, pLash);
            String fileName = name.substring(pLash+1, name.length());

            try {
                if(isDownload) {
                    response.setContentType("application/x-msdownload");
                    response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));

                } else {
                    response.setHeader("Content-Disposition", "inline;filename=" + URLEncoder.encode(fileName, "utf-8"));
                }

                String urlStr = DOC_HOST + URLEncoder.encode(folder, "utf-8") + "/" + URLEncoder.encode(fileName, "utf-8");
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                // 设置超时间为3秒
                conn.setConnectTimeout(3 * 1000);
                //获取输入流
                InputStream inputStream = conn.getInputStream();
                //获取输出流
                ServletOutputStream outputStream = response.getOutputStream();
                //每次下载1024位
                byte[] b =new byte[1024];
                int len = -1;
                while((len = inputStream.read(b))!=-1) {
                    outputStream.write(b, 0, len);
                }
                inputStream.close();
                outputStream.close();

                System.out.println(name + " downloaded.");

            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }

    }


}
