package qichen.code.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import qichen.code.entity.AssembleOrder;
import qichen.code.entity.QualityOrder;
import qichen.code.entity.WorkOrder;
import qichen.code.entity.dto.AssembleOrderDTO;
import qichen.code.entity.dto.QualityOrderFileDTO;
import qichen.code.exception.BusinessException;
import qichen.code.exception.ResException;
import qichen.code.mapper.AssembleOrderMapper;
import qichen.code.model.DeptTypeModel;
import qichen.code.service.IAssembleOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import qichen.code.service.IWorkOrderService;
import qichen.code.utils.BeanUtils;
import qichen.code.utils.JsonUtils;

import java.util.HashMap;
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
