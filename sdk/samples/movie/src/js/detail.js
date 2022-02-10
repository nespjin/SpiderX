function JsRuntime_LoadPage() {
    var page = runtime.createMovie();
    var name = document.querySelector("body > div:nth-child(3) > div > div.col-lg-wide-75.col-xs-1.padding-0 > div:nth-child(1) > div > div:nth-child(2) > div.stui-content__detail > h1").textContent;
    page.name = name;
    page.detail = document.querySelector("body > div:nth-child(3) > div > div.col-lg-wide-75.col-xs-1.padding-0 > div:nth-child(1) > div > div:nth-child(2) > div.stui-content__detail")
        .textContent.trimStart().trimEnd()
        .replace(name, "")
        .replace(
            document.querySelector("body > div:nth-child(3) > div > div.col-lg-wide-75.col-xs-1.padding-0 > div:nth-child(1) > div > div:nth-child(2) > div.stui-content__detail > p.desc.hidden-xs").textContent,
            "")
        .replace("\n\n\n立即播放", "");
    page.detail += "内容简介: \n" + document.querySelector("#desc > div > div.stui-pannel_bd > p").textContent
     
    // PlayLines
    let playLineElements = document.getElementsByClassName("stui-pannel-box b playlist mb");
    for (let i = 0; i < playLineElements.length; i++) {
        let playLineElement = playLineElements[i].querySelector("div.stui-pannel_bd.col-pd.clearfix > ul");
        let episodeElements = playLineElement.getElementsByTagName("a");

        var playLine = runtime.createPlayLine();

        for (let j = 0; j < episodeElements.length; j++) {
            let episodeElement = episodeElements[j];
            var episode = runtime.createEpisode();
            episode.title = episodeElement.textContent;
            episode.pageUrl = episodeElement.href;
            playLine.episodes.push(episode);
        }

        page.playLines.push(playLine);
    }

    return JSON.stringify(page);
}