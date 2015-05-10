import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Handles all database-related actions. Uses singleton design pattern.
 */
public class DatabaseHandler {

    // private static Logger log = LogManager.getLogger();

    /**
     * Makes sure only one database handler is instantiated.
     */
    private static DatabaseHandler singleton = new DatabaseHandler();


    static final String SEARCH_STARTUPS_BY_ZIPCODE_SQL = "SELECT startups.name," +
                                                         "  startups.vertical," +
                                                         "  startups.since," +
                                                         "  addresses.street_address," +
                                                         "  addresses.latitude," +
                                                         "  addresses.longitude," +
                                                         "  count(funding_rounds.amount) AS rounds," +
                                                         "  sum(funding_rounds.amount) AS total_funding" +
                                                         " FROM startups" +
                                                         "  JOIN located_at ON startups.name = located_at.startup" +
                                                         "  JOIN addresses ON located_at.street_address = addresses.street_address" +
                                                         "  JOIN funding_rounds ON startups.name = funding_rounds.startup" +
                                                         " WHERE addresses.zipcode = ?" +
                                                         " GROUP BY startups.name," +
                                                         "  startups.vertical," +
                                                         "  startups.since," +
                                                         "  addresses.street_address," +
                                                         "  addresses.latitude," +
                                                         "  addresses.longitude;";


    private static final String SEARCH_FOR_STARTUP_BY_NAME = "SELECT startups.name," +
                                                             "  startups.vertical," +
                                                             "  startups.since," +
                                                             "  addresses.street_address," +
                                                             "  addresses.latitude," +
                                                             "  addresses.longitude," +
                                                             "  count(funding_rounds.amount) AS rounds," +
                                                             "  sum(funding_rounds.amount) AS total_funding" +
                                                             " FROM startups" +
                                                             "  JOIN located_at ON startups.name = located_at.startup" +
                                                             "  JOIN addresses ON located_at.street_address = addresses.street_address" +
                                                             "  JOIN funding_rounds ON startups.name = funding_rounds.startup" +
                                                             " WHERE startups.name = ? " +
                                                             " GROUP BY startups.name," +
                                                             "  startups.vertical," +
                                                             "  startups.since," +
                                                             "  addresses.street_address," +
                                                             "  addresses.latitude," +
                                                             "  addresses.longitude;";


    private static final String SEARCH_FOR_STARTUP_BY_INDIVIDUAL = "SELECT startups.name," +
                                                                   "  startups.vertical," +
                                                                   "  startups.since," +
                                                                   "  addresses.street_address," +
                                                                   "  addresses.latitude," +
                                                                   "  addresses.longitude," +
                                                                   "  count(funding_rounds.amount) AS rounds," +
                                                                   "  sum(funding_rounds.amount) AS total_funding," +
                                                                   "  employed_at.role," +
                                                                   "  employed_at.from_date," +
                                                                   "  employed_at.to_date" +
                                                                   " FROM employed_at" +
                                                                   "  JOIN people ON people.id = employed_at.people_id" +
                                                                   "  JOIN startups ON startups.name = employed_at.name" +
                                                                   "  JOIN located_at ON startups.name = located_at.startup" +
                                                                   "  JOIN addresses ON located_at.street_address = addresses.street_address" +
                                                                   "  JOIN funding_rounds ON startups.name = funding_rounds.startup" +
                                                                   " WHERE people.first_name= ? AND" +
                                                                   "        people.last_name= ? " +
                                                                   " GROUP BY startups.name," +
                                                                   "  startups.vertical," +
                                                                   "  startups.since," +
                                                                   "  addresses.street_address," +
                                                                   "  addresses.latitude," +
                                                                   "  addresses.longitude," +
                                                                   "  employed_at.role," +
                                                                   "  employed_at.from_date," +
                                                                   "  employed_at.to_date;";


