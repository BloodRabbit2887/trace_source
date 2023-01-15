package qichen.code.utils;

import com.alibaba.fastjson.JSONObject;
import qichen.code.exception.BusinessException;
import qichen.code.exception.ResException;

import java.util.Map;

public class JsonUtils {

    //入参空值校验
    public static void checkColumnNull(Map<String,String> columns, JSONObject object){
        for (String key : columns.keySet()) {
            if (object.get(key)==null){
                throw new BusinessException(ResException.MAKE_ERR.getCode(),columns.get(key)+"不可为空");
            }
        }
    }
}
