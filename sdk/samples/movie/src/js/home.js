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
    let homePage = runtime.createHomePage();
   
    // 轮播图
    let slideContainer = document.getElementsByClassName("flickity-slider")[1];
    let slideItemContainers = slideContainer.getElementsByClassName("col-md-2 col-xs-1 list");
    for (let i = 0; i < slideItemContainers.length; i++) {
        var slideItem = slideItemContainers[i].getElementsByTagName("a")[0];
        var movie = runtime.createMovie();
        movie.name = slideItem.getAttribute("title");
        movie.status = movie.name;
        movie.coverImageUrl = (/[a-zA-z]+:\/\/[^\s^\"]*/g).exec(slideItem.style.background)[0];
        movie.detailUrl = slideItem.href;
        homePage.slideMovies.push(movie);
    }

    // 添加各分类部分
    for (let i = 0; i < 4; i++) {
        getMovies(i, document.querySelector("body > div:nth-child(3) > div > div:nth-child(" + (2 + i) + ")"))
    }

    function getMovies(i, container) {
        let itemEles = container.getElementsByClassName("stui-vodlist clearfix")[0]
            .getElementsByTagName("li");
        for (let i = 0; i < itemEles.length; i++) {
            let itemEle = itemEles[i].getElementsByTagName("a")[0];
            var movieItem = runtime.createMovie();
            movieItem.name = itemEle.getAttribute("title");
            movieItem.detailUrl = itemEle.href;
            movieItem.coverImageUrl = itemEle.getAttribute("data-original");
            movieItem.status = itemEle.getElementsByTagName("span")[1].innerText;
            if (i == 0) {
                homePage.newMovie.push(movieItem);
            } else if (i == 1) {
                homePage.newSoap.push(movieItem);
            } else if (i == 2) {
                homePage.newVariety.push(movieItem);
            } else if (i == 3) {
                homePage.newAnim.push(movieItem);
            }
        }
    }

    // 直接返回Page 或调用 runtime.sendPage(homePage);
    return JSON.stringify(homePage);
}