    private static final String SEARCH_FOR_STARTUP_BY_INVESTOR = "SELECT startups.name," +
                                                                 "  startups.vertical," +
                                                                 "  startups.since," +
                                                                 "  addresses.street_address," +
                                                                 "  addresses.latitude," +
                                                                 "  addresses.longitude," +
                                                                 "  count(funding_rounds.amount) AS rounds," +
                                                                 "  sum(funding_rounds.amount) AS total_funding," +
                                                                 "  investors.name" +
                                                                 " FROM investors" +
                                                                 "  JOIN funding_rounds ON funding_rounds.investor = investors.name" +
                                                                 "  JOIN startups ON funding_rounds.startup = startups.name" +
                                                                 "  JOIN located_at ON startups.name = located_at.startup" +
                                                                 "  JOIN addresses ON located_at.street_address = addresses.street_address" +
                                                                 " WHERE investors.name = ? " +
                                                                 " GROUP BY startups.name," +
                                                                 "  startups.vertical," +
                                                                 "  startups.since," +
                                                                 "  addresses.street_address," +
                                                                 "  addresses.latitude," +
                                                                 "  addresses.longitude," +
                                                                 "  investors.name;";


    /**
     * Used to configure connection to database.
     */
    private DatabaseConnector db;

    /**
     * Initializes a database handler for the Login example. Private constructor
     * forces all other classes to use singleton.
     */
    private DatabaseHandler() {
        Status status = Status.OK;

        try {
            // called
            db = new DatabaseConnector("database.properties");
            status = db.testConnection() ? Status.OK : Status.CONNECTION_FAILED;
        } catch (FileNotFoundException e) {
            status = Status.MISSING_CONFIG;
        } catch (IOException e) {
            status = Status.MISSING_VALUES;
        }

        if (status != Status.OK) {
            // log.fatal(status.message());
        }
    }

    /**
     * Gets the single instance of the database handler.
     *
     * @return instance of the database handler
     */
    public static DatabaseHandler getInstance() {
        return singleton;
    }

    /**
     * Checks to see if a String is null or empty.
     *
     * @param text - String to check
     * @return true if non-null and non-empty
     */
    public static boolean isBlank(String text) {
        return (text == null) || text.trim().isEmpty();
    }


    /**
     * Returns the list of searches conducted by the user.
     *
     * @param connection - active database connection
     * @param zipcode    - username whose searches we are listing
     * @return Status.OK if the query returns searches
     * @throws SQLException
     */
    private ArrayList<Startup> searchByZipcode(Connection connection, String zipcode) throws SQLException {
        assert connection != null;

        ArrayList<Startup> startups = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(SEARCH_STARTUPS_BY_ZIPCODE_SQL)) {
            statement.setString(1, zipcode);

            ResultSet results = statement.executeQuery();

            while (results.next()) {
                String startup = results.getString("name");
                String vertical = results.getString("vertical");
                String since = results.getString("since");
                String street_address = results.getString("street_address");
                double latitude = results.getDouble("latitude");
                double longitude = results.getDouble("longitude");
                int rounds = results.getInt("rounds");
                int total_funding = results.getInt("total_funding");

                startups.add(new Startup(startup, vertical, since, rounds, total_funding,
                                         new Address(street_address, zipcode, latitude, longitude)));
            }
        }

