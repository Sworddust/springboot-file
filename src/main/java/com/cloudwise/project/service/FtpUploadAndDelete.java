package com.cloudwise.project.service;

import com.cloudwise.project.conf.FtpConfig;
import com.cloudwise.project.util.FtpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class FtpUploadAndDelete {
    @Autowired
    private FtpConfig ftpConfig;

    public Map<String, Object> upload(MultipartFile file) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        // 1.获取文件的原始名称
        String FileName =file.getOriginalFilename();
        System.out.println(FileName);
        try {
            Boolean uploadResult = FtpUtil.uploadFile(ftpConfig, FileName, file.getInputStream());
            String path = ftpConfig.getFtpPath()+"/"+FileName;
            resultMap.put("result", uploadResult);
            resultMap.put("path", path);
        } catch (IOException e) {
            resultMap.put("result", false);
            e.printStackTrace();
        }
        return resultMap;
    }


    public Map<String, Object> delete(String filename){
        Map<String, Object> resultMap = new HashMap<String, Object>();
        Boolean deleteResult = FtpUtil.deleteFiles(ftpConfig, filename);
        resultMap.put("result", deleteResult);
        return resultMap;
    }

    public Map<String, Object> download(String path,String filename) throws IOException {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        boolean downResult = FtpUtil.downloadSingleFile(ftpConfig, path, filename);
        resultMap.put("result",downResult);
        return resultMap;
    }
}
