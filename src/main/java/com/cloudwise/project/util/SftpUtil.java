package com.cloudwise.project.util;

import com.cloudwise.project.conf.SftpConfig;
import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;
import java.io.*;
import java.util.Vector;

@Slf4j
public class SftpUtil {

    public static boolean upload(SftpConfig sftpConfig, String filename, InputStream input) {
        try {
            Session session = connect(sftpConfig);
            ChannelSftp channel = null;
            // 创建连接的形式，这里是sftp
            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();
            channel.put(input, filename);
            channel.exit();
            int status = channel.getExitStatus();
            if (status == 1) {
                return true;
            }
            input.close();
        } catch (JSchException e) {
            e.printStackTrace();
        } catch (SftpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    //这个方法用来删除sftp上对应的文件
    //ls方法是用来将制定文件夹下的文件名都给取出来
    //遍历之后，拿到文件名进行删除，可以在删除前，给出判断条件，或者是制定文件名
    public static boolean delete(SftpConfig sftpConfig, String filename) {
        try {
            Session session = connect(sftpConfig);
            ChannelSftp channel = null;
            // 创建连接的形式，这里是sftp
            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();
            channel.rm(filename);
            channel.exit();
            int status = channel.getExitStatus();
            if (status == 1) {
                return true;
            }
        } catch (SftpException | JSchException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static boolean deleteDirectory(SftpConfig sftpConfig, String directory) {
        try {
            Session session = connect(sftpConfig);
            ChannelSftp channel = null;
            // 创建连接的形式，这里是sftp
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


    public static Session connect(SftpConfig sftpConfig) {
        Session session = null;
        JSch jsch = new JSch();
        try {
            //给出连接需要的用户名，ip地址以及端口号
            session = jsch.getSession(sftpConfig.getUsername(), sftpConfig.getHost(), Integer.parseInt(sftpConfig.getPort()));
            //第一次登陆时候，是否需要提示信息，value可以填写 yes，no或者是ask
            session.setConfig("StrictHostKeyChecking", "no");
            //设置是否超时
            session.setTimeout(30000);
            //设置密码
            session.setPassword(sftpConfig.getPassword());
            //创建连接
            session.connect();
            log.info("sftp session set properties success");
            if (session == null) {
                log.error("session is null");
            } else if (session.isConnected() == true) {
                log.info("connet to sftp server is successful");
            }
        } catch (JSchException e) {
            e.printStackTrace();
        }
        return session;
    }
}
