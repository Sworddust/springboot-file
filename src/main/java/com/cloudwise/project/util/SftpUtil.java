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
                sftp.put(input, filename);
            } else {
                log.error("创建sftp进程任务失败");
            }
            sftp.exit();
            int status = sftp.getExitStatus();
            if (status == -1) {
                return true;
            }
            input.close();
        } catch (SftpException e) {
            log.error("sftp产生异常: " + e, e);
        } catch (IOException e) {
            log.error("IO产生异常: " + e, e);
        }
        return false;
    }

    public boolean delete(String filename) {
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
                return true;
            }
        } catch (SftpException e) {
            log.error("sftp操作异常: " + e, e);
        }
        return false;
    }


    /**
     * 下载文件。
     *
     * @param filename 下载的文件
     */
    public Boolean download(String filename,String saveDirectory) throws SftpException, IOException {
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
        sftp.get(filename, new FileOutputStream(localfile));
        sftp.exit();
            int status = sftp.getExitStatus();
            if (status == -1) {
                return true;
            }
        return false;
    }


    /**
     * @Description: 上传文件至sftp服务器
     * @Author: Locas Hu
     * @Date: 2019/10/21
     **/
//    public static boolean upload(SftpConfig sftpConfig, String filename, InputStream input) {
//        try {
//            Session session = connect(sftpConfig);
//            if (session==null){
//                log.warn("sftp服务器连接为空，无法上传文件");
//                return false;
//            }
//            ChannelSftp channel = null;
//            //打开sftp通道
//            channel = (ChannelSftp) session.openChannel("sftp");
//            channel.connect();
//            boolean connected = channel.isConnected();
//            if(connected){
//                log.info("创建sftp进程任务成功，执行上传任务");
//                channel.put(input, filename);
//            }else{
//                log.error("创建sftp进程任务失败");
//            }
//            channel.exit();
//            int status = channel.getExitStatus();
//            if (status == -1) {
//                return true;
//            }
//            input.close();
//        } catch (JSchException e) {
//            log.error("jsch产生异常: "+e,e);
//        } catch (SftpException e) {
//            log.error("sftp产生异常: "+e,e);
//        } catch (IOException e) {
//            log.error("IO产生异常: "+e,e);
//        }
//        return false;
//    }

    /**
     * @Description: 删除sftp服务器的指定文件
     * @Author: Locas Hu
     * @Date: 2019/10/21
     **/
//    public static boolean delete(SftpConfig sftpConfig, String filename) {
//        try {
//            Session session = connect(sftpConfig);
//            if (session==null){
//                log.warn("sftp服务器连接为空，无法删除文件");
//                return false;
//            }
//            ChannelSftp channel = null;
//            channel = (ChannelSftp) session.openChannel("sftp");
//            channel.connect();
//            boolean connected = channel.isConnected();
//            if(connected){
//                log.info("创建sftp进程任务成功，执行删除任务");
//                channel.rm(filename);
//            }else{
//                log.error("创建sftp进程任务失败");
//            }
//            channel.exit();
//            int status = channel.getExitStatus();
//            if (status == -1) {
//                return true;
//            }
//        } catch (SftpException e){
//            log.error("sftp操作异常: "+e, e);
//        }catch (JSchException e){
//            log.error("jsch产生异常: "+e, e);
//        }
//        return false;
//    }

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

    /**
     * @Description: 连接sftp
     * @Author: Locas Hu
     * @Date: 2019/10/21
     **/
//    public static Session connect(SftpConfig sftpConfig) {
//        Session session = null;
//        JSch jsch = new JSch();
//        try {
//            //给出连接需要的用户名，ip地址以及端口号
//            session = jsch.getSession(sftpConfig.getUsername(), sftpConfig.getIp(), Integer.parseInt(sftpConfig.getPort()));
//            //第一次登陆时候，是否需要提示信息，value可以填写 yes，no或者是ask
//            session.setConfig("StrictHostKeyChecking", "no");
//            //设置是否超时
//            session.setTimeout(30000);
//            //设置密码
//            session.setPassword(sftpConfig.getPassword());
//            //创建连接
//            session.connect();
//            if (session == null) {
//                log.warn("session为空，无法连接");
//            } else if (session.isConnected() == true) {
//                log.info("连接sftp服务器成功");
//            }
//        } catch (JSchException e) {
//            log.error("连接sftp服务器异常: "+e,e);
//            if(session != null){
//                session.disconnect();
//                session = null;
//            }
//        }
//        return session;
//    }
}
