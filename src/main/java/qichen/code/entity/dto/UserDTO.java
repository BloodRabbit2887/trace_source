package qichen.code.entity.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import qichen.code.entity.User;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserDTO extends User {

    private String deptName;
    private String typeName;
}
