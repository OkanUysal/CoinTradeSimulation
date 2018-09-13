package cointrade.uysal.development.cointradesimulation;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by okan on 16.02.2018.
 */

public class TransactionDialog extends Dialog{

    Activity c;
    int position;

    TextView name;
    TextView value;
    EditText piece;
    Button buy;
    Button cancel;
    Button sell;
    CoinInfo coinInfo;

    public TransactionDialog(Activity a, String name) {
        super(a);
        this.c = a;
        for(int i = 0; i < MainActivity.coinInfos.size(); i++){
            if(MainActivity.coinInfos.get(i).name.equals(name)){
                position = i;
                break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.transaction_dialog);

        name = (TextView) findViewById(R.id.textViewName);
        value = (TextView) findViewById(R.id.textViewValue);
        piece = (EditText) findViewById(R.id.editTextPiece);
        buy = (Button) findViewById(R.id.buttonBuy);
        cancel = (Button) findViewById(R.id.buttonCancel);
        sell = (Button) findViewById(R.id.buttonSell);
        coinInfo = MainActivity.coinInfos.get(position);


        final NumberFormat formatter = new DecimalFormat("#0.00");
        NumberFormat formatter2 = new DecimalFormat("#0.0000");
        value.setText("Price:" + formatter.format(MainActivity.coinInfos.get(position).value) + "$");



        name.setText(MainActivity.coinInfos.get(position).name + "(" + formatter2.format(MainActivity.coinInfos.get(position).amount) + ")");

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!piece.getText().toString().equals("") && !piece.getText().toString().equals(".")) {
                    if (coinInfo.value * Double.valueOf(piece.getText().toString()) <= MainActivity.balanceDolar) {
                        String date = android.text.format.DateFormat.format("yyyy/MM/dd", new java.util.Date()).toString();
                        MainActivity.editor.putString("transaction" + MainActivity.transactionCount, "true:" + coinInfo.name + ":" + piece.getText().toString() +
                                ":" + date + ":" + coinInfo.value);
                        MainActivity.balanceDolar -= coinInfo.value * Double.valueOf(piece.getText().toString());
                        MainActivity.editor.putString("balance", String.valueOf(MainActivity.balanceDolar));
                        MainActivity.editor.putInt("transactionCount", ++MainActivity.transactionCount);
                        MainActivity.editor.commit();
                        MainActivity.balance.setText(MainActivity.balanceName + ":" + formatter.format(MainActivity.balanceDolar) + "$");

                        MainActivity.transactions.add(new Transaction(true, coinInfo.name, Double.valueOf(piece.getText().toString()), date, coinInfo.value));
                        MainActivity.coinInfos.get(position).amount += Double.valueOf(piece.getText().toString());
                        TransactionAdapter.setList(MainActivity.transactions);
                        MainActivity.appendbalance();
                        dismiss();
                    } else {
                        Toast.makeText(c.getApplicationContext(), MainActivity.nomoney, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!piece.getText().toString().equals("") && !piece.getText().toString().equals(".")) {
                    if (Double.valueOf(piece.getText().toString()) <= MainActivity.coinInfos.get(position).amount) {
                        String date = android.text.format.DateFormat.format("yyyy/MM/dd", new java.util.Date()).toString();
                        MainActivity.editor.putString("transaction" + MainActivity.transactionCount, "false:" + coinInfo.name + ":" + piece.getText().toString() +
                                ":" + date + ":" + coinInfo.value);
                        MainActivity.balanceDolar += coinInfo.value * Double.valueOf(piece.getText().toString());
                        MainActivity.editor.putString("balance", String.valueOf(MainActivity.balanceDolar));
                        MainActivity.editor.putInt("transactionCount", ++MainActivity.transactionCount);
                        MainActivity.editor.commit();
                        MainActivity.balance.setText(MainActivity.balanceName + ":" + formatter.format(MainActivity.balanceDolar) + "$");

                        MainActivity.transactions.add(new Transaction(false, coinInfo.name, Double.valueOf(piece.getText().toString()), date, coinInfo.value));
                        MainActivity.coinInfos.get(position).amount -= Double.valueOf(piece.getText().toString());
                        MainActivity.adapter2.getFilter().filter(MainActivity.search.getText());
                        TransactionAdapter.setList(MainActivity.transactions);
                        MainActivity.appendbalance();
                        dismiss();
                    } else {
                        Toast.makeText(c.getApplicationContext(), MainActivity.nocoin, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

}
