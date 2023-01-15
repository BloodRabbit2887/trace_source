package qichen.code.service;

import qichen.code.entity.AssembleCheckAlloyPackage;
import com.baomidou.mybatisplus.extension.service.IService;
import qichen.code.entity.dto.AssembleCheckAlloyPackageDTO;

/**
 * <p>
 * 合金组装组扭转部位工作检查表(装配车间) 服务类
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-01-05
 */
public interface IAssembleCheckAlloyPackageService extends IService<AssembleCheckAlloyPackage> {

    AssembleCheckAlloyPackage add(AssembleCheckAlloyPackageDTO dto);

    AssembleCheckAlloyPackage verify(Integer id, Integer userId, Integer status, String remark);

    AssembleCheckAlloyPackageDTO getAlloyModel(Integer userId, String nummber);
}
