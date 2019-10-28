package com.cloudwise.project.service;

import com.cloudwise.project.mapper.FileInfoMapper;
import com.cloudwise.project.vo.FileInfo;
import com.cloudwise.project.vo.ResultMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

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

    public ResultMessage uploadFile(MultipartFile file[], String uploader) {
        ResultMessage resultMessage = new ResultMessage();
        for (int i = 0; i < file.length; i++) {
            //截取文件名
            String name = file[i].getOriginalFilename().substring(0, file[i].getOriginalFilename().lastIndexOf("."));
            //截取文件类型
            String type = file[i].getOriginalFilename().substring(file[i].getOriginalFilename().lastIndexOf(".") + 1);
            double size = file[i].getSize();
            // 创建新的文档id
            String id = getFileID();
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //上传时间
            String time = dateFormat.format(date);
            Map<String, Object> upload = ftpUploadAndDelete.upload(file[i]);
            String filename = fileInfoMapper.checkNameRepeat(name);
            if (upload.get("result").equals(true)) {
                //如果文档名不为空，说明是重新上传一个重名文件，更新文件信息；不为空，说明是一个新的文件，插入一条新的记录
                if (filename != null && !filename.equals("")) {
                    fileInfoMapper.updateFileinfo(time, size, upload.get("path").toString(), uploader, name);
                    resultMessage.setCode(200);
                    resultMessage.setMsg("更新上传文件信息成功");
                } else {
                    fileInfoMapper.insertFileInfo(id, name, upload.get("path").toString(), time, size, uploader, type);
                    resultMessage.setCode(200);
                    resultMessage.setMsg("上传文件成功");
                }
            }else {
                resultMessage.setCode(404);
                resultMessage.setMsg("上传失败");
            }
        }
        return resultMessage;
    }


    /**
     * @Description: 删除文件
     * @Author: Locas Hu
     * @Date: 2019/10/11
     **/
    public ResultMessage deleteFile(String name, String type) {
        ResultMessage resultMessage = new ResultMessage();
        Map<String, Object> delete = ftpUploadAndDelete.delete(name + "." + type);
        if ((Boolean) delete.get("result")) {
            fileInfoMapper.deleteFileinfo(name);
            resultMessage.setCode(200);
            resultMessage.setMsg("删除文件成功");
        }else{
            resultMessage.setCode(404);
            resultMessage.setMsg("删除失败");
        }
        return resultMessage;
    }


    /**
     * @Description: 查询全部文档信息
     * @Author: Locas Hu
     * @Date: 2019/10/9
     **/
    public ResultMessage getAllfile() {
        ResultMessage resultMessage = new ResultMessage();
        List<FileInfo> allfileInfo = fileInfoMapper.getAllfileInfo();
        if (allfileInfo != null && allfileInfo.size() >= 0) {
            resultMessage.setCode(200);
            resultMessage.setMsg("查询成功");
            resultMessage.setData(allfileInfo);
            
        } else {
            resultMessage.setCode(404);
            resultMessage.setMsg("查询失败");
        }
        return resultMessage;
    }

    /**
     * @Description: 返回前台下载链接，每调用一次本方法，根据id更新下载次数
     * @Author: Locas Hu
     * @Date: 2019/10/9
     **/
    public ResultMessage getPath(String id) {
        ResultMessage resultMessage = new ResultMessage();
        String path = fileInfoMapper.getPath(id);
        if (!path.isEmpty() && !path.equals("")) {
            fileInfoMapper.updateFileDownCount(id);
            resultMessage.setCode(200);
            resultMessage.setMsg("下载成功");
            resultMessage.setData(path);
        } else {
            resultMessage.setCode(404);
            resultMessage.setMsg("下载失败");
        }
        return resultMessage;
    }

    /**
     * @Description: 生成随机文档ID
     * @Author: Locas Hu
     * @Date: 2019/10/11
     **/
    public static String getFileID() {
        // 1.获取当前系统的时间毫秒数
        long millis = System.currentTimeMillis();
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
