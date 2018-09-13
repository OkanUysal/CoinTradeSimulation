package cointrade.uysal.development.cointradesimulation;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by okan on 11.02.2018.
 */

public class AsyncRead extends AsyncTask<Void, Void, String> {

    private ProgressDialog dialog;
    /** application context. */

    public AsyncRead(AppCompatActivity activity) {
        dialog = new ProgressDialog(activity);
    }

    @Override
    protected void onPreExecute()
    {
        this.dialog.setMessage(MainActivity.load);
        this.dialog.show();
    }
    @Override
    protected String doInBackground(Void... arg0)
    {
        int count = 0;
        if(android.os.Debug.isDebuggerConnected())
            android.os.Debug.waitForDebugger();
        try{
            URL url = new URL("https://coinmarketcap.com/");
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String line = null;

            int lastCoin = -1;

            while ((line = reader.readLine()) != null) {

                if(line.contains("currency-symbol")) {
                    String[] names = line.split("/");
                    String[] names2 = line.split("[<>]");
                    for(int i = 0; i < MarketAdapter.coinInfos2.size(); i++){
                        if(names[2].equals(MarketAdapter.coinInfos2.get(i).name)){
                            MarketAdapter.coinInfos2.get(i).name2 = "(" + names2[4] + ")";
                        }
                    }
                }
                else if(line.contains("currency-name-container")) {
                    String[] names = line.split("/");
                    String[] names2 = line.split("[<>]");
                    for(int i = 0; i < MarketAdapter.coinInfos2.size(); i++){
                        if(names[2].equals(MarketAdapter.coinInfos2.get(i).name)){
                            MarketAdapter.coinInfos2.get(i).name2 += names2[2];
                        }
                    }
                }

                if(line.contains("currencies") && line.contains("markets") && line.contains("price")){

                    String[] names = line.split("/");
                    String[] prize = line.split("\"");
                    for(int i = 0; i < MarketAdapter.coinInfos2.size(); i++){
                        if(names[2].equals(MarketAdapter.coinInfos2.get(i).name)){
                            MarketAdapter.coinInfos2.get(i).value = Double.valueOf(prize[5]);
                            lastCoin = i;
                        }
                    }
                }
                else if(line.contains("24h") && line.contains("no-wrap") && line.contains("change")) {
                    String[] names = line.split("[<>]");
                    MarketAdapter.coinInfos2.get(lastCoin).change = names[2];
                }
                count++;
                if(!MainActivity.internet)
                    MainActivity.internet = true;
            }
        } catch (Exception e){
            System.out.println(e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result)
    {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        MainActivity.adapter.getFilter().filter(MainActivity.search.getText());
        MainActivity.appendbalance();

    }
}
