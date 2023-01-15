package qichen.code.exception;

public enum ResException {

    ADMIN_LOGIN_MISS(101,"管理员未登录"),
    ADMIN_PER_MISS(102, "管理员权限不足！"),
    ADMIN_LOGIN_ERR(103, "用户名不存在或密码错误"),
    ADMIN_ALREADY(104,"该管理员账号已被注册"),




    USER_MISS(201,"登录已过期,请重新登录"),
    USER_DETAIL_ERR(202,"用户信息异常"),
    USER_PER_MISS(203, "用户权限不足！"),
    USER_MOBILE_ALREADY(204,"手机号已被使用"),
    USER_MOBILE_ERR(205,"用户账户不存在"),
    USER_PASS_ERR(206,"密码错误"),
    USER_BE_RES(207,"请先注册"),
    USER_DETAIL_MISS(207,"获取用户信息失败"),
    USER_LOGIN_PASS(210,"密码错误"),
    USER_LOCK(211,"账户锁定中,请联系管理员"),

    SMS_SAND_ERR(301,"短信发送失败"),
    CODE_NULL(302,"请输入验证码"),
    SMS_OVERDUE(303,"验证码已过期"),
    SMS_MISS(304,"验证码错误"),
    PASS_NULL(305,"请输入密码"),

    STORE_ALREADY(401,"不可多次申请"),

    ORDER_USER_ALREADY(501,"请勿给自己下单"),

    PARMA_ALREADY(901,"参数信息已存在"),
    PARMA_ERR(902,"参数错误"),
    PARMA_MISS(903,"参数信息不存在"),

    UPDATE_ERR(1401,"修改失败"),
    CANCEL_ERR(1402,"取消失败"),
    QUERY_MISS(1403,"查询失败"),
    NOT_NULL(1404,"不能为空"),
    IS_NULL(1405,"不存在的"),
    IS_DEL(1406,"已删除"),
    ADD_ERR(1407,"添加失败"),
    MAKE_ERR(1408,"操作失败"),
    DEL_ERR(1409,"删除失败"),
    SYSTEM_ERR(1410,"系统错误"),

    SIGN_ALREADY(1501,"您已签到"),

    NET_TIME_OUT(1601,"网络超时"),

    MENU_PARENT_DEL(1801,"无上级菜单或上级菜单被删除！"),
    MENU_PARENT_NULL(1802,"父级列表不存在");
    private Integer code;

    private String message;

    ResException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
