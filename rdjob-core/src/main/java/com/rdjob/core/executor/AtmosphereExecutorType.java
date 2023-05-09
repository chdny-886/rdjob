package com.rdjob.core.executor;

import lombok.Getter;

/**
 * @description:
 * @author: ChenDong
 * @time: 2022/4/21 16:11
 */
public enum AtmosphereExecutorType {
    QUARTZ(1),
    SPRING(2)
    ;

    @Getter
    private int code;

    AtmosphereExecutorType(int code) {
        this.code = code;
    }
}
