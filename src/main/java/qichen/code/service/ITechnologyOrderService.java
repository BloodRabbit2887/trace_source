package qichen.code.service;

import qichen.code.entity.TechnologyOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import qichen.code.entity.dto.TechonologyOrderDTO;

/**
 * <p>
 * 工艺部工单表 服务类
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-12-19
 */
public interface ITechnologyOrderService extends IService<TechnologyOrder> {

    TechnologyOrder createWorkOrder(TechonologyOrderDTO techonologyOrderDTO);

    TechnologyOrder getDraft(Integer userId);

    TechnologyOrder getByNumber(String number);

    void removeByNumber(String number, Integer otherId);

    TechonologyOrderDTO getWorkOrderModel(Integer userId, String number);

    TechonologyOrderDTO getByWorkOrderId(Integer workerOrderId, boolean draft);
}
