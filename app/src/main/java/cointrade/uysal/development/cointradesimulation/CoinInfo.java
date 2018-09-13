package cointrade.uysal.development.cointradesimulation;

/**
 * Created by okan on 11.02.2018.
 */

public class CoinInfo {
    public String name;
    public String name2;
    public int drawable;
    public double value;
    public String change;
    public double amount;

    public CoinInfo(String name, int drawable, double value, String change) {
        this.name = name;
        this.drawable = drawable;
        this.value = value;
        this.change = change;
        this.name2 = "";
        this.amount = 0;
    }

}
