package qichen.code.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 质量管理部工单内文件表
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-12-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_quality_order_file")
public class QualityOrderFile implements Serializable {

private static final long serialVersionUID=1L;

    /**
     * ID
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 标题
     */
    @TableField("`title`")
    private String title;

    /**
     * 质管工单ID
     */
    @TableField("qualityOrderID")
    private Integer qualityOrderID;

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

    /**
     * 类型 1下模配磨检查记录 2配磨导正钉检查记录 3成型尺寸凸凹模检测记录 4关键零部件特采（是否有特采评审单）放行记录（不良尺寸描述和特采放行原因)
     */
    @TableField("`type`")
    private Integer type;

    /**
     * pdf文件路径
     */
    @TableField("pdfLink")
    private String pdfLink;

    /**
     * 图片文件路径
     */
    @TableField("imgLink")
    private String imgLink;

    /**
     * 版本号
     */
    @TableField("`version`")
    private BigDecimal version;

    @TableField("`newVersion`")
    private Integer newVersion;

}
