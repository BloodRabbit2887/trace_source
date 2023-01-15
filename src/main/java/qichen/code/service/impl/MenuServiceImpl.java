package qichen.code.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import qichen.code.entity.*;
import qichen.code.entity.dto.AdminMenuDTO;
import qichen.code.entity.dto.MenuDTO;
import qichen.code.exception.BusinessException;
import qichen.code.exception.ResException;
import qichen.code.mapper.MenuMapper;
import qichen.code.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import qichen.code.utils.BeanUtils;
import qichen.code.utils.ContextUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单表 服务实现类
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-12-16
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements IMenuService {

    @Autowired
    private IAdminMenuService adminMenuService;
    @Autowired
    private IRoleMenuService roleMenuService;
    @Autowired
    private IAdminService adminService;
    @Autowired
    private IAdminRoleService adminRoleService;


    @Override
    public MenuDTO getMenu(MenuDTO filter) {
        Menu menus = this.getById( filter.getId());
        return BeanUtils.copyAs(menus, MenuDTO.class);
    }

    @Override
    public void delete(MenuDTO filter) {
        Menu menu = this.getById(filter.getId());
        MenuDTO menuDTO1 = new MenuDTO();
        menuDTO1.setParentID(filter.getId());
        List<Menu> tMenuList = this.listByFlt(menuDTO1);
        if(null != tMenuList  && tMenuList.size() > 0){
            throw new BusinessException(ResException.DEL_ERR.getCode(), "请先删除他的子列表");
        }
        this.removeById(menu.getId());

    }

    @Override
    public void updateDto(MenuDTO menuDTO) {
        Menu menu = this.getById(menuDTO.getId());

        if(!menu.getParentID().equals(0)){
            Menu menu1 =  this.getById(menu.getParentID());
            if(menu1 == null ){
                throw new BusinessException(ResException.MENU_PARENT_NULL);
            }
        }

        BeanUtils.copyPropertiesIgnoreNull(menuDTO, menu);
        this.updateById(menu);
    }

    @Override
    public void setAccess(MenuDTO filter) {
        MenuDTO ftl = new MenuDTO();
        ftl.setMenuType(filter.getMenuType());
        List<Menu> menus = listByFlt(ftl);

        Set<Integer> checkedSet = new HashSet<>();
        Set<Integer> checkedSet1 = new HashSet<>();

        Integer adminId = filter.getAdminId();
        List<AdminMenu> adminMenus = adminMenuService.listByAdminId(adminId);

        for (AdminMenu adminMenu : adminMenus) {
            checkedSet1.add(adminMenu.getMenuID());

            if ("checked".equals(adminMenu.getCheckState())) {
                checkedSet.add(adminMenu.getMenuID());
            } else if ("indeterminate".equals(adminMenu.getCheckState())) {
                checkedSet1.add(adminMenu.getMenuID());
            }
        }

        Map<String, String> apiUrlMaps = new HashMap<>();

        List<MenuDTO> list = BeanUtils.copyAs(menus, MenuDTO.class);
        for (MenuDTO item : list) {
            if (checkedSet.contains(item.getId())) {
                String apiUrl = item.getApiUrl();
                if (StringUtils.hasText(apiUrl)) {
                    String[] apiUrls = apiUrl.split(";");
                    for (String apiItem : apiUrls) {
                        apiUrlMaps.put(apiItem, "1");
                    }
                }
            } else if (checkedSet1.contains(item.getId())) {
                String apiUrl = item.getApiUrl();
                if (StringUtils.hasText(apiUrl)) {
                    String[] apiUrls = apiUrl.split(";");
                    for (String apiItem : apiUrls) {
                        apiUrlMaps.put(apiItem, "1");
                    }
                }
            }
        }

        ContextUtils.clearAccessMap(adminId);
        ContextUtils.insertAccessMap(adminId, apiUrlMaps);
    }

    private List<Menu> listByFlt(MenuDTO filter) {
        QueryWrapper<Menu> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(filter.getMenuType())) {
            wrapper.eq("menuType", filter.getMenuType());
        }
        if (StringUtils.hasText(filter.getTitle())) {
            wrapper.eq("title", filter.getTitle());
        }
        if (null != filter.getParentID()) {
            wrapper.eq("parentId", filter.getParentID());
        }
        if (filter.getAdminId() != null) {
            List<AdminMenu> adminMenus = adminMenuService.listByAdminId(filter.getAdminId());
            if (!CollectionUtils.isEmpty(adminMenus) && adminMenus.size()>0){
                List<Integer> menuIds = adminMenus.stream().map(AdminMenu::getMenuID).collect(Collectors.toList());
                wrapper.in("ID",menuIds);
            }
        }
        if (filter.getRoleId() != null){
            List<RoleMenu> roleMenus = roleMenuService.listByRoleId(filter.getRoleId());
            if (!CollectionUtils.isEmpty(roleMenus) && roleMenus.size()>0){
                List<Integer> menuIds = roleMenus.stream().map(RoleMenu::getMenuID).collect(Collectors.toList());
                wrapper.in("ID",menuIds);
            }
        }

        wrapper.eq("status", "1");
        wrapper.orderByAsc("orderBy");
        return this.list(wrapper);
    }

    @Override
    public List<MenuDTO> list2(MenuDTO filter) {
        MenuDTO ftl = new MenuDTO();
        ftl.setMenuType(filter.getMenuType());
        List<Menu> menus = listByFlt(ftl);

        Set<Integer> checkedSet = new HashSet<>();
        Set<Integer> checkedSet1 = new HashSet<>();


        if (filter.getRoleId() != null) {
            Integer roleId = filter.getRoleId();
            List<RoleMenu> rolrMenus = roleMenuService.listByRoleId(roleId);

            for (RoleMenu roelMenu : rolrMenus) {
                checkedSet1.add(roelMenu.getMenuID());

                if ("checked".equals(roelMenu.getCheckState())) {
                    checkedSet.add(roelMenu.getMenuID());
                } else if ("indeterminate".equals(roelMenu.getCheckState())) {
                    checkedSet1.add(roelMenu.getMenuID());
                }
            }
        }else if(filter.getAdminId() != null){
            Integer adminId = filter.getAdminId();
            AdminMenuDTO fil = new AdminMenuDTO();
            fil.setAdminId(adminId);
            List<AdminMenu> adminMenus = adminMenuService.listByFliter(fil);

            for (AdminMenu adminMenu : adminMenus) {
                checkedSet1.add(adminMenu.getMenuID());

                if ("checked".equals(adminMenu.getCheckState())) {
                    checkedSet.add(adminMenu.getMenuID());
                } else if ("indeterminate".equals(adminMenu.getCheckState())) {
                    checkedSet1.add(adminMenu.getMenuID());
                }
            }
        }
        List<MenuDTO> list = BeanUtils.copyAs(menus, MenuDTO.class);
        for (MenuDTO item : list) {
            item.setText(item.getTitle());
            item.setExpand(true);
            if (checkedSet.contains(item.getId())) {
                item.setChecked(true);
                item.setCheckState("checked");

            } else if (checkedSet1.contains(item.getId())) {
                item.setCheckState("checked");
            }

        }

        // list 总数据
        // 最后的结果
        List<MenuDTO> menuList = new ArrayList<MenuDTO>();
        // 先找到所有的一级菜单
        for (int i = 0; i < list.size(); i++) {
            // 一级菜单没有parentId
            if (list.get(i).getParentID().equals(0)) {
                menuList.add(list.get(i));
            }
        }
        // 为一级菜单设置子菜单，getChild是递归调用的
        for (MenuDTO menu : menuList) {
            menu.setChildren(getChild(menu.getId(), list));
        }

        return menuList;
    }

    // 递归查询
    private List<MenuDTO> getChild(Integer id, List<MenuDTO> menuList) {
        // 子菜单
        List<MenuDTO> childList = new ArrayList<>();
        for (MenuDTO menu : menuList) {
            // 遍历所有节点，将父菜单id与传过来的id比较
            if (menu.getParentID().equals(id)) {
                childList.add(menu);
            }

        }
        // 把子菜单的子菜单再循环一遍
        for (MenuDTO menu : childList) {
            MenuDTO ftl = new MenuDTO();
            //ftl.setMenuType("0");
            ftl.setParentID(menu.getId());
            List<MenuDTO> children = BeanUtils.copyAs(listByFlt(ftl), MenuDTO.class);
            if (children != null && !children.isEmpty()) {
                // 递归
                menu.setChildren(getChild(menu.getId(), menuList));
            }
        } // 递归退出条件
        if (childList.size() == 0) {
            return null;
        }
        return childList;
    }

    @Override
    public List<MenuDTO> menulist(MenuDTO filter) {
        List<Menu> menus = listByFlt(filter);
        List<MenuDTO> list = BeanUtils.copyAs(menus, MenuDTO.class);
        for (MenuDTO menuDTO : list) {
            menuDTO.setMenuLevelText(MenuDTO.MAP_MENU_LEVEL.get(menuDTO.getMenuLevel()));
            menuDTO.setMenuTypeText(MenuDTO.MAP_MENU_TYPE.get(menuDTO.getMenuType()));
        }

        // 最后的结果
        List<MenuDTO> menuList = new ArrayList<MenuDTO>();
        // 先找到所有的一级菜单
        for (int i = 0; i < list.size(); i++) {
            // 一级菜单没有parentId
            if (list.get(i).getParentID().equals(0)) {
                menuList.add(list.get(i));
            }
        }
        // 为一级菜单设置子菜单，getChild是递归调用的
        for (MenuDTO menu : menuList) {
            menu.setChildren(getChild(menu.getId(), list));
        }

        return menuList;
    }

    @Override
    public List<MenuDTO> listByFilter(MenuDTO filter) {
        List<Menu> menus;
        if (filter.getAdminId() != null) {
            menus = listByAdminId(filter.getAdminId());
        } else {
            menus = this.listByFlt(filter);
        }
        List<MenuDTO> list = BeanUtils.copyAs(menus, MenuDTO.class);
        for (MenuDTO menuDTO : list) {
            menuDTO.setMenuLevelText(MenuDTO.MAP_MENU_LEVEL.get(menuDTO.getMenuLevel()));
            menuDTO.setMenuTypeText(MenuDTO.MAP_MENU_TYPE.get(menuDTO.getMenuType()));
        }
        return list;
    }

    @Override
    public List<Menu> listByAdminId(Integer adminId) {

        String sql = " SELECT ";
        sql += "     t1.*";
        sql += " FROM";
        sql += "     t_menu t1,";
        sql += "     (SELECT ";
        sql += "         t3.MenuID";
        sql += "     FROM";
        sql += "         t_admin t2, t_admin_menu t3";
        sql += "     WHERE";
        sql += "         t2.ID = t3.AdminID";
        sql += "         AND t2.ID = "+adminId+" UNION SELECT ";
        sql += "         t3.MenuID";
        sql += "     FROM";
        sql += "         t_admin t2, t_role_menu t3, t_admin_role t4";
        sql += "     WHERE";
        sql += "         t2.ID = t4.AdminID";
        sql += "             AND t2.ID = "+adminId;
        sql += "             AND t3.RoleID = t4.RoleID) t0";
        sql += " WHERE";
        sql += "     t1.ID = t0.MenuID AND t1.MenuType = '0'";
        sql += " ORDER BY t1.OrderBy";
        return this.baseMapper.listByAdminId(sql);
    }

    @Override
    public MenuDTO add(MenuDTO menuDTO) {

        Menu menu = BeanUtils.copyAs(menuDTO, Menu.class);
        menu.setParentID(menuDTO.getParentID());
        Integer parentId = menu.getParentID();
        if (parentId!=null && !parentId.equals(0)) {
            Menu parentMenu = this.getById(parentId);
            if (null == parentMenu || parentMenu.getStatus().equals("0")) {
                throw new BusinessException(ResException.MENU_PARENT_DEL);
            }
            menu.setMenuLevel(parentMenu.getMenuLevel() + 1);
        } else {
            menu.setMenuLevel(1);
        }
        menu.setStatus("1");
        menu.setCreateTime(LocalDateTime.now());

        this.save(menu);
        MenuDTO menuDTO1 =BeanUtils.copyAs(menu, MenuDTO.class);
        menuDTO1.setMenuLevelText(MenuDTO.MAP_MENU_LEVEL.get(menuDTO1.getMenuLevel()));
        menuDTO1.setMenuTypeText(MenuDTO.MAP_MENU_TYPE.get(menuDTO1.getMenuType()));
        return menuDTO1;
    }

    @Override
    public List<MenuDTO> listAndSetAccess(MenuDTO filter) {
        List<Menu> menus = new ArrayList<>();
        Admin admin = adminService.getById(filter.getAdminId());
/*        if (admin.getAdminType().intValue()==1){
            MenuDTO ftl = new MenuDTO();
            ftl.setMenuType(filter.getMenuType());
            menus = listByFlt(ftl);
        }else {*/
        AdminRole adminRole = adminRoleService.getByAdmin(admin.getId());
        if (adminRole!=null){
            List<RoleMenu> roleMenus = roleMenuService.listByRoleId(adminRole.getRoleID());
            if (!CollectionUtils.isEmpty(roleMenus) && roleMenus.size()>0){
                List<Integer> menuIds = roleMenus.stream().map(RoleMenu::getMenuID).collect(Collectors.toList());
                menus = (List<Menu>) this.listByIds(menuIds);
            }
        }
        /*        }*/


        Set<Integer> checkedSet = new HashSet<>();
        Set<Integer> checkedSet1 = new HashSet<>();

        Integer adminId = filter.getAdminId();
        AdminMenuDTO fil = new AdminMenuDTO();
        fil.setAdminId(adminId);
        List<AdminMenu> adminMenus = adminMenuService.listByFliter(fil);

        for (AdminMenu adminMenu : adminMenus) {
            checkedSet1.add(adminMenu.getMenuID());

            if ("checked".equals(adminMenu.getCheckState())) {
                checkedSet.add(adminMenu.getMenuID());
            } else if ("indeterminate".equals(adminMenu.getCheckState())) {
                checkedSet1.add(adminMenu.getMenuID());
            }
        }
        Map<String, String> apiUrlMaps = new HashMap<>();
        List<MenuDTO> list = BeanUtils.copyAs(menus, MenuDTO.class);
        for (MenuDTO item : list) {
            item.setText(item.getTitle());
            item.setExpand(true);
            if (checkedSet.contains(item.getId())) {
                item.setChecked(true);
                item.setCheckState("checked");

                String apiUrl = item.getApiUrl();
                if (StringUtils.hasText(apiUrl)) {
                    String[] apiUrls = apiUrl.split(";");
                    for (String apiItem : apiUrls) {
                        apiUrlMaps.put(apiItem, "1");
                    }
                }
            } else if (checkedSet1.contains(item.getId())) {
                item.setCheckState("checked");

                String apiUrl = item.getApiUrl();
                if (StringUtils.hasText(apiUrl)) {
                    String[] apiUrls = apiUrl.split(";");
                    for (String apiItem : apiUrls) {
                        apiUrlMaps.put(apiItem, "1");
                    }
                }
            }
        }
        ContextUtils.clearAccessMap(adminId);
        ContextUtils.insertAccessMap(adminId, apiUrlMaps);

        // list 总数据
        // 最后的结果
        List<MenuDTO> menuList = new ArrayList<MenuDTO>();
        // 先找到所有的一级菜单
        for (MenuDTO item : list) {
            // 一级菜单没有parentId
            if (item.getParentID().equals(0)) {
                menuList.add(item);
            }
        }
        // 为一级菜单设置子菜单，getChild是递归调用的
        for (MenuDTO menu : menuList) {
            menu.setChildren(getChild(menu.getId(), list));
        }

        return menuList;
    }
}
