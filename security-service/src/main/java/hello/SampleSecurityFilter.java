package hello;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SampleSecurityFilter implements Filter {

    private  final Logger log = LoggerFactory.getLogger(this.getClass());    
	Map<String, String> environment = new HashMap<String,String>(); 
	public static String forwardedUrlHeader = "x-cf-forwarded-url";
	private static String greetingServiceAuthTokenName = "x-cf-greetingServiceAuthToken";
	private static String validAuthToken;

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		log.info(greetingServiceAuthTokenName + " provided via env variable:" + validAuthToken);		
		String serviceAuthenticationToken = ((HttpServletRequest) request).getHeader(greetingServiceAuthTokenName);
		if (serviceAuthenticationToken != null && serviceAuthenticationToken.trim().length() > 0) {
			log.info("serviceAuthenticationToken:"+serviceAuthenticationToken);
			if (validAuthToken !=null && validAuthToken.equals(serviceAuthenticationToken)) {
				new RequestForwarder().forwardRequest((HttpServletRequest) request, (HttpServletResponse) response);
			}
			else{
				log.info("greetingServiceAuthTokenName INVALID:'" + validAuthToken + "'");
			    setStatus(response, HttpServletResponse.SC_BAD_REQUEST);				
			}
		}else{
			log.info("Returning:" + HttpServletResponse.SC_NOT_FOUND + " greetingServiceAuthTokenName NOT PRESENT");
			setStatus(response, HttpServletResponse.SC_NOT_FOUND);
		}
	}

	private void setStatus(ServletResponse response, int statusCode) throws IOException{
		((HttpServletResponse)response).setStatus(statusCode);
		((HttpServletResponse)response).sendError(statusCode);
	}

	public void destroy() {
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		Map<String, String> environment = System.getenv();
		log.info("in the init--->");
		for (Map.Entry<String, String> entry : environment.entrySet()) {
			
		    log.info("'"+entry.getKey()+"':'"+entry.getValue()+"'");
		}
		validAuthToken = environment.get(greetingServiceAuthTokenName);		
		log.info("from init validAuthToken:" + validAuthToken);
	}

}