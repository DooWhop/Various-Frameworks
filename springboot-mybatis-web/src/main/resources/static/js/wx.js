/*(c) Copyright 2015 wangxiang. All Rights Reserved.  * */

/**
 * js调app原生 初始化方法1 必须有此段代码
 */
function setupWebViewJavascriptBridge(callback) {
    if (window.WebViewJavascriptBridge) { 
    	return callback(WebViewJavascriptBridge); 
    }
    if (window.WVJBCallbacks) { 
    	return window.WVJBCallbacks.push(callback); 
    }
    window.WVJBCallbacks = [callback];
    var WVJBIframe = document.createElement('iframe');
    WVJBIframe.style.display = 'none';
    WVJBIframe.src = 'wvjbscheme://__BRIDGE_LOADED__';
    document.documentElement.appendChild(WVJBIframe);
    setTimeout(function() { document.documentElement.removeChild(WVJBIframe) }, 0)
}

/**
 * js调app原生 初始化方法2 必须有此段代码
 */
setupWebViewJavascriptBridge(function(bridge) {
})

var boolBack;
var boolExit;

function back() {
	if(boolBack)
		return;
	boolBack = true;
	var userAgentInfo = navigator.userAgent.toLowerCase();
	var Agents = ["android", "adr","iphone","ipad", "ipod","symbianos", "windows phone"];
	var isMobile = false;
	for (var i = 0; i < Agents.length; i++) {
	    if (userAgentInfo.indexOf(Agents[i]) > -1) {
	    	isMobile = true;
	    }
	}
	if (isMobile && window.WebViewJavascriptBridge){
		WebViewJavascriptBridge.callHandler('onNativeBack', {}, function(response) {});
	}else if(window.history.length > 1) {
		window.history.back();
	}
}

function exit() {
	if(boolExit)
		return;
	boolExit = true;
	var userAgentInfo = navigator.userAgent.toLowerCase();
	var Agents = ["android", "adr","iphone","ipad", "ipod","symbianos", "windows phone"];
	var isMobile = false;
	for (var i = 0; i < Agents.length; i++) {
	    if (userAgentInfo.indexOf(Agents[i]) > -1) {
	    	isMobile = true;
	    }
	}
	if (isMobile && window.WebViewJavascriptBridge){
		WebViewJavascriptBridge.callHandler('onNativeExit', {}, function(response) {});
	}else if(window.history.length > 1) {
		window.history.go(-1);
	}
}

/*
 按钮绑定按下样式
 * */
$('body').on('touchstart', '.wx-btn', function() {
	$(this).addClass('wx-active');
})
$('body').on('touchend', '.wx-btn', function() {
	$(this).removeClass('wx-active');
})

$(function() {
	/*
	 	radio 控件
	 * */
	$('.wx-radio-box > span').click(function() {
		$(this).addClass('active');
		$(this).siblings().removeClass('active');
	})
	$('.wx-label li').click(function() {
		$(this).addClass('active');
		$(this).siblings().removeClass('active');
	})
	$('body').on('change', '.wx-select', function() {
		$(this).siblings('.wx-form-text').html($(this).find("option:selected").text())
		if($(this).val() != '') {
			$(this).siblings('.wx-form-text').addClass('cl3')
		} else {
			$(this).siblings('.wx-form-text').removeClass('cl3')
		}

	})
	//新增导航返回按钮
	$('.wx-action-back').tap(function(){
		back();
	})
	
	initSelect();
	/*
	 * 半圆进度条
	 */
	setTimeout(function() {
		$('.wx-circle').each(function(index, el) {
			var num = $(this).find('span.num').text() * 1.8;
			var speed = 200;
			$(this).find('.left').css('-webkit-transition-duration', num / speed + "s");
			$(this).find('.left').css('transform', "rotate(" + (num - 180) + "deg)");
		});
	}, 200)

	setSteps($('.wx-step-line ul'));
});

function initSelect() {
	$('.wx-select').each(function() {
		$(this).siblings('.wx-form-text').html($(this).find("option:selected").text())
		if($(this).val() != '') {
			$(this).siblings('.wx-form-text').addClass('cl3')
		} else {
			$(this).siblings('.wx-form-text').removeClass('cl3')
		}
	})

}

function setSteps(obj) {
	if ($(obj).size()<=0) {
		return;
	}
	var steps = $(obj).data("steps").split(',');
	var current = $(obj).data("current");
	var tem = "";
	for(var i = 0; i < steps.length; i++) {
		if(i < current - 1) {
			tem += '<li class="old"><i>' + steps[i] + '</i></li>';
		} else if(i == current - 1) {
			tem += '<li class="active"><i>' + steps[i] + '</i></li>';
		} else if(i < steps.length) {
			tem += '<li><i>' + steps[i] + '</i></li>';
		} else {
			tem += '<li></li>';
		}
	}
	$(obj).html(tem)
}

