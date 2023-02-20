package qichen.code.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AssembleTableTypeModel {

    public static final Integer TYPE_ALLOY = 5;//合金组装组扭转部位工作检查表5
    public static final Integer TYPE_PACKAGE = 4;//合金组装组工作检查表4 *
    public static final Integer TYPE_PLANK = 3;//模架组导槽板工作检查表3
    public static final Integer TYPE_DOWN = 2;//模架组下模座垫板工作检查表2
    public static final Integer TYPE_MODEL_PUSH = 6;//模具入库点检表6 *
    public static final Integer TYPE_MOULE_BASE = 1;//模架组装组工作检查表1 *

    public static List<Integer> PROJECT_TYPES = new ArrayList<>();

    static {
        PROJECT_TYPES.add(TYPE_MOULE_BASE);
        PROJECT_TYPES.add(TYPE_PACKAGE);
        PROJECT_TYPES.add(TYPE_MODEL_PUSH);
    }

}
