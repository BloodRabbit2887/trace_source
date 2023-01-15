package qichen.code.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import qichen.code.entity.*;
import qichen.code.entity.dto.AdminDTO;
import qichen.code.entity.dto.AdminMenuDTO;
import qichen.code.entity.dto.MenuDTO;
import qichen.code.entity.dto.RoleMenuDTO;
import qichen.code.exception.BusinessException;
import qichen.code.exception.ResException;
import qichen.code.mapper.AdminMapper;
import qichen.code.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import qichen.code.utils.BeanUtils;
import qichen.code.utils.HashUtils;
import qichen.code.utils.MathUtils;
import qichen.code.utils.TokenUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 管理员表 服务实现类
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-11-23
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements IAdminService {

    @Autowired
    private IAdminRoleService adminRoleService;
    @Autowired
    private IRoleService roleService;
    @Autowired
    private IRoleMenuService roleMenuService;
    @Autowired
    private IAdminMenuService adminMenuService;
    @Autowired
    private IMenuService menuService;
    @Autowired
    private IAdminService adminService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private IAdminTokenService adminTokenService;

    @Override
    public List<AdminDTO> listDtoByPage(AdminDTO filter, int page, int pageSize) {
        List<Admin> admins = listByPage(filter, page, pageSize);
        List<AdminDTO> adminDTO = BeanUtils.copyAs(admins, AdminDTO.class);
        for (AdminDTO admin : adminDTO) {
            admin.setAdminTypeText(AdminDTO.MAP_TYPE.get(admin.getAdminType()));
            AdminRole adminRole = adminRoleService.getByAdmin(admin.getId());
            if (null != adminRole) {
                admin.setRoleId(adminRole.getRoleID());
                Role role = roleService.getById(adminRole.getRoleID());
                admin.setRoleName(role.getName());
            }
        }
        return adminDTO;
    }

    @Override
    public int listCount(AdminDTO filter) {
        QueryWrapper<Admin> wrapper =new QueryWrapper<>();
        addFilter(wrapper,filter);
        return this.baseMapper.selectCount(wrapper);
    }

    @Override
    public List<Admin> listByPage(AdminDTO filter, int page, int pageSize) {
        QueryWrapper<Admin> wrapper =new QueryWrapper<>();
        addFilter(wrapper,filter);
        Page<Admin> page1 = new Page<>(page,pageSize);
        IPage<Admin> adminIPage = this.baseMapper.selectPage(page1, wrapper);
        if (adminIPage!=null){
            return adminIPage.getRecords();
        }
        return null;
    }

    private void addFilter(QueryWrapper<Admin> wrapper, AdminDTO filter) {
        if (StringUtils.hasText(filter.getAdminNO())) {
            wrapper.eq("AdminNO", filter.getAdminNO());
        }
        if (null != filter.getRelatedID() && !filter.getRelatedID().equals(0)) {
            wrapper.eq("RelatedID", filter.getRelatedID());
        }
        if (null != filter.getAdminType()) {
            wrapper.eq("AdminType", filter.getAdminType());
        }
        if (StringUtils.hasText(filter.getAdminName())) {
            wrapper.like("AdminName", filter.getAdminName());
        }
        if (filter.getRoleId() != null) {
            QueryWrapper<AdminRole> wrapper1 = new QueryWrapper<>();
            wrapper1.eq("RoleID",filter.getRoleId());
            List<AdminRole> adminRoles = adminRoleService.list(wrapper1);
            if (!CollectionUtils.isEmpty(adminRoles) && adminRoles.size()>0){
                List<Integer> adminIds = adminRoles.stream().map(AdminRole::getAdminID).collect(Collectors.toList());
                wrapper.in("ID",adminIds);
            }
        }

        if (filter.getNotInRoleId() != null) {
            QueryWrapper<AdminRole> wrapper1 = new QueryWrapper<>();
            wrapper1.eq("RoleID",filter.getNotInRoleId());
            List<AdminRole> adminRoles = adminRoleService.list(wrapper1);

            if (!CollectionUtils.isEmpty(adminRoles) && adminRoles.size()>0){
                List<Integer> adminIds = adminRoles.stream().map(AdminRole::getAdminID).collect(Collectors.toList());
                wrapper.notIn("ID",adminIds);
            }
        }

        if (null != filter.getStatus()) {
            wrapper.eq("status", filter.getStatus());
        } else {
            wrapper.eq("status", AdminDTO.STATUS_OK);
        }
        wrapper.eq("delTf", (byte)0);
    }

    @Override
    public AdminDTO getAdmin(Integer id) {
        Admin admin = this.getById(id);
        // admin.setPassword(HashUtils.getMd5((adminDTO.getPassword() +
        // admin.getSaltValue()).getBytes()));

        AdminDTO adminDTO = BeanUtils.copyAs(admin, AdminDTO.class);
        AdminRole adminRole = adminRoleService.getByAdmin(id);
        if (null != adminRole) {
            adminDTO.setRoleId(adminRole.getRoleID());
        }
        return adminDTO;
    }

    @Override
    public Admin add(AdminDTO adminDTO) {

        String adminNo = adminDTO.getAdminNO();
        Admin admin = getByAdminNo(adminNo);
        if (null != admin) {
            throw new BusinessException(ResException.ADMIN_ALREADY);
        }
        // String storeNo = adminDTO.getStoreNo();
        // admin = adminDao.getByStoreNo(storeNo);
        // if (null != admin) {
        // throw new BusinessException(901, "该商家编号已被注册");
        // }

        admin = new Admin();
        admin.setRelatedID(adminDTO.getRelatedID()==null?0:adminDTO.getRelatedID());
        admin.setAdminType(adminDTO.getAdminType()==null?1:adminDTO.getAdminType());
        admin.setStatus(admin.getStatus()==null?1:adminDTO.getStatus());


        BeanUtils.copyPropertiesIgnoreNull(adminDTO, admin, "");

        String saltValue = MathUtils.getRandomString(20);

        admin.setSaltValue(saltValue);
        String pass = HashUtils.getMd5((adminDTO.getPassword() + saltValue).getBytes());
        admin.setPassword(pass);
        admin.setStatus(AdminDTO.STATUS_OK.intValue());
        admin.setCreateTime(LocalDateTime.now());

        this.save(admin);
        Integer roleId = adminDTO.getRoleId();
        if (null != roleId) {
            AdminRole adminRole = new AdminRole();

            adminRole.setAdminID(admin.getId());
            adminRole.setRoleID(roleId);
            adminRoleService.save(adminRole);

            List<RoleMenuDTO> rolelist = roleMenuService.listDtoByRoleId(roleId);
            for (RoleMenuDTO roleDTO : rolelist) {
                AdminMenu adminMenu = new AdminMenu();
                adminMenu.setAdminID(admin.getId());
                adminMenu.setMenuID(roleDTO.getMenuID());
                adminMenu.setCheckState(roleDTO.getCheckState());
                adminMenuService.save(adminMenu);
            }
        }

        if (listByAdminNo(adminNo) > 1) {
            throw new BusinessException(ResException.ADMIN_ALREADY);
        }

        return admin;
    }


    @Override
    public Admin getByAdminNo(String adminNo) {
        QueryWrapper<Admin> wrapper = new QueryWrapper<>();
        wrapper.eq("adminNo", adminNo);
        return this.getOne(wrapper);
    }

    @Override
    public int listByAdminNo(String adminNo) {
        QueryWrapper<Admin> wrapper = new QueryWrapper<>();
        wrapper.eq("adminNo", adminNo);
        return this.list(wrapper).size();
    }

    @Override
    public void updateDto(AdminDTO adminDTO) {
        Admin admin = this.getById(adminDTO.getId());

        BeanUtils.copyPropertiesIgnoreNull(adminDTO, admin, "password", "adminNo");

        if (StringUtils.hasText(adminDTO.getPassword())) {
            if (!adminDTO.getPassword().equals(admin.getPassword())) {
                admin.setPassword(HashUtils.getMd5((adminDTO.getPassword() + admin.getSaltValue()).getBytes()));
            }
        }

        admin.setUpdateTime(LocalDateTime.now());
        admin.setDelTF(adminDTO.getDelTF());
        this.updateById(admin);
        Integer roleId = adminDTO.getRoleId();
        if (null != roleId) {
            AdminRole delAdminRole = adminRoleService.getByAdmin(admin.getId());
            if (null != delAdminRole) {
                adminRoleService.removeById(admin.getId());
            }

            AdminRole adminRole = new AdminRole();
            adminRole.setAdminID(admin.getId());
            adminRole.setRoleID(roleId);
            adminRoleService.save(adminRole);

            AdminMenuDTO adminMenuDTO = new AdminMenuDTO();
            adminMenuDTO.setAdminId(admin.getId());
            List<AdminMenu> tAdminMenu = adminMenuService.listByFliter(adminMenuDTO);
            if (null != tAdminMenu && !tAdminMenu.isEmpty()) {
                for (AdminMenu adminMenu : tAdminMenu) {
                    adminMenuService.removeById(adminMenu.getAdminID());
                }
            }
            RoleMenuDTO roleMenuDTO = new RoleMenuDTO();
            roleMenuDTO.setRoleId(roleId);
            List<RoleMenuDTO> rolelist = roleMenuService.listDtoByRoleId(roleId);
            for (RoleMenuDTO roleDTO : rolelist) {
                AdminMenu adminMenus = new AdminMenu();
                adminMenus.setAdminID(admin.getId());
                adminMenus.setMenuID(roleDTO.getMenuID());
                adminMenus.setCheckState(roleDTO.getCheckState());
                adminMenuService.save(adminMenus);
            }
        }
    }

    @Override
    public AdminDTO login(String loginName, String password) {
        AdminDTO filter = new AdminDTO();
        filter.setAdminNO(loginName);
        filter.setStatus(1);
        List<Admin> admins = listByPage(filter, 1, 1);
        if (admins.isEmpty()) {
            throw new BusinessException(ResException.ADMIN_LOGIN_ERR);
        }
        Admin admin = admins.get(0);

        String pass = HashUtils.getMd5((password + admin.getSaltValue()).getBytes());
        if (!pass.equals(admin.getPassword())) {
            throw new BusinessException(ResException.ADMIN_LOGIN_ERR);
        }

        AdminDTO adminDTO = BeanUtils.copyAs(admin, AdminDTO.class);
        MenuDTO menuFilter = new MenuDTO();
        menuFilter.setAdminId(admin.getId());
        AdminRole adminRole = adminRoleService.getByAdmin(admin.getId());
        if (adminRole!=null){
            adminDTO.setRoleId(adminRole.getRoleID());
        }
        List<MenuDTO> menuList = menuService.listAndSetAccess(menuFilter);
        adminDTO.setAdminMenuList(menuList);

        String tokenId = adminService.procToken(adminDTO, true);
        adminDTO.setTokenId(tokenId);
        return adminDTO;
    }


    @Override
    public String procToken(AdminDTO adminDTO, boolean deleteOld) {
        Integer adminId = adminDTO.getId();
        String tokenId = TokenUtils.getTokenId();

        if (deleteOld) {
            // 删除原令牌
            adminTokenService.removeByAdminId(adminId);
        }

        // 保存令牌
        AdminToken adminToken = new AdminToken();
        adminToken.setAdminID(adminId);
        adminToken.setTokenID(tokenId);
        adminToken.setCreateTime(LocalDateTime.now());
        adminToken.setExpireTime(LocalDateTime.now().plusDays(10L));

        adminTokenService.save(adminToken);
        // 添加到缓存
        redisTemplate.opsForValue().set(tokenId, JSON.toJSONString(adminDTO));
        return tokenId;
    }

    @Override
    public void check(AdminDTO adminDTO) {
        if (!StringUtils.isEmpty(adminDTO.getAdminNO()) && adminDTO.getAdminNO().length()>0){
            QueryWrapper<Admin> wrapper = new QueryWrapper<>();
            wrapper.eq("`AdminNO`",adminDTO.getAdminNO());
            if (adminDTO.getId()!=null){
                wrapper.ne("ID",adminDTO.getId());
            }
            Integer count = baseMapper.selectCount(wrapper);
            if (count>0){
                throw new BusinessException(ResException.MAKE_ERR.getCode(),"账户已被使用");
            }
        }
    }

    @Override
    public void updatePassword(Integer id,String oldPass, String newPass) {
        Admin admin = getById(id);
        if (admin==null){
            throw new BusinessException(ResException.QUERY_MISS);
        }
        if (!admin.getPassword().equals(HashUtils.getMd5((oldPass+admin.getSaltValue()).getBytes()))){
            throw new BusinessException(ResException.USER_PASS_ERR);
        }
        admin.setPassword(HashUtils.getMd5((newPass+admin.getSaltValue()).getBytes()));
        admin.setUpdateTime(LocalDateTime.now());
        updateById(admin);
    }

}
