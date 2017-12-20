var count = 10; //每次获取数据长度
/**
 * 滚动翻页 （自定义实现此方法）
 * myScroll.refresh();		// 数据加载完成后，调用界面更新方法
 */
function pullUpAction(el, myScroll) {
	var pullUpEl = $(el).find('.pullUp')[0];
	var ulList = $(el).find('.scroller-list');
	/*ajax 获得data 追加数据*/
	$.ajax({
		url: '../../ajax_tpl/'+$(el).data('url'),
		type: "GET",
		data: {
			index: $(ulList).find('li').size(), //当前数据条数
			count: count
		},
		dataType: "json",
		success: function(data, textStatus) {
			//模拟延迟
			setTimeout(function() {
				setList(ulList, data);
				pullUpEl.className = ' p10 tc cl666 f12 pullUp';
				pullUpEl.innerHTML = '向上滑动查看更多';
				myScroll.refresh();
			}, 500)
		},
		error: function(XMLHttpRequest, textStatus, errorThrown) {
			alert(errorThrown);
		},
		complete: function(XMLHttpRequest, textStatus) {}
	});

}

function initIscroll(id) {
	var pullUpEl = $(id).find('.pullUp')[0];
	var myScroll = new IScroll(id, {
		probeType: 3,
		mouseWheel: true,
		click: true
	});
	myScroll.on('scroll', function() {
		if (this.y < (this.maxScrollY - 25) && !pullUpEl.className.match('flip')) {
			pullUpEl.className = 'flip p10 tc cl666 f12 pullUp';
			pullUpEl.innerHTML = '松开开始加载...';
		} else if (this.y > (this.maxScrollY) && pullUpEl.className.match('flip')) {
			pullUpEl.className = ' p10 tc cl666 f12 pullUp';
			pullUpEl.innerHTML = '向上滑动查看更多';
		}
	});
	myScroll.on('scrollEnd', function() {
		if (pullUpEl.className.match('flip')) {
			pullUpEl.className = 'loading p10 tc cl666 f12 pullUp';
			pullUpEl.innerHTML = '加载中...';
			pullUpAction($(id), myScroll); // Execute custom function (ajax call?)
		}
	});
}
/**
 * 初始化iScroll控件
 */
function loaded() {
	initIscroll('#wrapper1');
	$('#all').addClass('active');
	initIscroll('#wrapper2');
	$('#all').removeClass('active');
}

//初始化绑定iScroll控件 
document.addEventListener('touchmove', function(e) {
	e.preventDefault();
}, false);
document.addEventListener('DOMContentLoaded', loaded, false);