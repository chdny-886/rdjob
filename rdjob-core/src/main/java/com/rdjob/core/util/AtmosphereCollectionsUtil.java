package com.rdjob.core.util;

import org.springblade.core.tool.utils.Func;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.easyya.atmosphere.x.lab.executor.job.inquiry.InquiryRule.AWS_ASIN_HOST_PLACEHOLDER;

/**
 * @description:
 * @author: ChenDong
 * @time: 2022/4/20 14:14
 */
public class AtmosphereCollectionsUtil {

    public static List<String> splitToList(String str) {
        return splitToList(str, "|");
    }
    /**
     * 将某个字符串切割后转换list
     * @param str
     * @param delimiter
     * @return
     */
    public static List<String> splitToList(String str, String delimiter) {
        String[] arrays = Func.splitTrim(str, delimiter);
        List<String> list = new ArrayList<>(arrays.length);
        Collections.addAll(list, arrays);
        return list;
    }

    /**
     * 获取第五级的bsr
     *
     * @param bsr
     * @return
     */
    public static String lastBsr(String bsr) {
        String[] arrays = Func.split(bsr, "-");
        if (arrays.length == 0) {
            return "";
        }
        if (arrays.length > 5) {
            //大于5取第5个
            return arrays[4];
        } else {
            //否则取最后一个
            return arrays[arrays.length - 1];
        }
    }

    public static String getAsinHost(String site, String asin) {
        if (StringUtils.isEmpty(site) || site.equals("us")) {
            site = "com";
        }
        return MessageFormat.format(AWS_ASIN_HOST_PLACEHOLDER, site, asin);
    }

}
