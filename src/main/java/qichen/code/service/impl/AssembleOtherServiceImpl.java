package qichen.code.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import qichen.code.entity.AssembleOther;
import qichen.code.entity.User;
import qichen.code.entity.UserTableProject;
import qichen.code.entity.dto.AssembleOtherDTO;
import qichen.code.entity.dto.UserTableProjectDTO;
import qichen.code.exception.BusinessException;
import qichen.code.exception.ResException;
import qichen.code.mapper.AssembleOtherMapper;
import qichen.code.model.AssembleTableTypeModel;
import qichen.code.model.Filter;
import qichen.code.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import qichen.code.utils.BeanUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 装配车间额外表单 服务实现类
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-02-17
 */
@Service
public class AssembleOtherServiceImpl extends ServiceImpl<AssembleOtherMapper, AssembleOther> implements IAssembleOtherService {

    @Autowired
    private IUserTableProjectService userTableProjectService;
    @Autowired
    private IUserService userService;
    @Autowired
    private IMouldBasePackageService mouldBasePackageService;
    @Autowired
    private IAssembleCheckPackageService assembleCheckPackageService;
    @Autowired
    private IAssembleModelPushPackageService assembleModelPushPackageService;


    @Override
    public AssembleOtherDTO getOtherModel(Integer userId, String number) {
        User user = userService.getById(userId);
        if (user==null){
            throw new BusinessException(ResException.USER_MISS);
        }
        AssembleOtherDTO dto = new AssembleOtherDTO();
        dto.setNeed(0);

        Map<String,Object> map = checkFinishAll(userId,number,user.getDeptId());
        if (map!=null){
            dto.setNumber(number);
            dto.setOrderId(Integer.parseInt(map.get("orderId").toString()));
            dto.setTableType(Integer.parseInt(map.get("type").toString()));
            dto.setNeed(1);
        }
        return dto;
    }

    @Override
    public void add(AssembleOtherDTO dto) {
        AssembleOther other = BeanUtils.copyAs(dto, AssembleOther.class);
        save(other);
    }

    private Map<String,Object> checkFinishAll(Integer userId, String number, Integer deptId) {
        UserTableProjectDTO dto = new UserTableProjectDTO();
        dto.setDeptId(deptId);
        dto.setUserId(userId);
        dto.setNumber(number);
        dto.setStatus(1);
        List<UserTableProject> list = userTableProjectService.listFilter(dto, new Filter());
        Map<String,Object> res = new HashMap<>();
        if (!CollectionUtils.isEmpty(list) && list.size()>0){
            UserTableProject project = list.get(0);
            Integer tableType = project.getTableType();
            List<Integer> skips = new ArrayList<>();
            if (!StringUtils.isEmpty(project.getSkipTypes()) && project.getSkipTypes().length()>0){
                skips = Arrays.stream(project.getSkipTypes().split(",")).map(Integer::parseInt).collect(Collectors.toList());
            }
            if (!CollectionUtils.isEmpty(skips) && skips.size()>0){
                if (tableType.equals(AssembleTableTypeModel.TYPE_MOULE_BASE)){
                    if (skips.contains(AssembleTableTypeModel.TYPE_DOWN) && skips.contains(AssembleTableTypeModel.TYPE_PLANK)){
                        Integer id = mouldBasePackageService.getIdByAssembleOther(userId, number);
                        if (id!=null){
                            res.put("orderId",id);
                            res.put("type",AssembleTableTypeModel.TYPE_MOULE_BASE);
                            return res;
                        }
                    }
                }
                if (tableType.equals(AssembleTableTypeModel.TYPE_DOWN)){
                    if (skips.contains(AssembleTableTypeModel.TYPE_PLANK)){
                        Integer id = mouldBasePackageService.getIdByAssembleOther(userId,number);
                        if (id!=null){
                            res.put("orderId",id);
                            res.put("type",AssembleTableTypeModel.TYPE_MOULE_BASE);
                            return res;
                        }
                    }
                }
                if (tableType.equals(AssembleTableTypeModel.TYPE_PACKAGE) && skips.contains(AssembleTableTypeModel.TYPE_ALLOY)){
                    Integer id = assembleCheckPackageService.getIdByAssembleOther(userId,number);
                    if (id!=null){
                        res.put("orderId",id);
                        res.put("type",AssembleTableTypeModel.TYPE_PACKAGE);
                        return res;
                    }
                }
                if (tableType.equals(AssembleTableTypeModel.TYPE_MODEL_PUSH)){
                    Integer id = mouldBasePackageService.getIdByAssembleOther(userId,number);
                    if (id!=null){
                        res.put("orderId",id);
                        res.put("type",AssembleTableTypeModel.TYPE_MOULE_BASE);
                        return res;
                    }
                }
            }else {
                if (tableType.equals(AssembleTableTypeModel.TYPE_PLANK)){
                    Integer id = mouldBasePackageService.getIdByAssembleOther(userId,number);
                    if (id!=null){
                        res.put("orderId",id);
                        res.put("type",AssembleTableTypeModel.TYPE_MOULE_BASE);
                        return res;
                    }
                }else if (tableType.equals(AssembleTableTypeModel.TYPE_ALLOY)){
                    Integer id = assembleCheckPackageService.getIdByAssembleOther(userId,number);
                    if (id!=null){
                        res.put("orderId",id);
                        res.put("type",AssembleTableTypeModel.TYPE_PACKAGE);
                        return res;
                    }
                }else if (tableType.equals(AssembleTableTypeModel.TYPE_MODEL_PUSH)){
                    Integer id = mouldBasePackageService.getIdByAssembleOther(userId,number);
                    if (id!=null){
                        res.put("orderId",id);
                        res.put("type",AssembleTableTypeModel.TYPE_MOULE_BASE);
                        return res;
                    }
                }
            }

        }
        return null;
    }
}
