package qichen.code.service;

import qichen.code.entity.MouldBasePackage;
import com.baomidou.mybatisplus.extension.service.IService;
import qichen.code.entity.dto.MouldBasePackageDTO;
import qichen.code.model.Filter;

import java.math.BigInteger;
import java.util.List;

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

    MouldBasePackageDTO getAlloyModel(Integer userId, String nummber);

    List<MouldBasePackageDTO> listByFilter(MouldBasePackageDTO dto, Filter filter);

    BigInteger listCount(MouldBasePackageDTO dto, Filter filter);

    MouldBasePackageDTO getDetail(Integer id);

    List<MouldBasePackage> listFilter(MouldBasePackageDTO dto, Filter filter);

    MouldBasePackageDTO getAlloyDetailByNumber(String number, Integer userId);

    MouldBasePackageDTO getVerify(String number);

    MouldBasePackage getByNumber(String number);

    Integer getIdByAssembleOther(Integer userId, String number);
}
