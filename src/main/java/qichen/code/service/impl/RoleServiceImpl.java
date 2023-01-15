package qichen.code.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.util.StringUtils;
import qichen.code.entity.Role;
import qichen.code.entity.dto.RoleDTO;
import qichen.code.exception.BusinessException;
import qichen.code.mapper.RoleMapper;
import qichen.code.service.IRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import qichen.code.utils.BeanUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-11-23
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

    @Override
    public List<RoleDTO> listByPage(RoleDTO filter, Integer page, Integer pageSize) {
        List<Role> roles = listPage(filter, page, pageSize);
        return BeanUtils.copyAs(roles, RoleDTO.class);
    }

    @Override
    public RoleDTO get(Integer id) {
        Role role = this.getById(id);
        if(null == role){
            throw new BusinessException(511, "角色不存在");
        }
        return BeanUtils.copyAs(role, RoleDTO.class);
    }

    @Override
    public void add(RoleDTO roleDTO) {
        Role role = BeanUtils.copyAs(roleDTO, Role.class);
        role.setStatus("1");
        role.setCreateTime(LocalDateTime.now());
        this.save(role);
    }

    @Override
    public void updateDto(RoleDTO roleDTO) {
        Role role = this.getById(roleDTO.getId());

        BeanUtils.copyPropertiesIgnoreNull(roleDTO, role);

        role.setUpdateTime(LocalDateTime.now());

        this.updateById(role);
    }

    @Override
    public List<Role> listPage(RoleDTO filter, Integer page, Integer pageSize) {
        QueryWrapper<Role> wrapper = new QueryWrapper<>();
        if(null != filter){
            if(StringUtils.hasText(filter.getName())){
                wrapper.eq("name", filter.getName());
            }
            if(StringUtils.hasText(filter.getStatus())){
                wrapper.eq("status", filter.getStatus());
            }
        }
        Page<Role> page1 = new Page<>(page, pageSize);
        IPage<Role> roleIPage = this.baseMapper.selectPage(page1, wrapper);
        if (roleIPage!=null){
            return roleIPage.getRecords();
        }
        return null;
    }

    @Override
    public int listCount(RoleDTO filter) {
        QueryWrapper<Role> wrapper = new QueryWrapper<>();
        if(null != filter){
            if(StringUtils.hasText(filter.getName())){
                wrapper.eq("name", filter.getName());
            }
            if(StringUtils.hasText(filter.getStatus())){
                wrapper.eq("status", filter.getStatus());
            }
        }
        return this.baseMapper.selectCount(wrapper);
    }
}
