package Classes;

public class passenger extends User {
    public int trajetsAmount;
    public passenger(String lastname, String firstname, String phonenumber, String email, String password, int since, int trajets) {
        super(lastname, firstname, phonenumber, email, password,since);
        this.trajetsAmount = trajets;
    }

    public int getTrajetsAmount() {
        return trajetsAmount;
    }

    public void setTrajetsAmount(int trajetsAmount) {
        this.trajetsAmount = trajetsAmount;
    }
}