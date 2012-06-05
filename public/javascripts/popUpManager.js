/**
 * Created with IntelliJ IDEA.
 * User: Mary Anne
 * Date: 6/5/12
 * Time: 5:09 PM
 * To change this template use File | Settings | File Templates.
 */
function toggle(){
        toggleVisibility('popup');
        toggleVisibility('blackback');
}

function popUp(msg){
    console.log("Hey!! POP!!")
	document.getElementById('popup').innerHTML="<h2>@username+" you "+msg</h2>";
	toggle();
}
function popUpClose(){
	toggle();
}