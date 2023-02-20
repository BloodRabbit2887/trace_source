package qichen.code.service;

import qichen.code.entity.AssembleOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import qichen.code.entity.dto.AssembleOrderDTO;
import qichen.code.model.Filter;

import java.util.List;
import java.util.Map;

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

    List<AssembleOrder> listFilter(AssembleOrderDTO dto, Filter filter);

    void skip(Integer userId, String number, Integer tableType);
}
