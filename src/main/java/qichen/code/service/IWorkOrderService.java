package qichen.code.service;

import qichen.code.entity.WorkOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import qichen.code.entity.dto.UserTableProjectDTO;
import qichen.code.entity.dto.WorkOrderDTO;
import qichen.code.exception.BusinessException;
import qichen.code.model.*;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

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

    Map<String, Object> listNeedOrders(Integer userId, Filter filter);

    WorkOrderDTO getDetailByNumber(String number, boolean detail);

    HomeModel getHomeModel();

    void checkFinish(UserTableProjectDTO dto);

    Map<String, Object> newListNeedOrders(Integer userId,Integer tableType, Filter filter);

    WorkOrder newLinkChange(Integer userId, String number, Integer status, String remark, Integer toUserId);

    List<TableTypeDTO> queryTableTypes(Integer deptId);

    Map<String, Object> listDistributionProjects(Integer tableType, Filter filter, Integer deptId);
}
