package com.xxl.mydoc.controller;


import com.xxl.mydoc.model.DocModel;
import com.xxl.mydoc.model.RequestGenHtml;
import com.xxl.mydoc.model.HtmlModel;
import com.xxl.mydoc.repository.DocRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class HtmlGenerateUtilController {

    @Autowired
    private DocRepository docRepository;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    LinkedHashMap<String, Integer> kvs = new LinkedHashMap<>();

    private void populateKvs(File dir, int clz, int step) {
        if(step == 0)
            step = 10;

        AtomicInteger categoryId = new AtomicInteger(clz);
        int finalStep = step;
        Arrays.stream(dir.listFiles()).forEach(d -> {
            if(d.isDirectory()) {
                kvs.put(d.getName(),categoryId.get());
                categoryId.getAndAdd(finalStep);  //跳开，加100或10；这是大类别。其中的小类，加1.
            }
        });
    }

    //第二种模式
    @PostMapping("/categories2DB")
    public void categories2DB(String parentFolder, int clz, int hasSub, @RequestParam(required = false, defaultValue = "10") int step) {
        File dir =  new File(parentFolder);

        if(hasSub == 1) {
            populateKvs(dir, clz, step);
        }

        Arrays.stream(dir.listFiles()).forEach(d -> {

            if(d.isDirectory() ) {

                int v = clz;
                if(kvs != null) {
                    v = kvs.get(d.getName());
                }

                int finalV = v;
                final int[] newV = {finalV};
                Arrays.stream(d.listFiles()).forEach(f -> {
                    if(f.isFile()) {
                        DocModel doc = new DocModel();
                        doc.setCategoryId(finalV);
                        doc.setName(f.getName());
                        docRepository.save(doc);
                    } else {
                        //最多，支持2级
                        Arrays.stream(f.listFiles()).forEach(file -> {
                            DocModel doc = new DocModel();
                            doc.setCategoryId(newV[0]);
                            doc.setName(file.getName());
                            docRepository.save(doc);
                        });

                        newV[0]++;


                    }

                });
            } else {
                DocModel doc = new DocModel();
                doc.setCategoryId(clz);
                doc.setName(d.getName());
                docRepository.save(doc);

            }
        });
    }


    //第一种模式
    @PostMapping("/put2DB")
    public void putAll2DB(@RequestBody List<HtmlModel> htmlModels) {
        htmlModels.forEach(m -> {
            //insert2DB(m);
        });
    }

//    private void insert2DB(HtmlModel m) {
//        int area = -1;
//        int subject = -1;
//        int grade = -1;
//
//        if(m.getFilePathFolder().indexOf("全国") != -1) {
//            area = 100;
//        }
//        if(m.getFilePathFolder().indexOf("上海") != -1) {
//            area = 1;
//        }
//        if(m.getFilePathFolder().indexOf("北京") != -1) {
//            area = 2;
//        }
//        if(m.getFilePathFolder().indexOf("深圳") != -1) {
//            area = 3;
//        }
//
//        if(m.getFilePathFolder().indexOf("中考") != -1) {
//            grade = 9;
//        }
//        if(m.getFilePathFolder().indexOf("高考") != -1) {
//            grade = 12;
//        }
//
//        if(m.getFilePathFolder().indexOf("语文") != -1) {
//            subject = 1;
//        }
//        if(m.getFilePathFolder().indexOf("数学") != -1) {
//            subject = 2;
//        }
//        if(m.getFilePathFolder().indexOf("英语") != -1) {
//            subject = 3;
//        }
//        if(m.getFilePathFolder().indexOf("物理") != -1) {
//            subject = 4;
//        }
//        if(m.getFilePathFolder().indexOf("化学") != -1) {
//            subject = 5;
//        }
//        if(m.getFilePathFolder().indexOf("生物") != -1) {
//            subject = 6;
//        }
//        if(m.getFilePathFolder().indexOf("地理") != -1) {
//            subject = 7;
//        }
//        if(m.getFilePathFolder().indexOf("历史") != -1) {
//            subject = 8;
//        }
//        if(m.getFilePathFolder().indexOf("政治") != -1) {
//            subject = 9;
//        }
//
//        File dir =  new File(m.getFilePathFolder());
//
//        int finalGrade = grade;
//        int finalSubject = subject;
//        int finalArea = area;
//
//        Arrays.stream(dir.listFiles()).forEach(f -> {
//            if(f.isFile() ) {
//                DocModel doc = new DocModel();
//                doc.setGrade(finalGrade);
//                doc.setSubject(finalSubject);
//                doc.setArea(finalArea);
//                doc.setName(f.getName());
//                docRepository.save(doc);
//            }
//        });
//    }



    //第一种模式
    @PostMapping("/genAllHtml")
    public void genAllHtml(@RequestBody RequestGenHtml myRequest) {
        genHtml(myRequest.getClz(), myRequest.getStep(), myRequest.getParentFolder(), myRequest.getStorageParentFolder(), myRequest.getExtraSub(), myRequest.getModels());
    }


    String mTagHeader = "<div id=\"tagHeader\" class=\"container\">\n" +
            "<div>\n" +
            "<ul>\n" +
            "<li class=\"tbo_sh\"><a href=\"shmyw.html\">上海初中语文</a></li>\n" +
            "<li class=\"tbo_sh\"><a href=\"shmmath.html\">上海初中数学</a></li>\n" +
            "<li class=\"tbo_sh\"><a href=\"shmeng.html\">上海初中英语</a></li>\n" +
            "<li class=\"tbo_sh\"><a href=\"shmphy.html\">上海初中物理</a></li>\n" +
            "<li class=\"tbo_sh\"><a href=\"shmche.html\">上海初中化学</a></li>\n" +
            "<li class=\"tbo_bj\"><a href=\"bjmyw.html\">北京初中语文</a></li>\n" +
            "<li class=\"tbo_bj\"><a href=\"bjmmath.html\">北京初中数学</a></li>\n" +
            "<li class=\"tbo_bj\"><a href=\"bjmeng.html\">北京初中英语</a></li>\n" +
            "<li class=\"tbo_bj\"><a href=\"bjmphy.html\">北京初中物理</a></li>\n" +
            "<li class=\"tbo_bj\"><a href=\"bjmche.html\">北京初中化学</a></li>\n" +
            "<li class=\"tbo_sz\"><a href=\"szmyw.html\">深圳初中语文</a></li>\n" +
            "<li class=\"tbo_sz\"><a href=\"szmmath.html\">深圳初中数学</a></li>\n" +
            "<li class=\"tbo_sz\"><a href=\"szmeng.html\">深圳初中英语</a></li>\n" +
            "<li class=\"tbo_sz\"><a href=\"szmphy.html\">深圳初中物理</a></li>\n" +
            "<li class=\"tbo_sz\"><a href=\"szmche.html\">深圳初中化学</a></li>\n" +
            "<li class=\"tbo_nj\"><a href=\"njmyw.html\">南京初中语文</a></li>\n" +
            "<li class=\"tbo_nj\"><a href=\"njmmath.html\">南京初中数学</a></li>\n" +
            "<li class=\"tbo_nj\"><a href=\"njmeng.html\">南京初中英语</a></li>\n" +
            "<li class=\"tbo_nj\"><a href=\"njmphy.html\">南京初中物理</a></li>\n" +
            "<li class=\"tbo_nj\"><a href=\"njmche.html\">南京初中化学</a></li>\n" +
            "<li class=\"tbo_suz\"><a href=\"suzmyw.html\">苏州初中语文</a></li>\n" +
            "<li class=\"tbo_suz\"><a href=\"suzmmath.html\">苏州初中数学</a></li>\n" +
            "<li class=\"tbo_suz\"><a href=\"suzmeng.html\">苏州初中英语</a></li>\n" +
            "<li class=\"tbo_suz\"><a href=\"suzmphy.html\">苏州初中物理</a></li>\n" +
            "<li class=\"tbo_suz\"><a href=\"suzmche.html\">苏州初中化学</a></li>\n" +
            "<li class=\"tbo_gz\"><a ref=\"gzmyw.html\">广州初中语文</a></li>\n" +
            "<li class=\"tbo_gz\"><a ref=\"gzmmath.html\">广州初中数学</a></li>\n" +
            "<li class=\"tbo_gz\"><a ref=\"gzmeng.html\">广州初中英语</a></li>\n" +
            "<li class=\"tbo_gz\"><a ref=\"gzmphy.html\">广州初中物理</a></li>\n" +
            "<li class=\"tbo_gz\"><a ref=\"gzmche.html\">广州初中化学</a></li>\n" +
            "<li class=\"tbo_cd\"><a ref=\"cdmyw.html\">成都初中语文</a></li>\n" +
            "<li class=\"tbo_cd\"><a ref=\"cdmmath.html\">成都初中数学</a></li>\n" +
            "<li class=\"tbo_cd\"><a ref=\"cdmeng.html\">成都初中英语</a></li>\n" +
            "<li class=\"tbo_cd\"><a ref=\"cdmphy.html\">成都初中物理</a></li>\n" +
            "<li class=\"tbo_cd\"><a ref=\"cdmche.html\">成都初中化学</a></li>\n" +
            "<li class=\"tbo_cq\"><a ref=\"cqmyw.html\">重庆初中语文</a></li>\n" +
            "<li class=\"tbo_cq\"><a ref=\"cqmmath.html\">重庆初中数学</a></li>\n" +
            "<li class=\"tbo_cq\"><a ref=\"cqmeng.html\">重庆初中英语</a></li>\n" +
            "<li class=\"tbo_cq\"><a ref=\"cqmphy.html\">重庆初中物理</a></li>\n" +
            "<li class=\"tbo_cq\"><a ref=\"cqmche.html\">重庆初中化学</a></li>\t\n" +
            "<li class=\"tbo_hz\"><a ref=\"hzmyw.html\">杭州初中语文</a></li>\n" +
            "<li class=\"tbo_hz\"><a ref=\"hzmmath.html\">杭州初中数学</a></li>\n" +
            "<li class=\"tbo_hz\"><a ref=\"hzmeng.html\">杭州初中英语</a></li>\n" +
            "<li class=\"tbo_hz\"><a ref=\"hzmphy.html\">杭州初中物理</a></li>\n" +
            "<li class=\"tbo_hz\"><a ref=\"hzmche.html\">杭州初中化学</a></li>\t\n" +
            "<li class=\"tbo_xa\"><a ref=\"xamyw.html\">西安初中语文</a></li>\n" +
            "<li class=\"tbo_xa\"><a ref=\"xammath.html\">西安初中数学</a></li>\n" +
            "<li class=\"tbo_xa\"><a ref=\"xameng.html\">西安初中英语</a></li>\n" +
            "<li class=\"tbo_xa\"><a ref=\"xamphy.html\">西安初中物理</a></li>\n" +
            "<li class=\"tbo_xa\"><a ref=\"xamche.html\">西安初中化学</a></li>\t\n" +
            "<li class=\"tbo_wh\"><a ref=\"whmyw.html\">武汉初中语文</a></li>\n" +
            "<li class=\"tbo_wh\"><a ref=\"whmmath.html\">武汉初中数学</a></li>\n" +
            "<li class=\"tbo_wh\"><a ref=\"whmeng.html\">武汉初中英语</a></li>\n" +
            "<li class=\"tbo_wh\"><a ref=\"whmphy.html\">武汉初中物理</a></li>\n" +
            "<li class=\"tbo_wh\"><a ref=\"whmche.html\">武汉初中化学</a></li>\t\n" +
            "<li class=\"tbo_zj\"><a ref=\"zjmyw.html\">郑州初中语文</a></li>\n" +
            "<li class=\"tbo_zj\"><a ref=\"zjmmath.html\">郑州初中数学</a></li>\n" +
            "<li class=\"tbo_zj\"><a ref=\"zjmeng.html\">郑州初中英语</a></li>\n" +
            "<li class=\"tbo_zj\"><a ref=\"zjmphy.html\">郑州初中物理</a></li>\n" +
            "<li class=\"tbo_zj\"><a ref=\"zjmche.html\">郑州初中化学</a></li>\t\n" +
            "<li class=\"tbo_tj\"><a ref=\"tjmyw.html\">天津初中语文</a></li>\n" +
            "<li class=\"tbo_tj\"><a ref=\"tjmmath.html\">天津初中数学</a></li>\n" +
            "<li class=\"tbo_tj\"><a ref=\"tjmeng.html\">天津初中英语</a></li>\n" +
            "<li class=\"tbo_tj\"><a ref=\"tjmphy.html\">天津初中物理</a></li>\n" +
            "<li class=\"tbo_tj\"><a ref=\"tjmche.html\">天津初中化学</a></li>\t\n" +
            "<li class=\"tbo_all\"><a href=\"allmyw.html\">全国初中语文</a></li>\n" +
            "<li class=\"tbo_all\"><a href=\"allmmath.html\">全国初中数学</a></li>\n" +
            "<li class=\"tbo_all\"><a href=\"allmeng.html\">全国初中英语</a></li>\n" +
            "<li class=\"tbo_all\"><a href=\"allmphy.html\">全国初中物理</a></li>\n" +
            "<li class=\"tbo_all\"><a href=\"allmche.html\">全国初中化学</a></li>\n" +
            "</ul>\n" +
            "</div>\n" +
            "</div>\n" +
            "<div style=\"clear:both\"></div>";

    String hTagHeader = "<div id=\"tagHeader\" class=\"container\">\n" +
            "<div>\n" +
            "<ul>\n" +
            "<li class=\"tbo_sh\"><a href=\"shhyw.html\">上海高中语文</a></li>\n" +
            "<li class=\"tbo_sh\"><a href=\"shhmath.html\">上海高中数学</a></li>\n" +
            "<li class=\"tbo_sh\"><a href=\"shheng.html\">上海高中英语</a></li>\n" +
            "<li class=\"tbo_sh\"><a href=\"shhphy.html\">上海高中物理</a></li>\n" +
            "<li class=\"tbo_sh\"><a href=\"shhche.html\">上海高中化学</a></li>\n" +
            "<li class=\"tbo_bj\"><a href=\"bjhyw.html\">北京高中语文</a></li>\n" +
            "<li class=\"tbo_bj\"><a href=\"bjhmath.html\">北京高中数学</a></li>\n" +
            "<li class=\"tbo_bj\"><a href=\"bjheng.html\">北京高中英语</a></li>\n" +
            "<li class=\"tbo_bj\"><a href=\"bjhphy.html\">北京高中物理</a></li>\n" +
            "<li class=\"tbo_bj\"><a href=\"bjhche.html\">北京高中化学</a></li>\n" +

            "<li class=\"tbo_all\"><a href=\"allhyw.html\">全国高中语文</a></li>\n" +
            "<li class=\"tbo_all\"><a href=\"allhmath.html\">全国高中数学</a></li>\n" +
            "<li class=\"tbo_all\"><a href=\"allheng.html\">全国高中英语</a></li>\n" +
            "<li class=\"tbo_all\"><a href=\"allhphy.html\">全国高中物理</a></li>\n" +
            "<li class=\"tbo_all\"><a href=\"allhche.html\">全国高中化学</a></li>\n" +
            "</ul>\n" +
            "</div>" +
            "</div>\n" +
            "<div style=\"clear:both\"></div>";

    String yTagHeader = "<div id=\"tagHeader\" class=\"container\">\n" +
            "<div>\n" +
            "\t\t\t<ul>\n" +
            "\t\t\t\t<li class=\"tbo01\"><a href=\"mika.html\">米卡成长天地</a></li>\n" +
            "\t\t\t\t<li class=\"tbo02\"><a href=\"tuxiaobei.html\">兔小贝成语故事</a></li>\n" +
            "\t\t\t</ul>\n" +
            "\t\t</div>"+
            "</div>\n" +
            "<div style=\"clear:both\"></div>";

    String carrTagHeader = "<div id=\"tagHeader\" class=\"container\">\n" +
            "<div>\n" +
            "\t\t\t<ul>\n" +
            "\t\t\t\t<li class=\"tbo01\"><a href=\"carr_more.html\">带你5分钟搞定面试官</a></li>\n" +
            "\t\t\t\t<li class=\"tbo02\"><a href=\"from0.html\">从零到年薪百万职业规划课</a></li>\n" +
            "\t\t\t\t<li class=\"tbo01\"><a href=\"career_plan.html\">职业生涯规划</a></li>\n" +
            "\t\t\t\t<li class=\"tbo02\"><a href=\"career_action.html\">人在职场</a></li>\n" +
            "\t\t\t</ul>\n" +
            "\t\t</div>"+
            "</div>\n" +
            "<div style=\"clear:both\"></div>";

    String hrTagHeader = "<div id=\"tagHeader\" class=\"container\">\n" +
            "<div>\n" +
            "\t\t\t<ul>\n" +
            "\t\t\t\t<li class=\"tbo01\"><a href=\"hr.html\">00.各大模块流程图</a></li>\n" +
            "\t\t\t\t<li class=\"tbo02\"><a href=\"hr_hire.html\">01.最强招聘十大常用表格精编宝典</a></li>\n" +
            "\t\t\t\t<li class=\"tbo01\"><a href=\"hr_hire2.html\">02.招聘面试技巧PPT工具包</a></li>\n" +
            "\t\t\t\t<li class=\"tbo02\"><a href=\"hr_new.html\">03.最新员工入职培训全套实用资料</a></li>\n" +
            "\t\t\t\t<li class=\"tbo01\"><a href=\"hr_try.html\">04.试用期管理全攻略</a></li>\n" +
            "\t\t\t\t<li class=\"tbo02\"><a href=\"hr_contract.html\">05.劳动合同必备模板</a></li>\n" +
            "\t\t\t\t<li class=\"tbo01\"><a href=\"hr_perform.html\">06.绩效工具包</a></li>\n" +
            "\t\t\t\t<li class=\"tbo02\"><a href=\"hr_empl.html\">07.人员结构分析资料包</a></li>\n" +
            "\t\t\t\t<li class=\"tbo01\"><a href=\"hr_empl2.html\">08.员工胜任力模型与任职资格资料包</a></li>\n" +
            "\t\t\t\t<li class=\"tbo02\"><a href=\"hr_quit.html\">09.解除劳动合同通知书</a></li>\n" +
            "\t\t\t\t<li class=\"tbo01\"><a href=\"hr_check.html\">10.成本核算系统</a></li>\n" +
            "\t\t\t\t<li class=\"tbo02\"><a href=\"hr_evl.html\">11.岗位价值评估表</a></li>\n" +
            "\t\t\t\t<li class=\"tbo01\"><a href=\"hr_doc.html\">12.员工档案管理表</a></li>\n" +
            "\t\t\t\t<li class=\"tbo02\"><a href=\"hr_sal.html\">13.公司职位等级薪酬表</a></li>\n" +
            "\t\t\t</ul>\n" +
            "\t\t</div>"+
            "</div>\n" +
            "<div style=\"clear:both\"></div>";

    String kaoyanTagHeader = "<div id=\"tagHeader\" class=\"container\">\n" +
            "<div>\n" +
            "\t\t\t<ul>\n" +
            "\t\t\t\t<li class=\"tbo01\"><a href=\"kaoyan.html\">考研数学</a></li>\n" +
            "\t\t\t\t<li class=\"tbo02\"><a href=\"kaoyan_eng.html\">考研英语</a></li>\n" +
            "\t\t\t\t<li class=\"tbo01\"><a href=\"kaoyan_zh.html\">综合</a></li>\n" +
            "\t\t\t</ul>\n" +
            "\t\t</div>"+
            "</div>\n" +
            "<div style=\"clear:both\"></div>";

    String template_list = "<div class=\"row\" LTag=\"L%s\">\n" +
            "  <div class=\"text-muted mb-2\">\n" +
            "\t<i class=\"fas fa-calendar-alt small me-1\"></i><small class=\"me-3\">%s</small>\n" +
            "\t<i class=\"fas fa-download text-primary me-1\"></i><small id=\"id_%s_down\" class=\"me-3\">0</small>\n" +
            "\t<i class=\"fas fa-heart text-danger small fw-bold me-1\"></i><small id=\"id_%s_loved\">0</small>\n" +
            "  </div>\n" +
            "  <div class=\"row mb-5\">\n" +
            "\t<a href=\"%s\" target=\"_blank\"><h4>%s</h4></a>\n" +
            "  </div>\n" +
            "</div>";

    private void makeOne(File file, StringBuilder sbContent, List<String> docIds, String storeSubFolder, String storagePath) {
        long docId = -1;
        String desc = null;
        String rep = null;
        int categoryId = -1;
        List<DocModel> docs = docRepository.findByName(file.getName());
        if(docs.size() > 0) {
            desc = docs.get(0).getShortDesc();
            docId = docs.get(0).getId();
            categoryId = docs.get(0).getCategoryId();
            docIds.add( String.valueOf(docId));
            rep = docs.get(0).getReplaceUrl();
        } else {
            log.info("######### Not found in db: " + file.getName());
        }

        //1,gen detail html
        String detailHtml = genDetail(categoryId, rep, docId,storagePath + "/" + storeSubFolder, file.getName(), desc);

        //2,保存href
        if(docs.size() > 0) {
            docs.get(0).setHref(detailHtml);
            docRepository.save(docs.get(0));
        }

        //3, append one to list
        String curr = String.format(template_list, categoryId, sdf.format(new Date()), docId, docId, detailHtml, file.getName());
        sbContent.append(curr);
    }




    private static double returnDouble(String str) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            if (Character.isDigit(str.charAt(i)))
                sb.append(str.charAt(i));
            else if (str.charAt(i) == '.' && i < str.length() - 1 && Character.isDigit(str.charAt(i + 1)))
                sb.append(str.charAt(i));
            else break;
        }

        if (sb.toString().isEmpty())
            return 0;
        else
            return Double.parseDouble(sb.toString());
    }


    private void genHtml(int clz, int step, String parentFolder, String storagePath, String extraSub, List<HtmlModel> models) {
        AtomicReference<StringBuilder> sbContent = new AtomicReference<>(new StringBuilder());
        AtomicReference<List<String>> docIds = new AtomicReference<>();
        //int category = -1;

        populateKvs(new File(parentFolder), clz, step);

        AtomicInteger idx = new AtomicInteger();

        File[] files = new File(parentFolder).listFiles();
        List<File> fileList = Arrays.asList(files);

        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if (returnDouble(o1.getName()) < returnDouble(o2.getName()))
                    return -1;
                else if (returnDouble(o1.getName()) > returnDouble(o2.getName()))
                    return 1;
                else
                    return 0;
            }
        });

        //kvs-大类。每一个大类，生成一个html。每个html内部，可筛选。每个html内部，header可链接到其它.
        fileList.forEach(fld -> {

            File dir =  new File(parentFolder + "\\" + fld.getName());

            AtomicReference<LinkedHashMap<String, Integer>> subKvs = new AtomicReference<>();
            //AtomicInteger sx = new AtomicInteger();
            sbContent.set(new StringBuilder());
            docIds.set(new ArrayList<>());

            subKvs.set(new LinkedHashMap<>());
            final int[] categoryId = {kvs.get(fld.getName())};

            Arrays.stream(dir.listFiles()).forEach(file -> {

                if(file.isFile()) {    //1级
                    makeOne(file, sbContent.get(), docIds.get(),fld.getName(),storagePath);
                } else {    //2级

                    //sx.set(1);

                    subKvs.get().put(file.getName(), categoryId[0]);
                    categoryId[0]++;

                    Arrays.stream(file.listFiles()).forEach(file2 -> {
                        makeOne(file2, sbContent.get(), docIds.get(),fld.getName()+"/"+file2.getParentFile().getName(),storagePath);
                    });

                }
            });

            int sx = 0;
            if(subKvs.get().size() > 1) {
                sx = 1;
            }

            //int switchSubId, String switchSubText, int shaixuan, String extraSub, String tip, String htmlName, String metaTitle, String h1Title
            HtmlModel model = models.get(idx.get());
            //Now gen html
            genOneHtml(subKvs.get(), clz, model.getShaixuan() == 0 ? sx : model.getShaixuan(),
                    model.getSwitchSubId(),model.getSwitchSubText(),extraSub,model.getHtmlName(),model.getTip(),
                    model.getMetaTitle() == null? new File(parentFolder).getName() + "-" +  fld.getName() : model.getMetaTitle(),
                    model.getH1Title() == null? fld.getName():model.getH1Title(),
                    sbContent.get(), docIds.get());

            idx.getAndIncrement();
        });


    }

    private void genOneHtml(LinkedHashMap<String, Integer> subKvs, int clz, int shaixuan, int switchSubId, String switchSubText, String extraSub, String htmlName, String tip, String metaTitle, String h1Title, StringBuilder sbContent, List<String> docIds) {

        //构建内容 subNav
        StringBuilder sbSubNav = new StringBuilder();

//        if(clz == 1 || clz == 11) {
//            sbSubNav.append("<div class=\"flex-v-center mt-4 mb-5\">");
//            kvs.forEach((k,v) -> {
//                String nav = String.format("<div class=\"form-check form-switch me-4\">\n" +
//                        "\t  <input class=\"form-check-input\" type=\"checkbox\" role=\"switch\" id=\"id%s\" checked onclick=\"switchcategory(this,'L%s')\"/>" +
//                        "\t  <label class=\"form-check-label\" for=\"id%s\">%s</label>\t\t  \n" +
//                        "\t</div>", v, v, v, k);
//                sbSubNav.append(nav);
//            });
//            sbSubNav.append("</div>");
//        }


        if(shaixuan == 1 && subKvs != null && !subKvs.isEmpty()) {
            sbSubNav.append("<div class=\"dropdown mb-5\">\n" +
                    "  <a class=\"btn btn-primary dropdown-toggle href=\"#\" role=\"button\" id=\"lTagDropdownLink\" data-mdb-toggle=\"dropdown\" aria-expanded=\"false\" >\n" +
                    "\t刷选一下\n" +
                    "  </a>\n" +
                    "\n" +
                    "  <ul class=\"dropdown-menu\" aria-labelledby=\"lTagDropdownLink\">");

            subKvs.forEach((k,v) -> {
                String nav = String.format("<li><a class=\"dropdown-item\" href=\"#\" onclick=\"switchlist('L%s','%s')\">%s</a></li>",
                        v, k, k);
                sbSubNav.append(nav);


            });
            sbSubNav.append("</ul>\n" +
                    "</div>");

            if(switchSubId > 0) {
                sbSubNav.append(String.format("<input id=\"switchId\" type=\"hidden\" value=\"%s\" />", switchSubId+"##"+switchSubText));
            } else {
                sbSubNav.append(String.format("<input id=\"switchId\" type=\"hidden\" value=\"%s\" />",
                        subKvs.entrySet().iterator().next().getValue()+"##"+subKvs.entrySet().iterator().next().getKey()));
            }
        }


        //构建内容 tagHeader
        String tagHeader = "";
        if(extraSub != null && extraSub.equals("m")) {  //初中
            tagHeader = mTagHeader.replace("href=\"" + htmlName + "\"",
                    "style=\"background-color:#111;color:#EEE\" " );
        }

        if(extraSub != null && extraSub.equals("h")) {  //高中
            tagHeader = hTagHeader.replace("href=\"" + htmlName + "\"",
                    "style=\"background-color:#111;color:#EEE\" " );
        }


//        if(clz == 300 || clz == 310) {
//            tagHeader = yTagHeader.replace("href=\"" + htmlName + "\"",
//                    "style=\"background-color:#FF8F00;color:#EEE\" " );
//        }
//        if(clz == 2000) {
//            tagHeader = carrTagHeader.replace("href=\"" + htmlName + "\"",
//                    "style=\"background-color:#111;color:#EEE\" " );
//        }

        if(clz == 2600) {
            tagHeader = hrTagHeader.replace("href=\"" + htmlName + "\"",
                    "style=\"background-color:#111;color:#EEE\" " );
        }

        if(clz == 1400) {
            tagHeader = kaoyanTagHeader.replace("href=\"" + htmlName + "\"",
                    "style=\"background-color:#111;color:#EEE\" " );
        }

        String great_tip = "";
        if(tip != null) {
            great_tip = tip;
        }

        String read;
        String read2;
        try{
            FileReader fileread = new FileReader("H:\\apache-tomcat-8.5.81\\webapps\\ROOT\\main_template.html");
            BufferedReader bufread = new BufferedReader(fileread);
            FileWriter newFile =new FileWriter("H:\\apache-tomcat-8.5.81\\webapps\\ROOT\\" + htmlName );
            BufferedWriter bw = new BufferedWriter(newFile);
            while ((read = bufread.readLine()) != null)
            {
                read2 = read.replace("##content##",sbContent.toString());
                read2 = read2.replace("##sub_nav##",sbSubNav.toString());
                read2 = read2.replace("##meta_title##",metaTitle);
                read2 = read2.replace("##h1_titile##",h1Title);
                read2 = read2.replace("##docIds##", String.join(",",  docIds));

                read2 = read2.replace("##great_tip##",great_tip);

                //设置当前页面的标签背景色
                read2 = read2.replace("##tag_header##",tagHeader);

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

    public static String getlinkNo() {
        String linkNo = "";
        // 用字符数组的方式随机
        String model = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        char[] m = model.toCharArray();
        for (int j = 0; j < 10; j++) {
            char c = m[(int) (Math.random() * 62)];
            // 保证六位随机数之间没有重复的
            if (linkNo.contains(String.valueOf(c))) {
                j--;
                continue;
            }
            linkNo = linkNo + c;
        }
        return linkNo;
    }

    private String genDetail(int categoryId, String rep, long docId, String storagePath, String fName, String desc) {

        String detailHtmlName = "detail-" + categoryId + "-" + docId + ".html";

        String read;
        String read2;
        try{
            FileReader fileread = new FileReader("H:\\apache-tomcat-8.5.81\\webapps\\ROOT\\detail_template.html");
            BufferedReader bufread = new BufferedReader(fileread);
            FileWriter newFile =new FileWriter("H:\\apache-tomcat-8.5.81\\webapps\\ROOT\\" + detailHtmlName );
            BufferedWriter bw = new BufferedWriter(newFile);
            while ((read = bufread.readLine()) != null)
            {
                read2 = read.replace("##filename##",fName);
                if(rep != null) {
                    read2 = read2.replace("##detail_file_name##",rep.split("@@")[1]);
                } else {
                    read2 = read2.replace("##detail_file_name##",storagePath + "/" + fName);
                }

                if(desc != null) {
                    read2 = read2.replace("##pre_text##",desc);
                } else {
                    read2 = read2.replace("##pre_text##","");
                }
                read2 = read2.replace("##detail_id##",String.valueOf(docId));


                if ( rep != null ) {
                    read2 = read2.replace("##doc_src##", rep.split("@@")[0]);
                } else {
                    read2 = read2.replace("##doc_src##", storagePath + "/" + fName);
                }

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
        return detailHtmlName;
    }


    private File findFile(String fName, String pFolder) {
        File folder = new File(pFolder);
        File[] files1 = folder.listFiles();
        for(int i = 0; i <= files1.length - 1; i++) {
            if(files1[i].isFile() && files1[i].getName().equals(fName)) {
                return files1[i];
            }

            if(files1[i].isDirectory()) {
                File[] files = files1[i].listFiles( f-> f.isFile() && f.getName().equals(fName));
                if(files.length > 0) {
                    return files[0];
                }

            }
        }


        for(int i = 0; i <= files1.length - 1; i++) {
            if(files1[i].isDirectory()) {
                File[] files = files1[i].listFiles( f-> f.isDirectory() );  //文件夹

                for(int j = 0; j <= files.length - 1; j++) {
                    File[] subFiles = files[j].listFiles(fi -> fi.isFile() && fi.getName().equals(fName));
                    if(subFiles.length > 0) {
                        return subFiles[0];
                    }
                }
            }

        }


        return null;
    }

    private String getTextFromPdf(File file) {
        PDDocument document = null;
        String text = null;
        try {
            document = PDDocument.load(file);
            PDFTextStripper pdfStripper = new PDFTextStripper();
            text = pdfStripper.getText(document);
            if(text.length() >= 800) {
                text = text.substring(0, 800);
            }

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(document != null) {
                try {
                    document.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return text;
    }

    private String getTextFromDocx(File file) {
        String text = null;
        try {

            FileInputStream fis = new FileInputStream(file.getAbsolutePath());
            XWPFDocument xwpfDocument = new XWPFDocument(fis);
            fis.close();

            List<XWPFParagraph> xwpfParas = xwpfDocument.getParagraphs();
            StringBuilder sbText = new StringBuilder();
            for(int i=0;i<xwpfParas.size();i++) {
                if(sbText.length() <= 800) {
                    sbText.append(xwpfParas.get(i).getText());
                }
            }

            text = sbText.toString();
            if(text.length() >= 800) {
                text = text.substring(0,800);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            log.warn("!!!!!!!!!!!!!!!!" + file.getName());
        }
        return text;
    }

    private String getTextFromExcel(File file)  {
        StringBuilder sbText = new StringBuilder();
        String text = null;
        log.warn("#######Starting........" + file.getName());
        try {
            FileInputStream fis = new FileInputStream(file.getAbsolutePath());
            XSSFWorkbook wb = new XSSFWorkbook(fis);
            fis.close();

            int i = 0;

            XSSFSheet sheet = wb.getSheetAt(0);
            for(Row row : sheet) {
                for(Cell cell : row) {
                    try {
                        sbText.append(cell.getCellType() == CellType.NUMERIC ? cell.getNumericCellValue() :  cell.getStringCellValue());
                        i++;
                        if(i>10) {
                            break;
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        log.info("One cell passed.");
                    }
                }
                if(i>10) {
                    break;
                }
            }

            text = sbText.toString();
            if(text.length() >= 800) {
                text = text.substring(0,800);
            }


        } catch(Exception ex) {
            ex.printStackTrace();
            log.warn("!!!!!!!!!!!!!!!!" + file.getName());
        }
        return text;
    }

    private String getTextFromDoc(File file)  {
        String strText = null;
        try {
            FileInputStream fis = new FileInputStream(file.getAbsolutePath());
            HWPFDocument hwpfDocument = new HWPFDocument(fis);
            fis.close();

            strText = hwpfDocument.getText().toString();
            if(strText.length() >= 800) {
                strText = strText.substring(0,800);
            }


        } catch(Exception ex) {
            ex.printStackTrace();
            log.warn("!!!!!!!!!!!!!!!!" + file.getName());
        }
        return strText;
    }

    private void updateOne(File file) {
        if(file.isDirectory()) {
            Arrays.stream(file.listFiles()).forEach(f -> {updateOne(f);});
        } else {
            //

            List<DocModel> docs = docRepository.findByName(file.getName());
            if( docs.size() > 0 && docs.get(0).getShortDesc() == null) {
                DocModel doc = docs.get(0);

                String text = null;

                if(doc.getName().endsWith(".pdf")) {
                    text = getTextFromPdf(file);
                    doc.setShortDesc(text);
                } else {
                    try {
                        if(doc.getName().endsWith(".doc")) {
                            text = getTextFromDoc(file);
                        }
                        if(doc.getName().endsWith(".docx")) {
                            text = getTextFromDocx(file);
                        }
                        if(doc.getName().endsWith(".xlsx")) {
                            text = getTextFromExcel(file);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    doc.setShortDesc(text);
                }
                try {
                    docRepository.save(doc);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }

        }
    }

    @PostMapping("/updateDocDesc")
    public void updateDocDesc(String parentFolder) {
        File folder = new File(parentFolder);

        try {
            updateOne(folder);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


    public static void main(String[] args) {
        File[] files = new File("I:\\考研").listFiles();
        List<File> fileList = Arrays.asList(files);

        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if (returnDouble(o1.getName()) < returnDouble(o2.getName()))
                    return -1;
                else if (returnDouble(o1.getName()) > returnDouble(o2.getName()))
                    return 1;
                else
                    return 0;
            }
        });
        fileList.forEach(f-> {System.out.println(f.getName());});
    }


}
