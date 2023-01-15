package qichen.code.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import qichen.code.entity.User;
import qichen.code.entity.UserLogin;
import qichen.code.entity.dto.UserDTO;
import qichen.code.entity.dto.UserLoginDTO;
import qichen.code.exception.BusinessException;
import qichen.code.exception.ResException;
import qichen.code.mapper.UserLoginMapper;
import qichen.code.model.Filter;
import qichen.code.model.SearchModel;
import qichen.code.service.IUserLoginService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import qichen.code.service.IUserService;
import qichen.code.utils.BeanUtils;

import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户登录记录表 服务实现类
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-11-23
 */
@Service
public class UserLoginServiceImpl extends ServiceImpl<UserLoginMapper, UserLogin> implements IUserLoginService {

    @Autowired
    private IUserService userService;

    @Override
    public List<SearchModel> searchByType(Integer type) {
        List<SearchModel> models = new ArrayList<>();
        QueryWrapper<UserLogin> wrapper = new QueryWrapper<>();
        LocalDateTime startTime = LocalDateTime.now();
        switch (type){
            case 1://今日登录 24小时
                startTime = LocalDateTime.now().toLocalDate().atStartOfDay();
                for (int i = 0; i < 24; i++) {
                    LocalDateTime dateTime = startTime.plusHours(i);
                    SearchModel model = new SearchModel();
                    model.setLoginTime(dateTime);
                    model.setCount(0);
                    models.add(model);
                }
                break;
            case 2://本周登录 7日内
                startTime = LocalDateTime.now().toLocalDate().with(DayOfWeek.MONDAY).atStartOfDay();
                for (int i = 0; i < 7; i++) {
                    LocalDateTime dateTime = startTime.plusDays(i);
                    SearchModel model = new SearchModel();
                    model.setLoginTime(dateTime);
                    models.add(model);
                    model.setCount(0);
                }
                break;
            case 3://本月 30日内
                startTime = LocalDateTime.now().toLocalDate().with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();
                LocalDateTime enTime = LocalDateTime.now().toLocalDate().with(TemporalAdjusters.lastDayOfMonth()).atStartOfDay().plusDays(1);
                for (int i = 0; i < 31; i++) {
                    LocalDateTime dateTime = startTime.plusDays(i);
                    if (enTime.compareTo(dateTime)>0){
                        SearchModel model = new SearchModel();
                        model.setLoginTime(dateTime);
                        models.add(model);
                        model.setCount(0);
                    }
                }
                break;
        }
        wrapper.ge("loginTime",startTime);
        List<UserLogin> list = list(wrapper);
        if (!CollectionUtils.isEmpty(list) && list.size()>0){
            for (SearchModel model : models) {
                LocalDateTime expireTime = type==1?model.getLoginTime().plusHours(1):model.getLoginTime().plusDays(1);
                List<UserLogin> logins = list.stream().filter(login -> login.getLoginTime().compareTo(model.getLoginTime()) > 0 && login.getLoginTime().compareTo(expireTime) < 0).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(logins) && logins.size()>0){
                    model.setCount(logins.size());
                }
            }
        }

        return models;
    }

    @Override
    public List<UserLoginDTO> listByFilter(UserLoginDTO userLoginDTO, Filter filter) {
        List<UserLogin> list = listFilter(userLoginDTO,filter);
        if (!CollectionUtils.isEmpty(list) && list.size()>0){
            return listDTO(list);
        }
        return null;
    }

    private List<UserLoginDTO> listDTO(List<UserLogin> list) {
        List<UserLoginDTO> dtos = BeanUtils.copyAs(list, UserLoginDTO.class);

        List<UserDTO> userDTOS = new ArrayList<>();
        List<User> users = (List<User>) userService.listByIds(list.stream().map(UserLogin::getUserId).distinct().collect(Collectors.toList()));
        if (!CollectionUtils.isEmpty(users) && users.size()>0){
            userDTOS = userService.listDTO(users);
        }
        for (UserLoginDTO dto : dtos) {
            if (!CollectionUtils.isEmpty(userDTOS) && userDTOS.size()>0){
                for (UserDTO userDTO : userDTOS) {
                    if (userDTO.getId().equals(dto.getUserId())){
                        dto.setUserName(userDTO.getName());
                        dto.setDeptId(userDTO.getDeptId());
                        dto.setDeptName(userDTO.getDeptName());
                    }
                }
            }
        }

        return dtos;
    }

    private List<UserLogin> listFilter(UserLoginDTO userLoginDTO, Filter filter) {
        QueryWrapper<UserLogin> wrapper = new QueryWrapper<>();
        addFilter(wrapper,userLoginDTO,filter);
        return list(wrapper);
    }

    private void addFilter(QueryWrapper<UserLogin> wrapper, UserLoginDTO userLoginDTO, Filter filter) {
        if (userLoginDTO!=null){
            if (userLoginDTO.getUserId()!=null){
                wrapper.eq("userId",userLoginDTO.getUserId());
            }
            if (userLoginDTO.getLoginTime()!=null){
                wrapper.ge("loginTime",userLoginDTO.getLoginTime());
            }
            if (userLoginDTO.getOffLineTime()!=null){
                wrapper.le("offLineTime",userLoginDTO.getOffLineTime());
            }
            if (userLoginDTO.getDeptId()!=null){
                List<Integer> userIds = new ArrayList<>();
                userIds.add(0);
                UserDTO userDTO = new UserDTO();
                userDTO.setDeptId(userLoginDTO.getDeptId());
                List<User> users = userService.listFilter(userDTO, null);
                if (!CollectionUtils.isEmpty(users) && users.size()>0){
                    userIds.addAll(users.stream().map(User::getId).distinct().collect(Collectors.toList()));
                }
                wrapper.in("userId",userIds);
            }
        }
        if (filter!=null){
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
    public BigInteger listCount(UserLoginDTO userLoginDTO, Filter filter) {
        QueryWrapper<UserLogin> wrapper = new QueryWrapper<>();
        if (filter!=null){
            filter.setPage(null);
            filter.setPageSize(null);
        }
        addFilter(wrapper,userLoginDTO,filter);
        return BigInteger.valueOf(baseMapper.selectCount(wrapper));
    }

    @Override
    public UserLoginDTO getDetail(Integer id) {
        UserLogin login = getById(id);
        if (login==null){
            throw new BusinessException(ResException.QUERY_MISS);
        }
        return getDTO(login);
    }

    private UserLoginDTO getDTO(UserLogin login) {
        UserLoginDTO dto = BeanUtils.copyAs(login, UserLoginDTO.class);
        User user = userService.getById(login.getUserId());
        if (user!=null){
            UserDTO userDTO = userService.getDTO(BeanUtils.copyAs(user,UserDTO.class));
            dto.setDeptId(userDTO.getDeptId());
            dto.setUserName(userDTO.getName());
            dto.setDeptName(userDTO.getDeptName());
        }

        return dto;
    }
}
