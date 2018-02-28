package mvbuddies.vertretungsplan;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ToggleButton;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private ArrayAdapter<String> adapter;
    private List<Map<String, String>> _tmp = new ArrayList<>();
    private boolean _error = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = new ArrayAdapter<String>(this, R.layout.list_view, R.id.txtitem, Environment._VERTRETUNG);
        ListView v = findViewById(R.id.list_vp);
        v.setAdapter(adapter);

        loadSchedule();

        ToggleButton n = (ToggleButton) findViewById(R.id.morgenheute);
        n.setChecked(false);
        n.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Environment._DAY == Environment.VPTime.TODAY)
                    Environment._DAY = Environment.VPTime.TOMORROW;
                else
                    Environment._DAY = Environment.VPTime.TODAY;

                loadSchedule();
            }
        });
    }

    // Toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                loadSchedule();
                break;
            case R.id.action_teacher:
                Environment._MODE = Environment.VPMode.TEACHER;
                break;
            case R.id.action_student:
                Environment._MODE = Environment.VPMode.STUDENT;
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

    public void loadSchedule() {
//        /* ZEIT Bitte nichts verändern */
//        String untildate = "2017-09-19";
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        Calendar cal = Calendar.getInstance();
//
//        try {
//            cal.setTime(dateFormat.parse(untildate));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        cal.add(Calendar.DAY_OF_YEAR, 1);
//        String convertedDate = dateFormat.format(cal.getTime());
//
//        Date tomorrow = new Date();
//        Calendar c = Calendar.getInstance();
//        c.setTime(tomorrow);
//        c.add(Calendar.DAY_OF_YEAR, 1);
//
//        tomorrow = c.getTime();
//        String Vertretungsplan = new String(String.valueOf(tomorrow));
//        String datum = dateFormat.format(c.getTime());
//
//        Date datetomorrow = new Date();
//        Calendar ctomorrow = Calendar.getInstance();
//        ctomorrow.setTime(datetomorrow);
//        ctomorrow.add(Calendar.DAY_OF_YEAR, 1);
//        datetomorrow = ctomorrow.getTime();
//        String Vertretungsplantomorrow = new String(String.valueOf(datetomorrow));
//        final String datumtomorrow = dateFormat.format(ctomorrow.getTime());
//
//        Date today = new Date();
//        Calendar ctoday = Calendar.getInstance();
//        ctoday.setTime(today);
//        ctoday.add(Calendar.DAY_OF_YEAR, 0);
//        today = ctoday.getTime();
//        String Vertretungsplantoday = new String(String.valueOf(today));
//        final String datumtoday = dateFormat.format(ctoday.getTime());

        Environment._VERTRETUNG.clear();

        Date d = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(d);

        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
            c.add(Calendar.DAY_OF_YEAR, 2);

        if (Environment._DAY != Environment.VPTime.TODAY) {
            if (c.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
                Environment._VERTRETUNG.add("Kann den Vertretungsplan am Freitag noch nicht laden.\nWelcher Administrator soll den bitte heute schon fertig haben?!");
                adapter.notifyDataSetChanged();
            } else
                c.add(Calendar.DAY_OF_YEAR, 1);
        } else {
            if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
                c.add(Calendar.DAY_OF_YEAR, 1);
        }

        d = c.getTime();

        System.out.println("NEW DATE: " + d);
        System.out.println(" => " + c.get(Calendar.YEAR) + " " + (c.get(Calendar.MONTH) + 1) + " " + c.get(Calendar.DAY_OF_MONTH));

        String YEAR = Integer.toString(c.get(Calendar.YEAR));
        String MONTH = Integer.toString(c.get(Calendar.MONTH) + 1);
        String DAY = Integer.toString(c.get(Calendar.DAY_OF_MONTH));

        if (c.get(Calendar.MONTH) < 9)
            MONTH = "0" + MONTH;
        if (c.get(Calendar.DAY_OF_MONTH) < 10)
            DAY = "0" + DAY;

        String URL = "";

        if (Environment._MODE == Environment.VPMode.STUDENT) {
            URL = "https://files.itslearning.com/data/2226/3/vertretungsplan%20sch%C3%BCler" + YEAR + "-" + MONTH + "-" + DAY + ".html";
        } else {
            URL = "https://files.itslearning.com/data/2226/3/vertretungsplan%20lehrer" + YEAR + "-" + MONTH + "-" + DAY + ".html";
        }

        (new Networking()).execute(URL);

        if (_error) {
            Environment._VERTRETUNG.clear();
            Environment._VERTRETUNG.add("Konnte den Vertretungsplan nicht laden.\nEntweder ist die Verbindung zu schlecht,\ndu hast kein Internet,\noder der Plan existiert nicht mehr.\n\nVielleicht sind auch die Entwickler schuld ;).");
            adapter.notifyDataSetChanged();
            _error = false;
            return;
        }

        String t = "Heute";

        if (Environment._DAY == Environment.VPTime.TOMORROW) {
            t = "Morgen";
        }

        Environment._VERTRETUNG.add("Vertretungsplan von " + t + ": " + (c.get(Calendar.DAY_OF_MONTH)) + "." + (c.get(Calendar.MONTH) + 1) + "." + c.get(Calendar.YEAR));

        for (Map map : _tmp) {
            System.out.println("=> CHANGE");
            String sentence = "";

            if (Environment._MODE == Environment.VPMode.STUDENT) { // Schauen ob "Schüler" asugewählt wurde
                /* [ Schlüssel ] | [Wert ]
                     klasse      -
                     stunde      - In welcher Stunde die Vertretung stattfindet
                     fach        - das neue Fach
                     lehrer      - der vertretende Lehrer
                     raum        - der neue Raum
                     info        - sonstige Info (meist sowas wie "für ILZ mit XYZ"

                     auf einen dieser Werte zugreifen:  map["<Schlüssel>"]
                     Beispiel:                          sentence = "Diese Klasse hat Vertretung: "+map["klasse"];
                */

                //Hinzegefügt von Jonas 28.02.18 23:24
                map.put("klasse", "9c");
                map.put("stunde", "1-2");
                map.put("fach", "Deutsch");
                map.put("lehrer", "Frau Müller");
                map.put("raum", "112");
                map.put("info", "für Mathe Herr Bauer");

                //Lehrer
                map.put("neues_fach", "Englisch");
                map.put("fuer_fach", "Mathe");
                map.put("fuer_lehrer", "Herr Groß");


                sentence = "Vertretung: Die Klasse "+map.get("klasse")+" hat in der Stunde "+map.get("stunde")+" im Raum "+map.get("raum")+" "+map.get("fach")+" mit "+map.get("lehrer")+".\nInfo: "+map.get("info"); // Sentence wird später angezeigt.
            } else {
                /* [ Schlüssel ] | [Wert ]
                     lehrer      - Der vertrende Lehrer
                     stunde      - In welcher Stunde der Lehrer diese Vertretung ausübt
                     klasse      - Mit welcher Klasse er Vertretung hat
                     neues_fach  - Welches Fach er ausübt (= Schlüssel: raum)
                     neuer_raum  - Der Raum in dem die Vertretung stattfindet
                     fuer_fach   - Das wach, was die Klasse normalerweise dort hätte
                     fuer_lehrer - Welcher Lehrer ausfällt

                     auf einen dieser Werte zugreifen:  map["<Schlüssel>"]
                     Beispiel:                          sentence = "Diese/r Lehrer/in hat Vertretung: "+map["lehrer"];
                */

                sentence = ""+map.get("lehrer")+" hat "+map.get("neues_fach")+" in der Stunde "+map.get("stunde")+" für die "+map.get("klasse")+" im Raum "+map.get("raum")+" statt "+map.get("fuer_fach")+" mit "+map.get("fuer_lehrer")+"."+"\nInfo: "+map.get("info"); // Sentence wird später angezeigt.
            }

            Environment._VERTRETUNG.add(sentence); // Sentence wird zur Liste der Vertretungen hinzugefügt
            adapter.notifyDataSetChanged(); // Liste aktualisieren
        }
    }

    class Networking extends AsyncTask<String, String, ArrayList<Map<String, String>>> { // Neuer Thread für Herunterladen von Vertretungsplan, damit die App nicht einfriert
        @Override
        protected ArrayList<Map<String, String>> doInBackground(String... strings) {

            ArrayList<Map<String, String>> changes = new ArrayList<>();

            for (String URL : strings) {
                try {
                    System.out.println("Try connecting to : " + URL);
                    Document doc = Jsoup.connect(URL).get();

                    for (Element e : doc.body().getAllElements()) {
                        if (e.tagName().equalsIgnoreCase("table") && e.attributes().get("border").equals("2")) {
                            for (Element tr : e.getAllElements()) {
                                if (tr.tagName().equalsIgnoreCase("tr")) {
                                    Map<String, String> tmp = new HashMap<>();

                                    for (int i = 1; i < tr.getAllElements().size(); i++) {
                                        if (tr.getAllElements().get(i).tagName().equalsIgnoreCase("th"))
                                            continue;

                                        String value = tr.getAllElements().get(i).text();

                                        if (value.equals(""))
                                            break;

                                        if (Environment._MODE == Environment.VPMode.STUDENT) {
                                            tmp.put("type", "student");

                                            switch (i) {
                                                case 1:     // Klasse
                                                    tmp.put("klasse", value);
                                                    break;

                                                case 2:     // Stunde
                                                    tmp.put("stunde", value);
                                                    break;

                                                case 3:     // Fach
                                                    tmp.put("fach", value);
                                                    break;

                                                case 4:     // Lehrer
                                                    tmp.put("lehrer", value);
                                                    break;

                                                case 5:     // Raum
                                                    tmp.put("raum", value);
                                                    break;

                                                case 6:     // Info
                                                    tmp.put("info", value);
                                                    break;
                                            }
                                        } else {
                                            tmp.put("type", "teacher");

                                            switch (i) {
                                                case 1:     // Lehrer
                                                    tmp.put("lehrer", value);
                                                    break;

                                                case 2:     // Stunde
                                                    tmp.put("stunde", value);
                                                    break;

                                                case 3:     // Klasse
                                                    tmp.put("klasse", value);
                                                    break;

                                                case 4:     // Neues Fach
                                                    tmp.put("neues_fach", value);
                                                    break;

                                                case 5:     // Neuer Raum
                                                    tmp.put("neuer_raum", value);
                                                    break;

                                                case 6:     // Für das Fach
                                                    tmp.put("fuer_fach", value);
                                                    break;

                                                case 7:     // Für Lehrer
                                                    tmp.put("fuer_lehrer", value);
                                                    break;

                                                case 8:     // Info
                                                    tmp.put("info", value);
                                                    break;
                                            }
                                        }
                                    }

                                    if (Environment._MODE == Environment.VPMode.STUDENT) {
                                        if (tmp.get("klasse") != null &&
                                                tmp.get("lehrer") != null &&
                                                tmp.get("raum") != null &&
                                                tmp.get("stunde") != null &&
                                                tmp.get("fach") != null &&
                                                tmp.get("info") != null)
                                            changes.add(tmp);
                                    } else {
                                        if (tmp.get("lehrer") != null &&
                                                tmp.get("stunde") != null &&
                                                tmp.get("klasse") != null &&
                                                tmp.get("neues_fach") != null &&
                                                tmp.get("neuer_raum") != null &&
                                                tmp.get("fuer_fach") != null &&
                                                tmp.get("fuer_lehrer") != null &&
                                                tmp.get("info") != null)
                                            changes.add(tmp);
                                    }
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    _error = true;
                    return null;
                }
            }

            _tmp.clear();
            _tmp = changes;
            return null;
        }
    }
}
