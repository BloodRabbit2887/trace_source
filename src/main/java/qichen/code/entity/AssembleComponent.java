package qichen.code.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 装配车间检测部件表
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-01-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_assemble_component")
public class AssembleComponent implements Serializable {

private static final long serialVersionUID=1L;

    /**
     * ID
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 名称
     */
    @TableField("`name`")
    private String name;

    /**
     * 检查表类型 1合金组组装工作检查表 2模架组导槽板点检表 3下模座垫板工作检查表 4合金组装组工作检查表 5模座组装组工作检查表 6合金组装组工作检查表
     */
    @TableField("checkType")
    private Integer checkType;

    /**
     * 状态 0正常 1锁定
     */
    @TableField("`Status`")
    private Integer Status;

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
     * 备注
     */
    @TableField("`remark`")
    private String remark;

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
