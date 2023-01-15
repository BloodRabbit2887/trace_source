package qichen.code.service;

import qichen.code.entity.ModelCheckLog;
import com.baomidou.mybatisplus.extension.service.IService;
import qichen.code.entity.dto.ModelCheckLogDTO;

/**
 * <p>
 * 零件检测尺寸特性表 服务类
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-12-19
 */
public interface IModelCheckLogService extends IService<ModelCheckLog> {

    ModelCheckLog submit(ModelCheckLogDTO dto);

    ModelCheckLogDTO getWorkOrderModel(Integer userId, String number);

    ModelCheckLogDTO getVerify(String number);
}
