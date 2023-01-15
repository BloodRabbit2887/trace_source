package qichen.code.service;

import qichen.code.entity.Parameter;
import com.baomidou.mybatisplus.extension.service.IService;
import qichen.code.entity.dto.ParameterDTO;
import qichen.code.entity.dto.ParameterFilterDTO;
import qichen.code.model.Filter;

import java.math.BigInteger;
import java.util.List;

/**
 * <p>
 * 参数表 服务类
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-11-23
 */
public interface IParameterService extends IService<Parameter> {

    ParameterDTO getParameterByParamName(String paramName, Integer chainId, Byte status, boolean bFromCache);

    Parameter methodGetParameterByParamName(String paramName, Integer chainId, Byte status);

    List<ParameterDTO> listRef(Byte statusOk, Byte def);

    boolean checkUniqueness(Integer id, String paramName, Integer storeId, Byte status);

    List<ParameterDTO> listByFilter(ParameterFilterDTO filter, Filter filterEx);

    BigInteger listCount(ParameterFilterDTO filter, Filter filterEx);


    ParameterDTO getDetails(ParameterDTO filter);

    void rucAdd(ParameterDTO parameterDTO);

    void rucUpdate(ParameterDTO parameterDTO);

    void delete(ParameterDTO parameterDTO);

    String getText(Integer type);

    String getValueByName(String name);
}
