package qichen.code.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import qichen.code.entity.WorkOrder;
import qichen.code.entity.dto.AdminDTO;
import qichen.code.entity.dto.ParameterDTO;
import qichen.code.entity.dto.UserDTO;
import qichen.code.entity.dto.WorkOrderDTO;
import qichen.code.exception.BusinessException;
import qichen.code.exception.ResException;
import qichen.code.model.*;
import qichen.code.service.*;
import qichen.code.utils.UserContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.util.*;

/**
 * <p>
 * 工单表 前端控制器
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-12-19
 */
@Slf4j
@RestController
@RequestMapping("/code/work-order")
public class WorkOrderController {

    @Autowired
    private IWorkOrderService workOrderService;
    @Autowired
    private UserContextUtils userContextUtils;
    @Autowired
    private IParameterService parameterService;
    @Autowired
    private IOperationLogService operationLogService;
    @Autowired
    private IUserTableProjectService userTableProjectService;

    @ResponseBody
    @GetMapping("/newNeeds")
    public ResponseBean newNeeds(HttpServletRequest request,
                                 @RequestParam(value = "tableType") Integer type,
                                 @RequestParam(value = "page", defaultValue = "1") Integer page,
                                 @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                 @RequestParam(value = "orders", defaultValue = "createTime") String orders,
                                 @RequestParam(value = "orderBy", defaultValue = "false") Boolean orderBy){
        UserDTO user = userContextUtils.getCurrentUser(request);
        if (user==null){
            return new ResponseBean(ResException.USER_MISS);
        }
        if (user.getStatus()==1){
            return new ResponseBean(ResException.USER_LOCK);
        }
        Filter filter = new Filter();
        filter.setOrders(orders);
        filter.setOrderBy(orderBy);
        filter.setPage(page);
        filter.setPageSize(pageSize);

        try {
            Map<String, Object> res =workOrderService.newListNeedOrders(user.getId(),type, filter);
            return new ResponseBean(res);
        } catch (BusinessException exception) {
            return new ResponseBean(exception);
        } catch (Exception exception) {
            exception.printStackTrace();
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }



    @ResponseBody
    @GetMapping("/needs")
    public ResponseBean needs(HttpServletRequest request,
                              @RequestParam(value = "page", defaultValue = "1") Integer page,
                              @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                              @RequestParam(value = "orders", defaultValue = "createTime") String orders,
                              @RequestParam(value = "orderBy", defaultValue = "false") Boolean orderBy){

        UserDTO user = userContextUtils.getCurrentUser(request);
        if (user==null){
            return new ResponseBean(ResException.USER_MISS);
        }
        if (user.getStatus()==1){
            return new ResponseBean(ResException.USER_LOCK);
        }

        Filter filter = new Filter();
        filter.setOrders(orders);
        filter.setOrderBy(orderBy);
        filter.setPage(page);
        filter.setPageSize(pageSize);

        try {
            Map<String, Object> res =workOrderService.listNeedOrders(user.getId(), filter);
            return new ResponseBean(res);
        } catch (BusinessException exception) {
            return new ResponseBean(exception);
        } catch (Exception exception) {
            exception.printStackTrace();
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }

    @ResponseBody
    @GetMapping("/query")
    public ResponseBean query(@RequestParam(value = "deptId",required = false) Integer deptId,
                              @RequestParam(value = "customId",required = false) Integer customId,
                              @RequestParam(value = "customName",required = false) String customName,
                              @RequestParam(value = "customMobile",required = false) String customMobile,
                              @RequestParam(value = "number",required = false) String number,
                              @RequestParam(value = "verifyStatus",required = false) Integer verifyStatus,
                              @RequestParam(value = "count",required = false) Integer count,
                              @RequestParam(value = "electricTypeId",required = false) Integer electricTypeId,
                              @RequestParam(value = "modelTypeId",required = false) Integer modelTypeId,
                              @RequestParam(value = "saleId",required = false) Integer saleId,
                              @RequestParam(value = "deptStatus",required = false) Integer deptStatus,
                              @RequestParam(value = "draft",defaultValue = "0") Integer draft,
                              @RequestParam(value = "modelTitle",required = false) String modelTitle,
                              @RequestParam(value = "status", required = false) Integer status,
                              @RequestParam(value = "page", defaultValue = "1") Integer page,
                              @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                              @RequestParam(value = "createTimeBegin", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date createTimeBegin,
                              @RequestParam(value = "createTimeEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date createTimeEnd,
                              @RequestParam(value = "orders", defaultValue = "createTime") String orders,
                              @RequestParam(value = "orderBy", defaultValue = "false") Boolean orderBy,
                              @RequestParam(value = "keyword", required = false) String keyword) {

        WorkOrderDTO workOrderDTO = new WorkOrderDTO();
        workOrderDTO.setDeptId(deptId);
        workOrderDTO.setCustomId(customId);
        workOrderDTO.setCustomName(customName);
        workOrderDTO.setNumber(number);
        workOrderDTO.setVerifyStatus(verifyStatus);
        workOrderDTO.setCount(count);
        workOrderDTO.setElectricTypeId(electricTypeId);
        workOrderDTO.setModelTypeId(modelTypeId);
        workOrderDTO.setSaleId(saleId);
        workOrderDTO.setDeptStatus(deptStatus);
        workOrderDTO.setDraft(draft);
        workOrderDTO.setModelTitle(modelTitle);
        workOrderDTO.setStatus(status);
        workOrderDTO.setCustomMobile(customMobile);

        Filter filter = new Filter();
        filter.setKeyword(keyword);
        filter.setCreateTimeBegin(createTimeBegin);
        filter.setCreateTimeEnd(createTimeEnd);
        filter.setOrders(orders);
        filter.setOrderBy(orderBy);
        filter.setPage(page);
        filter.setPageSize(pageSize);
        Map<String, Object> res = new HashMap<>();

        try {
            List<WorkOrderDTO> list =workOrderService.listByFilter(workOrderDTO, filter);
            BigInteger resCount =workOrderService.listCount(workOrderDTO, filter);
            res.put("list", list);
            res.put("count", resCount);
            return new ResponseBean(res);
        } catch (BusinessException exception) {
            return new ResponseBean(exception);
        } catch (Exception exception) {
            exception.printStackTrace();
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }


    @ResponseBody
    @GetMapping("/detail")
    public ResponseBean detail(@RequestParam(value = "id",required = false) Integer id,
                               @RequestParam(value = "number",required = false) String number){
        try {
            WorkOrderDTO dto = new WorkOrderDTO();
            if (id!=null){
                dto = workOrderService.getDetail(id,true);
            }else if (!StringUtils.isEmpty(number) && number.length()>0){
                dto = workOrderService.getDetailByNumber(number,true);
            }

            if (dto!=null){
                return new ResponseBean(workOrderService.changeToModel(dto));
            }
            return new ResponseBean(dto);
        } catch (BusinessException exception) {
            return new ResponseBean(exception);
        } catch (Exception exception) {
            exception.printStackTrace();
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }


    @ResponseBody
    @GetMapping("/getDraft")
    public ResponseBean getDraft(HttpServletRequest request){
        UserDTO user = userContextUtils.getCurrentUser(request);
        if (user==null){
            return new ResponseBean(ResException.USER_MISS);
        }
        if (user.getStatus()==1){
            return new ResponseBean(ResException.USER_LOCK);
        }
        try {
            Object draft = workOrderService.getDraft(user.getId(),2);
            return new ResponseBean(draft);
        }catch (BusinessException exception){
            return new ResponseBean(exception);
        }catch (Exception exception){
            exception.printStackTrace();
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }


    @ResponseBody
    @GetMapping(value = "/getAcceptMobile")
    public ResponseBean getDetails() {

        try {
            String mobile = parameterService.getValueByName("accept.mobile");
            return new ResponseBean(mobile);
        }catch (BusinessException exception){
            return new ResponseBean(exception);
        }catch (Exception exception){
            exception.printStackTrace();
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }

    @ResponseBody
    @GetMapping("/getLink")
    public ResponseBean getLink(@RequestParam(value = "id") Integer id){

        try {
            WorkOrderDTO dto = workOrderService.getDetail(id, true);
            LinkModel linkModel = changeToLinkModel(dto);
            return new ResponseBean(JSONObject.parseObject(JSON.toJSONString(linkModel, SerializerFeature.DisableCircularReferenceDetect)));
        }catch (BusinessException exception){
            return new ResponseBean(exception);
        }catch (Exception exception){
            exception.printStackTrace();
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }

    private LinkModel changeToLinkModel(WorkOrderDTO dto) {
        LinkModel model = new LinkModel();
        List<LinkModel.Link> links = new ArrayList<>();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        LinkModel.Link sale = new LinkModel.Link();
        sale.setOrders(1);
        sale.setDeptName("营销部");
        sale.setStatus(0);

        if (dto!=null){
            if (!StringUtils.isEmpty(dto.getSubmitName()) && dto.getSubmitName().length()>0){
                sale.setStatus(1);
                sale.setUserName(dto.getSubmitName());

            }
            if (dto.getCreateTime()!=null){
                sale.setSubmitTime(format.format(Date.from(dto.getCreateTime().toInstant(ZoneOffset.UTC))));
            }
        }
        links.add(sale);

        LinkModel.Link devise = new LinkModel.Link();
        devise.setOrders(2);
        devise.setDeptName("设计部");
        devise.setStatus(0);

        if (dto!=null){
            if (!StringUtils.isEmpty(dto.getDeviseUserName()) && dto.getDeviseUserName().length()>0){
                devise.setStatus(1);
                devise.setUserName(dto.getSubmitName());
            }
            if (dto.getDeviseCreateTime()!=null){
                devise.setSubmitTime(format.format(Date.from(dto.getDeviseCreateTime().toInstant(ZoneOffset.UTC))));
            }
        }
        links.add(devise);


        LinkModel.Link tec = new LinkModel.Link();
        tec.setOrders(3);
        tec.setDeptName("工艺部");
        tec.setStatus(0);

        if (dto!=null){
            if (!StringUtils.isEmpty(dto.getTecUserName()) && dto.getTecUserName().length()>0){
                tec.setStatus(1);
                tec.setUserName(dto.getTecUserName());
            }
            if (dto.getTeCreateTime()!=null){
                tec.setSubmitTime(format.format(Date.from(dto.getTeCreateTime().toInstant(ZoneOffset.UTC))));
            }
        }
        links.add(tec);

        LinkModel.Link quality = new LinkModel.Link();
        quality.setOrders(4);
        quality.setDeptName("质量管理部");
        quality.setStatus(0);

        if (dto!=null){

            if (!StringUtils.isEmpty(dto.getQualityUserName()) && dto.getQualityUserName().length()>0){
                quality.setStatus(1);
                quality.setUserName(dto.getQualityUserName());
            }
            if (dto.getQualityCreateTime()!=null){
                quality.setSubmitTime(format.format(Date.from(dto.getQualityCreateTime().toInstant(ZoneOffset.UTC))));
            }
        }
        links.add(quality);

        LinkModel.Link assemble = new LinkModel.Link();
        assemble.setOrders(5);
        assemble.setDeptName("装配车间");
        assemble.setStatus(0);
        links.add(assemble);

        LinkModel.Link check = new LinkModel.Link();
        check.setOrders(6);
        check.setDeptName("检验科");
        check.setStatus(0);
        links.add(check);

        LinkModel.Link after = new LinkModel.Link();
        after.setOrders(7);
        after.setDeptName("装配调试售后服务科");
        after.setStatus(0);

        if (dto!=null){
            if (!StringUtils.isEmpty(dto.getAfterUserName()) && dto.getAfterUserName().length()>0){
                after.setStatus(1);
                after.setUserName(dto.getAfterUserName());
            }
            if (dto.getAfterCreateTime()!=null){
                after.setSubmitTime(format.format(Date.from(dto.getAfterCreateTime().toInstant(ZoneOffset.UTC))));
            }
        }
        links.add(after);

        model.setLinks(links);

        return model;
    }


    //环节推送
    @ResponseBody
    @GetMapping("/linkChange")
    public ResponseBean linkChange(HttpServletRequest request,
                                   @RequestParam(value = "number") String number,
                                   @RequestParam(value = "status") Integer status,
                                   @RequestParam(value = "remark",required = false) String remark,
                                   @RequestParam(value = "toUserId",required = false) Integer toUserId){
        UserDTO user = userContextUtils.getCurrentUser(request);
        if (user==null){
            return new ResponseBean(ResException.USER_MISS);
        }
        if (user.getStatus()==1){
            return new ResponseBean(ResException.USER_LOCK);
        }
        if (user.getLinkChangePermission()==0){
            return new ResponseBean(ResException.USER_PER_MISS);
        }
/*        if (user.getType()!=1){
            return new ResponseBean(new BusinessException(ResException.USER_PER_MISS.getCode(),"职工无环节推进权限,请联系部门主管"));
        }*/
        try {
            WorkOrder workOrder = workOrderService.linkChange(user.getId(),number,status,remark);
/*            WorkOrder workOrder = workOrderService.newLinkChange(user.getId(),number,status,remark,toUserId);*/
            operationLogService.saveOperationLog(user.getType(),user.getId(),"410","工单环节推进【"+DeptTypeModel.TYPE_MAP.get(user.getDeptId())+"】","t_work_order",workOrder.getId(), JSON.toJSONString(workOrder));
            return new ResponseBean();
        }catch (BusinessException exception){
            return new ResponseBean(exception);
        }catch (Exception exception){
            exception.printStackTrace();
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }

    @ResponseBody
    @GetMapping("/queryTableTypes")
    public ResponseBean queryTableTypes(HttpServletRequest request){
        UserDTO user = userContextUtils.getCurrentUser(request);
        if (user==null){
            return new ResponseBean(ResException.USER_MISS);
        }
        if (user.getStatus()==1){
            return new ResponseBean(ResException.USER_LOCK);
        }
/*        if (user.getDistributionPermission()==0){
            return new ResponseBean(ResException.USER_PER_MISS);
        }*/
        try {
            List<TableTypeDTO> list = workOrderService.queryTableTypes(user.getDeptId());
            return new ResponseBean(list);
        }catch (BusinessException exception){
            return new ResponseBean(exception);
        }catch (Exception exception){
            exception.printStackTrace();
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }

    @ResponseBody
    @GetMapping("/listDistributionProjects")
    public ResponseBean listDistributionProjects(HttpServletRequest request,
                                                 @RequestParam(value = "tableType") Integer tableType,
                                                 @RequestParam(value = "status", required = false) Integer status,
                                                 @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                 @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                 @RequestParam(value = "createTimeBegin", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date createTimeBegin,
                                                 @RequestParam(value = "createTimeEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date createTimeEnd,
                                                 @RequestParam(value = "orders", defaultValue = "createTime") String orders,
                                                 @RequestParam(value = "orderBy", defaultValue = "false") Boolean orderBy){
        UserDTO user = userContextUtils.getCurrentUser(request);
        if (user==null){
            return new ResponseBean(ResException.USER_MISS);
        }
        if (user.getStatus()==1){
            return new ResponseBean(ResException.USER_LOCK);
        }
        if (user.getDistributionPermission()==0){
            return new ResponseBean(ResException.USER_PER_MISS);
        }
        try {
            Filter filter = new Filter();
            filter.setCreateTimeBegin(createTimeBegin);
            filter.setCreateTimeEnd(createTimeEnd);
            filter.setOrders(orders);
            filter.setOrderBy(orderBy);
            filter.setPage(page);
            filter.setPageSize(pageSize);
            Map<String,Object> res = workOrderService.listDistributionProjects(tableType,filter,user.getDeptId());
            return new ResponseBean(res);
        }catch (BusinessException exception){
            return new ResponseBean(exception);
        }catch (Exception exception){
            exception.printStackTrace();
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }

    @ResponseBody
    @GetMapping("/getProjectByNumber")
    public ResponseBean getProjectByNumber(HttpServletRequest request,
                                           @RequestParam(value = "number") String number){
        UserDTO user = userContextUtils.getCurrentUser(request);
        if (user==null){
            return new ResponseBean(ResException.USER_MISS);
        }
        if (user.getStatus()==1){
            return new ResponseBean(ResException.USER_LOCK);
        }
        try {
            AssembleProjectModel model = userTableProjectService.getProjectByNumber(user.getId(),user.getDeptId(),number);
            return new ResponseBean(model);
        }catch (BusinessException exception){
            return new ResponseBean(exception);
        }catch (Exception exception){
            exception.printStackTrace();
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }

}

