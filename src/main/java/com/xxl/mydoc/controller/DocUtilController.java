package com.xxl.mydoc.controller;

import com.xxl.mydoc.model.DocModel;
import com.xxl.mydoc.repository.DocRepository;
import com.xxl.mydoc.util.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.xxl.mydoc.myconst.MyConst.AnSubjects;

@RestController
public class DocUtilController {

    @Autowired
    private DocRepository docRepository;


    static int getSubjectFromStr(String str) {
        int subject = 0;
        if(str.indexOf("语文") != -1) {
            subject = 1;
        }
        if(str.indexOf("数学") != -1) {
            subject = 2;
        }
        if(str.indexOf("英语") != -1) {
            subject = 3;
        }
        if(str.indexOf("物理") != -1) {
            subject = 4;
        }
        if(str.indexOf("化学") != -1) {
            subject = 5;
        }
        if(str.indexOf("生物") != -1) {
            subject = 6;
        }
        if(str.indexOf("地理") != -1) {
            subject = 7;
        }
        if(str.indexOf("历史") != -1) {
            subject = 8;
        }
        if(str.indexOf("政治") != -1) {
            subject = 9;
        }
        return subject;
    }

    private static int getSubjectFromPdfFile(File f) {

        int subject = 0;
        PDDocument document = null;
        try {
            document = PDDocument.load(f);
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(document);

            subject = getSubjectFromStr(text);

            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(document != null) {
                try {
                    document.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return subject;
    }

    @GetMapping("/pdf2db")
    private void saveToDb() {
        String[] folders = new String[]{};
        //String[] folders = new String[]{"H:\\downloads\\zl\\预初 32套\\","H:\\downloads\\zl\\初一 35套\\","H:\\downloads\\zl\\初二 40套\\","H:\\downloads\\zl\\初三 37套\\"};

        for(String folder: folders ) {
            File dir =  new File(folder);


            Arrays.stream(dir.listFiles()).forEach(f -> {
                if(f.isFile()) {
                    System.out.println(f.getName());

                    DocModel doc = new DocModel();
                    if(folder.indexOf("预初") != -1) {
                        doc.setGrade(6);
                    }
                    if(folder.indexOf("初一") != -1) {
                        doc.setGrade(7);
                    }
                    if(folder.indexOf("初二") != -1) {
                        doc.setGrade(8);
                    }
                    if(folder.indexOf("初三") != -1) {
                        doc.setGrade(9);
                    }

                    doc.setName(f.getName());

                    int subject = getSubjectFromStr(f.getName());
                    if(subject != 0) {
                        doc.setSubject(subject);
                    } else {
                        doc.setSubject(getSubjectFromPdfFile(f));
                    }

                    if(doc.getSubject() == 0) {
                        System.out.println(f.getName() + "###########################################未找到学科");
                    }

                    doc.setCreatedDate(new Date());

                    docRepository.save(doc);
                    System.out.println(f.getName() + " Done.");

                }
            });

        }



    }

    @GetMapping("/pdf2db1")
    private void saveToDb1() {
        String[] folders = new String[]{};
        //String[] folders = new String[]{"H:\\downloads\\zl\\预初 32套\\","H:\\downloads\\zl\\初一 35套\\","H:\\downloads\\zl\\初二 40套\\","H:\\downloads\\zl\\初三 37套\\"};

        for(String folder: folders ) {
            File dir =  new File(folder);


            Arrays.stream(dir.listFiles()).forEach(f -> {
                if(f.isFile()) {
                    System.out.println(f.getName());

                    DocModel doc = new DocModel();
                    if(f.getName().indexOf("高一") != -1) {
                        doc.setGrade(10);
                        if(f.getName().indexOf("开学考") != -1) {
                            doc.setGrade(9);
                        }
                    }
                    if(f.getName().indexOf("高二") != -1) {
                        doc.setGrade(11);
                        if(f.getName().indexOf("开学考") != -1) {
                            doc.setGrade(10);
                        }
                    }
                    if(f.getName().indexOf("高三") != -1) {
                        doc.setGrade(12);
                        if(f.getName().indexOf("开学考") != -1) {
                            doc.setGrade(11);
                        }
                    }


                    doc.setName(f.getName());

                    int subject = getSubjectFromStr(f.getName());
                    if(subject != 0) {
                        doc.setSubject(subject);
                    } else {
                        doc.setSubject(getSubjectFromPdfFile(f));
                    }

                    if(doc.getGrade() == 0) {
                        System.out.println(f.getName() + "###########################################未找到年级");
                    }

                    if(doc.getSubject() == 0) {
                        System.out.println(f.getName() + "###########################################未找到学科");
                    }

                    doc.setCreatedDate(new Date());

                    docRepository.save(doc);
                    System.out.println(f.getName() + " Done.");

                }
            });

        }



    }

    @GetMapping("/pdf2db2")
    private void renameFileName() {
        docRepository.findAll().forEach(doc -> {
            String name = doc.getName();
            int subject = getSubjectFromStr(name);
            if(subject == 0) {  //名字里不含 语文、数学、英语 字样
                String strSubjectShouldBe = AnSubjects.get(doc.getSubject());

                //更新db中的 name 字段
                doc.setName(doc.getName().replace(".pdf",strSubjectShouldBe +".pdf"));
                docRepository.save(doc);

                //更改文件名
                String srcFolder = "H:\\downloads\\zl\\done\\";
                File dir =  new File(srcFolder);
                Arrays.stream(dir.listFiles()).forEach(f -> {
                    if(f.isFile() && f.getName().equals( name ) ) {
                        f.renameTo(new File(srcFolder + f.getName().replace(".pdf",strSubjectShouldBe +".pdf")) );
                    }

                });


            }
        });
    }

    @GetMapping("/pdf2db3")
    private void pdf2db() {
        String srcFolder = "H:\\downloads\\zl\\Test\\to4\\";
        File dir =  new File(srcFolder);
        Arrays.stream(dir.listFiles()).forEach(f -> {
            if(f.isFile()) {
                DocModel doc = new DocModel();
                doc.setName(f.getName());
                doc.setGrade(9);
                doc.setSubject(4);
                doc.setCreatedDate(new Date());
                docRepository.save(doc);
            }

        });
    }

    @GetMapping("/pdf2dbupdate")
    private void clearFileName() {
        String srcFolder = "H:\\downloads\\zl\\Test\\";
        String destFolder = "H:\\downloads\\zl\\Test\\to\\";
        File dir =  new File(srcFolder);
        Arrays.stream(dir.listFiles()).forEach(f -> {
            if(f.isFile() ) {

                docRepository.findAll().forEach( doc -> {
                    if(  doc.getName().equals(f.getName()) &&  !(doc.getSubject() == 1 || doc.getSubject() == 3 ) ) {
                        f.renameTo(new File(destFolder + f.getName() ));
                        doc.setCategoryId(-1);
                        docRepository.save(doc);
                    }

                });


            }

        });

    }


    private void replaceTxtFile(String name, long id)
    {
        String read;
        String read2;
        try{
            FileReader fileread = new FileReader("C:\\apache-tomcat-8.5.81\\webapps\\ROOT\\preview_template.html");
            BufferedReader bufread = new BufferedReader(fileread);
            FileWriter newFile =new FileWriter("C:\\apache-tomcat-8.5.81\\webapps\\ROOT\\preview_" + id + ".html");
            BufferedWriter bw = new BufferedWriter(newFile);
            while ((read = bufread.readLine()) != null)
            {
                read2 = read.replace("@@title@@",name);

                //System.out.println(read2);
                bw.write(read2);
                bw.newLine();
                bw.flush();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/db2html")
    public void generateHtml() throws Exception {
         docRepository.findAll().forEach(doc -> {
             long id = doc.getId();
             String name = doc.getName();
             replaceTxtFile(name, id);
         });
    }


    @GetMapping("/db2html_category")
    public void generateCategoriesHtml() throws Exception {

        List<DocModel> m3ch = new ArrayList<>();
        List<DocModel> m3en = new ArrayList<>();
        List<DocModel> m3math = new ArrayList<>();

        List<DocModel> mphy = new ArrayList<>();
        List<DocModel> mche = new ArrayList<>();


        List<DocModel> h3ch = new ArrayList<>();
        List<DocModel> h3en = new ArrayList<>();
        List<DocModel> h3math = new ArrayList<>();

        List<DocModel> hphy = new ArrayList<>();
        List<DocModel> hche = new ArrayList<>();
        List<DocModel> hbio = new ArrayList<>();
        List<DocModel> hhis = new ArrayList<>();
        List<DocModel> hgo = new ArrayList<>();
        List<DocModel> hpol = new ArrayList<>();

        docRepository.findAll().forEach(doc -> {
            //根据 科目 和 年级 分类
            switch (doc.getGrade()) {
                case 9:
                    if(doc.getSubject() == 1) {
                        m3ch.add(doc);
                    }
                    if(doc.getSubject() == 2) {
                        m3math.add(doc);
                    }
                    if(doc.getSubject() == 3) {
                        m3en.add(doc);
                    }

                    if(doc.getSubject() == 4) {
                        mphy.add(doc);
                    }
                    if(doc.getSubject() == 5) {
                        mche.add(doc);
                    }
                    break;


                case 12:
                    if(doc.getSubject() == 1) {
                        h3ch.add(doc);
                    }
                    if(doc.getSubject() == 2) {
                        h3math.add(doc);
                    }
                    if(doc.getSubject() == 3) {
                        h3en.add(doc);
                    }

                    if(doc.getSubject() == 4) {
                        hphy.add(doc);
                    }
                    if(doc.getSubject() == 5) {
                        hche.add(doc);
                    }

                    if(doc.getSubject() == 6) {
                        hbio.add(doc);
                    }
                    if(doc.getSubject() == 7) {
                        hgo.add(doc);
                    }

                    if(doc.getSubject() == 8) {
                        hhis.add(doc);
                    }
                    if(doc.getSubject() == 9) {
                        hpol.add(doc);
                    }
                    break;
            }

        });

        //初中


        replaceTxtFileByCategory("shmch", populateContent(m3ch), populateNavMid("shmch"));
        replaceTxtFileByCategory("shmen", populateContent(m3en), populateNavMid("shmen"));
        replaceTxtFileByCategory("shmmath", populateContent(m3math), populateNavMid("shmmath"));

        replaceTxtFileByCategory("shmphy", populateContent(mphy), populateNavMid("shmphy"));
        replaceTxtFileByCategory("shmche", populateContent(mche), populateNavMid("shmche"));



        //高中


        replaceTxtFileByCategory("shhch", populateContent(h3ch), populateNavHi("shhch"));
        replaceTxtFileByCategory("shhen", populateContent(h3en), populateNavHi("shhen"));
        replaceTxtFileByCategory("shhmath", populateContent(h3math), populateNavHi("shhmath"));

        replaceTxtFileByCategory("shhphy", populateContent(hphy), populateNavHi("shhphy"));
        replaceTxtFileByCategory("shhche", populateContent(hche), populateNavHi("shhche"));
        replaceTxtFileByCategory("shhbio", populateContent(hbio), populateNavHi("shhbio"));
        replaceTxtFileByCategory("shhgo", populateContent(hgo), populateNavHi("shhgo"));
        replaceTxtFileByCategory("shhhis", populateContent(hhis), populateNavHi("shhhis"));
        replaceTxtFileByCategory("shhpol", populateContent(hpol), populateNavHi("shhpol"));

    }

    private StringBuilder populateContent(List<DocModel> coll) {
        StringBuilder sb = new StringBuilder();
        coll.forEach( d -> {
            String curr = String.format("<div class=\"row my-item pt-3 pb-5\">\n" +
                    "  <div class=\"text-muted mb-3\">\n" +
                    "\t<i class=\"bi bi-calendar3 small me-1\"></i><small class=\"me-3\">%tF%n</small>\n" +
                    "\t<i class=\"bi bi-tags-fill small me-1\"></i><small class=\"me-3\">上海, 初中英语, 中考, 徐汇</small>\n" +
                    "\t<i class=\"bi bi-file-earmark-arrow-down-fill text-primary me-1\"></i><small class=\"me-3\">%s</small>\n" +
                    "\t<i class=\"bi bi-heart-fill small fw-bold me-1\"></i><small>%s</small>\n" +
                    "  </div>\n" +
                    "  <div class=\"my-font row mb-5\">\n" +
                    "    <div class=\"col mx-auto\">%s</div>\n" +
                    "    <div class=\"my-link col-2\"><a class=\"me-2\" target=\"_blank\" href=\"/preview.html?\">预览</a><a class=\"me-2\" target=\"_blank\" href=\"host/download?\">下载</a></div>\n" +
                    "  </div>\n" +
                    "</div>",d.getCreatedDate(), d.getDownloaded(), d.getLoved(), d.getName());
            sb.append(curr);

        });
        return sb;


    }
    private String populateNavMid(String categoryName) {
        String nav = 
                "<li>\n" +
                "\t<a href=\"shmch.html\" style=\"padding: 6px 30px; %s\">初中语文</a>\n" +
                "</li>\n" +
                "<li>\n" +
                "\t<a href=\"shmmath.html\" style=\"padding: 6px 30px; %s\">初中数学</a>\n" +
                "</li>\t\t\t\t  \n" +
                "<li>\n" +
                "\t<a href=\"shmen.html\" style=\"padding: 6px 30px; %s\">初中英语</a>\n" +
                "</li>\n" +
                "\n" +
                "<li>\n" +
                "\t<a href=\"shmphy.html\" style=\"padding: 6px 30px; %s\">初中物理</a>\n" +
                "</li>\t\t\t\t  \n" +
                "<li>\n" +
                "\t<a href=\"shmche.html\" style=\"padding: 6px 30px; %s\">初中化学</a>\n" +
                "</li>";

        switch (categoryName) {

            case "shmch":
                nav = String.format(nav,"background-color: #414141; my-checked:1;","","","","");
                break;
            case "shmmath":
                nav = String.format(nav,"","background-color: #414141; my-checked:1;","","","");
                break;
            case "shmen":
                nav = String.format(nav,"","","background-color: #414141; my-checked:1;","","");
                break;

            case "shmphy":
                nav = String.format(nav,"","","","background-color: #414141; my-checked:1;","");
                break;
            case "shmche":
                nav = String.format(nav,"","","","","background-color: #414141; my-checked:1;");
                break;

        }
        return nav;

    }

    private String populateNavHi(String categoryName) {
        String nav = 
                "<li>\n" +
                "<a href=\"shhch.html\" style=\"padding: 6px 30px; %s\">高中语文</a>\n" +
                "</li>\n" +
                "<li>\n" +
                "<a href=\"shhmath.html\" style=\"padding: 6px 30px; %s\">高中数学</a>\n" +
                "</li>  \n" +
                "<li>\n" +
                "<a href=\"shhen.html\" style=\"padding: 6px 30px; %s\">高中英语</a>\n" +
                "</li>\n" +
                "\n" +
                "<li>\n" +
                "<a href=\"shhphy.html\" style=\"padding: 6px 30px; %s\">高中物理</a>\n" +
                "</li>\n" +
                "<li>\n" +
                "<a href=\"shhche.html\" style=\"padding: 6px 30px; %s\">高中化学</a>\n" +
                "</li>  \n" +
                "<li>\n" +
                "<a href=\"shhhis.html\" style=\"padding: 6px 30px; %s\">高中历史</a>\n" +
                "</li>\n" +
                "\n" +
                "<li>\n" +
                "<a href=\"shhgo.html\" style=\"padding: 6px 30px; %s\">高中地理</a>\n" +
                "</li>\n" +
                "<li>\n" +
                "<a href=\"shhbio.html\" style=\"padding: 6px 30px; %s\">生命科学</a>\n" +
                "</li>  \n" +
                "<li>\n" +
                "<a href=\"shhpol.html\" style=\"padding: 6px 30px; %s\">思想政治</a>\n" +
                "</li>";

        switch (categoryName) {
            case "shhch":
                nav = String.format(nav,"background-color: #414141; my-checked:1;","","","","","","","","");
                break;
            case "shhmath":
                nav = String.format(nav,"","background-color: #414141; my-checked:1;","","","","","","","");
                break;
            case "shhen":
                nav = String.format(nav,"","","background-color: #414141; my-checked:1;","","","","","","");
                break;

            case "shhphy":
                nav = String.format(nav,"","","","background-color: #414141; my-checked:1;","","","","","");
                break;
            case "shhche":
                nav = String.format(nav,"","","","","background-color: #414141; my-checked:1;","","","","");
                break;
            case "shhhis":
                nav = String.format(nav,"","","","","background-color: #414141; my-checked:1;","","","","");
                break;
            case "shhgo":
                nav = String.format(nav,"","","","","","","background-color: #414141; my-checked:1;","","");
                break;
            case "shhbio":
                nav = String.format(nav,"","","","","","","","background-color: #414141; my-checked:1;","");
                break;
            case "shhpol":
                nav = String.format(nav,"","","","","","","","","background-color: #414141; my-checked:1;");
                break;

        }
        return nav;

    }

    private void replaceTxtFileByCategory(String categoryName, StringBuilder sbContent, String nav)
    {
        String read;
        String read2;
        try{
            FileReader fileread = new FileReader("C:\\apache-tomcat-8.5.81\\webapps\\ROOT\\doc_template.html");
            BufferedReader bufread = new BufferedReader(fileread);
            FileWriter newFile =new FileWriter("C:\\apache-tomcat-8.5.81\\webapps\\ROOT\\" + categoryName + ".html");
            BufferedWriter bw = new BufferedWriter(newFile);
            while ((read = bufread.readLine()) != null)
            {
                read2 = read.replace("@@my-content@@",sbContent.toString());
                read2 = read2.replace("@@my-nav@@",nav);

                //System.out.println(read2);
                bw.write(read2);
                bw.newLine();
                bw.flush();
            }

            bw.close();
            bufread.close();
            fileread.close();
            newFile.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    @PostMapping("/deal")
    public void deal(String parentFolder) {
        deal(new File(parentFolder));
    }

    private void deal(File f) {
        if(f.isDirectory()) {
            Arrays.stream(f.listFiles()).forEach(file -> {deal(file);});
        } else {
            //RenameUtil.doit(f);
            //RightsRemover.doit(f);  //仅移除docx的rights
            WaterMarkPdfHeadPagesUtil.append(f);
            EncrptUtil.encrpt(f);   //仅加密pdf
        }
    }

    @PostMapping("/makePdfZipPreview")
    public void makePdfZipPreview(String parentFolder) {
        makeSmallPdf(new File(parentFolder));
    }

    private void makeSmallPdf(File f) {
        if(f.isDirectory()) {
            Arrays.stream(f.listFiles()).forEach(file -> {makeSmallPdf(file);});
        } else {
            PageOneSaver.SavedAs(f);
        }
    }


}
