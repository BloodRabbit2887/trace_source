package qichen.code.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.util.CollectionUtils;
import qichen.code.entity.RoleMenu;
import qichen.code.entity.dto.RoleMenuDTO;
import qichen.code.mapper.RoleMenuMapper;
import qichen.code.service.IRoleMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import qichen.code.utils.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 角色菜单表 服务实现类
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-11-23
 */
@Service
public class RoleMenuServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenu> implements IRoleMenuService {

    @Override
    public List<RoleMenu> listByRoleId(Integer roleId) {
        QueryWrapper<RoleMenu> wrapper = new QueryWrapper<>();
        wrapper.eq("RoleID",roleId);
        return this.list(wrapper);
    }

    @Override
    public List<RoleMenuDTO> listDtoByRoleId(Integer roleId) {
        List<RoleMenu> roleMenus = listByRoleId(roleId);
        return BeanUtils.copyAs(roleMenus, RoleMenuDTO.class);
    }

    @Override
    public void add(RoleMenuDTO roleMenuDTO) {
        Integer roleId = roleMenuDTO.getRoleId();
        Integer[] menuIds = roleMenuDTO.getMenuIds();
        String[] checkStates = roleMenuDTO.getCheckStates();

        List<RoleMenu> roleMenus = listByRoleId(roleId);
        if (!CollectionUtils.isEmpty(roleMenus) && roleMenus.size()>0){
            this.removeByIds(roleMenus.stream().map(RoleMenu::getRoleID).collect(Collectors.toList()));
        }

        int i = 0;
        for (Integer menuId : menuIds) {
            RoleMenu adminMenu = new RoleMenu();
            adminMenu.setRoleID(roleId);
            adminMenu.setMenuID(menuId);
            adminMenu.setCheckState(checkStates[i]);
            this.save(adminMenu);
            i++;
        }
    }
}
