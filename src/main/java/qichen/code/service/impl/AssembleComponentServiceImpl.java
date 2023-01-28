package qichen.code.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import qichen.code.entity.AssembleComponent;
import qichen.code.entity.ComponentOption;
import qichen.code.entity.dto.AssembleComponentDTO;
import qichen.code.entity.dto.ComponentOptionDTO;
import qichen.code.exception.BusinessException;
import qichen.code.exception.ResException;
import qichen.code.mapper.AssembleComponentMapper;
import qichen.code.model.Filter;
import qichen.code.service.IAssembleComponentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import qichen.code.service.IComponentOptionService;
import qichen.code.utils.BeanUtils;
import qichen.code.utils.JsonUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 装配车间检测部件表 服务实现类
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-01-05
 */
@Service
public class AssembleComponentServiceImpl extends ServiceImpl<AssembleComponentMapper, AssembleComponent> implements IAssembleComponentService {

    @Autowired
    private IComponentOptionService componentOptionService;

    @Transactional
    @Override
    public AssembleComponent add(AssembleComponentDTO assembleComponentDTO) {
        Map<String,String> params = new HashMap<>();
        params.put("name","名称");
        params.put("checkType","检查表归属");
        JsonUtils.checkColumnNull(params, JSONObject.parseObject(JSON.toJSONString(assembleComponentDTO)));
        checkAlready(assembleComponentDTO);

        AssembleComponent assembleComponent = BeanUtils.copyAs(assembleComponentDTO, AssembleComponent.class);
        save(assembleComponent);

        List<ComponentOptionDTO> componentOptions = assembleComponentDTO.getComponentOptions();
        if (!CollectionUtils.isEmpty(componentOptions) && componentOptions.size()>0){
            for (ComponentOptionDTO componentOption : componentOptions) {
                componentOption.setComponentId(assembleComponent.getId());
            }
            List<ComponentOption> options = BeanUtils.copyAs(componentOptions, ComponentOption.class);
            componentOptionService.saveBatch(options);
        }

        return assembleComponent;
    }

    @Override
    public AssembleComponent adminUpdate(AssembleComponentDTO assembleComponentDTO) {
        checkAlready(assembleComponentDTO);
        AssembleComponent assembleComponent = BeanUtils.copyAs(assembleComponentDTO, AssembleComponent.class);
        updateById(assembleComponent);

        List<ComponentOptionDTO> componentOptions = assembleComponentDTO.getComponentOptions();
        if (!CollectionUtils.isEmpty(componentOptions) && componentOptions.size()>0){
            for (ComponentOptionDTO componentOption : componentOptions) {
                componentOption.setComponentId(assembleComponent.getId());
            }
            List<ComponentOption> options = BeanUtils.copyAs(componentOptions, ComponentOption.class);
            componentOptionService.saveOrUpdateBatch(options);
        }

        return assembleComponent;
    }

    @Transactional
    @Override
    public AssembleComponent delete(Integer id) {
        AssembleComponent component = getById(id);
        if (component==null){
            throw new BusinessException(ResException.QUERY_MISS);
        }
        removeById(id);
        componentOptionService.removeByComponentID(id);
        return component;
    }

    @Override
    public List<AssembleComponentDTO> listDTO(List<AssembleComponent> components) {

        List<AssembleComponentDTO> dtos = BeanUtils.copyAs(components, AssembleComponentDTO.class);

        List<Integer> componentIds = components.stream().map(AssembleComponent::getId).distinct().collect(Collectors.toList());

        ComponentOptionDTO componentOptionDTO = new ComponentOptionDTO();
        componentOptionDTO.setComponentIds(componentIds);
        componentOptionDTO.setStatus(0);
        List<ComponentOptionDTO> optionDTOS = componentOptionService.listByFilter(componentOptionDTO,new Filter());

        for (AssembleComponentDTO dto : dtos) {
            if (!CollectionUtils.isEmpty(optionDTOS) && optionDTOS.size()>0){
                List<ComponentOptionDTO> dtoList = optionDTOS.stream().filter(optionDTO -> optionDTO.getComponentId().equals(dto.getId())).distinct().collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(dtoList) && dtoList.size()>0){
                    dto.setComponentOptions(dtoList);
                }
            }
        }

        return dtos;
    }

    @Override
    public List<AssembleComponentDTO> listByFilter(AssembleComponentDTO dto, Filter filter) {
        List<AssembleComponent> list = listFilter(dto,filter);
        if (!CollectionUtils.isEmpty(list) && list.size()>0){
           return listDTO(list);
        }
        return null;
    }

    private List<AssembleComponent> listFilter(AssembleComponentDTO dto, Filter filter) {
        QueryWrapper<AssembleComponent> wrapper = new QueryWrapper<>();
        addFilter(wrapper,dto,filter);
        return list(wrapper);
    }

    private void addFilter(QueryWrapper<AssembleComponent> wrapper, AssembleComponentDTO dto, Filter filter) {
        if (dto!=null){
            if (dto.getCheckType()!=null){
                wrapper.eq("`checkType`",dto.getCheckType());
            }
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

    private void checkAlready(AssembleComponentDTO assembleComponentDTO) {
        QueryWrapper<AssembleComponent> wrapper = new QueryWrapper<>();
        wrapper.eq("`name`",assembleComponentDTO.getName());
        wrapper.eq("checkType",assembleComponentDTO.getCheckType());
        if (assembleComponentDTO.getId()!=null){
            wrapper.ne("ID",assembleComponentDTO.getId());
        }
        Integer count = baseMapper.selectCount(wrapper);
        if (count>0){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"名称重复");
        }
    }
}
