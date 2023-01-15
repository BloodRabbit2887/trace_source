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
 * 操作记录表
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-11-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_operation_log")
public class OperationLog implements Serializable {

private static final long serialVersionUID=1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 对象类型
     */
    @TableField("ObjectType")
    private Integer ObjectType;

    /**
     * 对象ID
     */
    @TableField("ObjectID")
    private Integer ObjectID;

    /**
     * 操作
     */
    @TableField("Operation")
    private String Operation;

    /**
     * 备注
     */
    @TableField("Remark")
    private String Remark;

    /**
     * 创建时间
     */
    @TableField("CreateTime")
    private LocalDateTime CreateTime;

    /**
     * 关联表
     */
    @TableField("AssociationTable")
    private String AssociationTable;

    /**
     * 关联ID
     */
    @TableField("AssociationID")
    private Integer AssociationID;

    /**
     * 备注
     */
    @TableField("DataStr")
    private String DataStr;


}
