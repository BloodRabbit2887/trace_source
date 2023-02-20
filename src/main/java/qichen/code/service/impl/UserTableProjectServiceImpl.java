package qichen.code.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import qichen.code.entity.*;
import qichen.code.entity.dto.UserDTO;
import qichen.code.entity.dto.UserTableProjectDTO;
import qichen.code.exception.BusinessException;
import qichen.code.exception.ResException;
import qichen.code.mapper.UserTableProjectMapper;
import qichen.code.model.*;
import qichen.code.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import qichen.code.utils.BeanUtils;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户工单分配任务表 服务实现类
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-02-15
 */
@Service
public class UserTableProjectServiceImpl extends ServiceImpl<UserTableProjectMapper, UserTableProject> implements IUserTableProjectService {

    @Autowired
    private IUserService userService;
    @Autowired
    private IDeptService deptService;
    @Autowired
    private IDeptRoleService deptRoleService;
    @Autowired
    private IWorkOrderService workOrderService;

    @Override
    public void add(UserTableProjectDTO dto) {

        User submitUser = userService.getById(dto.getSubmitId());
        if (submitUser==null){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"提交人信息有误");
        }
        dto.setDeptId(submitUser.getDeptId());
        DeptRole submitRole = deptRoleService.getById(submitUser.getDeptRoleId());
        if (submitRole==null){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"提交人信息有误");
        }
        if (submitRole.getDistributionPermission()==0){
            throw new BusinessException(ResException.USER_PER_MISS);
        }
        User user = userService.getById(dto.getUserId());
        if (user==null){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"用户信息有误");
        }
        if (!submitRole.getDeptId().equals(user.getDeptId())){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"只可发布至同部门员工");
        }
        DeptRole userRole = deptRoleService.getById(user.getDeptRoleId());
        if (userRole==null){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"用户信息有误");
        }
