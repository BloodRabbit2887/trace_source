package qichen.code.service;

import qichen.code.entity.ModelInstall;
import com.baomidou.mybatisplus.extension.service.IService;
import qichen.code.entity.dto.ModelInstallDTO;

/**
 * <p>
 * 模具安装调试服务报告单 服务类
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-01-13
 */
public interface IModelInstallService extends IService<ModelInstall> {

    ModelInstall createWorkOrder(ModelInstallDTO dto);

    ModelInstallDTO getWorkOrderModel(Integer userId, String number);

    ModelInstallDTO getByNumber(String number, Integer draft);
}
