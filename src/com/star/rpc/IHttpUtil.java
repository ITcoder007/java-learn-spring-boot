package com.star.rpc;

import java.io.IOException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IHttpUtil {

	private static final String APPLICATION_JSON = "application/json";

	private static final String HTTP_ENCODE = "UTF-8";

	/**
	 * json post请求
	 * @param url
	 * @param paramStr
	 * @return
	 * @throws Exception
	 */
	private static String httpPost(String url, String paramStr) throws Exception {
		String resData = null;
		try (CloseableHttpClient httpClient = HttpClients.createDefault()){
			resData = doPost(url, paramStr, httpClient);
		}
		return resData;
	}

	/**
	 * httpGet
	 *
	 * @param url
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	private static String httpGet(String url, Map<String, Object> paramMap) throws Exception  {
		String resData = null;
		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			resData = doGet(url, paramMap, httpClient);

		}
		return resData;
	}

	private static String doGet(String url, Map<String, Object> paramMap, CloseableHttpClient httpClient) throws IOException {
		String resData = null;
		// 创建参数队列
		List<NameValuePair> params = new ArrayList<>();
		if(null != paramMap)
        {
            for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
                String key = entry.getKey();
                String val = getStr( entry.getValue() );
                params.add(new BasicNameValuePair(URLEncoder.encode(key, "utf-8"), URLEncoder.encode(val, "utf-8")));
            }
            //参数转换为字符串
            String paramsStr = EntityUtils.toString(new UrlEncodedFormEntity(params, "UTF-8"));
            url = url + "?" + paramsStr;
            log.info("http get url = {}", url);
        }

		// 创建httpGet.
		HttpGet httpget = new HttpGet(url);
		// 执行get请求.
		try (CloseableHttpResponse response = httpClient.execute(httpget)){
            //获取结果实体
            HttpEntity httpEntity = response.getEntity();
            if(null != httpEntity)
            {
                resData = EntityUtils.toString(httpEntity);
            }
        }
		return resData;
	}


	private static String getStr(Object value) {
		String res = null;

		if( value == null ) return res;

		if( value instanceof String ) {
			res = value.toString();
		}else {
			res = JSON.toJSONString(value);
		}
		return res;
	}


	/**
	 * POST方法
	 * @param postUrl
	 * @param param
	 * @return
	 */
	public static JSONObject jsonPost(String postUrl, JSONObject param) {
		JSONObject json = null;
		try {
			log.info("postUrl is {} , param is {}", postUrl, param.toString());
			String result = httpPost(postUrl, param.toString());
			log.info("result is {}", result);
			json = JSON.parseObject(result);
		} catch (Exception e) {
			log.error(e.getMessage() , e );
		}
		return json;
	}

	/**
	 * Get请求发送
	 * @param getUrl
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public static JSONObject jsonGet(String getUrl, Map<String, Object> paramMap) {
		JSONObject data = null;
		try {
			log.info("getUrl :{} , params :{} ", getUrl, JSON.toJSONString(paramMap));
			String result = httpGet(getUrl, paramMap);
			log.info("result：{}", result);
			data = JSON.parseObject(result);
		} catch (Exception e) {
			log.error(e.getMessage() , e );
		}
		return data;
	}

	/**
	 * 产生返回对象
	 * @param data  返回信息
	 * @param msgName 返回信息的字段名称
	 * 	eg.  data为 {"status":true,"code":200,"message":"成功","data":{"outname":"加推","outphone":"400-1234-567"}}
	 *       msgName为  message
	 * @return
	 */
//	@SuppressWarnings("rawtypes")
//	public static ServiceResponse generateResponse(JSONObject data , String msgName) {
//		if( data == null ) {
//			return new ServiceResponse( ResponseErrorCodeEnum.SYSTEM_ERR );
//		}
//
//		String msg = data.getString(msgName);
//		log.info("调用失败! , {} is {}" , msgName , msg);
//		return new ServiceResponse(ResponseErrorCodeEnum.SYSTEM_ERR.getCode() , msg);
//	}


	/**
	 * 绕过验证
	 *
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	public static SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {
		SSLContext sc = SSLContext.getInstance("SSLv3");

		// 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
		X509TrustManager trustManager = new X509TrustManager() {
			@Override
			public void checkClientTrusted(
					java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
					String paramString) throws CertificateException {
			}

			@Override
			public void checkServerTrusted(
					java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
					String paramString) throws CertificateException {
			}

			@Override
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};

		sc.init(null, new TrustManager[] { trustManager }, null);
		return sc;
	}


    public static JSONObject httpsGet(String url, Map<String, Object> paramMap) throws Exception
	{
		String resData = null;
		//采用绕过验证的方式处理https请求
		SSLContext sslcontext = createIgnoreVerifySSL();
		// 设置协议http和https对应的处理socket链接工厂的对象
		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
				.register("http", PlainConnectionSocketFactory.INSTANCE)
				.register("https", new SSLConnectionSocketFactory(sslcontext))
				.build();
		PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
		HttpClients.custom().setConnectionManager(connManager);
		try (CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connManager).build()){
			resData = doGet(url, paramMap, httpClient);
		}
		if(null != resData)
		{
			return JSON.parseObject(resData);
		}
		return null;
	}

	/**
	 * https 请求工具类
	 *
	 * @param url
	 * @param paramStr
	 * @return
	 * @throws Exception
	 */
	public static JSONObject httpsPost(String url, String paramStr) throws Exception
	{
		String resData = null;
		//采用绕过验证的方式处理https请求
		SSLContext sslcontext = createIgnoreVerifySSL();
		// 设置协议http和https对应的处理socket链接工厂的对象
		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
				.register("http", PlainConnectionSocketFactory.INSTANCE)
				.register("https", new SSLConnectionSocketFactory(sslcontext))
				.build();
		PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
		HttpClients.custom().setConnectionManager(connManager);
		try (CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connManager).build()){
			resData = doPost(url, paramStr, httpClient);
		}
		if(null != resData)
		{
			return JSON.parseObject(resData);
		}
		return null;
	}

	private static String doPost(String url, String paramStr, CloseableHttpClient httpClient) throws IOException {
		String resData = null;
		HttpPost httpPost = new HttpPost(url);
		StringEntity entity = new StringEntity(paramStr, HTTP_ENCODE);
		entity.setContentEncoding(HTTP_ENCODE);
		entity.setContentType(APPLICATION_JSON);
		httpPost.setEntity(entity);
		try(CloseableHttpResponse response = httpClient.execute(httpPost))
        {
            //获取结果实体
            HttpEntity httpEntity = response.getEntity();
            if(null != httpEntity)
            {
                resData = EntityUtils.toString(httpEntity);
            }
        }
		return resData;
	}

}
