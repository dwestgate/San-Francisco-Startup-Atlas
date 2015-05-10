import java.util.ArrayList;

public class Investor extends Organization {

    private ArrayList<Startup> investments;

    /**
     * Constructor for Investor data object
     * @param name
     */
    public Investor(String name) {
        super(name);
        investments = new ArrayList<>();
    }

    public ArrayList<Startup> getInvestments() {
        return investments;
    }

    public void addInvestment(Startup startup) {
        this.investments.add(startup);
    }
}
