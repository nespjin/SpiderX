(function () {
	const groupEles = document.querySelector('body > div.page > div > div.page-row > div:nth-child(3) > div').querySelectorAll('div.shoutu-screen');
	const groups = [];
	for (const ele of groupEles) {
		const categoryEles = ele.querySelectorAll('li.swiper-slide');
		const categories = [...categoryEles].map((item) => item.querySelector('a')).filter((item) => item).map((item) => ({
			title: item.innerText,
			pageUrl: item.href,
		}));
		groups.push({
			title: ele.querySelector('div:nth-child(1)').innerText,
			categories: categories
		})
	}
	const movieEles = document.querySelector('body > div.page > div > div.page-row > div:nth-child(4) > div.panel-bd > ul.shoutu-vodlist').querySelectorAll('li');
	const movies = [];
	const originUrl = window.location.origin;
	for (const ele of movieEles) {
		const a = ele.querySelector('a');
		movies.push({
			name: a.title,
			coverImageUrl: originUrl + ele.querySelector('img').getAttribute('data-original'),
			detail: a.href,
			status: ele.querySelector('p.text').innerText
		})
	}
	let nextPageUrl = '';
	const pageContainer = document.querySelector('body > div.page > div > div.page-row > div:nth-child(4) > div.panel-bd > ul.shoutu-page.text-center');
	if (pageContainer) {
		const pagePos = pageContainer.querySelector('li.visible-xs').innerText;
		if (pagePos.match(/\\d+/)[0] < pagePos.match(/\\d+/g)[1]) {
			const pageEles = pageContainer.querySelectorAll('li');
			for (const ele of pageEles) {
				if (ele.innerText === '下一页') {
					nextPageUrl = ele.querySelector('a').href;
					break
				}
			}
		}
	}
	return {
		movieCategoryGroups: groups,
		movies: movies,
		nextPageUrl: nextPageUrl
	}
})()
