package com.partner.contract.agreement.repository;

import com.partner.contract.agreement.domain.Agreement;
import com.partner.contract.agreement.domain.QAgreement;
import com.partner.contract.agreement.dto.AgreementListRequestForAndroidDto;
import com.partner.contract.category.domain.QCategory;
import com.partner.contract.common.enums.AiStatus;
import com.partner.contract.common.enums.FileStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.List;

@RequiredArgsConstructor
public class AgreementRepositoryImpl implements AgreementRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Agreement> findAllByConditions(AgreementListRequestForAndroidDto requestForAndroidDto) {
        BooleanBuilder builder = new BooleanBuilder();
        QAgreement qAgreement = QAgreement.agreement;
        QCategory qCategory = QCategory.category;

        // status
        if(requestForAndroidDto.getStatus() != null && !requestForAndroidDto.getStatus().isEmpty()){
            BooleanBuilder statusBuilder = new BooleanBuilder();
            for (String status : requestForAndroidDto.getStatus()) {
                switch (status.toUpperCase()) {
                    case "ANALYZING":
                        statusBuilder.or(qAgreement.fileStatus.eq(FileStatus.SUCCESS)
                                .and(qAgreement.aiStatus.eq(AiStatus.ANALYZING)));
                        break;
                    case "SUCCESS":
                        statusBuilder.or(qAgreement.fileStatus.eq(FileStatus.SUCCESS)
                                .and(qAgreement.aiStatus.eq(AiStatus.SUCCESS)));
                        break;
                    case "AI-FAILED":
                        statusBuilder.or(qAgreement.fileStatus.eq(FileStatus.SUCCESS)
                                .and(qAgreement.aiStatus.eq(AiStatus.FAILED)));
                        break;
                    default:
                        statusBuilder.or(Expressions.FALSE);
                        break;
                }
            }
            builder.and(statusBuilder);
        } else {
            builder.and(
                    qAgreement.fileStatus.eq(FileStatus.SUCCESS)
                    .and(qAgreement.aiStatus.isNotNull()));
        }

        // type
        if(requestForAndroidDto.getType() != null && !requestForAndroidDto.getType().isEmpty()){
            builder.and(qAgreement.type.in(requestForAndroidDto.getType()));
        }

        //categoryId
        if(requestForAndroidDto.getCategoryId() != null){
            builder.and(qAgreement.category.id.eq(requestForAndroidDto.getCategoryId()));
        }

        //name
        if(StringUtils.hasText(requestForAndroidDto.getName())){
            builder.and(qAgreement.name.containsIgnoreCase(requestForAndroidDto.getName()));
        }

        JPAQuery<Agreement> query = jpaQueryFactory
                .selectFrom(qAgreement)
                .innerJoin(qAgreement.category, qCategory).fetchJoin()
                .where(builder);

        //sort
        if(requestForAndroidDto.getSortBy() != null && !requestForAndroidDto.getSortBy().isEmpty()){
            for(int i=0; i<requestForAndroidDto.getSortBy().size(); i++){
                String sortKey = requestForAndroidDto.getSortBy().get(i);
                Boolean asc = Boolean.TRUE.equals(requestForAndroidDto.getAsc().get(i));
                OrderSpecifier<?> orderSpecifier = getOrderSpecifier(sortKey, asc, qAgreement);
                if(orderSpecifier != null){
                    query.orderBy(orderSpecifier);
                }
            }
        } else {
            query.orderBy(qAgreement.createdAt.desc());
        }

        return query.fetch();
    }

    private OrderSpecifier<?> getOrderSpecifier(String sortKey, boolean asc, QAgreement qAgreement) {
        return switch (sortKey) {
            case "name" -> asc ? qAgreement.name.asc() : qAgreement.name.desc();
            case "createdAt" -> asc ? qAgreement.createdAt.asc() : qAgreement.createdAt.desc();
            default -> null;
        };
    }
}
