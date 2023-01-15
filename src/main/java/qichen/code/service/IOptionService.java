package qichen.code.service;

import qichen.code.entity.Option;
import com.baomidou.mybatisplus.extension.service.IService;
import qichen.code.entity.dto.OptionDTO;
import qichen.code.model.Filter;

import java.math.BigInteger;
import java.util.List;

/**
 * <p>
 * 下拉选项表 服务类
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-12-19
 */
public interface IOptionService extends IService<Option> {

    void removeByTypeId(Integer typeId);

    Option add(OptionDTO dto);

    Option adminUpdate(OptionDTO dto);

    Option adminDelete(Integer id);

    List<OptionDTO> listByFilter(OptionDTO optionDTO, Filter filter);

    List<Option> listFilter(OptionDTO optionDTO, Filter filter);

    BigInteger listCount(OptionDTO optionDTO, Filter filter);

    OptionDTO getDetail(Integer id);
}
