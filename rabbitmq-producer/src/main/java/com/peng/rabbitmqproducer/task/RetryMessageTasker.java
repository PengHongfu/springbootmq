package com.peng.rabbitmqproducer.task;


import com.peng.entity.BrokerMessageLog;
import com.peng.entity.Order;
import com.peng.rabbitmqproducer.constant.Constants;
import com.peng.rabbitmqproducer.mapper.BrokerMessageLogMapper;
import com.peng.rabbitmqproducer.producer.RabbitOrderSender;
import com.peng.rabbitmqproducer.utils.FastJsonConvertUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class RetryMessageTasker {


    @Autowired
    private RabbitOrderSender rabbitOrderSender;

    @Autowired
    private BrokerMessageLogMapper brokerMessageLogMapper;

    @Scheduled(initialDelay = 5000, fixedDelay = 10000)
    public void reSend(){
       log.info("-----------定时任务开始-----------");
        //pull status = 0 and timeout message
        List<BrokerMessageLog> list = brokerMessageLogMapper.query4StatusAndTimeoutMessage();

        list.forEach(messageLog -> {
            if(messageLog.getTryCount() >= 3){
                log.info("-----------定时任务：查询任务失败次数超过三次-----------");
                //update fail message
                brokerMessageLogMapper.changeBrokerMessageLogStatus(messageLog.getMessageId(), Constants.ORDER_SEND_FAILURE, new Date());
            } else {
                log.info("-----------定时任务：任务重新发送-----------");
                // resend
                brokerMessageLogMapper.update4ReSend(messageLog.getMessageId(),  new Date());
                Order reSendOrder = FastJsonConvertUtil.convertJSONToObject(messageLog.getMessage(), Order.class);
                try {
                    rabbitOrderSender.sendOrder(reSendOrder);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("-----------定时任务：发生异常-----------");
                }
            }
        });
    }
}

