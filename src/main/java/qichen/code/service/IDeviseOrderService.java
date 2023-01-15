package qichen.code.service;

import qichen.code.entity.DeviseOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import qichen.code.entity.dto.DeviseOrderDTO;

/**
 * <p>
 * 设计部工单表 服务类
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-12-19
 */
public interface IDeviseOrderService extends IService<DeviseOrder> {

    DeviseOrder createWorkOrder(DeviseOrderDTO dto);

    DeviseOrder getByNumber(String number);

    void removeByNumber(String number, Integer otherId);

    DeviseOrder getDraft(Integer userId);

    DeviseOrderDTO getWorkOrderModel(Integer userId,String number);

    DeviseOrderDTO getByWorkOrderId(Integer workerOrderId, boolean draft);
}
