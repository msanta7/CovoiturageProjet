package Classes;

public class User {
    public Double Num;
    public String Firstname;
    public String Lastname;
    public String Phonenumber;
    public String Email;
    public String Password;
    public int MemberSince;

    public User(String firstname, String lastname, String phonenumber, String email, String password, int memberSince) {
        Firstname = firstname;
        Lastname = lastname;
        Phonenumber = phonenumber;
        Email = email;
        Password = password;
        MemberSince = memberSince;
    }

    public String getFirstname() {
        return Firstname;
    }

    public void setFirstname(String firstname) {
        Firstname = firstname;
    }

    public String getLastname() {
        return Lastname;
    }

    public void setLastname(String lastname) {
        Lastname = lastname;
    }

    public String getPhonenumber() {
        return Phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        Phonenumber = phonenumber;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public int getMemberSince() {
        return MemberSince;
    }

    public void setMemberSince(int memberSince) {
        MemberSince = memberSince;
    }
}