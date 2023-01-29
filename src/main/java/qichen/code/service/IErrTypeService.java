package qichen.code.service;

import qichen.code.entity.ErrType;
import com.baomidou.mybatisplus.extension.service.IService;
import qichen.code.entity.dto.ErrTypeDTO;
import qichen.code.model.Filter;

import java.math.BigInteger;
import java.util.List;

/**
 * <p>
 * 错误类型表 服务类
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-01-29
 */
public interface IErrTypeService extends IService<ErrType> {

    List<ErrTypeDTO> listByFilter(ErrTypeDTO dto, Filter filter);

    BigInteger listCount(ErrTypeDTO dto, Filter filter);

    ErrTypeDTO getDetail(Integer id);

    ErrType add(ErrTypeDTO typeDTO);

    ErrType adminUpdate(ErrTypeDTO typeDTO);

    ErrType adminDelete(Integer id);
}
