package qichen.code.service;

import qichen.code.entity.ErrModel;
import com.baomidou.mybatisplus.extension.service.IService;
import qichen.code.entity.dto.ErrModelDTO;
import qichen.code.model.Filter;

import java.math.BigInteger;
import java.util.List;

/**
 * <p>
 * 错误典型库表 服务类
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-01-29
 */
public interface IErrModelService extends IService<ErrModel> {

    ErrModel commit(ErrModelDTO dto);

    List<ErrModelDTO> listByFilter(ErrModelDTO dto, Filter filter);

    BigInteger listCount(ErrModelDTO dto, Filter filter);

    ErrModelDTO getDetail(Integer id);

    ErrModel verify(ErrModelDTO modelDTO);

    void removeByTypeId(Integer typeId);

    ErrModel delete(Integer id);
}
