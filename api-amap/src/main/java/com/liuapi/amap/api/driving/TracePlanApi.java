package com.liuapi.amap.api.driving;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.liuapi.amap.api.driving.model.AMapDrivingResult;
import com.liuapi.amap.api.driving.model.GeometryJson;
import com.liuapi.amap.api.driving.model.TracePlanResult;
import com.liuapi.http.httpclient4.HttpClients;
import com.liuapi.property.PropertyReader;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class TracePlanApi {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static final String ROUTETRACE_PLANNING_URL ="http://restapi.amap.com/v3/direction/driving";
    private static final String ORIGIN = "origin";
    private static final String DESTINATION = "destination";
    private static final String KEY = "key";
    private static final String STRATEGY = "strategy";
    private static final String AMAP_KEY = PropertyReader.get("amap.key");

    /*驾车策略*/
    private static final String STRATEGY_PLAN = "6";


    private static String invokeApi(double aLng,double alat,double blng,double blat) throws IOException, URISyntaxException {
        Map<String, String> map = new HashMap<>(16);
        String url = ROUTETRACE_PLANNING_URL;
        /*规划每一个站点到下一个站点的距离*/
        /*起点*/
        map.put(ORIGIN, aLng + "," + alat);
        /*终点*/
        map.put(DESTINATION, blng + "," + blat);
        /*高德key*/
        map.put(KEY, AMAP_KEY);
        /*规划策略*/
        map.put(STRATEGY, STRATEGY_PLAN);
        /*访问高德URL获取数据*/
        return HttpClients.doGet(url,map,null);
    }

    public static TracePlanResult queryForTracePlanResult(double aLng,double alat,double blng,double blat) throws IOException, URISyntaxException {
        String str = invokeApi(aLng, alat, blng, blat);

        TracePlanResult tracePlanResult = new TracePlanResult();
        AMapDrivingResult result = objectMapper.readValue(str, AMapDrivingResult.class);
        if(Objects.equals("1",result.getStatus())&&result.getRoute()!=null&&result.getRoute().getPaths().size()>0){
            AMapDrivingResult.Route.Path path = result.getRoute().getPaths().get(0);
            List<AMapDrivingResult.Route.Path.Step> steps = path.getSteps();
            String trace = steps.stream()
                    .map(AMapDrivingResult.Route.Path.Step::getPolyline)
                    .collect(Collectors.joining(";"));

            List<Double> origin = Lists.newArrayList(aLng,alat);
            List<Double> destination = Lists.newArrayList(blng,blat);

            /*包含上一个站点的经纬度和下一个站点的经纬*/
            LinkedList<List<Double>> stopTrance = parseTrace(trace);
            stopTrance.addFirst(origin);
            stopTrance.addLast(destination);

            GeometryJson geometryJson = new GeometryJson();
            geometryJson.setCoordinates(stopTrance);
            geometryJson.setType(GeometryJson.GeometryType.LineString);

            tracePlanResult.setTrace(geometryJson);
            tracePlanResult.setDuration(path.getDuration());
            tracePlanResult.setDistance(path.getDistance());
        }
        return tracePlanResult;
    }

    private static LinkedList<List<Double>> parseTrace(String trace) {
        LinkedList<List<Double>> traceList = Lists.newLinkedList();
        String[] stopTraceArray = trace.split(";");
        int length = stopTraceArray.length;
        for (int j = 0; j <= length - 1; j++) {
            LinkedList<Double> points = new LinkedList<>();
            String[] point = stopTraceArray[j].split(",");
            points.add(Double.valueOf(point[0]));
            points.add(Double.valueOf(point[1]));
            traceList.add(points);
        }
        return traceList;
    }


}
