package qichen.code.service;

import qichen.code.entity.AfterSaleOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import qichen.code.entity.dto.AfterSaleOrderDTO;
import qichen.code.model.Filter;

import java.util.List;

/**
 * <p>
 * 维修工单表 服务类
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-01-12
 */
public interface IAfterSaleOrderService extends IService<AfterSaleOrder> {

    AfterSaleOrderDTO getWorkOrderModel(Integer userId, String number);

    AfterSaleOrder createWorkOrder(AfterSaleOrderDTO dto);

    List<AfterSaleOrderDTO> listByFilter(AfterSaleOrderDTO afterSaleOrderDTO, Filter filter);
}
