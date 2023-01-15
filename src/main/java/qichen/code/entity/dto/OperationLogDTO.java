package qichen.code.entity.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import qichen.code.entity.OperationLog;


import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class OperationLogDTO extends OperationLog {
    private String adminName;
    private String adminNO;

    public static final Map<Integer,String> ABOUT_TABLE = new HashMap<>();

    private static final Integer USER_TABLE = 1;//用户
    private static final Integer DEPT_TABLE = 2;//部门

    static {
        ABOUT_TABLE.put(USER_TABLE,"t_user");
        ABOUT_TABLE.put(DEPT_TABLE,"t_dept");
    }
}
