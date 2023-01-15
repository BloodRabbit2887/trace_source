package qichen.code.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import qichen.code.entity.AdminToken;
import qichen.code.mapper.AdminTokenMapper;
import qichen.code.service.IAdminTokenService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-11-23
 */
@Service
public class AdminTokenServiceImpl extends ServiceImpl<AdminTokenMapper, AdminToken> implements IAdminTokenService {

    @Override
    public List<AdminToken> listAdminTokens(Integer adminId) {
        QueryWrapper<AdminToken> wrapper = new QueryWrapper<>();
        wrapper.eq("adminId", adminId);
        Date now = new Date();
        wrapper.le("createTime", now);
        wrapper.ge("expireTime", now);
        return this.list(wrapper);
    }

    @Override
    public void removeByAdminId(Integer adminId) {
        UpdateWrapper<AdminToken> wrapper = new UpdateWrapper<>();
        wrapper.eq("adminId", adminId);
        this.remove(wrapper);
    }
}
