package com.iecube.iecubetutorial.model_user.points.vo;

import com.iecube.iecubetutorial.model_user.points.entity.PointsRecord;
import lombok.Data;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Data
public class YearMonthConsumptionResponse {

    private Map<Integer, Map<Integer, List<PointRecordVo>>> yearlyData = new TreeMap<>(Comparator.reverseOrder());

    public void addRecord(PointRecordVo record) {
        // 使用Instant的atZone方法转换为ZonedDateTime，然后获取年和月
        ZonedDateTime zonedDateTime = record.getCreateTime().atZone(ZoneId.systemDefault());
        int year = zonedDateTime.getYear();
        int month = zonedDateTime.getMonthValue();

        // 初始化年份和月份的嵌套Map结构
        yearlyData.computeIfAbsent(year, k -> new TreeMap<>(Comparator.reverseOrder()))
                .computeIfAbsent(month, k -> new ArrayList<>())
                .add(record);
    }

    // getters and setters
}