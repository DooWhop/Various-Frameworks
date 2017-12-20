/*window.onload = function(){
  var multiData = {values:[
 { value0:[
   {x:"10-07",y:3.2},
   {x:"10-08",y:3.3},
   {x:"10-08",y:3.4},
   {x:"10-08",y:3.4},
   {x:"10-08",y:3.6},
   {x:"10-08",y:3.5},
   {x:"10-08",y:3.7},
   {x:"10-09",y:3.8}
  ]},
  ]	
 }//必须按照这个格式定义数据，关键字values value0 value1 ...... 
   
    *@param0: canvas 的id
    *@param1: json 数据
    *@param2: 坐标距离画布的间隙padding
    *@param3: 如果只有一条数据时数据的颜色，多条数据颜色随机
    *@param4: 点的颜色
    *@param5: 是否绘制背景线
    
   //先定义数据线的名字，再绘制数据
   //LineChart.setKey(["2013","2014"]);
   LineChart.setData("canvas",multiData,30,"#f16848","#f16848",true);
 }*/

var LineChart = {
	keynames: [], //数据信息数组
	can: undefined,
	ctx: undefined,
	width: undefined,
	lineColor: undefined,
	dotColor: undefined,
	isBg: false,
	isMultiData: false,
	ptop: 100,
	setData: function(canId, data, padding, lineColor, dotColor, isBg, isMultiData) {
		this.lineColor = lineColor;
		this.dotColor = dotColor;
		this.can = document.getElementById(canId);
		this.ctx = this.can.getContext("2d");
		this.isBg = isBg;
		this.isMultiData = isMultiData;
		this.drawXY(data, 0, padding, this.can, this.ptop);
	},
	clear:function(){
		this.ctx.clearRect(0,0,this.can.width,this.can.height);
	},
	isMultiData: function(data) {
		if (data.values.length > 1) {
			this.isMultiData = true;
		}
	}, //是否是多条数据线

	drawXY: function(data, key, padding, can, ptop) {
		this.clear();
		this.ctx.lineWidth = "1";
		this.ctx.font = '22px Arial';
		this.ctx.fillStyle = "#acacae";
		var perwidth = this.getPixel(data, key, can.width - 40, padding); //x 轴每一个数据占据的宽度
		var maxY = this.getMax(data, 0, this.isMultiData); //获得Y轴上的最大值
		var minY = this.getMin(data, 0, this.isMultiData); //获得Y轴上的最小值
		var maxY2 = maxY + (maxY - minY);
		var minY2 = ((minY - (maxY - minY))) > 0 ? (minY - (maxY - minY)) : 0;
		var yPixel = this.getYPixel(maxY, can.height - 20 - ptop, padding).pixel;
		var ycount = 6;
		var yPixel2 = (maxY2 - minY2) / ycount;
		this.ctx.textBaseline = "bottom"; //文字的中心线的调整
		for (var i = 0, ptindex; i < data.values[key]["value" + key].length; i++) {
			ptindex = i + 1;
			var x_x = this.getCoordX(padding, perwidth, ptindex);
			var x_y = can.height - padding + 25;
			if (i > 0)
				x_x = x_x - 28;
			this.ctx.fillText(data.values[key]["value" + key][i].x, x_x, x_y, perwidth);
		}
		this.ctx.textAlign = "left" //y轴文字靠右写
		this.ctx.textBaseline = "middle"; //文字的中心线的调整
		for (var i = 0; i <= ycount; i++) {
			this.ctx.fillText((maxY2 - i * yPixel2).toFixed(3),4, (i) * yPixel + 10 + ptop, perwidth);
		}
		if (this.isBg) {
			var x = 95;
			this.ctx.lineWidth = "1";
			this.ctx.strokeStyle = "#f3f4f4";
			for (var i = 0; i <= ycount; i++) {
				var y = (i) * yPixel + 10 + ptop;
				this.ctx.moveTo(x, y);
				this.ctx.lineTo(can.width, y);
				this.ctx.stroke();
			}
			for (var i = 0; i <= data.values[key]['value0'].length; i++) {
				var x2 = this.getCoordX(padding, perwidth, i + 1);
				this.ctx.moveTo(x2, ptop);
				this.ctx.lineTo(x2, can.height - padding);
				this.ctx.stroke();
			}
		} //选择绘制背景线
		this.ctx.closePath();
		this.drawData(data, 0, padding, perwidth, yPixel, this.isMultiData, minY2, yPixel2, this.can);
	}, //绘制XY坐标 线 以及点

	drawData: function(data, key, padding, perwidth, yPixel, isMultiData, minY2, yPixel2, can) {
		if (!isMultiData) {
			var keystr = "value" + key;
			this.ctx.beginPath();
			this.ctx.strokeStyle = this.lineColor;
			var startX = this.getCoordX(padding, perwidth, 0);
			var startY = this.getCoordY(padding, yPixel, data.values[key][keystr][0].y, minY2, yPixel2);
			this.ctx.lineWidth = "4";
			for (var i = 0; i < data.values[key][keystr].length; i++) {
				var x = this.getCoordX(padding, perwidth, i + 1);
				var y = this.getCoordY(padding, yPixel, data.values[key][keystr][i].y, minY2, yPixel2);
				this.ctx.lineTo(x, y);
			}
			this.ctx.stroke();
			this.ctx.closePath();
			//绘制阴影区域
			this.ctx.beginPath();
			this.ctx.lineWidth = "1";
			this.ctx.strokeStyle = 'rgba(255,255,255,0)';
			this.ctx.lineTo(0, can.height - padding);
			for (var i = 0; i < data.values[key][keystr].length; i++) {
				var x = this.getCoordX(padding, perwidth, i + 1);
				var y = this.getCoordY(padding, yPixel, data.values[key][keystr][i].y, minY2, yPixel2) + 2;
				this.ctx.lineTo(x, y);
			}
			this.ctx.lineTo(can.width - 40, can.height - padding);
			this.ctx.lineTo(0, can.height - padding);
			this.ctx.stroke();
			this.ctx.fillStyle = 'rgba(225,245,254,0.5)';
			this.ctx.fill();
			this.ctx.closePath();
			//title
			this.ctx.beginPath();
			this.ctx.fillStyle = "#b2b2b4";
			this.ctx.font = '26px 黑体';
			this.ctx.textAlign = "left" //y轴文字靠右写
			this.ctx.fillText('单位(千元)', 0, this.ptop - 40, 300);
			this.ctx.closePath();
			
		}
	}, //绘制数据线和数据点
	roundedRect: function(cornerX, cornerY, width, height, cornerRadius) {
		if (width > 0) this.ctx.moveTo(cornerX + cornerRadius, cornerY);
		else this.ctx.moveTo(cornerX - cornerRadius, cornerY);
		this.ctx.arcTo(cornerX + width, cornerY, cornerX + width, cornerY + height, cornerRadius);
		this.ctx.arcTo(cornerX + width, cornerY + height, cornerX, cornerY + height, cornerRadius);
		this.ctx.arcTo(cornerX, cornerY + height, cornerX, cornerY, cornerRadius);
		if (width > 0) {
			this.ctx.arcTo(cornerX, cornerY, cornerX + cornerRadius, cornerY, cornerRadius);
		} else {
			this.ctx.arcTo(cornerX, cornerY, cornerX - cornerRadius, cornerY, cornerRadius);
		}
	},
	getPixel: function(data, key, width, padding) {
		var count = data.values[key]["value" + key].length;
		return (width) / (count - 1);
	}, //宽度
	getCoordX: function(padding, perwidth, ptindex) { //下标从1开始 不是从0开始
		return perwidth * ptindex - perwidth;
	}, //横坐标X 随ptindex 获得
	getCoordY: function(padding, yPixel, value, minY2, yPixel2) {
		var y = yPixel * ((value - minY2) / yPixel2) + 10;
		return this.can.height - padding - y;
	}, //纵坐标X 随ptindex 获得(注意 纵坐标的算法是倒着的因为原点在最上面)
	getYPixel: function(maxY, height, padding) {
		var ycount = 6; //y轴最大值
		return {
			pixel: (height - padding) / ycount,
			ycount: ycount
		};
	}, //y轴的单位长度

	getMax: function(data, key, isMultiData) {
		var maxY = data.values[key]["value" + key][0].y;
		var length = data.values[key]["value" + key].length;
		var keystr = "value" + key;
		for (var i = 1; i < length; i++) {
			if (maxY < data.values[key][keystr][i].y)
					maxY = data.values[key][keystr][i].y;
		}
		return maxY; //返回最大值 如果不是多数据
	},
	getMin: function(data, key, isMultiData) {
		var minY = data.values[key]["value" + key][0].y;
		var length = data.values[key]["value" + key].length;
		var keystr = "value" + key;
		for (var i = 1; i < length; i++) {
			if (minY > data.values[key][keystr][i].y)
				minY = data.values[key][keystr][i].y;
		}
		return minY; //返回最大值 如果不是多数据
	},
	setKey: function(keynames) { //keynames 是数组
			for (var i = 0; i < keynames.length; i++) {
				this.keynames.push(keynames[i]); //存入数组中
			}
		}
}