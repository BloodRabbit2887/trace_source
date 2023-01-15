package qichen.code.service;

import qichen.code.entity.Dept;
import com.baomidou.mybatisplus.extension.service.IService;
import qichen.code.entity.dto.DeptDTO;
import qichen.code.model.Filter;

import java.math.BigInteger;
import java.util.List;

/**
 * <p>
 * 部门表 服务类
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-12-16
 */
public interface IDeptService extends IService<Dept> {

    Dept add(DeptDTO deptDTO);

    List<DeptDTO> listByFilter(DeptDTO deptDTO, Filter filter);

    List<DeptDTO> listDTO(List<Dept> list);

    List<Dept> listFilter(DeptDTO deptDTO, Filter filter);

    BigInteger listCount(DeptDTO deptDTO, Filter filter);

    Dept adminUpdate(DeptDTO deptDTO);

    Dept delete(Integer id);

    DeptDTO getDetail(Integer id);
}
