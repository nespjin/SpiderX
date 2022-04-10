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
    var page = runtime.createCategoryPage();
    // Categories
    let categoryGroupElements = document.getElementsByClassName("stui-screen__list type-slide bottom-line-dot clearfix flickity-enabled is-draggable");
    for (let i = 0; i < categoryGroupElements.length; i++) {
        var movieCategoryGroupElement = categoryGroupElements[i];
        var movieCategoryGroup = runtime.createMovieCategoryGroup();
        var movieCategoriesElements = movieCategoryGroupElement.getElementsByTagName("li");
        for (let j = 1; j < movieCategoriesElements.length; j++) {
            var movieCategoriesElement = movieCategoriesElements[j];
            var movieCategory = runtime.createMovieCategory();
            movieCategory.title = movieCategoriesElement.textContent;
            movieCategory.pageUrl = movieCategoriesElement.getElementsByTagName("a")[0].href;
            movieCategoryGroup.movieCategories.push(movieCategory);
        }
        page.movieCategoryGroups.push(movieCategoryGroup);
    }
    // Movies
    var movieItemElements = document.getElementsByClassName("stui-vodlist clearfix")[0].getElementsByTagName("li");
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
