package com.partner.contract.agreement.repository;

import com.partner.contract.agreement.domain.Agreement;
import com.partner.contract.agreement.dto.AgreementListRequestForAndroidDto;

import java.util.List;

public interface AgreementRepositoryCustom {
    List<Agreement> findAllByConditions(AgreementListRequestForAndroidDto requestForAndroidDto);
}
