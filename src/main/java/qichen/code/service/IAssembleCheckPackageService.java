package qichen.code.service;

import qichen.code.entity.AssembleCheckPackage;
import com.baomidou.mybatisplus.extension.service.IService;
import qichen.code.entity.dto.AssembleCheckPackageDTO;

/**
 * <p>
 * 合金组装组工作检查表(装配车间) 服务类
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-01-06
 */
public interface IAssembleCheckPackageService extends IService<AssembleCheckPackage> {

    AssembleCheckPackage add(AssembleCheckPackageDTO dto);

    AssembleCheckPackage verify(Integer id, Integer userId, Integer status, String remark);
}
