ships=[["aircraftCarrier",5,"Aircraft carrier"], ["battleship",4,"Battleship"],["destroyer",3,"Destroyer"]
  ["patrolShip",2,"Patrol boat"],["submarine",3,"Submarine"]]

createDiv = (jsonObj) ->
  $('<div/>', jsonObj)

selectedShip=exports ? this

@populateShipYard= ->
  #$('#shipyard').append(createDiv({
  $('#shipyard').append($('<img/>',{
  src:"/assets/images/#{ship[0]}.png"
  alt:ship[0]
  id:ship[0]+i
  draggable: true
  class: "draggableShip"
  tileLength: ship[1]
  shipType:ship[2]
  #ondragstart:->dragIt("#{ship}{i}",event)
  #onclick:-> toggleDirection(this)
  horizontal: true
  })) for ship ,i in ships
  ### $("""##{ship[0]}#{i}""").append($('<img/>',{
  src:"/assets/images/#{ship[0]}.png"
  alt:ship[0]
  })) for ship, i in ships    ###
  $div = $('#boardContainer');
  $(".draggableShip").click(-> toggleDirection(this)).drag("start",( ev, dd )->
    selectedShip=$(this)
    console.log $div.offset()
    sendResetPosition(selectedShip.attr('shipType'))
    dd.limit = $div.offset()
    dd.limit.bottom = dd.limit.top + $div.outerHeight() - $( this ).outerHeight()
    dd.limit.right = dd.limit.left + $div.outerWidth() - $( this ).outerWidth()
  ).drag(( ev, dd )->
    $(this).css({
    position: 'absolute'
    top: Math.round( dd.offsetY / 32 ) * 32
    left: Math.round( dd.offsetX / 32 ) * 32
    #top: dd.offsetY
    #left: dd.offsetX
    #top: Math.min( dd.limit.bottom, Math.max( dd.limit.top, dd.offsetY ) )
    #left: Math.min( dd.limit.right, Math.max( dd.limit.left, dd.offsetX ) )
    })
    $.drop({ multi: $(this).attr('tileLength') });
  )
  $(".My_tile").drop(( ev, dd )->
    # $(this).addClass('ship')
    tileId=$(this).attr('id').substr(9,2)
    console.log "#{selectedShip.attr('shipType')} is  on #{tileId}"
    sendBoatPosition(tileId,selectedShip.attr('shipType'),selectedShip.attr('tileLength'))
  )
  $.drop({ multi: 20 });

@sendBoatPosition=(tile,ship,length)->
  conn.ws({send:{
  ship: ship
  length:length
  tile:tile
  }})
@sendResetPosition=(ship)->
  conn.ws({send:{
  resetPosition: ship
  }})

@readyToPlay=->
  $(".draggableShip").click(->).drag(->)
  conn.ws({send:{
  ready: "ready"
  }})

@toggleDirection=(img)->
  horizontal= isHorizontal(img)
  $(img).attr("horizontal",horizontal)
#if horizontal then $(img).removeClass("vertical") else $(img).addClass("vertical")

#function called when drag starts
@dragIt=(drop_target,e)->
  e.dataTransfer.setDragImage(e.target, 5, 20)
  e.dataTransfer.setData("Text", drop_target)
  console.log "drop target: #{drop_target}"
  console.log "the target id is:#{e.target.id}"
  console.log "the target is:#{e.target}"

@isHorizontal=(img)->
  if $(img).attr("horizontal")!="true" then true else false

#function called when element drops
@dropIt = (e) ->
  #get a reference to the element being dragged
  data = e.dataTransfer.getData("Text")
  console.log "the id is: #{data}"
  #get the element
  theDraggedElement = document.getElementById(data)
  #add it to the drop element
  e.target.appendChild(theDraggedElement);
  #conn.ws({send:{attack: tile}})
  #instruct the browser to allow the drop
  e.preventDefault()

###
@populateShipYard= ->
 $('#shipyard').append($('<img/>',{
 id:ship+i
 src:"/assets/images/#{ship}.png"
 alt:ship
 draggable: true
 class: "draggableShip"
 # ondragstart:->dragIt(ship+i,event)
 onclick:-> toggleDirection(this)
 horizontal: true
 })) for ship ,i in ships
 $(".draggableShip").draggable({
 containment:"#boardContainer"
 snap: ".My_tile"
 snapMode: "inner"
 revert:"invalid"
 }).click( -> toggleDirection(this) )
 $(".My_tile").droppable({
 drop:( event, ui )->
   alert('ouch')
 })


@populateShipYard= ->
 $('#shipyard').append(createDiv({
   id:ship+i
   draggable: true
   class: "draggableShip"
  # ondragstart:->dragIt(ship+i,event)
   onclick:-> toggleDirection(this)
   horizontal: true
 })) for ship ,i in ships
 $("""##{ship}#{i}""").append($('<img/>',{
   src:"/assets/images/#{ship}.png"
   alt:ship
 })) for ship, i in ships
 $(".draggableShip").draggable({
   containment:"#My_board"
   snap:".My_tile"
   revert:"invalid"
 }).click( -> toggleDirection(this) )
 $(".My_tile").droppable({
   drop:( event, ui )->
     alert('ouch')
 })
###





ships=[["aircraftCarrier",5,"Aircraft carrier"], ["battleship",4,"Battleship"],["destroyer",3,"Destroyer"]
  ["patrolShip",2,"Patrol boat"],["submarine",3,"Submarine"]]

