package qichen.code.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import qichen.code.entity.ErrType;
import qichen.code.entity.dto.ErrTypeDTO;
import qichen.code.exception.BusinessException;
import qichen.code.exception.ResException;
import qichen.code.mapper.ErrTypeMapper;
import qichen.code.model.Filter;
import qichen.code.service.IErrModelService;
import qichen.code.service.IErrTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import qichen.code.utils.BeanUtils;
import qichen.code.utils.JsonUtils;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 错误类型表 服务实现类
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-01-29
 */
@Service
public class ErrTypeServiceImpl extends ServiceImpl<ErrTypeMapper, ErrType> implements IErrTypeService {

    @Autowired
    private IErrModelService errModelService;

    @Override
    public List<ErrTypeDTO> listByFilter(ErrTypeDTO dto, Filter filter) {
        List<ErrType> list = listFilter(dto,filter);
        if (!CollectionUtils.isEmpty(list) && list.size()>0){
           return listDTO(list);
        }
        return null;
    }

    private List<ErrTypeDTO> listDTO(List<ErrType> list) {
        List<ErrTypeDTO> dtos = BeanUtils.copyAs(list, ErrTypeDTO.class);
        //TODO
        return dtos;
    }

    private List<ErrType> listFilter(ErrTypeDTO dto, Filter filter) {
        QueryWrapper<ErrType> wrapper = new QueryWrapper<>();
        addFilter(wrapper,dto,filter);
        return list(wrapper);
    }

    private void addFilter(QueryWrapper<ErrType> wrapper, ErrTypeDTO dto, Filter filter) {
        if (dto!=null){
            if (dto.getStatus()!=null){
                wrapper.eq("`Status`",dto.getStatus());
            }
        }
        if (filter!=null){
            if (filter.getCreateTimeBegin()!=null){
                wrapper.ge("createTime",filter.getCreateTimeBegin());
            }
            if (filter.getCreateTimeEnd()!=null){
                wrapper.le("createTime",filter.getCreateTimeEnd());
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
    public BigInteger listCount(ErrTypeDTO dto, Filter filter) {
        QueryWrapper<ErrType> wrapper = new QueryWrapper<>();
        if (filter!=null){
            filter.setPage(null);
            filter.setPageSize(null);
        }
        return BigInteger.valueOf(baseMapper.selectCount(wrapper));
    }

    @Override
    public ErrTypeDTO getDetail(Integer id) {
        ErrType type = getById(id);
        if (type==null){
            throw new BusinessException(ResException.QUERY_MISS);
        }
        return getDTO(type);
    }

    @Transactional
    @Override
    public ErrType add(ErrTypeDTO typeDTO) {
        Map<String,String> params = new HashMap<>();
        params.put("`title`","名称");
        JsonUtils.checkColumnNull(params, JSONObject.parseObject(JSON.toJSONString(typeDTO)));

        check(typeDTO);

        ErrType errType = BeanUtils.copyAs(typeDTO, ErrType.class);
        saveOrUpdate(errType);

        return errType;
    }

    @Transactional
    @Override
    public ErrType adminUpdate(ErrTypeDTO typeDTO) {
        ErrType errType = getById(typeDTO.getId());
        if (errType==null){
            throw new BusinessException(ResException.QUERY_MISS);
        }
        ErrType type = BeanUtils.copyAs(typeDTO, ErrType.class);
        updateById(type);
        return type;
    }

    @Transactional
    @Override
    public ErrType adminDelete(Integer id) {
        ErrType type = getById(id);
        if (type==null){
            throw new BusinessException(ResException.QUERY_MISS);
        }
        errModelService.removeByTypeId(id);
        removeById(id);

        return type;
    }

    private void check(ErrTypeDTO typeDTO) {
        String title = typeDTO.getTitle();
        if (!StringUtils.isEmpty(title) && title.length()>0){
            QueryWrapper<ErrType> wrapper = new QueryWrapper<>();
            wrapper.eq("`title`",title);
            if (typeDTO.getId()!=null){
                wrapper.ne("ID",typeDTO.getId());
            }
            Integer count = baseMapper.selectCount(wrapper);
            if (count>0){
                throw new BusinessException(ResException.MAKE_ERR.getCode(),"名称重复");
            }
        }
    }

    private ErrTypeDTO getDTO(ErrType type) {
        ErrTypeDTO dto = BeanUtils.copyAs(type, ErrTypeDTO.class);
        //TODO
        return dto;
    }
}
