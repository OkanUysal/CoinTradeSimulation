package cointrade.uysal.development.cointradesimulation;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Created by okan on 17.02.2018.
 */

public class TransactionAdapter extends BaseAdapter implements Filterable {

    public static ArrayList<Transaction> coinInfos;
    public static ArrayList<Transaction> coinInfos2;
    public LayoutInflater inflater;
    public Activity activity;

    public static ArrayList<Transaction> FilteredArrayNames;

    public TransactionAdapter(Activity activity, ArrayList<Transaction> coinInfos) {
        this.coinInfos = coinInfos;
        this.coinInfos2 = new ArrayList<Transaction>(coinInfos);
        this.FilteredArrayNames = new ArrayList<Transaction>(coinInfos);
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.activity = activity;
    }


    @Override
    public int getCount() {
        return coinInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return coinInfos.get(position);
    }

    public String getName(int position) {

        return FilteredArrayNames.get(position).coin;
    }

    public static void setList(ArrayList<Transaction> newTransactions) {
        coinInfos = newTransactions;
        coinInfos2 = new ArrayList<Transaction>(newTransactions);
        FilteredArrayNames = new ArrayList<Transaction>(newTransactions);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = null;

        try {
            rowView = inflater.inflate(R.layout.coininfo, null);

            Transaction coinInfo = coinInfos.get(position);

            ImageView imageView = (ImageView) rowView.findViewById(R.id.imageView);
            int findCoin = 0;
            for(; findCoin < MainActivity.coinInfos.size(); findCoin++) {
                if(coinInfo.coin.equals(MainActivity.coinInfos.get(findCoin).name))
                    break;
            }

            imageView.setImageResource(MainActivity.coinInfos.get(findCoin).drawable);

            if(coinInfo.buy)
                rowView.setBackgroundResource(R.drawable.round_button4);
            else
                rowView.setBackgroundResource(R.drawable.round_button3);

            TextView name = (TextView) rowView.findViewById(R.id.name);
            name.setText(MainActivity.coinInfos.get(findCoin).name2.replace("-", " "));

            TextView value = (TextView) rowView.findViewById(R.id.value);
            NumberFormat formatter = new DecimalFormat("#0.00");
            NumberFormat formatter2 = new DecimalFormat("#0.0000");
            value.setText(formatter.format(coinInfo.value) + "$(" + formatter2.format(coinInfo.amount) + ")");

            TextView change = (TextView) rowView.findViewById(R.id.change);
            change.setText(coinInfo.date);
        } catch (Exception e) {
            System.out.println();
        }

        return rowView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                coinInfos = (ArrayList<Transaction>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();
                FilteredArrayNames = new ArrayList<Transaction>();

                // perform your search here using the searchConstraint String.

                constraint = constraint.toString().toLowerCase();
                for (int i = 0; i < coinInfos2.size(); i++) {
                    String dataNames = coinInfos2.get(i).coin;
                    int findCoin = 0;
                    for(; findCoin < MainActivity.coinInfos.size(); findCoin++) {
                        if(coinInfos2.get(i).coin.equals(MainActivity.coinInfos.get(findCoin).name))
                            break;
                    }
                    String dataNames2 = MainActivity.coinInfos.get(findCoin).name2;
                    if ( (dataNames.toLowerCase().contains(constraint.toString()) || dataNames2.toLowerCase().contains(constraint.toString()))
                            && coinInfos2.get(i).amount > 0)   {
                        FilteredArrayNames.add(coinInfos2.get(i));
                    }
                }

                results.count = FilteredArrayNames.size();
                results.values = FilteredArrayNames;

                return results;
            }
        };

        return filter;
    }
}
