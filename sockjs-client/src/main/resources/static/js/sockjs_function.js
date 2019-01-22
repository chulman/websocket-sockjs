var sockjs;
var rInterval;

var comment = document.getElementById("comment");

function setConnected(connected) {
    $("#connect_btn").prop("disabled", connected);
    $("#disconnect_btn").prop("disabled", !connected);
    $("#send_btn").prop("disabled", !connected);

}

do_sockjs_connect = function () {

    var url = document.getElementById("url");
    var sockjs_url = url.value;
    // var options = {transports: ['websocket', 'xdr-streaming'], debug: true, jsessionid: false};
    // var options = {transports: ['websocket', 'xdr-streaming', 'xhr-streaming', 'iframe-htmlfile'], debug: true, jsessionid: false};
    // var options = {transports: ['websocket', 'iframe-htmlfile'], debug: false, jsessionid: true};
    var options = {transports: ['websocket'], debug: true, jsessionid: false, heartbeat_delay: 10000, disable_cors: true};
    // var options = {transports: ['iframe-htmlfile'], debug: false, jsessionid: true};
    // var options = {transports: ['xdr-streaming'], debug: true, jsessionid: false};
    // var options = {transports: ['xh-streaming'], debug: true, jsessionid: false};
    //var options = {transports: ['xhr-streaming'], debug: true, jsessionid: false};
    //var options = {transports: ['xdr-streaming'], debug: true, jsessionid: false};

    sockjs = new SockJS(sockjs_url, [], options);

    sockjs.onopen = function () {
        var open = '[SockJS]: open = [' + sockjs.transport + ']\n';
        console.log(open);
        setConnected(true);
    }

    sockjs.onload = function () {
        var load = '[SockJS]: load = [' + sockjs.transport + ']\n';
        console.log(load);
    }

    sockjs.onmessage = function (e) {
        var parse = JSON.stringify(e.data);
        var msg = '[SockJS]: received msg = [' + parse + ']\n';
        comment.value += msg;
    };

    sockjs.onheartbeat = function () {
        var hearbeat = '[SockJS]: hearbeat = [' + sockjs.transport + ']\n';
        console.log(hearbeat);
    };

    sockjs.onerror = function (e) {
        var error = '[SockJS]: error = [' + JSON.parse(e.data) + ']\n';
        console.log(error);
    };


    sockjs.onclose = function () {
        var close = '[SockJS]: close()' +'\n';
        console.log(close);
        setConnected(false);
    };


}

do_sockjs_send = function () {
    var message = {'message': document.getElementById("message").value};
    var jsonData = JSON.stringify(message);
    sockjs.send(jsonData);
}

do_sockjs_disconnect = function () {
    sockjs.close();
    sockjs = null;
}


do_textArea_clear = function () {
    document.getElementById("comment").value = "";
}