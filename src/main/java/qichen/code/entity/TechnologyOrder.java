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
 * 工艺部工单表
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-12-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_technology_order")
public class TechnologyOrder implements Serializable {

private static final long serialVersionUID=1L;

    /**
     * ID
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 工单ID
     */
    @TableField("orderID")
    private Integer orderID;

    /**
     * 状态 0未开始 1进行中 2已完成 3已结束
     */
    @TableField("Status")
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
     * 慢丝工时
     */
    @TableField("wedmLsTime")
    private BigDecimal wedmLsTime;

    /**
     * 快丝工时
     */
    @TableField("wedmHsTime")
    private BigDecimal wedmHsTime;

    /**
     * 曲磨工时
     */
    @TableField("flexTime")
    private BigDecimal flexTime;

    /**
     * 型磨工时
     */
    @TableField("grinderTime")
    private BigDecimal grinderTime;

    /**
     * 外圆磨工时
     */
    @TableField("cylindricalTime")
    private BigDecimal cylindricalTime;

    /**
     * 标磨工时
     */
    @TableField("modelTime")
    private BigDecimal modelTime;

    /**
     * 精镗铣工时
     */
    @TableField("jtxTime")
    private BigDecimal jtxTime;

    /**
     * 小型加工中心工时
     */
    @TableField("smallTime")
    private BigDecimal smallTime;

    /**
     * 龙门加工中心工时
     */
    @TableField("dragonTime")
    private BigDecimal dragonTime;

    /**
     * 创建人ID
     */
    @TableField("submitId")
    private Integer submitId;

    /**
     * 模号
     */
    @TableField("`number`")
    private String number;

    @TableField("`draft`")
    private Integer draft;

    @TableField("verifyId")
    private Integer verifyId;

    @TableField("`verifyStatus`")
    private Integer verifyStatus;

    @TableField("verifyRemark")
    private String verifyRemark;

    @TableField("verifyTime")
    private LocalDateTime verifyTime;

}
