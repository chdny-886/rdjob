package com.rdjob.core.config;

import lombok.Data;

import java.io.Serializable;

/**
 * @description:
 * @author: ChenDong
 * @time: 2022/4/19 17:11
 */
@Data
public class GlobalJobConfig implements Serializable {

    private int rangeMin;
    private int rangeMax;
    private int countMin;
    private int countMax;
    private boolean debug = false;
    private boolean pushData = true;
}
