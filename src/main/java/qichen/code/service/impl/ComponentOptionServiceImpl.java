package qichen.code.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import qichen.code.entity.ComponentOption;
import qichen.code.entity.dto.ComponentOptionDTO;
import qichen.code.mapper.ComponentOptionMapper;
import qichen.code.model.Filter;
import qichen.code.service.IComponentOptionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import qichen.code.utils.BeanUtils;

import java.util.List;

/**
 * <p>
 * 装配车间部件检测项目表 服务实现类
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-01-05
 */
@Service
public class ComponentOptionServiceImpl extends ServiceImpl<ComponentOptionMapper, ComponentOption> implements IComponentOptionService {

    @Override
    public void removeByComponentID(Integer componentId) {
        UpdateWrapper<ComponentOption> wrapper = new UpdateWrapper<>();
        wrapper.eq("componentId",componentId);
        remove(wrapper);
    }

    @Override
    public List<ComponentOptionDTO> listByFilter(ComponentOptionDTO componentOptionDTO, Filter filter) {
        List<ComponentOption> list = listFilter(componentOptionDTO,filter);
        if (!CollectionUtils.isEmpty(list) && list.size()>0){
           return listDTO(list);
        }
        return null;
    }

    private List<ComponentOptionDTO> listDTO(List<ComponentOption> list) {
        List<ComponentOptionDTO> dtos = BeanUtils.copyAs(list, ComponentOptionDTO.class);

        //TODO

        return dtos;
    }

    private List<ComponentOption> listFilter(ComponentOptionDTO componentOptionDTO, Filter filter) {
        QueryWrapper<ComponentOption> wrapper = new QueryWrapper<>();
        addFilter(wrapper,componentOptionDTO,filter);
        return list(wrapper);
    }

    private void addFilter(QueryWrapper<ComponentOption> wrapper, ComponentOptionDTO optionDTO, Filter filter) {
        if (optionDTO!=null){
            if (optionDTO.getComponentId()!=null){
                wrapper.eq("componentId",optionDTO.getComponentId());
            }
            if (optionDTO.getNumber()!=null){
                wrapper.eq("`number`",optionDTO.getNumber());
            }
            if (optionDTO.getStatus()!=null){
                wrapper.eq("`Status`",optionDTO.getStatus());
            }
            if (!CollectionUtils.isEmpty(optionDTO.getComponentIds()) && optionDTO.getComponentIds().size()>0){
                wrapper.in("componentId",optionDTO.getComponentIds());
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
/*                wrapper.like("`number`",filter.getKeyword());*/
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
}
