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
 * 合金组装组工作检查表(装配车间)
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-01-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_assemble_check_package")
public class AssembleCheckPackage implements Serializable {

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
     * 0未完成 1已完成
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
     * 模号
     */
    @TableField("`number`")
    private String number;

    /**
     * 0非草稿 1草稿
     */
    @TableField("`draft`")
    private Integer draft;

    /**
     * 审核人ID
     */
    @TableField("verifyId")
    private Integer verifyId;

    /**
     * 审核状态 0待审核 1已审核 2未通过
     */
    @TableField("verifyStatus")
    private Integer verifyStatus;

    /**
     * 审核备注
     */
    @TableField("verifyRemark")
    private String verifyRemark;

    /**
     * 审核时间
     */
    @TableField("verifyTime")
    private LocalDateTime verifyTime;

    /**
     * 板件装配者
     */
    @TableField("assembleName")
    private String assembleName;

    /**
     * 领班
     */
    @TableField("foremanName")
    private String foremanName;

    /**
     * 质检
     */
    @TableField("checkName")
    private String checkName;


}
