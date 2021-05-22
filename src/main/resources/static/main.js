function submitUsername() {
    let username = document.getElementById("username").value;

    if (/^[0-9A-Za-z][0-9A-Za-z ]*/.test(username) && username.length > 1) {
        let formData = new FormData();
        formData.append("username", username);
        fetch("/api/player/username", {
            method: "POST",
            body: formData
        }).then(res => {
            if (res.ok) {
                window.localStorage.setItem("username", username);
                window.location.replace("/welcome");
            }
        })
    } else {
        alert("Invalid username");
    }
}