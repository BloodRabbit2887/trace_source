package qichen.code.service;

import qichen.code.entity.DeptRole;
import com.baomidou.mybatisplus.extension.service.IService;
import qichen.code.entity.dto.DeptRoleDTO;
import qichen.code.model.Filter;

import java.math.BigInteger;
import java.util.List;

/**
 * <p>
 * 部门角色表 服务类
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-02-15
 */
public interface IDeptRoleService extends IService<DeptRole> {

    DeptRole add(DeptRoleDTO dto);

    List<DeptRoleDTO> listByFilter(DeptRoleDTO dto, Filter filter);

    List<DeptRole> listFilter(DeptRoleDTO dto, Filter filter);

    BigInteger listCount(DeptRoleDTO dto, Filter filter);

    DeptRoleDTO getDetail(Integer id);

    DeptRole adminUpdate(DeptRoleDTO dto);

    DeptRole delete(Integer id);

    DeptRole getByAdd(Integer deptId,Integer deptRoleId);
}
