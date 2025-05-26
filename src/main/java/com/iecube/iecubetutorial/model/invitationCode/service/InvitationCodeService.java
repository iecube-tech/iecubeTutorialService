package com.iecube.iecubetutorial.model.invitationCode.service;

import com.iecube.iecubetutorial.model.invitationCode.entity.InvitationCode;

public interface InvitationCodeService {
    InvitationCode createInvitationCode(String invitationCode);

    void bindCodeToEmail(InvitationCode invitationCode, String email);

    void applyCode(String email);

    InvitationCode applyCodeByEmail(String email);

    boolean bindCodeToUser(String code, Long userId);
}
