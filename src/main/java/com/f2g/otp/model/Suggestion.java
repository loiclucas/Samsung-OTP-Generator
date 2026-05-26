package com.f2g.otp.model;

public class Suggestion {
    public int offset;
    public String code;
    public boolean recommended;

    public Suggestion() {}

    public Suggestion(int offset, String code, boolean recommended) {
        this.offset = offset;
        this.code = code;
        this.recommended = recommended;
    }
}
