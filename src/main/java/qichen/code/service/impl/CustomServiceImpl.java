package qichen.code.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import qichen.code.entity.Custom;
import qichen.code.entity.dto.CustomDTO;
import qichen.code.exception.BusinessException;
import qichen.code.exception.ResException;
import qichen.code.mapper.CustomMapper;
import qichen.code.model.Filter;
import qichen.code.service.ICustomService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import qichen.code.utils.BeanUtils;

import java.math.BigInteger;
import java.util.List;

/**
 * <p>
 * 客户管理表 服务实现类
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-12-16
 */
@Service
public class CustomServiceImpl extends ServiceImpl<CustomMapper, Custom> implements ICustomService {


    @Override
    public List<CustomDTO> listByFilter(CustomDTO customDTO, Filter filter) {
        List<Custom> list = listFilter(customDTO,filter);
        if (!CollectionUtils.isEmpty(list) && list.size()>0){
            return listDTO(list);
        }
        return null;
    }

    private List<CustomDTO> listDTO(List<Custom> list) {
        List<CustomDTO> dtos = BeanUtils.copyAs(list, CustomDTO.class);
        //TODO
        return dtos;
    }


    @Override
    public List<Custom> listFilter(CustomDTO customDTO, Filter filter) {
        QueryWrapper<Custom> wrapper = new QueryWrapper<>();
        addFilter(wrapper,customDTO,filter);
        return list(wrapper);
    }

    private void addFilter(QueryWrapper<Custom> wrapper, CustomDTO customDTO, Filter filter) {
        if (customDTO!=null){
            if (!StringUtils.isEmpty(customDTO.getAcceptMobile()) && customDTO.getAcceptMobile().length()>0){
                wrapper.eq("`acceptMobile`",customDTO.getAcceptMobile());
            }
            if (!StringUtils.isEmpty(customDTO.getName()) && customDTO.getName().length()>0){
                wrapper.eq("`name`",customDTO.getName());
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
                wrapper.and(queryWrapper->queryWrapper.like("`name`",filter.getKeyword())
                        .or().like("acceptName",filter.getKeyword()).or().like("`address`",filter.getKeyword()));
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
    public BigInteger listCount(CustomDTO customDTO, Filter filter) {
        QueryWrapper<Custom> wrapper = new QueryWrapper<>();
        if (filter!=null){
            filter.setPage(null);
            filter.setPageSize(null);
        }
        addFilter(wrapper,customDTO,filter);
        return BigInteger.valueOf(baseMapper.selectCount(wrapper));
    }

    @Override
    public Custom add(CustomDTO customDTO) {
        check(customDTO);
        Custom custom = BeanUtils.copyAs(customDTO, Custom.class);
        save(custom);
        return custom;
    }

    @Override
    public Custom adminUpdate(CustomDTO customDTO) {
        Custom custom = getById(customDTO);
        if (custom==null){
            throw new BusinessException(ResException.QUERY_MISS);
        }
        check(customDTO);
        Custom custom1 = BeanUtils.copyAs(customDTO, Custom.class);
        updateById(custom1);
        return custom1;
    }

    @Override
    public CustomDTO getDetail(Integer id) {
        Custom custom = getById(id);
        if (custom==null){
            throw new BusinessException(ResException.QUERY_MISS);
        }
        return getDTO(custom);
    }

    @Override
    public Custom delete(Integer id) {
        Custom custom = getById(id);
        if (custom==null){
            throw new BusinessException(ResException.QUERY_MISS);
        }
        removeById(id);
        return custom;
    }

    private CustomDTO getDTO(Custom custom) {
        CustomDTO customDTO = BeanUtils.copyAs(custom, CustomDTO.class);
        //TODO
        return customDTO;
    }

    private void check(CustomDTO customDTO) {
        if (!StringUtils.isEmpty(customDTO.getName()) && customDTO.getName().length()>0){
            QueryWrapper<Custom> wrapper = new QueryWrapper<>();
            wrapper.eq("`name`",customDTO.getName());
            if (customDTO.getId()!=null){
                wrapper.ne("ID",customDTO.getId());
            }
            Integer count = baseMapper.selectCount(wrapper);
            if (count>0){
                throw new BusinessException(ResException.MAKE_ERR.getCode(),"客户名已存在");
            }
        }
    }
}
