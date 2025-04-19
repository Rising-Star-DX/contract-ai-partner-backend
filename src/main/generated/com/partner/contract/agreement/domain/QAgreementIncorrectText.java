package com.partner.contract.agreement.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAgreementIncorrectText is a Querydsl query type for AgreementIncorrectText
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAgreementIncorrectText extends EntityPathBase<AgreementIncorrectText> {

    private static final long serialVersionUID = -937169019L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAgreementIncorrectText agreementIncorrectText = new QAgreementIncorrectText("agreementIncorrectText");

    public final NumberPath<Double> accuracy = createNumber("accuracy", Double.class);

    public final QAgreement agreement;

    public final ListPath<AgreementIncorrectPosition, QAgreementIncorrectPosition> agreementIncorrectPositionList = this.<AgreementIncorrectPosition, QAgreementIncorrectPosition>createList("agreementIncorrectPositionList", AgreementIncorrectPosition.class, QAgreementIncorrectPosition.class, PathInits.DIRECT2);

    public final StringPath correctedText = createString("correctedText");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath incorrectText = createString("incorrectText");

    public final StringPath proofText = createString("proofText");

    public QAgreementIncorrectText(String variable) {
        this(AgreementIncorrectText.class, forVariable(variable), INITS);
    }

    public QAgreementIncorrectText(Path<? extends AgreementIncorrectText> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAgreementIncorrectText(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAgreementIncorrectText(PathMetadata metadata, PathInits inits) {
        this(AgreementIncorrectText.class, metadata, inits);
    }

    public QAgreementIncorrectText(Class<? extends AgreementIncorrectText> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.agreement = inits.isInitialized("agreement") ? new QAgreement(forProperty("agreement"), inits.get("agreement")) : null;
    }

}

