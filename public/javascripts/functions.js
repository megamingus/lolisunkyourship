var aircraftCarrier=new Array();
var destroyer=new Array();
var battleship=new Array();
var patrolShip=new Array();
var submarine=new Array();



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
    bot = new BattleshipBot();
    var letters = new Array("A","B","C","D","E","F","G","H","I","J");
    //creo la primer fila de numeros
    var html="<div class='boardRow'>"
    var html2="<div id='boardContainer' class='My_boardRow'>"

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
    document.getElementById("board").style.display ="block";
    document.getElementById("main").style.display ="block";
    document.getElementById("autoPlay").style.display ="inline";
    document.getElementById("shipyard").style.display ="none";
    document.getElementById("buttom").style.display ="none";
    document.getElementById("sideText").style.display="none";
    $("#onChat").removeClass('onChat');
    readyToPlay() ;
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
    if(data.data){
    var json=JSON.parse(data.data);
    console.log(json);
           if(json.endGame=="EndGame"){
                       console.log("MSG: "+json.myURL);
                                   popUp(username,data.message,json.myURL);
                               }
    }

    if (data.message){
        chatMessage(data,username)
    }
    //Update the members list
   // $("#members").html('')
   // $(data.members).each(function(){$("#members").append('<li>' + this + '</li>')})

    if(data.data){

        var json=JSON.parse(data.data)



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
                    document.getElementById('My_board_'+json.shipPositions[positions]).style.backgroundColor="red";


                }
            }else{
               /* $('#My_board_'+json.tile).toggleClass(json.state);   */

                if(json.state=='hit'){
                    document.getElementById('My_board_'+json.tile).style.backgroundColor="yellow";
                } else{
                    $('#My_board_'+json.tile).toggleClass(json.state);
                }

            }
            if(autoplay){
                botAttack()
            }
            //si ataque yo
        }else if(json.tile){



            var tileX=json.tile.substr(0,1).charCodeAt()-65
            var tileY=parseInt(json.tile.substr(1))-1
            var command=json.state


            if(json.state == 'sunk'){
               command=botConversion(json.shipName)
                for(positions in json.shipPositions){
                    $('#My_board_'+json.tile).removeClass('hit');
                    $('#'+json.shipPositions[positions]).toggleClass(json.state);

                }

            }else{
                //console.log(letterIndex+1+" "+json.tile.substring(1)+" "+json.state)
               // bot.update(parseInt(json.tile.substring(1)),letterIndex+1,json.state);

                $('#'+json.tile).toggleClass(json.state);

            }
            bot.update(tileX,tileY,command);
            //console.log("updating bot:"+tileX+tileY+" comm: "+command);
        }
    }
}




function drawStrategy(json){

if(json.shipName=="aircraftCarrier"){
            for(y=0;y<aircraftCarrier.length;y++){

             $('#My_board_'+aircraftCarrier[y]).removeClass("aircraftCarrier")
              $('#My_board_'+aircraftCarrier[y]).removeClass("ship")
             $('#My_board_'+aircraftCarrier[y]).removeClass("aircraftCarrierVertical")

            }

              aircraftCarrier=json.shipPositions;

           }else if(json.shipName=="battleship"){
                 for(y=0;y<battleship.length;y++){

                 $('#My_board_'+battleship[y]).removeClass("battleship")
               $('#My_board_'+battleship[y]).removeClass("ship")
                 $('#My_board_'+battleship[y]).removeClass("battleshipVertical")



                  }
                              battleship=json.shipPositions;

           }else if(json.shipName=="patrolShip"){
                    for(y=0;y<patrolShip.length;y++){

                    $('#My_board_'+patrolShip[y]).removeClass("patrolShip")
                    $('#My_board_'+patrolShip[y]).removeClass("ship")
                    $('#My_board_'+patrolShip[y]).removeClass("patrolShipVertical")

                        }
                                 patrolShip=json.shipPositions;
       }else if(json.shipName=="submarine"){
             for(y=0;y<submarine.length;y++){

             $('#My_board_'+submarine[y]).removeClass("submarine")
             $('#My_board_'+submarine[y]).removeClass("ship")
             $('#My_board_'+submarine[y]).removeClass("submarineVertical")

                        }
                          submarine=json.shipPositions;
       }else{
             for(y=0;y<destroyer.length;y++){

             $('#My_board_'+destroyer[y]).removeClass("destroyer")
             $('#My_board_'+destroyer[y]).removeClass("ship")
             $('#My_board_'+destroyer[y]).removeClass("destroyerVertical")
             }
                          destroyer=json.shipPositions;
       }

    for(x=0;x < json.shipPositions.length;x++){
    console.log(json);




        if(json.orientation=="true"){
            $('#My_board_'+json.shipPositions[x]).addClass(json.shipName);
             $('#My_board_'+json.shipPositions[x]).addClass("ship");
            div ='My_board_'+json.shipPositions[x];
            document.getElementById(div).style.backgroundPosition = -32*x+'px 0px';
        }else{
            $('#My_board_'+json.shipPositions[x]).addClass(json.shipName+'Vertical');
             $('#My_board_'+json.shipPositions[x]).addClass("ship");
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
        var point = bot.suggest();
        var tile=""+letters[point.x]+(point.y+1).toString()
        attack(tile);
}