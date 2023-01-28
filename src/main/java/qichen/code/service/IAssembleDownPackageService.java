package qichen.code.service;

import qichen.code.entity.AssembleDownPackage;
import com.baomidou.mybatisplus.extension.service.IService;
import qichen.code.entity.dto.AssembleDownPackageDTO;

/**
 * <p>
 * 模架组下模座垫板工作检查表 (装配车间) 服务类
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-01-06
 */
public interface IAssembleDownPackageService extends IService<AssembleDownPackage> {

    AssembleDownPackage add(AssembleDownPackageDTO dto);

    AssembleDownPackage verify(Integer id, Integer userId, Integer status, String remark);

    AssembleDownPackageDTO getAlloyModel(Integer userId, String nummber);
}
