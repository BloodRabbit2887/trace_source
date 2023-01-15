package qichen.code.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 工单表
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-12-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_work_order")
public class WorkOrder implements Serializable {

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
     * 客户ID
     */
    @TableField("customId")
    private Integer customId;

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
    @TableLogic
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
     * 开始时间
     */
    @TableField("startTime")
    private LocalDateTime startTime;

    /**
     * 截止时间
     */
    @TableField("expireTime")
    private LocalDateTime expireTime;

    /**
     * 上门安装/维修时间
     */
    @TableField("maintenanceTime")
    private LocalDateTime maintenanceTime;

    /**
     * 备注
     */
    @TableField("`remark`")
    private String remark;

    /**
     * 二维码
     */
    @TableField("`qrcode`")
    private String qrcode;

    /**
     * 1单列 2双列 3三列 4多列
     */
    @TableField("`count`")
    private Integer count;

    /**
     * 电机类型ID
     */
    @TableField("electricTypeId")
    private Integer electricTypeId;

    /**
     * 模具类型ID
     */
    @TableField("modelTypeId")
    private Integer modelTypeId;

    /**
     * 业务员ID
     */
    @TableField("saleId")
    private Integer saleId;

    /**
     * 模具名称
     */
    @TableField("modelTitle")
    private String modelTitle;

    /**
     * 技术协议PDF文件
     */
    @TableField("tecPDF")
    private String tecPDF;

    /**
     * 技术协议img文件
     */
    @TableField("tecImg")
    private String tecImg;

    /**
     * 部门ID
     */
    @TableField("deptId")
    private Integer deptId;

    /**
     * 部门环节完成状态 1未完成 2已完成
     */
    @TableField("deptStatus")
    private Integer deptStatus;

    @TableField("`draft`")
    private Integer draft;

    @TableField("verifyRemark")
    private String verifyRemark;

    @TableField("saleStatus")
    private Integer saleStatus;

    @TableField("saleRemark")
    private String saleRemark;

    @TableField("customMobile")
    private String customMobile;
}
