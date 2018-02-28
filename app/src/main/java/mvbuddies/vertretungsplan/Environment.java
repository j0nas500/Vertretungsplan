package mvbuddies.vertretungsplan;

import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by survari on 28.02.18.
 */

public class Environment {
    public enum VPMode {
        STUDENT,
        TEACHER
    }

    public enum VPTime {
        TODAY,
        TOMORROW
    }

    public static VPMode _MODE = VPMode.STUDENT; // Der Modus der Sicht, standard auf Schüler gesetzt
    public static VPTime _DAY = VPTime.TODAY; // Option für Heute/Morgen
    public static List<String> _VERTRETUNG = new ArrayList<>();
}
