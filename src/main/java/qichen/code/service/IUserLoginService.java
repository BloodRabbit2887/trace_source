package qichen.code.service;

import qichen.code.entity.UserLogin;
import com.baomidou.mybatisplus.extension.service.IService;
import qichen.code.entity.dto.UserLoginDTO;
import qichen.code.model.Filter;
import qichen.code.model.SearchModel;

import java.math.BigInteger;
import java.util.List;

/**
 * <p>
 * 用户登录记录表 服务类
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-11-23
 */
public interface IUserLoginService extends IService<UserLogin> {

    List<SearchModel> searchByType(Integer type);

    List<UserLoginDTO> listByFilter(UserLoginDTO userLoginDTO, Filter filter);

    BigInteger listCount(UserLoginDTO userLoginDTO, Filter filter);

    UserLoginDTO getDetail(Integer id);

}
