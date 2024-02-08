function loadGetMsg() {
    let nameMovie = document.getElementById("nameMovie").value;
    const xhttp = new XMLHttpRequest();
    xhttp.onload = function () {
        let movie = JSON.parse(this.responseText);
        if (movie.Response === "False" ) {
            document.getElementById("getrespmsg").innerHTML = "<h1>Movie not found</h1>";
        } else {
            document.getElementById("getrespmsg").innerHTML = `
                <h2>${movie.Title}</h2>
                <img src="${movie.Poster}" alt="Poster" style="width: 200px;">
                <p>Year: ${movie.Year}</p>
                <p>Rated: ${movie.Rated}</p>
                <p>Released: ${movie.Released}</p>
                <p>Runtime: ${movie.Runtime}</p>
                <p>Genre: ${movie.Genre}</p>
                <p>Director: ${movie.Director}</p>
            `;
        }
    }
    xhttp.open("GET", "/movie?name=" + nameMovie);
    xhttp.send();
}

