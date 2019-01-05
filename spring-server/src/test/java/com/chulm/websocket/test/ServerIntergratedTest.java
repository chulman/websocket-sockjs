package com.chulm.websocket.test;

import com.chulm.websocket.test.common.JavaConnector;

import java.io.IOException;

//@RunWith(SpringRunner.class)
public class ServerIntergratedTest {

//    @Test
    public void Test(){
        JavaConnector sockClient = new JavaConnector();
        try {
            sockClient.connect("localhost",10080);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
