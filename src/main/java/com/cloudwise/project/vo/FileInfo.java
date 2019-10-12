package com.cloudwise.project.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 文档信息：文档id、文档名、文档上传路径、文档大小、文档时间、文档类型
 * @Author: Locas Hu
 * @Date: 2019/10/9
**/
@Data
@NoArgsConstructor
@ToString
public class FileInfo implements Serializable {
    private String id;
    private String uploader;
    private String name;
    private String path;
    private double size;
    private String time;
    private long count;
    private String type;
}
