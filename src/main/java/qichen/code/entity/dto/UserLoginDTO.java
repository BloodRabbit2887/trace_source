package qichen.code.entity.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import qichen.code.entity.UserLogin;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserLoginDTO extends UserLogin {

    private String userName;
    private String deptName;
    private Integer deptId;

}