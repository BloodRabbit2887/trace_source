package qichen.code.model;

import lombok.Data;

@Data
public class DistributionStatusDTO {

    private String number;//模号
    private Integer tableType;//表单类型
    private String tableName;//表单名称
    private Integer userId;//分配用户ID
    private Integer status;//状态 0未完成 1待审核 2已审核 3已完成
    private Integer deptId;//部门ID
    private String userName;//用户名
    private String submitName;//分配人名称

}
