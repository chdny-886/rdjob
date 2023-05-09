package com.rdjob.core.util;

import org.springblade.core.tool.utils.Holder;

import java.security.SecureRandom;
import java.util.Random;

/**
 * @description:
 * @author: ChenDong
 * @time: 2022/7/13 14:12
 */
public class RandomUtil {

    private final static SecureRandom random = Holder.SECURE_RANDOM;

    /**
     * 范围随机
     *
     * @param min
     * @param max
     * @return
     */
    public static Integer random(Integer min, Integer max) {
        int range = max - min;
        return min + random.nextInt(range);
    }

    public static Integer random(Integer bound) {
        return random.nextInt(bound);
    }

    public static Random getRandom() {
        return random;
    }
}
