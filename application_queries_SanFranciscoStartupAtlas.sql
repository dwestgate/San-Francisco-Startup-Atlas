
/*****************************************************************************/
/*                                                                           */
/*                        Applicaton Queries                                 */
/*                                                                           */
/*****************************************************************************/

/* Validation query */
SELECT table_name FROM information_schema.tables WHERE table_schema = 'public';


/* Given a startup zipcode, find startups in that zipcode */
select startups.name,
  startups.vertical,
  startups.since,
  addresses.street_address,
  addresses.latitude,
  addresses.longitude,
  count(funding_rounds.amount) as rounds,
  sum(funding_rounds.amount) as total_funding
from startups
  join located_at on startups.name = located_at.startup
  join addresses on located_at.street_address = addresses.street_address
  join funding_rounds on startups.name = funding_rounds.startup
where addresses.zipcode='94107'
group by startups.name,
  startups.vertical,
  startups.since,
  addresses.street_address,
  addresses.latitude,
  addresses.longitude;


/* Given a startup name, find the current location of that startup */
select startups.name,
  startups.vertical,
  startups.since,
  addresses.street_address,
  addresses.latitude,
  addresses.longitude,
  count(funding_rounds.amount) as rounds,
  sum(funding_rounds.amount) as total_funding
from startups
  join located_at on startups.name = located_at.startup
  join addresses on located_at.street_address = addresses.street_address
  join funding_rounds on startups.name = funding_rounds.startup
where startups.name = 'Yelp'
group by startups.name,
  startups.vertical,
  startups.since,
  addresses.street_address,
  addresses.latitude,
  addresses.longitude;



/* Given the name of an individual, list all startups he/she has been involved with*/
select startups.name,
  startups.vertical,
  startups.since,
  addresses.street_address,
  addresses.latitude,
  addresses.longitude,
  count(funding_rounds.amount) as rounds,
  sum(funding_rounds.amount) as total_funding,
  employed_at.role,
  employed_at.from_date,
  employed_at.to_date
from employed_at
  join people on people.id = employed_at.people_id
  join startups on startups.name = employed_at.name
  join located_at on startups.name = located_at.startup
  join addresses on located_at.street_address = addresses.street_address
  join funding_rounds on startups.name = funding_rounds.startup
where people.first_name='Bob' and
      people.last_name='Goodson'
group by startups.name,
  startups.vertical,
  startups.since,
  addresses.street_address,
  addresses.latitude,
  addresses.longitude,
  employed_at.role,
  employed_at.from_date,
  employed_at.to_date;


/* Given the name of an investor, list all the startups the investor has invested in*/
select startups.name,
  startups.vertical,
  startups.since,
  addresses.street_address,
  addresses.latitude,
  addresses.longitude,
  count(funding_rounds.amount) as rounds,
  sum(funding_rounds.amount) as total_funding,
  investors.name
from investors
  join funding_rounds on funding_rounds.investor = investors.name
  join startups on funding_rounds.startup = startups.name
  join located_at on startups.name = located_at.startup
  join addresses on located_at.street_address = addresses.street_address
where investors.name = 'Angelrush Ventures'
group by startups.name,
  startups.vertical,
  startups.since,
  addresses.street_address,
  addresses.latitude,
  addresses.longitude,
  investors.name;




/*****************************************************************************/
/*                                                                           */
/*                        Data Cleanup Queries                               */
/*                                                                           */
/*****************************************************************************/

DROP TABLE IF EXISTS cb_organizations;
CREATE TABLE cb_organizations
(
  name CITEXT NOT NULL ,
  type CITEXT NOT NULL,
  PRIMARY KEY(name, type)
);
COPY cb_organizations FROM '/Users/davidwestgate/Documents/Classes/cs333/Workspace/San-Francisco-Startup-Atlas/cb_organizations.csv' DELIMITER ',' HEADER CSV;


DROP TABLE IF EXISTS cb_people;
create table cb_people
(
  organization CITEXT NOT NULL ,
  first_name CITEXT NOT NULL ,
  last_name CITEXT NOT NULL ,
  title CITEXT,
  PRIMARY KEY(organization, first_name, last_name, title)
);
COPY cb_people FROM '/Users/davidwestgate/Documents/Classes/cs333/Workspace/San-Francisco-Startup-Atlas/cb_people.csv' DELIMITER ',' HEADER CSV;



DROP TABLE IF EXISTS city_addresses;
CREATE TABLE city_addresses
(
  company CITEXT NOT NULL ,
  street_address CITEXT NOT NULL ,
  city CITEXT NOT NULL DEFAULT 'San Francisco',
  state CITEXT NOT NULL DEFAULT 'CA',
  zipcode CITEXT,
  latitude DOUBLE PRECISION,
  longitude DOUBLE PRECISION,
  PRIMARY KEY(company, street_address)
);
DROP TABLE IF EXISTS tmp_table;
CREATE TEMP TABLE tmp_table
ON COMMIT DROP
AS
  SELECT *
  FROM city_addresses
WITH NO DATA;

COPY tmp_table FROM '/Users/davidwestgate/Documents/Classes/cs333/Workspace/San-Francisco-Startup-Atlas/city_addresses.csv' DELIMITER ',' HEADER CSV;

INSERT INTO city_addresses
  SELECT DISTINCT ON (company, street_address) *
  FROM tmp_table;





select * from cb_organizations
where type = 'company' and name = 'blinq';

select city_addresses.company from city_addresses
where city_addresses.company = ' blinq';


select cb_organizations.name,
  city_addresses.company,
  city_addresses.street_address,
  city_addresses.zipcode,
  city_addresses.latitude,
  city_addresses.longitude
from city_addresses
  join cb_organizations on cb_organizations.name = city_addresses.company
where cb_organizations.type = 'company'
ORDER BY cb_organizations.name ASC;



select cb_organizations.name,
  city_addresses.company,
  city_addresses.street_address,
  city_addresses.zipcode,
  city_addresses.latitude,
  city_addresses.longitude,
  cb_people.first_name,
  cb_people.last_name,
  cb_people.title
from city_addresses
  join cb_organizations on cb_organizations.name = city_addresses.company
  join cb_people on cb_organizations.name = cb_people.organization
where cb_organizations.type = 'company'
ORDER BY cb_organizations.name ASC;