/**
 * Author: Sergey Bondarenko (BR0kEN)
 * E-mail: broken@propeople.com.ua
 * Github: https://github.com/BR0kEN-/jTap
 * Updated: June 2, 2014
 * Version: 0.2.9
 */
(function($, _) {
	'use strict';
	var ev = {
		start: 'touchstart mousedown',
		end: 'touchend mouseup'
	};

	$.event.special[_] = {
		setup: function() {
			$(this).off('click').on(ev.start + ' ' + ev.end, function(e) {
				ev.E = e.originalEvent.changedTouches ? e.originalEvent.changedTouches[0] : e;
			}).on(ev.start, function(e) {
				if(e.which && e.which !== 1) {
					return;
				}
				ev.target = e.target;
				ev.time = new Date().getTime();
				ev.X = ev.E.pageX;
				ev.Y = ev.E.pageY;
			}).on(ev.end, function(e) {
				if(
					ev.target === e.target &&
					((new Date().getTime() - ev.time) < 750) &&
					(ev.X === ev.E.pageX && ev.Y === ev.E.pageY)
				) {
					e.type = _;
					e.pageX = ev.E.pageX;
					e.pageY = ev.E.pageY;

					$.event.dispatch.call(this, e);
				}
			});
		},
		remove: function() {
			$(this).off(ev.start + ' ' + ev.end);
		}
	};
	$.fn[_] = function(fn) {
		return this[fn ? 'on' : 'trigger'](_, fn);
	};
})(jQuery, 'tap');
/*
 * tab切换页面 
 */
$('.wx-tab li').tap(function() {
	$(this).siblings().removeClass('active');
	$(this).addClass('active');
	$(this).parent().find('i').css('left', $(this).index() / $(this).parent().find('li').size() * 100 + '%')
	var aria = $('#' + $(this).attr('wx-aria-controls'));
	$(aria).siblings('.wx-tab-pane').removeClass('active');
	$(aria).addClass('active');
})
$('.wx-tab i').each(function() {
	$(this).css('left', $(this).siblings('li.active').index() / $(this).parent().find('li').size() * 100 + '%')
})

/*
 * toast 提示浮层
 */
function alert(str, type, showTime) {
	$('.wx-tips').remove();
	$('body').append('<div class="wx-tips wx-tips-' + type + '">' +
		'<div class="wx-tips-text">' +
		str +
		'</div>' +
		'</div>');
	$('.wx-tips').show();
	setTimeout(function() {
		$('.wx-tips').fadeOut(100);
	}, showTime ? showTime : 2000)
}

function showLoading() {
	$('.wx-tips').remove();
	$('body').append('<div class="wx-tips">' +
		'<div class="wx-tips-text">' +
		str +
		'</div>' +
		'</div>');
	$('.wx-tips').show();
	setTimeout(function() {
		$('.wx-tips').fadeOut(100);
	}, 2000)
}
//发送验证码

var countdown = 60;
var sett;

/*$('.getSMS').click(function() {
	//此处调用发送短信接口
	//console.log('此处调用发送短信接口')
	$('.SMSTips').show();
	settime($(this));
});*/

function settime(obj) {
	if(countdown == 0) {
		$(obj).html('立即获取');
		$(obj).addClass('clh');
		$('.getSMS').click(function() {
			settime($(this));
		});
		countdown = 60;
	} else {
		$(obj).unbind(); //移除所有 
		$(obj).removeClass('clh');
		$(obj).addClass('cl3c');
		$(obj).html("重新获取(" + countdown + ")");
		countdown--;
	}
	clearInterval(sett);
	sett = setTimeout(function() {
		settime($(obj))
	}, 1000)
}
//loading
function showLoading() {
	$('body').append('<div class="wx-tips" id="loading" style="display: block;">' +
		'<div style="width: 100px;height: 100px;margin: 40% auto;">' +
		'<img style="width: 100%;" src="../../images/loading.gif" />' +
		'</div>' +
		'</div>');
	$('#loading').show();
}

function hideLoading() {
	$('#loading').remove();
}
/*邮箱检验*/
isEmail();

function isEmail(obj) {
	var pattern = /^([\.a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(\.[a-zA-Z0-9_-]+)+$/;
	if(!pattern.test($(obj).val())) {
		return false;
	}
	return true;
}
/*手机校验*/
function isPhoneNo(obj) {
	var pattern = /^1\d{10}$/;
	if(!pattern.test($(obj).val())) {
		return false;
	}
	return true;
}
/*拼音检验*/
function isPY(obj) {
	var pattern = /^([a-zA-Z ])+/;
	if(!pattern.test($(obj).val())) {
		return false;
	}
	return true;
}