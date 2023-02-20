package qichen.code.service;

import qichen.code.entity.UserTableProject;
import com.baomidou.mybatisplus.extension.service.IService;
import qichen.code.entity.dto.UserTableProjectDTO;
import qichen.code.model.AssembleProjectModel;
import qichen.code.model.DistributionStatusDTO;
import qichen.code.model.Filter;

import java.math.BigInteger;
import java.util.List;

/**
 * <p>
 * 用户工单分配任务表 服务类
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-02-15
 */
public interface IUserTableProjectService extends IService<UserTableProject> {

    void add(UserTableProjectDTO dto);

    void delete(Integer id);

    List<UserTableProjectDTO> listByFilter(UserTableProjectDTO dto, Filter filter);

    List<UserTableProject> listFilter(UserTableProjectDTO dto, Filter filter);

    BigInteger listCount(UserTableProjectDTO dto, Filter filter);

    UserTableProjectDTO getDetail(Integer id);

    List<DistributionStatusDTO> tableTypes(Integer userId, String number);

    void updateStatus(String number, Integer userId, Integer status,Integer tableType);

    AssembleProjectModel getProjectByNumber(Integer userId,Integer deptId, String number);

    void cancelByTableType(String number, Integer tableType, Integer deptId);

    void linkChange(Integer deptId, Integer tableType, String number);
}
