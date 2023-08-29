package io.storytailor.central.code;

public enum ChatTypeCode {
    USER("human"),
    AI("ai");

    private String code;

    private ChatTypeCode(String code){
        this.code = code;
    }

    public String getCode(){
        return code;
    }

    public static ChatTypeCode parseCode(String code){
        for (ChatTypeCode chatTypeCode : values()) {
            if (chatTypeCode.getCode().equals(code)) {
                return chatTypeCode;
            }
        }
        return null;
    }
    
}
