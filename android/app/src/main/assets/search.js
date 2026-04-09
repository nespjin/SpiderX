(function () {
    const movieEles = document.querySelector('body > div.page > div > div.page-row > div.panel > div.panel-bd > ul').querySelectorAll('li');
    const movies = [];
    for (const ele of movieEles) {
        const a = ele.querySelector('a');
        movies.push(
            {
                name: a.title,
                coverImageUrl: ele.querySelector('img').src,
                detail: a.href,
                status: ele.querySelector('p.text').innerText
            }
        );
    }

    let nextPageUrl = '';
    const pageContainer = document.querySelector('body > div.page > div > div.page-row > div.panel > ul');
    if (pageContainer) {
        const pagePos = pageContainer.querySelector('li.visible-xs').innerText;

        if (pagePos.match(/\d+/)[0] < pagePos.match(/\d+/g)[1]) {
            const pageEles = pageContainer.querySelectorAll('li');
            for (const ele of pageEles) {
                if (ele.innerText === '下一页') {
                    nextPageUrl = ele.querySelector('a').href;
                    break;
                }
            }
        }
    }
    return {
        movies: movies,
        nextPageUrl: nextPageUrl
    };
})()
