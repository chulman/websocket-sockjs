[![](https://badges.gitter.im/chriskacerguis/codeigniter-restserver.png)](https://gitter.im/salmar/spring-websocket-chat)



# spring5-reactive-websocket

* https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html#webflux-websocket
* https://www.baeldung.com/spring-5-reactive-websockets

* https://www.sudoinit5.com/post/spring-reactive-ws-cold-server/
* https://github.com/eugenp/tutorials/tree/master/spring-5-reactive



## Server

* http header upgrade, connection, checker..

```java

   @Bean
      public WebSocketHandlerAdapter handlerAdapter() {
          return new WebSocketHandlerAdapter();
      }

```

* Router Mapping and config cors ... so on 

```java

    @Bean
    public HandlerMapping webSocketMapping() {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/reactive/websocket", webSocketHandler);

        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setOrder(10);
        mapping.setUrlMap(map);
        return mapping;
    }

```


* handle session packet
```java
  @Override
    public Mono<Void> handle(WebSocketSession session) {

        session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .....
                
        return session.send(
                ....
        );
    }
```


## Client
 
 * Reactor Client
 
```java

public void send(){
    
    WebSocketClient client = new ReactorNettyWebSocketClient();

        client.execute(
                URI.create(uri),
                session -> session.send(
                        Mono.just(session.textMessage(payload)))
                        .thenMany(session.receive()
                                .map(WebSocketMessage::getPayloadAsText)
                                .log())
                        .then())
                .block(Duration.ofSeconds(10L));
    }

``` 


* javscript 

```js

    var ws = new WebSocket("ws://localhost:8080/reactive/websocket");
    ws.onopen = function() {
        console.log("ws.onopen", ws);
        console.log(ws.readyState, "websocketstatus");
    }
    ws.onclose = function(e) {
        console.log("ws.onclose", ws, e);
        updateContainer("Closing connection");
    }
    ws.onerror = function(e) {
        console.log("ws.onerror", ws, e);
        updateContainer("An error occured");
    }
    ws.onmessage = function(e) {
        console.log("ws.onmessage", ws, e);
        updateContainer(e.data);
    }
    function updateContainer(message) {
        document.querySelector(".container").innerHTML += message + "<br>";
    }
```
  