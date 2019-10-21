package com.cloudwise.project.controller;

import com.cloudwise.project.service.FileInfoService;
import com.cloudwise.project.vo.ResultData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/cloudwise")
@SuppressWarnings("all")
public class FileInfoController {
    @Autowired
    private FileInfoService fileInfoService;
    @RequestMapping("/upload")
    public ResultData  uploadFile(@RequestParam("file") MultipartFile file[], @RequestParam("uploader") String uploader) throws IOException {
        ResultData uploadResult = fileInfoService.uploadFile(file, "uploader");
        return uploadResult;
    }

    @RequestMapping("/delete")
    public ResultData deleteFile(@RequestParam("name")String name,@RequestParam("type")String type){
        ResultData deleteResult = fileInfoService.deleteFile(name,type);
        return deleteResult;
    }

    @RequestMapping("/getAllfile")
    public ResultData getAllfile(){
        ResultData allfile = fileInfoService.getAllfile();
        return allfile;
    }

    @RequestMapping("/getPath")
    public ResultData getPath(@RequestParam("id")String id){
        ResultData path = fileInfoService.getPath(id);
        return path;
    }
}
