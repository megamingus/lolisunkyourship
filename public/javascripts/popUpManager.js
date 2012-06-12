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
    console.log("el mensaje es"+msg+"y el username es"+username);


    document.getElementById("result").textContent=username+" you "+msg+"!";
    document.getElementById("okButton").onclick="location.href='http://localhost:9000/room?username="+username+"'\"";
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