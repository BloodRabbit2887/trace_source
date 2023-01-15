package qichen.code.service;

import qichen.code.entity.ModelPushOption;
import com.baomidou.mybatisplus.extension.service.IService;

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
}
