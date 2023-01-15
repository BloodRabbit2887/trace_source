package qichen.code.service;

import qichen.code.entity.AssembleOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import qichen.code.entity.dto.AssembleOrderDTO;

/**
 * <p>
 * 装配车间工单表 服务类
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-01-09
 */
public interface IAssembleOrderService extends IService<AssembleOrder> {

    AssembleOrder createWorkOrder(AssembleOrderDTO dto);

    AssembleOrder getByNumber(String number);

    void removeByNumber(String number, Integer id);
}
