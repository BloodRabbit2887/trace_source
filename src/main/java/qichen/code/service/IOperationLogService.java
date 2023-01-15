package qichen.code.service;

import qichen.code.entity.OperationLog;
import com.baomidou.mybatisplus.extension.service.IService;
import qichen.code.entity.dto.OperationLogDTO;
import qichen.code.model.Filter;

import java.math.BigInteger;
import java.util.List;

/**
 * <p>
 * 操作记录表 服务类
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-11-23
 */
public interface IOperationLogService extends IService<OperationLog> {

    void saveOperationLog(Integer operManType, Integer adminId, String s, String s1, String t_ad, Integer id, String dataStr);

    List<OperationLogDTO> listByFilter(OperationLogDTO dto, Filter filter);

    BigInteger listCount(OperationLogDTO dto, Filter filter);

    OperationLogDTO getDetail(Integer id);
}
