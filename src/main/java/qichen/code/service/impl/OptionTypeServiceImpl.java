package qichen.code.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import qichen.code.entity.Option;
import qichen.code.entity.OptionType;
import qichen.code.entity.dto.OptionDTO;
import qichen.code.entity.dto.OptionTypeDTO;
import qichen.code.exception.BusinessException;
import qichen.code.exception.ResException;
import qichen.code.mapper.OptionTypeMapper;
import qichen.code.model.Filter;
import qichen.code.service.IOptionService;
import qichen.code.service.IOptionTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import qichen.code.utils.BeanUtils;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 下拉选项类型表 服务实现类
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-12-19
 */
@Service
public class OptionTypeServiceImpl extends ServiceImpl<OptionTypeMapper, OptionType> implements IOptionTypeService {

    @Autowired
    private IOptionService optionService;


    @Transactional
    @Override
    public OptionType add(OptionTypeDTO dto) {
        check(dto);
        OptionType type = BeanUtils.copyAs(dto, OptionType.class);
        save(type);
        return type;
    }

    @Transactional
    @Override
    public OptionType adminUpdate(OptionTypeDTO dto) {
        OptionType optionType = getById(dto.getId());
        if (optionType==null){
            throw new BusinessException(ResException.QUERY_MISS);
        }
        check(dto);
        OptionType type = BeanUtils.copyAs(dto, OptionType.class);
        updateById(type);
        return type;
    }

    @Transactional
    @Override
    public OptionType adminDelete(Integer id) {
        OptionType optionType = getById(id);
        if (optionType==null){
            throw new BusinessException(ResException.QUERY_MISS);
        }
        optionService.removeByTypeId(id);
        removeById(id);
        return optionType;
    }

    @Override
    public List<OptionTypeDTO> listByFilter(OptionTypeDTO dto, Filter filter) {
        List<OptionType> list = listFilter(dto,filter);
        if (!CollectionUtils.isEmpty(list) && list.size()>0){
           return listDTO(list);
        }
        return null;
    }

    @Override
    public List<OptionTypeDTO> listDTO(List<OptionType> list) {
        List<OptionTypeDTO> dtos = BeanUtils.copyAs(list, OptionTypeDTO.class);

        OptionDTO optionDTO = new OptionDTO();
        optionDTO.setTypeIds(dtos.stream().map(OptionTypeDTO::getId).distinct().collect(Collectors.toList()));
        List<Option> options = optionService.listFilter(optionDTO, null);
        for (OptionTypeDTO dto : dtos) {
            if (!CollectionUtils.isEmpty(options) && options.size()>0){
                List<Option> optionList = options.stream().filter(entity -> entity.getTypeId().equals(dto.getId())).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(optionList) && optionList.size()>0){
                    List<OptionDTO> dtoList = BeanUtils.copyAs(optionList, OptionDTO.class);
                    for (OptionDTO optionDTO1 : dtoList) {
                        optionDTO1.setTypeName(dto.getTitle());
                    }
                    dto.setOptions(dtoList);
                }
            }
        }
        //TODO
        return dtos;
    }

    @Override
    public List<OptionType> listFilter(OptionTypeDTO dto, Filter filter) {
        QueryWrapper<OptionType> wrapper = new QueryWrapper<>();
        addFilter(wrapper,dto,filter);
        return list(wrapper);
    }

    private void addFilter(QueryWrapper<OptionType> wrapper, OptionTypeDTO dto, Filter filter) {
        if (dto!=null){
            if (dto.getStatus()!=null){
                wrapper.eq("`Status`",dto.getStatus());
            }
            if (!StringUtils.isEmpty(dto.getTitle()) && dto.getTitle().length()>0){
                wrapper.eq("`title`",dto.getTitle());
            }
            if (dto.getTableOptionId()!=null){
                wrapper.eq("`tableOptionId`",dto.getTableOptionId());
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
    public BigInteger listCount(OptionTypeDTO dto, Filter filter) {
        QueryWrapper<OptionType> wrapper = new QueryWrapper<>();
        if (filter!=null){
            filter.setPage(null);
            filter.setPageSize(null);
        }
        addFilter(wrapper,dto,filter);
        return BigInteger.valueOf(baseMapper.selectCount(wrapper));
    }

    @Override
    public OptionTypeDTO getDetail(Integer id) {
        OptionType optionType = getById(id);
        if (optionType==null){
            throw new BusinessException(ResException.QUERY_MISS);
        }
        return getDTO(optionType);
    }

    private OptionTypeDTO getDTO(OptionType optionType) {
        OptionTypeDTO dto = BeanUtils.copyAs(optionType, OptionTypeDTO.class);

        OptionDTO optionDTO = new OptionDTO();
        optionDTO.setTypeId(optionType.getId());
        List<OptionDTO> dtos = optionService.listByFilter(optionDTO, null);
        if (!CollectionUtils.isEmpty(dtos) && dtos.size()>0){
            dto.setOptions(dtos);
        }
        return dto;
    }

    private void check(OptionTypeDTO dto) {
        if (!StringUtils.isEmpty(dto.getTitle()) && dto.getTitle().length()>0){
            QueryWrapper<OptionType> wrapper = new QueryWrapper<>();
            wrapper.eq("`title`",dto.getTitle());
            if (dto.getId()!=null){
                wrapper.ne("ID",dto.getId());
            }
            Integer count = baseMapper.selectCount(wrapper);
            if (count>0){
                throw new BusinessException(ResException.MAKE_ERR.getCode(),"名称重复");
            }
        }
    }
}
