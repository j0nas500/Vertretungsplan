package mvbuddies.vertretungsplan;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class InitUser extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_user);

        JSONObject jo = Environment.getUser();

        final Button save = (Button) findViewById(R.id.save);
        final EditText et_class = (EditText) findViewById(R.id.et_class);
        final EditText et_name = (EditText) findViewById(R.id.et_name);
        final CheckBox cb_class = (CheckBox) findViewById(R.id.cb_class);

        if (Environment._SETTINGS) {
            try {
                if (jo != null) {
                    et_class.setText(jo.getString("classes"), TextView.BufferType.EDITABLE);
                    et_name.setText(jo.getString("name"), TextView.BufferType.EDITABLE);
                    cb_class.setChecked(jo.getBoolean("yn"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Environment.saveUser(et_name.getText(), et_class.getText(), cb_class.isChecked());
               finish();
            }
        });
    }

}
