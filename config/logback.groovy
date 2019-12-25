

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.util.FileSize

appender("Console", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
    }

}
appender("R", RollingFileAppender) {
    file = "/data/logs/console.log"
    encoder(PatternLayoutEncoder) {
        pattern = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
    }
    rollingPolicy(SizeAndTimeBasedRollingPolicy) {
        fileNamePattern = "/data/logs/%d{yyyy-MM-dd}.%i.log"
        maxFileSize = "1024MB"
        maxHistory = 7
        totalSizeCap = FileSize.valueOf("1024MB")
    }
}

logger("org.apache.ibatis", Level.ERROR)
logger("com.cloudwise.project", Level.ERROR)

root(Level.valueOf("INFO"), ["Console", "R"])