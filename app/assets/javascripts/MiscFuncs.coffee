ships=[["aircraftCarrier",5], ["battleship",4],["destroyer",4]
  ["patrolShip",2],["submarine",3]]

createDiv = (jsonObj) ->
  $('<div/>', jsonObj)

selectedShip=exports ? this

@populateShipYard= ->
  $('#shipyard').append(createDiv({
  id:ship[0]+i
  draggable: true
  class: "draggableShip"
  tileLength: ship[1]
  shipType:ship[0]
  #ondragstart:->dragIt("#{ship}{i}",event)
  #onclick:-> toggleDirection(this)
  horizontal: true
  })) for ship ,i in ships
  $("""##{ship[0]}#{i}""").append($('<img/>',{
  src:"/assets/images/#{ship[0]}.png"
  alt:ship[0]
  })) for ship, i in ships
  $div = $('#boardContainer');
  $(".draggableShip").click(-> toggleDirection(this)).drag("start",( ev, dd )->
    selectedShip=$(this)
    sendResetPosition(selectedShip.attr('shipType'))
    dd.limit = $div.offset();
    dd.limit.bottom = dd.limit.top + $div.outerHeight() - $( this ).outerHeight();
    dd.limit.right = dd.limit.left + $div.outerWidth() - $( this ).outerWidth();
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
    $(this).css({background:'green'})
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

@toggleDirection=(img)->
  horizontal= isHorizontal(img)
  $(img).attr("horizontal",horizontal)
  if horizontal then $(img).removeClass("vertical") else $(img).addClass("vertical")

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
