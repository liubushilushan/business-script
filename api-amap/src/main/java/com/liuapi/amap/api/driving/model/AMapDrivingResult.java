package com.liuapi.amap.api.driving.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AMapDrivingResult {
    private String status;
    private String info;
    private String infocode;
    private String count;
    private Route route;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Route{
        private String origin;
        private String destination;
        private String taxi_cost;
        private List<Path> paths;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Path{
            private String distance;
            private String duration;
            private String strategy;
            private String tolls;
            private String toll_distance;
            private List<Step> steps;
            private String restriction;
            private String traffic_lights;

            @Data
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class Step{
                private String instruction;
                private String orientation;
                private String road;
                private String distance;
                private String tolls;
                private String toll_distance;
                private String duration;
                private String polyline;
            }
        }
    }


}
