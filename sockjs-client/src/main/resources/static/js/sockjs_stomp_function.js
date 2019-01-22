var stompClient = null;
var stomp_comment = document.getElementById("stomp_comment");


function setStompConnected(connected) {
    $("#stomp_connect_btn").prop("disabled", connected);
    $("#stomp_disconnect_btn").prop("disabled", !connected);
    $("#stomp_send_btn").prop("disabled", !connected);

}

function append(message) {
    stomp_comment.value += '[SockJs-stomp]: received=' + message.message+'\n';
}

function do_sockjs_stomp_connect() {

    var url = document.getElementById("url_stomp");
    var sockjs_url = url.value;

    var socket = new SockJS(sockjs_url);
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setStompConnected(true);
        console.log('Connected: ' + frame);

        stompClient.subscribe('/topic/messages', function (message) {
            append(JSON.parse(message.body));
        });
    });
}

function do_sockjs_stomp_disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setStompConnected(false);
    console.log("Disconnected");
}

function do_sockjs_stomp_send() {
    var message = {'message': document.getElementById("stomp_message").value};
    var jsonData = JSON.stringify(message);
    stompClient.send("/api/echo", {}, jsonData);
}


do_textArea_clear2 = function () {
    document.getElementById("stomp_comment").value = "";
}