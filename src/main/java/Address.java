public class Address {

    private String streetAddress;
    private String city;
    private String state;
    private String zipcode;
    private double latitude;
    private double longitude;

    /**
     * Constructor for the Address data object
     * @param streetAddress
     * @param zipcode
     * @param latitude
     * @param longitude
     */
    Address(String streetAddress, String zipcode, double latitude, double longitude) {
        this.streetAddress = streetAddress;
        this.city = "San Francisco";
        this.state = "CA";
        this.zipcode = zipcode;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Constructor for the Address data object
     * @param streetAddress
     * @param latitude
     * @param longitude
     */
    Address(String streetAddress, double latitude, double longitude) {
        this.streetAddress = streetAddress;
        this.city = "San Francisco";
        this.state = "CA";
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }


    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }
}
