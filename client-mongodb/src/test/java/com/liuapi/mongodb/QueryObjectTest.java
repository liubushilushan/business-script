package com.liuapi.mongodb;

import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class QueryObjectTest {
    @Test
    void testGetQueryObject(){
        String  id= "3301000200112653";
        Criteria criteria = new Criteria();
        Criteria criteria1 = Criteria.where("upLineForm.routeStopDomains").elemMatch(Criteria.where("stopId").is(id));
        Criteria criteria2 = Criteria.where("downLineForm.routeStopDomains").elemMatch(Criteria.where("stopId").is(id));
        criteria.orOperator(criteria1,criteria2);
        Query query = new Query(criteria);
        System.out.println(query.getQueryObject());
    }
}
