package io.storytailor.central.config.rest;

import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
public class RestService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RequestFactory requestFactory;

    /**
     * make NiFi request url
     *
     * @param nifiUrl Base url
     * @param paths   paths
     * @return URI
     */
    protected URI getUrl(
            String nifiUrl,
            List<String> paths,
            Map<String, Object> params) {
        String integratedPath = "";
        if (paths != null) {
            for (String path : paths) {
                integratedPath += "/";
                integratedPath += path;
            }
        }

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(nifiUrl)
                .path(integratedPath);
        if (params != null) {
            Iterator<String> iteratortor = params.keySet().iterator();
            while (iteratortor.hasNext()) {
                String key = iteratortor.next();
                if (StringUtils.hasLength(String.valueOf(params.get(key)))) {
                    builder.queryParam(key, params.get(key));
                }
            }
        }
        return builder.build().toUri();
    }

    /**
     * Set http header and body with request entity
     *
     * @param body    Http request body
     * @param headers Http request header
     * @return HttpEntity
     */
    protected <T> HttpEntity<T> getRequestEntity(
            T body,
            Map<String, String> headers,
            String accessToken) {
        HttpHeaders httpHeaders = new HttpHeaders();
        if (headers != null) {
            Iterator<String> iterator = headers.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                httpHeaders.add(key, headers.get(key));
            }
        } 
        if (accessToken != null) {
            httpHeaders.setBearerAuth(accessToken);
        }
        return new HttpEntity<>(body, httpHeaders);
    }

    /**
     * Request get method for list
     *
     * @param moduleHost   Destination module host
     * @param pathUri      Path uri
     * @param headers      Http request header
     * @param body         Http request body
     * @param params       Http request param
     * @param responseType Response type
     * @return List of result object by retrieved.
     */

    public <T> ResponseEntity<List<T>> getList(
            String moduleHost,
            List<String> pathUri,
            Map<String, String> headers,
            Object body,
            Map<String, Object> params,
            ParameterizedTypeReference<List<T>> responseType) {
        URI uri = getUrl(moduleHost, pathUri, params);
        log.info("GET LIST - REST API URL : {}", uri);

        ResponseEntity<List<T>> response = null;
        try {
            response = requestFactory
                    .getRestTemplate()
                    .exchange(
                            uri,
                            HttpMethod.GET,
                            getRequestEntity(body, headers, null),
                            responseType);
        } catch (HttpClientErrorException e) {
            log.error(
                    "Client Exception - Error Status Code: {}, Response Body as String: {}",
                    e.getStatusCode(),
                    e.getResponseBodyAsString(),
                    e);
            throw new RestException(e.getStatusCode(), e);
        } catch (ResourceAccessException e) {
            log.error("Connection refused - {}", moduleHost, e);
            throw new RestException(HttpStatus.SERVICE_UNAVAILABLE);
        } catch (RestClientException e) {
            log.error("REST GET LIST Exception, ", e);
            throw new RestException(HttpStatus.BAD_REQUEST);
        }

        return response;
    }

    /**
     * Request get method for list
     *
     * @param moduleHost   Destination module host
     * @param pathUri      Path uri
     * @param headers      Http request header
     * @param body         Http request body
     * @param params       Http request param
     * @param responseType Response type
     * @return List of result object by retrieved.
     */
    public <T> ResponseEntity<T> getList(
            String moduleHost,
            List<String> pathUri,
            Map<String, String> headers,
            Object body,
            Map<String, Object> params,
            Class<T> responseType) {
        URI uri = getUrl(moduleHost, pathUri, params);
        log.info("GET LIST - REST API URL : {}", uri);

        ResponseEntity<T> response = null;
        try {
            response = requestFactory
                    .getRestTemplate()
                    .exchange(
                            uri,
                            HttpMethod.GET,
                            getRequestEntity(body, headers, null),
                            responseType);
        } catch (HttpClientErrorException e) {
            log.error(
                    "Client Exception - Error Status Code: {}, Response Body as String: {}",
                    e.getStatusCode(),
                    e.getResponseBodyAsString(),
                    e);
            throw new RestException(e.getStatusCode(), e);
        } catch (ResourceAccessException e) {
            log.error("Connection refused - {}", moduleHost, e);
            throw new RestException(HttpStatus.SERVICE_UNAVAILABLE);
        } catch (RestClientException e) {
            log.error("REST GET LIST Exception, ", e);
            throw new RestException(HttpStatus.BAD_REQUEST);
        }

        return response;
    }

    /**
     * Request get method
     *
     * @param moduleHost   Destination module host
     * @param pathUri      Path uri
     * @param headers      Http request header
     * @param body         Http request body
     * @param params       Http request param
     * @param responseType Response type
     * @return Result object by retrieved.
     */
    public <T> ResponseEntity<T> get(
            String moduleHost,
            List<String> pathUri,
            Map<String, String> headers,
            Object body,
            Map<String, Object> params,
            String accessToken,
            Class<T> responseType) {
        URI uri = getUrl(moduleHost, pathUri, params);
        log.info("GET - REST API URL : {}", uri);

        ResponseEntity<T> response = null;
        try {
            response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    getRequestEntity(body, headers, accessToken),
                    responseType);
        } catch (HttpClientErrorException e) {
            log.error(
                    "Client Exception - Error Status Code: {}, Response Body as String: {}",
                    e.getStatusCode(),
                    e.getResponseBodyAsString(),
                    e);
            throw new RestException(e.getStatusCode(), e);
        } catch (ResourceAccessException e) {
            log.error("Connection refused - {}", moduleHost, e);
            throw new RestException(HttpStatus.SERVICE_UNAVAILABLE);
        } catch (RestClientException e) {
            log.error("REST GET Exception, ", e);
            throw new RestException(HttpStatus.BAD_REQUEST);
        }

        return response;
    }

    /**
     * Request post method
     *
     * @param moduleHost   Destination module host
     * @param pathUri      Path uri
     * @param headers      Http request header
     * @param body         Http request body
     * @param params       Http request param
     * @param responseType Response type
     * @return A response object to a POST request.
     */
    public <T> ResponseEntity<T> post(
            String moduleHost,
            List<String> pathUri,
            Map<String, String> headers,
            Object body,
            Map<String, Object> params,
            String accessToken,
            Class<T> responseType) {
        URI uri = getUrl(moduleHost, pathUri, params);
        log.info("POST - REST API URL : {}", uri);

        ResponseEntity<T> response = null;
        try {
            response = restTemplate.exchange(
                    uri,
                    HttpMethod.POST,
                    getRequestEntity(body, headers, accessToken),
                    responseType);
        } catch (HttpClientErrorException e) {
            log.error(
                    "Client Exception - Error Status Code: {}, Response Body as String: {}",
                    e.getStatusCode(),
                    e.getResponseBodyAsString(),
                    e);
            throw new RestException(e.getStatusCode(), e);
        } catch (ResourceAccessException e) {
            log.error("Connection refused - {}", moduleHost, e);
            throw new RestException(HttpStatus.SERVICE_UNAVAILABLE);
        } catch (RestClientException e) {
            log.error("REST POST Exception, ", e);
            throw new RestException(HttpStatus.BAD_REQUEST);
        }

        return response;
    }

    /**
     * Request post method
     *
     * @param moduleHost   Destination module host
     * @param pathUri      Path uri
     * @param headers      Http request header
     * @param body         Http request body
     * @param params       Http request param
     * @param responseType Response type
     * @return A response object to a POST request.
     */
    public <T> ResponseEntity<T> postTemplate(
            String moduleHost,
            List<String> pathUri,
            Map<String, String> headers,
            Object body,
            Map<String, Object> params,
            String accessToken,
            Class<T> responseType) {
        URI uri = getUrl(moduleHost, pathUri, params);
        log.info("POST - REST API URL : {}", uri);

        ResponseEntity<T> response = null;
        try {
            response = restTemplate.exchange(
                    uri,
                    HttpMethod.POST,
                    getRequestEntity(body, headers, accessToken),
                    responseType);
        } catch (HttpClientErrorException e) {
            if (e.getRawStatusCode() == 409) {
                return null;
            } else {
                log.error(
                        "Client Exception - Error Status Code: {}, Response Body as String: {}",
                        e.getStatusCode(),
                        e.getResponseBodyAsString(),
                        e);
                throw new RestException(e.getStatusCode(), e);
            }
        } catch (ResourceAccessException e) {
            log.error("Connection refused - {}", moduleHost, e);
            throw new RestException(HttpStatus.SERVICE_UNAVAILABLE);
        } catch (RestClientException e) {
            log.error("REST POST Exception, ", e);
            throw new RestException(HttpStatus.BAD_REQUEST);
        }

        return response;
    }

    /**
     * Request patch method
     *
     * @param moduleHost   Destination module host
     * @param pathUri      Path uri
     * @param headers      Http request header
     * @param body         Http request body
     * @param params       Http request param
     * @param responseType Response type
     * @return A response object to a PATCH request.
     */
    public <T> ResponseEntity<T> patch(
            String moduleHost,
            List<String> pathUri,
            Map<String, String> headers,
            Object body,
            Map<String, Object> params,
            String accessToken,
            Class<T> responseType) {
        URI uri = getUrl(moduleHost, pathUri, params);
        log.info("PATCH - REST API URL : {}", uri);

        ResponseEntity<T> response = null;
        try {
            response = restTemplate.exchange(
                    uri,
                    HttpMethod.PATCH,
                    getRequestEntity(body, headers, accessToken),
                    responseType);
        } catch (HttpClientErrorException e) {
            log.error(
                    "Client Exception - Error Status Code: {}, Response Body as String: {}",
                    e.getStatusCode(),
                    e.getResponseBodyAsString(),
                    e);
            throw new RestException(e.getStatusCode(), e);
        } catch (ResourceAccessException e) {
            log.error("Connection refused - {}", moduleHost, e);
            throw new RestException(HttpStatus.SERVICE_UNAVAILABLE);
        } catch (RestClientException e) {
            log.error("REST PUT Exception, ", e);
            throw new RestException(HttpStatus.BAD_REQUEST);
        }

        return response;
    }

    /**
     * @param moduleHost   Destination module host
     * @param pathUri      Path uri
     * @param headers      Http request header
     * @param body         Http request body
     * @param params       Http request param
     * @param responseType Response type
     * @return A response object to a PUT request.
     */
    public <T> ResponseEntity<T> put(
            String moduleHost,
            List<String> pathUri,
            Map<String, String> headers,
            Object body,
            Map<String, Object> params,
            String accessToken,
            Class<T> responseType) {
        URI uri = getUrl(moduleHost, pathUri, params);
        log.info("PUT - REST API URL : {}", uri);

        ResponseEntity<T> response = null;
        try {
            response = restTemplate.exchange(
                    uri,
                    HttpMethod.PUT,
                    getRequestEntity(body, headers, accessToken),
                    responseType);
        } catch (HttpClientErrorException e) {
            log.error(
                    "Client Exception - Error Status Code: {}, Response Body as String: {}",
                    e.getStatusCode(),
                    e.getResponseBodyAsString(),
                    e);
            throw new RestException(e.getStatusCode(), e);
        } catch (ResourceAccessException e) {
            log.error("Connection refused - {}", moduleHost, e);
            throw new RestException(HttpStatus.SERVICE_UNAVAILABLE);
        } catch (RestClientException e) {
            log.error("REST PUT Exception, ", e);
            throw new RestException(HttpStatus.BAD_REQUEST);
        }

        return response;
    }

    /**
     * Request delete method
     *
     * @param moduleHost   Destination module host
     * @param pathUri      Path uri
     * @param headers      Http request header
     * @param body         Http request body
     * @param params       Http request param
     * @param responseType Response type
     * @return A response object to a DELETE request.
     */
    public <T> ResponseEntity<T> delete(
            String moduleHost,
            List<String> pathUri,
            Map<String, String> headers,
            Object body,
            Map<String, Object> params,
            String accessToken,
            Class<T> responseType) {
        URI uri = getUrl(moduleHost, pathUri, params);
        log.info("DELETE - REST API URL : {}", uri);

        ResponseEntity<T> response = null;
        try {
            response = restTemplate.exchange(
                    uri,
                    HttpMethod.DELETE,
                    getRequestEntity(body, headers, accessToken),
                    responseType);
        } catch (HttpClientErrorException e) {
            log.error(
                    "Client Exception - Error Status Code: {}, Response Body as String: {}",
                    e.getStatusCode(),
                    e.getResponseBodyAsString(),
                    e);
            throw new RestException(e.getStatusCode(), e);
        } catch (ResourceAccessException e) {
            log.error("Connection refused - {}", moduleHost, e);
            throw new RestException(HttpStatus.SERVICE_UNAVAILABLE);
        } catch (RestClientException e) {
            log.error("REST DELETE Exception, ", e);
            throw new RestException(HttpStatus.BAD_REQUEST);
        }

        return response;
    }
}
