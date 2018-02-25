package mvbuddies.vertretungsplan;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {


    WebView webView ;
    SwipeRefreshLayout swipe;
    private ProgressBar progressBar;
    private FrameLayout frameLayout;





    // Toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_refresh:
                LoadWeb();
                break;
            case R.id.action_lehrer:
                Intent openActivityChatIntent = new Intent(MainActivity.this,
                        VertretungsplanLehrerActivity.class);
                startActivity(openActivityChatIntent);
                break;
            default:
                break;

        }

        return true;


    }






    //Suche: noch nichts damit gemacht
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.vertretungsplan_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }
    //OFFLINE MODUS
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);






        //Refresh Layout
        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LoadWeb();
            }
        });



        LoadWeb();



    }








    public void LoadWeb() {

// ZEIT Bite nichts verändern
        String untildate="2017-09-19";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(dateFormat.parse(untildate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        cal.add(Calendar.DATE, 1);
        String convertedDate=dateFormat.format(cal.getTime());

        Date tomorrow = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(tomorrow);
        c.add(Calendar.DATE, 1);
        tomorrow = c.getTime();
        String Vertretungsplan = new String(String.valueOf(tomorrow));
        String datum=dateFormat.format(c.getTime());

        Date datetomorrow = new Date();
        Calendar ctomorrow = Calendar.getInstance();
        ctomorrow.setTime(datetomorrow);
        ctomorrow.add(Calendar.DATE, 1);
        datetomorrow = ctomorrow.getTime();
        String Vertretungsplantomorrow = new String(String.valueOf(datetomorrow));
        final String datumtomorrow=dateFormat.format(ctomorrow.getTime());

        Date today = new Date();
        Calendar ctoday = Calendar.getInstance();
        ctoday.setTime(today);
        ctoday.add(Calendar.DATE, 0);
        today = ctoday.getTime();
        String Vertretungsplantoday = new String(String.valueOf(today));
        final String datumtoday=dateFormat.format(ctoday.getTime());







        frameLayout = (FrameLayout) findViewById(R.id.frameLayout);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(100);
        webView = (WebView) findViewById(R.id.webView);
        // Erlaube JavaScript
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setFocusable(true);
        webView.setFocusableInTouchMode(true);
        //Zoom
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(false);
        // Setzte Render-Priorität Hoch Wieso auch immer :D
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        // Speicher die HTML Seite für Offline Nutzung
        webView.getSettings().setAppCacheMaxSize(5 * 1024 * 1024); // 5MB
        webView.getSettings().setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT); // load online by default
        if (!isNetworkAvailable()) { // loading offline
            webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            Toast.makeText(MainActivity.this, "Offlinemodus", Toast.LENGTH_LONG).show();
        }
        // URL laden
        webView.loadUrl("https://files.itslearning.com/data/2226/3/vertretungsplan%20schüler"+datum+".html");
        swipe.setRefreshing(true);
        webView.setWebViewClient(new WebViewClient());

        // Heute_Morgen Umstellung
        ToggleButton toggle = (ToggleButton) findViewById(R.id.morgenheute);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(getApplicationContext(), "Vertretungsplan für Heute wird geladen", Toast.LENGTH_SHORT).show();
                    webView.loadUrl("https://files.itslearning.com/data/2226/3/vertretungsplan%20schüler"+datumtoday+".html");
                } else {
                    Toast.makeText(getApplicationContext(), "Vertretungsplan für Morgen wird geladen", Toast.LENGTH_SHORT).show();
                    webView.loadUrl("https://files.itslearning.com/data/2226/3/vertretungsplan%20schüler"+datumtomorrow+".html");
                }
            }
        });




        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(final WebView view, int progress) {



                frameLayout.setVisibility(View.GONE);
                progressBar.setProgress(progress);

                setTitle("Lädt...");

                if (progress == 0) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 0%");
                }
                if (progress == 1) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 1%");
                }
                if (progress == 2) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 2%");
                }
                if (progress == 3) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 3%");
                }
                if (progress == 4) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 4%");
                }
                if (progress == 5) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 5%");
                }
                if (progress == 6) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 6%");
                }
                if (progress == 7) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 7%");
                }
                if (progress == 8) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 8%");
                }
                if (progress == 9) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 9%");
                }
                if (progress == 10) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 10%");
                }
                if (progress == 11) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 11%");
                }
                if (progress == 12) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 12%");
                }
                if (progress == 13) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 13%");
                }
                if (progress == 14) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 14%");
                }
                if (progress == 15) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 15%");
                }
                if (progress == 16) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 16%");
                }
                if (progress == 17) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 17%");
                }
                if (progress == 18) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 18%");
                }
                if (progress == 19) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 19%");
                }
                if (progress == 20) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 20%");
                }
                if (progress == 21) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 21%");
                }
                if (progress == 22) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 22%");
                }
                if (progress == 23) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 23%");
                }
                if (progress == 24) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 24%");
                }
                if (progress == 25) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 25%");
                }
                if (progress == 26) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 26%");
                }
                if (progress == 27) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 27%");
                }
                if (progress == 28) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 28%");
                }
                if (progress == 29) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 29%");
                }
                if (progress == 30) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 30%");
                }
                if (progress == 31) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 31%");
                }
                if (progress == 32) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 32%");
                }
                if (progress == 33) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 33%");
                }
                if (progress == 34) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 34%");
                }
                if (progress == 35) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 35%");
                }
                if (progress == 36) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 36%");
                }
                if (progress == 37) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 37%");
                }
                if (progress == 38) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 38%");
                }
                if (progress == 39) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 39%");
                }
                if (progress == 40) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 40%");
                }
                if (progress == 41) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 41%");
                }
                if (progress == 42) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 42%");
                }
                if (progress == 43) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 43%");
                }
                if (progress == 44) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 44%");
                }
                if (progress == 45) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 45%");
                }
                if (progress == 46) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 46%");
                }
                if (progress == 47) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 47%");
                }
                if (progress == 48) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 48%");
                }
                if (progress == 49) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 49%");
                }
                if (progress == 50) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 50%");
                }
                if (progress == 61) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 61%");
                }
                if (progress == 62) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 62%");
                }
                if (progress == 63) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 63%");
                }
                if (progress == 64) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 64%");
                }
                if (progress == 65) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 65%");
                }
                if (progress == 66) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt...66%");
                }
                if (progress == 67) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 67%");
                }
                if (progress == 68) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 68%");
                }
                if (progress == 69) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 69%");
                }
                if (progress == 70) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 70%");
                }
                if (progress == 71) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 71%");
                }
                if (progress == 72) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 72%");
                }
                if (progress == 73) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 73%");
                }
                if (progress == 74) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 74%");
                }
                if (progress == 75) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 75%");
                }
                if (progress == 76) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 76%");
                }
                if (progress == 77) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 77%");
                }
                if (progress == 78) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 78%");
                }
                if (progress == 79) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 79%");
                }
                if (progress == 80) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 80%");
                }
                if (progress == 81) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 81%");
                }
                if (progress == 82) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 82%");
                }
                if (progress == 83) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 83%");
                }
                if (progress == 84) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 84%");
                }
                if (progress == 85) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 85%");
                }
                if (progress == 86) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 86%");
                }
                if (progress == 87) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 87%");
                }
                if (progress == 88) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 88%");
                }
                if (progress == 89) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 89%");
                }
                if (progress == 90) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 90%");
                }
                if (progress == 91) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 91%");
                }
                if (progress == 92) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 92%");
                }
                if (progress == 93) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 93%");
                }
                if (progress == 94) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 94%");
                }
                if (progress == 95) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 95%");
                }
                if (progress == 96) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 96%");
                }
                if (progress == 97) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 97%");
                }
                if (progress == 98) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 98%");
                }
                if (progress == 99) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 99%");
                }
                if (progress == 100) {
                    frameLayout.setVisibility(View.GONE);
                    setTitle("Lädt... 100%");
                    // SETZTE Standard TITEL
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setTitle(view.getTitle());
                        }
                    }, 500);


                }

                super.onProgressChanged(view, progress);

                ProgressDialog progressDialog = null;





            }


        });


//No Internet dialog:
        webView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView webView, int errorCode, String description, String failingUrl) {

                webView.loadUrl("file:///android_asset/error.html");

            }
            public void onPageFinished(WebView view, String url) {

                //Versteck Refresh
                swipe.setRefreshing(false);
            }
        });




    }


}
