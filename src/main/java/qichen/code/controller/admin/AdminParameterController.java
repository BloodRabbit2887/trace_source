package qichen.code.controller.admin;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import qichen.code.entity.Parameter;
import qichen.code.entity.dto.AdminDTO;
import qichen.code.entity.dto.ParameterDTO;
import qichen.code.entity.dto.ParameterFilterDTO;
import qichen.code.exception.ResException;
import qichen.code.model.Filter;
import qichen.code.model.ResponseBean;
import qichen.code.service.IOperationLogService;
import qichen.code.service.IParameterService;
import qichen.code.utils.FileUtils;
import qichen.code.utils.UserContextUtils;


import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/api/v1/admin/parameter")
public class AdminParameterController {

	@Autowired
	private IParameterService parameterService;

	@Autowired
	private IOperationLogService operationLogService;
	@Autowired
	private UserContextUtils userContextUtils;

	/**
	 * get 查询参数信息 /api/v1/admin/parameter/query
	 * @param storeId(Integer) 连锁ID
	 * @param status(Byte)     状态
	 * @param keyword(String)  关键字(用于搜索)
	 * @param orders(String)   排序
	 * @param page(int)        起始页码
	 * @param pageSize(int)    获取记录数
	 */
	@ResponseBody
	@RequestMapping(value = "/query", method = RequestMethod.GET)
	public ResponseBean query(HttpServletRequest request, @RequestParam(value = "storeId", required = false) Integer storeId,
							  @RequestParam(value = "status", required = false) Byte status,
							  @RequestParam(value = "keyword", required = false) String keyword,
							  @RequestParam(value = "orders", required = false) String orders,
							  @RequestParam(value = "page", defaultValue = "1") int page,
							  @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

		AdminDTO adminDTO = userContextUtils.newGetCurrentAdmin(request);
		if (adminDTO==null){
			return new ResponseBean(ResException.ADMIN_LOGIN_MISS);
		}
		Byte adminType = adminDTO.getAdminType().byteValue();
		ParameterFilterDTO filter = new ParameterFilterDTO();
		if (AdminDTO.TYPE_PLATFORM.equals(adminType)) {
			filter.setStoreId(storeId);
		} else {
			return new ResponseBean(ResException.ADMIN_PER_MISS);
		}

		if (storeId!=null){
			filter.setStoreId(storeId);
		}
		if (status!=null){
			filter.setStatus(status);
		}
		filter.setDelTf((byte) 0);

		Filter filterEx = new Filter();
		if (!StringUtils.isEmpty(keyword) && keyword.length()>0){
			filterEx.setKeyword(keyword);
		}
		if (!StringUtils.isEmpty(orders) && orders.length()>0){
			filterEx.setOrders(orders);
		}
		filterEx.setPage(page);
		filterEx.setPageSize(pageSize);

		try {
			List<ParameterDTO> list = parameterService.listByFilter(filter, filterEx);
			BigInteger count = parameterService.listCount(filter, filterEx);
			Map<String, Object> map = new HashMap<>();
			map.put("list", list);
			map.put("totalCount", count);
			ResponseBean responseBean = new ResponseBean();
			responseBean.setData(map);
			return responseBean;
		} catch (Exception e) {
			log.error(e.getMessage());
			return new ResponseBean(ResException.QUERY_MISS);
		}
	}


	/**
	 * 获取自身参数信息详情

	 */
	@ResponseBody
	@RequestMapping(value = "/getSelf", method = RequestMethod.GET)
	public ResponseBean getSelf(HttpServletRequest request) {

		AdminDTO adminDTO = userContextUtils.newGetCurrentAdmin(request);
		if (adminDTO==null){
			return new ResponseBean(ResException.ADMIN_LOGIN_MISS);
		}
		Byte adminType = adminDTO.getAdminType().byteValue();
		Integer relatedId = adminDTO.getRelatedID();
		ParameterDTO filter = new ParameterDTO();
		if (!AdminDTO.TYPE_PLATFORM.equals(adminType)) {
			return new ResponseBean(ResException.ADMIN_PER_MISS);
		}

		filter.setId(relatedId);
		ParameterDTO parameter = parameterService.getDetails(filter);
		ResponseBean responseBean = new ResponseBean();
		responseBean.setData(parameter);
		return responseBean;
	}