        return startups;
    }

    /**
     * Returns the list of searches conducted by the user.
     *
     * @param zipcode - username whose searches we are listing
     * @return Status.OK if user does not exist in database
     * @see #searchByZipcode(Connection, String)
     */
    public ArrayList<Startup> searchByZipcode(String zipcode) {
        ArrayList<Startup> startups = null;

        try (Connection connection = db.getConnection();) {
            startups = searchByZipcode(connection, zipcode);
        } catch (SQLException e) {
            // log.debug(e.getMessage(), e);
        }

        return startups;
    }


    /**
     * Returns the list of searches conducted by the user.
     *
     * @param connection - active database connection
     * @param name       - username whose searches we are listing
     * @return Status.OK if the query returns searches
     * @throws SQLException
     */
    private ArrayList<Startup> searchByStartupName(Connection connection, String name) throws SQLException {
        assert connection != null;

        ArrayList<Startup> startups = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(SEARCH_FOR_STARTUP_BY_NAME)) {
            statement.setString(1, name);

            ResultSet results = statement.executeQuery();

            while (results.next()) {

                String vertical = results.getString("vertical");
                String since = results.getString("since");
                String street_address = results.getString("street_address");
                double latitude = results.getDouble("latitude");
                double longitude = results.getDouble("longitude");
                int rounds = results.getInt("rounds");
                int total_funding = results.getInt("total_funding");

                startups.add(new Startup(name, vertical, since, rounds, total_funding,
                                         new Address(street_address, latitude, longitude)));
            }
        }
        return startups;
    }

    /**
     * Returns the list of searches conducted by the user.
     *
     * @param name - username whose searches we are listing
     * @return Status.OK if user does not exist in database
     * @see #searchByZipcode(Connection, String)
     */
    public ArrayList<Startup> searchByStartupName(String name) {
        ArrayList<Startup> startups = null;

        try (Connection connection = db.getConnection();) {
            startups = searchByStartupName(connection, name);
        } catch (SQLException e) {
            // log.debug(e.getMessage(), e);
        }

        return startups;
    }


    /**
     * Returns the list of searches conducted by the user.
     *
     * @param connection - active database connection
     * @param firstName  - username whose searches we are listing
     * @return Status.OK if the query returns searches
     * @throws SQLException
     */
    private Person searchByIndividual(Connection connection, String firstName, String lastName) throws SQLException {
        assert connection != null;

        Person person = new Person(firstName, lastName);

        try (PreparedStatement statement = connection.prepareStatement(SEARCH_FOR_STARTUP_BY_INDIVIDUAL)) {
            statement.setString(1, firstName);
            statement.setString(2, lastName);

            ResultSet results = statement.executeQuery();

            if (results != null) {

                while (results.next()) {

                    String name = results.getString("name");
                    String title = results.getString("role");
                    String fromDate = results.getString("from_date");
                    String toDate = results.getString("to_date");
                    String vertical = results.getString("vertical");
                    String since = results.getString("since");
                    String streetAddress = results.getString("street_address");
                    double latitude = results.getDouble("latitude");
                    double longitude = results.getDouble("longitude");
                    int rounds = results.getInt("rounds");
                    int total_funding = results.getInt("total_funding");

                    Startup startup = new Startup(name, vertical, since, rounds, total_funding,
                                                  new Address(streetAddress, latitude, longitude));

                    person.addRole(startup, title, fromDate, toDate);

                }
            }
        }
        return person;
    }

    /**
     * Returns the list of searches conducted by the user.
     *
     * @param firstName - username whose searches we are listing
     * @return Status.OK if user does not exist in database
     * @see #searchByZipcode(Connection, String)
     */
    public Person searchByIndividual(String firstName, String lastName) {
        Person person = null;

        try (Connection connection = db.getConnection();) {
            person = searchByIndividual(connection, firstName, lastName);
        } catch (SQLException e) {
            // log.debug(e.getMessage(), e);
        }

        return person;
    }


    /**
     * Returns the list of searches conducted by the user.
     *
     * @return Status.OK if the query returns searches
     * @throws SQLException
     */
    private Investor searchByInvestor(Connection connection, String name) throws SQLException {
        assert connection != null;

        Investor investor = new Investor(name);

        try (PreparedStatement statement = connection.prepareStatement(SEARCH_FOR_STARTUP_BY_INVESTOR)) {
            statement.setString(1, name);

            ResultSet results = statement.executeQuery();

            if (results != null) {

                while (results.next()) {

                    String startupName = results.getString("name");
                    String vertical = results.getString("vertical");
                    String since = results.getString("since");
                    String streetAddress = results.getString("street_address");
                    double latitude = results.getDouble("latitude");
                    double longitude = results.getDouble("longitude");
                    int rounds = results.getInt("rounds");
                    int total_funding = results.getInt("total_funding");

                    Startup startup = new Startup(startupName, vertical, since, rounds, total_funding,
                                                  new Address(streetAddress, latitude, longitude));

                    investor.addInvestment(startup);

                }
            }
        }
        return investor;
    }

    public Investor searchByInvestor(String name) {
        Investor investor = null;

        try (Connection connection = db.getConnection();) {
            investor = searchByInvestor(connection, name);
        } catch (SQLException e) {
            // log.debug(e.getMessage(), e);
        }

        return investor;
    }

}