letters = new Array("A","B","C","D","E","F","G","H","I","J")

createDiv = (jsonObj) ->
  $('<div/>', jsonObj)

selectedShip=exports ? this

@populateShipYard= ->
  $('#shipyard').append(createDiv({
  id:ship[0]+i
  draggable: true
  class: "draggableShip"
  tileLength: ship[1]
  shipType:ship[2]
  #ondragstart:->dragIt("#{ship}{i}",event)
  #onclick:-> toggleDirection(this)
  horizontal: true
  })) for ship ,i in ships
  $("""##{ship[0]}#{i}""").append($('<img/>',{
  src:"/assets/images/#{ship[0]}.png"
  alt:ship[0]
  draggable:false
  })) for ship, i in ships
  $div = $('#boardContainer');
  $(".draggableShip").click(-> toggleDirection(this))
  makeDraggable(drag,->dragIt(drag)) for drag in $(".draggableShip")
  makeDroppable(drop,dropShip) for drop in $(".My_tile")

@makeDroppable=(elem, func)->
  addEvent(elem, 'dragover', cancel)
  addEvent(elem, 'dragenter', cancel)
  addEvent(elem, 'drop',func)

@makeDraggable=(elem,func)->
  $(elem).attr("draggable",true)
  addEvent(elem, 'dragstart', func)
  log "make draggable #{elem}"

dropShip=(event)->
  if event.preventDefault then event.preventDefault()
  log event.dataTransfer.getData("text/html")


cancel=(event)->
  if (event.preventDefault) then event.preventDefault()
  return false;

#ondrop='dropIt(event)' ondragover='event.preventDefault()'
###.drag("start",( ev, dd )->
selectedShip=$(this)
console.log $div.offset()
sendResetPosition(selectedShip.attr('shipType'))
dd.limit = $div.offset()
dd.limit.bottom = dd.limit.top + $div.outerHeight() - $( this ).outerHeight()
dd.limit.right = dd.limit.left + $div.outerWidth() - $( this ).outerWidth()
).drag(( ev, dd )->
$(this).css({
 position: 'absolute'
 top: Math.round( dd.offsetY / 32 ) * 32
 left: Math.round( dd.offsetX / 32 ) * 32
})
$.drop({ multi: $(this).attr('tileLength') });
)   ###
###.drop(( ev, dd )->
# $(this).addClass('ship')
tileId=$(this).attr('id').substr(9,2)
console.log "#{selectedShip.attr('shipType')} is  on #{tileId}"
sendBoatPosition(tileId,selectedShip.attr('shipType'),selectedShip.attr('tileLength'))
)
$.drop({ multi: 20 });###

@sendBoatPosition=(tile,ship,length)->
  conn.ws({send:{
  ship: ship
  length:length
  tile:tile
  }})
@sendResetPosition=(ship)->
  conn.ws({send:{
  resetPosition: ship
  }})

@readyToPlay=->
  $(".draggableShip").click(->).drag(->)
  conn.ws({send:{
  ready: "ready"
  }})

@toggleDirection=(img)->
  horizontal= isHorizontal(img)
  $(img).attr("horizontal",horizontal)
  if horizontal then $(img).removeClass("vertical") else $(img).addClass("vertical")

#function called when drag starts
@dragIt=(drop_target,e)->
  log this
  log e
  log   "hola"
###
e.dataTransfer.setDragImage(e.target, 5, 20)
e.dataTransfer.setData("Text", drop_target)
console.log "drop target: #{drop_target}"
console.log "the target id is:#{e.target.id}"
console.log "the target is:#{e.target}"
###

@isHorizontal=(img)->
  if $(img).attr("horizontal")!="true" then true else false

#function called when element drops
@dropIt = (e) ->
  #get a reference to the element being dragged
  data = e.dataTransfer.getData("Text")
  console.log "the id is: #{data}"
  #get the element
  theDraggedElement = document.getElementById(data)
  #add it to the drop element
  e.target.appendChild(theDraggedElement);
  #conn.ws({send:{attack: tile}})
  #instruct the browser to allow the drop
  e.preventDefault()

log=(msg)->
  console.log msg

###
@populateShipYard= ->
 $('#shipyard').append($('<img/>',{
 id:ship+i
 src:"/assets/images/#{ship}.png"
 alt:ship
 draggable: true
 class: "draggableShip"
 # ondragstart:->dragIt(ship+i,event)
 onclick:-> toggleDirection(this)
 horizontal: true
 })) for ship ,i in ships
 $(".draggableShip").draggable({
 containment:"#boardContainer"
 snap: ".My_tile"
 snapMode: "inner"
 revert:"invalid"
 }).click( -> toggleDirection(this) )
 $(".My_tile").droppable({
 drop:( event, ui )->
   alert('ouch')
 })


@populateShipYard= ->
 $('#shipyard').append(createDiv({
   id:ship+i
   draggable: true
   class: "draggableShip"
  # ondragstart:->dragIt(ship+i,event)
   onclick:-> toggleDirection(this)
   horizontal: true
 })) for ship ,i in ships
 $("""##{ship}#{i}""").append($('<img/>',{
   src:"/assets/images/#{ship}.png"
   alt:ship
 })) for ship, i in ships
 $(".draggableShip").draggable({
   containment:"#My_board"
   snap:".My_tile"
   revert:"invalid"
 }).click( -> toggleDirection(this) )
 $(".My_tile").droppable({
   drop:( event, ui )->
     alert('ouch')
 })
###
