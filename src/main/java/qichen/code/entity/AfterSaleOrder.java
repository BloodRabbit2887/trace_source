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
 * 维修工单表
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-01-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_after_sale_order")
public class AfterSaleOrder implements Serializable {

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
     * 故障名称
     */
    @TableField("`title`")
    private String title;

    /**
     * 故障详情
     */
    @TableField("`detail`")
    private String detail;

    /**
     * 故障照片/视频
     */
    @TableField("`views`")
    private String views;

    /**
     * 维修措施
     */
    @TableField("`measure`")
    private String measure;

    /**
     * 配件ID
     */
    @TableField("partsId")
    private Integer partsId;

    /**
     * 状态 0未结束 1已结束 2已取消
     */
    @TableField("`Status`")
    private Integer Status;

    /**
     * 审核状态 0待审核 1已审核 2未通过
     */
    @TableField("verifyStatus")
    private Integer verifyStatus;

    /**
     * 审核人ID
     */
    @TableField("verifyId")
    private Integer verifyId;

    /**
     * 审核人类别 1管理员 2用户
     */
    @TableField("verifyType")
    private Integer verifyType;

    /**
     * 创建人ID
     */
    @TableField("submitId")
    private Integer submitId;

    /**
     * 创建人类别 1管理员 2用户
     */
    @TableField("submitType")
    private Integer submitType;

    /**
     * 审核时间
     */
    @TableField("verifyTime")
    private LocalDateTime verifyTime;

    /**
     * 0未删除 1已删除
     */
    @TableField("Deltf")
    private Integer Deltf;

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
     * 是否为草稿 0否 1是
     */
    @TableField("`draft`")
    private Integer draft;

    /**
     * 审核备注
     */
    @TableField("verifyRemark")
    private String verifyRemark;

    /**
     * 管理员备注
     */
    @TableField("saleRemark")
    private String saleRemark;


}
