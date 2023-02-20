package qichen.code.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import qichen.code.entity.AssembleOrder;
import qichen.code.entity.QualityOrder;
import qichen.code.entity.User;
import qichen.code.entity.WorkOrder;
import qichen.code.entity.dto.AssembleOrderDTO;
import qichen.code.entity.dto.QualityOrderFileDTO;
import qichen.code.exception.BusinessException;
import qichen.code.exception.ResException;
import qichen.code.mapper.AssembleOrderMapper;
import qichen.code.model.AssembleTableTypeModel;
import qichen.code.model.DeptTypeModel;
import qichen.code.model.Filter;
import qichen.code.service.IAssembleOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import qichen.code.service.IUserService;
import qichen.code.service.IUserTableProjectService;
import qichen.code.service.IWorkOrderService;
import qichen.code.utils.BeanUtils;
import qichen.code.utils.JsonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 装配车间工单表 服务实现类
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-01-09
 */
@Service
public class AssembleOrderServiceImpl extends ServiceImpl<AssembleOrderMapper, AssembleOrder> implements IAssembleOrderService {

    @Autowired
    private IWorkOrderService workOrderService;
    @Autowired
    private IUserService userService;
    @Autowired
    private IUserTableProjectService userTableProjectService;

    @Override
    public AssembleOrder createWorkOrder(AssembleOrderDTO dto) {
        WorkOrder workOrder = workOrderService.getUnFinish(dto.getNumber());
        if (workOrder==null){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"当前装配车间无待完成工单");
        }
        if (!workOrder.getDeptId().equals(DeptTypeModel.DEPT_WORK_ASSEMBLE)){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"当前装配车间无待完成工单");
        }

        dto.setOrderID(workOrder.getId());
        checkAlready(dto.getNumber());
        //空值检验
        if (dto.getDraft()==null || dto.getDraft()==0){
            Map<String,String> params = new HashMap<>();
            params.put("title","模具名称");
            params.put("number","模号");
            params.put("submitId","创建人");
            JsonUtils.checkColumnNull(params, JSONObject.parseObject(JSON.toJSONString(dto)));
        }

        AssembleOrder assembleOrder = BeanUtils.copyAs(dto, AssembleOrder.class);
        saveOrUpdate(assembleOrder);

        return assembleOrder;
    }

    @Override
    public AssembleOrder getByNumber(String number) {
        QueryWrapper<AssembleOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("`number`",number);
        wrapper.eq("`draft`",0);
        wrapper.ne("`verifyStatus`",2);
        return getOne(wrapper);
    }

    @Override
    public void removeByNumber(String number, Integer id) {
        UpdateWrapper<AssembleOrder> wrapper = new UpdateWrapper<>();
        wrapper.eq("`number`",number);
        wrapper.ne("`ID`",id);
        remove(wrapper);
    }


    @Override
    public List<AssembleOrder> listFilter(AssembleOrderDTO assembleOrderDTO, Filter filter) {
        QueryWrapper<AssembleOrder> wrapper = new QueryWrapper<>();
        addFilter(wrapper,assembleOrderDTO,filter);
        return list(wrapper);
    }

    @Transactional
    @Override
    public void skip(Integer userId, String number, Integer tableType) {
        WorkOrder workOrder = workOrderService.getByNumber(number);
        if (workOrder==null){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"工单信息有误");
        }
        User user = userService.getById(userId);
        if (user==null){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"用户信息异常");
        }
        if (!workOrder.getDeptId().equals(user.getDeptId())){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"当前环节无操作权限");
        }
        if (tableType.equals(AssembleTableTypeModel.TYPE_MOULE_BASE)){
            workOrder.setTableType(AssembleTableTypeModel.TYPE_DOWN);
            workOrder.setTableTypeStatus(0);
        }else if (tableType.equals(AssembleTableTypeModel.TYPE_DOWN)){
            workOrder.setTableType(AssembleTableTypeModel.TYPE_PLANK);
            workOrder.setTableTypeStatus(0);
        }else if (tableType.equals(AssembleTableTypeModel.TYPE_PLANK)){
            workOrder.setTableType(AssembleTableTypeModel.TYPE_PACKAGE);
            workOrder.setTableTypeStatus(0);
        }else if (tableType.equals(AssembleTableTypeModel.TYPE_PACKAGE)){
            workOrder.setTableType(AssembleTableTypeModel.TYPE_ALLOY);
            workOrder.setTableTypeStatus(0);
        }else if (tableType.equals(AssembleTableTypeModel.TYPE_ALLOY)){
            workOrder.setTableType(AssembleTableTypeModel.TYPE_MODEL_PUSH);
            workOrder.setTableTypeStatus(0);
        }
        workOrderService.updateById(workOrder);
        userTableProjectService.cancelByTableType(number,tableType,DeptTypeModel.DEPT_WORK_ASSEMBLE);
    }

    private void addFilter(QueryWrapper<AssembleOrder> wrapper, AssembleOrderDTO dto, Filter filter) {
        if (dto!=null){
            if (!StringUtils.isEmpty(dto.getNumber()) && dto.getNumber().length()>0){
                wrapper.eq("`number`",dto.getNumber());
            }
            if (dto.getDraft()!=null){
                wrapper.eq("`draft`",dto.getDraft());
            }
            if (dto.getStatus()!=null){
                wrapper.eq("`Status`",dto.getStatus());
            }
            if (dto.getVerifyStatus()!=null){
                wrapper.eq("`verifyStatus`",dto.getVerifyStatus());
            }
            if (dto.getSubmit()!=null){
                wrapper.ne("verifyStatus",2);
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
            if (!StringUtils.isEmpty(filter.getKeyword()) && filter.getKeyword().length()>0){
                wrapper.and(queryWrapper->queryWrapper.like("`title`",filter.getKeyword()).or().like("`number`",filter.getKeyword()));
            }
            if (filter.getPage()!=null && filter.getPageSize()!=null && filter.getPage()!=0 && filter.getPageSize()!=0){
                int fast = filter.getPage()<=1?0:(filter.getPage()-1)*filter.getPageSize();
                wrapper.last(" limit "+fast+", "+filter.getPageSize());
            }
        }
    }


    private void checkAlready(String number) {
        QueryWrapper<AssembleOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("`number`",number);
        wrapper.eq("`draft`",0);
        wrapper.eq("`verifyStatus`",1);
        Integer count = baseMapper.selectCount(wrapper);
        if (count>0){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"当前模号存在已审核工单");
        }
    }
}
