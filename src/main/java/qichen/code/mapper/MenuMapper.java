package qichen.code.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;
import qichen.code.entity.Menu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 菜单表 Mapper 接口
 * </p>
 *
 * @author BloodRabbit
 * @since 2022-12-16
 */
public interface MenuMapper extends BaseMapper<Menu> {

    @Select({"${sql}"})
    @ResultType(ArrayList.class)
    List<Menu> listByAdminId(@Param("sql") String sql);
}
