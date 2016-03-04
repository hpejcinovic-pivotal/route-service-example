package hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController

public class RestConsumer {

	private static String greetingServiceAuthTokenName = "x-cf-greetingServiceAuthToken";
	
    private static final Logger log = LoggerFactory.getLogger(RestConsumer.class);

    
    @RequestMapping("/sayHi")
    public String greeting(@RequestParam(value="name", defaultValue="World") String name) {
    	String urlToInvoke=System.getenv("urlToInvoke");
    	log.info("--->Invoking remote service urlToInvoke:" + urlToInvoke);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        
        String validAuthToken = System.getenv().get(greetingServiceAuthTokenName);		
        log.info("sec token:" + greetingServiceAuthTokenName + ":" + validAuthToken);
        
        headers.add(greetingServiceAuthTokenName, validAuthToken);
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);

        ResponseEntity<String> response = restTemplate.exchange(urlToInvoke + name, HttpMethod.GET, entity, String.class);
        log.info("--->" + response.getBody());
        return "Response from REST Client:" + response.getBody();
    }

    
    
}
