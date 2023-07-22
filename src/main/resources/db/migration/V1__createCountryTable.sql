CREATE TABLE COUNTRY
(
    ID                  		bigserial PRIMARY KEY,
    NAME          			    VARCHAR(64) UNIQUE NOT NULL,
    COUNTRY_CODE                VARCHAR(64),
    CREATED_BY           		VARCHAR(64),
    CREATE_TIME         		TIMESTAMP(6),
    EDITED_BY           		VARCHAR(64),
    EDIT_TIME           		TIMESTAMP(6),
    INTERNAL_VERSION    		BIGINT      DEFAULT 0
);