package cointrade.uysal.development.cointradesimulation;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
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
 * Created by okan on 11.02.2018.
 */

public class MarketAdapter extends BaseAdapter implements Filterable {
    public static ArrayList<CoinInfo> coinInfos;
    public static ArrayList<CoinInfo> coinInfos2;
    public LayoutInflater inflater;
    public Activity activity;

    public static ArrayList<CoinInfo> FilteredArrayNames;


    public MarketAdapter(Activity activity, ArrayList<CoinInfo> coinInfos) {
        this.coinInfos = coinInfos;
        this.coinInfos2 = new ArrayList<CoinInfo>(coinInfos);
        this.FilteredArrayNames = new ArrayList<CoinInfo>(coinInfos);
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
        return FilteredArrayNames.get(position).name;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = null;

        try{
            rowView = inflater.inflate(R.layout.coininfo, null);

            CoinInfo coinInfo = coinInfos.get(position);

            ImageView imageView = (ImageView) rowView.findViewById(R.id.imageView);
            imageView.setImageResource(coinInfo.drawable);

            TextView name = (TextView) rowView.findViewById(R.id.name);
            name.setText(coinInfo.name2.replace("-", " "));

            TextView value = (TextView) rowView.findViewById(R.id.value);
            NumberFormat formatter = new DecimalFormat("#0.00");
            value.setText("Price:" + formatter.format(coinInfo.value) + "$");

            TextView change = (TextView) rowView.findViewById(R.id.change);
            change.setText("Change(24h):" + coinInfo.change);
            if(coinInfo.change.startsWith("-")) {
                change.setTextColor(Color.rgb(178,34,34));
            } else  {
                change.setTextColor(Color.rgb(34,139,34));
            }
        }catch (Exception e) {
            System.out.println();
        }




        return  rowView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                coinInfos = (ArrayList<CoinInfo>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();
                FilteredArrayNames = new ArrayList<CoinInfo>();

                // perform your search here using the searchConstraint String.

                constraint = constraint.toString().toLowerCase();
                for (int i = 0; i < coinInfos2.size(); i++) {
                    String dataNames = coinInfos2.get(i).name;
                    String dataNames2 = coinInfos2.get(i).name2;
                    if (dataNames.toLowerCase().contains(constraint.toString()) || dataNames2.toLowerCase().contains(constraint.toString()))  {
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
