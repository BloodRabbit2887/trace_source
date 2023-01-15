package qichen.code.service;

import qichen.code.entity.SparePartsSize;
import com.baomidou.mybatisplus.extension.service.IService;
import qichen.code.entity.dto.SparePartsSizeDTO;
import qichen.code.model.Filter;

import java.util.List;

/**
 * <p>
 * 零件检测尺寸特性表 服务类
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-12-19
 */
public interface ISparePartsSizeService extends IService<SparePartsSize> {

    List<SparePartsSizeDTO> listByFilter(SparePartsSizeDTO sparePartsSizeDTO, Filter filter);
}
