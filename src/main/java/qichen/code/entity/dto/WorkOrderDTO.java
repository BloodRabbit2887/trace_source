package qichen.code.entity.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import qichen.code.entity.WorkOrder;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class WorkOrderDTO extends WorkOrder {

    private String customName;//客户名
    private String submitName;//提交人名称
    private String modelTypeName;//模具类型名称
    private String electricName;//电机类型名称
    private String saleName;//业务员名称
    private String countStr;//列数名称
    private String deptName;

    public static final Integer STATUS_UN_FINISH = 1;
    public static final Integer STATUS_FINISH = 2;

    private List<SubmitTableOptionDTO> deviseOptions;//设计部表单项列表
    private String deviseImg1;//安装图和排样图涵盖气管及批量接线图（设计部）
    private String deviseImg2;//产品图（设计部）
    private String devisePdf1;//安装图和排样图涵盖气管及批量接线图（设计部）
    private String devisePdf2;//产品图（设计部）

    private List<SubmitTableOptionDTO> technologyOptions;//工艺部表单项列表

    private ModelCheckLogDTO modelCheckLog;//模具检测报告(质量管理部)
    private SparePartsLogDTO sparePartsLog;//零件检测报告(质量管理部)
    private QualityOrderFileDTO qualityOrderFile1;//1下模配磨检查记录(质量管理部)
    private QualityOrderFileDTO qualityOrderFile2;//2配磨导正钉检查记录(质量管理部)
    private QualityOrderFileDTO qualityOrderFile3;//3成型尺寸凸凹模检测记录(质量管理部)
    private QualityOrderFileDTO qualityOrderFile4;//4关键零部件特采放行记录(质量管理部)

    private List<AfterSaleOrderDTO> afterSaleOrders;//维修工单(装配调试售后服务科)

    private ModelInstallDTO modelInstall;

    private String deviseUserName;
    private LocalDateTime deviseCreateTime;

    private String tecUserName;
    private LocalDateTime teCreateTime;

    private String qualityUserName;
    private LocalDateTime qualityCreateTime;

    private String afterUserName;
    private LocalDateTime afterCreateTime;

    private Integer userId;
    private List<String> numbers;


}
