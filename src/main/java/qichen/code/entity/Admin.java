package qichen.code.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 管理员表
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-11-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_admin")
public class Admin implements Serializable {

private static final long serialVersionUID=1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 管理员编号
     */
    @TableField("AdminNO")
    private String AdminNO;

    /**
     * 管理员名称
     */
    @TableField("AdminName")
    private String AdminName;

    /**
     * 管理员类型:1,平台;2,代理商;3,连锁店;4,门店
     */
    @TableField("AdminType")
    private Integer AdminType;

    @TableField("SaltValue")
    private String SaltValue;

    @TableField("Password")
    private String Password;

    /**
     * 关联ID
     */
    @TableField("RelatedID")
    private Integer RelatedID;

    /**
     * 状态:0,锁定;1,正常
     */
    @TableField("Status")
    private Integer Status;

    /**
     * 是否删除:1,删除;0,未删除
     */
    @TableField("DelTF")
    @TableLogic
    private Integer DelTF;

    /**
     * 创建时间
     */
    @TableField("CreateTime")
    private LocalDateTime CreateTime;

    /**
     * 更新时间
     */
    @TableField("UpdateTime")
    private LocalDateTime UpdateTime;


}
