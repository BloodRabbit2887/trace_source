package qichen.code.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 菜单表
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-12-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_menu")
public class Menu implements Serializable {

private static final long serialVersionUID=1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 上级ID
     */
    @TableField("ParentID")
    private Integer ParentID;

    /**
     * 所属管理员类型
     */
    @TableField("AdminTypes")
    private String AdminTypes;

    @TableField("Title")
    private String Title;

    @TableField("ComponentText")
    private String ComponentText;

    @TableField("Path")
    private String Path;

    @TableField("Name")
    private String Name;

    /**
     * 菜单里包含的API列表
     */
    @TableField("ApiUrl")
    private String ApiUrl;

    @TableField("Icon")
    private String Icon;

    /**
     * 权限归属
     */
    @TableField("OwnerShip")
    private String OwnerShip;

    /**
     * 菜单等级
     */
    @TableField("MenuLevel")
    private Integer MenuLevel;

    @TableField("MenuType")
    private String MenuType;

    /**
     * 排序
     */
    @TableField("OrderBy")
    private Integer OrderBy;

    @TableField("Status")
    private String Status;

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
