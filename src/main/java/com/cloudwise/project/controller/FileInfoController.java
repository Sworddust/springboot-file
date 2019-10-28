package com.cloudwise.project.controller;

import com.cloudwise.project.service.FileInfoService;
import com.cloudwise.project.vo.ResultMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/cloudwise")
@SuppressWarnings("all")
public class FileInfoController {
    @Autowired
    private FileInfoService fileInfoService;

    @RequestMapping("/upload")
    public ResultMessage uploadFile(@RequestParam("file") MultipartFile file[]) throws IOException {
        ResultMessage uploadResult = fileInfoService.uploadFile(file, "uploader");
        return uploadResult;
    }

    @RequestMapping("/delete")
    public ResultMessage deleteFile(@RequestParam("name")String name,@RequestParam("type")String type){
        ResultMessage deleteResult = fileInfoService.deleteFile(name,type);
        return deleteResult;
    }

    @RequestMapping("/getAllfile")
    public ResultMessage getAllfile() {
        ResultMessage allfile = fileInfoService.getAllfile();
        return allfile;
    }

    @RequestMapping("/getPath")
    public ResultMessage getPath(@RequestParam("id") String id) {
        ResultMessage path = fileInfoService.getPath(id);
        return path;
    }
}
