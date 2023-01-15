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
 * 模具入库点检事项提交表 (装配车间)
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-01-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_submit_model_push_option")
public class SubmitModelPushOption implements Serializable {

private static final long serialVersionUID=1L;

    /**
     * ID
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 提交内容 0是 1否 2无此项
     */
    @TableField("`answer`")
    private Integer answer;

    /**
     * 模具入库点检表Id
     */
    @TableField("packageId")
    private Integer packageId;

    /**
     * 0正常 1锁定
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

    @TableField("`optionId`")
    private Integer optionId;

}
