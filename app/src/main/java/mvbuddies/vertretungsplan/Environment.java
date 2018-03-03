package mvbuddies.vertretungsplan;

import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by survari on 28.02.18.
 */

public class Environment {
    public static String getDay(int i) {
        switch (i) {
            case 1:
                return "Sontag";
            case 2:
                return "Montag";
            case 3:
                return "Dienstag";
            case 4:
                return "Mittwoch";
            case 5:
                return "Donnerstag";
            case 6:
                return "Freitag";
            case 7:
                return "Samstag";
            default:
                return "Tag des Unterganges";
        }
    }

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
