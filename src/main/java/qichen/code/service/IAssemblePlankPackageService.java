package qichen.code.service;

import qichen.code.entity.AssemblePlankPackage;
import com.baomidou.mybatisplus.extension.service.IService;
import qichen.code.entity.dto.AssemblePlankPackageDTO;

/**
 * <p>
 * 模架组导槽板工作检查表(装配车间) 服务类
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-01-06
 */
public interface IAssemblePlankPackageService extends IService<AssemblePlankPackage> {

    AssemblePlankPackage add(AssemblePlankPackageDTO dto);

    AssemblePlankPackage verify(Integer id, Integer userId, Integer status, String remark);

    AssemblePlankPackageDTO getAlloyModel(Integer userId, String nummber);
}
