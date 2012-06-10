
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
    var html2="<div id='boardContainer' class='My_boardRow'>"

/*    html+="<span class='tile'></span>"
      html2+="<span class='My_tile'></span>"
    for (var i = 1; i < 11; i++) {
        html+="<span class='tile'>"+i+"</span>"
        html2+="<span class='My_tile'>"+i+"</span>"
    }
    html+="</div>"
        html2+="</div>"   */
    //creo las filas

    for (var i = 0; i < letters.length; i++) {
        html+="<div class='boardRow'>"
       // html+="<span class='tile'>"+letters[i]+"</span>"

          html2+="<div class='My_boardRow'>"
         // html2+="<span class='My_tile'>"+letters[i]+"</span>"
        for (var j = 1; j <11; j++) {
            html+="<span style='cursor:crosshair' title="+letters[i]+j+" class='tile' id="+letters[i]+j+" onclick='attack(\""+letters[i]+j+"\")'></span>"
            //html2+="<span class='My_tile' ondrop='dropIt(event)' ondragover='event.preventDefault()' id=My_board_"+letters[i]+j+"></span>"
            html2+="<span class='My_tile' id=My_board_"+letters[i]+j+"></span>"
        }
        html+="</div>"
          html2+="</div>"
    }
    html+="</div>"
    html2+="</div>"
    $("#board").html(html)
    $("#My_board").html(html2)
    $("#talk").keypress(handleReturnKey)
    populateShipYard()
})




function attack(tile){
    conn.ws({send:{attack: tile}})
}

function ready(){
    bot = new BattleshipBot();
    document.getElementById("board").style.display ="block";
    document.getElementById("main").style.display ="block";
    document.getElementById("shipyard").style.display ="none";
    document.getElementById("buttom").style.display ="none";
    $("#onChat").removeClass('onChat');
    readyToPlay()
}
function setAutoplay(){
    if(document.getElementById("autoplayCheck").checked==1){
        autoplay=true;
    }else{
        autoplay=false;
    }


}



function battleRecieve(username,event) {
    var data = JSON.parse(event.data)

    // Handle errors
    if(data.error) {
        conn.close()
        $("#onError span").text(data.error)
        $("#onError").show()
        return
    } else {
        $("#onChat").show()
    }

    if (data.message){
        chatMessage(data,username)
    }
    //Update the members list
    $("#members").html('')
    $(data.members).each(function(){$("#members").append('<li>' + this + '</li>')})

    if(data.data){

        var json=JSON.parse(data.data)

        console.log(json);
        //+++++++++++++++++++++++++++++Strategy++++++++++++++++++++++++++++++++++++++//
        if(json.type=='strategy'){
            drawStrategy(json)
        }
        //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
        //++++++++++++++++++++++++++++++++++++attack++++++++++++++++++++++++++++++++++++++++++++++//

        //si me atacaron
        if(json.board == username){
            if(json.state == 'sunk'){
                for(positions in json.shipPositions){
                    $('#My_board_'+json.tile).removeClass('hit');
                    $('#My_board_'+json.shipPositions[positions]).addClass(json.state);

                }
            }else{
                $('#My_board_'+json.tile).addClass(json.state);
            }
            if(autoplay){
                botAttack()
            }
            //si ataque yo
        }else {
            var tile=json.tile
            tile.x=parseInt(tile.substr(1))
            tile.y=tile.substr(0,1).charCodeAt()-65
            var command=json.state
            if(json.state=='win'){
                popUp("won");

            }
           /* console.log("tile: "+json.tile)
            var letterPos = json.tile;
            var letritas=letterPos.substr(0,1);

            var letterIndex = 0;
            for(var j=0;j<letters.length;j++) {
                if(letters[j]==letritas){
                    letterIndex=j;
                }
                break;
            }       */

            if(json.state == 'sunk'){
               command=json.shipName;
                for(positions in json.shipPositions){
                    $('#My_board_'+json.tile).removeClass('hit');
                    $('#'+json.shipPositions[positions]).addClass(json.state);

                }

            }else{
                //console.log(letterIndex+1+" "+json.tile.substring(1)+" "+json.state)
               // bot.update(parseInt(json.tile.substring(1)),letterIndex+1,json.state);

                $('#'+json.tile).addClass(json.state);

            }
            bot.update(tile.x,tile.y,command);
            console.log("updating bot:"+tile.x,tile.y,command);
        }
    }
}





function drawStrategy(json){
    for(x=0;x < json.shipPositions.length;x++){
        if(json.orientation=="true"){
            $('#My_board_'+json.shipPositions[x]).addClass(json.shipName);
            div ='My_board_'+json.shipPositions[x];
            document.getElementById(div).style.backgroundPosition = -32*x+'px 0px';
        }else{
            $('#My_board_'+json.shipPositions[x]).addClass(json.shipName+'Vertical');
            div ='My_board_'+json.shipPositions[x];
            document.getElementById(div).style.backgroundPosition = '0px'+' '+-32*x+"px";

        }
    }
}





function chatRecieve(username,event) {
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
    if(data.user == '@username') $(el).addClass('me')
    $('#chat_messages').append(el)
    // Update the members list
    $("#members").html('')
    $(data.members).each(function() {
        $("#members").append('<li>' + this + '</li>')
    })
    if(data.data){
        var json=JSON.parse(data.data)
        if(json.url){window.location.href=json.url}

    }
}
function play(){
    conn.ws({send:{play: "play"}})
}
var handleReturnKey = function(e) {
    if(e.charCode == 13 || e.keyCode == 13) {
        e.preventDefault()
        conn.ws({send:{text: $("#talk").val()},beforeSend:function(){$("#talk").val('')}})
    }
}

function botAttack(){
        var letters = new Array("A","B","C","D","E","F","G","H","I","J");
        console.log("bot play!");
        var point = bot.suggest();
        var tile=""+letters[point.x]+point.y.toString()
        attack(tile);
}