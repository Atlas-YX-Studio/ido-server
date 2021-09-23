package com.bixin.ido.server.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimalUtil {

    public static String toPlainString(BigDecimal bigDecimal, Integer precision) {
        BigDecimal precisionFactor = getPrecisionFactor(precision);
        return bigDecimal.divide(precisionFactor, 18, RoundingMode.HALF_DOWN)
                .stripTrailingZeros().toPlainString();
    }

    public static BigDecimal getPrecisionFactor(Integer precision) {
        return BigDecimal.valueOf(Math.pow(10, precision));
    }

}
