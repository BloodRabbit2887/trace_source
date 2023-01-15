package qichen.code.entity.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import qichen.code.entity.AfterSaleOrder;

@EqualsAndHashCode(callSuper = true)
@Data
public class AfterSaleOrderDTO extends AfterSaleOrder {

    private String partsName;

}
