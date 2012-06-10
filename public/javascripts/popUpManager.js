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

function popUp(username,msg){
  	document.getElementById('popup').innerHTML="<img onclick=\"popUpClose();\" src=\"/assets/images/close.png\"/><h2>username+you+msg</h2>";
	toggle();
}
function popUpClose(){
	toggle();
}

function toggleVisibility(elem)
    {
            var control = document.getElementById(elem);
            if(control.style.display == "none" || control.style.display == "" ){
                control.style.display = "block";
            }else{
                control.style.display = "none";
            }
    }