package qichen.code.entity.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import qichen.code.entity.TableOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class TableOptionDTO extends TableOptions {

    private List<Integer> tableTypes;

    public static final Integer TYPE_TABLE_DEVISE = 1;
    public static final Integer TYPE_TABLE_SPARE_LOG = 2;
    public static final Integer TYPE_TABLE_TECHNOLOGY = 3;
    public static final Integer TYPE_MODEL_CHECK_MODEL = 4;
    public static final Integer TYPE_MODEL_CHECK_OTHER = 5;
    public static final Integer TYPE_MODEL_CHECK_MEANS = 6;

    public static final String ITEM_MODEL_CHECK_MODEL = "模具参数";
    public static final String ITEM_MODEL_CHECK_OTHER = "其他";
    public static final String ITEM_MODEL_CHECK_MEANS = "随模资料";

    public static Map<Integer,String> MODEL_CHECK_MAP = new HashMap<>();


    public static Map<Integer,String> TABLE_TYPE_MAP = new HashMap<>();
    static {
        TABLE_TYPE_MAP.put(TYPE_TABLE_DEVISE,"设计部工单");
        TABLE_TYPE_MAP.put(TYPE_TABLE_SPARE_LOG,"零件检测报告");
        TABLE_TYPE_MAP.put(TYPE_TABLE_TECHNOLOGY,"工艺科工单");
        TABLE_TYPE_MAP.put(TYPE_MODEL_CHECK_MODEL,"模具报告单-模具参数");
        TABLE_TYPE_MAP.put(TYPE_MODEL_CHECK_OTHER,"模具报告单-其他");
        TABLE_TYPE_MAP.put(TYPE_MODEL_CHECK_MEANS,"模具报告单-随模资料");

        MODEL_CHECK_MAP.put(TYPE_MODEL_CHECK_MODEL,ITEM_MODEL_CHECK_MODEL);
        MODEL_CHECK_MAP.put(TYPE_MODEL_CHECK_OTHER,ITEM_MODEL_CHECK_OTHER);
        MODEL_CHECK_MAP.put(TYPE_MODEL_CHECK_MEANS,ITEM_MODEL_CHECK_MEANS);
    }

}
