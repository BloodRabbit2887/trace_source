package qichen.code.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 参数表
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-11-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_parameter")
public class Parameter implements Serializable {

private static final long serialVersionUID=1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 连锁ID
     */
    @TableField("StoreID")
    private Integer StoreID;

    /**
     * 参数名
     */
    @TableField("ParamName")
    private String ParamName;

    /**
     * 参数值
     */
    @TableField("ParamValue")
    private String ParamValue;

    /**
     * 本地化
     */
    @TableField("Locale")
    private String Locale;

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
     * 扩展字段1
     */
    @TableField("EXT1")
    private String ext1;

    @TableField("EXT2")
    private String ext2;

    /**
     * 更新内容
     */
    @TableField("EXT3")
    private String ext3;

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

    public Parameter(boolean b) {
        if (b) {
            this.Status = 1;
            this.DelTF = 0;
            this.CreateTime = LocalDateTime.now();
        }
    }
    public Parameter() {
    }

}
