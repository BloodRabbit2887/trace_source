package qichen.code;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import qichen.code.controller.conf.FilePathConf;

@Slf4j
@EnableScheduling
@EnableCaching
@EnableConfigurationProperties(value = {FilePathConf.class})
@MapperScan(value = {"qichen.code.mapper"})
@SpringBootApplication
public class TraceSourceApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(TraceSourceApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(TraceSourceApplication.class);
    }
}
