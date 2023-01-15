package qichen.code.model;

import lombok.Data;

import java.util.List;

@Data
public class LinkModel {

    private List<Link> links;

    @Data
    public static class Link{
        private Integer orders;
        private String deptName;
        private String userName;
        private String submitTime;
        private Integer status;
    }
}
