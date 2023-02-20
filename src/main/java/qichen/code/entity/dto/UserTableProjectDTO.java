package qichen.code.entity.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import qichen.code.entity.UserTableProject;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserTableProjectDTO extends UserTableProject {

    private String userName;
    private String submitName;
    private String tableName;
    private String deptName;
}
