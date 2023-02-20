package qichen.code.entity.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import qichen.code.entity.User;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserDTO extends User {

    private String deptName;
    private String typeName;

    private String deptRoleName;

    private Integer deptRoleLevel;

    private Integer createAllOrderPermission;

    private Integer distributionPermission;

    private Integer verifyPer;

    private Integer linkChangePermission;

    private Integer updatePermission;

    private String token;
}
