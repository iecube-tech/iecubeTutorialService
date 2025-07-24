package com.iecube.iecubetutorial.model.mOutline.service;


import com.iecube.iecubetutorial.model.mOutline.entity.MOutline;
import com.iecube.iecubetutorial.model.materials.qo.MaterialQo;

public interface MOutlineService {

    MOutline genMOutline(MaterialQo materialQo);
}
