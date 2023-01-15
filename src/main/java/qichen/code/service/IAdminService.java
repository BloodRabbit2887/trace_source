package qichen.code.service;

import qichen.code.entity.Admin;
import com.baomidou.mybatisplus.extension.service.IService;
import qichen.code.entity.dto.AdminDTO;

import java.util.List;

/**
 * <p>
 * 管理员表 服务类
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-11-23
 */
public interface IAdminService extends IService<Admin> {

    List<AdminDTO> listDtoByPage(AdminDTO filter, int page, int pageSize);

    int listCount(AdminDTO filter);

    List<Admin> listByPage(AdminDTO filter, int page, int pageSize);

    AdminDTO getAdmin(Integer id);

    Admin add(AdminDTO adminDTO);

    Admin getByAdminNo(String adminNo);

    int listByAdminNo(String adminNo);

    void updateDto(AdminDTO adminDTO);

    AdminDTO login(String loginName, String password);

    String procToken(AdminDTO adminDTO, boolean deleteOld);

    void check(AdminDTO adminDTO);

    void updatePassword(Integer id,String oldPass, String newPass);

}
