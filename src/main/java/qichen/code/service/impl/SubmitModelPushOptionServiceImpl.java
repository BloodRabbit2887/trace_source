package qichen.code.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.util.StringUtils;
import qichen.code.entity.SubmitModelPushOption;
import qichen.code.entity.dto.AssembleModelPushPackageDTO;
import qichen.code.entity.dto.SubmitComponentOptionDTO;
import qichen.code.entity.dto.SubmitModelPushOptionDTO;
import qichen.code.mapper.SubmitModelPushOptionMapper;
import qichen.code.model.Filter;
import qichen.code.service.ISubmitModelPushOptionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 模具入库点检事项提交表 (装配车间) 服务实现类
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-01-06
 */
@Service
public class SubmitModelPushOptionServiceImpl extends ServiceImpl<SubmitModelPushOptionMapper, SubmitModelPushOption> implements ISubmitModelPushOptionService {

    @Override
    public List<SubmitModelPushOption> listFilter(SubmitModelPushOptionDTO dto, Filter filter) {
        QueryWrapper<SubmitModelPushOption> wrapper = new QueryWrapper<>();
        addFilter(wrapper,dto,filter);
        return list(wrapper);
    }

    private void addFilter(QueryWrapper<SubmitModelPushOption> wrapper, SubmitModelPushOptionDTO dto, Filter filter) {
        if (dto!=null){
            if (dto.getPackageId()!=null){
                wrapper.eq("`packageId`",dto.getPackageId());
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
