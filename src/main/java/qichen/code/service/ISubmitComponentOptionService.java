package qichen.code.service;

import qichen.code.entity.SubmitComponentOption;
import com.baomidou.mybatisplus.extension.service.IService;
import qichen.code.entity.dto.SubmitComponentOptionDTO;
import qichen.code.model.Filter;

import java.util.List;

/**
 * <p>
 * 装配车间部件检测结果表 服务类
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-01-05
 */
public interface ISubmitComponentOptionService extends IService<SubmitComponentOption> {

    List<SubmitComponentOption> listFilter(SubmitComponentOptionDTO dto, Filter filter);
}
