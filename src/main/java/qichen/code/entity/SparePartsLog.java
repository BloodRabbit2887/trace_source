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
 * 零件检测报告表
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-12-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_spare_parts_log")
public class SparePartsLog implements Serializable {

private static final long serialVersionUID=1L;

    /**
     * ID
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 模号
     */
    @TableField("`number`")
    private String number;

    /**
     * 部号
     */
    @TableField("partNumber")
    private String partNumber;

    /**
     * 零件名称
     */
    private String name;

    /**
     * 数量
     */
    @TableField("`count`")
    private Integer count;

    /**
     * 表名有无生锈，有无崩角 1是 2否
     */
    private Integer view1;

    /**
     * 表面粗糙度是否符合要求 1是 2否
     */
    private Integer view2;

    /**
     * 材质
     */
    private String view3;

    /**
     * 硬度
     */
    private BigDecimal view4;


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

    @TableField("`draft`")
    private Integer draft;

    @TableField("verifyId")
    private Integer verifyId;

    @TableField("verifyStatus")
    private Integer verifyStatus;

    @TableField("verifyRemark")
    private String verifyRemark;

    @TableField("verifyTime")
    private LocalDateTime verifyTime;

}
