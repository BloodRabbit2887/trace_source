package qichen.code.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import qichen.code.entity.dto.TableOptionDTO;
import qichen.code.exception.BusinessException;
import qichen.code.exception.ResException;
import qichen.code.model.Filter;
import qichen.code.model.ResponseBean;
import qichen.code.service.ITableOptionsService;

import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 表单选项表 前端控制器
 * </p>
 *
 * @author BloodRabbit
 * @since 2023-01-11
 */
@Slf4j
@RestController
@RequestMapping("/code/table-options")
public class TableOptionsController {

    @Autowired
    private ITableOptionsService tableOptionsService;

    @ResponseBody
    @GetMapping("/query")
    public ResponseBean query(@RequestParam(value = "type",required = false) Integer type,
                              @RequestParam(value = "tableType",required = false) Integer tableType,
                              @RequestParam(value = "level",required = false) Integer level,
                              @RequestParam(value = "must",required = false) Integer must,
                              @RequestParam(value = "status", required = false) Integer status,
                              @RequestParam(value = "page", defaultValue = "1") Integer page,
                              @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                              @RequestParam(value = "createTimeBegin", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date createTimeBegin,
                              @RequestParam(value = "createTimeEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date createTimeEnd,
                              @RequestParam(value = "orders", defaultValue = "CreateTime") String orders,
                              @RequestParam(value = "orderBy", defaultValue = "false") Boolean orderBy,
                              @RequestParam(value = "keyword", required = false) String keyword) {

        TableOptionDTO dto = new TableOptionDTO();
        dto.setType(type);
        dto.setTableType(tableType);
        dto.setLevel(level);
        dto.setMust(must);
        dto.setStatus(status);

        Filter filter = new Filter();
        filter.setKeyword(keyword);
        filter.setCreateTimeBegin(createTimeBegin);
        filter.setCreateTimeEnd(createTimeEnd);
        filter.setOrders(orders);
        filter.setOrderBy(orderBy);
        filter.setPage(page);
        filter.setPageSize(pageSize);
        Map<String, Object> res = new HashMap<>();

        try {
            List<TableOptionDTO> list =tableOptionsService.listByFilter(dto, filter);
            BigInteger count =tableOptionsService.listCount(dto, filter);
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
            TableOptionDTO dto = tableOptionsService.getDetail(id);
            return new ResponseBean(dto);
        } catch (BusinessException exception) {
            return new ResponseBean(exception);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }

}

