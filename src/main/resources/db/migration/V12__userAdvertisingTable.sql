DROP TABLE IF EXISTS USER_ADVERTISING;
CREATE TABLE USER_ADVERTISING
(
    ID                          BIGSERIAL NOT NULL,
    USER_ID						BIGINT NOT NULL,
    ADVERTISING_ID				BIGINT NOT NULL,
    PROMO_CODE                  VARCHAR,
    LINK_GENERATED              VARCHAR,
    PRICE_GENERATED             NUMERIC,
    CONSTRAINT USER_ADVERTISING_ID_PK PRIMARY KEY (ID),
    CONSTRAINT USER_ADVERTISING_USER_ID_FK FOREIGN KEY (USER_ID) REFERENCES USER_DETAILS (ID),
    CONSTRAINT USER_ADVERTISING_ADVERTISING_ID_FK FOREIGN KEY (ADVERTISING_ID) REFERENCES ADVERTISING (ID)
);