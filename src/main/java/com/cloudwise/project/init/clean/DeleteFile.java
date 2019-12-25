//package com.cloudwise.project.init.clean;
//
//import com.cloudwise.project.init.BaseJob;
//import com.cloudwise.project.mapper.FileInfoMapper;
//import com.cloudwise.project.service.FtpUploadAndDelete;
//import com.cloudwise.project.vo.FileInfo;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.io.File;
//import java.util.List;
//@Slf4j
//public class DeleteFile extends BaseJob {
//    @Autowired
//    private FtpUploadAndDelete clean;
//    @Autowired
//    private FileInfoMapper fileInfoMapper;
//
//    @Override
//    public void collectorData() {
//        cleanFile();
//    }
//    public void cleanFile(){
//        List<FileInfo> allfileInfo = fileInfoMapper.getAllfileInfo();
//        String homedir=System.getProperty("user.home") + "/";
//        for(int i=0;i<allfileInfo.size();i++){
//            String filename=allfileInfo.get(i).getName()+"."+allfileInfo.get(i).getType();
//            File localfile=new File(homedir+filename);
//            if (localfile.exists()){
//                localfile.delete();
//            }
//            log.info(filename+"本地文件已经删除");
//        }
//    }
//}
