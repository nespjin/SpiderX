(function () {
    const detailsEle = document.querySelector('body > div.page > div > div.page-row > div.panel.play > div > div > div.shoutu-media-bd');
    const detailsPanelEle = document.querySelector('#DialogDesc > div > div > div');
    const episodesEle = document.querySelectorAll('body > div.page > div > div.page-row > div:nth-child(5) > div.panel-bd > ul > li > a:not([rel])');
    return {
        name: detailsEle.querySelector('h1').innerText,
        introduction: detailsPanelEle.querySelector('p').innerText,
        detail: [...detailsPanelEle.querySelectorAll('div:not(.hr)')].map(ele => ele.innerText).join('\n'),
        _nextDatasetUrl: episodesEle.length > 0 ? episodesEle[episodesEle.length - 1].href : null,
    };
})()