package com.cloudwise.project.util;

import com.cloudwise.project.conf.SftpConfig;
import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

@Slf4j
public class SftpUtil {

    public static boolean upload(SftpConfig sftpConfig, String filename, InputStream input) {
        try {
            Session session = connect(sftpConfig);
            ChannelSftp channel = null;
            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();
            boolean connected = channel.isConnected();
            if (connected){
                log.info("创建sftp进程任务成功");
                channel.put(input, filename);
            }else {
                log.error("创建sftp进程任务失败");
            }
            channel.exit();
            int status = channel.getExitStatus();
            if (status == -1) {
                return true;
            }
            input.close();
        } catch (JSchException e) {
            log.error("jsch产生异常",e);
        } catch (SftpException e) {
            log.error("sftp产生异常错误",e);
        } catch (IOException e) {
            log.error("IO产生异常",e);
        }
        return false;
    }

    /**
     * @Description: 删除sftp服务器的指定文件
     * @Author: Locas Hu
     * @Date: 2019/10/21
     **/
    public static boolean delete(SftpConfig sftpConfig, String filename) {
        try {
            Session session = connect(sftpConfig);
            ChannelSftp channel = null;
            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();
            boolean connected = channel.isConnected();
            if (connected){
                log.info("创建sftp进程任务成功");
                channel.rm(filename);
            }else {
                log.error("创建sftp进程任务失败");
            }
            channel.exit();
            int status = channel.getExitStatus();
            if (status == -1) {
                return true;
            }
        } catch (SftpException | JSchException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @Description: 删除目录下的所有文件以及删除后的空目录（便于后期需求扩展，目前不需要）
     * @Author: Locas Hu
     * @Date: 2019/10/21
     **/
    public static boolean deleteDirectory(SftpConfig sftpConfig, String directory) {
        try {
            Session session = connect(sftpConfig);
            ChannelSftp channel = null;
            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();
            Vector filelist = channel.ls(directory);
            for (Object file : filelist) {
                if (file instanceof ChannelSftp.LsEntry) {
                    String filename = ((ChannelSftp.LsEntry) file).getFilename();
                    channel.rm(filename);
                }
            }
            channel.rmdir(directory);
            channel.exit();
            return true;
        } catch (SftpException | JSchException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @Description: 连接sftp
     * @Author: Locas Hu
     * @Date: 2019/10/21
    **/
    public static Session connect(SftpConfig sftpConfig) {
        Session session = null;
        JSch jsch = new JSch();
        try {
            //给出连接需要的用户名，ip地址以及端口号
            session = jsch.getSession(sftpConfig.getUsername(), sftpConfig.getIp(), Integer.parseInt(sftpConfig.getPort()));
            //第一次登陆时候，是否需要提示信息，value可以填写 yes，no或者是ask
            session.setConfig("StrictHostKeyChecking", "no");
            //设置是否超时
            session.setTimeout(30000);
            //设置密码
            session.setPassword(sftpConfig.getPassword());
            //创建连接
            session.connect();
            if (session == null) {
                log.warn("session is null");
            } else if (session.isConnected() == true) {
                log.info("connet to sftp server is successful");
            }
        } catch (JSchException e) {
            log.error("连接sftp服务器异常"+e,e);
        }
        return session;
    }
}
