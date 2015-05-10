
public class Startup extends Organization {

    private String vertical;
    private String since;
    private String status;
    private int fundingRounds;
    private int totalFunding;

    public Startup(String name, Address address) {
        super(name, address);
    }

    /**
     * Constructor for the Startup data object
     * @param name
     * @param vertical
     * @param since
     * @param fundingRounds
     * @param totalFunding
     * @param address
     */
    public Startup(String name, String vertical, String since, int fundingRounds, int totalFunding, Address address) {
        this(name, address);
        this.vertical = vertical;
        this.since = since;
        this.status = "Active";
        this.fundingRounds = fundingRounds;
        this.totalFunding = totalFunding;
    }

    public String getVertical() {
        return vertical;
    }

    public void setVertical(String vertical) {
        this.vertical = vertical;
    }

    public String getSince() {
        return since;
    }

    public void setSince(String since) {
        this.since = since;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getFundingRounds() {
        return fundingRounds;
    }

    public void setFundingRounds(int fundingRounds) {
        this.fundingRounds = fundingRounds;
    }

    public int getTotalFunding() {
        return totalFunding;
    }

    public void setTotalFunding(int totalFunding) {
        this.totalFunding = totalFunding;
    }
}