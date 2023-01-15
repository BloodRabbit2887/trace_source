package qichen.code.service;

import qichen.code.entity.Role;
import com.baomidou.mybatisplus.extension.service.IService;
import qichen.code.entity.dto.RoleDTO;

import java.util.List;

/**
 * <p>
 * 角色表 服务类
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-11-23
 */
public interface IRoleService extends IService<Role> {

    List<RoleDTO> listByPage(RoleDTO filter, Integer page, Integer pageSize);

    List<Role> listPage(RoleDTO filter, Integer page, Integer pageSize);

    int listCount(RoleDTO filter);

    RoleDTO get(Integer id);

    void add(RoleDTO roleDTO);

    void updateDto(RoleDTO roleDTO);
}
