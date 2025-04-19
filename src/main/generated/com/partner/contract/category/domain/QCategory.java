package com.partner.contract.category.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCategory is a Querydsl query type for Category
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCategory extends EntityPathBase<Category> {

    private static final long serialVersionUID = 695761825L;

    public static final QCategory category = new QCategory("category");

    public final ListPath<com.partner.contract.agreement.domain.Agreement, com.partner.contract.agreement.domain.QAgreement> agreementList = this.<com.partner.contract.agreement.domain.Agreement, com.partner.contract.agreement.domain.QAgreement>createList("agreementList", com.partner.contract.agreement.domain.Agreement.class, com.partner.contract.agreement.domain.QAgreement.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final ListPath<com.partner.contract.standard.domain.Standard, com.partner.contract.standard.domain.QStandard> standardList = this.<com.partner.contract.standard.domain.Standard, com.partner.contract.standard.domain.QStandard>createList("standardList", com.partner.contract.standard.domain.Standard.class, com.partner.contract.standard.domain.QStandard.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QCategory(String variable) {
        super(Category.class, forVariable(variable));
    }

    public QCategory(Path<? extends Category> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCategory(PathMetadata metadata) {
        super(Category.class, metadata);
    }

}

