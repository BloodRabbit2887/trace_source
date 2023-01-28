package qichen.code.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.util.StringUtils;
import qichen.code.entity.SubmitComponentOption;
import qichen.code.entity.dto.SubmitComponentOptionDTO;
import qichen.code.mapper.SubmitComponentOptionMapper;
import qichen.code.model.Filter;
import qichen.code.service.ISubmitComponentOptionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 装配车间部件检测结果表 服务实现类
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-01-05
 */
@Service
public class SubmitComponentOptionServiceImpl extends ServiceImpl<SubmitComponentOptionMapper, SubmitComponentOption> implements ISubmitComponentOptionService {

    @Override
    public List<SubmitComponentOption> listFilter(SubmitComponentOptionDTO dto, Filter filter) {
        QueryWrapper<SubmitComponentOption> wrapper = new QueryWrapper<>();
        addFilter(wrapper,dto,filter);
        return list(wrapper);
    }

    private void addFilter(QueryWrapper<SubmitComponentOption> wrapper, SubmitComponentOptionDTO dto, Filter filter) {
        if (dto!=null){
            if (dto.getOptionId()!=null){
                wrapper.eq("`optionId`",dto.getOptionId());
            }
            if (dto.getCheckTableId()!=null){
                wrapper.eq("`checkTableId`",dto.getCheckTableId());
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
