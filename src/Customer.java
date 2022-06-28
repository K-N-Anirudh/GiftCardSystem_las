import com.sun.org.apache.bcel.internal.generic.RETURN;

import java.util.ArrayList;
class Customer {
    private static int no = 0;// To give value of  customer in order in which typed
    private int custId=0;// its no is based on the nth account created. that is first person will have custid 1 and account no 101
    private long accountNo;
    private String name;//Alphabets and . are allowed
    private double balance;
    private String encryptedPassword;
    private ArrayList<GiftCard> g = new ArrayList<>();

    Customer(String Name, double balance, String encryptedPassword) {


            this.name = Name;
            this.balance = balance;
            setEncryptedPassword(encryptedPassword);
            this.custId = no;
            String samp = Integer.toString(this.custId);
            samp = samp + "0";
            samp += samp;
            this.accountNo = Long.parseLong(samp);//Generates id and account based on the logic first come first server



    }
    Customer(int custId,long accountNo,String Name,String encryptedPassword, double balance) {

        this.name = Name;
        this.balance = balance;
        this.encryptedPassword=encryptedPassword;
        this.custId=custId;
        this.accountNo=accountNo;
    }
    static void setNo(int n){
        no=n;
    }


    long getAccountNo() {
        return this.accountNo;
    }

    double getBalance() {
        return this.balance;
    }
    int getCustId(){
        return this.custId;
    }
    String getName(){
        return this.name;
    }

    String getEncryptedPassword()
    {
        return this.encryptedPassword;
    }

    void setEncryptedPassword(String Password)//To encrypt password
    {

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < Password.length(); i++) {
            char ch;
            if (Character.isUpperCase(Password.charAt(i))) {
                ch = (char) (((int) Password.charAt(i) +
                        1 - 65) % 26 + 65);
                result.append(ch);
            } else if (Character.isLowerCase((Password.charAt(i)))) {
                ch = (char) (((int) Password.charAt(i) +
                        1 - 97) % 26 + 97);
                result.append(ch);
            } else {
                ch = (char) (((int) Password.charAt(i) +
                        1 - 48) % 10 + 48);
                result.append(ch);

            }

        }
        this.encryptedPassword = new String(result);

    }

    String getPassword() {


        StringBuilder result = new StringBuilder();

        for (int i = 0; i < this.encryptedPassword.length(); i++) {
            char ch;
            if (Character.isUpperCase(this.encryptedPassword.charAt(i))) {
                ch = (char) (((int) this.encryptedPassword.charAt(i) +
                        25 - 65) % 26 + 65);
                result.append(ch);
            } else if (Character.isLowerCase((this.encryptedPassword.charAt(i)))) {
                ch = (char) (((int) this.encryptedPassword.charAt(i) +
                        25 - 97) % 26 + 97);
                result.append(ch);
            } else {
                ch = (char) (((int) this.encryptedPassword.charAt(i) +
                        9 - 48) % 10 + 48);
                result.append(ch);

            }

        }

        String Password = new String(result);
        return Password;

    }

    ArrayList<GiftCard> getCard() {
        return this.g;
    }

    void addBalance(double amount)// When blocking a gift card we add back the amount in the card to balance
    {
        this.balance += amount;
    }

    void removeBalance(double amount)// Removing amount from balance and adding to gifCard
    {
        this.balance -= amount;
    }



}
