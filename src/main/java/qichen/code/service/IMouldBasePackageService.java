package qichen.code.service;

import qichen.code.entity.MouldBasePackage;
import com.baomidou.mybatisplus.extension.service.IService;
import qichen.code.entity.dto.MouldBasePackageDTO;

/**
 * <p>
 * 模架组装组工作检查表(装配车间) 服务类
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-01-09
 */
public interface IMouldBasePackageService extends IService<MouldBasePackage> {

    MouldBasePackage add(MouldBasePackageDTO dto);

    MouldBasePackage verify(MouldBasePackageDTO dto, Integer userId);
}
