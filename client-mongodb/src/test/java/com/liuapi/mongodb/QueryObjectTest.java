package com.liuapi.mongodb;

import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class QueryObjectTest {

    @Test
    void testGetQueryObject() {
        long id = 3301000200112653L;
        Criteria criteria = new Criteria();
        Criteria criteria1 = Criteria.where("upLineForm.routeStopDomains").elemMatch(Criteria.where("stopId").is(id));
        Criteria criteria2 = Criteria.where("downLineForm.routeStopDomains").elemMatch(Criteria.where("stopId").is(id));
        criteria.orOperator(criteria1, criteria2);
        Query query = new Query(criteria);
        System.out.println(query.getQueryObject());
    }

    /**
     * 备注：在springbootData中可行的$date查询在mongodbCli中无法使用
     * mongodb CLI:
     * db.getCollection('route_draft_info').find({"releaseTime":{ "$gte": ISODate("2019-12-31T16:00:00.000Z")}})
     * db.getCollection('route_draft_info').find({"releaseTime":{ "$gte": new Date("2019-12-31T16:00:00.000Z")}})
     * <p>
     * Springboot Data:
     * { "releaseTime" : { "$gt" : { "$date" : "2019-12-31T16:00:00.000Z"}}}
     */
    @Test
    void testGetDateCompare() {
        Date from = Date.from(
                LocalDateTime.of(2020, 1, 1, 0, 0)
                        .atZone(ZoneId.systemDefault())
                        .toInstant());
        Criteria criteria = Criteria.where("releaseTime").gt(from);
        Query query = new Query(criteria);
        System.out.println(query.getQueryObject());
    }


}
