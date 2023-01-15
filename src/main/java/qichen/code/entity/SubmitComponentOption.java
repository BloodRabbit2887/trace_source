package qichen.code.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 装配车间部件检测结果表
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-01-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_submit_component_option")
public class SubmitComponentOption implements Serializable {

private static final long serialVersionUID=1L;

    /**
     * ID
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 检测项目ID
     */
    @TableField("optionId")
    private Integer optionId;

    /**
     * 检测表ID
     */
    @TableField("checkTableId")
    private Integer checkTableId;

    /**
     * 实测及判定
     */
    @TableField("realCheck")
    private String realCheck;

    /**
     * 备注
     */
    @TableField("`remark`")
    private String remark;


}
