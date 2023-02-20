package qichen.code.service;

import qichen.code.entity.AssembleModelPushPackage;
import com.baomidou.mybatisplus.extension.service.IService;
import qichen.code.entity.dto.AssembleModelPushPackageDTO;
import qichen.code.model.Filter;

import java.math.BigInteger;
import java.util.List;

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

    List<AssembleModelPushPackage> listFilter(AssembleModelPushPackageDTO dto, Filter filter);

    AssembleModelPushPackageDTO getModel(Integer userId, String number);

    List<AssembleModelPushPackageDTO> listByFilter(AssembleModelPushPackageDTO dto, Filter filter);

    BigInteger listCount(AssembleModelPushPackageDTO dto, Filter filter);

    AssembleModelPushPackage getByNumber(String number);
}
