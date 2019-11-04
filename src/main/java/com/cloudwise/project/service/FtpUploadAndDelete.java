package com.cloudwise.project.service;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import com.cloudwise.project.conf.SftpConfig;
import com.cloudwise.project.util.SftpUtil;
import com.jcraft.jsch.SftpException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * @Description: 文件上传，删除方法
 * @Author: Locas Hu
 * @Date: 2019/10/11
 **/
@Slf4j
@Component
public class FtpUploadAndDelete {
    @Autowired
    private SftpConfig sftpConfig;

    public Map<String, Object> upload(MultipartFile file) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        String FileName = file.getOriginalFilename();
        try {
            SftpUtil sftpUtil = new SftpUtil(sftpConfig);
            sftpUtil.login();
            Boolean uploadResult = sftpUtil.uploadfile(FileName, file.getInputStream());
            if (uploadResult) {
                String path = sftpConfig.getHttpPath() + "/" + FileName;
                resultMap.put("result", uploadResult);
                resultMap.put("path", path);
            } else {
                resultMap.put("result", false);
            }
        } catch (IOException e) {
            log.error("IO出现异常" + e, e);
        }
        return resultMap;
    }

    public Map<String, Object> delete(String filename) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        SftpUtil sftpUtil = new SftpUtil(sftpConfig);
        sftpUtil.login();
        Boolean deleteResult = sftpUtil.delete(filename);
        resultMap.put("result", deleteResult);
        return resultMap;
    }


    public Map<String, Object> downloadfile(String filename) throws IOException, SftpException {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        SftpUtil sftpUtil = new SftpUtil(sftpConfig);
        sftpUtil.login();
        String homedirectory = System.getProperty("user.home")+"/";
        Boolean download = sftpUtil.download(filename, homedirectory);
        // 取得文件的后缀名。
        //String ext = filename.substring(filename.lastIndexOf(".") + 1).toUpperCase();
        // 以流的形式下载文件。
        InputStream instream = new BufferedInputStream(new FileInputStream(homedirectory+"/"+filename));
        resultMap.put("file",instream);
        resultMap.put("result",true);
        return resultMap;
    }


//    public Map<String, Object> download(String filename,String saveDirectory) throws IOException, SftpException {
//        Map<String, Object> resultMap = new HashMap<String, Object>();
//        SftpUtil sftpUtil = new SftpUtil(sftpConfig);
//        sftpUtil.login();
//        String homedirectory = System.getProperty("user.home");
//        //保存路径为空则为预览
//        if (saveDirectory.equals("")||saveDirectory==null) {
//            //判断目录是否存在
//            File localDirectory = new File(homedirectory);
//            if (!localDirectory.exists()) {
//                resultMap.put("result", "文件夹不存在");
//            }else {
//                if (homedirectory.substring(homedirectory.length()-1).equals("/")){
//                    System.out.println(homedirectory.substring(homedirectory.length()-1));
//                    Boolean download = sftpUtil.download(filename,homedirectory);
//                    resultMap.put("result", download);
//                    resultMap.put("directory",homedirectory);
//                }else {
//                    Boolean download = sftpUtil.download(filename,homedirectory+"/");
//                    resultMap.put("result", download);
//                    resultMap.put("directory",homedirectory);
//                }
//            }
//            //下载路径
//        }else {
//            //判断目录是否存在
//            File localDirectory = new File(saveDirectory);
//            if (!localDirectory.exists()) {
//                resultMap.put("result", "文件夹不存在");
//            }else {
//                if (saveDirectory.substring(saveDirectory.length()-1).equals("/")){
//                    System.out.println(saveDirectory.substring(saveDirectory.length()-1));
//                    Boolean download = sftpUtil.download(filename,saveDirectory);
//                    resultMap.put("result", download);
//                    resultMap.put("directory",homedirectory);
//                }else {
//                    Boolean download = sftpUtil.download(filename,saveDirectory+"/");
//                    resultMap.put("result", download);
//                    resultMap.put("directory",saveDirectory);
//                }
//            }
//        }
//        return resultMap;
//    }
}
