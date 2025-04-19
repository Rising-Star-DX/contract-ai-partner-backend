package com.partner.contract.agreement.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAgreementIncorrectPosition is a Querydsl query type for AgreementIncorrectPosition
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAgreementIncorrectPosition extends EntityPathBase<AgreementIncorrectPosition> {

    private static final long serialVersionUID = -1770825855L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAgreementIncorrectPosition agreementIncorrectPosition = new QAgreementIncorrectPosition("agreementIncorrectPosition");

    public final QAgreementIncorrectText agreementIncorrectText;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> orderIndex = createNumber("orderIndex", Integer.class);

    public final NumberPath<Integer> page = createNumber("page", Integer.class);

    public final StringPath position = createString("position");

    public QAgreementIncorrectPosition(String variable) {
        this(AgreementIncorrectPosition.class, forVariable(variable), INITS);
    }

    public QAgreementIncorrectPosition(Path<? extends AgreementIncorrectPosition> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAgreementIncorrectPosition(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAgreementIncorrectPosition(PathMetadata metadata, PathInits inits) {
        this(AgreementIncorrectPosition.class, metadata, inits);
    }

    public QAgreementIncorrectPosition(Class<? extends AgreementIncorrectPosition> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.agreementIncorrectText = inits.isInitialized("agreementIncorrectText") ? new QAgreementIncorrectText(forProperty("agreementIncorrectText"), inits.get("agreementIncorrectText")) : null;
    }

}

