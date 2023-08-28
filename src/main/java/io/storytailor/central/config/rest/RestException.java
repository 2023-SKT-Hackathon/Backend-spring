package io.storytailor.central.config.rest;

import org.springframework.http.HttpStatus;

public class RestException extends BaseException {

    private static final long serialVersionUID = 325647261102179280L;

    HttpStatus statusCode = null;
    ErrorPayload errorPayload = null;

    /**
     * Data core UI exception with statusCode and exception
     * Exception 의 getResponseBodyAsString() 으로부터 ErrorPayload 생성
     * - getResponseBodyAsString() 은 RestClientResponseException 의 메소드
     * - 하위 Exception : HttpStatusCodeException, HttpClientErrorException
     *
     * @param statusCode                  HttpStatus
     * @param RestClientResponseException exception
     */
    public RestException(HttpStatus statusCode, RestClientResponseException exception) {
        super(null, exception.getMessage());
        this.statusCode = statusCode;
        String res = exception.getResponseBodyAsString();
        if (res != null) {
            try {
                ClientExceptionPayloadVO clientExceptionPayloadVO = new ObjectMapper()
                        .readValue(res, ClientExceptionPayloadVO.class);
                this.errorPayload = new ErrorPayload(
                        clientExceptionPayloadVO.getType(),
                        clientExceptionPayloadVO.getTitle(),
                        clientExceptionPayloadVO.getDetail());
            } catch (JsonProcessingException e) {
                log.info(
                        "fail to get ErrorPayload. exception [{}] res[{}]",
                        exception,
                        exception.getResponseBodyAsString());
            }
        }
    }

    /**
     * Data core UI exception with statusCode and clientExeptionPayload
     *
     * @param statusCode               HttpStatus
     * @param clientExceptionPayloadVO ClientExceptionPayloadVO
     */
    public RestException(
            HttpStatus statusCode,
            ClientExceptionPayloadVO clientExceptionPayloadVO) {
        super(null, clientExceptionPayloadVO.getDetail());
        this.statusCode = statusCode;
        if (clientExceptionPayloadVO != null) {
            this.errorPayload = new ErrorPayload(
                    clientExceptionPayloadVO.getType(),
                    clientExceptionPayloadVO.getTitle(),
                    clientExceptionPayloadVO.getDetail());
        }
    }

    /**
     * Data core UI exception with status code
     *
     * @param statusCode HttpStatus
     */
    public RestException(HttpStatus statusCode) {
        super(null, statusCode.getReasonPhrase());
        this.statusCode = statusCode;
        this.errorPayload = new ErrorPayload(
                statusCode.getReasonPhrase(),
                statusCode.name(),
                statusCode.getReasonPhrase());
    }

    /**
     * Get http status
     *
     * @return Http status code
     */
    public HttpStatus getHttpStatus() {
        return this.statusCode;
    }

    /**
     * Get error payload
     *
     * @return Error payload
     */
    public ErrorPayload getErrorPayload() {
        return this.errorPayload;
    }
}