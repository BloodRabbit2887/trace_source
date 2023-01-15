package qichen.code.service;

import qichen.code.entity.ElectricType;
import com.baomidou.mybatisplus.extension.service.IService;
import qichen.code.entity.dto.ElectricTypeDTO;

/**
 * <p>
 * 电机表 服务类
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-12-19
 */
public interface IElectricTypeService extends IService<ElectricType> {

    ElectricType add(ElectricTypeDTO dto);

    ElectricType adminUpdate(ElectricTypeDTO dto);

    ElectricType adminDelete(Integer id);
}
