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
 * 合金组装组扭转部位工作检查表(装配车间)
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-01-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_assemble_model_push_package")
public class AssembleModelPushPackage implements Serializable {

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
     * 导正钉高出卸料板有效导正部分为(mm)
     */
    private String ext1;

    /**
     * 凸模高度实测值(mm)
     */
    private String ext2;

    /**
     * 凹固板厚度实测值(mm)
     */
    private String ext3;

    /**
     * 转子扭转同轴度实测值为L列(mm)
     */
    private String ext4;

    /**
     * 转子扭转同轴度实测值为R列(mm)
     */
    private String ext5;

    /**
     * 转子扭转同轴度实测值为C列(mm)
     */
    private String ext6;

    /**
     * 转子扭转轴套上下窜动间隙为L列(mm)
     */
    private String ext7;

    /**
     * 转子扭转轴套上下窜动间隙为R列(mm)
     */
    private String ext8;

    /**
     * 转子扭转轴套上下窜动间隙为C列(mm)
     */
    private String ext9;

    /**
     * 推管力实测（要求：∅4-∅6 2-2.5kg，∅8-∅12 3-3.5kg）
     */
    private String ext10;

    /**
     * 扣点关系示意图(dwg)
     */
    @TableField("dwgFile")
    private String dwgFile;


}
