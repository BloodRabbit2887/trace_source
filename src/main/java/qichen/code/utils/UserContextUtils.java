package qichen.code.utils;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import qichen.code.entity.dto.AdminDTO;
import qichen.code.entity.dto.UserDTO;

import javax.servlet.http.HttpServletRequest;


@Component
public class UserContextUtils {

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	public UserDTO getCurrentUser(HttpServletRequest request){

		if (StringUtils.isEmpty(request.getHeader("App-Token")) || request.getHeader("App-Token").length()==0){
			return null;
		}
		String userToken = stringRedisTemplate.opsForValue().get(request.getHeader("App-Token"));
		if (userToken!=null && !userToken.equals("")){
			return JSON.parseObject(userToken, UserDTO.class);
		}
		return null;
	}


	public AdminDTO newGetCurrentAdmin(HttpServletRequest request){

		if (StringUtils.isEmpty(request.getHeader("Admin-Token")) || request.getHeader("Admin-Token").length()==0){
			return null;
		}
		String adminToken = stringRedisTemplate.opsForValue().get(request.getHeader("Admin-Token"));
		if (adminToken!=null && !adminToken.equals("")){
			return JSON.parseObject(adminToken,AdminDTO.class);
		}
		return null;
	}

	public Integer newGetCurrentAdminId(HttpServletRequest request){

		String adminToken = stringRedisTemplate.opsForValue().get(request.getHeader("Admin-Token"));
		if (adminToken!=null && !adminToken.equals("")){
			return JSON.parseObject(adminToken,AdminDTO.class).getId();
		}
		return null;
	}


}
