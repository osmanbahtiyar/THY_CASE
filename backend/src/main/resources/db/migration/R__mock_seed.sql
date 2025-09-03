BEGIN;

-- Locations
INSERT INTO "location" (code, location_name, city, country) VALUES
                                                                ('IST','Istanbul Airport','Istanbul','Turkey'),
                                                                ('SAW','Sabiha Gokcen Airport','Istanbul','Turkey'),
                                                                ('CCIST','Istanbul City Center','Istanbul','Turkey'),
                                                                ('TAKSIM','Taksim Square','Istanbul','Turkey'),
                                                                ('ESB','Esenboga Airport','Ankara','Turkey'),
                                                                ('LHR','London Heathrow Airport','London','United Kingdom'),
                                                                ('LONCC','London City Center','London','United Kingdom'),
                                                                ('WEMBLEY','Wembley Stadium','London','United Kingdom')
    ON CONFLICT (code) DO NOTHING;

-- Transportations
INSERT INTO transportation (origin_location_id, destination_location_id, transportation_type, operating_days)
VALUES ((SELECT id FROM "location" WHERE code='TAKSIM'), (SELECT id FROM "location" WHERE code='IST'), 'SUBWAY', ARRAY[1,2,3,4,5,6,7])
    ON CONFLICT (origin_location_id, destination_location_id, transportation_type) DO NOTHING;
INSERT INTO transportation (origin_location_id, destination_location_id, transportation_type, operating_days)
VALUES ((SELECT id FROM "location" WHERE code='TAKSIM'), (SELECT id FROM "location" WHERE code='IST'), 'BUS', ARRAY[1,2,3,4,5,6,7])
    ON CONFLICT (origin_location_id, destination_location_id, transportation_type) DO NOTHING;
INSERT INTO transportation (origin_location_id, destination_location_id, transportation_type, operating_days)
VALUES ((SELECT id FROM "location" WHERE code='TAKSIM'), (SELECT id FROM "location" WHERE code='SAW'), 'BUS', ARRAY[1,2,3,4,5,6,7])
    ON CONFLICT (origin_location_id, destination_location_id, transportation_type) DO NOTHING;
INSERT INTO transportation (origin_location_id, destination_location_id, transportation_type, operating_days)
VALUES ((SELECT id FROM "location" WHERE code='CCIST'), (SELECT id FROM "location" WHERE code='IST'), 'BUS', ARRAY[1,2,3,4,5,6,7])
    ON CONFLICT (origin_location_id, destination_location_id, transportation_type) DO NOTHING;
INSERT INTO transportation (origin_location_id, destination_location_id, transportation_type, operating_days)
VALUES ((SELECT id FROM "location" WHERE code='CCIST'), (SELECT id FROM "location" WHERE code='SAW'), 'BUS', ARRAY[1,2,3,4,5,6,7])
    ON CONFLICT (origin_location_id, destination_location_id, transportation_type) DO NOTHING;
INSERT INTO transportation (origin_location_id, destination_location_id, transportation_type, operating_days)
VALUES ((SELECT id FROM "location" WHERE code='ESB'), (SELECT id FROM "location" WHERE code='IST'), 'FLIGHT', ARRAY[1,2,3,4,5,6,7])
    ON CONFLICT (origin_location_id, destination_location_id, transportation_type) DO NOTHING;
INSERT INTO transportation (origin_location_id, destination_location_id, transportation_type, operating_days)
VALUES ((SELECT id FROM "location" WHERE code='IST'), (SELECT id FROM "location" WHERE code='ESB'), 'FLIGHT', ARRAY[1,2,3,4,5,6,7])
    ON CONFLICT (origin_location_id, destination_location_id, transportation_type) DO NOTHING;
INSERT INTO transportation (origin_location_id, destination_location_id, transportation_type, operating_days)
VALUES ((SELECT id FROM "location" WHERE code='IST'), (SELECT id FROM "location" WHERE code='LHR'), 'FLIGHT', ARRAY[1,3,5,7])
    ON CONFLICT (origin_location_id, destination_location_id, transportation_type) DO NOTHING;
INSERT INTO transportation (origin_location_id, destination_location_id, transportation_type, operating_days)
VALUES ((SELECT id FROM "location" WHERE code='SAW'), (SELECT id FROM "location" WHERE code='LHR'), 'FLIGHT', ARRAY[2,4,6])
    ON CONFLICT (origin_location_id, destination_location_id, transportation_type) DO NOTHING;
INSERT INTO transportation (origin_location_id, destination_location_id, transportation_type, operating_days)
VALUES ((SELECT id FROM "location" WHERE code='LHR'), (SELECT id FROM "location" WHERE code='LONCC'), 'SUBWAY', ARRAY[1,2,3,4,5,6,7])
    ON CONFLICT (origin_location_id, destination_location_id, transportation_type) DO NOTHING;
INSERT INTO transportation (origin_location_id, destination_location_id, transportation_type, operating_days)
VALUES ((SELECT id FROM "location" WHERE code='LHR'), (SELECT id FROM "location" WHERE code='LONCC'), 'BUS', ARRAY[1,2,3,4,5,6,7])
    ON CONFLICT (origin_location_id, destination_location_id, transportation_type) DO NOTHING;
INSERT INTO transportation (origin_location_id, destination_location_id, transportation_type, operating_days)
VALUES ((SELECT id FROM "location" WHERE code='LHR'), (SELECT id FROM "location" WHERE code='WEMBLEY'), 'BUS', ARRAY[1,2,3,4,5,6,7])
    ON CONFLICT (origin_location_id, destination_location_id, transportation_type) DO NOTHING;
INSERT INTO transportation (origin_location_id, destination_location_id, transportation_type, operating_days)
VALUES ((SELECT id FROM "location" WHERE code='LONCC'), (SELECT id FROM "location" WHERE code='WEMBLEY'), 'SUBWAY', ARRAY[1,2,3,4,5,6,7])
    ON CONFLICT (origin_location_id, destination_location_id, transportation_type) DO NOTHING;
INSERT INTO transportation (origin_location_id, destination_location_id, transportation_type, operating_days)
VALUES ((SELECT id FROM "location" WHERE code='LONCC'), (SELECT id FROM "location" WHERE code='WEMBLEY'), 'BUS', ARRAY[1,2,3,4,5,6,7])
    ON CONFLICT (origin_location_id, destination_location_id, transportation_type) DO NOTHING;

COMMIT;