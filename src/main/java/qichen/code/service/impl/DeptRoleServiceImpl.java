package qichen.code.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import qichen.code.entity.Dept;
import qichen.code.entity.DeptRole;
import qichen.code.entity.dto.DeptRoleDTO;
import qichen.code.exception.BusinessException;
import qichen.code.exception.ResException;
import qichen.code.mapper.DeptRoleMapper;
import qichen.code.model.Filter;
import qichen.code.service.IDeptRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import qichen.code.service.IDeptService;
import qichen.code.service.IUserService;
import qichen.code.utils.BeanUtils;
import qichen.code.utils.JsonUtils;

import java.math.BigInteger;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 部门角色表 服务实现类
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-02-15
 */
@Service
public class DeptRoleServiceImpl extends ServiceImpl<DeptRoleMapper, DeptRole> implements IDeptRoleService {

    @Autowired
    private IDeptService deptService;
    @Autowired
    private IUserService userService;

    @Override
    public DeptRole add(DeptRoleDTO dto) {
        Map<String,String> params = new HashMap<>();
        params.put("name","名称");
        params.put("level","等级");
        params.put("deptId","部门");
        JsonUtils.checkColumnNull(params, JSONObject.parseObject(JSON.toJSONString(dto)));

        checkAlready(dto);

        DeptRole role = BeanUtils.copyAs(dto, DeptRole.class);
        save(role);
        return role;
    }

    @Override
    public List<DeptRoleDTO> listByFilter(DeptRoleDTO dto, Filter filter) {
        List<DeptRole> list = listFilter(dto,filter);
        if (!CollectionUtils.isEmpty(list) && list.size()>0){
           return listDTO(list);
        }
        return null;
    }

    private List<DeptRoleDTO> listDTO(List<DeptRole> list) {
        List<DeptRoleDTO> dtos = BeanUtils.copyAs(list, DeptRoleDTO.class);

        List<Dept> depts = (List<Dept>) deptService.listByIds(list.stream().map(DeptRole::getDeptId).distinct().collect(Collectors.toList()));

        for (DeptRoleDTO dto : dtos) {
            if (!CollectionUtils.isEmpty(depts) && depts.size()>0){
                for (Dept dept : depts) {
                    if (dept.getId().equals(dto.getDeptId())){
                        dto.setDeptName(dept.getTitle());
                    }
                }
            }
        }

        return dtos;
    }

    @Override
    public List<DeptRole> listFilter(DeptRoleDTO dto, Filter filter) {
        QueryWrapper<DeptRole> wrapper = new QueryWrapper<>();
        addFilter(wrapper,dto,filter);
        return list(wrapper);
    }

    private void addFilter(QueryWrapper<DeptRole> wrapper, DeptRoleDTO dto, Filter filter) {
        if (dto!=null){
            if (dto.getLevel()!=null){
                wrapper.eq("`level`",dto.getLevel());
            }
            if (dto.getCreateAllOrderPermission()!=null){
                wrapper.eq("createAllOrderPermission",dto.getCreateAllOrderPermission());
            }
            if (dto.getDistributionPermission()!=null){
                wrapper.eq("distributionPermission",dto.getDistributionPermission());
            }
            if (dto.getVerifyPermission()!=null){
                wrapper.eq("verifyPermission",dto.getVerifyPermission());
            }
            if (dto.getLinkChangePermission()!=null){
                wrapper.eq("linkChangePermission",dto.getLinkChangePermission());
            }
            if (dto.getUpdatePermission()!=null){
                wrapper.eq("updatePermission",dto.getUpdatePermission());
            }
            if (dto.getDeptId()!=null){
                wrapper.eq("deptId",dto.getDeptId());
            }
        }
        if (filter!=null){
            if (filter.getCreateTimeBegin()!=null){
                wrapper.ge("createTime",filter.getCreateTimeBegin());
            }
            if (filter.getCreateTimeEnd()!=null){
                wrapper.le("createTime",filter.getCreateTimeEnd());
            }
            if (!StringUtils.isEmpty(filter.getKeyword()) && filter.getKeyword().length()>0){
                wrapper.like("`name`",filter.getKeyword());
            }
            if (!StringUtils.isEmpty(filter.getOrders()) && filter.getOrders().length()>0){
                if (filter.getOrderBy()!=null){
                    wrapper.orderBy(true,filter.getOrderBy(),filter.getOrders());
                }
            }
            if (filter.getPage()!=null && filter.getPageSize()!=null && filter.getPage()!=0 && filter.getPageSize()!=0){
                int fast = filter.getPage()<=1?0:(filter.getPage()-1)*filter.getPageSize();
                wrapper.last(" limit "+fast+", "+filter.getPageSize());
            }
        }
    }

