package qichen.code.model;


import lombok.Data;
import qichen.code.entity.dto.*;

import java.util.List;

@Data
public class WorkOrderModel {

    private WorkOrderDTO workOrderDTO;

    private DeviseModel deviseOrder;//设计部

    private List<SubmitTableOptionDTO> technologyOptions;//工艺部表单项列表

    private List<AfterSaleOrderDTO> afterSaleOrders;//维修工单(装配调试售后服务科)

    private QualityModel qualityModel;

    private ModelInstallDTO modelInstall;

    @Data
    public static class DeviseModel{
        private List<SubmitTableOptionDTO> deviseOptions;//设计部表单项列表
        private String deviseImg1;//安装图和排样图涵盖气管及批量接线图（设计部）
        private String deviseImg2;//产品图（设计部）
        private String devisePdf1;//安装图和排样图涵盖气管及批量接线图（设计部）
        private String devisePdf2;//产品图（设计部）
    }

    @Data
    public static class QualityModel{
        private ModelCheckLogDTO modelCheckLog;//模具检测报告(质量管理部)
        private SparePartsLogDTO sparePartsLog;//零件检测报告(质量管理部)
        private QualityOrderFileDTO qualityOrderFile1;//1下模配磨检查记录(质量管理部)
        private QualityOrderFileDTO qualityOrderFile2;//2配磨导正钉检查记录(质量管理部)
        private QualityOrderFileDTO qualityOrderFile3;//3成型尺寸凸凹模检测记录(质量管理部)
        private QualityOrderFileDTO qualityOrderFile4;//4关键零部件特采放行记录(质量管理部)
    }
}
