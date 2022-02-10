function JsRuntime_LoadPage() {
    var page = runtime.createSearchPage();
    // Movies
    var movieItemElements = document.getElementsByClassName("stui-vodlist__media col-pd clearfix")[0].getElementsByTagName("li");
    for (let i = 0; i < movieItemElements.length; i++) {
        let movieItemElement = movieItemElements[i].getElementsByTagName("a")[0];
        let movie = runtime.createMovie();
        movie.name = movieItemElement.getAttribute("title");
        // movie.coverImageUrl = (/[a-zA-z]+:\/\/[^\s^\"]*/g).exec(movieItemElement.getAttribute("style"))[0];
        movie.coverImageUrl = movieItemElement.getAttribute("data-original");
        movie.detailUrl = movieItemElement.href;
        try {
            movie.status = movieItemElement.getElementsByTagName("span")[1].textContent;
        } catch (error) {

        }
        page.movies.push(movie);
    }

    try {
        document.querySelectorAll("body > div:nth-child(3) > div > div.col-lg-wide-75.col-xs-1.padding-0 > ul > li > a").forEach(function (a, b) {
            if (a.textContent === "下一页" && a.href !== document.URL) page.nextPageUrl = a.href;
        });
    } catch (error) {
    }

    return JSON.stringify(page);
}