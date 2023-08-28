package io.storytailor.central.config;

import java.net.http.HttpClient;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.tomcat.util.modeler.Registry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfiguration {

        @Value("${http.pool.max.total}")
        private Integer maxTotal;

        @Value("${http.pool.defaultMaxPerRoute}")
        private Integer defaultMaxPerRoute;

        @Value("${http.pool.connection.timeout}")
        private Integer connectionTimeout;

        @Value("${http.pool.connection.request.timeout}")
        private Integer connectionRequestTimeout;

        @Value("${http.pool.read.timeout}")
        private Integer readTimeout;

        @Value("${http.pool.validate.after.inactivity}")
        private Integer validateAfterInactivity;

        @Bean
        public RestTemplate restTemplate()
                        throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
                RestTemplate restTemplate = new RestTemplate(httpRequestFactory());
                restTemplate
                                .getMessageConverters()
                                .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
                return restTemplate;
        }

        @Bean
        public ClientHttpRequestFactory httpRequestFactory()
                        throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
                return new HttpComponentsClientHttpRequestFactory(httpClient());
        }

        @Bean
        public HttpClient httpClient()
                        throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
                SSLContext sslContext = new SSLContextBuilder()
                                .loadTrustMaterial(
                                                null,
                                                new TrustStrategy() {
                                                        public boolean isTrusted(X509Certificate[] arg0, String arg1)
                                                                        throws CertificateException {
                                                                return true;
                                                        }
                                                })
                                .build();

                SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(
                                sslContext,
                                new HostnameVerifier() {
                                        @Override
                                        public boolean verify(String s, SSLSession sslSession) {
                                                return true;
                                        }
                                });

                Registry<ConnectionSocketFactory> registry = RegistryBuilder
                                .<ConnectionSocketFactory>create()
                                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                                // .register("https", SSLConnectionSocketFactory.getSocketFactory())
                                .register("https", csf)
                                .build();

                PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(
                                registry);
                connectionManager.setMaxTotal(maxTotal);
                connectionManager.setDefaultMaxPerRoute(defaultMaxPerRoute);
                connectionManager.setValidateAfterInactivity(validateAfterInactivity);

                RequestConfig requestConfig = RequestConfig
                                .custom()
                                // The time for the server to return data (response) exceeds the throw of read
                                // timeout
                                .setSocketTimeout(readTimeout)
                                // The time to connect to the server (handshake succeeded) exceeds the throw
                                // connect timeout
                                .setConnectTimeout(connectionTimeout)
                                // The timeout to get the connection from the connection pool. If the connection
                                // is not available after the timeout, the following exception will be thrown
                                // org.apache.http.conn.ConnectionPoolTimeoutException: Timeout waiting for
                                // connection from pool
                                .setConnectionRequestTimeout(connectionRequestTimeout)
                                .build();

                return HttpClientBuilder
                                .create()
                                .setDefaultRequestConfig(requestConfig)
                                .setConnectionManager(connectionManager)
                                .setSSLSocketFactory(csf)
                                .build();
        }
}
