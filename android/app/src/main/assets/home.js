// kkcechi.com
(function() {
	const sliders = document.querySelector('div.swiper-wrapper').querySelectorAll('div.swiper-slide');
	const sliderMovies = [];
	const originUrl = window.location.origin;
	for (const slider of sliders) {
		sliderMovies.push({
			name: slider.querySelector('h2').innerText,
			coverImageUrl: slider.querySelector('img').src,
			detail: slider.querySelector('a.poster-img').href
		})
	}

	function querySectionMovies(query) {
		const elements = document.querySelectorAll(query);
		const movies = [];
		for (const newPlay of elements) {
			const div = newPlay.querySelector('div.cover-data');
			const a = div.querySelector('h4 > a');
			movies.push({
				name: a.innerText,
				coverImageUrl: originUrl + newPlay.querySelector('img').getAttribute('data-original'),
				detail: a.href,
				status: div.querySelector('p.text').innerText
			})
		}
		return movies
	}
	const newPlayMovies = querySectionMovies('body > div.page > div > div.page-row > div:nth-child(3) > div.panel-bd > ul > li');
	const newMovies = querySectionMovies('body > div.page > div > div.page-row > div:nth-child(5) > div.panel-bd > ul > li');
	const newSoaps = querySectionMovies('body > div.page > div > div.page-row > div:nth-child(6) > div.panel-bd > ul > li');
	const newVariety = querySectionMovies('body > div.page > div > div.page-row > div:nth-child(8) > div.panel-bd > ul > li');
	const newAnim = querySectionMovies('body > div.page > div > div.page-row > div:nth-child(6) > div.panel-bd > ul > li');
	return {
		slideMovies: sliderMovies,
		newPlay: newPlayMovies,
		newMovie: newMovies,
		newSoap: newSoaps,
		newVariety: newVariety,
		newAnim: newAnim
	}
})()
