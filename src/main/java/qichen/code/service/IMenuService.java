package qichen.code.service;

import qichen.code.entity.Menu;
import com.baomidou.mybatisplus.extension.service.IService;
import qichen.code.entity.dto.MenuDTO;

import java.util.List;

/**
 * <p>
 * 菜单表 服务类
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-12-16
 */
public interface IMenuService extends IService<Menu> {

    void setAccess(MenuDTO filter);

    List<MenuDTO> list2(MenuDTO filter);

    List<MenuDTO> menulist(MenuDTO filter);

    MenuDTO getMenu(MenuDTO filter);

    List<MenuDTO> listByFilter(MenuDTO filter);

    List<Menu> listByAdminId(Integer adminId);

    MenuDTO add(MenuDTO menuDTO);

    void updateDto(MenuDTO menuDTO);

    void delete(MenuDTO menuDTO);

    List<MenuDTO> listAndSetAccess(MenuDTO menuFilter);
}
