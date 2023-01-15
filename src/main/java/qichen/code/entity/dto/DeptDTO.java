package qichen.code.entity.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import qichen.code.entity.Dept;

@EqualsAndHashCode(callSuper = true)
@Data
public class DeptDTO extends Dept {

    public static final Integer TYPE_WORK_ORDER = 1;//营销部
    public static final Integer TYPE_SALE_ORDER = 2;//设计部
    public static final Integer TYPE_TECHNOLOGY_ORDER = 3;//工艺部
}
