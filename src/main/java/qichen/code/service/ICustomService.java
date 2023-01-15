package qichen.code.service;

import qichen.code.entity.Custom;
import com.baomidou.mybatisplus.extension.service.IService;
import qichen.code.entity.dto.CustomDTO;
import qichen.code.model.Filter;

import java.math.BigInteger;
import java.util.List;

/**
 * <p>
 * 客户管理表 服务类
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-12-16
 */
public interface ICustomService extends IService<Custom> {

    List<CustomDTO> listByFilter(CustomDTO customDTO, Filter filter);

    List<Custom> listFilter(CustomDTO customDTO, Filter filter);

    BigInteger listCount(CustomDTO customDTO, Filter filter);

    Custom add(CustomDTO customDTO);

    Custom adminUpdate(CustomDTO customDTO);

    CustomDTO getDetail(Integer id);

    Custom delete(Integer id);
}