/*        if (submitRole.getLevel()<=userRole.getLevel()){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"只可发布至下级员工");
        }*/

        WorkOrder workOrder = workOrderService.getByNumber(dto.getNumber());
        if (workOrder==null){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"工单信息有误");
        }

        workOrder.setTableTypeStatus(1);
        workOrderService.updateById(workOrder);

        checkAlready(dto);
        workOrderService.checkFinish(dto);

        UserTableProject userTableProject = BeanUtils.copyAs(dto, UserTableProject.class);
        saveOrUpdate(userTableProject);
    }

    @Override
    public void delete(Integer id) {
        UserTableProject project = getById(id);
        if (project==null){
            throw new BusinessException(ResException.QUERY_MISS);
        }
        if (project.getStatus()==3){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"表单任务已完成");
        }
        removeById(id);
    }

    @Override
    public List<UserTableProjectDTO> listByFilter(UserTableProjectDTO dto, Filter filter) {
        List<UserTableProject> list = listFilter(dto,filter);
        if (!CollectionUtils.isEmpty(list) && list.size()>0){
           return listDTO(list);
        }
        return null;
    }

    private List<UserTableProjectDTO> listDTO(List<UserTableProject> list) {
        List<UserTableProjectDTO> dtos = BeanUtils.copyAs(list, UserTableProjectDTO.class);

        List<User> users = (List<User>) userService.listByIds(list.stream().map(UserTableProject::getUserId).distinct().collect(Collectors.toList()));
        List<User> submitUsers = (List<User>) userService.listByIds(list.stream().map(UserTableProject::getSubmitId).distinct().collect(Collectors.toList()));
        List<Dept> depts = (List<Dept>) deptService.listByIds(list.stream().map(UserTableProject::getDeptId).distinct().collect(Collectors.toList()));

        for (UserTableProjectDTO dto : dtos) {
            dto.setTableName(TableTypeModel.TYPE_MAP.get(dto.getDeptId()).get(dto.getTableType()));
            if (!CollectionUtils.isEmpty(users) && users.size()>0){
                for (User user : users) {
                    if (user.getId().equals(dto.getUserId())){
                        dto.setUserName(user.getName());
                    }
                }
            }
            if (!CollectionUtils.isEmpty(submitUsers) && submitUsers.size()>0){
                for (User user : submitUsers) {
                    if (user.getId().equals(dto.getSubmitId())){
                        dto.setSubmitName(user.getName());
                    }
                }
            }
            if (!CollectionUtils.isEmpty(depts) && depts.size()>0){
                for (Dept dept : depts) {
                    if (dept.getId().equals(dto.getDeptId())){
                        dto.setDeptName(dept.getTitle());
                    }
                }
            }
        }

        return dtos;
    }

    @Override
    public List<UserTableProject> listFilter(UserTableProjectDTO dto, Filter filter) {
        QueryWrapper<UserTableProject> wrapper = new QueryWrapper<>();
        addFitler(wrapper,dto,filter);
        return list(wrapper);
    }

    private void addFitler(QueryWrapper<UserTableProject> wrapper, UserTableProjectDTO dto, Filter filter) {
        if (dto!=null){
            if (dto.getStatus()!=null){
                wrapper.eq("`Status`",dto.getStatus());
            }
            if (dto.getSubmitId()!=null){
                wrapper.eq("submitId",dto.getSubmitId());
            }
            if (dto.getUserId()!=null){
                wrapper.eq("userId",dto.getUserId());
            }
            if (dto.getDeptId()!=null){
                wrapper.eq("deptId",dto.getDeptId());
            }
            if (!StringUtils.isEmpty(dto.getNumber()) && dto.getNumber().length()>0){
                wrapper.eq("`number`",dto.getNumber());
            }
            if (dto.getTableType()!=null){
                wrapper.eq("`tableType`",dto.getTableType());
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
                wrapper.like("`number`",filter.getKeyword());
            }
            if (filter.getPage()!=null && filter.getPageSize()!=null && filter.getPage()!=0 && filter.getPageSize()!=0){
                int fast = filter.getPage()<=1?0:(filter.getPage()-1)*filter.getPageSize();
                wrapper.last(" limit "+fast+", "+filter.getPageSize());
            }
        }
    }

    @Override
    public BigInteger listCount(UserTableProjectDTO dto, Filter filter) {
        QueryWrapper<UserTableProject> wrapper = new QueryWrapper<>();
        if (filter!=null){
            filter.setPage(null);
            filter.setPageSize(null);
        }
        addFitler(wrapper,dto,filter);
        return BigInteger.valueOf(baseMapper.selectCount(wrapper));
    }

    @Override
    public UserTableProjectDTO getDetail(Integer id) {
        UserTableProject project = getById(id);
        if (project==null){
            throw new BusinessException(ResException.QUERY_MISS);
        }
        return getDTO(project);
    }

    @Override
    public List<DistributionStatusDTO> tableTypes(Integer userId, String number) {
        User user = userService.getById(userId);
        if (user==null){
            throw new BusinessException(ResException.QUERY_MISS);
        }

        List<DistributionStatusDTO> dtos = new ArrayList<>();

        QueryWrapper<UserTableProject> wrapper = new QueryWrapper<>();
        wrapper.eq("deptId",user.getDeptId());
        wrapper.eq("`number`",number);
        wrapper.ne("`Status`",4);
        List<UserTableProject> list = list(wrapper);

        Map<Integer, String> tableMap = TableTypeModel.TYPE_MAP.get(user.getDeptId());

        for (Integer key : tableMap.keySet()) {
            DistributionStatusDTO dto = new DistributionStatusDTO();
            dto.setNumber(number);
            dto.setTableType(key);
            dto.setTableName(tableMap.get(key));
            dto.setDeptId(user.getDeptId());
            dto.setStatus(0);
            if (!CollectionUtils.isEmpty(list) && list.size()>0){
                List<UserTableProjectDTO> projectDTOS = listDTO(list);
                for (UserTableProjectDTO userTableProject : projectDTOS) {
                    if (userTableProject.getTableType().equals(key)){
                        dto.setStatus(userTableProject.getStatus());
                        dto.setUserName(userTableProject.getUserName());
                        dto.setSubmitName(userTableProject.getSubmitName());
                    }
                }
            }
            dtos.add(dto);
        }

        if (user.getDeptId().equals(DeptTypeModel.DEPT_WORK_ASSEMBLE)){
            dtos = dtos.stream().filter(dto->AssembleTableTypeModel.PROJECT_TYPES.contains(dto.getTableType())).collect(Collectors.toList());
        }

        return dtos;
    }

    @Override
    public void updateStatus(String number, Integer userId, Integer status,Integer tableType) {
        QueryWrapper<UserTableProject> wrapper = new QueryWrapper<>();
        wrapper.eq("`number`",number);
        wrapper.eq("userId",userId);
        wrapper.ne("`Status`",4);
        wrapper.eq("tableType",tableType);
        UserTableProject project = getOne(wrapper);
        if (project==null){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"当前工单暂无任务");
        }
        if (project.getStatus()==3){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"当前表单已完成");
        }
        project.setStatus(status);
        updateById(project);
    }

    @Override
    public AssembleProjectModel getProjectByNumber(Integer userId, Integer deptId, String number) {
        QueryWrapper<UserTableProject> wrapper = new QueryWrapper<>();
        wrapper.eq("`number`",number);
        wrapper.eq("userId",userId);
        wrapper.eq("`Status`",0);
        wrapper.eq("deptId",deptId);
        UserTableProject project = getOne(wrapper);
        if (project==null){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"当前模号无待完成任务");
        }
        AssembleProjectModel model = new AssembleProjectModel();
        model.setNumber(number);
        model.setTableType(project.getTableType());
        model.setTableName(TableTypeModel.TYPE_MAP.get(deptId).get(project.getTableType()));
        return model;
    }

    @Override
    public void cancelByTableType(String number, Integer tableType, Integer deptId) {
        UpdateWrapper<UserTableProject> wrapper = new UpdateWrapper<>();
        wrapper.eq("`number`",number);
        wrapper.eq("tableType",tableType);
        wrapper.eq("deptId",deptId);
        wrapper.set("`Status`",4);
        update(wrapper);
    }

    @Transactional
    @Override
    public void linkChange(Integer deptId, Integer tableType, String number) {
        QueryWrapper<UserTableProject> wrapper = new QueryWrapper<>();
        wrapper.eq("deptId",deptId);
        wrapper.eq("tableType",tableType);
        wrapper.eq("`number`",number);
        wrapper.ne("`Status`",4);
        UserTableProject project = getOne(wrapper);
        if (project==null){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"分配信息异常");
        }

        List<Integer> skipList = new ArrayList<>();
        if (!StringUtils.isEmpty(project.getSkipTypes()) && project.getSkipTypes().length()>0){
            skipList = Arrays.stream(project.getSkipTypes().split(",")).map(Integer::parseInt).sorted().collect(Collectors.toList());
        }
        if (deptId.equals(DeptTypeModel.DEPT_WORK_ASSEMBLE)){

            if (tableType.equals(AssembleTableTypeModel.TYPE_MODEL_PUSH)){
                finishByNumber(number,deptId);
            }else {
                Integer next = tableType+1;
                if (!CollectionUtils.isEmpty(skipList) && skipList.size()>0){
                    for (Integer skipType : skipList) {
                        if (skipType.equals(next)){
                            next++;
                        }
                    }
                }
                if (next>AssembleTableTypeModel.TYPE_MODEL_PUSH){
                    throw new BusinessException(ResException.MAKE_ERR.getCode(),"结果超出预期");
                }
                UserTableProject tableProject = new UserTableProject();
                tableProject.setUserId(project.getUserId());
                tableProject.setSubmitId(project.getSubmitId());
                tableProject.setDeptId(deptId);
                tableProject.setTableType(next);
                tableProject.setSkipTypes(project.getSkipTypes());
                tableProject.setStatus(0);
                save(tableProject);
            }

        }
    }

    private void finishByNumber(String number,Integer deptId) {
        UpdateWrapper<UserTableProject> wrapper = new UpdateWrapper<>();
        wrapper.eq("deptId",deptId);
        wrapper.eq("`number`",number);
        wrapper.ne("`Status`",4);
        wrapper.set("`Status`",3);
        update(wrapper);
    }

    private UserTableProjectDTO getDTO(UserTableProject project) {
        UserTableProjectDTO dto = BeanUtils.copyAs(project, UserTableProjectDTO.class);

        User user = userService.getById(project.getUserId());
        if (user!=null){
            dto.setUserName(user.getName());
        }

        User submitUser = userService.getById(project.getSubmitId());
        if (submitUser!=null){
            dto.setSubmitName(submitUser.getName());
        }

        dto.setTableName(TableTypeModel.TYPE_MAP.get(project.getDeptId()).get(project.getTableType()));

        Dept dept = deptService.getById(project.getDeptId());
        if (dept!=null){
            dto.setDeptName(dept.getTitle());
        }

        return dto;
    }


    private void checkAlready(UserTableProjectDTO dto) {
        QueryWrapper<UserTableProject> wrapper = new QueryWrapper<>();
        wrapper.eq("number",dto.getNumber());
        wrapper.eq("deptId",dto.getDeptId());
        wrapper.eq("tableType",dto.getTableType());
        wrapper.ne("`Status`",4);
        if (dto.getId()!=null){
            wrapper.ne("`ID`",dto.getId());
        }
        Integer count = baseMapper.selectCount(wrapper);
        if (count>0){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"已存在工单任务");
        }
    }
}
