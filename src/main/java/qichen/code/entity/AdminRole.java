package qichen.code.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 人员角色表
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-11-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_admin_role")
public class AdminRole implements Serializable {

private static final long serialVersionUID=1L;

    /**
     * 管理员ID
     */
    @TableId("AdminID")
    private Integer AdminID;

    /**
     * 角色ID
     */
    @TableField("RoleID")
    private Integer RoleID;


}
