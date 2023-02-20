package qichen.code.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.util.StringUtils;
import qichen.code.entity.ModelPushOption;
import qichen.code.entity.dto.ModelPushOptionDTO;
import qichen.code.mapper.ModelPushOptionMapper;
import qichen.code.model.Filter;
import qichen.code.service.IModelPushOptionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 模具入库点检事项表 (装配车间) 服务实现类
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-01-06
 */
@Service
public class ModelPushOptionServiceImpl extends ServiceImpl<ModelPushOptionMapper, ModelPushOption> implements IModelPushOptionService {

    @Override
    public List<ModelPushOption> commitModelPushLogBatch(List<String> list) {
        list = list.stream().distinct().collect(Collectors.toList());
        List<ModelPushOption> optionList = list.stream().map(detail -> {
            ModelPushOption option = new ModelPushOption();
            option.setDetail(detail);
            option.setCreateTime(LocalDateTime.now());
            return option;
        }).collect(Collectors.toList());
        saveBatch(optionList);
        return optionList;
    }

    @Override
    public List<ModelPushOption> listFilter(ModelPushOptionDTO dto, Filter filter) {
        QueryWrapper<ModelPushOption> wrapper = new QueryWrapper<>();
        addFilter(wrapper,dto,filter);
        return list(wrapper);
    }

    private void addFilter(QueryWrapper<ModelPushOption> wrapper, ModelPushOptionDTO dto, Filter filter) {
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
}
