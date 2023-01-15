package qichen.code.service;

import qichen.code.entity.AdminRole;
import com.baomidou.mybatisplus.extension.service.IService;
import qichen.code.entity.dto.AdminRoleDTO;

/**
 * <p>
 * 人员角色表 服务类
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-11-23
 */
public interface IAdminRoleService extends IService<AdminRole> {

    AdminRole getByAdmin(Integer id);

    void addDto(AdminRoleDTO adminRoleDTO);

}
