package com.xxl.mydoc.model;

import lombok.Data;

import java.util.List;

@Data
public class RequestGenHtml {
    String parentFolder;
    String storageParentFolder;

    private List<HtmlModel> models;
    private int clz;
    private int step;
    private String extraSub;
}
