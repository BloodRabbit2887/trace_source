package qichen.code.model;

import java.util.HashMap;
import java.util.Map;

public class DeptTypeModel {

    public static final Integer DEPT_SALE = 1;//营销部
    public static final Integer DEPT_DESIGN = 2;//设计部
    public static final Integer DEPT_TECHNOLOGY = 3;//工艺科
    public static final Integer DEPT_QUALITY = 4;//质量管理部
    public static final Integer DEPT_WORK_ASSEMBLE = 5;//装配车间
    public static final Integer DEPT_VERIFY = 6;//质量管理部/检验科
    public static final Integer DEPT_AFTER_SALE = 7;//装配调试售后服务科


    public static Map<Integer,String> TYPE_MAP = new HashMap<>();
    static {
        TYPE_MAP.put(DEPT_SALE,"营销部");
        TYPE_MAP.put(DEPT_DESIGN,"设计部");
        TYPE_MAP.put(DEPT_TECHNOLOGY,"工艺科");
        TYPE_MAP.put(DEPT_QUALITY,"质量管理部");
        TYPE_MAP.put(DEPT_WORK_ASSEMBLE,"装配车间");
        TYPE_MAP.put(DEPT_VERIFY,"检验科");
        TYPE_MAP.put(DEPT_AFTER_SALE,"装配调试售后服务科");
    }


}
