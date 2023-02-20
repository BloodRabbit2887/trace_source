package qichen.code.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NeedOrderModel {
    private String number;
    private LocalDateTime createTime;
    private String createName;
}
