package qichen.code.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 零件检测尺寸特性表
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-12-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_spare_parts_size")
public class SparePartsSize implements Serializable {

private static final long serialVersionUID=1L;

    /**
     * ID
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;


    /**
     * 特征
     */
    @TableField("`detail`")
    private String detail;

    /**
     * 尺寸
     */
    @TableField("`size`")
    private BigDecimal size;

    /**
     * 上公差
     */
    @TableField("upSize")
    private BigDecimal upSize;

    /**
     * 下公差
     */
    @TableField("downSize")
    private BigDecimal downSize;

    /**
     * 实测尺寸
     */
    @TableField("realSize")
    private BigDecimal realSize;

    /**
     * 结论 1是 2否
     */
    @TableField("`answer`")
    private Integer answer;

    /**
     * 检测工具ID
     */
    @TableField("toolId")
    private Integer toolId;

    /**
     * 零件检测报告表ID
     */
    @TableField("logId")
    private Integer logId;

    /**
     * 状态 0正常 1锁定
     */
    @TableField("`Status`")
    private Integer Status;

    /**
     * 排序
     */
    @TableField("`orders`")
    private Integer orders;

    /**
     * 0未删除 1已删除
     */
    @TableField("Deltf")
    @TableLogic
    private Integer Deltf;

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
     * 创建人ID
     */
    @TableField("submitId")
    private Integer submitId;


}
