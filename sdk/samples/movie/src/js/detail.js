/*
 * Copyright (c) 2022.  NESP Technology.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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