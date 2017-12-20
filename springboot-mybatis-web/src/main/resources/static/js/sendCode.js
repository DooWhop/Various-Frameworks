var sends = {
	
	InterValObj: null, //timer变量，控制时间
	count: 60, //间隔函数，1秒执行
	curCount: null, //当前剩余秒数
	checked:false,

	sendMessage: function(requestUrl,mobile,type) {
		if (sends.checked) {
			return false;
		}
		sends.checked = true;
		sends.curCount = sends.count;
		$(".getSMS").removeClass("cllan");
		$(".getSMS").addClass("cl9");
		$(".getSMS").html("重新获取(" + sends.curCount + ")");
		sends.InterValObj = window.setInterval(sends.SetRemainTime, 1000); //启动计时器，1秒执行一次
		//向后台发送处理数据
		$.ajax({　　
			type: "post", 
			　　dataType: "JSON",
			　　url: requestUrl,
			   async:true,
			   traditional:true,
			　　data: {
					mobile:mobile,
					type : type
				},
			　　error: function(XMLHttpRequest, textStatus, errorThrown) {},
			　　success : function(data, textStatus)  {
				if(data.code == "000000"){
			         $("#isExist").attr("value",data.isExist);
			      }else{
			    	  if(data.message == null || data.message == ''){
			    		  alert("短信验证码发送失败，请稍后再试！");
			    	  }else{
			    		  alert(data.message);
			    	  }
			      }
			}
		});
	},

	//timer处理函数
	SetRemainTime: function() {
		if(sends.curCount == 0) {
			window.clearInterval(sends.InterValObj); //停止计时器
			$(".getSMS").removeClass("cl9");
			$(".getSMS").addClass("cllan");
			sends.checked = false;
			$(".getSMS").html("立即获取");
		} else {
			sends.curCount--;
			$(".getSMS").html("重新获取(" + sends.curCount + ")");
		}
	}
}
