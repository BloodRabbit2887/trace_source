package qichen.code.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import qichen.code.entity.*;
import qichen.code.entity.dto.AfterSaleOrderDTO;
import qichen.code.entity.dto.DeviseOrderDTO;
import qichen.code.entity.dto.WorkOrderDTO;
import qichen.code.exception.BusinessException;
import qichen.code.exception.ResException;
import qichen.code.mapper.AfterSaleOrderMapper;
import qichen.code.model.DeptTypeModel;
import qichen.code.model.Filter;
import qichen.code.service.IAfterSaleOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import qichen.code.service.IOptionService;
import qichen.code.service.IWorkOrderService;
import qichen.code.utils.BeanUtils;
import qichen.code.utils.JsonUtils;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 维修工单表 服务实现类
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-01-12
 */
@Service
public class AfterSaleOrderServiceImpl extends ServiceImpl<AfterSaleOrderMapper, AfterSaleOrder> implements IAfterSaleOrderService {

    @Autowired
    private IOptionService optionService;
    @Autowired
    private IWorkOrderService workOrderService;


    @Override
    public AfterSaleOrderDTO getWorkOrderModel(Integer userId, String number) {

        WorkOrderDTO workOrderDTO = new WorkOrderDTO();
        workOrderDTO.setNumber(number);
        workOrderDTO.setDraft(0);
        workOrderDTO.setVerifyStatus(1);
        BigInteger count = workOrderService.listCount(workOrderDTO, new Filter());
        if (count.intValue()<=0){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"模号有误");
        }


        AfterSaleOrderDTO dto = new AfterSaleOrderDTO();
        dto.setNumber(number);
        AfterSaleOrder order = getDraft(userId,number);
        if (order!=null){
            dto = BeanUtils.copyAs(order, AfterSaleOrderDTO.class);
        }
        return dto;
    }

    @Override
    public AfterSaleOrder createWorkOrder(AfterSaleOrderDTO dto) {
/*        checkDraft(dto);*/
        if (dto.getId()==null){
            removeDraft(dto);
        }

        if (dto.getDraft()==null || dto.getDraft()==0){
            Map<String,String> params = new HashMap<>();
            params.put("number","模号");
            params.put("title","故障名称");
            params.put("detail","故障详情");
            params.put("views","故障照片/视频");
/*            params.put("measure","维修措施");*/
            params.put("submitId","创建人");
            JsonUtils.checkColumnNull(params, JSONObject.parseObject(JSON.toJSONString(dto)));
        }

        AfterSaleOrder order = BeanUtils.copyAs(dto, AfterSaleOrder.class);
        saveOrUpdate(order);

        WorkOrder workOrder = workOrderService.getByNumber(dto.getNumber());
/*        if (workOrder!=null){
            //TODO  正式删除
            workOrder.setDeptId(DeptTypeModel.DEPT_AFTER_SALE);
            workOrderService.updateById(workOrder);
        }*/

        return order;
    }

    private void removeDraft(AfterSaleOrderDTO dto) {
        UpdateWrapper<AfterSaleOrder> wrapper = new UpdateWrapper<>();
        wrapper.eq("submitId",dto.getSubmitId());
        wrapper.eq("draft",1);
        wrapper.eq("`number`",dto.getNumber());
        remove(wrapper);
    }




    @Override
    public List<AfterSaleOrderDTO> listByFilter(AfterSaleOrderDTO afterSaleOrderDTO, Filter filter) {
        List<AfterSaleOrder> list = listFilter(afterSaleOrderDTO,filter);
        if (!CollectionUtils.isEmpty(list) && list.size()>0){
           return listDTO(list);
        }
        return null;
    }

    private List<AfterSaleOrder> listFilter(AfterSaleOrderDTO dto, Filter filter) {
        QueryWrapper<AfterSaleOrder> wrapper = new QueryWrapper<>();
        addFilter(wrapper,dto,filter);
        return list(wrapper);
    }

    private void addFilter(QueryWrapper<AfterSaleOrder> wrapper, AfterSaleOrderDTO dto, Filter filter) {
        if (dto!=null){
            if (dto.getDraft()!=null){
                wrapper.eq("`draft`",dto.getDraft());
            }
            if (dto.getVerifyStatus()!=null){
                wrapper.eq("verifyStatus",dto.getVerifyStatus());
            }
            if (dto.getStatus()!=null){
                wrapper.eq("`Status`",dto.getStatus());
            }
            if (!StringUtils.isEmpty(dto.getNumber()) && dto.getNumber().length()>0){
                wrapper.eq("`number`",dto.getNumber());
            }
        }
        if (filter!=null){
            if (filter.getCreateTimeBegin()!=null){
                wrapper.ge("createTime",filter.getCreateTimeBegin());
            }
            if (filter.getCreateTimeEnd()!=null){
                wrapper.le("createTime",filter.getCreateTimeEnd());
            }
            if (!StringUtils.isEmpty(filter.getOrders()) && filter.getOrders().length()>0){
                if (filter.getOrderBy()!=null){
                    wrapper.orderBy(true,filter.getOrderBy(),filter.getOrders());
                }
            }
            if (filter.getPage()!=null && filter.getPageSize()!=null && filter.getPage()!=0 && filter.getPageSize()!=0){
                int fast = filter.getPage()<=1?0:(filter.getPage()-1)*filter.getPageSize();
                wrapper.last(" limit "+fast+", "+filter.getPageSize());
            }
        }
    }

    private List<AfterSaleOrderDTO> listDTO(List<AfterSaleOrder> list) {
        List<AfterSaleOrderDTO> dtos = BeanUtils.copyAs(list, AfterSaleOrderDTO.class);
        List<Option> options = optionService.list();
        for (AfterSaleOrderDTO dto : dtos) {
            if (dto.getPartsId()!=null){
                for (Option option : options) {
                    if (option.getId().equals(dto.getPartsId())){
                        dto.setPartsName(option.getTitle());
                    }
                }
            }
        }
        //TODO
        return dtos;
    }

    private void checkDraft(AfterSaleOrderDTO dto) {
        QueryWrapper<AfterSaleOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("submitId",dto.getSubmitId());
        wrapper.eq("draft",1);
        if (dto.getId()!=null){
            wrapper.ne("`ID`",dto.getId());
        }
        Integer count = baseMapper.selectCount(wrapper);
        if (count>0){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"当前存在草稿未完成");
        }
    }

    private AfterSaleOrder getDraft(Integer userId,String number) {
        QueryWrapper<AfterSaleOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("submitId",userId);
        wrapper.eq("`draft`",1);
        wrapper.eq("`number`",number);
        return getOne(wrapper);
    }

}
