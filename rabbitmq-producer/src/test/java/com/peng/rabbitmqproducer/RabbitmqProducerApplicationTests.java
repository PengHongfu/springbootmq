package com.peng.rabbitmqproducer;

import com.peng.entity.Order;
import com.peng.rabbitmqproducer.server.OrderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RabbitmqProducerApplicationTests {

    @Autowired
    private OrderService orderService;
    @Test
    public void contextLoads() {
    }
    @Test
    public void setOrderService() throws Exception {
        Order order = new Order();
        order.setId("000002222");
        order.setName("你好啊");
        order.setMessageId("888888sdfsdjflksf");
        orderService.createOrder(order);
    }

}
