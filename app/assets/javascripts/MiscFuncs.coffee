@ships=[["carrier",5,"Aircraft carrier","carrier"], ["battleship",4,"Battleship","battleship"],
  ["destroyer",3,"Destroyer","destroyer"],
  ["patrolShip",2,"Patrol boat","patrol"],["submarine",3,"Submarine","submarine"]]

letters = new Array("A","B","C","D","E","F","G","H","I","J")

createDiv = (jsonObj) ->
  $('<div/>', jsonObj)

selectedShip=exports ? this

@populateShipYard= ->
  $('#shipyard').append($('<img/>',{
  src:"/assets/images/#{ship[0]}.png"
  alt:ship[0]
  id:ship[0]+i
  draggable: true
  ondragstart: "dragIt(this,event)"
  class: "draggableShip"
  tileLength: ship[1]
  shipType:ship[2]
  horizontal: true
  })) for ship ,i in ships
  $div = $('#boardContainer');
  $(".draggableShip").click(-> toggleDirection(this))
  #makeDraggable(drag,->dragIt) for drag in $(".draggableShip")
  makeDroppable(drop,dropIt) for drop in $(".My_tile")

@drawBoat=()->
  ###
  deberia decrles a los tiles consiguientes que dibujen los demas pedazons del barquito ,
  se puede hacer poniendo la imagen como fondo y corriendola como sprite 32px ( o 30 no estoy seguro)
  ###

@toggleDirection=(img)->
  horizontal= isHorizontal(img)
  $(img).attr("horizontal",horizontal)
  if horizontal then $(img).removeClass("vertical") else $(img).addClass("vertical")
  sendResetPosition($(img).attr('shipType'))
  tile=document.getElementById($(img).attr("id")).parentNode
  if tile?
    tileId=$(tile).attr("id").substr(9,2)
    if tileId.length==2
     sendBoatPosition(tileId,$(img).attr('shipType'),$(img).attr('horizontal'),$(img).attr('tileLength'),$(img).attr('alt'))
     #ese ==2 es horrible, pero fue....
  drawBoat()


@dropIt = (event) ->
  if event.preventDefault then event.preventDefault()
  log event.dataTransfer.getData("text/html")
  id = event.dataTransfer.getData("text/html")
  element = document.getElementById(id)
  event.target.appendChild(element);
  #$(event.target).addClass('ship')
  tileId=$(event.target).attr('id').substr(9)
  sendBoatPosition(tileId,$(element).attr('shipType'),$(element).attr('horizontal'),$(element).attr('tileLength'),$(element).attr('alt'))
  drawBoat()

@dragIt=(div,event)->
  selectedShip=$(div)
  sendResetPosition(selectedShip.attr('shipType'))
  event.dataTransfer.setDragImage(div, 5, 20)
  event.dataTransfer.setData("text/html",event.target.id)
###
log div
log event
log $(div).attr('id')
log event.target
log event.target.id
###

@makeDroppable=(elem, func)->
  addEvent(elem, 'dragover', cancel)
  addEvent(elem, 'dragenter', cancel)
  addEvent(elem, 'drop',func)

@makeDraggable=(elem,func)->
  addEvent(elem, 'dragstart', func)


cancel=(event)->
  if (event.preventDefault) then event.preventDefault()
  return false;

@sendBoatPosition=(tile,ship,horizontal,length,alt)->
  conn.ws({send:{
    alt: alt
    ship: ship
    length:length
    horizontal: horizontal
    tile:tile
  }})
@sendResetPosition=(ship)->
  conn.ws({send:{
    resetPosition: ship
  }})

@readyToPlay=->
  $(".draggableShip").hide()
  conn.ws({send:{
    ready: "ready"
  }})

@isHorizontal=(img)->
  if $(img).attr("horizontal")!="true" then true else false

log=(msg)->
  console.log msg



@chatMessage=(data,username)->
  # Create the message element
  el = $('<div class="message"><span></span><p></p></div>')
  $("span", el).text(data.user+":")
  $("p", el).text(data.message)
  $(el).addClass(data.kind)
  if(data.user == username)
    $(el).addClass('me')
  $('#messages').append(el)





###
@ready=->
  bot = new BattleshipBot();
  $("#board").show()
  $("#main").show
  $("#shipyard").hide
  $("#buttom").hide
  $("#onChat").removeClass('onChat');
  alert("ready")
  readyToPlay()
   ###