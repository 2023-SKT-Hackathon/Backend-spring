package io.storytailor.central.code;

public enum ImageCode {
    ORIGIN("origin"),
    EXPAND("expand"),
    AI("ai");

    private String code;

    private ImageCode(String code){
        this.code = code;
    }

    public String getCode(){
        return code;
    }

    public static ImageCode parseCode(String code){
        for (ImageCode imageCode : values()) {
            if (imageCode.getCode().equals(code)) {
                return imageCode;
            }
        }
        return null;
    }
    
}
