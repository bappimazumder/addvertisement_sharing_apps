CREATE TABLE USER_DETAILS
(
    ID                     bigserial PRIMARY KEY,
    USERNAME          	    VARCHAR(64) UNIQUE NOT NULL,
    FIRST_NAME             VARCHAR(64),
    ENABLED                BOOLEAN NOT NULL,
    LAST_NAME              VARCHAR(64),
    EMAIL                  VARCHAR(64) UNIQUE,
    USER_REF_ID            VARCHAR(64) UNIQUE NOT NULL,
    HOME_ADDRESS           VARCHAR(64),
    DATE_OF_BIRTH          TIMESTAMP(6),
    POSTAL_CODE            VARCHAR(64),
    COUNTRY_ID             BIGINT DEFAULT NULL,
    REGION_ID              BIGINT DEFAULT NULL,
    CREATED_BY           	 VARCHAR(64),
    CREATE_TIME         	 TIMESTAMP(6),
    EDITED_BY           	 VARCHAR(64),
    EDIT_TIME           	 TIMESTAMP(6),
    INTERNAL_VERSION    	 BIGINT      DEFAULT 0
);