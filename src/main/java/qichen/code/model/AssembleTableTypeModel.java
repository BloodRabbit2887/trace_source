package qichen.code.model;

import lombok.Data;

@Data
public class AssembleTableTypeModel {

    public static final Integer TYPE_ALLOY = 1;//合金组装组扭转部位工作检查表
    public static final Integer TYPE_PACKAGE = 2;//合金组装组工作检查表
    public static final Integer TYPE_PLANK = 3;//模架组导槽板工作检查表
    public static final Integer TYPE_DOWN = 4;//模架组下模座垫板工作检查表
    public static final Integer TYPE_MODEL_PUSH = 5;//模具入库点检表
    public static final Integer TYPE_MOULE_BASE = 6;//模架组装组工作检查表
}
