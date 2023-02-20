package qichen.code.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class TableTypeModel {

    public static final Integer TABLE_ONE = 1;
    public static final Integer TABLE_TWO = 2;
    public static final Integer TABLE_THREE = 3;

    public static Map<Integer,String> SALE_MAP = new HashMap<>();//营销部表单
    static {
        SALE_MAP.put(TABLE_ONE,"营销部工单");
    }

    public static Map<Integer,String> DESIGN_MAP = new HashMap<>();//设计部表单
    static {
        DESIGN_MAP.put(TABLE_ONE,"设计部工单");
    }

    public static Map<Integer,String> TECHNOLOGY_MAP = new HashMap<>();//工艺科表单
    static {
        TECHNOLOGY_MAP.put(TABLE_ONE,"工艺科工单");
    }

    public static Map<Integer,String> QUALITY_MAP = new HashMap<>();//质量管理部表单
    static {
        QUALITY_MAP.put(TABLE_ONE,"质量管理部工单");
        QUALITY_MAP.put(TABLE_THREE,"零件检测报告单");
        QUALITY_MAP.put(TABLE_TWO,"模具检测报告单");
    }

    public static Map<Integer,String> ASSEMBLE_MAP = new HashMap<>();//装配车间表单
    static {
        ASSEMBLE_MAP.put(AssembleTableTypeModel.TYPE_MOULE_BASE,"模架组装组工作检查表");
        ASSEMBLE_MAP.put(AssembleTableTypeModel.TYPE_DOWN,"模架组下模座垫板工作检查表");
        ASSEMBLE_MAP.put(AssembleTableTypeModel.TYPE_PLANK,"模架组导槽板工作检查表");
        ASSEMBLE_MAP.put(AssembleTableTypeModel.TYPE_PACKAGE,"合金组装组工作检查表");
        ASSEMBLE_MAP.put(AssembleTableTypeModel.TYPE_ALLOY,"合金组装组扭转部位工作检查表");
        ASSEMBLE_MAP.put(AssembleTableTypeModel.TYPE_MODEL_PUSH,"模具入库点检表");

    }


    public static Map<Integer,Map<Integer,String>> TYPE_MAP = new HashMap<>();

    static {
        TYPE_MAP.put(DeptTypeModel.DEPT_SALE,SALE_MAP);
        TYPE_MAP.put(DeptTypeModel.DEPT_DESIGN,DESIGN_MAP);
        TYPE_MAP.put(DeptTypeModel.DEPT_TECHNOLOGY,TECHNOLOGY_MAP);
        TYPE_MAP.put(DeptTypeModel.DEPT_QUALITY,QUALITY_MAP);
        TYPE_MAP.put(DeptTypeModel.DEPT_WORK_ASSEMBLE,ASSEMBLE_MAP);
    }


/*
    public static List<TableTypeDTO> TABLE_TYPE_DESIGN = new ArrayList<>();
    static {
        TableTypeDTO dto = new TableTypeDTO();
        dto.setTableType(TABLE_ONE);
        dto.setTableName("设计部工单");
        TABLE_TYPE_DESIGN.add(dto);
    }

    public static List<TableTypeDTO> TABLE_TYPE_TECHNOLOGY = new ArrayList<>();
    static {
        TableTypeDTO dto = new TableTypeDTO();
        dto.setTableType(TABLE_ONE);
        dto.setTableName("工艺科工单");
        TABLE_TYPE_TECHNOLOGY.add(dto);
    }

    public static List<TableTypeDTO> TABLE_TYPE_QUALITY = new ArrayList<>();
    static {
        TableTypeDTO dto = new TableTypeDTO();
        dto.setTableType(TABLE_ONE);
        dto.setTableName("质量管理部工单");
        TABLE_TYPE_QUALITY.add(dto);

        TableTypeDTO dto1= new TableTypeDTO();
        dto1.setTableType(TABLE_TWO);
        dto1.setTableName("模具检测报告单");
        TABLE_TYPE_QUALITY.add(dto1);

        TableTypeDTO dto2 = new TableTypeDTO();
        dto2.setTableType(TABLE_THREE);
        dto2.setTableName("零件检测报告单");
        TABLE_TYPE_QUALITY.add(dto2);
    }
*/

}
