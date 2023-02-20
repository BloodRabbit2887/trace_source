package qichen.code.model;

import lombok.Data;

import java.math.BigInteger;

@Data
public class HomeModel {

    private BigInteger finishCount;

    private BigInteger unFinishCount;

    private BigInteger afterSaleCount;

    private BigInteger partsCount;


}
