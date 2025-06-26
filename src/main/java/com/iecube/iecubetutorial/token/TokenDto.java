package com.iecube.iecubetutorial.token;

import com.iecube.iecubetutorial.model_admin.user.entity.AUser;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenDto {
    private String accessToken;
    private String refreshToken;
    private AUser user;

    public TokenDto() {}

    public TokenDto(String accessToken, String refreshToken, AUser user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.user = user;
    }
}
