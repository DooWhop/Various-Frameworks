var sendVerifyCode = {
	
	InterValObj: null, //timer变量，控制时间
	count: 60, //间隔函数，1秒执行
	curCount: null, //当前剩余秒数
	checked:false,

	sendMessage: function(requestUrl,userId,phoneNo,reqType) {
		if (sendVerifyCode.checked) {
			return false;
		}
		sendVerifyCode.checked = true;
		sendVerifyCode.curCount = sendVerifyCode.count;
		$(".getSMS").removeClass("cllan");
		$(".getSMS").addClass("cl9");
		$(".getSMS").html("重新获取(" + sendVerifyCode.curCount + ")");
		sendVerifyCode.InterValObj = window.setInterval(sendVerifyCode.SetRemainTime, 1000); //启动计时器，1秒执行一次
		//向后台发送处理数据
		$.ajax({
			type: "POST", //用POST方式传输
			dataType: "JSON", //数据格式:JSON
			url: requestUrl, //目标地址
			data: {
						userId:userId,
						reqType:reqType,
						hpNo:phoneNo				
				},
				success : function(data, textStatus) {
					if(data.isSuccess == "true"){
						if (data.response.code == "W-Y000000") {
							$("#verifycodeid").val(data.response.verifycodeid);
							console.log("verifycodeid: "+data.response.verifycodeid);
						}else if(data.response.code == "W-B000005"){
							alert("您输入的手机号码已开户");
						}else {
							alert(data.response.message);
						}
					}else{
						if(data.errorMessage){
							alert(data.errorMessage);
						}else if(data.response.message){
							alert(data.response.message);
						}						
					}
					
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert("服务器连接错误，请稍候重试。");
				}
		});
	},

	//timer处理函数
	SetRemainTime: function() {
		if(sendVerifyCode.curCount == 0) {
			window.clearInterval(sendVerifyCode.InterValObj); //停止计时器
			$(".getSMS").removeClass("cl9");
			$(".getSMS").addClass("cllan");
			sendVerifyCode.checked = false;
			$(".getSMS").html("立即获取");
		} else {
			sendVerifyCode.curCount--;
			$(".getSMS").html("重新获取(" + sendVerifyCode.curCount + ")");
		}
	}
}
