package qichen.code.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 模具安装调试服务报告单
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-01-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_model_install")
public class ModelInstall implements Serializable {

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
     * 模具名称
     */
    @TableField("modelTitle")
    private String modelTitle;

    /**
     * 客户名称
     */
    @TableField("customName")
    private String customName;

    /**
     * 服务时间
     */
    @TableField("installTime")
    private LocalDateTime installTime;

    /**
     * 详细地址
     */
    @TableField("`address`")
    private String address;

    /**
     * 到达时间
     */
    @TableField("getToTime")
    private LocalDateTime getToTime;

    /**
     * 离开时间
     */
    @TableField("offTime")
    private LocalDateTime offTime;

    /**
     * 联系人
     */
    @TableField("acceptName")
    private String acceptName;

    /**
     * 联系电话
     */
    @TableField("acceptMobile")
    private String acceptMobile;

    /**
     * 出厂时间
     */
    @TableField("outTime")
    private LocalDateTime outTime;

    /**
     * 工作类型 0新模具安装调试 1旧模维修 2模具保养 3技术示意/培训
     */
    @TableField("jobType")
    private Integer jobType;

    /**
     * 客户冲床型号
     */
    @TableField("punchTpye")
    private String punchTpye;

    /**
     * 冲床平行度
     */
    @TableField("punchSize")
    private String punchSize;

    /**
     * 客户矫平机使用情况 0校平能力良好 1校平能力一般 2校平能力较差 3使用但无校平能力
     */
    @TableField("levellerStatus")
    private Integer levellerStatus;

    /**
     * 工作内容拓展1
     */
    private String ext1;

    /**
     * 工作内容拓展2
     */
    private String ext2;

    /**
     * 工作内容拓展3
     */
    private String ext3;

    /**
     * 工作内容拓展4
     */
    private String ext4;

    /**
     * 模具经安装调试后状态 0正常冲压 1异常冲压 2无法使用
     */
    @TableField("testStatus")
    private Integer testStatus;

    /**
     * 试模结果判定-冲床平行度
     */
    @TableField("testPunchSize")
    private String testPunchSize;

    /**
     * 本次调试总冲次
     */
    @TableField("testPunchCount")
    private String testPunchCount;

    /**
     * 模具经调试后仍存在的问题
     */
    @TableField("testErr")
    private String testErr;

    /**
     * 是否需要我司提供后续改善措施 0需要 1不需要
     */
    @TableField("testNeeds")
    private Integer testNeeds;

    /**
     * 后续改善方式为 0派人上门 1客户自行维修
     */
    @TableField("outMethod")
    private Integer outMethod;

    /**
     * 后续改善日期
     */
    @TableField("outMethodTime")
    private LocalDateTime outMethodTime;

    /**
     * 本次试模结果判定 0合格 1不合格 2可用,部分需改善
     */
    @TableField("outResult")
    private Integer outResult;

    /**
     * 回访方式 0电子邮件 1电话
     */
    @TableField("returnType")
    private Integer returnType;

    /**
     * 被回访人员
     */
    @TableField("returnName")
    private String returnName;

    /**
     * 被回访人员职务
     */
    @TableField("returnJob")
    private String returnJob;

    /**
     * 回访内容记录
     */
    @TableField("returnDetail")
    private String returnDetail;

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
    @TableLogic
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
     * 审核时间
     */
    @TableField("verifyTime")
    private LocalDateTime verifyTime;



}
