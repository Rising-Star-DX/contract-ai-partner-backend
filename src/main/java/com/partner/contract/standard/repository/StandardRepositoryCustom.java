package com.partner.contract.standard.repository;

import com.partner.contract.standard.domain.Standard;
import com.partner.contract.standard.dto.StandardListRequestForAndroidDto;

import java.util.List;

public interface StandardRepositoryCustom {
    List<Standard> findAllByCondition(StandardListRequestForAndroidDto requestForAndroidDto);
}
