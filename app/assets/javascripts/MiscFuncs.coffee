ships=[["aircraftCarrier",5], ["battleship",4],["destroyer",4]
  ["patrolShip",2],["submarine",3]]

createDiv = (jsonObj) ->
  $('<div/>', jsonObj)

  ###
jQuery(function($){
   $('.drag')
      .drag("start",function( ev, dd ){
         $( this ).css('opacity',.75);
      })
      .drag(function( ev, dd ){
         $( this ).css({
            top: dd.offsetY,
            left: dd.offsetX
         });
      })
      .drag("end",function( ev, dd ){
         $( this ).css('opacity','');
      });
   $('.drop td')
      .drop("start",function(){
         $( this ).addClass("active");
      })
      .drop(function( ev, dd ){
         $( this ).toggleClass("dropped");
      })
      .drop("end",function(){
         $( this ).removeClass("active");
      });
   $.drop({ multi: true });
});###

###
.drag(function( ev, dd ){
         $( this ).css({
            top: dd.offsetY,
            left: dd.offsetX
         });
      })
###

@populateShipYard= ->
  $('#shipyard').append(createDiv({
  id:ship[0]+i
  draggable: true
  class: "draggableShip"
  tiles: ship[1]
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
    dd.limit = $div.offset();
    dd.limit.bottom = dd.limit.top + $div.outerHeight() - $( this ).outerHeight();
    dd.limit.right = dd.limit.left + $div.outerWidth() - $( this ).outerWidth();
  ).drag(( ev, dd )->
    $(this).css({
      position: 'absolute'
      top: dd.offsetY
      left: dd.offsetX
      top: Math.min( dd.limit.bottom, Math.max( dd.limit.top, dd.offsetY ) )
      left: Math.min( dd.limit.right, Math.max( dd.limit.left, dd.offsetX ) )
    })
    $.drop({ multi: $(this).attr('tiles') });
  )
  $(".My_tile").drop(( ev, dd )->
    $(this).css({background:'green'})
    console.log $(this).attr('id').substr(9,2)
  )
  $.drop({ multi: 20 });



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
