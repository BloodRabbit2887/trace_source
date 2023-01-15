package qichen.code.service;

import qichen.code.entity.RoleMenu;
import com.baomidou.mybatisplus.extension.service.IService;
import qichen.code.entity.dto.RoleMenuDTO;

import java.util.List;

/**
 * <p>
 * 角色菜单表 服务类
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-11-23
 */
public interface IRoleMenuService extends IService<RoleMenu> {

    List<RoleMenu> listByRoleId(Integer roleId);

    List<RoleMenuDTO> listDtoByRoleId(Integer roleId);

    void add(RoleMenuDTO roleMenuDTO);

}
