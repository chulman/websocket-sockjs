
    var sockjs;
    var rInterval;

    function delete_cookie( name ) {
        document.cookie = name + "=; expires=Thu, 01 Jan 1970 00:00:01 GMT;path=/;";
    }

    do_sockjs_connect = function() {

        delete_cookie("")

        var ip = document.getElementById("ip");
        var port = document.getElementById("port");
        var ipValue = ip.value;
        var portValue = port.value;
        var pathValue = path.value;

        var sockjs_url = ipValue + ":" + portValue + pathValue;

        // var options = {transports: ['websocket', 'xdr-streaming'], debug: true, jsessionid: false};
        // var options = {transports: ['websocket', 'xdr-streaming', 'xhr-streaming', 'iframe-htmlfile'], debug: true, jsessionid: false};
        // var options = {transports: ['websocket', 'iframe-htmlfile'], debug: false, jsessionid: true};
        var options = {transports: ['websocket'], debug: true, jsessionid: false};
        // var options = {transports: ['iframe-htmlfile'], debug: false, jsessionid: true};
        // var options = {transports: ['xdr-streaming'], debug: true, jsessionid: false};
        // var options = {transports: ['xh-streaming'], debug: true, jsessionid: false};
        //var options = {transports: ['xhr-streaming'], debug: true, jsessionid: false};
        //var options = {transports: ['xdr-streaming'], debug: true, jsessionid: false};


        sockjs = new SockJS(sockjs_url, [], options);

        sockjs.onopen = function() {
            document.getElementById("comment").defaultValue = "";
            var old = document.getElementById("comment").value;
            old = old + "\n" + '[*] open [' + sockjs.transport + ']'
            document.getElementById("comment").value = old;

            var comment = document.getElementById("comment");
            //document.getElementById("comment").defaultValue('[*] open ' + sockjs.protocol);
            //alert('[*] open ' + sockjs.protocol)
        }

        sockjs.onload = function()  {
            console.log('[*] load', sockjs.protocol);
            sockjs.send("hello");
        }

        sockjs.onmessage = function(e) {
            var message;
            message = "[.] message' : " + e.data;
            //alert(message);
            var old = document.getElementById("comment").value;
            old = old + "\n" + message;
            document.getElementById("comment").value = old;

            var obj = JSON.parse(e.data);
        };

        sockjs.onheartbeat = function() {
            var old = document.getElementById("comment").value;
            old = old + "\n" + '[*] onheartbeat ';
            document.getElementById("comment").value = old;
            //alert('[*] onheartbeat ');
        };

        sockjs.onerror = function(e) {
            var old = document.getElementById("comment").value;
            old = old + "\n" + '[*] onheartbeat ' + e.data;
            document.getElementById("comment").value = old;
            //alert('[*] onheartbeat ' + e.data);
        };

/*
       sockjs.onclose   = function()  {
           var old = document.getElementById("comment").value;
           old = old + "\n" + '[*] close ';
           document.getElementById("comment").value = old;
           alert('[*] close ');
       };
*/

}


  do_xpush_AUTH = function() {
        var userId = document.getElementById("userId");
        var password = document.getElementById("password");

        var userIdValue = userId.value;
        var passwordValue = password.value;

        var AUTH = {'Action' : 'AUTH', 'ID' : userIdValue, 'PW' :  passwordValue };
        sockjs.send( JSON.stringify(AUTH) );


    }

    do_xpush_BYEC = function() {
        var BYEC = {"Action": "BYEC"};
        sockjs.send(JSON.stringify(BYEC));
//        sockjs.close()
    }

    do_xpush_ADDF = function() {
        var topicType = document.getElementById("topicType");
        var password = document.getElementById("topicId");

        var topicTypeValue = topicType.value;
        var topicIdValue = topicId.value;

        var ADDF = {'Action' : 'ADDF', 'TopicType' : topicTypeValue, 'TopicId' : topicIdValue };
        sockjs.send( JSON.stringify(ADDF) );
    }

    do_xpush_DELF = function() {
        var topicType = document.getElementById("topicType");
        var password = document.getElementById("topicId");

        var topicTypeValue = topicType.value;
        var topicIdValue = topicId.value;

        var DELF = {'Action' : 'DELF', 'TopicType' : topicTypeValue, 'TopicId' : topicIdValue };
        sockjs.send( JSON.stringify(DELF) );
    }

    do_xpush_KEEP_S = function() {
        var KEEP = {
            Action : "KEEP"
        };

        sockjs.send( JSON.stringify(KEEP) );
    }

    do_xpush_KEEP_C = function() {
        setInterval(function() {

            var KEEP = {
                Action : "KEEP"
            };
            sockjs.send( JSON.stringify(KEEP) );

        }, 20000);
    }

    do_xpush_REQD = function() {
        var userId = document.getElementById("userId");
        var topicType = document.getElementById("topicType");
        var password = document.getElementById("topicId");

        var userIdValue = userId.value;
        var topicTypeValue = topicType.value;
        var topicIdValue = topicId.value;

       var REQD = {"Action"	: "REQD","UserID"	: userIdValue,"TopicType"	: topicTypeValue,"TopicId"	: topicIdValue};
	   sockjs.send( JSON.stringify(REQD) );
    }

    do_xpush_MSGC = function() {
        var userId = document.getElementById("userId");
        var topicType = document.getElementById("topicType");
        var password = document.getElementById("topicId");

        var userIdValue = userId.value;
        var topicTypeValue = topicType.value;
        var topicIdValue = topicId.value;

       var MSGC = {"Action"	: "MSGC", "UserID"	: userIdValue,"TopicType"	: topicTypeValue,"TopicId"	: topicIdValue };
	   sockjs.send( JSON.stringify(MSGC) );
    }

    do_xpush_ADUI = function() {
        var userId = document.getElementById("userId");
        var topicType = document.getElementById("topicType");
        var password = document.getElementById("topicId");

        var userIdValue = userId.value;
        var topicTypeValue = topicType.value;
        var topicIdValue = topicId.value;

	   var ADUI = {"Action": "ADUI", "UserID"	: userIdValue,"TopicType"	: topicTypeValue,"TopicId"	: topicIdValue	   };
	   sockjs.send( JSON.stringify(ADUI) );
    }
    do_xpush_UNUI = function() {
        var userId = document.getElementById("userId");
        var topicType = document.getElementById("topicType");
        var password = document.getElementById("topicId");

        var userIdValue = userId.value;
        var topicTypeValue = topicType.value;
        var topicIdValue = topicId.value;

	   var UNUI = {"Action": "UNUI", "UserID"	: userIdValue,"TopicType"	: topicTypeValue,"TopicId"	: topicIdValue	   };
	   sockjs.send( JSON.stringify(UNUI) );
    }

    do_xpush_RGST = function() {
        var userId = document.getElementById("userId");
        var userIdValue = userId.value;


	   var RGST = {"Action":"RGST", "UserID":userIdValue, "DeviceToken":"1111111111", "OS"	: "1", "OS_version"	: "10.2"	   };
	   sockjs.send( JSON.stringify(RGST) );
    }
    do_xpush_UNRG = function() {
        var userId = document.getElementById("userId");
        var userIdValue = userId.value;

	   var UNRG = {"Action":"UNRG",  "UserID":userIdValue, "DeviceToken":"1111111111", "OS"	: "1", "OS_version"	: "10.2"	   };
	   sockjs.send( JSON.stringify(UNRG) );
    }

    do_xpush_ACKN = function() {
        var userId = document.getElementById("userId");
         var userIdValue = userId.value;
        var messageID = document.getElementById("messageID");
         var messageIDValue = messageID.value;

       var ACKN = {"Action": "ACKN","UserID": userIdValue, "MessageID": messageIDValue	   };
	   sockjs.send( JSON.stringify(ACKN) );
    }

    do_xpush_RECT = function() {
        var userId = document.getElementById("userId");
        var userIdValue = userId.value;
        var messageID = document.getElementById("messageID");
        var messageIDValue = messageID.value;

       var RECT = {"Action": "RECT","UserID": userIdValue, "MessageID": messageIDValue     };
       sockjs.send( JSON.stringify(RECT) );
    }



    do_textArea_clear = function() {
        document.getElementById("comment").value = "";
    }