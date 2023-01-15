package qichen.code.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import qichen.code.entity.Option;
import qichen.code.entity.SubmitTableOptions;
import qichen.code.entity.TableOptions;
import qichen.code.entity.dto.SubmitTableOptionDTO;
import qichen.code.mapper.SubmitTableOptionsMapper;
import qichen.code.model.Filter;
import qichen.code.service.IOptionService;
import qichen.code.service.ISubmitTableOptionsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import qichen.code.service.ITableOptionsService;
import qichen.code.utils.BeanUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 表单提交选项表 服务实现类
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-01-11
 */
@Service
public class SubmitTableOptionsServiceImpl extends ServiceImpl<SubmitTableOptionsMapper, SubmitTableOptions> implements ISubmitTableOptionsService {

    @Autowired
    private ITableOptionsService tableOptionsService;
    @Autowired
    private IOptionService optionService;

    @Override
    public void removeByOptionId(Integer optionId) {
        UpdateWrapper<SubmitTableOptions> wrapper = new UpdateWrapper<>();
        wrapper.eq("`optionId`",optionId);
        remove(wrapper);
    }

    @Override
    public List<SubmitTableOptions> listFilter(SubmitTableOptionDTO optionDTO, Filter filter) {
        QueryWrapper<SubmitTableOptions> wrapper = new QueryWrapper<>();
        addFilter(wrapper,optionDTO,filter);
        return list(wrapper);
    }

    @Override
    public List<SubmitTableOptionDTO> listDTO(List<SubmitTableOptions> list) {
        List<SubmitTableOptionDTO> dtos = BeanUtils.copyAs(list, SubmitTableOptionDTO.class);
        List<TableOptions> options = (List<TableOptions>) tableOptionsService.listByIds(list.stream().map(SubmitTableOptions::getTableOptionId).distinct().collect(Collectors.toList()));

        List<Option> optionList = optionService.list();

        for (SubmitTableOptionDTO dto : dtos) {
            if (!CollectionUtils.isEmpty(optionList) && optionList.size()>0){
                for (Option option : optionList) {
                    if (dto.getSubmitAnswerId()!=null && option.getId().equals(dto.getSubmitAnswerId())){
                        dto.setSubmitOptionName(option.getTitle());
                    }
                }
            }
            if (!CollectionUtils.isEmpty(options) && options.size()>0){
                for (TableOptions option : options) {
                    if (option.getId().equals(dto.getTableOptionId())){
                        dto.setTitle(option.getTitle());
                        dto.setType(option.getType());
                        dto.setTableType(option.getTableType());
                        dto.setLevel(option.getLevel());
                        dto.setMust(option.getMust());
                    }
                }
            }
        }

        return dtos;
    }

    private void addFilter(QueryWrapper<SubmitTableOptions> wrapper, SubmitTableOptionDTO dto, Filter filter) {
        if (dto!=null){
            if (dto.getTableType()!=null){
                wrapper.eq("tableType",dto.getTableType());
            }
            if (dto.getOrderId()!=null){
                wrapper.eq("orderId",dto.getOrderId());
            }
            if (dto.getTableOptionId()!=null){
                wrapper.eq("`optionId`",dto.getTableOptionId());
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
