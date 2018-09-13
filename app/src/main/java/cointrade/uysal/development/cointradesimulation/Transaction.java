package cointrade.uysal.development.cointradesimulation;

/**
 * Created by okan on 15.02.2018.
 */

public class Transaction {
    public boolean buy;
    public double value;
    public String coin;
    public String date;
    public double amount;

    public Transaction(boolean buy, String coin, double amount, String date, double value) {
        this.buy = buy;
        this.value = value;
        this.coin = coin;
        this.date = date;
        this.amount = amount;
    }
}
