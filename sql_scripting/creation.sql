-- DIMENSIONS

CREATE TABLE dim_vendor (
    vendor_id SMALLINT PRIMARY KEY,
    vendor_description TEXT
);

CREATE TABLE dim_datetime (
    datetime_id SERIAL PRIMARY KEY,
    datetime TIMESTAMP,
    year SMALLINT,
    month SMALLINT,
    day SMALLINT,
    hour SMALLINT,
    minute SMALLINT
);

CREATE TABLE dim_location (
    location_id INTEGER PRIMARY KEY
);

CREATE TABLE dim_payment_type (
    payment_type SMALLINT PRIMARY KEY,
    payment_description TEXT
);

CREATE TABLE dim_ratecode (
    ratecode_id SMALLINT PRIMARY KEY,
    rate_description TEXT
);

-- TABLE DE FAITS

CREATE TABLE fact_trips (
    trip_id SERIAL PRIMARY KEY,
    vendor_id SMALLINT REFERENCES dim_vendor(vendor_id),
    pickup_datetime_id INTEGER REFERENCES dim_datetime(datetime_id),
    dropoff_datetime_id INTEGER REFERENCES dim_datetime(datetime_id),
    passenger_count SMALLINT,
    trip_distance REAL,
    ratecode_id SMALLINT REFERENCES dim_ratecode(ratecode_id),
    store_and_fwd_flag CHAR(1),
    pickup_location_id INTEGER REFERENCES dim_location(location_id),
    dropoff_location_id INTEGER REFERENCES dim_location(location_id),
    payment_type SMALLINT REFERENCES dim_payment_type(payment_type),
    fare_amount NUMERIC(10, 2),
    extra NUMERIC(10, 2),
    mta_tax NUMERIC(10, 2),
    tip_amount NUMERIC(10, 2),
    tolls_amount NUMERIC(10, 2),
    improvement_surcharge NUMERIC(10, 2),
    total_amount NUMERIC(10, 2),
    congestion_surcharge NUMERIC(10, 2),
    airport_fee NUMERIC(10, 2)
);
-- Mettre des index pour fact trips