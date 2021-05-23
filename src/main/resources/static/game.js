let players = [];
let teeth   = [];
let player_id_username_map = {};
let currentTurn = undefined;
let badTooth = undefined;


window.setInterval(pollGame, 3000);


function resetGame() {
    let data = new FormData();
    data.append("action_name", "RESET_GAME");

    fetch("/api/game/action", {
        method: "POST",
        body: data
    }).then(async res => {
        let json = await res.json();
        console.log(json);
    })
}


function renderPlayers() {
    let lst = document.getElementById("player-list");
    lst.innerHTML = "";

    players.forEach(p => {
        if (player_id_username_map[p] === undefined) {
            let curr = document.createElement("li");
            curr.innerText = p;
            if (p === currentTurn)
                curr.classList.add("bold");
            lst.appendChild(curr);

            retrieveUsernameOfID(p);
        } else {
            let curr = document.createElement("li");
            curr.innerText = player_id_username_map[p];
            if (p === currentTurn)
                curr.classList.add("bold");
            lst.appendChild(curr);
        }
    })
}

function renderTeeth() {
    let buttons = document.getElementById("teeth-list");
    buttons.innerHTML = "";

    teeth.forEach(t => {
        let button = document.createElement("button");
        button.innerHTML = t['number'];
        button.classList.add("tooth");

        if (badTooth !== undefined && t['number'] === badTooth) {
            button.classList.add("red");
        }

        if (t['available'] === false) {
            button.disabled = true;
        } else {
            button.onclick = (_ => pickTooth(t['number']));
        }

        buttons.appendChild(button);
    })
}

function pickTooth(num) {
    let data = new FormData();

    data.append("action_name", "PICK_TOOTH");
    data.append("params", JSON.stringify({ "tooth_number": num }));

    fetch("/api/game/action", {
        method: "POST",
        body: data
    }).then(async res => {
        let json = await res.json();
        console.log(json);
        if (json['state'] === "FINISHED") {
            document.getElementById(`tooth-${num}`).classList.add("red");
        }
    })
}

function pollGame() {
    fetch("/api/game").then(async res => {
        let json = await res.json();
        players = json['players'];
        document.getElementById("current-status").innerHTML = json['state'];

        if (json['state'] === "INITIALIZING") {
            badTooth = undefined;
            currentTurn = undefined;
            teeth = [];
        }

        if (json['position']['CurrentTurn'] !== undefined) {
            currentTurn = json['position']['CurrentTurn'];
        }
        if (json['position']['X'] !== undefined) {
            badTooth = json['position']['X'];
        }
        if (json['position']['TeethList'] !== undefined) {
            teeth = json['position']['TeethList'];
            renderTeeth();
        }

        renderPlayers();

        console.log(json);
    });
}

function retrieveUsernameOfID(id) {
    let data = new FormData();
    data.append("user_id", id);

    fetch("/api/player/info", {
        method: "POST",
        body: data
    }).then(async res => {
        let json = await res.json();
        player_id_username_map[id] = json['username'];
    })
}

function startGame() {
    let data = new FormData();
    data.append("action_name", "START_GAME");

    fetch("/api/game/action", {
        method: "POST",
        body: data
    }).then(async res => {
        let json = await res.json();
    })
}