function changeTab(toTab)
{
    //make all tabs inviseble
    let currentActive = document.getElementsByClassName("active-content");
    for(tab of currentActive)
        tab.classList.replace("active-content","content");
    document.getElementById(toTab).classList.replace("content","active-content");

}


document.addEventListener('DOMContentLoaded', footballGame());
function footballGame()
{
    let football = document.getElementById("football");
    football.style.display='block';
    football.addEventListener('click', function (){
        football.style.display='none';
        moveFootball();
        setTimeout(function(){
            football.style.display='block';
            let recBall = football.getBoundingClientRect();
            let recGoal = document.getElementById("goal").getBoundingClientRect();
            if(recBall.left>=recGoal.left && recBall.bottom<=recGoal.bottom)
                alert("you won the game");
            }
            ,2000);
    })
}


function moveFootball(){
    let football=document.getElementById("football");
    let randomHor=Math.floor(Math.random()*(90))+1;
    let randomVertical=Math.floor(Math.random()*(90))+1;
    football.style.right=randomHor+'%';
    football.style.bottom=randomVertical+'%';

}


function thankYou(){
    alert("thank you for sharing");
}