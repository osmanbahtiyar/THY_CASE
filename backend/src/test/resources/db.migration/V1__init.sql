DROP TABLE IF EXISTS transportation;
DROP TABLE IF EXISTS "location";

CREATE SEQUENCE LOCATION_SEQ
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 50;

CREATE SEQUENCE TRANSPORTATION_SEQ
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 50;

CREATE TABLE "location" (
                            id BIGINT NOT NULL PRIMARY KEY DEFAULT nextval('LOCATION_SEQ'),
                            code VARCHAR(16) NOT NULL,
                            location_name VARCHAR(255) NOT NULL,
                            city VARCHAR(100) NOT NULL,
                            country VARCHAR(100) NOT NULL,
                            CONSTRAINT uq_location_code UNIQUE (code)
);

CREATE TABLE transportation (
                                id BIGINT NOT NULL PRIMARY KEY DEFAULT nextval('TRANSPORTATION_SEQ'),
                                origin_location_id BIGINT NOT NULL,
                                destination_location_id BIGINT NOT NULL,
                                transportation_type VARCHAR(20) NOT NULL,
                                operating_days INTEGER[],

                                CONSTRAINT fk_origin_location
                                    FOREIGN KEY(origin_location_id)
                                        REFERENCES "location"(id) ON DELETE CASCADE,
                                CONSTRAINT fk_destination_location
                                    FOREIGN KEY(destination_location_id)
                                        REFERENCES "location"(id) ON DELETE CASCADE,
                                CONSTRAINT chk_origin_and_destination_location
                                    CHECK (origin_location_id <> destination_location_id),
                                CONSTRAINT chk_operating_days_valid
                                    CHECK (operating_days IS NULL
                                        OR array_length(operating_days, 1) = 0
                                        OR operating_days <@ ARRAY[1,2,3,4,5,6,7]
),
    CONSTRAINT uq_origin_dest_type
        UNIQUE (origin_location_id, destination_location_id, transportation_type)
);