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
 * 零件检测尺寸特性表
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-12-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_model_check_log")
public class ModelCheckLog implements Serializable {

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
     * 外形规格
     */
    @TableField("shapeSku")
    private String shapeSku;

    /**
     * 外形规格备注
     */
    @TableField("shapeSkuRemark")
    private String shapeSkuRemark;

    /**
     * 上模净重
     */
    @TableField("upWeight")
    private BigDecimal upWeight;

    /**
     * 上模净重备注
     */
    @TableField("upWeightRemark")
    private String upWeightRemark;

    /**
     * 下模净重
     */
    @TableField("downWeight")
    private BigDecimal downWeight;

    /**
     * 下模净重备注
     */
    @TableField("downWeightRemark")
    private String downWeightRemark;

    /**
     * 合模高度
     */
    @TableField("stantdHeight")
    private BigDecimal stantdHeight;

    /**
     * 合模高度备注
     */
    @TableField("stantdHeightRemark")
    private String stantdHeightRemark;

    /**
     * 送料线高度
     */
    @TableField("lineHeight")
    private BigDecimal lineHeight;

    /**
     * 送料线高度备注
     */
    @TableField("lineHeightRemark")
    private String lineHeightRemark;

    /**
     * 适用料宽
     */
    @TableField("matWidth")
    private BigDecimal matWidth;

    /**
     * 适用料宽备注
     */
    @TableField("matWidthRemark")
    private String matWidthRemark;

    /**
     * 步距
     */
    @TableField("`pace`")
    private BigDecimal pace;

    /**
     * 步距备注
     */
    @TableField("paceSkuRemark")
    private String paceSkuRemark;

    /**
     * 模具磁力强度
     */
    private String magnetic;

    /**
     * 模具磁力强度备注
     */
    @TableField("magneticRemark")
    private String magneticRemark;

    /**
     * 试模冲裁速度
     */
    @TableField("`speed`")
    private BigDecimal speed;

    /**
     * 试模冲裁速度备注
     */
    @TableField("speedRemark")
    private String speedRemark;

    /**
     * 试模冲床型号
     */
    @TableField("punchVersion")
    private String punchVersion;

    /**
     * 试模冲床型号备注
     */
    @TableField("punchVersionRemark")
    private String punchVersionRemark;

    /**
     * 备件种类及数量
     */
    @TableField("spareParesStaus")
    private Integer spareParesStaus;

    /**
     * 备件种类及数量备注
     */
    @TableField("spareParesStausRemark")
    private String spareParesStausRemark;

    /**
     * 外接电机型号
     */
    @TableField("externalVersion")
    private String externalVersion;

    /**
     * 外接电机型号备注
     */
    @TableField("externalVersionRemark")
    private String externalVersionRemark;

    /**
     * 推出气缸型号ID
     */
    @TableField("cylinderId")
    private Integer cylinderId;

    /**
     * 推出气缸型号备注
     */
    @TableField("cylinderRemark")
    private String cylinderRemark;

    /**
     * 冲样检测报告 1齐全 2未齐全
     */
    @TableField("sampleLogStatus")
    private Integer sampleLogStatus;

    /**
     * 冲样检测报告备注
     */
    @TableField("sampleLogStatusRemark")
    private String sampleLogStatusRemark;

    /**
     * 扣点高度关系图 1齐全 2未齐全
     */
    @TableField("pointImgsStatus")
    private Integer pointImgsStatus;

    /**
     * 扣点高度关系图备注
     */
    @TableField("pointImgsStatusRemark")
    private String pointImgsStatusRemark;

    /**
     * 下模步距检测报告 1齐全 2未齐全
     */
    @TableField("downPaceCheckLogs")
    private Integer downPaceCheckLogs;

    /**
     * 下模步距检测报告备注
     */
    @TableField("downPaceCheckLogsRemark")
    private String downPaceCheckLogsRemark;

    /**
     * 上模导正钉步距检测报告 1齐全 2未齐全
     */
    @TableField("upPaceCheckLogs")
    private Integer upPaceCheckLogs;

    /**
     * 上模导正钉步距检测报告备注
     */
    @TableField("upPaceCheckLogsRemark")
    private String upPaceCheckLogsRemark;

    /**
     * 刃口切纸 1齐全 2未齐全
     */
    @TableField("`cutting`")
    private Integer cutting;

    /**
     * 刃口切纸备注
     */
    @TableField("cuttingRemark")
    private String cuttingRemark;

    /**
     * 试冲料条 1齐全 2未齐全
     */
    @TableField("testMats")
    private Integer testMats;

    /**
     * 试冲料条备注
     */
    @TableField("testMatsRemark")
    private String testMatsRemark;

    /**
     * 模具说明书 1齐全 2未齐全
     */
    @TableField("`readme`")
    private Integer readme;

    /**
     * 模具说明书备注
     */
    @TableField("readmeRemark")
    private String readmeRemark;

    /**
     * 电控机构接线图 1齐全 2未齐全
     */
    @TableField("lineImg")
    private Integer lineImg;

    /**
     * 电控机构接线图备注
     */
    @TableField("lineImgRemark")
    private String lineImgRemark;

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
