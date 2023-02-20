package qichen.code.service;

import qichen.code.entity.Basics;
import com.baomidou.mybatisplus.extension.service.IService;
import qichen.code.entity.dto.BasicsDTO;
import qichen.code.model.Filter;

import java.math.BigInteger;
import java.util.List;

/**
 * <p>
 * 基础库表 服务类
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-01-30
 */
public interface IBasicsService extends IService<Basics> {

    Basics add(BasicsDTO dto);

    List<BasicsDTO> listByFilter(BasicsDTO dto, Filter filter);

    BigInteger listCount(BasicsDTO dto, Filter filter);

    BasicsDTO getDetail(Integer id);

    Basics adminUpdate(BasicsDTO dto);

    Basics delete(Integer id);

    BasicsDTO getDraft(Integer id);
}
