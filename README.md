[![](https://badges.gitter.im/chriskacerguis/codeigniter-restserver.png)](https://gitter.im/salmar/spring-websocket-chat)



# WebSocket-SockJs

* spring-websocket with stomp 
    + [Getting Started](https://spring.io/guides/gs/messaging-stomp-websocket/)
    
* spring-boot integration with netty-websocket codec on the low level


## Required

**Browser** 

- IE : 10
- Chrome : 4
- Safari : 5
- FireFox : 6


**Server**
- websocket library  
- Spring 4.0
- Java 8 in Spring
```
compile('org.springframework.boot:spring-boot-starter-websocket')
```

**Client**

- SockJs.js 
- SockJs-stomp.js


## SockJs Javascript Client

https://github.com/sockjs/sockjs-client

- connect
```js
var socket = new SockJS(sockjs_url);
stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {

        stompClient.subscribe('/topic/hello', function (message) {
            append(JSON.parse(message.body));
        });
    });
```
- connect
   
```js
var data = JSON.stringify(message);
stompClient.send("/api/echo", {}, data);
```

- close
```js
stompClient.disconnect();
```   

- heartbeat (ping & pong)
```js
stompClient.heartbeat.outgoing = 5000; 
stompClient.heartbeat.incoming = 5000;    
```


## Usage SockJs With STOMP

1) Spring은 websocket을 stomp protocol(Text Oriented)과 함께 지원한다.  
2) STOMP를 사용하지 않는 경우 주고 받는 메세지에 대해 직접 처리 해야한다.
   + TextWebSocketFrame
   + PingWebSocketFrame
   + PongWebSocketFrame
   + CloseWebSocketFrame
   + BinaryWebSocketFrame

3) 뿐만 아니라 Server Side에서는 Session 관리까지 직접 구현해야 한다.
4) STOMP는 기본적으로 subscribe를 통해서 데이터를 전달 받는다. 따라서, 이 message가 어떠한 method에 의해서 전송을 받았는지를 좀 더 명확히 할 수 있다.


## SockJs Protocol 
- GET /info : get server side info
- GET /serverid/sessionid/transport
- transport : websocket, xhr, http-streaming, http-long polling ... 
   
1 . request info

localhost:8080/ws/info?t=1722525...

````
GET /ws HTTP/1.1..
Host: 172.10.12.126:9091..
User-Agent: Mozilla/5.0 (Windows NT 6.1; rv:12.0;; NCLIENT50_AAP50077C6E346) Gecko/20100101 Firefox/12.0.. Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8..
Accept-Language: ko-kr,ko;q=0.8,en-us;q=0.5,en;q=0.3..
Accept-Encoding: gzip, deflate..
Connection: keep-alive..
Cookie: JSESSIONID=19F5C2A0B60401C699F43A71509C8271....
````

2 . response

````
HTTP/1.1 200 OK..
Server: Apache-Coyote/1.1..
Content-Type: text/html;charset=ISO-8859-1.. Content-Language: ko-KR..
Content-Length: 3112..
Date: Wed, 30 Mar 2016 04:34:05 GMT....
````

3 . request websocket

Header : **connection**, **upgrade**, **sec-websocket-key**

````
GET /ws/serverid/sessionId/websocket HTTP/1.1..
Host: 172.10.12.126:9091..
User-Agent: Mozilla/5.0 (Windows NT 6.1; rv:12.0; ; NCLIENT50_AAP50077C6E346)Gecko/20100101 Firefox/12.0.. Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8..
Accept-Language: ko-kr,ko;q=0.8,en-us;q=0.5,en;q=0.3..
Accept-Encoding: gzip, deflate..
Connection: keep-alive, Upgrade.. Sec-WebSocket-Version: 13..
Origin:http://172.10.12.126:9091..
Sec-WebSocket-Key: 88C1PEvPYxIw+okRGj/MbQ==..
Cookie:JSESSIONID=19F5C2A0B60401C699F43A71509C8271.. Pragma: no-cache..
Cache-Control: no-cache..
Upgrade: websocket....
````

4 . response upgrade and handshake

````
HTTP/1.1 101 Switching Protocols.. Server: Apache-Coyote/1.1..
Upgrade: websocket..
Connection: upgrade..
Sec-WebSocket-Accept: ymWqxWoBvll5eKUcrQGmm1GNRnQ=..
Date:Wed, 30 Mar 2016 04:34:05 GMT....
````

## frame

- open frame

````
81 01 6F : ..o
````

- heartbeat frame 
  + session check with heartbeat / ping / pong
  + session close, not disconnect.
````
81 01 68 : ..h
````
 
 - OpCode
 
 ````
 continue = 0x0
 text = 0x1
 binary = 0x2
 close: 0x8
 Ping: 0x9
 Pong: 0xA
 ````