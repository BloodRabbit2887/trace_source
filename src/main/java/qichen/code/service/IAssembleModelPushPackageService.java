package qichen.code.service;

import qichen.code.entity.AssembleModelPushPackage;
import com.baomidou.mybatisplus.extension.service.IService;
import qichen.code.entity.dto.AssembleModelPushPackageDTO;

/**
 * <p>
 * 合金组装组扭转部位工作检查表(装配车间) 服务类
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-01-06
 */
public interface IAssembleModelPushPackageService extends IService<AssembleModelPushPackage> {

    AssembleModelPushPackage add(AssembleModelPushPackageDTO dto);

    AssembleModelPushPackage verify(Integer id, Integer userId, Integer status, String remark);
}
