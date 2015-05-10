import java.util.ArrayList;

/**
 * Created by davidwestgate on 5/1/15.
 */
public class Organization {

    private String name;
    private ArrayList<Address> addresses;

    /**
     * Constructor for Organization data object
     * @param name
     * @param address
     */
    public Organization(String name, Address address) {
        this.name = name;
        this.addresses = new ArrayList<Address>();
        this.addresses.add(address);
    }

    /**
     * Constructor for Organization data object
     * @param name
     */
    public Organization(String name) {
        this.name = name;
        this.addresses = new ArrayList<Address>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(ArrayList<Address> addresses) {
        this.addresses = addresses;
    }

    public void addAddress(Address address) {
        this.addresses.add(address);
    }


}
