package qichen.code.service;

import qichen.code.entity.SubmitModelPushOption;
import com.baomidou.mybatisplus.extension.service.IService;
import qichen.code.entity.dto.AssembleModelPushPackageDTO;
import qichen.code.entity.dto.SubmitModelPushOptionDTO;
import qichen.code.model.Filter;

import java.util.List;

/**
 * <p>
 * 模具入库点检事项提交表 (装配车间) 服务类
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-01-06
 */
public interface ISubmitModelPushOptionService extends IService<SubmitModelPushOption> {

    List<SubmitModelPushOption> listFilter(SubmitModelPushOptionDTO dto, Filter filter);
}
