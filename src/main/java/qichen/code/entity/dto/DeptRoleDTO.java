package qichen.code.entity.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import qichen.code.entity.DeptRole;

@EqualsAndHashCode(callSuper = true)
@Data
public class DeptRoleDTO extends DeptRole {

    private String deptName;
}
