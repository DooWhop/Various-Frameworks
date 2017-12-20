package com.allinpal.twodfireservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 应用配置信息类
 * 
 * @author Shawn
 *
 */
@Component
public class AppConfig {
	/**
	 * 应用ip地址
	 */
	@Value("${twodfire.service.ip}")
	public String appIp;
	
	@Value("${twodfire.service.contract.downloanFilePath}")
	public String downloadFilePath;
	
	@Value("${twodfire.service.contract.templatePath}")
	public String templatePath;

	public String getAppIp() {
		return appIp;
	}

	public void setAppIp(String appIp) {
		this.appIp = appIp;
	}

	public String getDownloadFilePath() {
		return downloadFilePath;
	}

	public void setDownloadFilePath(String downloadFilePath) {
		this.downloadFilePath = downloadFilePath;
	}

	public String getTemplatePath() {
		return templatePath;
	}

	public void setTemplatePath(String templatePath) {
		this.templatePath = templatePath;
	}
}
