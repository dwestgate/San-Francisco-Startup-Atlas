/*****************************************************************************/
/*                                                                           */
/*                        Database Creation Queries                          */
/*                                                                           */
/*****************************************************************************/

CREATE EXTENSION citext;

DROP INDEX IF EXISTS public.addresses_zipcode_idx CASCADE;
DROP INDEX IF EXISTS public.addresses_latitude_idx CASCADE;
DROP INDEX IF EXISTS public.addresses_longitude_idx CASCADE;
DROP INDEX IF EXISTS public.located_at_startup_idx CASCADE;
DROP INDEX IF EXISTS public.people_first_name_idx CASCADE;
DROP INDEX IF EXISTS public.people_last_name_idx CASCADE;
DROP INDEX IF EXISTS public.people_alma_mata_idx CASCADE;
DROP INDEX IF EXISTS public.employed_at_name_idx CASCADE;
DROP INDEX IF EXISTS public.employed_at_people_id_idx CASCADE;
DROP INDEX IF EXISTS public.funding_rounds_startup_idx CASCADE;
DROP INDEX IF EXISTS public.funding_rounds_investor_idx CASCADE;
DROP INDEX IF EXISTS public.investors_name_idx CASCADE;
DROP INDEX IF EXISTS public.startups_name_idx CASCADE;

DROP TABLE IF EXISTS located_at;
DROP TABLE IF EXISTS employed_at;
DROP TABLE IF EXISTS people;
DROP TABLE IF EXISTS funding_rounds;
DROP TABLE IF EXISTS acquired_by;
DROP TABLE IF EXISTS investors;
DROP TABLE IF EXISTS startups;
DROP TABLE IF EXISTS addresses;
DROP TABLE IF EXISTS organizations;


CREATE TABLE organizations
(
  name CITEXT PRIMARY KEY NOT NULL
);


CREATE TABLE addresses
(
  street_address CITEXT NOT NULL PRIMARY KEY ,
  city CITEXT NOT NULL DEFAULT 'San Francisco',
  state CITEXT NOT NULL DEFAULT 'CA',
  zipcode CITEXT,
  latitude DOUBLE PRECISION,
  longitude DOUBLE PRECISION
);


CREATE TABLE located_at
(
  id serial primary key,
  startup CITEXT,
  street_address CITEXT,
  from_date CITEXT NOT NULL,
  to_date CITEXT NOT NULL,
  FOREIGN KEY (startup) REFERENCES organizations(name) ON DELETE CASCADE,
  FOREIGN KEY (street_address) REFERENCES addresses(street_address) ON DELETE CASCADE
);


CREATE TABLE people
(
  id serial primary key,
  first_name CITEXT NOT NULL,
  last_name CITEXT NOT NULL,
  alma_mata CITEXT,
  notes CITEXT
);

CREATE TABLE employed_at
(
  id serial primary key,
  name CITEXT NOT NULL,
  people_id INTEGER NOT NULL,
  role CITEXT NOT NULL,
  from_date CITEXT,
  to_date CITEXT,
  foreign key (name) references organizations(name) ON DELETE CASCADE,
  foreign key (people_id) references people(id) ON DELETE CASCADE
);

CREATE TABLE funding_rounds
(
  id serial primary key,
  startup CITEXT REFERENCES organizations(name) ON DELETE CASCADE,
  investor CITEXT REFERENCES organizations(name) ON DELETE CASCADE,
  round CITEXT NOT NULL,
  funding_date CITEXT NOT NULL ,
  amount INT NOT NULL CHECK (amount > 0),
  valuation INT NOT NULL CHECk (Valuation > 0)
);

CREATE TABLE acquired_by
(
  id serial primary key,
  acquirer CITEXT REFERENCES organizations(name) ON DELETE CASCADE NOT NULL  ,
  startup CITEXT REFERENCES organizations(name) ON DELETE CASCADE NOT NULL ,
  acquisition_date CITEXT NOT NULL ,
  acquisition_amount INT CHECK (acquisition_amount > 0)
);

CREATE TABLE investors
(
  id serial primary key,
  name CITEXT REFERENCES organizations(name) ON DELETE CASCADE
);

CREATE TABLE startups
(
  id serial primary key,
  name CITEXT REFERENCES organizations(name) ON DELETE CASCADE,
  vertical CITEXT,
  since CITEXT,
  status CITEXT NOT NULL
);

COPY organizations FROM '/Users/davidwestgate/Documents/Classes/cs333/Workspace/San-Francisco-Startup-Atlas/Organizations.csv' DELIMITER ',' HEADER CSV;
/* Addresses.csv is huge and has duplicates - so special handling here */
CREATE TEMP TABLE tmp_table
ON COMMIT DROP
AS
SELECT *
FROM addresses
     WITH NO DATA;

COPY tmp_table FROM '/Users/davidwestgate/Documents/Classes/cs333/Workspace/San-Francisco-Startup-Atlas/Addresses.csv' DELIMITER ',' HEADER CSV;

INSERT INTO addresses
  SELECT DISTINCT ON (street_address) *
FROM tmp_table;

COPY investors FROM '/Users/davidwestgate/Documents/Classes/cs333/Workspace/San-Francisco-Startup-Atlas/Investors.csv' DELIMITER ',' HEADER CSV;
COPY startups FROM '/Users/davidwestgate/Documents/Classes/cs333/Workspace/San-Francisco-Startup-Atlas/Startups.csv' DELIMITER ',' HEADER CSV;
COPY people FROM '/Users/davidwestgate/Documents/Classes/cs333/Workspace/San-Francisco-Startup-Atlas/People.csv' DELIMITER ',' HEADER CSV;
COPY located_at FROM '/Users/davidwestgate/Documents/Classes/cs333/Workspace/San-Francisco-Startup-Atlas/LocatedAt.csv' DELIMITER ',' HEADER CSV;
COPY employed_at FROM '/Users/davidwestgate/Documents/Classes/cs333/Workspace/San-Francisco-Startup-Atlas/EmployedAt.csv' DELIMITER ',' HEADER CSV;
COPY acquired_by FROM '/Users/davidwestgate/Documents/Classes/cs333/Workspace/San-Francisco-Startup-Atlas/AcquiredBy.csv' DELIMITER ',' HEADER CSV;
COPY funding_rounds FROM '/Users/davidwestgate/Documents/Classes/cs333/Workspace/San-Francisco-Startup-Atlas/FundingRounds.csv' DELIMITER ',' HEADER CSV;

CREATE INDEX ON addresses (zipcode);
CREATE INDEX ON addresses (latitude);
CREATE INDEX ON addresses (longitude);

CREATE INDEX ON located_at USING hash (startup);

CREATE INDEX ON people (first_name);
CREATE INDEX ON people (last_name);
CREATE INDEX ON people (alma_mata);

CREATE INDEX ON employed_at USING hash (name);
CREATE INDEX ON employed_at USING hash (people_id);

CREATE INDEX ON funding_rounds USING hash (startup);
CREATE INDEX ON funding_rounds USING hash (investor);

CREATE INDEX ON investors USING hash (name);

CREATE INDEX ON startups USING hash (name);
