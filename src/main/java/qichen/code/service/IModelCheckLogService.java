package qichen.code.service;

import org.springframework.web.bind.annotation.RequestParam;
import qichen.code.entity.ModelCheckLog;
import com.baomidou.mybatisplus.extension.service.IService;
import qichen.code.entity.dto.ModelCheckLogDTO;
import qichen.code.model.Filter;

import java.math.BigInteger;
import java.util.List;

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

    List<ModelCheckLog> listFilter(ModelCheckLogDTO checkLogDTO, Filter filter);

    ModelCheckLogDTO getDetail(Integer id);

    List<ModelCheckLogDTO> listByFilter(ModelCheckLogDTO dto, Filter filter);

    BigInteger listCount(ModelCheckLogDTO dto, Filter filter);

    ModelCheckLog verifyWorkOrder(Integer userId, Integer id, Integer status, String verifyRemark);

    ModelCheckLog getByNumber(String number);
}
