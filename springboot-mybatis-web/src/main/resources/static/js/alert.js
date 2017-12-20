document.writeln("<style type=\"text\/css\">");
document.writeln("");
document.writeln("#windowcenter {");
document.writeln("	display:none;");
document.writeln("}");
document.writeln(".window {");
document.writeln("	width:80%;");
document.writeln("	position:absolute;");
document.writeln("	top:50%;");
document.writeln("	left:50%;");
document.writeln("	 z-index:9999;");
document.writeln("	margin:-100px auto 0 -40%;");
document.writeln("	padding:2px;");
document.writeln("	border-radius:0.6em;");
document.writeln("	-webkit-border-radius:0.6em;");
document.writeln("	-moz-border-radius:0.6em;");
document.writeln("	background-color: #ffffff;");
document.writeln("	-webkit-box-shadow: 0 0 10px rgba(0, 0, 0, 0.5);");
document.writeln("	-moz-box-shadow: 0 0 10px rgba(0, 0, 0, 0.5);");
document.writeln("	-o-box-shadow: 0 0 10px rgba(0, 0, 0, 0.5);");
document.writeln("	box-shadow: 0 0 10px rgba(0, 0, 0, 0.5);");
document.writeln("	font:14px\/1.5 Microsoft YaHei,Helvitica,Verdana,Arial,san-serif;");
document.writeln("}");
document.writeln(".window .content {");
document.writeln("	\/*min-height:100px;*\/");
document.writeln("	overflow:auto;");
document.writeln("	padding:15px;");
document.writeln("    text-shadow: none;");
document.writeln("}");
document.writeln(".window #txt {");
document.writeln("	min-height:30px;font-size:14px; line-height:25px;");
document.writeln("}");
document.writeln("html .window input[type=button].txtbtn {");
document.writeln("	");
document.writeln("	border: 0;");
document.writeln("	border-top: 1px solid #ccc;");
document.writeln("	display: block;");
document.writeln("	width: 100%;");
document.writeln("	cursor: pointer;");
document.writeln("	text-align: center;");
document.writeln("	padding:6px;");
document.writeln("	margin:10px 0 0 0;");
document.writeln("	font:16px\/1.5 Microsoft YaHei,Helvitica,Verdana,Arial,san-serif;");
document.writeln("	font-weight: bold;");
document.writeln("}");
document.writeln("");
document.writeln("<\/style>");
document.writeln("<div class='bgzz' id=\"windowcenter\"><div class=\"window\">");
document.writeln("	<div class=\"content\">");
document.writeln("	 <div id=\"txt\" class='tc'><\/div>");
document.writeln("	<\/div>");
document.writeln("	 <input type=\"button\" value=\"确定\" id=\"windowclosebutton\" name=\"确定\" class=\"txtbtn cll\">	");
document.writeln("<\/div></div>");
$(document).ready(function () { 

$("#windowclosebutton").click(function () { 
$("#windowcenter").hide();
}); 
$("#alertclose").click(function () { 
$("#windowcenter").hide();
}); 

}); 
function alert(title){ 
//var windowHeight; 
//var windowWidth; 
//var popWidth;  
//var popHeight; 
//windowHeight=$(window).height(); 
//windowWidth=$(window).width(); 
//popHeight=$(".window").height(); 
//popWidth=$(".window").width(); 
//var popY=(windowHeight-popHeight)/2; 
//var popX=(windowWidth-popWidth)/2; 
//$("#windowcenter").css("top",popY).css("left",popX).slideToggle("slow"); 
$("#windowcenter").show();
$("#txt").html(title);
//$("#windowcenter").hide("slow"); 
//setTimeout('$("#windowcenter").slideUp(500)',8000);
} 

