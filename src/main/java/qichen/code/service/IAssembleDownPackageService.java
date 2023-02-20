package qichen.code.service;

import qichen.code.entity.AssembleDownPackage;
import com.baomidou.mybatisplus.extension.service.IService;
import qichen.code.entity.dto.AssembleDownPackageDTO;
import qichen.code.model.Filter;

import java.math.BigInteger;
import java.util.List;

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

    List<AssembleDownPackage> listFilter(AssembleDownPackageDTO packageDTO, Filter filter);

    List<AssembleDownPackageDTO> listByFilter(AssembleDownPackageDTO dto, Filter filter);

    BigInteger listCount(AssembleDownPackageDTO dto, Filter filter);

    AssembleDownPackageDTO getDetail(Integer id);

    AssembleDownPackage getByNumber(String number);
}
