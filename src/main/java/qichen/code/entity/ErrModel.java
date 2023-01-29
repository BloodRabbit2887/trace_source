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
 * 错误典型库表
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-01-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_err_model")
public class ErrModel implements Serializable {

private static final long serialVersionUID=1L;

    /**
     * ID
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 错误类型ID
     */
    @TableField("typeId")
    private Integer typeId;

    /**
     * 模号
     */
    @TableField("`number`")
    private String number;

    /**
     * 错误标题
     */
    @TableField("`title`")
    private String title;

    /**
     * 问题描述
     */
    @TableField("`detail`")
    private String detail;

    /**
     * 临时措施
     */
    @TableField("`temporary`")
    private String temporary;

    /**
     * 长期措施
     */
    @TableField("longTerm")
    private String longTerm;

    /**
     * 状态 0未结束 1已结束 2已取消
     */
    @TableField("`Status`")
    private Integer status;

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

    @TableField("serverRemark")
    private String serverRemark;

}
