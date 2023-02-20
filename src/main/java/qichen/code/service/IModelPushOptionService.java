package qichen.code.service;

import qichen.code.entity.ModelPushOption;
import com.baomidou.mybatisplus.extension.service.IService;
import qichen.code.entity.dto.ModelPushOptionDTO;
import qichen.code.model.Filter;

import java.util.List;

/**
 * <p>
 * 模具入库点检事项表 (装配车间) 服务类
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-01-06
 */
public interface IModelPushOptionService extends IService<ModelPushOption> {

    List<ModelPushOption> commitModelPushLogBatch(List<String> list);

    List<ModelPushOption> listFilter(ModelPushOptionDTO dto, Filter filter);
}
