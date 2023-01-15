package qichen.code.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import qichen.code.entity.AdminRole;
import qichen.code.entity.dto.AdminRoleDTO;
import qichen.code.mapper.AdminRoleMapper;
import qichen.code.service.IAdminRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import qichen.code.utils.BeanUtils;

/**
 * <p>
 * 人员角色表 服务实现类
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-11-23
 */
@Service
public class AdminRoleServiceImpl extends ServiceImpl<AdminRoleMapper, AdminRole> implements IAdminRoleService {

    @Override
    public AdminRole getByAdmin(Integer id) {
        QueryWrapper<AdminRole> wrapper = new QueryWrapper<>();
        wrapper.eq("adminId",id);
        return this.getOne(wrapper);
    }

    @Override
    public void addDto(AdminRoleDTO adminRoleDTO) {
        AdminRole adminRole = BeanUtils.copyAs(adminRoleDTO, AdminRole.class);
        this.save(adminRole);
    }
}
