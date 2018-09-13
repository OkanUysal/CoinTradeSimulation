package cointrade.uysal.development.cointradesimulation;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RewardedVideoAdListener {

    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    private RewardedVideoAd mRewardedVideoAd;
    public static int admobCount = 5;

    public static Button myWallet;
    public static Button market;
    public static Button summary;
    public static Button refresh;
    public static String load;
    public static String coinbalance;

    public static TextView balance;

    public static EditText search;

    public static ListView listView;

    public static ArrayList<CoinInfo> coinInfos;

    public static MarketAdapter adapter;
    public static MyCoinAdapter adapter2;
    public static TransactionAdapter adapter3;

    public static SharedPreferences sharedPref;
    public static SharedPreferences.Editor editor;

    public static int transactionCount = 0;
    public static double balanceDolar = 0;
    public static String balanceName = "";

    public static String nomoney ="";
    public static String nocoin ="";

    public static int page = 1;

    public static boolean internet = false;

    public static ArrayList<Transaction> transactions;

    public static AppCompatActivity activity;

    public int[] ids= {
            R.drawable.aeternity, R.drawable.ardor, R.drawable.ark, R.drawable.augur, R.drawable.binancecoin, R.drawable.bitcoincash,
            R.drawable.bitcoingold, R.drawable.bitcoin, R.drawable.bitshares, R.drawable.bytecoin,
            R.drawable.cardano, R.drawable.dash, R.drawable.decred, R.drawable.digixdao, R.drawable.dogecoin,
            R.drawable.eos, R.drawable.ethereumclassic, R.drawable.ethereum, R.drawable.hshare, R.drawable.icon,
            R.drawable.iota, R.drawable.komodo, R.drawable.kucoinshares, R.drawable.kybernetwork, R.drawable.lisk,
            R.drawable.litecoin, R.drawable.maker, R.drawable.monero, R.drawable.nem, R.drawable.neo,
            R.drawable.omisego, R.drawable.populous, R.drawable.qtum, R.drawable.rchain, R.drawable.revain,
            R.drawable.ripple, R.drawable.siacoin, R.drawable.status, R.drawable.steem, R.drawable.stellar,
            R.drawable.stratis, R.drawable.tether, R.drawable.tron, R.drawable.vechain, R.drawable.verge,
            R.drawable.veritaseum, R.drawable.walton, R.drawable.waves, R.drawable.zcash
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, "ca-app-pub-6328620287804966~5518026934");

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-6328620287804966/4894731472");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();

        if(!isNetworkConnected()) {
            Toast.makeText(getApplicationContext(), getString(R.string.internet), Toast.LENGTH_LONG).show();
        }

        load = getString(R.string.load);
        activity = this;

        sharedPref = getSharedPreferences("My Preferences", MODE_PRIVATE);
        editor = sharedPref.edit();

        transactionCount = sharedPref.getInt("transactionCount", 0);
        balanceDolar = Double.valueOf(sharedPref.getString("balance", "100000"));
        transactions = new ArrayList<Transaction>();


        Resources res = getResources();
        String[] names = res.getStringArray(R.array.coinString);

        coinInfos = new ArrayList<CoinInfo>();
        for( int i = 0; i < names.length; i++) {
            coinInfos.add(new CoinInfo(names[i], ids[i], i, String.valueOf(i) + "%"));
        }

        for(int i = 0; i < transactionCount; i++) {
            String line = sharedPref.getString("transaction" + i, "");
            String[] splits = line.split(":");
            boolean buy = Boolean.valueOf(splits[0]);
            double amount = Double.valueOf(splits[2]);
            double value = Double.valueOf(splits[4]);

            transactions.add(new Transaction(buy,splits[1], amount, splits[3], value));
            for( int j = 0; j < names.length; j++) {
                if(splits[1].equals(names[j])){
                    if(buy) {
                        coinInfos.get(j).amount += amount;
                    } else {
                        coinInfos.get(j).amount -= amount;
                    }
                    break;
                }
            }

        }


        myWallet = (Button) findViewById(R.id.mywallet);
        market = (Button) findViewById(R.id.market);
        summary = (Button) findViewById(R.id.summary);
        refresh = (Button) findViewById(R.id.refresh);

        market.setBackgroundResource(R.drawable.round_button2);


        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncRead asyncRead = new AsyncRead(activity);
                asyncRead.execute();
            }
        });

        balanceName = getString(R.string.balance);
        nomoney = getString(R.string.nomoney);
        nocoin = getString(R.string.nocoin);
        coinbalance = getString(R.string.coinbalance);
        balance = (TextView) findViewById(R.id.balance);
        final NumberFormat formatter = new DecimalFormat("#0.00");
        balance.setText(getString(R.string.balance) + ":" + formatter.format(balanceDolar) + "$");
        appendbalance();

        search = (EditText) findViewById(R.id.search);

        listView = (ListView) findViewById(R.id.listview);



        AsyncRead asyncRead = new AsyncRead(this);
        asyncRead.execute();

        adapter=new MarketAdapter(this, coinInfos);
        listView.setAdapter(adapter);

        adapter2 = new MyCoinAdapter(this, coinInfos);
        adapter3 = new TransactionAdapter(this, transactions);

        adapter.getFilter().filter(search.getText().toString());

        search.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                adapter.getFilter().filter(cs);
                adapter2.getFilter().filter(cs);
                adapter3.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }

            @Override
            public void afterTextChanged(Editable arg0) {}
        });

        myWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter2.getFilter().filter(search.getText());
                listView.setAdapter(adapter2);
                myWallet.setBackgroundResource(R.drawable.round_button2);
                market.setBackgroundResource(R.drawable.round_button);
                summary.setBackgroundResource(R.drawable.round_button);
                page = 0;
                loadAdMob();
            }
        });

        market.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.getFilter().filter(search.getText());
                listView.setAdapter(adapter);
                myWallet.setBackgroundResource(R.drawable.round_button);
                market.setBackgroundResource(R.drawable.round_button2);
                summary.setBackgroundResource(R.drawable.round_button);
                page = 1;
                loadAdMob();
            }
        });

        summary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView.setAdapter(adapter3);
                adapter3.getFilter().filter(search.getText());
                myWallet.setBackgroundResource(R.drawable.round_button);
                market.setBackgroundResource(R.drawable.round_button);
                summary.setBackgroundResource(R.drawable.round_button2);
                page = 2;
                loadAdMob();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                loadAdMob();
                if(!MainActivity.internet) {
                    Toast.makeText(getApplicationContext(), getString(R.string.internet), Toast.LENGTH_LONG).show();
                } else {
                    String name = "";
                    if (page == 0) {
                        name = adapter2.getName(position);
                    } else if (page == 1) {
                        name = adapter.getName(position);
                    }
                    if (page != 2) {
                        TransactionDialog cdd = new TransactionDialog(activity, name);
                        cdd.show();
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd("ca-app-pub-6328620287804966/2652136547",
                new AdRequest.Builder().build());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { switch(item.getItemId()) {
        case R.id.star:
            if (mRewardedVideoAd.isLoaded()) {
                mRewardedVideoAd.show();
                loadRewardedVideoAd();
            }
            return(true);

    }
        return(super.onOptionsItemSelected(item));
    }

    public void loadAdMob(){
        if(admobCount > 0)
            admobCount --;
        else {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
                mInterstitialAd = new InterstitialAd(activity);
                mInterstitialAd.setAdUnitId("ca-app-pub-6328620287804966/4894731472");
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                admobCount = 10;
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);
            }
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    public void onRewardedVideoAdLoaded() {

    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {

    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        balanceDolar += rewardItem.getAmount();
        editor.putString("balance", String.valueOf(balanceDolar));
        editor.commit();
        NumberFormat formatter = new DecimalFormat("#0.00");
        balance.setText(MainActivity.balanceName + ":" + formatter.format(balanceDolar) + "$");
        appendbalance();

    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {

    }

    public static void appendbalance() {

        double value = 0;
        NumberFormat formatter = new DecimalFormat("#0.00");
        balance.setText(MainActivity.balanceName + ":" + formatter.format(balanceDolar) + "$");
        for(int i = 0; i < coinInfos.size(); i++) {
            value += coinInfos.get(i).value * coinInfos.get(i).amount;
        }
        balance.append("\n" + coinbalance + formatter.format(value) + "$");
    }
}
