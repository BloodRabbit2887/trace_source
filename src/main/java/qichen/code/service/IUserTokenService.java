package qichen.code.service;

import qichen.code.entity.UserToken;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 用户令牌表 服务类
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-11-23
 */
public interface IUserTokenService extends IService<UserToken> {

    void removeByUserId(Integer userId);

    List<UserToken> listByUserIds(List<Integer> userIds);
}
