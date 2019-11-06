package com.cloudwise.project.conf;

import lombok.Data;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Data
@ToString
@Component
public class SftpConfig implements Serializable {
    @Value("${sftp.ip}")
    private String ip;
    @Value("${sftp.port}")
    private String port;
    @Value("${sftp.username}")
    private String username;
    @Value("${sftp.password}")
    private String password;
    @Value("${sftp.sftpPath}")
    private String sftpPath;
}
