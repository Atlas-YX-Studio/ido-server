package com.bixin.common.utils;

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

    public static BigDecimal removePrecision(BigDecimal number, Integer precision) {
        if (number == null) {
            return BigDecimal.ZERO;
        }
        if (precision == null || precision == 0) {
            return BigDecimal.ZERO;
        }
        return number.divide(BigDecimalUtil.getPrecisionFactor(precision), 18, RoundingMode.HALF_UP);
    }

    public static BigDecimal addPrecision(BigDecimal number, Integer precision) {
        if (number == null) {
            return BigDecimal.ZERO;
        }
        if (precision == null || precision == 0) {
            return BigDecimal.ZERO;
        }
        return number.subtract(BigDecimalUtil.getPrecisionFactor(precision));
    }

}
