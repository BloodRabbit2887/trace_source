package qichen.code.service;

import qichen.code.entity.AssemblePlankPackage;
import com.baomidou.mybatisplus.extension.service.IService;
import qichen.code.entity.dto.AssemblePlankPackageDTO;
import qichen.code.model.Filter;

import java.math.BigInteger;
import java.util.List;

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

    List<AssemblePlankPackage> listFilter(AssemblePlankPackageDTO dto, Filter filter);

    List<AssemblePlankPackageDTO> listByFilter(AssemblePlankPackageDTO dto, Filter filter);

    BigInteger listCount(AssemblePlankPackageDTO dto, Filter filter);

    AssemblePlankPackageDTO getDetail(Integer id);

    AssemblePlankPackageDTO getAlloyDetailByNumber(String number, Integer userId);

    AssemblePlankPackage getByNumber(String number);
}
