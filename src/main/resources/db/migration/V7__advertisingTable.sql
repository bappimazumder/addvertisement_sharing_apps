DROP TABLE IF EXISTS ADVERTISING;
CREATE TABLE ADVERTISING
(
    ID                          BIGSERIAL          NOT NULL,
    REFERENCE                   VARCHAR(64) UNIQUE NOT NULL,
    START_DATE                  TIMESTAMP          NOT NULL,
    END_DATE                    TIMESTAMP          NOT NULL,
    BUDGET                      NUMERIC            NOT NULL,
    RATE                        NUMERIC            NOT NULL,
    STATUS                      VARCHAR            NOT NULL,
    TARGETING                   VARCHAR            NOT NULL,
    DESCRIPTION_POST            VARCHAR,
    NB_CLICKS_GENERATED         INTEGER,
    DESCRIPTION_ADD             VARCHAR,
    REMUNERATION                NUMERIC,
    LINK_URL_ENTERPRISE_PRODUCT VARCHAR,
    IS_AVAILABLE                BOOLEAN,
    IS_DELETED                  BOOLEAN,
    IS_HEADER_LOCATION          BOOLEAN,
    COMPANY_ID                  BIGINT,
    TITLE_OF_ADVERTISEMENT      VARCHAR,
    NAME_OF_COMPANY             VARCHAR,
    DESCRIPTION_OF_COMPANY      VARCHAR,
    SAMPLE_TITLE                VARCHAR,
    SAMPLE_DESCRIPTION          VARCHAR,
    CONSTRAINT ADVERTISING_PK PRIMARY KEY (ID),
    CONSTRAINT ADVERTISING_COMPANY_ID_FK FOREIGN KEY (COMPANY_ID) REFERENCES COMPANY (ID)
);