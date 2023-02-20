package qichen.code.service;

import qichen.code.entity.AssembleCheckPackage;
import com.baomidou.mybatisplus.extension.service.IService;
import qichen.code.entity.dto.AssembleCheckPackageDTO;
import qichen.code.model.Filter;

import java.math.BigInteger;
import java.util.List;

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

    AssembleCheckPackageDTO getAlloyModel(Integer userId, String nummber);

    List<AssembleCheckPackageDTO> listByFilter(AssembleCheckPackageDTO dto, Filter filter);

    BigInteger listCount(AssembleCheckPackageDTO dto, Filter filter);

    AssembleCheckPackageDTO getDetail(Integer id);

    List<AssembleCheckPackage> listFilter(AssembleCheckPackageDTO packageDTO, Filter filter);

    AssembleCheckPackageDTO getAlloyDetailByNumber(String number, Integer userId);

    AssembleCheckPackageDTO getVerify(String number);

    AssembleCheckPackage getByNumber(String number);

    Integer getIdByAssembleOther(Integer userId, String number);
}
