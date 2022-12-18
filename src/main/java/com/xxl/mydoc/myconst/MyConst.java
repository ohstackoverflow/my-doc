package com.xxl.mydoc.myconst;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MyConst {

    public static String DOC_HOST = "http://www.go100.site/rs/";

    public static LinkedHashMap<String, Integer> dictHR = new LinkedHashMap<>();
    public static LinkedHashMap<String, Integer> dictHRPerTools = new LinkedHashMap<>();
    public static LinkedHashMap<String, Integer> cet4 = new LinkedHashMap<>();
    public static LinkedHashMap<String, Integer> cet6 = new LinkedHashMap<>();
    public static LinkedHashMap<String, Integer> mika = new LinkedHashMap<>();
    public static LinkedHashMap<String, Integer> gwy = new LinkedHashMap<>();
    public static LinkedHashMap<String, Integer> dictMTest = new LinkedHashMap<>();
    public static LinkedHashMap<String, Integer> dictHTest = new LinkedHashMap<>();

    public static HashMap<String, Integer> Subjects = new HashMap<>();

    public static HashMap<Integer,String> AnSubjects = new HashMap<>();

    static {
//        dictHR.put("人员计划申请表",101);
//        dictHR.put("面试通知",102);
//        dictHR.put("应聘登记表",103);
//        dictHR.put("初试 复试评价表&记录表",104);
//        dictHR.put("背景调查表格",105);
//        dictHR.put("录用类表格",106);
//        dictHR.put("试用期相关表格",107);
//        dictHR.put("转正表格",108);
//        dictHR.put("招聘管理",109);
//        dictHR.put("离职类表格",110);
//        dictHR.put("招聘效果分析",111);
//
//        dictHRPerTools.put("360度绩效考核",121);
//        dictHRPerTools.put("BSC平衡计分卡",122);
//        dictHRPerTools.put("关键事件法",123);
//        dictHRPerTools.put("目标管理法",124);
//        dictHRPerTools.put("排序法",125);
//        dictHRPerTools.put("强制正态分布法",126);
//        dictHRPerTools.put("相对评价法",127);
//        dictHRPerTools.put("行为锚定评分法",128);
//        dictHRPerTools.put("要素评价法",129);

        AtomicInteger i = new AtomicInteger(310);
//        File dir =  new File("I:\\【教育】米卡全套1-6岁《幼儿教育资源》\\【米卡成长天地】动画全集1-6岁");
//        Arrays.stream(dir.listFiles()).forEach(d -> {
//            if(d.isDirectory()) {
//                i.getAndIncrement();
//                mika.put(d.getName(),i.get());
//            }
//        });

        AtomicInteger j = new AtomicInteger(400);
//        dir =  new File("I:\\英语等级考试\\英语四级历年真题及答案解析");
//        Arrays.stream(dir.listFiles()).forEach(d -> {
//            if(d.isDirectory()) {
//                j.getAndIncrement();
//                cet4.put(d.getName(),j.get());
//            }
//        });

        AtomicInteger k = new AtomicInteger(420);
//        dir =  new File("I:\\英语等级考试\\英语六级历年真题及答案解析");
//        Arrays.stream(dir.listFiles()).forEach(d -> {
//            if(d.isDirectory()) {
//                k.getAndIncrement();
//                cet6.put(d.getName(),k.get());
//            }
//        });


        AtomicInteger m = new AtomicInteger(600);
//        dir =  new File("I:\\公务员题库 (必刷题)");
//        Arrays.stream(dir.listFiles()).forEach(d -> {
//            if(d.isDirectory()) {
//                m.getAndIncrement();
//                gwy.put(d.getName(),m.get());
//            }
//        });


        dictMTest.put("模拟试卷",1);
        dictMTest.put("中考试卷",2);
        dictMTest.put("专题复习汇总",3);

        dictHTest.put("模拟试卷",11);
        dictHTest.put("高考试卷",12);
        dictHTest.put("专题复习汇总",13);

//        Subjects.put("语文",1);
//        Subjects.put("数学",2);
//        Subjects.put("英语",3);
//        Subjects.put("物理",4);
//        Subjects.put("化学",5);
//        Subjects.put("生物",6);
//        Subjects.put("地理",7);
//        Subjects.put("历史",8);
//        Subjects.put("政治",9);
//
//        AnSubjects.put(1,"语文");
//        AnSubjects.put(2,"数学");
//        AnSubjects.put(3,"英语");
//        AnSubjects.put(4,"物理");
//        AnSubjects.put(5,"化学");
//        AnSubjects.put(6,"生物");
//        AnSubjects.put(7,"地理");
//        AnSubjects.put(8,"历史");
//        AnSubjects.put(9,"政治");


        System.out.println("Init const done.");
    }
}
