(function () {
    const episodesEle = document.querySelector('.swiper-wrapper').querySelectorAll('li > a:not([rel])');
    const episodes = [];
    for (const ele of episodesEle) {
        episodes.push({
            title: ele.innerText,
            pageUrl: ele.href,
        });
    }

    const linesEle = document.querySelectorAll('#DialogSid > div > div > div > ul > li > a');
    const linesMeta = [];
    for (const ele of linesEle) {
        linesMeta.push({
            title: ele.innerText,
            id: ele.href.match(/-(\d+)-/)[1],
        });
    }

    const lines = [];
    for (const m of linesMeta) {
        lines.push({
            title: m.title,
            episodes: episodes.map(e => ({ ...e, pageUrl: e.pageUrl.replace(/-(\d+)-/, `-${m.id}-`) })),
        });
    }

    return {
        playLines: lines,
    };
})()