	/**
	 * get 获取参数信息详情 /api/v1/admin/parameter/details
	 * @param id(Integer) id
	 */
	@ResponseBody
	@RequestMapping(value = "/details", method = RequestMethod.GET)
	public ResponseBean getDetails(HttpServletRequest request,@RequestParam(value = "id", required = true) Integer id) {

		AdminDTO adminDTO = userContextUtils.newGetCurrentAdmin(request);
		if (adminDTO==null){
			return new ResponseBean(ResException.ADMIN_LOGIN_MISS);
		}
		Byte adminType = adminDTO.getAdminType().byteValue();
		ParameterDTO filter = new ParameterDTO();
		if (!AdminDTO.TYPE_PLATFORM.equals(adminType)) {
			return new ResponseBean(ResException.ADMIN_PER_MISS);
		}

		filter.setId(id);
		ParameterDTO parameter = parameterService.getDetails(filter);
		ResponseBean responseBean = new ResponseBean();
		responseBean.setData(parameter);
		return responseBean;
	}

	/**
	 * post 添加参数信息 /api/v1/admin/parameter/add
	 */
	@ResponseBody
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public ResponseBean addParameter(HttpServletRequest request,@RequestBody ParameterDTO parameterDTO) {
		AdminDTO adminDTO = userContextUtils.newGetCurrentAdmin(request);
		if (adminDTO==null){
			return new ResponseBean(ResException.ADMIN_LOGIN_MISS);
		}
		Integer adminId = adminDTO.getId();
		Byte adminType = adminDTO.getAdminType().byteValue();
		Integer operManType = adminDTO.getAdminType();
		if (!AdminDTO.TYPE_PLATFORM.equals(adminType)) {
			return new ResponseBean(ResException.ADMIN_PER_MISS);
		}

		String remark = JSON.toJSONString(parameterDTO);
		parameterService.rucAdd(parameterDTO);
		operationLogService.saveOperationLog(operManType, adminId, "310", "添加【参数信息】", "t_parameter",
				parameterDTO.getId(), remark);
		return new ResponseBean();
	}

	/**
	 * post 修改参数信息 /api/v1/admin/parameter/update
	 */
	@ResponseBody
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public ResponseBean updateParameter(HttpServletRequest request,@RequestBody ParameterDTO parameterDTO) {
		AdminDTO adminDTO = userContextUtils.newGetCurrentAdmin(request);
		if (adminDTO == null) {
			return new ResponseBean(ResException.ADMIN_LOGIN_MISS);
		}
		Integer adminId = adminDTO.getId();
		Byte adminType = adminDTO.getAdminType().byteValue();
		Integer operManType = adminDTO.getAdminType();
		if (!AdminDTO.TYPE_PLATFORM.equals(adminType)) {
			return new ResponseBean(ResException.ADMIN_PER_MISS);
		}

		String remark = JSON.toJSONString(parameterDTO);
		parameterService.rucUpdate(parameterDTO);
		operationLogService.saveOperationLog(operManType, adminId, "310", "修改【参数信息】", "t_parameter",
				parameterDTO.getId(), remark);
		return new ResponseBean();
	}

	@ResponseBody
	@PostMapping("/updateShareImg")
	public ResponseBean updateShareImg(HttpServletRequest request, @RequestParam(value = "file")MultipartFile file){
		AdminDTO adminDTO = userContextUtils.newGetCurrentAdmin(request);
		if (adminDTO==null){
			return new ResponseBean(ResException.ADMIN_LOGIN_MISS);
		}
		Byte adminType = adminDTO.getAdminType().byteValue();
		if (!AdminDTO.TYPE_PLATFORM.equals(adminType)) {
			return new ResponseBean(ResException.ADMIN_PER_MISS);
		}
		String path = FileUtils.saveMultipartFile(file, null);
		Parameter parameter = parameterService.methodGetParameterByParamName("share.poster", null, Byte.valueOf("1"));
		parameter.setParamValue(path);
		parameterService.updateById(parameter);
		return new ResponseBean(path);
	}



	/**
	 * post 删除参数信息 /api/v1/admin/parameter/delete
	 * @param id(Integer) id
	 */
	@ResponseBody
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public ResponseBean deleteParameter(HttpServletRequest request,@RequestParam(value = "id", required = true) Integer id) {
		AdminDTO adminDTO = userContextUtils.newGetCurrentAdmin(request);
		if (adminDTO==null){
			return new ResponseBean(ResException.ADMIN_LOGIN_MISS);
		}
		Integer adminId = adminDTO.getId();
		Byte adminType = adminDTO.getAdminType().byteValue();
		Integer operManType = adminDTO.getAdminType();
		ParameterDTO parameterDTO = new ParameterDTO();
		if (!AdminDTO.TYPE_PLATFORM.equals(adminType)) {
			return new ResponseBean(ResException.ADMIN_PER_MISS);
		}

		parameterDTO.setId(id);
		parameterService.delete(parameterDTO);
		operationLogService.saveOperationLog(operManType, adminId, "310", "删除【参数信息】", "t_parameter",
				parameterDTO.getId(), null);

		return new ResponseBean();
	}




}
