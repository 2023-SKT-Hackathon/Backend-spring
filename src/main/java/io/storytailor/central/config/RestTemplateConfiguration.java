package io.storytailor.central.config;

import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
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
        private Long readTimeout;

        @Bean
        public RestTemplate restTemplate()
                        throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
                RestTemplate restTemplate = new RestTemplate(httpRequestFactory());
                restTemplate
                                .getMessageConverters()
                                .add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
                return restTemplate;
        }

        @Bean
        public ClientHttpRequestFactory httpRequestFactory()
                        throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
                return new HttpComponentsClientHttpRequestFactory(httpClient());
        }

        @Bean
        public CloseableHttpClient httpClient()
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
                          Timeout timeoutConfig = Timeout.ofSeconds(connectionTimeout);
                RequestConfig requestConfig = RequestConfig
                                .custom()

                                // The time to connect to the server (handshake succeeded) exceeds the throw
                                // connect timeout
                                // The timeout to get the connection from the connection pool. If the connection
                                // is not available after the timeout, the following exception will be thrown
                                // org.apache.http.conn.ConnectionPoolTimeoutException: Timeout waiting for
                                // connection from pool
                                .setConnectionRequestTimeout(timeoutConfig)
                                .build();

                return HttpClientBuilder
                                .create()
                                .setDefaultRequestConfig(requestConfig)
                                .setConnectionManager(connectionManager)
                                .build();
        }
}
