package com.iecube.iecubetutorial.model.invitationCode.mapper;

import com.iecube.iecubetutorial.model.invitationCode.entity.InvitationCode;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface InvitationCodeMapper {
    int addInvitationCode(InvitationCode invitationCode);
    int updateInvitationCode(InvitationCode invitationCode);
    InvitationCode getInvitationCode(String invitationCode);
    InvitationCode getInvitationCodeByEmail(String email);

}
