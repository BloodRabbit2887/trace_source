package qichen.code.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import qichen.code.entity.dto.CustomDTO;
import qichen.code.exception.BusinessException;
import qichen.code.exception.ResException;
import qichen.code.model.Filter;
import qichen.code.model.ResponseBean;
import qichen.code.service.ICustomService;

import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 客户管理表 前端控制器
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-12-16
 */
@Slf4j
@RestController
@RequestMapping("/code/custom")
public class CustomController {

    @Autowired
    private ICustomService customService;

    @ResponseBody
    @GetMapping("/query")
    public ResponseBean query(@RequestParam(value = "page", defaultValue = "1") Integer page,
                              @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                              @RequestParam(value = "createTimeBegin", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date createTimeBegin,
                              @RequestParam(value = "createTimeEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date createTimeEnd,
                              @RequestParam(value = "orders", defaultValue = "createTime") String orders,
                              @RequestParam(value = "orderBy", defaultValue = "false") Boolean orderBy,
                              @RequestParam(value = "keyword", required = false) String keyword){

        CustomDTO customDTO = new CustomDTO();
        Filter filter = new Filter();
        filter.setPage(page);
        filter.setPageSize(pageSize);
        filter.setKeyword(keyword);
        filter.setOrders(orders);
        filter.setOrderBy(orderBy);
        filter.setCreateTimeBegin(createTimeBegin);
        filter.setCreateTimeEnd(createTimeEnd);

        Map<String, Object> res = new HashMap<>();

        try {
            List<CustomDTO> list = customService.listByFilter(customDTO,filter);
            BigInteger count = customService.listCount(customDTO,filter);
            res.put("list", list);
            res.put("count", count);
            return new ResponseBean(res);
        } catch (BusinessException exception) {
            return new ResponseBean(exception);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }

    @ResponseBody
    @GetMapping("/detail")
    public ResponseBean detail(@RequestParam(value = "id") Integer id){
        try {
            CustomDTO custom = customService.getDetail(id);
            return new ResponseBean(custom);
        } catch (BusinessException exception) {
            return new ResponseBean(exception);
        } catch (Exception exception) {
            exception.printStackTrace();
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }

}

