package qichen.code.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import qichen.code.entity.*;
import qichen.code.entity.dto.DeptRoleDTO;
import qichen.code.entity.dto.UserDTO;
import qichen.code.exception.BusinessException;
import qichen.code.exception.ResException;
import qichen.code.mapper.UserMapper;
import qichen.code.model.DeptTypeModel;
import qichen.code.model.Filter;
import qichen.code.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import qichen.code.utils.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-12-16
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private IParameterService parameterService;
    @Autowired
    private IUserLoginService userLoginService;
    @Autowired
    private UserContextUtils userContextUtils;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private IUserTokenService userTokenService;
    @Autowired
    private IDeptService deptService;
    @Autowired
    private IDeptRoleService deptRoleService;


    @Transactional
    @Override
    public User add(UserDTO userDTO) {

        if (StringUtils.isEmpty(userDTO.getName()) || userDTO.getName().length()==0){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"姓名不能为空");
        }
        if (userDTO.getSex()==null){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"请选定性别");
        }
        if (StringUtils.isEmpty(userDTO.getAccount()) || userDTO.getAccount().length()==0){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"账户信息不能为空");
        }
        if (StringUtils.isEmpty(userDTO.getPass()) || userDTO.getPass().length()==0){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"密码不能为空");
        }
        if (userDTO.getDeptId()==null){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"部门信息不能为空");
        }
        check(userDTO);

        DeptRole deptRole = deptRoleService.getByAdd(userDTO.getDeptId(),userDTO.getDeptRoleId());
        userDTO.setDeptRoleId(deptRole.getId());

        User user = BeanUtils.copyAs(userDTO, User.class);

        //部门主管变更
        if (user.getType()!=null && user.getType()==1){
            downUserTypes(user.getDeptId());
        }

        if (StringUtils.isEmpty(user.getAvatar()) || user.getAvatar().length()==0){
            String defaultAvatar = parameterService.methodGetParameterByParamName("user.default.avatar", null, Byte.valueOf("1")).getParamValue();
            user.setAvatar(defaultAvatar);
        }

        String saltValue = MathUtils.getRandomString(20);
        user.setSaltValue(saltValue);
        String pass = HashUtils.getMd5((user.getPass() + saltValue).getBytes());
        user.setPass(pass);
        save(user);

        return user;
    }

    private void downUserTypes(Integer deptId) {

        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("deptId",deptId);
        List<User> users = list(wrapper);
        if (!CollectionUtils.isEmpty(users) && users.size()>0){
            for (User user : users) {
                user.setType(0);
                user.setVerifyPermission(0);
            }
            updateBatchById(users);
            refushUsersByUpdate(users.stream().map(User::getId).distinct().collect(Collectors.toList()));
        }

/*        UpdateWrapper<User> wrapper = new UpdateWrapper<>();
        wrapper.eq("deptId",deptId);
        wrapper.set("`type`",0);
        wrapper.set("verifyPermission",0);
        update(wrapper);*/
    }


    @Transactional
    @Override
    public Map<String, Object> login(String account, String password, HttpServletRequest request, String mac) {

        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("account",account);
        User user = getOne(wrapper);
        if (user==null){
            throw new BusinessException(ResException.QUERY_MISS.getCode(),"账号输入有误");
        }
        String pass = HashUtils.getMd5((password + user.getSaltValue()).getBytes());
        if (!pass.equals(user.getPass())){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"密码错误");
        }
        if (user.getStatus()==1){
            throw new BusinessException(ResException.USER_LOCK);
        }

        Map<String,Object> res = new HashMap<>();
        UserDTO dto = refresh(user);
        res.put("tokenId",dto.getToken());
        res.put("deptRoleName",dto.getDeptRoleName());
        res.put("deptRoleLevel",dto.getDeptRoleLevel());
        res.put("createAllOrderPermission",dto.getCreateAllOrderPermission());
        res.put("distributionPermission",dto.getDistributionPermission());
        res.put("verifyPermission",dto.getVerifyPermission());
        res.put("linkChangePermission",dto.getLinkChangePermission());
        res.put("updatePermission",dto.getUpdatePermission());

        offLine(user.getId());

        UserLogin userLogin = new UserLogin();
        userLogin.setUserId(user.getId());
        userLogin.setLoginTime(LocalDateTime.now());
        userLoginService.save(userLogin);
        res.put("avatar",user.getAvatar());
        res.put("name",user.getName());
        res.put("userId",user.getId());
        res.put("deptId",user.getDeptId());
        res.put("type",user.getType());
        res.put("typeName",user.getType()==0?"普通员工":"部门主管");
        if (user.getAssembleTableType()!=null){
            res.put("assembleTableType",user.getAssembleTableType());
        }
        Dept dept = deptService.getById(user.getDeptId());
        if (dept!=null){
            res.put("deptName",dept.getTitle());
        }

        return res;
    }



    public void offLine(Integer userId){
        QueryWrapper<UserLogin> wrapper = new QueryWrapper<>();
        wrapper.eq("userId",userId);
        List<UserLogin> logins = userLoginService.list(wrapper);
        if (!CollectionUtils.isEmpty(logins) && logins.size()>0){
            UserLogin userLogin = logins.stream().max(Comparator.comparing(UserLogin::getLoginTime)).get();
            if (userLogin.getOffLineTime()==null){
                userLogin.setOffLineTime(LocalDateTime.now());
                userLoginService.updateById(userLogin);
            }
        }
    }

    @Transactional
    @Override
    public void logout(HttpServletRequest request) {
        UserDTO user = userContextUtils.getCurrentUser(request);
        if (user!=null){
            redisTemplate.delete(request.getHeader("App-Token"));
            userTokenService.removeByUserId(user.getId());
            offLine(user.getId());
        }
    }

    @Override
    public UserDTO getDTO(UserDTO user) {
        UserDTO dto = BeanUtils.copyAs(user, UserDTO.class);
        //部门名称
        Dept dept = deptService.getById(user.getDeptId());
        if (dept!=null){
            dto.setDeptName(dept.getTitle());
        }

        DeptRole role = deptRoleService.getById(user.getDeptRoleId());
        if (role!=null){
            dto.setDeptRoleName(role.getName());
        }

        dto.setTypeName(dto.getType()==1?"部门主管":"职工");

        return dto;
    }

    @Override
    public List<UserDTO> listByFilter(UserDTO userDTO, Filter filter) {
        List<User> list = listFilter(userDTO,filter);
        if (!CollectionUtils.isEmpty(list) && list.size()>0){
            return listDTO(list);
        }
        return null;
    }

    @Override
    public List<UserDTO> listDTO(List<User> list) {
        List<UserDTO> dtos = BeanUtils.copyAs(list, UserDTO.class);

        for (UserDTO dto : dtos) {
            dto.setDeptName("暂无信息");
            dto.setTypeName(dto.getType()==1?"部门主管":"职工");
        }

        //部门
        List<Dept> depts = (List<Dept>) deptService.listByIds(list.stream().map(User::getDeptId).distinct().collect(Collectors.toList()));
        List<DeptRole> roles = (List<DeptRole>) deptRoleService.listByIds(list.stream().map(User::getDeptRoleId).distinct().collect(Collectors.toList()));


        for (UserDTO dto : dtos) {
            if (!CollectionUtils.isEmpty(depts) && depts.size()>0){
                for (Dept dept : depts) {
                    if (dto.getDeptId().equals(dept.getId())){
                        dto.setDeptName(dept.getTitle());
                    }
                }
            }
            if (!CollectionUtils.isEmpty(roles) && roles.size()>0){
                for (DeptRole role : roles) {
                    if (role.getId().equals(dto.getDeptRoleId())){
                        dto.setDeptRoleName(role.getName());
                    }
                }
            }
        }


        return dtos;
    }

    @Override
    public BigInteger listCount(UserDTO userDTO, Filter filter) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        if (filter!=null){
            filter.setPage(null);
            filter.setPageSize(null);
        }
        addFilter(wrapper,userDTO,filter);
        return BigInteger.valueOf(baseMapper.selectCount(wrapper));
    }

    private void addFilter(QueryWrapper<User> wrapper, UserDTO userDTO, Filter filter) {
        if (userDTO!=null){
            if (userDTO.getDeptId()!=null){
                wrapper.eq("deptId",userDTO.getDeptId());
            }
            if (userDTO.getSex()!=null){
                wrapper.eq("sex",userDTO.getSex());
            }
            if (userDTO.getStatus()!=null){
                wrapper.eq("`Status`",userDTO.getStatus());
            }
            if (!StringUtils.isEmpty(userDTO.getName()) && userDTO.getName().length()>0){
                wrapper.eq("`name`",userDTO.getName());
            }
            if (!StringUtils.isEmpty(userDTO.getAccount()) && userDTO.getAccount().length()>0){
                wrapper.eq("account",userDTO.getAccount());
            }
            if (userDTO.getType()!=null){
                wrapper.eq("`type`",userDTO.getType());
            }
            if (userDTO.getVerifyPermission()!=null){
                wrapper.eq("verifyPermission",userDTO.getVerifyPermission());
            }
        }
        if (filter!=null){
            if (filter.getCreateTimeBegin()!=null){
                wrapper.ge("createTime",filter.getCreateTimeBegin());
            }
            if (filter.getCreateTimeEnd()!=null){
                wrapper.le("createTime",filter.getCreateTimeEnd());
            }
            if (!StringUtils.isEmpty(filter.getKeyword()) && filter.getKeyword().length()>0){
                wrapper.like("`name`",filter.getKeyword());
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

    @Override
    public List<User> listFilter(UserDTO userDTO, Filter filter) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        addFilter(wrapper,userDTO,filter);
        return list(wrapper);
    }

    @Override
    public UserDTO getDetail(Integer userId) {
        User user = getById(userId);
        if (user==null){
            throw new BusinessException(ResException.QUERY_MISS);
        }
        return getDTO(BeanUtils.copyAs(user,UserDTO.class));
    }


    @Transactional
    @Override
    public User adminUpdate(UserDTO userDTO) {
        User user = getById(userDTO);
        if (user==null){
            throw new BusinessException(ResException.QUERY_MISS);
        }

        if (userDTO.getType()!=null && userDTO.getType()==1 && !user.getType().equals(userDTO.getType())){
            downUserTypes(user.getDeptId());
        }

        check(userDTO);

        if (userDTO.getDeptRoleId()!=null){
            DeptRole deptRole = deptRoleService.getByAdd(userDTO.getDeptId(),userDTO.getDeptRoleId());
            userDTO.setDeptRoleId(deptRole.getId());
        }

        if (!StringUtils.isEmpty(userDTO.getPass()) && userDTO.getPass().length()>0){
            String pass = HashUtils.getMd5((userDTO.getPass()+user.getSaltValue()).getBytes());
            userDTO.setPass(pass);
        }
        userDTO.setUpdateTime(LocalDateTime.now());
        updateById(BeanUtils.copyAs(userDTO,User.class));

        List<Integer> userIds = new ArrayList<>();
        userIds.add(user.getId());
        refushUsersByUpdate(userIds);

        return user;
    }

    private void refushUsersByUpdate(List<Integer> userIds) {
        List<UserToken> userTokens = userTokenService.listByUserIds(userIds);
        if (!CollectionUtils.isEmpty(userTokens) && userTokens.size()>0){
            List<User> users = (List<User>) listByIds(userIds);
            if (!CollectionUtils.isEmpty(users) && users.size()>0){
                Parameter expireTimeStr = parameterService.methodGetParameterByParamName("user.login.expire.time", null, Byte.valueOf("1"));
                for (UserToken userToken : userTokens) {
                    for (User user : users) {
                        if (userToken.getUserID().equals(user.getId())){
                            user.setAccount(null);
                            user.setPass(null);
                            redisTemplate.opsForValue().set(userToken.getTokenID(),JSON.toJSONString(BeanUtils.copyAs(user,UserDTO.class)),Long.parseLong(expireTimeStr.getParamValue()),TimeUnit.SECONDS);
                            userToken.setExpireTime(userToken.getCreateTime().plusSeconds(Long.parseLong(expireTimeStr.getParamValue())));
                            userTokenService.updateById(userToken);
                        }
                    }
                }
            }
        }
    }

    @Override
    public User lock(Integer id) {
        User user = getById(id);
        if (user==null){
            throw new BusinessException(ResException.QUERY_MISS);
        }
        user.setStatus(user.getStatus()==0?1:0);
        updateById(user);
        if (user.getStatus()==1){
            Dept dept = deptService.getById(user.getDeptId());
            if (dept!=null){
                dept.setStaffCount(dept.getStaffCount()-1);
                deptService.updateById(dept);
            }
        }
        userTokenService.removeByUserId(user.getId());
        return user;
    }

    @Transactional
    @Override
    public User delete(Integer id) {
        User user = getById(id);
        if (user==null){
            throw new BusinessException(ResException.QUERY_MISS);
        }
        removeById(id);
        if (user.getStatus()==0){
            Dept dept = deptService.getById(user.getDeptId());
            if (dept!=null){
                dept.setStaffCount(dept.getStaffCount()-1);
                deptService.updateById(dept);
            }
        }
        return user;
    }

    @Override
    public void changePermission(Integer userId, UserDTO user) {
        User user1 = getById(userId);//用户
        if (user1==null){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"职工信息有误");
        }
        if (!user.getDeptId().equals(user1.getDeptId())){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"非同部门主管无权限操作");
        }
        user1.setVerifyPermission(user1.getVerifyPermission()==1?0:1);
        updateById(user1);
        List<Integer> userIds = new ArrayList<>();
        userIds.add(userId);
        refushUsersByUpdate(userIds);
    }

    @Override
    public void cleanDeptRoleByRoleId(Integer roleId) {
        UpdateWrapper<User> wrapper = new UpdateWrapper<>();
        wrapper.eq("deptRoleId",roleId);
        wrapper.set("deptRoleId",0);
        update(wrapper);
    }

