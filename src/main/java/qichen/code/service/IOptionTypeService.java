package qichen.code.service;

import qichen.code.entity.OptionType;
import com.baomidou.mybatisplus.extension.service.IService;
import qichen.code.entity.dto.OptionTypeDTO;
import qichen.code.model.Filter;

import java.math.BigInteger;
import java.util.List;

/**
 * <p>
 * 下拉选项类型表 服务类
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-12-19
 */
public interface IOptionTypeService extends IService<OptionType> {

    OptionType add(OptionTypeDTO dto);

    OptionType adminUpdate(OptionTypeDTO dto);

    OptionType adminDelete(Integer id);

    List<OptionTypeDTO> listByFilter(OptionTypeDTO dto, Filter filter);

    List<OptionTypeDTO> listDTO(List<OptionType> list);

    List<OptionType> listFilter(OptionTypeDTO dto, Filter filter);

    BigInteger listCount(OptionTypeDTO dto, Filter filter);

    OptionTypeDTO getDetail(Integer id);
}
