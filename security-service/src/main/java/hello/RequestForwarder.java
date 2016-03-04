package hello;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestForwarder {
//lifted from
//http://stackoverflow.com/questions/12130992/forward-httpservletrequest-to-a-different-server	
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    
	public void forwardRequest(HttpServletRequest req, HttpServletResponse resp) {
		final boolean hasoutbody = (req.getMethod().equals("POST"));
		try {
			String forwardToURL = req.getHeader(SampleSecurityFilter.forwardedUrlHeader);

			log.info("Forwarding request to:---->" + forwardToURL);
			printHeaders(req);

			final URL url = new URL(forwardToURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(req.getMethod());

			final Enumeration<String> headers = req.getHeaderNames();
			while (headers.hasMoreElements()) {
				final String header = headers.nextElement();
				final Enumeration<String> values = req.getHeaders(header);
				while (values.hasMoreElements()) {
					final String value = values.nextElement();
					conn.addRequestProperty(header, value);
				}
			}

			// conn.setFollowRedirects(false); // throws AccessDenied exception
			conn.setUseCaches(false);
			conn.setDoInput(true);
			conn.setDoOutput(hasoutbody);
			conn.connect();

			final byte[] buffer = new byte[16384];
			while (hasoutbody) {
				final int read = req.getInputStream().read(buffer);
				if (read <= 0)
					break;
				conn.getOutputStream().write(buffer, 0, read);
			}

			resp.setStatus(conn.getResponseCode());
			for (int i = 0;; ++i) {
				final String header = conn.getHeaderFieldKey(i);
				if (header == null)
					break;
				final String value = conn.getHeaderField(i);
				resp.setHeader(header, value);
			}

			while (true) {
				final int read = conn.getInputStream().read(buffer);
				if (read <= 0)
					break;
				resp.getOutputStream().write(buffer, 0, read);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void printHeaders(HttpServletRequest req) {
		Enumeration<String> v = req.getHeaderNames();
		while (v.hasMoreElements()) {
			String headerName = v.nextElement();
			String headerValue = req.getHeader(headerName);
			log.info("HeaderName:" + headerName + " headerValue:" + headerValue);
		}
	}
}
