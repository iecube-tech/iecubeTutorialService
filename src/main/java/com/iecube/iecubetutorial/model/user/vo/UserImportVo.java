package com.iecube.iecubetutorial.model.user.vo;

import com.iecube.iecubetutorial.model.user.dto.UserImportQo;
import lombok.Data;

import java.util.List;

@Data
public class UserImportVo {
    List<UserImportQo> filedList;
    List<UserImportQo> successList;
}
