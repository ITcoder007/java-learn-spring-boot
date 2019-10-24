package com.star.rpc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;

import javax.net.ssl.HttpsURLConnection;

public class JdkHttpClient {

	private static final int TIME_OUT_MILLI = 15000;

	public static String sendPost(String path, String json) throws IOException {
		
		OutputStream out = null;
		InputStream in = null;
		URLConnection conn = null;
		try {
			URL url = new URL(path);
			conn = url.openConnection();
			conn.setRequestProperty("Accept", "*/*");
			if (conn instanceof HttpURLConnection) {
				((HttpURLConnection) conn).setRequestMethod("POST"); // 设置请求方式
			} else if (conn instanceof HttpsURLConnection) {
				((HttpsURLConnection) conn).setRequestMethod("POST"); // 设置请求方式
			}
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
			conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setConnectTimeout(TIME_OUT_MILLI);
			conn.setReadTimeout(TIME_OUT_MILLI);
			conn.connect();

			out = conn.getOutputStream();
			out.write(json.toString().getBytes("UTF-8"));
			out.close();

			in = conn.getInputStream();

			byte[] buf = new byte[4096];
			byte[] data = new byte[0];
			int readLen = 0;
			int destPos = 0;
			while ((readLen = in.read(buf)) > 0) {
				data = Arrays.copyOf(data, data.length + readLen);
				System.arraycopy(buf, 0, data, destPos, readLen);
				destPos += readLen;
			}
			in.close();
			return new String(data, "UTF-8");
		} finally {
			if (out != null) {
				out.close();
			}
			if (in != null) {
				in.close();
			}
			if (conn != null) {
				if (conn instanceof HttpURLConnection) {
					((HttpURLConnection) conn).disconnect(); // 关闭连接
				} else if (conn instanceof HttpsURLConnection) {
					((HttpsURLConnection) conn).disconnect(); // 关闭连接
				}
			}
		}
	}
	
	public static String sendGet(String url, String param) throws IOException{
		InputStream in = null;
		URLConnection connection = null;
		try {
			String urlNameString = url + "?" + param;
			URL realUrl = new URL(urlNameString);
			// 打开和URL之间的连接
			connection = realUrl.openConnection();
			// 设置通用的请求属性
			connection.setRequestProperty("Accept", "*/*");
			if (connection instanceof HttpURLConnection) {
				((HttpURLConnection) connection).setRequestMethod("GET"); // 设置请求方式
			} else if (connection instanceof HttpsURLConnection) {
				((HttpsURLConnection) connection).setRequestMethod("GET"); // 设置请求方式
			}
			connection.setUseCaches(false);
			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
			connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setConnectTimeout(TIME_OUT_MILLI);
			connection.setReadTimeout(TIME_OUT_MILLI);
			connection.connect();

			in = connection.getInputStream();

			byte[] buf = new byte[4096];
			byte[] data = new byte[0];
			int readLen = 0;
			int destPos = 0;
			while ((readLen = in.read(buf)) > 0) {
				data = Arrays.copyOf(data, data.length + readLen);
				System.arraycopy(buf, 0, data, destPos, readLen);
				destPos += readLen;
			}
			in.close();
			return new String(data, "UTF-8");
		} finally {
			if (in != null) {
				in.close();
			}
			if (connection != null) {
				if (connection instanceof HttpURLConnection) {
					((HttpURLConnection) connection).disconnect(); // 关闭连接
				} else if (connection instanceof HttpsURLConnection) {
					((HttpsURLConnection) connection).disconnect(); // 关闭连接
				}
			}
		}
    }
}
