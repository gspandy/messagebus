/**
 * (C) Copyright 2016 Ymatou (http://www.ymatou.com/).
 *
 * All rights reserved.
 */
package com.ymatou.messagebus.facade.rest;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.ymatou.messagebus.facade.PublishKafkaFacade;
import com.ymatou.messagebus.facade.model.PublishMessageReq;
import com.ymatou.messagebus.facade.model.PublishMessageResp;
import com.ymatou.messagebus.facade.model.PublishMessageRestReq;
import com.ymatou.performancemonitorclient.PerformanceStatisticContainer;

/**
 * @author wangxudong 2016年7月27日 下午7:14:02
 *
 */
@Component("publishKafkaResource")
@Path("/{message:(?i:message)}/{kafka:(?i:kafka)}")
@Produces({"application/json; charset=UTF-8"})
@Consumes({MediaType.APPLICATION_JSON})
public class PublishKafkaResourceImpl implements PublishKafkaResource {

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Resource
    PublishKafkaFacade publishKafkaFacade;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ymatou.messagebus.facade.rest.PublishMessageResource#publish(com.ymatou.messagebus.facade
     * .model.PublishMessageReq)
     */
    @Override
    @POST
    @Path("/{publish:(?i:publish)}")
    public RestResp publish(PublishMessageRestReq req) {
        long startTime = System.currentTimeMillis();

        PublishMessageReq request = new PublishMessageReq();
        request.setAppId(req.getAppId());
        request.setCode(req.getCode());
        request.setIp(req.getIp());
        request.setMsgUniqueId(req.getMsgUniqueId());
        request.setBody(JSON.toJSONStringWithDateFormat(req.getBody(), DATE_FORMAT));

        PublishMessageResp resp = publishKafkaFacade.publish(request);

        // 向性能监控器汇报性能情况
        long consumedTime = System.currentTimeMillis() - startTime;
        PerformanceStatisticContainer.add(consumedTime, "Rest.Publish", "mqpublish.kafka.iapi.ymatou.com");

        return RestResp.newInstance(resp);

    }

}
