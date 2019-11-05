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

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
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
    public List<ResultMessage> uploadFile(@RequestParam("file") MultipartFile file[]) throws IOException {
        List<ResultMessage> uploadResult = fileInfoService.uploadFile(file, "uploader");
        System.out.println(uploadResult);
        return uploadResult;
    }

    @RequestMapping("/delete")
    public ResultMessage deleteFile(@RequestParam("id")String id){
        ResultMessage deleteResult = fileInfoService.deleteFile(id);
        return deleteResult;
    }

    @RequestMapping("/getAllfile")
    public ResultMessage getAllfile() {
        ResultMessage allfile = fileInfoService.getAllfile();
        return allfile;
    }


    @RequestMapping("/download")
    public HttpServletResponse download(@RequestParam("id")String id,HttpServletResponse response) throws Exception {
        Map<String, Object> downResult = fileInfoService.downloadFile(id);
        //读取本地已经下载的文件流
        InputStream inputStream=(InputStream)downResult.get("file");
        //文件名
        String filename=(String)downResult.get("filename");
        //默认保存到本地用户主目录
        String homedirectory = System.getProperty("user.home")+"/";
        File file=new File(homedirectory+filename);
        byte[] buffer = new byte[inputStream.available()];
        inputStream.read(buffer);
        inputStream.close();
        // 清空response
        response.reset();
        // 设置response的Header
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename,"UTF-8"));
        response.addHeader("Content-Length", "" + file.length());
        OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
        response.setContentType("application/octet-stream");
        toClient.write(buffer);
        toClient.flush();
        toClient.close();
        Date qianend=new Date();
        Date qianbegin=(Date)downResult.get("qianbegin");
        long s=qianend.getTime()-qianbegin.getTime();
        long seconds=0;
        if (s%1000>500){
            seconds=s/1000+1;
        }else {
            seconds=s/1000;
        }
        log.info("从请求下载到浏览器响应一共耗时："+seconds+"s");
        return null;
    }


    //    @RequestMapping("/topdf")
//    public ResultMessage getPDF(@RequestParam("id")String id){
//        fileInfoService.toPDF();
//    }
}
