package mvbuddies.vertretungsplan;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        final TextView tv = findViewById(R.id.txtv_info);
        tv.setText("Verantwortliche für die Entwicklung dieser App:\n" +
                "\n" +
                "    Tristan Pieper (8b)\n" +
                "    Jonas Klugmann (9c)\n" +
                "    Bjarne Bötcher (9c)\n" +
                "\n" +
                "Fehler bitte so schnell wie möglich an diese Personen melden, solange es nicht nur Verbindungsstörungen sind. Dafür können die Entwickler (meistens) nichts.\n" +
                "\n" +
                "Letzte aktualisierung dieser Daten: Mai 2018.");
    }
}
