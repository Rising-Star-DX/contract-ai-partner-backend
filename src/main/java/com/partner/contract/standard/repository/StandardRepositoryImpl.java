package com.partner.contract.standard.repository;

import com.partner.contract.category.domain.QCategory;
import com.partner.contract.common.enums.AiStatus;
import com.partner.contract.common.enums.FileStatus;
import com.partner.contract.standard.domain.QStandard;
import com.partner.contract.standard.domain.Standard;
import com.partner.contract.standard.dto.StandardListRequestForAndroidDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.List;

@RequiredArgsConstructor
public class StandardRepositoryImpl implements StandardRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Standard> findAllByConditions(StandardListRequestForAndroidDto requestForAndroidDto) {
        BooleanBuilder builder = new BooleanBuilder();
        QStandard qStandard = QStandard.standard;
        QCategory qCategory = QCategory.category;

        // status
        if(requestForAndroidDto.getStatus() != null && !requestForAndroidDto.getStatus().isEmpty()){
            BooleanBuilder statusBuilder = new BooleanBuilder();
            for (String status : requestForAndroidDto.getStatus()) {
                switch (status.toUpperCase()) {
                    case "ANALYZING":
                        statusBuilder.or(qStandard.fileStatus.eq(FileStatus.SUCCESS)
                                .and(qStandard.aiStatus.eq(AiStatus.ANALYZING)));
                        break;
                    case "SUCCESS":
                        statusBuilder.or(qStandard.fileStatus.eq(FileStatus.SUCCESS)
                                .and(qStandard.aiStatus.eq(AiStatus.SUCCESS)));
                        break;
                    case "AI-FAILED":
                        statusBuilder.or(qStandard.fileStatus.eq(FileStatus.SUCCESS)
                                .and(qStandard.aiStatus.eq(AiStatus.FAILED)));
                        break;
                    default:
                        statusBuilder.or(Expressions.FALSE);
                        break;
                }
            }
            builder.and(statusBuilder);
        }

        // type
        if(requestForAndroidDto.getType() != null && !requestForAndroidDto.getType().isEmpty()){
            builder.and(qStandard.type.in(requestForAndroidDto.getType()));
        }

        //categoryId
        if(requestForAndroidDto.getCategoryId() != null){
            builder.and(qStandard.category.id.eq(requestForAndroidDto.getCategoryId()));
        }

        //name
        if(StringUtils.hasText(requestForAndroidDto.getName())){
            builder.and(qStandard.name.containsIgnoreCase(requestForAndroidDto.getName()));
        }

        JPAQuery<Standard> query = jpaQueryFactory
                .selectFrom(qStandard)
                .innerJoin(qStandard.category, qCategory).fetchJoin()
                .where(builder);

        //sort
        if(requestForAndroidDto.getSortBy() != null && !requestForAndroidDto.getSortBy().isEmpty()){
            for(int i=0; i<requestForAndroidDto.getSortBy().size(); i++){
                String sortKey = requestForAndroidDto.getSortBy().get(i);
                Boolean asc = requestForAndroidDto.getAsc() != null
                        && requestForAndroidDto.getSortBy().size() == requestForAndroidDto.getAsc().size()
                        && Boolean.TRUE.equals(requestForAndroidDto.getAsc().get(i));
                OrderSpecifier<?> orderSpecifier = getOrderSpecifier(sortKey, asc, qStandard);
                if(orderSpecifier != null){
                    query.orderBy(orderSpecifier);
                }
            }
        } else {
            query.orderBy(qStandard.createdAt.desc());
        }

        return query.fetch();
    }

    private OrderSpecifier<?> getOrderSpecifier(String sortKey, boolean asc, QStandard qStandard) {
        return switch (sortKey) {
            case "name" -> asc ? qStandard.name.asc() : qStandard.name.desc();
            case "createdAt" -> asc ? qStandard.createdAt.asc() : qStandard.createdAt.desc();
            default -> null;
        };
    }
}
