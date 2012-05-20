
function WsConnection(ws, recive){
    var WS = window['MozWebSocket'] ? MozWebSocket : WebSocket
    this.socket = new WS(ws)
    this.socket.onmessage =recive
};
WsConnection.prototype.close=function(){
    this.socket.close()
}
WsConnection.prototype.ws=function(json){
    var socket=this.socket;
    var val=function(param,alt){
        return param?param:(alt?alt:function(){})
    };
    var sendMessage = function(){
        val(json.beforeSend)()
         socket.send(
            JSON.stringify(
                json.send
        ))
        val(json.afterSend)()
    }
    sendMessage()
    return this;
}

$(document).ready(function() {

    var letters = new Array("A","B","C","D","E","F","G","H","I","J");
    //creo la primer fila de numeros
    var html="<div class='boardRow'>"
    var html2="<div class='My_boardRow'>"


    html+="<span class='tile'></span>"
      html2+="<span class='My_tile'></span>"
    for (var i = 1; i < 11; i++) {
        html+="<span class='tile'>"+i+"</span>"
        html2+="<span class='My_tile'>"+i+"</span>"
    }
    html+="</div>"
        html2+="</div>"
    //creo las filas

    for (var i = 0; i < letters.length; i++) {
        html+="<div class='boardRow'>"
        html+="<span class='tile'>"+letters[i]+"</span>"

          html2+="<div class='My_boardRow'>"
          html2+="<span class='My_tile'>"+letters[i]+"</span>"
        for (var j = 1; j <11; j++) {
            html+="<span class='tile' id="+letters[i]+j+" onclick='attack(\""+letters[i]+j+"\")'></span>"
             html2+="<span class='My_tile' id=My_board_"+letters[i]+j+"></span>"
        }
        html+="</div>"
          html2+="</div>"
    }
    paintDefault();
    $("#board").html(html)
    $("#My_board").html(html2)

})

                 function void paintDefault(){

                    $('#My_board_A1').addClass('ship');
                    $('#My_board_A2').addClass('ship');
                    $('#My_board_B3').addClass('ship');
                    $('#My_board_C3').addClass('ship');
                    $('#My_board_D3').addClass('ship');
                    $('#My_board_G6').addClass('ship');
                    $('#My_board_G7').addClass('ship');
                    $('#My_board_G8').addClass('ship');
                    $('#My_board_a5').addClass('ship');
                    $('#My_board_B5').addClass('ship');
                    $('#My_board_C5').addClass('ship');
                    $('#My_board_D5').addClass('ship');
                    $('#My_board_J5').addClass('ship');
                    $('#My_board_J6').addClass('ship');
                    $('#My_board_J7').addClass('ship');
                    $('#My_board_J8').addClass('ship');
                    $('#My_board_J9').addClass('ship');

                       }


function attack(tile){
    conn.ws({send:{attack: tile}})
}


/*
function connect(username,WsURL) {
    var WS = window['MozWebSocket'] ? MozWebSocket : WebSocket
    var chatSocket = new WS(WsURL)

    var sendMessage = function() {
        chatSocket.send(JSON.stringify(
            {text: $("#talk").val()}
        ))
        $("#talk").val('')
    }

    var receiveEvent = function(event) {
        var data = JSON.parse(event.data)

        // Handle errors
        if(data.error) {
            chatSocket.close()
            $("#onError span").text(data.error)
            $("#onError").show()
            return
        } else {
            $("#onChat").show()
        }

        // Create the message element
        var el = $('<div class="message"><span></span><p></p></div>')
        $("span", el).text(data.user)
        $("p", el).text(data.message)
        $(el).addClass(data.kind)
        if(data.user == username) $(el).addClass('me')
        $('#messages').append(el)

        // Update the members list
        $("#members").html('')
        $(data.members).each(function() {
            $("#members").append('<li>' + this + '</li>')
        })
    }

    var handleReturnKey = function(e) {
        if(e.charCode == 13 || e.keyCode == 13) {
            e.preventDefault()
            sendMessage()
        }
    }

    $("#talk").keypress(handleReturnKey)

    chatSocket.onmessage = receiveEvent


}*/