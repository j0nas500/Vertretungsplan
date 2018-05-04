package mvbuddies.vertretungsplan;

import android.content.Context;
import android.text.Editable;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by survari on 28.02.18.
 */

public class Environment {
    public static boolean _SETTINGS = false;
    public static boolean _ONLY_CLASSES = false;
    public static boolean _BY_NAME = false;
    public static String _FILES_DIR = "";
    public static List<String> _CLASSES = new ArrayList<String>();
    public static String _USER_FILE = "/data/data/mvbuddies.vertretungsplan/files/user.json";

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

    public static void saveUser(Editable _name, Editable _class, boolean checked, boolean byname) {
        File f = new File(_USER_FILE);

        if (f.exists())
            f.delete();

        try {
            JSONObject jo = new JSONObject();
            jo.put("name", _name.toString());
            jo.put("classes", _class.toString());
            jo.put("yn", checked);
            jo.put("byname", byname);

            BufferedWriter outputStream;

            try {
                outputStream = new BufferedWriter(new FileWriter(new File(_USER_FILE)));
                outputStream.write(jo.toString());
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            System.out.println("ERROR =============================");
            e.printStackTrace();
            System.exit(1);
        }

    }

    public static JSONObject getUser() {
        JSONObject jo = null;
        String ret = "";

        try {
            FileReader inputStream = new FileReader((new File(_USER_FILE).getAbsolutePath()));

            if ( inputStream != null ) {
                BufferedReader bufferedReader = new BufferedReader(inputStream);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }

            jo = new JSONObject(ret);
        }
        catch (FileNotFoundException e) {
            Log.e("user init activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("user init activity", "Can not read file: " + e.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("user init activity", "JSONError at parsing: " + e.toString());
        }

        return jo;
    }

    public static void loadUser() {
        JSONObject jo;
        String ret = "";

        try {
            FileReader inputStream = new FileReader((new File(_USER_FILE).getAbsolutePath()));

            if ( inputStream != null ) {
                BufferedReader bufferedReader = new BufferedReader(inputStream);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }

            jo = new JSONObject(ret);
            _ONLY_CLASSES = jo.getBoolean("yn");
            _BY_NAME = jo.getBoolean("byname");
            _CLASSES = Arrays.asList((jo.getString("classes")).split(", "));
        }
        catch (FileNotFoundException e) {
            Log.e("user init activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("user init activity", "Can not read file: " + e.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("user init activity", "JSONError at parsing: " + e.toString());
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
