package com.fastjrun.sdkg.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import com.fastjrun.sdkg.common.CodeException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public abstract class BaseHttpClient extends BaseClient {

	protected JSONObject processInternal(String reqStr, String urlReq, String method,
			Map<String, String> requestProperties) {
		String line = "";
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";

		URL request;
		try {
			if (method.toUpperCase().equals("GET")) {
				if (reqStr != null && !reqStr.equals("")) {
					String data = URLEncoder.encode(reqStr, "UTF-8");
					urlReq = urlReq + "/" + data;
				}
			}
			request = new URL(urlReq);
			HttpURLConnection connection = (HttpURLConnection) request.openConnection();
			connection.setRequestMethod(method);
			requestProperties.keySet().parallelStream()
					.forEach(n -> connection.setRequestProperty(n, requestProperties.get(n)));
			if (!method.toUpperCase().equals("GET")) {
				connection.setDoOutput(true);
				connection.setDoInput(true);
				connection.setUseCaches(false);
				connection.setInstanceFollowRedirects(true);
				connection.connect();
				out = new PrintWriter(connection.getOutputStream());
				// 发送请求参数
				out.print(reqStr);
				// flush输出流的缓冲
				out.flush();

			}
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			while ((line = in.readLine()) != null) {
				result += line;
			}
			connection.disconnect();
			log.debug(result);
		} catch (MalformedURLException e) {
			log.error("", e);
			throw new CodeException("601", "网络异常" + e);
		} catch (IOException e) {
			log.error("", e);
			throw new CodeException("601", "网络异常" + e);
		}
		if (result.equals("")) {
			throw new CodeException("602", "返回数据为空");
		}
		JSONObject responseJsonObject=new JSONObject();
		if(result.startsWith("{")) {
			responseJsonObject=JSONObject.fromObject(result);
		}else {
			JSONArray jsonArray=JSONArray.fromObject(result);			
			responseJsonObject=new JSONObject();
			responseJsonObject.put("defaultKey", jsonArray);
		}
		
		return responseJsonObject;
	}

	protected JSONObject process(String reqStr, String urlReq, String method) {
		Map<String, String> requestProperties = new HashMap<String, String>();
		requestProperties.put("Content-Type", "application/json");
		requestProperties.put("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");

		requestProperties.put("Accept", "*/*");
		return this.process(reqStr, urlReq, method, requestProperties);
	}

	protected abstract JSONObject process(String reqStr, String urlReq, String httpMethod,
			Map<String, String> requestProperties);
}
