package qichen.code.service;

import qichen.code.entity.SubmitTableOptions;
import com.baomidou.mybatisplus.extension.service.IService;
import qichen.code.entity.dto.SubmitTableOptionDTO;
import qichen.code.model.Filter;

import java.util.List;

/**
 * <p>
 * 表单提交选项表 服务类
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-01-11
 */
public interface ISubmitTableOptionsService extends IService<SubmitTableOptions> {

    void removeByOptionId(Integer optionId);

    List<SubmitTableOptions> listFilter(SubmitTableOptionDTO optionDTO, Filter filter);

    List<SubmitTableOptionDTO> listDTO(List<SubmitTableOptions> list);
}
