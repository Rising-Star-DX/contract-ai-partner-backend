package com.partner.contract.standard.repository;

import com.partner.contract.standard.domain.Standard;
import com.partner.contract.standard.dto.StandardListRequestForAndroidDto;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class StandardRepositoryImpl implements StandardRepositoryCustom {

    @Override
    public List<Standard> findAllByCondition(StandardListRequestForAndroidDto requestForAndroidDto) {
        return List.of();
    }
}
