package com.liuapi.mongodb;

import com.liuapi.mongodb.model.DraftBrief;
import com.liuapi.property.PropertyReader;
import com.mongodb.MongoClientURI;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class MongoClientTest {
    @Test
    void ping() throws UnknownHostException {
        Date from = Date.from(
                LocalDateTime.of(2020, 1, 1, 0, 0)
                        .atZone(ZoneId.systemDefault())
                        .toInstant());
        Criteria criteria = Criteria.where("releaseTime").gt(from);
        Query query = new Query(criteria);

        String mongodbUrl = PropertyReader.get("mongodb.url");
        SimpleMongoDbFactory yourdb = new SimpleMongoDbFactory(new MongoClientURI(mongodbUrl));
        MongoTemplate mongoTemplate = new MongoTemplate(yourdb);

        List<DraftBrief> drafts = mongoTemplate.find(query, DraftBrief.class, "route_draft_info");
        System.out.println(drafts.size());
    }
}
