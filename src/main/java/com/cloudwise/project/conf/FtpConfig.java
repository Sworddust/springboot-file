package com.cloudwise.project.conf;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class FtpConfig {
    @Value("${spring.ftp.host}")
    private String host;
    @Value("${spring.ftp.port}")
    private String port;
    @Value("${spring.ftp.username}")
    private String username;
    @Value("${spring.ftp.password}")
    private String password;
    @Value("${spring.ftp.ftpPath}")
    private String ftpPath;
}
