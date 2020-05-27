package com.curtisnewbie.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ------------------------------------
 * <p>
 * Author: Yongjie Zhuang
 * <p>
 * ------------------------------------
 * <p>
 * Util class for Date related operations
 * </p>
 */
public class DateUtil {

    /**
     * Create a string of Date in this format: ddMMyyyy
     *
     * @return a string of Date in this format: ddMMyyyy
     */
    public static String getDateStr() {
        return new SimpleDateFormat("ddMMyyyy").format(new Date());
    }

    /**
     * Convert date to a string in this format: dd-MM-yyyy
     *
     * @return a string of Date in this format: dd-MM-yyyy
     */
    public static String toDateStr(Date date) {
        return new SimpleDateFormat("dd-MM-yyyy").format(date);
    }

}
