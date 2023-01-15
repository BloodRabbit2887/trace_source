package qichen.code.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import qichen.code.entity.Option;
import qichen.code.entity.SparePartsSize;
import qichen.code.entity.dto.SparePartsSizeDTO;
import qichen.code.mapper.SparePartsSizeMapper;
import qichen.code.model.Filter;
import qichen.code.service.IOptionService;
import qichen.code.service.ISparePartsSizeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import qichen.code.utils.BeanUtils;

import java.util.List;

/**
 * <p>
 * 零件检测尺寸特性表 服务实现类
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-12-19
 */
@Service
public class SparePartsSizeServiceImpl extends ServiceImpl<SparePartsSizeMapper, SparePartsSize> implements ISparePartsSizeService {

    @Autowired
    private IOptionService optionService;

    @Override
    public List<SparePartsSizeDTO> listByFilter(SparePartsSizeDTO sparePartsSizeDTO, Filter filter) {
        List<SparePartsSize> list = listFilter(sparePartsSizeDTO,filter);
        if (!CollectionUtils.isEmpty(list) && list.size()>0){
           return listDTO(list);
        }
        return null;
    }

    private List<SparePartsSize> listFilter(SparePartsSizeDTO sparePartsSizeDTO, Filter filter) {
        QueryWrapper<SparePartsSize> wrapper = new QueryWrapper<>();
        addFilter(wrapper,sparePartsSizeDTO,filter);
        return list(wrapper);
    }

    private void addFilter(QueryWrapper<SparePartsSize> wrapper, SparePartsSizeDTO dto, Filter filter) {
        if (dto!=null){
            if (dto.getLogId()!=null){
                wrapper.eq("`logId`",dto.getLogId());
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

    private List<SparePartsSizeDTO> listDTO(List<SparePartsSize> list) {

        List<SparePartsSizeDTO> dtos = BeanUtils.copyAs(list, SparePartsSizeDTO.class);

        List<Option> options = optionService.list();
        for (SparePartsSizeDTO dto : dtos) {
            if (!CollectionUtils.isEmpty(options) && options.size()>0){
                for (Option option : options) {
                    if (dto.getToolId().equals(option.getId())){
                        dto.setToolName(option.getTitle());
                    }
                }
            }
        }

        return dtos;

    }
}
