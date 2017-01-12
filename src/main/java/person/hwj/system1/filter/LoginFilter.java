package person.hwj.system1.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.web.filter.OncePerRequestFilter;

import person.hwj.system1.common.Constants;
import person.hwj.system1.common.CookieUtil;
import redis.clients.jedis.JedisCluster;

public class LoginFilter extends OncePerRequestFilter{

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		//获取全局令牌
		Cookie CAllToken = CookieUtil.getCookie(request, "globalToken");
		//获取局部令牌
		Cookie CSystem1= CookieUtil.getCookie(request, "Token1");
		//登陆后带有全局Token
		String globalToken=request.getParameter("globalToken");
        String[] uris = request.getRequestURI().split("/");
        if (uris.length < 1) {
        	response.sendRedirect("http://localhost:8080/user-center/loginPage");;
            return;
        }
		String uri = uris[uris.length - 1];
		if (uri.contains(".")){
		    uri = uri.split("\\.")[uri.split("\\.").length - 1];
		}
		if (Constants.FILTER_IGNORE.IGNORES.contains(uri)) {
            filterChain.doFilter(request, response);
            return;
        }
		@SuppressWarnings("resource")
		ApplicationContext context = new
                FileSystemXmlApplicationContext("classpath:spring/spring-web.xml");
        JedisCluster jedisCluster = context.getBean("jedisCluster", JedisCluster.class);
        if(globalToken!=null&& jedisCluster.exists(globalToken)){
        	CookieUtil.saveCookie(response, "globalToken", globalToken);
        	if (null == CSystem1 || null == CSystem1.getValue()
        			||jedisCluster.exists(CSystem1.getValue())) {
    			//获取当前用户在当前子系统中的用户信息和权限
    			//如果需要跳转到之前的页面，参数带上旧的URL
    			response.sendRedirect("initUserInfo?oldUrl="+uri);
    			return;
        	}
            filterChain.doFilter(request, response);
            return;
        }
		if(uri.equals("logout")){
			response.sendRedirect("http://localhost:8080/user-center/logout");
			return;
		}
        if (null != CAllToken && null != CAllToken.getValue()
                && jedisCluster.exists(CAllToken.getValue())) {
        	if (null == CSystem1 || null == CSystem1.getValue()
        			|| !jedisCluster.exists(CSystem1.getValue())) {
        		response.sendRedirect("initUserInfo?oldUrl="+uri);
    			return;
        	}
            filterChain.doFilter(request, response);
            return;
        }
        response.sendRedirect("http://localhost:8080/user-center/loginPage?oldUrl="+request.getRequestURL());
	}
}
