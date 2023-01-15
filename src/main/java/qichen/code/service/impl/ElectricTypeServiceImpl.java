package qichen.code.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import qichen.code.entity.ElectricType;
import qichen.code.entity.dto.ElectricTypeDTO;
import qichen.code.entity.dto.WorkOrderDTO;
import qichen.code.exception.BusinessException;
import qichen.code.exception.ResException;
import qichen.code.mapper.ElectricTypeMapper;
import qichen.code.model.Filter;
import qichen.code.service.IElectricTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import qichen.code.service.IWorkOrderService;
import qichen.code.utils.BeanUtils;

import java.math.BigInteger;

/**
 * <p>
 * 电机表 服务实现类
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-12-19
 */
@Service
public class ElectricTypeServiceImpl extends ServiceImpl<ElectricTypeMapper, ElectricType> implements IElectricTypeService {

    @Autowired
    private IWorkOrderService workOrderService;

    @Override
    public ElectricType add(ElectricTypeDTO dto) {
        check(dto);
        ElectricType type = BeanUtils.copyAs(dto, ElectricType.class);
        save(type);
        return type;
    }

    @Override
    public ElectricType adminUpdate(ElectricTypeDTO dto) {
        ElectricType electricType = getById(dto.getId());
        if (electricType==null){
            throw new BusinessException(ResException.QUERY_MISS);
        }
        check(dto);
        ElectricType type = BeanUtils.copyAs(dto, ElectricType.class);
        updateById(type);
        return type;
    }

    @Override
    public ElectricType adminDelete(Integer id) {
        ElectricType electricType = getById(id);
        if (electricType==null){
            throw new BusinessException(ResException.QUERY_MISS);
        }

        //TODO
        WorkOrderDTO workOrderDTO = new WorkOrderDTO();
        workOrderDTO.setElectricTypeId(id);
        BigInteger count = workOrderService.listCount(workOrderDTO,new Filter());
        if (count.compareTo(BigInteger.ZERO)>0){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"电机类型已被占用");
        }

        removeById(id);
        return electricType;
    }

    private void check(ElectricTypeDTO dto) {
        if (!StringUtils.isEmpty(dto.getTitle()) && dto.getTitle().length()>0){
            QueryWrapper<ElectricType> wrapper = new QueryWrapper<>();
            wrapper.eq("`title`",dto.getTitle());
            if (dto.getId()!=null){
                wrapper.ne("ID",dto.getId());
            }
            Integer count = baseMapper.selectCount(wrapper);
            if (count>0){
                throw new BusinessException(ResException.MAKE_ERR.getCode(),"电机名称重复");
            }
        }
    }
}
