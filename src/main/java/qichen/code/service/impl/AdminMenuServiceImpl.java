package qichen.code.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.util.CollectionUtils;
import qichen.code.entity.AdminMenu;
import qichen.code.entity.dto.AdminMenuDTO;
import qichen.code.mapper.AdminMenuMapper;
import qichen.code.service.IAdminMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 管理员菜单表 服务实现类
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-11-23
 */
@Service
public class AdminMenuServiceImpl extends ServiceImpl<AdminMenuMapper, AdminMenu> implements IAdminMenuService {

    @Override
    public List<AdminMenu> listByAdminId(Integer adminId) {
        QueryWrapper<AdminMenu> wrapper = new QueryWrapper<>();
        wrapper.eq("AdminID",adminId);
        return this.list(wrapper);
    }


    @Override
    public List<AdminMenu> listByFliter(AdminMenuDTO fil) {
        QueryWrapper<AdminMenu> wrapper =new QueryWrapper<>();
        wrapper.eq("AdminID",fil.getAdminId());
        return this.list(wrapper);
    }

    @Override
    public void add(AdminMenuDTO adminMenuDTO) {
        Integer adminId = adminMenuDTO.getAdminId();
        Integer[] menuIds = adminMenuDTO.getMenuIds();
        String[] checkStates = adminMenuDTO.getCheckStates();
        AdminMenuDTO filter = new AdminMenuDTO();
        filter.setAdminId(adminId);
        List<AdminMenu> adminMenus = listByFliter(filter);
        if (!CollectionUtils.isEmpty(adminMenus) && adminMenus.size()>0){
            List<Integer> adminIds = adminMenus.stream().map(AdminMenu::getAdminID).collect(Collectors.toList());
            this.removeByIds(adminIds);
        }
        int i = 0;
        List<AdminMenu> list = new ArrayList<>();
        for (Integer menuId : menuIds) {
            AdminMenu adminMenu = new AdminMenu();
            adminMenu.setAdminID(adminId);
            adminMenu.setMenuID(menuId);
            adminMenu.setCheckState(checkStates[i]);
            list.add(adminMenu);
            i++;
        }
        this.saveBatch(list);
    }

}

