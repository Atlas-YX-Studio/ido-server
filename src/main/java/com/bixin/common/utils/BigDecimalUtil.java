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
        return number.multiply(BigDecimalUtil.getPrecisionFactor(precision));
    }


    // 除法,默认精度
    public static BigDecimal div(BigDecimal v1, BigDecimal v2) {
        return div(v1, v2, 18);
    }

    // 除法,自定义精度
    public static BigDecimal div(BigDecimal v1, BigDecimal v2, int scale) {
        if (scale < 0) {
            new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        return v1.divide(v2, scale, BigDecimal.ROUND_DOWN);
    }

}