    @Override
    public BigInteger listCount(DeptRoleDTO dto, Filter filter) {
        QueryWrapper<DeptRole> wrapper = new QueryWrapper<>();
        if (filter!=null){
            filter.setPage(null);
            filter.setPageSize(null);
        }
        addFilter(wrapper,dto,filter);
        return BigInteger.valueOf(baseMapper.selectCount(wrapper));
    }

    @Override
    public DeptRoleDTO getDetail(Integer id) {
        DeptRole role = getById(id);
        if (role==null){
            throw new BusinessException(ResException.QUERY_MISS);
        }
        return getDTO(role);
    }

    @Override
    public DeptRole adminUpdate(DeptRoleDTO dto) {

        DeptRole deptRole = getById(dto.getId());
        if (deptRole==null){
            throw new BusinessException(ResException.QUERY_MISS);
        }
        if (deptRole.getLevel()==1 && dto.getLevel()!=1){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"必须保留最低等级");
        }

        checkAlready(dto);

        DeptRole role = BeanUtils.copyAs(dto, DeptRole.class);
        updateById(role);
        return role;
    }

    @Transactional
    @Override
    public DeptRole delete(Integer id) {
        DeptRole role = getById(id);
        if (role==null){
            throw new BusinessException(ResException.QUERY_MISS);
        }
        if (role.getLevel()==1){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"最低等级无法删除");
        }
        userService.cleanDeptRoleByRoleId(id);
        removeById(id);
        return role;
    }

    @Override
    public DeptRole getByAdd(Integer deptId,Integer deptRoleId) {
        DeptRole role = null;
        if (deptRoleId==null || deptRoleId == 0){
            role = getMinByDeptId(deptId);
        }else {
            role = getById(deptRoleId);
        }
        if (role==null || !role.getDeptId().equals(deptId)){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"部门角色信息有误");
        }
        return role;
    }

    private DeptRole getMinByDeptId(Integer deptId) {
        QueryWrapper<DeptRole> wrapper = new QueryWrapper<>();
        wrapper.eq("deptId",deptId);
        wrapper.eq("`level`",1);
        return getOne(wrapper);
    }

    private DeptRoleDTO getDTO(DeptRole role) {
        DeptRoleDTO dto = BeanUtils.copyAs(role, DeptRoleDTO.class);

        Dept dept = deptService.getById(role.getDeptId());
        if (dept!=null){
            dto.setDeptName(dept.getTitle());
        }
        return dto;
    }

    private void checkAlready(DeptRoleDTO dto) {
        QueryWrapper<DeptRole> wrapper = new QueryWrapper<>();
        wrapper.eq("deptId",dto.getDeptId());
        if (dto.getId()!=null){
            wrapper.ne("`ID`",dto.getId());
        }
        List<DeptRole> list = list(wrapper);
        if (!CollectionUtils.isEmpty(list) && list.size()>0){
            for (DeptRole role : list) {
                if (role.getName().equals(dto.getName())){
                    throw new BusinessException(ResException.MAKE_ERR.getCode(),"名称重复");
                }
                if (role.getLevel().equals(dto.getLevel())){
                    throw new BusinessException(ResException.MAKE_ERR.getCode(),"等级已存在");
                }
            }
        }
    }
}
