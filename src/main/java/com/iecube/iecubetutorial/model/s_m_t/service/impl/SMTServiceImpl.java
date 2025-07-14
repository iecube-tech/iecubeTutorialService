package com.iecube.iecubetutorial.model.s_m_t.service.impl;

import com.iecube.iecubetutorial.model.s_m_t.mapper.SMaterialTagMapper;
import com.iecube.iecubetutorial.model.s_m_t.service.SMTService;
import com.iecube.iecubetutorial.model.tags.mapper.TagMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SMTServiceImpl implements SMTService {

    @Autowired
    private SMaterialTagMapper SMTMapper;

    @Autowired
    private TagMapper TagMapper;
}
