package io.storytailor.central.code;

public enum ChatProgressCode {
    DOING("doing"),
    COMPLETE("complete");

    private String code;

    private ChatProgressCode(String code){
        this.code = code;
    }

    public String getCode(){
        return code;
    }

    public static ChatProgressCode parseCode(String code){
        for (ChatProgressCode chatProgressCode : values()) {
            if (chatProgressCode.getCode().equals(code)) {
                return chatProgressCode;
            }
        }
        return null;
    }
    
}