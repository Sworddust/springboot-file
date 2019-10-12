package com.cloudwise.project.controller;

import com.cloudwise.project.service.FileInfoService;
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
    public Map<String,Object>  uploadFile(@RequestParam("file") MultipartFile file[]) throws IOException {
        Map<String, Object> uploadResult = fileInfoService.uploadFile(file, "hu");
        return uploadResult;
    }

    @RequestMapping("/delete")
    public Map<String,Object> deleteFile(@RequestParam("name")String name,@RequestParam("type")String type){
        Map<String, Object> deleteResult = fileInfoService.deleteFile(name,type);
        return deleteResult;
    }

    @RequestMapping("/getAllfile")
    public Map<String,Object> getAllfile(){
        Map<String, Object> allfile = fileInfoService.getAllfile();
        return allfile;
    }

    @RequestMapping("/getPath")
    public String getPath(@RequestParam("id")String id){
        String path = fileInfoService.getPath(id);
        return path;
    }
}
