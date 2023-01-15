package qichen.code.service;

import qichen.code.entity.AdminMenu;
import com.baomidou.mybatisplus.extension.service.IService;
import qichen.code.entity.dto.AdminMenuDTO;

import java.util.List;

/**
 * <p>
 * 管理员菜单表 服务类
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-11-23
 */
public interface IAdminMenuService extends IService<AdminMenu> {

    List<AdminMenu> listByFliter(AdminMenuDTO adminMenuDTO);

    void add(AdminMenuDTO adminMenu);

    List<AdminMenu> listByAdminId(Integer adminId);
}
