DROP TABLE IF EXISTS CONSUMER;
CREATE TABLE CONSUMER
(
    ID                  	BIGSERIAL NOT NULL,
    IP_ADDRESS          	VARCHAR NOT NULL,
    ADVERTISING_ID          BIGINT   NOT NULL,
    CONSTRAINT CONSUMER_ID_PK PRIMARY KEY (ID),
    CONSTRAINT CONSUMER_ADVERTISING_ID_FK FOREIGN KEY (ADVERTISING_ID) REFERENCES ADVERTISING (ID)
);