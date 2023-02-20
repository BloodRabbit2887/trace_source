package qichen.code.service;

import qichen.code.entity.AssembleCheckAlloyPackage;
import com.baomidou.mybatisplus.extension.service.IService;
import qichen.code.entity.dto.AssembleCheckAlloyPackageDTO;
import qichen.code.model.Filter;

import java.math.BigInteger;
import java.util.List;

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

    List<AssembleCheckAlloyPackage> listFilter(AssembleCheckAlloyPackageDTO packageDTO, Filter filter);

    List<AssembleCheckAlloyPackageDTO> listByFilter(AssembleCheckAlloyPackageDTO dto, Filter filter);

    BigInteger listCount(AssembleCheckAlloyPackageDTO dto, Filter filter);

    AssembleCheckAlloyPackageDTO getAlloyDetail(Integer id);

    AssembleCheckAlloyPackageDTO getAlloyDetailByNumber(String number, Integer userId);

    AssembleCheckAlloyPackage getByNumber(String number);
}
