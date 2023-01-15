package qichen.code.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SearchModel {
    private LocalDateTime loginTime;
    private Integer count;
}
