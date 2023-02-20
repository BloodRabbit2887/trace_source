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
 * 装配车间额外表单
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-02-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_assemble_other")
public class AssembleOther implements Serializable {

private static final long serialVersionUID=1L;

    /**
     * ID
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 表单类型 1模架组装组工作检查表 4 合金组装组工作检查表 6 模具入库点检表
     */
    @TableField("tableType")
    private Integer tableType;

    /**
     * 表单ID
     */
    @TableField("orderId")
    private Integer orderId;

    /**
     * 模号
     */
    @TableField("`number`")
    private String number;

    /**
     * 模具名称
     */
    @TableField("modelName")
    private String modelName;

    /**
     * 调试项目
     */
    @TableField("`item`")
    private String item;

    /**
     * 不良问题点ID列表
     */
    @TableField("errIds")
    private String errIds;

    /**
     * 不良描述
     */
    @TableField("errDetail")
    private String errDetail;

    /**
     * 最终解决方案
     */
    @TableField("fastIdea")
    private String fastIdea;

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


}
