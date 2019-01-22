package com.chulm.websocket.netty.server.handler.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private final Map<String, Session> sessionMap = new ConcurrentHashMap<>();

    enum Singleton {
        INSTANCE;
    }

    public static SessionManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    private static class LazyHolder {
        private static final SessionManager INSTANCE = new SessionManager();
    }


    public void put(String key, Session value){
        sessionMap.put(key, value);
    }

    public Session get(String key){
       return sessionMap.get(key);
    }

    public void remove(String key){
        sessionMap.remove(key);
    }

    public int getUserCount(){
        return sessionMap.size();
    }
}
