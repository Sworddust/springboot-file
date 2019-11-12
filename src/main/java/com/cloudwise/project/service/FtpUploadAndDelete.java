package com.cloudwise.project.service;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

import com.cloudwise.project.conf.SftpConfig;
import com.cloudwise.project.util.SftpUtil;
import com.jcraft.jsch.SftpException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * @Description: 文件上传，下载，删除方法
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
        String homedirectory = System.getProperty("user.home") + "/";
        String FileName = file.getOriginalFilename();
        try {
            localexist(FileName, 1);
            SftpUtil sftpUtil = new SftpUtil(sftpConfig);
            sftpUtil.login();
            Boolean uploadResult = sftpUtil.uploadfile(FileName, file.getInputStream());
            //上传后在项目服务器备份
            ThreadFactory threadFactory = new ThreadFactoryBuilder();
            ThreadPoolExecutor downExecutor = new ThreadPoolExecutor(5, 10, 0L,
                    TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>(1024),
                    threadFactory,
                    new ThreadPoolExecutor.AbortPolicy());
            downExecutor.execute(new backtolocal(FileName, homedirectory));
            downExecutor.shutdown();
            if (uploadResult) {
                String path = sftpConfig.getSftpPath() + "/" + FileName;
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

    /**
     * @Description: 线程工厂
     * @Author: Locas Hu
     * @Date: 2019/11/12
    **/
    class ThreadFactoryBuilder implements ThreadFactory {
        @Override
        public Thread newThread(@NonNull Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("downTread");
            return thread;
        }
    }

    /**
     * @Description: 线程任务
     * @Author: Locas Hu
     * @Date: 2019/11/12
    **/
    class backtolocal implements Runnable {
        private String filename;
        private String saveDirectory;

        public backtolocal(String filename, String saveDirectory) {
            this.filename = filename;
            this.saveDirectory = saveDirectory;
        }

        public backtolocal() {
        }

        @Override
        public void run() {
            try {
                Thread.sleep(20000);
                SftpUtil sftpUtil = new SftpUtil(sftpConfig);
                sftpUtil.login();
                sftpUtil.download(filename, saveDirectory);
            } catch (InterruptedException | SftpException | IOException e) {
                e.printStackTrace();
            }
        }
    }


    public Map<String, Object> downloadfile(String filename) throws IOException, SftpException {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        String homedirectory = System.getProperty("user.home") + "/";
        int searchlocal = localexist(filename, 2);
        //如果searchlocal为2说明已经用户主目录已经存在文件，为3说明用户主目录不存在该文件
        if (searchlocal == 2) {
            //前台请求下载开始时间
            Date qianbegin = new Date();
            InputStream instream = new BufferedInputStream(new FileInputStream(homedirectory  + filename));
            resultMap.put("file", instream);
            resultMap.put("qianbegin", qianbegin);
            resultMap.put("result", true);
        } else if (searchlocal == 3) {
            ///前台请求下载开始时间
            Date qianbegin = new Date();
            SftpUtil sftpUtil = new SftpUtil(sftpConfig);
            sftpUtil.login();
            Boolean download = sftpUtil.download(filename, homedirectory);
            // 以流的形式下载文件。
            InputStream instream = new BufferedInputStream(new FileInputStream(homedirectory  + filename));
            resultMap.put("file", instream);
            resultMap.put("qianbegin", qianbegin);
            resultMap.put("result", true);
        }
        return resultMap;
    }


    public Map<String, Object> delete(String filename) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        localexist(filename, 3);
        SftpUtil sftpUtil = new SftpUtil(sftpConfig);
        sftpUtil.login();
        Boolean deleteResult = sftpUtil.deletefile(filename);
        resultMap.put("result", deleteResult);
        return resultMap;
    }

    private static int localexist(String filename, int tag) {
        String homedirectory = System.getProperty("user.home") + "/";
        //上传前判断本地是否有与上传文件重复的文件
        File file = new File(homedirectory + filename);
        if (tag == 1) {
            if (file.exists()) {
                file.delete();
                return 1;
            }
        } else if (tag == 2) {
            if (file.exists()) {
                return 2;
            } else {
                return 3;
            }
        } else if (tag == 3) {
            if (file.exists()) {
                file.delete();
            }
        }
        return 0;
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
