function setUsername() {
    document.getElementById("player-username").innerHTML = localStorage.getItem("username");
}

function createGame() {
    fetch("/api/game/create", {
        method: "POST"
    }).then(async res => {
        if (res.ok) {
            let response = await res.json();
            let gameCode = response['game'];
            window.localStorage.setItem("game", gameCode);
            window.location.replace(`/g/${gameCode}`);
        }
    })
}

function redirectToGame() {
    let gameCode = prompt("Game Code").toUpperCase();

    let data = new FormData();
    data.append("game_id", gameCode);
    fetch("/api/game/join", {
        method: "POST",
        body: data
    }).then(res => {
        if (res.ok) {
            window.localStorage.setItem("game", gameCode);
            window.location.replace(`/g/${gameCode}`);
        }
    })
}