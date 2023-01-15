package qichen.code.service;

import qichen.code.entity.SparePartsLog;
import com.baomidou.mybatisplus.extension.service.IService;
import qichen.code.entity.dto.SparePartsLogDTO;

/**
 * <p>
 * 零件检测报告表 服务类
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-12-19
 */
public interface ISparePartsLogService extends IService<SparePartsLog> {

    SparePartsLog submit(SparePartsLogDTO dto);

    SparePartsLogDTO getWorkOrderModel(Integer userId, String number);

    SparePartsLog getDraft(Integer userId);

    SparePartsLogDTO getVerify(String number);
}
