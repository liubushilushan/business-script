package com.liuapi.amap.api.driving.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeometryJson<T> implements Serializable {
    private static final long serialVersionUID = -1L;
    private GeometryType type;
    private List<T> coordinates;

    public enum GeometryType{
        LineString,Point,Polygon;
    }
}
