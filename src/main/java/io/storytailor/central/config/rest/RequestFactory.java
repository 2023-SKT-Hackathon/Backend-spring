package io.storytailor.central.config.rest;

import java.net.URI;

import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RequestFactory {

    private RestTemplate restTemplate = new RestTemplate();

    /**
     * Constructor of DataCoreRequestFactory
     */
    public RequestFactory() {
        this.restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestWithBodyFactory());
    }

    /**
     * HttpComponents HttpClient to create requests with body
     */
    private static final class HttpComponentsClientHttpRequestWithBodyFactory
            extends HttpComponentsClientHttpRequestFactory {

        /**
         * Create http uri request
         */
        @Override
        protected ClassicHttpRequest createHttpUriRequest(HttpMethod httpMethod, URI uri) {
            if (httpMethod == HttpMethod.GET) {
                return new HttpGetRequestWithEntity(httpMethod.toString(), uri);
            }
            return super.createHttpUriRequest(httpMethod, uri);
        }
    }

    /**
     * Http get request with entity
     */
    private static final class HttpGetRequestWithEntity extends HttpUriRequestBase {

        public HttpGetRequestWithEntity(final String method, final URI uri) {
            super(method, uri);
        }

        /**
         * Get http method
         */
        @Override
        public String getMethod() {
            return HttpMethod.GET.name();
        }
    }

    /**
     * Get Rest template
     * 
     * @return
     */
    public RestTemplate getRestTemplate() {
        return restTemplate;
    }
}
