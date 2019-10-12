package com.cloudwise.project.controller;

import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @Description: 文件上传和下载
 * @Author: Locas Hu
 * @Date: 2019/10/9
**/
@CrossOrigin
@Controller
public class FileUploadController {
    /**
     * @Description: 多文件上传（同时上传多个文件）
     * @Author: Locas Hu
     * @Date: 2019/10/9
    **/
    @RequestMapping("/uploadMul")
    @ResponseBody
    public List<Map<String,Object>> uploadMultipartFile(@RequestParam("file") MultipartFile file[]) throws IOException {
        List<Map<String,Object>> fileList=new ArrayList<>();
        for(int i=0;i<file.length;i++){
            //String path = request.getSession().getServletContext().getRealPath("/static/upload/");
            String path="/myapp/upload";
            String filename = file[i].getOriginalFilename();
            File mkdir=new File(path);
            if(!mkdir.exists()){
                mkdir.mkdirs();
            }
            File newfile = new File(path +"/"+ filename);
            if(newfile.exists()){
                newfile.delete();
            }
            file[i].transferTo(newfile);
            Map<String,Object> fileInfo=new HashMap<>();
            fileInfo.put("fileName",filename);
            fileInfo.put("fileUrl",newfile.getAbsolutePath());
            fileInfo.put("fileDate",new Date());
            fileList.add(fileInfo);
        }
        return fileList;
    }
    /**
     * @Description: 单文件上传
     * @Author: Locas Hu
     * @Date: 2019/10/9
    **/
    @RequestMapping("/uploadOne")
    @ResponseBody
    public Map<String,Object> upload(@RequestParam("file") MultipartFile file) throws IOException {
            //上传路径
            String path="/myapp/upload";
            //文件名
            String filename = file.getOriginalFilename();
            //根据上传路径创建一个新的文件目录
            File mkdir=new File(path);
            if(!mkdir.exists()){
                //创建文件目录，可以在父文件目录不存在的情况下创建父文件目录和子文件目录
                mkdir.mkdirs();
            }
            //根据文件名创建一个新的文件
            File newfile = new File(path +"/"+ filename);
            //判断该文件是否已经存在
            if(newfile.exists()){
                newfile.delete();
            }
            //将接收的文件内容转换到新的文件中
            file.transferTo(newfile);
            Map<String,Object> fileInfo=new HashMap<>();
            fileInfo.put("fileName",filename);
            fileInfo.put("fileUrl",newfile.getAbsolutePath());
            fileInfo.put("fileDate",new Date());
        return fileInfo;
    }
    /**
     * @Description:  文件下载
     *              ResponseEntity:响应体；用来应对Http请求，封装：http状态码，头部信息，内容
     * @Author: Locas Hu
     * @Date: 2019/10/9
    **/
    @RequestMapping("/download")
    public ResponseEntity<byte[]> download(@RequestParam("filename") String filenname, HttpServletRequest request) throws IOException {
        filenname=new String(filenname.getBytes(StandardCharsets.ISO_8859_1),"utf-8");
        //获取绝对路径
        String path="/myapp/upload";
        //要下载的文件对象
        File file=new File(path+"/"+filenname);
        //读取文件得到字节数组
        byte[] data= FileUtils.readFileToByteArray(file);
        //头部信息
        HttpHeaders headers=new HttpHeaders();
        //文档类型
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        //以何种形式处理下载文件：弹出来保存窗口
        headers.setContentDispositionFormData("attachment", filenname);
        return new ResponseEntity<>(data,headers, HttpStatus.CREATED);
    }
}
