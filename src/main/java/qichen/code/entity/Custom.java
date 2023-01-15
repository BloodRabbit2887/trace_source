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
 * 客户管理表
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-12-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_custom")
public class Custom implements Serializable {

private static final long serialVersionUID=1L;

    /**
     * ID
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 客户名称/公司名称
     */
    @TableField("`name`")
    private String name;

    /**
     * 联系人姓名
     */
    @TableField("acceptName")
    private String acceptName;

    /**
     * 联系人电话
     */
    @TableField("acceptMobile")
    private String acceptMobile;

    /**
     * 详细地址
     */
    @TableField("`address`")
    private String address;

    /**
     * 创建时间
     */
    @TableField("createTime")
    private LocalDateTime createTime;


}
