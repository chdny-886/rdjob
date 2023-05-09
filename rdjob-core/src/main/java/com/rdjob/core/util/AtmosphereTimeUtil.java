package com.rdjob.core.util;

import lombok.extern.slf4j.Slf4j;
import org.springblade.core.tool.utils.DateTimeUtil;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.core.tool.utils.Func;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Date;

/**
 * @description:
 * @author: ChenDong
 * @time: 2022/4/18 20:21
 */
@Slf4j
public class AtmosphereTimeUtil {

    /**
     * 计算下次间隔时间
     * @param executorStartTime
     * @param executorEndTime
     * @param preIntervalSeconds
     * @return
     */
    public static Date nextIntervalTime(LocalTime executorStartTime, LocalTime executorEndTime, int preIntervalSeconds, String describe) {
        log.info("{} preIntervalSeconds: {}", describe, preIntervalSeconds);
        LocalDateTime nowTime = LocalDateTime.now();
        //LocalDateTime nowTime = LocalDateTime.of(2022,5,10,13,33,2);
        //预计下次的执行时间
        Date date = DateTimeUtil.toDate(nowTime.plusSeconds(preIntervalSeconds));
        //今天任务执行结束时间
        Date todayExecutorEndDateTime = DateTimeUtil.toDate(LocalDateTime.of(nowTime.toLocalDate(), executorEndTime));
        //在18:00之前,返回随机的下次任务执行时间
        if (date.before(todayExecutorEndDateTime)) {
            return date;
        }

        //明天任务开始时间,即明天9:00 + preIntervalSeconds
        return DateTimeUtil.toDate(LocalDateTime.of(nowTime.plusDays(1).toLocalDate(), executorStartTime).plusSeconds(preIntervalSeconds));
    }

    public static LocalDateTime randomDateTime(LocalTime startTime, LocalTime endTime) {
        return randomDateTime(LocalDate.now(), startTime, endTime);
    }
    public static LocalDateTime randomDateTime(LocalDate localDate, LocalTime startTime, LocalTime endTime) {
        LocalDateTime startDateTime = LocalDateTime.of(localDate, startTime);
        LocalDateTime endDateTime = LocalDateTime.of(localDate, endTime);

        int startSeconds = Func.toInt(startDateTime.toEpochSecond(ZoneOffset.of("+8")));
        int endSeconds = Func.toInt(endDateTime.toEpochSecond(ZoneOffset.of("+8")));
        long random = Func.toLong(RandomUtil.random(startSeconds, endSeconds))  * 1000;
        return DateUtil.fromDate(new Date(random));
    }

    //public static void main(String[] args) {
    //    int max = 16200;
    //    int min = 10800;
    //    Random random = new Random();
    //    int range = max - min;
    //    int intervalSeconds = min + random.nextInt(range);
    //
    //    LocalTime executorStartTime = LocalTime.of(9,0,0);
    //    LocalTime executorEndTime = LocalTime.of(18,0,0);
    //    int nextIntervalTime = AtmosphereTimeUtil.nextIntervalTime(executorStartTime, executorEndTime, intervalSeconds);
    //    System.out.println(nextIntervalTime);
    //}
}
