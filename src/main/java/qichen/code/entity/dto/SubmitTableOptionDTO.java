package qichen.code.entity.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import qichen.code.entity.SubmitTableOptions;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class SubmitTableOptionDTO extends SubmitTableOptions {

    private String title;

    private Integer type;

    private Integer level;

    private Integer must;

    private String submitOptionName;

    private List<Integer> tableTypes;

}
