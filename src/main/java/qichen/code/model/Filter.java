package qichen.code.model;

import lombok.Data;

import java.util.Date;

@Data
public class Filter {
    private String keyword;
    private Integer page;
    private Integer pageSize;
    private String orders;
    private Boolean orderBy;

    private Date createTimeBegin;
    private Date createTimeEnd;
}
