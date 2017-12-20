$(function() {
	setTimeout(function(){
		$('.circle').each(function(index, el) {
		var num = $(this).find('span.num').text() * 3.6;
		var speed = 250;
		if (num <= 180) {
			$(this).find('.right').css('-webkit-transition-duration', num / speed + "s");
			$(this).find('.right').css('transform', "rotate(" + num + "deg)");
		} else {
			$(this).find('.right').css('-webkit-transition-duration', 180 / speed + "s");
			$(this).find('.right').css('transform', "rotate(180deg)");
			var this1 = $(this);
			setTimeout(function() {
				$(this1).find('.left').css('-webkit-transition-duration', (num - 180) / speed + "s");
				$(this1).find('.left').css('transform', "rotate(" + (num - 180) + "deg)");
			}, 180 / speed * 1000)
		};
	});
	},200)
});