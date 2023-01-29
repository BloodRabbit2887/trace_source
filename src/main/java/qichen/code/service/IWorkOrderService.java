package qichen.code.service;

import qichen.code.entity.WorkOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import qichen.code.entity.dto.WorkOrderDTO;
import qichen.code.exception.BusinessException;
import qichen.code.model.Filter;
import qichen.code.model.ResponseBean;
import qichen.code.model.WorkOrderModel;

import java.math.BigInteger;
import java.util.List;

/**
 * <p>
 * 工单表 服务类
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-12-19
 */
public interface IWorkOrderService extends IService<WorkOrder> {

    WorkOrder getByNumber(String number);

    BigInteger listCount(WorkOrderDTO workOrderDTO, Filter filter);

    void createOrderBySale(WorkOrderDTO workOrderDTO);

    List<WorkOrderDTO> listByFilter(WorkOrderDTO workOrderDTO, Filter filter);

    WorkOrder verifyWorkOrder(Integer userId, String number, Integer status, String verifyRemark);

    WorkOrder linkChange(Integer userId, String number,Integer status,String remark);

    Object getDraft(Integer userId, Integer submitType);

    WorkOrder getUnFinish(String number);

    WorkOrderDTO getOrderModel(Integer userId, String number);

    WorkOrderDTO getDetail(Integer id, boolean detail);

    WorkOrderModel changeToModel(WorkOrderDTO dto);
}
