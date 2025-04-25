-- insertion_dblink.sql (modèle en étoile avec dblink)

-- 1. Activer l'extension dblink (si ce n'est pas déjà fait)
CREATE EXTENSION IF NOT EXISTS dblink;

-- 2. Connexion à l'autre serveur (Data Warehouse)
-- insertion_dblink.sql (modèle en étoile avec dblink)

-- 1. Activer l'extension dblink (si ce n'est pas déjà fait)
CREATE EXTENSION IF NOT EXISTS dblink;

-- 2. Connexion à l'autre serveur (Data Warehouse)
-- Le nom de connexion ici est 'dwh_conn'
SELECT dblink_connect('dwh_conn',
  'host=data-warehouse port=5432 dbname=dbwarehouse user=postgres password=admin');

-- 3. Insertions dans les dimensions
-- potentiellement a partir d'ici que ca bug, connection dblink_connect Surement OK
-- eVeriifier pouyr debuger
-- dim_vendor
INSERT INTO dim_vendor (vendor_id, vendor_description)
VALUES
  (1, 'Creative Mobile Technologies, LLC'),
  (2, 'Curb Mobility, LLC'),
  (6, 'Myle Technologies Inc'),
  (7, 'Helix')
ON CONFLICT DO NOTHING;
SELECT DISTINCT vendorid FROM dblink('dwh_conn',
  'SELECT DISTINCT "vendorid" AS vendorid FROM public.nyc_yellow_taxi_trips')
AS t(vendorid SMALLINT);

-- dim_payment_type
INSERT INTO dim_payment_type (payment_type, payment_description)
VALUES
  (0, 'Flex Fare trip'),
  (1, 'Credit card'),
  (2, 'Cash'),
  (3, 'No charge'),
  (4, 'Dispute'),
  (5, 'Unknown'),
  (6, 'Voided trip')
ON CONFLICT DO NOTHING;

-- dim_ratecode
INSERT INTO dim_ratecode (ratecode_id, rate_description)
VALUES
  (1, 'Standard rate'),
  (2, 'JFK'),
  (3, 'Newark'),
  (4, 'Nassau or Westchester'),
  (5, 'Negotiated fare'),
  (6, 'Group ride'),
  (99, 'Null/unknown')
ON CONFLICT DO NOTHING;

-- dim_location (pickup + dropoff)
INSERT INTO dim_location (location_id)
SELECT DISTINCT location_id FROM (
  SELECT location_id FROM dblink('dwh_conn',
    'SELECT DISTINCT "pulocationid" FROM public.nyc_yellow_taxi_trips')
  AS t(location_id INTEGER)
  UNION
  SELECT "dolocationid" FROM dblink('dwh_conn',
    'SELECT DISTINCT "dolocationid" FROM public.nyc_yellow_taxi_trips')
  AS t(dolocationid INTEGER)
) AS sub;

-- dim_datetime
INSERT INTO dim_datetime (datetime)
SELECT DISTINCT datetime FROM (
  SELECT "tpep_pickup_datetime" AS datetime FROM dblink('dwh_conn',
    'SELECT DISTINCT "tpep_pickup_datetime" FROM public.nyc_yellow_taxi_trips')
  AS t(tpep_pickup_datetime TIMESTAMP)
  UNION
  SELECT "tpep_dropoff_datetime" FROM dblink('dwh_conn',
    'SELECT DISTINCT "tpep_dropoff_datetime" FROM public.nyc_yellow_taxi_trips')
  AS t(tpep_dropoff_datetime TIMESTAMP)
) AS sub;

-- Update des champs dérivés
UPDATE dim_datetime
SET
  year = EXTRACT(YEAR FROM datetime),
  month = EXTRACT(MONTH FROM datetime),
  day = EXTRACT(DAY FROM datetime),
  hour = EXTRACT(HOUR FROM datetime),
  minute = EXTRACT(MINUTE FROM datetime)
WHERE year IS NULL;

-- 4. Insertion dans la table de faits
INSERT INTO fact_trips (
    vendor_id,
    pickup_datetime_id,
    dropoff_datetime_id,
    passenger_count,
    trip_distance,
    ratecode_id,
    store_and_fwd_flag,
    pickup_location_id,
    dropoff_location_id,
    payment_type,
    fare_amount,
    extra,
    mta_tax,
    tip_amount,
    tolls_amount,
    improvement_surcharge,
    total_amount,
    congestion_surcharge,
    airport_fee
)
SELECT
    t.vendor_id,
    dp.datetime_id,
    dd.datetime_id,
    t.passenger_count,
    t.trip_distance,
    t.RatecodeID,
    t.store_and_fwd_flag,
    t.pulocationid,
    t.dolocationid,
    t.payment_type,
    t.fare_amount,
    t.extra,
    t.mta_tax,
    t.tip_amount,
    t.tolls_amount,
    t.improvement_surcharge,
    t.total_amount,
    t.congestion_surcharge,
    t.airport_fee
FROM dblink('dwh_conn',
  'SELECT * FROM public.nyc_yellow_taxi_trips')
AS t(
  vendor_id SMALLINT,
  tpep_pickup_datetime TIMESTAMP,
  tpep_dropoff_datetime TIMESTAMP,
  passenger_count SMALLINT,
  trip_distance REAL,
  RatecodeID SMALLINT,
  store_and_fwd_flag CHAR(1),
  pulocationid INTEGER,
  dolocationid INTEGER,
  payment_type SMALLINT,
  fare_amount NUMERIC(10,2),
  extra NUMERIC(10,2),
  mta_tax NUMERIC(10,2),
  tip_amount NUMERIC(10,2),
  tolls_amount NUMERIC(10,2),
  improvement_surcharge NUMERIC(10,2),
  total_amount NUMERIC(10,2),
  congestion_surcharge NUMERIC(10,2),
  Airport_fee NUMERIC(10,2)
)
JOIN dim_datetime dp ON t.tpep_pickup_datetime = dp.datetime
JOIN dim_datetime dd ON t.tpep_dropoff_datetime = dd.datetime;

-- 5. Fermer la connexion dblink
SELECT dblink_disconnect('dwh_conn');
