package person.hwj.system1.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import person.hwj.system1.common.CookieUtil;
import redis.clients.jedis.JedisCluster;

@Controller
public class UserController {
	@Autowired
	private JedisCluster jedisCluster;
	@SuppressWarnings("unused")
	@RequestMapping(value="/initUserInfo")
	public void initUserInfo(HttpServletRequest request,String oldUrl,
			String password,HttpServletResponse response) throws IOException{
		Cookie cookie=CookieUtil.getCookie(request, "globalToken");
		System.out.println("系统1:globalToken="+cookie.getValue());
		//防止用户没有登陆直接在地址栏输入initUserInfo进入此方法
		if(cookie==null){
			response.sendRedirect("http://localhost:8080/user-center/loginPage?oldUrl=http://localhost:8080/system1/index");
			return;
        }
		String userPubliInfo=jedisCluster.get(cookie.getValue());
		//根据公共信息,到子系统相应数据库查询当前子系统下的用户信息
		String userPriveateInfo="system1 info";
		// 生成登录系统1用户的唯一标识
		String system1Id = UUID.randomUUID().toString();
		//生成局部令牌
		jedisCluster.setex(system1Id, 60*30, userPriveateInfo);
		//保存系统1的token
		CookieUtil.saveCookie(response, "Token1", system1Id);
		
		//创建HttpClient
		CloseableHttpClient httpclient = HttpClients.createDefault();
		//创建Post请求
		HttpPost httpPost=new HttpPost("http://localhost:8080/user-center/registered");
		//创建参数列表
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();  
        formparams.add(new BasicNameValuePair("sysId", system1Id));
        formparams.add(new BasicNameValuePair("globalId", cookie.getValue())); 
        //为请求添加参数
        httpPost.setEntity(new UrlEncodedFormEntity(formparams));  //进行转码
        //执行请求并获取系统注册结果
        CloseableHttpResponse result=httpclient.execute(httpPost);
        //获得结果字符串
        String isSuccess=EntityUtils.toString(result.getEntity());
        if(!isSuccess.equals("success")){
        	 response.sendRedirect("http://localhost:8080/user-center/loginPage");
        }
		if(oldUrl==null||oldUrl.equals("initUserInfo")){
			response.sendRedirect("index");
			return;
		}else{
			response.sendRedirect(oldUrl);
			return;
		}
	}
	@RequestMapping(value="/index")
	public String index(){
		return "index";
	}
	@RequestMapping(value="/test")
	public String test(){
		return "test";
	}
}

