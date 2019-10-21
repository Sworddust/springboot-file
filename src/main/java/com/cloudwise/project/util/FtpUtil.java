package com.cloudwise.project.util;

import com.cloudwise.project.conf.FtpConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;
@Slf4j
public class FtpUtil {


    public static Boolean uploadFile(FtpConfig ftpConfig,
                                     String filename, InputStream input) throws IOException {
        // 2.FTPClient对象(是ftp进行连接/断开连接和上传的重要对象)
        FTPClient ftp = new FTPClient();
        try {
            // reply:答复(ftp服务器所返回的状态，只会在连接的时候进行返回状态码，上传的时候返回的是boolean类型)
            int reply;
            ftp.setControlEncoding("UTF-8");
            // 3.connect()：连接ftp
            ftp.connect(ftpConfig.getHost(), Integer.parseInt(ftpConfig.getPort()));
            // 4.login()：登录ftp的用户
            ftp.login(ftpConfig.getUsername(), ftpConfig.getPassword());
            // 5.ftp.getReplyCode():连接和登录后的状态码
            // reply:230表示连接成功 530表示连接和登录失败
            reply = ftp.getReplyCode();
            // 6.判断状态码是否为230(只要是200到300之间[200,300)都返回true，表示连接和登录成功，否则返回false说明登录失败)
            if (!FTPReply.isPositiveCompletion(reply)) {
                // 如果登录失败断开连接
                ftp.disconnect();
                return false;
            }
            // 11.把文件以字符流的形式进行上传(开启字符流上传的模式)
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            // 12.真正的上传其实在storeFile()方法中 返回值为Boolean类型 true:上传成功 false:上传失败
            if (!ftp.storeFile(filename, input)) {
                log.error(filename+"文件上传失败。发生异常");
                return false;
            }
            // 13.关闭输入流
            input.close();
            // 14.退出ftp的登录
            ftp.logout();
        } catch (IOException e) {
            log.error("文件上传异常");
            throw new IOException(e);
        } finally {
            if (ftp.isConnected()) {
                try {
                    // 断开连接
                    ftp.disconnect();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
        return true;
    }


    /**
     * @Description: 删除文件目录或者文件，为目录时逐个删除包括文件
     * @Author: Locas Hu
     * @Date: 2019/10/11
    **/
    public static boolean deleteFiles(FtpConfig ftpConfig, String filename) {
        try {
            /** 尝试改变当前工作目录到 deleteFiles
             * 1）changeWorkingDirectory：变更FTPClient当前工作目录，变更成功返回true，否则失败返回false
             * 2）如果变更工作目录成功，则表示 deleteFiles 为服务器已经存在的目录
             * 3）否则变更失败，则认为 deleteFiles 是文件，是文件时则直接删除
             */
            FTPClient ftp = new FTPClient();
            int reply;
            ftp.setControlEncoding("UTF-8");
            ftp.connect(ftpConfig.getHost(), Integer.parseInt(ftpConfig.getPort()));
            ftp.login(ftpConfig.getUsername(), ftpConfig.getPassword());
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                return false;
            }
            boolean changeFlag = ftp.changeWorkingDirectory(filename);
            if (changeFlag) {
                /**当被删除的是目录时*/
                FTPFile[] ftpFiles = ftp.listFiles();
                for (FTPFile ftpFile : ftpFiles) {
                    if (ftpFile.isFile()) {
                        boolean deleteFlag = ftp.deleteFile(ftpFile.getName());
                        if (deleteFlag) {
                            System.out.println(">>>>>删除服务器文件成功****" + ftpFile.getName());
                        } else {
                            System.out.println(">>>>>删除服务器文件失败****" + ftpFile.getName());
                        }
                    } else {
                        /**printWorkingDirectory：获取 FTPClient 客户端当前工作目录
                         * 然后开始迭代删除子目录
                         */
                        String workingDirectory = ftp.printWorkingDirectory();
                        deleteFiles(ftpConfig, workingDirectory + "/" + ftpFile.getName());
                    }
                }
                /**printWorkingDirectory：获取 FTPClient 客户端当前工作目录
                 * removeDirectory：删除FTP服务端的空目录，注意如果目录下存在子文件或者子目录，则删除失败
                 * 运行到这里表示目录下的内容已经删除完毕，此时再删除当前的为空的目录，同时将工作目录移动到上移层级
                 * */
                String workingDirectory = ftp.printWorkingDirectory();
                ftp.removeDirectory(workingDirectory);
                ftp.changeToParentDirectory();
            } else {
                /**deleteFile：删除FTP服务器上的文件
                 * 1）只用于删除文件而不是目录，删除成功时，返回 true
                 * 2）删除目录时无效,方法返回 false
                 * 3）待删除文件不存在时，删除失败，返回 false
                 * */
                String workingDirectory = ftp.printWorkingDirectory();
                boolean deleteFlag = ftp.deleteFile(filename);
                if (deleteFlag) {
                    System.out.println(">>>>>删除服务器文件成功****" + filename);
                } else {
                    System.out.println(">>>>>删除服务器文件失败****" + filename);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }



    /**
     * @Description: 下载
     * @Author: Locas Hu
     * @Date: 2019/10/12
    **/

    public static boolean downloadSingleFile(FtpConfig ftpConfig,String path,String filename) throws IOException {
        FTPClient ftp = new FTPClient();
        int reply;
        ftp.setControlEncoding("UTF-8");
        ftp.connect(ftpConfig.getHost(), Integer.parseInt(ftpConfig.getPort()));
        ftp.login(ftpConfig.getUsername(), ftpConfig.getPassword());
        reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            return false;
        }
        if (path.isEmpty()) {
            return false;
        }
        try {
            OutputStream outputStream = null;
            ftp.retrieveFile(filename, outputStream);
            }catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

}
