package com.liuapi.amap.api;

import com.liuapi.amap.api.driving.model.GeometryJson;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;


@Data
@Accessors(chain = true)
public class ParkTraceDO {
    private Long id;
    private Long ltId;
    private String ltName;
    private String ltCoord;
    private Long gtId;
    private String gtName;
    private String gtCoord;
    private String ltGtMileage;
    private Long ltGtDuring;
    private String ltGtTrace;
    private int ltGtStatus;
    private String gtLtMileage;
    private Long gtLtDuring;
    private String gtLtTrace;
    private int gtLtStatus;
    private String merchantId;
    private Date createTime;
    private Date modifyTime;
    private Integer relType;
    private Long idxId;
}
