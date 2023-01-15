package qichen.code.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import qichen.code.entity.Dept;
import qichen.code.entity.dto.DeptDTO;
import qichen.code.entity.dto.UserDTO;
import qichen.code.exception.BusinessException;
import qichen.code.exception.ResException;
import qichen.code.mapper.DeptMapper;
import qichen.code.model.Filter;
import qichen.code.service.IDeptService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import qichen.code.service.IUserService;
import qichen.code.utils.BeanUtils;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 部门表 服务实现类
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-12-16
 */
@Service
public class DeptServiceImpl extends ServiceImpl<DeptMapper, Dept> implements IDeptService {

    @Autowired
    private IUserService userService;

    @Override
    public Dept add(DeptDTO deptDTO) {
        if (StringUtils.isEmpty(deptDTO.getTitle()) || deptDTO.getTitle().length()==0){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"部门名称不能为空");
        }
        check(deptDTO);
        Dept dept = BeanUtils.copyAs(deptDTO, Dept.class);
        save(dept);
        return dept;
    }

    @Override
    public List<DeptDTO> listByFilter(DeptDTO deptDTO, Filter filter) {
        List<Dept> list = listFilter(deptDTO,filter);
        if (!CollectionUtils.isEmpty(list) && list.size()>0){
            return listDTO(list);
        }
        return null;
    }

    @Override
    public List<DeptDTO> listDTO(List<Dept> list) {
        List<DeptDTO> dtos = BeanUtils.copyAs(list, DeptDTO.class);

        //TODO

        return dtos;
    }

    @Override
    public List<Dept> listFilter(DeptDTO deptDTO, Filter filter) {
        QueryWrapper<Dept> wrapper = new QueryWrapper<>();
        addFilter(wrapper,deptDTO,filter);
        return list(wrapper);
    }

    private void addFilter(QueryWrapper<Dept> wrapper, DeptDTO deptDTO, Filter filter) {
        if (deptDTO!=null){
            if (deptDTO.getStatus()!=null){
                wrapper.eq("`Status`",deptDTO.getStatus());
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
                wrapper.like("`title`",filter.getKeyword());
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
    public BigInteger listCount(DeptDTO deptDTO, Filter filter) {
        QueryWrapper<Dept> wrapper = new QueryWrapper<>();
        if (filter!=null){
            filter.setPage(null);
            filter.setPageSize(null);
        }
        addFilter(wrapper,deptDTO,filter);
        return BigInteger.valueOf(baseMapper.selectCount(wrapper));
    }

    @Override
    public Dept adminUpdate(DeptDTO deptDTO) {
        Dept dept = getById(deptDTO.getId());
        if (dept==null){
            throw new BusinessException(ResException.QUERY_MISS);
        }
        check(deptDTO);
        Dept dept1 = BeanUtils.copyAs(deptDTO, Dept.class);
        updateById(dept1);
        return dept1;
    }

    @Transactional
    @Override
    public Dept delete(Integer id) {
        Dept dept = getById(id);
        if (dept==null){
            throw new BusinessException(ResException.QUERY_MISS);
        }
        UserDTO userDTO = new UserDTO();
        userDTO.setDeptId(id);
        BigInteger count = userService.listCount(userDTO, null);
        if (count.compareTo(BigInteger.ZERO)>0){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"请先删除职工");
        }

        removeById(id);
        return dept;
    }

    @Override
    public DeptDTO getDetail(Integer id) {
        Dept dept = getById(id);
        if (dept==null){
            throw new BusinessException(ResException.QUERY_MISS);
        }
        return getDTO(dept);
    }

    private DeptDTO getDTO(Dept dept) {
        DeptDTO dto = BeanUtils.copyAs(dept, DeptDTO.class);

        //TODO
        return dto;
    }

    private void check(DeptDTO deptDTO) {
        QueryWrapper<Dept> wrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(deptDTO.getTitle()) && deptDTO.getTitle().length()>0){
            wrapper.eq("title",deptDTO.getTitle());
            if (deptDTO.getId()!=null){
                wrapper.ne("title",deptDTO.getId());
            }
            Integer count = baseMapper.selectCount(wrapper);
            if (count>0){
                throw new BusinessException(ResException.MAKE_ERR.getCode(),"部门名称重复");
            }
        }
    }
}
