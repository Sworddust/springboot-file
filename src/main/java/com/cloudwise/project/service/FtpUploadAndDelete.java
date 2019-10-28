package com.cloudwise.project.service;

import com.cloudwise.project.conf.SftpConfig;
import com.cloudwise.project.util.SftpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 文件上传，删除方法
 * @Author: Locas Hu
 * @Date: 2019/10/11
 **/
@Component
public class FtpUploadAndDelete {
    @Autowired
    private SftpConfig sftpConfig;

    public Map<String, Object> upload(MultipartFile file) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        // 1.获取文件的原始名称
        String FileName = file.getOriginalFilename();
        try {
            Boolean uploadResult = SftpUtil.upload(sftpConfig, FileName, file.getInputStream());
            String path = sftpConfig.getHttpPath() + "/" + FileName;
            resultMap.put("result", uploadResult);
            resultMap.put("path", path);
        } catch (IOException e) {
            resultMap.put("result", false);
            e.printStackTrace();
        }
        return resultMap;
    }

    public Map<String, Object> delete(String filename) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        Boolean deleteResult = SftpUtil.delete(sftpConfig, filename);
        resultMap.put("result", deleteResult);
        return resultMap;
    }
}
