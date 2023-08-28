package io.storytailor.central.config.rest;

import lombok.Data;

@Data
public class ErrorPayload {
    private String type;
    private String title;
    private String detail;
    private String debugMessage;

    /**
     * Constructor of ErrorPayload class.
     * 
     * @param type   Error type
     * @param title  Error title
     * @param detail Errot detail message
     */
    public ErrorPayload(String type, String title, String detail) {
        this.setType(type);
        this.setTitle(title);
        this.setDetail(detail);
    }

    /**
     * Constructor of ErrorPayload class.
     * 
     * @param type         Error type
     * @param title        Error title
     * @param detail       Error detail message
     * @param debugMessage Debug message
     */
    public ErrorPayload(String type, String title, String detail, String debugMessage) {
        this.setType(type);
        this.setTitle(title);
        this.setDetail(detail);
        this.setDebugMessage(debugMessage);
    }
}