package qichen.code.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 电机表
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-12-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_electric_type")
public class ElectricType implements Serializable {

private static final long serialVersionUID=1L;

    /**
     * ID
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 名称
     */
    @TableField("`title`")
    private String title;

    /**
     * 状态 0正常 1锁定
     */
    @TableField("Status")
    private Integer Status;

    /**
     * 排序
     */
    @TableField("`orders`")
    private Integer orders;

    /**
     * 创建时间
     */
    @TableField("createTime")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("updateTime")
    private LocalDateTime updateTime;

    /**
     * 备注
     */
    @TableField("`remark`")
    private String remark;

    /**
     * 0未删除 1已删除
     */
    @TableField("Deltf")
    @TableLogic
    private Integer Deltf;


}
