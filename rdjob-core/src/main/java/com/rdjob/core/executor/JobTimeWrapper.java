package com.rdjob.core.executor;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @description:
 * @author: ChenDong
 * @time: 2022/5/13 15:33
 */
@Data
@Builder
public class JobTimeWrapper implements Serializable {

    private Integer nextTimeInterval;

    private Date nextJobTime;
}
