package qichen.code.utils;


import com.alibaba.fastjson.serializer.PropertyPreFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qichen.code.entity.dto.MenuDTO;
import qichen.code.entity.dto.ParameterDTO;
import qichen.code.entity.dto.ParameterFilterDTO;
import qichen.code.service.IMenuService;
import qichen.code.service.IParameterService;


import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class ContextUtils {

    private static ThreadLocal<PropertyPreFilter> propertyPreFilter = new ThreadLocal<>();

	private static IMenuService menuService;
	private static IParameterService parameterService;


	@Autowired
    public void setMenuService(IMenuService menuService) {
        ContextUtils.menuService = menuService;
    }
	
	@Autowired
    public void setParameterService(IParameterService parameterService) {
        ContextUtils.parameterService = parameterService;
    }


    @PostConstruct
    public void init() {
        refresh();
    }

	

    // 订单状态
    private static Map<String, String> orderStatusMap;
	
	// 权限列表
    private static ConcurrentHashMap<String, Map<String, String>> accessMap;
    
    // 账户收支类型
    private static Map<String, String> accountInOrOutText;
    
    // 参数
    private static Map<String, ParameterDTO> parameterMap;

	public static void refresh() {
				// 需在最前执行
		refreshParameter();				// 需在最前执行
		initAccountInOrOut();
		initOrderStatus();
		initOrderErrorStatus();
		initOrderRefundStatus();
	}


	public static void insertAccessMap(Integer id, Map<String, String> maps) {
		if (null == ContextUtils.accessMap) {
			ContextUtils.accessMap = new ConcurrentHashMap<>();
		}
		ContextUtils.accessMap.put(id.toString(), maps);  
    }
	
	public static void clearAccessMap(Integer id) {
		String key = id.toString();
		if (null != ContextUtils.accessMap && ContextUtils.accessMap.containsKey(key)) {
			ContextUtils.accessMap.remove(key);
		}  
    }
	
	public static boolean checkAccess(Integer id, String api, String method) {
		
		if (api.equals("logout")) {
			return true;
		}
		
		if (null == ContextUtils.accessMap) {
			MenuDTO filter = new MenuDTO();
			filter.setAdminId(id);
			menuService.setAccess(filter);
		}
		
		boolean result = false;	
		String key = id.toString();

		if (ContextUtils.accessMap.containsKey(key)){ 
			Map<String, String> maps = ContextUtils.accessMap.get(key);
			if (maps.containsKey(method.concat(api))){
				result = true;
			}
		} 
		
		return result;
    }
    
    // 初始化账户收支类型TEXT
    public static void initAccountInOrOut() {
       Map<String, String> maps = new HashMap<>();
        
//        maps.put(UserAccountDTO.U_BUY_IN, "购买获取阿拉币");
        
//        maps.put(UserAccountDTO.U_JS_OUT, "放入急售使用");  
//        maps.put(UserAccountDTO.U_ZD_OUT, "急售中置顶使用");

       ContextUtils.accountInOrOutText = maps;
    }
    
    
    public static String getAccountInOrOutText(String key) {
		if (accountInOrOutText.containsKey(key)){
			return accountInOrOutText.get(key);
		} else {
			return null;
		}
	}
    
    // 初始化订单状态TEXT
    public static void initOrderStatus() {
        Map<String, String> maps = new HashMap<>();
       
        ContextUtils.orderStatusMap = maps;
    }
    
    // 初始化订单异常状态TEXT
    public static void initOrderErrorStatus() {
        Map<String, String> maps = new HashMap<>();
       
        ContextUtils.orderStatusMap = maps;
    }
    
    // 初始化订单退款状态TEXT
    public static void initOrderRefundStatus() {
        Map<String, String> maps = new HashMap<>();
      
        ContextUtils.orderStatusMap = maps;
    }
    
    public static String geOrderStatusText(String key) {
		if (orderStatusMap.containsKey(key)){
			return orderStatusMap.get(key);
		} else {
			return null;
		}
	}


    public static void setPropertyPreFilter(PropertyPreFilter propertyPreFilter) {
        ContextUtils.propertyPreFilter.set(propertyPreFilter);
    }

    public static PropertyPreFilter getPropertyPreFilter() {
        return propertyPreFilter.get();
    }



    
    public static void refreshParameter() {

    	ConcurrentHashMap<String, ParameterDTO> tempMap = new ConcurrentHashMap<>();
    	ParameterFilterDTO filter = new ParameterFilterDTO();
    	filter.setStatus(ParameterDTO.STATUS_OK);
    	filter.setDelTf(Byte.valueOf("0"));
      
        // 参数list
        List<ParameterDTO> parameterList = parameterService.listRef(ParameterDTO.STATUS_OK,Byte.valueOf("0"));

        for (ParameterDTO item : parameterList) {
        	tempMap.put(getParamKey(item.getStoreID(), item.getParamName()), item);
        }



        ContextUtils.parameterMap = tempMap; 
    }
    
    public static String getParamKey(Integer storeId, String paramName) {
    	String key = "";
    	key = (null == storeId || storeId.equals(0)) ? paramName:paramName.concat(".").concat(storeId.toString());
    	
    	return key;
    }
    
    public static ParameterDTO getParameter(Integer storeId, String paramName, boolean bFromDbIfNotExist) {
        refreshParameter();
    	String key = getParamKey(storeId, paramName);
    	ParameterDTO parameterDTO = ContextUtils.parameterMap.get(key);
    	if (null == parameterDTO && bFromDbIfNotExist) {
    		parameterDTO = parameterService.getParameterByParamName(paramName, storeId, ParameterDTO.STATUS_OK, false);
    	}
	    return parameterDTO;
    }
    
    
    public static void addOrUpdateParameter(ParameterDTO parameterDTO) {
        refreshParameter();
    	String key = getParamKey(parameterDTO.getStoreID(), parameterDTO.getParamName());
    	ContextUtils.parameterMap.put(key, parameterDTO);
    }
    
    
    public static void deleteParameter(Integer storeId, String paramName) {
    	String key = getParamKey(storeId, paramName);
	    if (ContextUtils.parameterMap.containsKey(key)){ 
	    	ContextUtils.parameterMap.remove(key);
		} 
    }
    
    public static String getImgHeadUrl(Byte type) {
    	String paramName = "";
    	Integer storeId = null;
    	boolean bFromDbIfNotExist = true;
    	if (Byte.valueOf("1").equals(type)) {
    		paramName = "thumb.virtual.address";
    	} else if (Byte.valueOf("2").equals(type)) {
    		paramName = "upload.virtual.address";
    	} else if (Byte.valueOf("3").equals(type)) {
    		paramName = "qr.virtual.address";
    	}
    	String key = getParamKey(storeId, paramName);
    	ParameterDTO parameterDTO = ContextUtils.parameterMap.get(key);
    	if (null == parameterDTO && bFromDbIfNotExist) {
    		parameterDTO = parameterService.getParameterByParamName(paramName, storeId, ParameterDTO.STATUS_OK, false);
    	}
	    if(null != parameterDTO){
	    	 return parameterDTO.getParamValue();
	    }else{
	    	return null;
	    }
	   
    }
    
}
