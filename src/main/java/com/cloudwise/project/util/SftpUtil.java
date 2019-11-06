package com.cloudwise.project.util;

import com.cloudwise.project.conf.SftpConfig;
import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

@Slf4j
public class SftpUtil {

    private ChannelSftp sftp;

    private Session session;
    /**
     * SFTP 登录用户名
     */
    private String username;
    /**
     * SFTP 登录密码
     */
    private String password;
    /**
     * 私钥
     */
    private String privateKey;
    /**
     * SFTP 服务器地址IP地址
     */
    private String host;
    /**
     * SFTP 端口
     */
    private int port;

    /**
     * 构造基于密码认证的sftp对象
     */
    public SftpUtil(SftpConfig sftpConfig) {
        this.username = sftpConfig.getUsername();
        this.password = sftpConfig.getPassword();
        this.host = sftpConfig.getIp();
        this.port = Integer.parseInt(sftpConfig.getPort());
    }

    /**
     * @Description: 连接sftp服务器
     * @Author: Locas Hu
     * @Date: 2019/10/31
     **/
    public void login() {
        try {
            JSch jsch = new JSch();
            if (privateKey != null) {
                jsch.addIdentity(privateKey);
            }
            session = jsch.getSession(username, host, port);
            if (password != null) {
                session.setPassword(password);
            }
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            Channel channel = session.openChannel("sftp");
            channel.connect();
            sftp = (ChannelSftp) channel;
        } catch (JSchException e) {
            log.error("连接sftp服务器异常: " + e, e);
            logout();
        }
    }

    /**
     * 关闭连接 server
     */
    public void logout() {
        if (sftp != null) {
            if (sftp.isConnected()) {
                sftp.disconnect();
            }
        }
        if (session != null) {
            if (session.isConnected()) {
                session.disconnect();
            }
        }
    }

    /**
     * @Description: 上传文件至sftp服务器
     * @Author: Locas Hu
     * @Date: 2019/10/31
     **/
    public boolean uploadfile(String filename, InputStream input) {
        try {
            boolean connected = sftp.isConnected();
            if (connected) {
                log.info("创建sftp进程任务成功，执行上传任务");
                Date begin=new Date();
                sftp.put(input, filename);
                Date end=new Date();
                long s=end.getTime()-begin.getTime();
                long seconds=0;
                if (s%1000>500){
                    seconds=s/1000+1;
                }else {
                    seconds=s/1000;
                }
                log.info("sftp服务器上传文件: "+filename+" 耗时: "+seconds+"s");
            } else {
                log.error("创建sftp进程任务失败");
            }
            sftp.exit();
            int status = sftp.getExitStatus();
            if (status == -1) {
                input.close();
                logout();
                return true;
            }
        } catch (SftpException e) {
            log.error("sftp产生异常: " + e, e);
        } catch (IOException e) {
            log.error("IO产生异常: " + e, e);
        }
        return false;
    }


    /**
     * @Description: 下载文件
     * @Author: Locas Hu
     * @param filename 下载的文件
     */
    public Boolean download(String filename, String saveDirectory) throws SftpException, IOException {
        boolean connected = sftp.isConnected();
        if (connected) {
            log.info("创建sftp进程任务成功，执行下载任务");
        } else {
            log.error("创建sftp进程任务失败");
        }
        File localfile = new File(saveDirectory + filename);
        if (localfile.exists()) {
            localfile.delete();
        }
        Date begin=new Date();
        sftp.get(filename, new FileOutputStream(localfile));
        Date end=new Date();
        long s=end.getTime()-begin.getTime();
        long seconds=0;
        if (s%1000>500){
            seconds=s/1000+1;
        }else {
            seconds=s/1000;
        }
        log.info("sftp服务器文件:"+filename+" 下载耗时: "+seconds+"s");
        sftp.exit();
        int status = sftp.getExitStatus();
        if (status == -1) {
            logout();
            return true;
        }
        return false;
    }


    /**
     * @Description: 删除指定文件
     * @Author: Locas Hu
     * @Date: 2019/11/5
     **/
    public boolean deletefile(String filename) {
        try {
            boolean connected = sftp.isConnected();
            if (connected) {
                log.info("创建sftp进程任务成功，执行删除任务");
                sftp.rm(filename);
            } else {
                log.error("创建sftp进程任务失败");
            }
            sftp.exit();
            int status = sftp.getExitStatus();
            if (status == -1) {
                logout();
                return true;
            }
        } catch (SftpException e) {
            log.error("sftp操作异常: " + e, e);
        }
        return false;
    }

    /**
     * @Description: 删除目录下的所有文件以及删除后的空目录（便于后期需求扩展，目前不需要）
     * @Author: Locas Hu
     * @Date: 2019/10/21
     **/
//    public static boolean deleteDirectory(SftpConfig sftpConfig, String directory) {
//        try {
//            Session session = connect(sftpConfig);
//            if (session==null){
//                log.warn("sftp服务器连接为空，无法删除目录");
//                return false;
//            }
//            ChannelSftp channel = null;
//            channel = (ChannelSftp) session.openChannel("sftp");
//            channel.connect();
//            Vector filelist = channel.ls(directory);
//            for (Object file : filelist) {
//                if (file instanceof ChannelSftp.LsEntry) {
//                    String filename = ((ChannelSftp.LsEntry) file).getFilename();
//                    channel.rm(filename);
//                }
//            }
//            channel.rmdir(directory);
//            channel.exit();
//            return true;
//        } catch (SftpException e){
//            log.error("sftp操作异常: "+e, e);
//        }catch (JSchException e){
//            log.error("jsch产生异常: "+e, e);
//        }
//        return false;
//    }
}
