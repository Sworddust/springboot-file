package com.cloudwise.project.mapper;

import com.cloudwise.project.vo.FileInfo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Mapper
@Component
public interface FileInfoMapper {
    @Select("select id,name,path,time,size,uploader,count,type from fileinfo order by time desc")
    List<FileInfo>  getAllfileInfo();

    @Select("select name,type from fileinfo where id=#{id}")
    FileInfo  selectNameAndType(@Param("id")String id);


    @Select("select id,name,path,time,size,count,type from fileinfo where id=#{id}")
    FileInfo getFileInfo(@Param("id") String id);

    @Select("select name from fileinfo where name=#{name}")
    String checkNameRepeat(@Param("name") String name);

//    @Select("select path from fileinfo where id=#{id}")
//    String getPath(@Param("id") String id);


    @Insert("insert into fileinfo(id,name,path,time,size,uploader,type) values(#{id},#{name},#{path},#{time},#{size},#{uploader},#{type})")
    int insertFileInfo(@Param("id") String id, @Param("name") String name, @Param("path") String path, @Param("time") String time,
                       @Param("size") double size, @Param("uploader") String uploader,@Param("type")String type);

    @Update("update fileinfo set count=count+1 where id=#{id}")
    int updateFileDownCount(@Param("id") String id);

    @Update("update fileinfo set time=#{time} , size=#{size} , path=#{path} , uploader=#{uploader} where name=#{name}")
    int updateFileinfo(@Param("time")String time,@Param("size")double size,@Param("path")String path,@Param("uploader")String uploader,@Param("name")String name);

    @Delete("delete from fileinfo where id=#{id}")
    int deleteFileinfo(@Param("id")String id);
}
