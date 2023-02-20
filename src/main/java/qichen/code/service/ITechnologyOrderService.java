package qichen.code.service;

import qichen.code.entity.TechnologyOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import qichen.code.entity.dto.TechonologyOrderDTO;
import qichen.code.model.Filter;

import java.math.BigInteger;
import java.util.List;

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

    List<TechonologyOrderDTO> listByFilter(TechonologyOrderDTO dto, Filter filter);

    BigInteger listCount(TechonologyOrderDTO dto, Filter filter);

    List<TechnologyOrder> listFilter(TechonologyOrderDTO dto, Filter filter);

    TechonologyOrderDTO getDetail(Integer id);
}
