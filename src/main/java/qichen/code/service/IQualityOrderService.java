package qichen.code.service;

import qichen.code.entity.QualityOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import qichen.code.entity.dto.QualityOrderDTO;
import qichen.code.model.Filter;

import java.math.BigInteger;
import java.util.List;

/**
 * <p>
 * 质量管理部工单表 服务类
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-12-19
 */
public interface IQualityOrderService extends IService<QualityOrder> {

    QualityOrder createWorkOrder(QualityOrderDTO qualityOrderDTO);

    QualityOrder getByNumber(String number);

    void removeByNumber(String number, Integer otherId);

    QualityOrderDTO getWorkOrderModel(Integer userId, String number);

    QualityOrderDTO getByOrderId(Integer workerOrderId, boolean draft);

    List<QualityOrder> listFilter(QualityOrderDTO qualityOrderDTO, Filter filter);

    List<QualityOrderDTO> listByFilter(QualityOrderDTO dto, Filter filter);

    BigInteger listCount(QualityOrderDTO dto, Filter filter);

    QualityOrderDTO getDetail(Integer id);
}
