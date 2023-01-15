package qichen.code.service;

import qichen.code.entity.AdminToken;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-11-23
 */
public interface IAdminTokenService extends IService<AdminToken> {

    List<AdminToken> listAdminTokens(Integer adminId);

    void removeByAdminId(Integer adminId);

}
