package com.liuapi.amap.api;

import com.csvreader.CsvReader;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.liuapi.amap.api.driving.TracePlanApi;
import com.liuapi.amap.api.driving.model.GeometryJson;
import com.liuapi.amap.api.driving.model.TracePlanResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 */
@Slf4j
class TracePlanApiTest {
    @Test
    void testQuery() throws IOException, URISyntaxException {
        TracePlanResult tracePlanResult = TracePlanApi.queryForTracePlanResult(
                120.169013, 30.25474,
                120.15924, 30.18089);
        ObjectMapper objectMapper = new ObjectMapper();
        String result = objectMapper.writeValueAsString(tracePlanResult);
        System.out.println(result);
    }

    @Test
    void handleHistoryRecords() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String filePath = "C:\\Users\\Administrator\\Desktop\\批处理调度场站关系数据.csv";

        CsvReader csvReader = new CsvReader(filePath, ',', Charset.forName("UTF-8"));
        csvReader.readHeaders();
        Map<String, ParkTraceDO> maps = Maps.newHashMap();
        PrintWriter writer = new PrintWriter("C:\\Users\\Administrator\\Desktop\\批处理调度场站关系数据.sql");
        // 逐行解析csv文件
        while (csvReader.readRecord()) {
            int start_site_type = Integer.valueOf(csvReader.get("start_site_type"));
            int end_site_type = Integer.valueOf(csvReader.get("end_site_type"));
            if (start_site_type == 1 && end_site_type == 1) {
                continue;
            }
            long start_site_id = Long.valueOf(csvReader.get("start_site_id"));
            long end_site_id = Long.valueOf(csvReader.get("end_site_id"));
            // 转为bean
            String key = getKey(start_site_id, end_site_id, start_site_type, end_site_type);
            ParkTraceDO parkTraceDO = maps.computeIfAbsent(key, k -> new ParkTraceDO());
            String[] split = key.split("-");
            long ltId = Long.parseLong(split[0]);
            long gtId = Long.parseLong(split[1]);

            int mile = (int) (Double.valueOf(csvReader.get("mile")) * 1000);
            long runTime = (long) (Double.valueOf(csvReader.get("run_time")) * 60);

            String ltCoords = ltId == start_site_id ? csvReader.get("start_site_coord") : csvReader.get("end_site_coord");
            String gtCoords = ltId == start_site_id ? csvReader.get("end_site_coord") : csvReader.get("start_site_coord");
            int relType = Math.min(start_site_type, end_site_type);
            String ltName = ltId == start_site_id ? csvReader.get("start_site_name") : csvReader.get("end_site_name");
            String gtName = ltId == start_site_id ? csvReader.get("end_site_name") : csvReader.get("start_site_name");
            if (ltId == start_site_id) {
                parkTraceDO.setLtGtMileage(mile + "")
                        .setLtGtStatus(1)
                        .setLtGtDuring(runTime);
            } else {
                parkTraceDO.setGtLtMileage(mile + "")
                        .setGtLtDuring(runTime)
                        .setGtLtStatus(1);
            }
            parkTraceDO.setLtId(ltId).setGtId(gtId)
                    .setLtName(ltName).setGtName(gtName)
                    .setLtCoord(ltCoords)
                    .setGtCoord(gtCoords);
            parkTraceDO.setRelType(relType == 1 ? 1 : 2);
        }
        maps.values()
                .stream()
                .forEach(
                        parkTraceDO -> {
                            try {
                                GeometryJson<Double> ltCoord = objectMapper.readValue(parkTraceDO.getLtCoord(), GeometryJson.class);
                                GeometryJson<Double> gtCoord = objectMapper.readValue(parkTraceDO.getGtCoord(), GeometryJson.class);
                                // 请求高德接口
                                if (parkTraceDO.getLtGtStatus() == 1) {
                                    TracePlanResult ltGtResult = TracePlanApi.queryForTracePlanResult(
                                            ltCoord.getCoordinates().get(0), ltCoord.getCoordinates().get(1),
                                            gtCoord.getCoordinates().get(0), gtCoord.getCoordinates().get(1));
                                    String ltGtTrace = objectMapper.writeValueAsString(ltGtResult.getTrace());
                                    parkTraceDO.setLtGtTrace(ltGtTrace.replaceAll("\\\"", "\\\\\""));


                                    if (parkTraceDO.getLtGtMileage() == null) {
                                        parkTraceDO.setLtGtMileage(ltGtResult.getDistance());
                                    }
                                    if (parkTraceDO.getLtGtDuring() == null) {
                                        parkTraceDO.setLtGtDuring(Long.valueOf(ltGtResult.getDuration()));
                                    }
                                }
                                if (parkTraceDO.getGtLtStatus() == 1) {
                                    TracePlanResult gtLtResult = TracePlanApi.queryForTracePlanResult(
                                            gtCoord.getCoordinates().get(0), gtCoord.getCoordinates().get(1),
                                            ltCoord.getCoordinates().get(0), ltCoord.getCoordinates().get(1));
                                    String gtLtTrace = objectMapper.writeValueAsString(gtLtResult.getTrace());
                                    parkTraceDO.setGtLtTrace(gtLtTrace.replaceAll("\\\"", "\\\\\""));
                                    if (parkTraceDO.getGtLtMileage() == null) {
                                        parkTraceDO.setGtLtMileage(gtLtResult.getDistance());
                                    }
                                    if (parkTraceDO.getGtLtDuring() == null) {
                                        parkTraceDO.setGtLtDuring(Long.valueOf(gtLtResult.getDuration()));
                                    }
                                }
                                parkTraceDO.setLtCoord(parkTraceDO.getLtCoord().replaceAll("\\\"", "\\\\\""))
                                        .setGtCoord(parkTraceDO.getGtCoord().replaceAll("\\\"", "\\\\\""));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                );

        writer.println("truncate `park_trace`;");
        writer.println("truncate `park_trace_idx`;");

        Collection<ParkTraceDO> dos = maps.values();
        AtomicInteger idGenerator = new AtomicInteger(300);
        dos.stream()
                .forEach(
                        parkTraceDO -> {
                            int id = idGenerator.incrementAndGet();
                            parkTraceDO.setId((long) id);
                            String sql = "INSERT INTO `park_trace`" +
                                    "(`id`, `lt_id`, `lt_name`, `lt_coord`, `gt_id`, `gt_name`, `gt_coord`," +
                                    "`lt_gt_mileage`, `lt_gt_during`, `lt_gt_trace`, `lt_gt_status`, " +
                                    "`gt_lt_mileage`, `gt_lt_during`, `gt_lt_trace`, `gt_lt_status`, " +
                                    "`merchant_id`, `modify_time`, `create_time`, `rel_type`) " +
                                    "VALUES " +
                                    "(" + id + ", " + parkTraceDO.getLtId() + ",\"" + parkTraceDO.getLtName()
                                    + "\", \"" + parkTraceDO.getLtCoord() + "\",\"" + parkTraceDO.getGtId() + "\",\"" +
                                    parkTraceDO.getGtName() + "\",\"" + parkTraceDO.getGtCoord() + "\",\"" +
                                    "" + parkTraceDO.getLtGtMileage() + "\",\"" + parkTraceDO.getLtGtDuring() + "\",\"" + parkTraceDO.getLtGtTrace() + "\","+parkTraceDO.getLtGtStatus()+"," +
                                    "\"" + parkTraceDO.getGtLtMileage() + "\",\"" + parkTraceDO.getGtLtDuring() + "\",\"" + parkTraceDO.getGtLtTrace() + "\", "+parkTraceDO.getGtLtStatus()+"," +
                                    "'33010001', now(), now(), " + parkTraceDO.getRelType() + ");";
                            writer.println(sql);
                        }
                );
        // 迭代查询高德轨迹
        csvReader.close();
        writer.flush();
        dos.stream()
                .filter(parkTraceDO -> parkTraceDO.getRelType() == 2)
                .forEach(
                        parkTraceDO -> {
                            if(parkTraceDO.getLtGtStatus()==1){
                                // insert park_trace_idx表
                                String sql = ("INSERT INTO `park_trace_idx` " +
                                        "(`id`, `park_trace_id`, `park_id`, `merchant_id`, `modify_time`, `create_time`)" +
                                        " VALUES (null, " + parkTraceDO.getId() + ", " + parkTraceDO.getLtId() + ", '33010001', now(), now());");
                                writer.println(sql);
                            }
                            if(parkTraceDO.getGtLtStatus()==1){
                                String sql = ("INSERT INTO `park_trace_idx` " +
                                        "(`id`, `park_trace_id`, `park_id`, `merchant_id`, `modify_time`, `create_time`)" +
                                        " VALUES (null, " + parkTraceDO.getId() + ", " + parkTraceDO.getGtId() + ", '33010001', now(), now());");
                                writer.println(sql);
                            }
                        }
                );
        writer.println("update `park_trace` set `lt_gt_mileage` = 0,`lt_gt_trace`=null,`lt_gt_during`=0 where `lt_gt_status`=0;");
        writer.println("update `park_trace` set `gt_lt_mileage` = 0,`gt_lt_trace`=null,`gt_lt_during`=0 where `gt_lt_status`=0;");
        writer.flush();
        writer.close();
        System.out.println("----------------JOB FINISH!--------------------");
    }

    private String getKey(long start_site_id, long end_site_id, int start_site_type, int end_site_type) {
        // 定义一下顺序
        if (start_site_type == 2 && end_site_type == 2) {
            // 都是场站
            return Math.min(start_site_id, end_site_id) + "-" + Math.max(start_site_id, end_site_id);
        }
        if (start_site_type == 2) {
            // 第一个是场站
            return start_site_id + "-" + end_site_id;
        } else {
            // 第二个是场站
            return end_site_id + "-" + start_site_id;
        }
    }

    @Test
    void testReplace() {
        String value = "{\"type\":\"Point\"}";
        // {"type":"Point"}
        // {\"type\":\"Point\"}
        System.out.println(value);
        System.out.println(value.replaceAll("\\\"", "\\\\\""));

    }


    @Test
    void testMathOperation() {
        System.out.println((int) (Double.valueOf("0.02") * 60));
        System.out.println((int) (Double.valueOf("0.002") * 60));
        System.out.println((int) (Double.valueOf("0.002") * 60));
    }
}