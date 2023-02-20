package qichen.code.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 设计部工单表
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-12-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_devise_order")
public class DeviseOrder implements Serializable {

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
     * 产品大小 mm
     */
    @TableField("`size`")
    private Integer size;

    /**
     * 定子叠铆类型ID
     */
    @TableField("statorId")
    private Integer statorId;

    /**
     * 转子叠铆类型ID
     */
    @TableField("rotorId")
    private Integer rotorId;

    /**
     * 驱动类型ID
     */
    @TableField("driveTypeId")
    private Integer driveTypeId;

    /**
     * 料厚类型ID
     */
    @TableField("tricknessId")
    private Integer tricknessId;

    /**
     * 材料牌号ID
     */
    @TableField("matNumberId")
    private Integer matNumberId;

    /**
     * 搭边情况ID
     */
    @TableField("broderId")
    private Integer broderId;

    /**
     * 出料形式ID
     */
    @TableField("outMetId")
    private Integer outMetId;

    /**
     * 安装图和排样图涵盖气管及批量接线图（PDF格式）
     */
    private String pdf1;

    /**
     * 安装图和排样图涵盖气管及批量接线图（img格式）
     */
    private String img1;

    /**
     * 产品图列表（PDF格式）
     */
    private String pdf2;

    /**
     * 产品图列表（img格式）
     */
    private String img2;

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


    @TableField("verifyId")
    private Integer verifyId;

    @TableField("verifyStatus")
    private Integer verifyStatus;

    @TableField("`draft`")
    private Integer draft;

    @TableField("verifyRemark")
    private String verifyRemark;

    @TableField("`verifyTime`")
    private LocalDateTime verifyTime;

    @TableField("fileName1")
    private String fileName1;

    @TableField("fileName2")
    private String fileName2;
}
