package qichen.code.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DistributionProjectDTO {

    private String number;
    private String createUserName;
    private LocalDateTime createTime;
    private Integer tableType;
    private String tableName;
}
