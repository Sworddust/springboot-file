package com.cloudwise.project.service;

import com.cloudwise.project.mapper.FileInfoMapper;
import com.cloudwise.project.vo.FileInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
public class FileInfoService {
    @Autowired
    private FileInfoMapper fileInfoMapper;
    @Autowired
    private FtpUploadAndDelete ftpUploadAndDelete;
    /**
     * @Description: 多文件上传（同时上传多个文件）
     * @Author: Locas Hu
     * @Date: 2019/10/9
     **/
    public Map<String,Object> uploadFile(MultipartFile file[],String uploader) throws IOException {
        Map<String,Object> uploadResult=new HashMap<>();
        for(int i=0;i<file.length;i++){
            String name=file[i].getOriginalFilename().substring(0,file[i].getOriginalFilename().lastIndexOf("."));
            System.out.println("去除后缀的文件名"+name);

            String type=file[i].getOriginalFilename().substring(file[i].getOriginalFilename().lastIndexOf(".")+1);
            System.out.println("文档类型-------"+type);

            double size=file[i].getSize();
            // 创建新的文档id
            String id = getFileID();
            Date date=new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String  time=dateFormat.format(date);
            Map<String, Object> upload = ftpUploadAndDelete.upload(file[i]);
            String filename = fileInfoMapper.checkNameRepeat(name);
            if (upload.get("result").equals(true)){
                if (filename!=null&&!filename.equals("")){
                    fileInfoMapper.updateFileinfo(time,size,upload.get("path").toString(),uploader,name);
                }else{
                    fileInfoMapper.insertFileInfo(id,name,upload.get("path").toString(),time,size,uploader,type);
                }
            }
            uploadResult.put("result",upload.get("result"));
        }
        return uploadResult;
    }


    /**
     * @Description: 删除文件
     * @Author: Locas Hu
     * @Date: 2019/10/11
    **/
    public Map<String,Object> deleteFile(String name,String type){
        Map<String,Object> deleteResult=new HashMap<>();
        Map<String, Object> delete = ftpUploadAndDelete.delete(name+"."+type);
        if ((Boolean) delete.get("result")){
            fileInfoMapper.deleteFileinfo(name);
            deleteResult.put("result",delete.get("result"));
        }
        return deleteResult;
    }


    /**
     * @Description: 查询全部文档信息
     * @Author: Locas Hu
     * @Date: 2019/10/9
    **/
    public Map<String,Object> getAllfile(){
        Map<String,Object> allfile=new HashMap<>();
        List<FileInfo> allfileInfo = fileInfoMapper.getAllfileInfo();
        if (allfileInfo!=null&&allfileInfo.size()>=0){
            allfile.put("result",true);
            allfile.put("allfileinfo",allfileInfo);
        }else {
            allfile.put("result",false);
        }
        return allfile;
    }
    /**
     * @Description: 返回前台下载链接，每调用一次本方法，根据id更新下载次数
     * @Author: Locas Hu
     * @Date: 2019/10/9
    **/
    public String getPath(String id){
        String path = fileInfoMapper.getPath(id);
        if (!path.isEmpty()&&!path.equals("")){
            fileInfoMapper.updateFileDownCount(id);
        }
        return path;
    }

    /**
     * @Description: 生成随机文档ID
     * @Author: Locas Hu
     * @Date: 2019/10/11
     **/
    public static String getFileID() {
        // 1.获取当前系统的时间毫秒数
        long millis = System.currentTimeMillis();
        System.out.println(millis);
        // 2.生成随机数(0-999之间进行随机)
        Random random = new Random();
        int randomNum = random.nextInt(999);
        // 3.需要进行占位符(需要把当前系统时间的毫秒数和随机数整合在一起)
        // %:占位符   03:三位(如果不足三位往前补0)  d:数字
        String id = millis + String.format("%03d", randomNum);
        // 4.返回文件名称
        return id;
    }
}
