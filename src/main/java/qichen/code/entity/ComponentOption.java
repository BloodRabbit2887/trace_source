package qichen.code.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 装配车间部件检测项目表
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-01-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_component_option")
public class ComponentOption implements Serializable {

private static final long serialVersionUID=1L;

    /**
     * ID
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 部件ID
     */
    @TableField("componentId")
    private Integer componentId;

    /**
     * 序号
     */
    @TableField("`number`")
    private Integer number;

    /**
     * 检验内容
     */
    @TableField("`detail`")
    private String detail;

    /**
     * 装配要求（图纸要求）
     */
    @TableField("`needs`")
    private String needs;

    /**
     * 状态 0正常 1锁定
     */
    @TableField("`Status`")
    private Integer Status;

    /**
     * 0未删除 1已删除
     */
    @TableField("Deltf")
    @TableLogic
    private Integer Deltf;

    /**
     * 排序
     */
    @TableField("`orders`")
    private Integer orders;

    /**
     * 备注
     */
    @TableField("`remark`")
    private String remark;

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


}
