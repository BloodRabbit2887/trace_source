package qichen.code.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import qichen.code.entity.UserToken;
import qichen.code.mapper.UserTokenMapper;
import qichen.code.service.IUserTokenService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户令牌表 服务实现类
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-11-23
 */
@Service
public class UserTokenServiceImpl extends ServiceImpl<UserTokenMapper, UserToken> implements IUserTokenService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void removeByUserId(Integer userId) {
        QueryWrapper<UserToken> wrapper = new QueryWrapper<>();
        wrapper.eq("UserID",userId);
        List<UserToken> tokens = list(wrapper);
        if (!CollectionUtils.isEmpty(tokens) && tokens.size()>0){
            redisTemplate.delete(tokens.stream().map(UserToken::getTokenID).distinct().collect(Collectors.toList()));
        }
        remove(wrapper);
    }

    @Override
    public List<UserToken> listByUserIds(List<Integer> userIds) {
        QueryWrapper<UserToken> wrapper = new QueryWrapper<>();
        wrapper.in("UserID",userIds);
        return list(wrapper);
    }

}
