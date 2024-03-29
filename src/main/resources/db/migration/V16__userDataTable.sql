DROP TABLE IF EXISTS USER_DATA;
CREATE TABLE USER_DATA
(
    ID                 BIGSERIAL NOT NULL,
    PHONE_NUMBER       VARCHAR,
    CIVILITY_ID		   BIGINT,
    PICTURE_LINK       VARCHAR,
    PSEUDO             VARCHAR,
    IBAN               VARCHAR,
    TARGETING          VARCHAR,
    ACTIVITY_ID        BIGINT,
    HOME_ADDRESS       VARCHAR,
    SOCIAL_MEDIA_LINKS VARCHAR,
    USER_ID			   BIGINT NOT NULL,
    CONSTRAINT USER_DATA_ID_PK PRIMARY KEY (ID),
    CONSTRAINT USER_DATA_USER_ID_FK FOREIGN KEY (USER_ID) REFERENCES USER_DETAILS (ID),
    CONSTRAINT USER_DATA_CIVILITY_ID_FK FOREIGN KEY (CIVILITY_ID) REFERENCES CIVILITY (ID),
    CONSTRAINT USER_DATA_ACTIVITY_ID_FK FOREIGN KEY (ACTIVITY_ID) REFERENCES ACTIVITY (ID)
);