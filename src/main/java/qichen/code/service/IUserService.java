package qichen.code.service;

import qichen.code.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import qichen.code.entity.dto.UserDTO;
import qichen.code.model.Filter;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-12-16
 */
public interface IUserService extends IService<User> {

    User add(UserDTO userDTO);

    Map<String, Object> login(String account, String password, HttpServletRequest request, String mac);

    void logout(HttpServletRequest request);

    UserDTO getDTO(UserDTO user);

    List<UserDTO> listByFilter(UserDTO userDTO, Filter filter);

    List<UserDTO> listDTO(List<User> list);

    BigInteger listCount(UserDTO userDTO, Filter filter);

    List<User> listFilter(UserDTO userDTO, Filter filter);

    UserDTO getDetail(Integer userId);

    User adminUpdate(UserDTO userDTO);

    User lock(Integer id);

    User delete(Integer id);

    void changePermission(Integer userId, UserDTO user);

    void cleanDeptRoleByRoleId(Integer roleId);

/*    Integer getMaxDeptUserId(Integer deptId);*/
}
