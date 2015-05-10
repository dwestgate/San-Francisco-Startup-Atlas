import java.util.ArrayList;

/**
 * Created by davidwestgate on 5/2/15.
 */
public class Person {

    private String firstName;
    private String lastName;
    private String almaMata;
    private ArrayList<Role> roles;

    /**
     * Constructor for Person data object
     * @param firstName
     * @param lastName
     */
    public Person(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.roles = new ArrayList<>();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAlmaMata() {
        return almaMata;
    }

    public void setAlmaMata(String almaMata) {
        this.almaMata = almaMata;
    }

    public ArrayList<Role> getRoles() {
        return roles;
    }

    public void setRoles(ArrayList<Role> roles) {
        this.roles = roles;
    }

    public void addRole(Startup startup, String title, String fromDate, String toDate) {
        this.roles.add(new Role(startup, title, fromDate, toDate));
    }

    public class Role {

        private Startup startup;
        private String title;
        private String fromDate;
        private String toDate;

        /**
         * Constructor for Role data object
         * @param startup
         * @param role
         * @param fromDate
         * @param toDate
         */
        public Role(Startup startup, String role, String fromDate, String toDate) {
            this.startup = startup;
            this.title = role;
            this.fromDate = fromDate;
            this.toDate = toDate;
        }

        public Startup getOrganization() {
            return startup;
        }

        public void setOrganization(Startup startup) {
            this.startup = startup;
        }

        public String getRole() {
            return title;
        }

        public void setRole(String role) {
            this.title = role;
        }

        public String getFromDate() {
            return fromDate;
        }

        public void setFromDate(String fromDate) {
            this.fromDate = fromDate;
        }

        public String getToDate() {
            return toDate;
        }

        public void setToDate(String toDate) {
            this.toDate = toDate;
        }
    }

}