/*    @Override
    public Integer getMaxDeptUserId(Integer deptId) {

        UserDTO userDTO = new UserDTO();
        userDTO.setDeptId(deptId);
        userDTO.setStatus(0);
        List<User> users = listFilter(userDTO, null);
        if (CollectionUtils.isEmpty(users) || users.size()==0){
            throw new BusinessException(ResException.MAKE_ERR.getCode(), DeptTypeModel.TYPE_MAP.get(deptId)+"暂无员工");
        }

        DeptRoleDTO deptRoleDTO = new DeptRoleDTO();
        deptRoleDTO.setDeptId(deptId);
        List<DeptRole> list = deptRoleService.listFilter(deptRoleDTO, null);
        list = list.stream().filter(deptRole->deptRole.getCreateAllOrderPermission()==1 && deptRole.getDistributionPermission()==1 && deptRole.getVerifyPermission()==1).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(list) || list.size()==0){
            throw new BusinessException(ResException.MAKE_ERR.getCode(),"当前部门权限未完善,请联系管理员");
        }
        list.
    }*/


    private UserDTO refresh(User user) {
        userTokenService.removeByUserId(user.getId());
        user.setAccount(null);
        user.setPass(null);
        String tokenId = TokenUtils.getTokenId();
        UserToken userToken = new UserToken();
        userToken.setUserID(user.getId());
        userToken.setTokenID(tokenId);
        userToken.setCreateTime(LocalDateTime.now());
        Parameter expireTimeStr = parameterService.methodGetParameterByParamName("user.login.expire.time", null, Byte.valueOf("1"));
        userToken.setExpireTime(userToken.getCreateTime().plusSeconds(Long.parseLong(expireTimeStr.getParamValue())));
        userTokenService.save(userToken);

        UserDTO dto = BeanUtils.copyAs(user, UserDTO.class);


        DeptRoleDTO roleDTO = deptRoleService.getDetail(user.getDeptRoleId());
        if (roleDTO!=null){
            dto.setDeptRoleName(roleDTO.getName());
            dto.setDeptRoleLevel(roleDTO.getLevel());
            dto.setCreateAllOrderPermission(roleDTO.getCreateAllOrderPermission());
            dto.setDistributionPermission(roleDTO.getDistributionPermission());
            dto.setVerifyPermission(roleDTO.getVerifyPermission());
            dto.setLinkChangePermission(roleDTO.getLinkChangePermission());
            dto.setUpdatePermission(roleDTO.getUpdatePermission());
        }

        dto.setToken(tokenId);

        redisTemplate.opsForValue().set(tokenId, JSON.toJSONString(dto),Long.parseLong(expireTimeStr.getParamValue()), TimeUnit.SECONDS);
        return dto;
    }

    private void check(UserDTO userDTO) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        if (userDTO.getId()!=null){
            wrapper.ne("ID",userDTO.getId());
        }
        if (userDTO.getDeptId()!=null){
            Dept dept = deptService.getById(userDTO.getDeptId());
            if (dept==null){
                throw new BusinessException(ResException.MAKE_ERR.getCode(),"部门信息有误");
            }
        }

        if (!StringUtils.isEmpty(userDTO.getAccount()) && userDTO.getAccount().length()>0){
            wrapper.eq("account",userDTO.getAccount());
            Integer count = baseMapper.selectCount(wrapper);
            if (count>0){
                throw new BusinessException(ResException.MAKE_ERR.getCode(),"账号已被使用");
            }
        }




    }
}
