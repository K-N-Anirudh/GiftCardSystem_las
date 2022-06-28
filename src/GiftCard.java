import java.util.ArrayList;

class GiftCard {


    private int  cardNumber,pin;
    private double giftCardAmount;
    private int rewardPoints=0;
    private boolean isAlive;//To know if card is blocked
    private ArrayList<Transaction> t=new ArrayList<>();//To store transactions
    GiftCard(int cardNumber,int pin,double amount) // To give details of account for the gift card
    {
        this.cardNumber=cardNumber;
        this.pin=pin;
        this.giftCardAmount=amount;
        this.isAlive=true;
        Transaction initial=new Transaction(amount,TransactionType.Initial);
        t.add(initial);



}

    void addAmount(double amount)//->Change it based on condition topup
    {
        this.giftCardAmount+=amount;
        Transaction topup=new Transaction(amount,TransactionType.Credited);
        t.add(topup);
    }

    void  setIsAlive()
    {this.isAlive=!this.isAlive;}


    int getCardNumber() // To check no during purchase
    {
        return this.cardNumber;
    }

    double getAmount()
    {
        return this.giftCardAmount;
    }


}