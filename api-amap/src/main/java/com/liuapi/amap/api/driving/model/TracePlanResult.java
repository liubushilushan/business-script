package com.liuapi.amap.api.driving.model;

import lombok.Data;
@Data
public class TracePlanResult {
    private GeometryJson trace;
    private String duration;
    private String distance;
}
