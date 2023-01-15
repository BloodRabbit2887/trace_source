package qichen.code.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
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
import qichen.code.mapper.OptionMapper;
import qichen.code.model.Filter;
import qichen.code.service.IOptionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import qichen.code.service.IOptionTypeService;
import qichen.code.utils.BeanUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 下拉选项表 服务实现类
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-12-19
 */
@Service
public class OptionServiceImpl extends ServiceImpl<OptionMapper, Option> implements IOptionService {

    @Autowired
    private IOptionTypeService optionTypeService;

    @Transactional
    @Override
    public void removeByTypeId(Integer typeId) {
        UpdateWrapper<Option> wrapper = new UpdateWrapper<>();
        wrapper.eq("typeId",typeId);
        remove(wrapper);
    }

    @Transactional
    @Override
    public Option add(OptionDTO dto) {
        check(dto);
        Option option = BeanUtils.copyAs(dto, Option.class);
        save(option);
        return option;
    }

    @Transactional
    @Override
    public Option adminUpdate(OptionDTO dto) {
        Option option = getById(dto.getId());
        if (option==null){
            throw new BusinessException(ResException.QUERY_MISS);
        }
        check(dto);
        Option entity = BeanUtils.copyAs(dto, Option.class);
        updateById(entity);
        return entity;
    }

    @Override
    public Option adminDelete(Integer id) {
        Option option = getById(id);
        if (option==null){
            throw new BusinessException(ResException.QUERY_MISS);
        }
        removeById(id);
        return option;
    }

    @Override
    public List<OptionDTO> listByFilter(OptionDTO optionDTO, Filter filter) {
        List<Option> list = listFilter(optionDTO,filter);
        if (!CollectionUtils.isEmpty(list) && list.size()>0){
           return listDTO(list);
        }
        return null;
    }

    private List<OptionDTO> listDTO(List<Option> list) {
        List<OptionDTO> dtos = BeanUtils.copyAs(list, OptionDTO.class);
        //TODO
        return dtos;
    }

    @Override
    public List<Option> listFilter(OptionDTO optionDTO, Filter filter) {
        QueryWrapper<Option> wrapper = new QueryWrapper<>();
        addFilter(wrapper,optionDTO,filter);
        return list(wrapper);
    }

    private void addFilter(QueryWrapper<Option> wrapper, OptionDTO optionDTO, Filter filter) {
        if (optionDTO!=null){
            if (optionDTO.getStatus()!=null){
                wrapper.eq("`Status`",optionDTO.getStatus());
            }
            if (optionDTO.getTypeId()!=null){
                wrapper.eq("`typeId`",optionDTO.getTypeId());
            }
            if(!StringUtils.isEmpty(optionDTO.getTitle()) && optionDTO.getTitle().length()>0){
                wrapper.eq("`title`",optionDTO.getTitle());
            }
            if (optionDTO.getTableOptionId()!=null){
                List<Integer> typeIds = new ArrayList<>();
                typeIds.add(0);
                OptionTypeDTO dto = new OptionTypeDTO();
                dto.setTableOptionId(optionDTO.getTableOptionId());
                List<OptionType> list = optionTypeService.listFilter(dto, null);
                if (!CollectionUtils.isEmpty(list) && list.size()>0){
                    typeIds.addAll(list.stream().map(OptionType::getId).distinct().collect(Collectors.toList()));
                }
                wrapper.in("`typeId`",typeIds);
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
    public BigInteger listCount(OptionDTO optionDTO, Filter filter) {
        QueryWrapper<Option> wrapper = new QueryWrapper<>();
        if (filter!=null){
            filter.setPage(null);
            filter.setPageSize(null);
        }
        addFilter(wrapper,optionDTO,filter);
        return BigInteger.valueOf(baseMapper.selectCount(wrapper));
    }

    @Override
    public OptionDTO getDetail(Integer id) {
        Option option = getById(id);
        if (option==null){
            throw new BusinessException(ResException.QUERY_MISS);
        }
        return getDTO(option);
    }

    private OptionDTO getDTO(Option option) {

        OptionDTO dto = BeanUtils.copyAs(option, OptionDTO.class);

        OptionType optionType = optionTypeService.getById(option.getTypeId());
        if (optionType!=null){
            dto.setTypeName(optionType.getTitle());
        }else {
            dto.setTypeName("类型已删除");
        }

        return dto;
    }

    private void check(OptionDTO dto) {
        if (dto.getTypeId()==null){
            if (dto.getId()==null){
                throw new BusinessException(ResException.MAKE_ERR.getCode(),"类型不能为空");
            }
        }else {
            OptionType optionType = optionTypeService.getById(dto.getTypeId());
            if (optionType==null){
                throw new BusinessException(ResException.MAKE_ERR.getCode(),"类型信息有误");
            }
        }

/*        if (!StringUtils.isEmpty(dto.getTitle()) && dto.getTitle().length()>0){
            QueryWrapper<Option> wrapper = new QueryWrapper<>();
            wrapper.eq("`title`",dto.getTitle());
            if (dto.getId()!=null){
                wrapper.ne("ID",dto.getId());
            }
            Integer count = baseMapper.selectCount(wrapper);
            if (count>0){
                throw new BusinessException(ResException.MAKE_ERR.getCode(),"名称重复");
            }
        }*/
    }
}
