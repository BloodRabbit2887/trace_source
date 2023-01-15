package qichen.code.controller.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "filepath")
public class FilePathConf {
    private String question_local;
    private String question_net;
}
