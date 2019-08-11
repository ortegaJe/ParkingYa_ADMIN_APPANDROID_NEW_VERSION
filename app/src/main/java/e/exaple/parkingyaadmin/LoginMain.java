package e.exaple.parkingyaadmin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class LoginMain extends AppCompatActivity implements interfaceListener, AdapterView.OnItemSelectedListener {

    http http;
    ConnectionDetector cd;
    EditText txtInputEmail, txtInputContraseña;
    private SharedPreferences pref;
    private final String MyPREFERENCES = "MyPrefs";
    LinearLayout mainL;
    ProgressBar progress;
    Spinner parking;
    ArrayList idsedes = new ArrayList();
    ArrayList sedesName = new ArrayList();
    String sedeid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_main);

        txtInputEmail      = findViewById(R.id.editText_LoginName);
        txtInputContraseña = findViewById(R.id.editText_LoginPass);
        mainL              = findViewById(R.id.mainlayout);
        progress           = findViewById(R.id.progress);
        pref               = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        parking = findViewById(R.id.idsede);
        parking.setOnItemSelectedListener(this);
        consultParking();

    }

    private void consultParking() {
        cd = new ConnectionDetector(this);
        if(!cd.isNetworkAvailable()){
            String msg = "VERIFIQUE LA CONEXION DE RED";
            Toast.makeText(this, ""+msg, Toast.LENGTH_SHORT).show();
            mainL.setAlpha(1);
            progress.setVisibility(View.INVISIBLE);
        }else{
            http = new http(this);
            http.getParking();
        }
    }

    public void AccessLogin(View v){
        String pass = txtInputContraseña.getText().toString().trim();
        String email= txtInputEmail.getText().toString().trim();
        if(!pass.isEmpty()){
            if(!email.isEmpty()){
                mainL.setAlpha((float) 0.0);
                progress.setVisibility(View.INVISIBLE);
                validateData(email,pass);
            }else{
                Toast.makeText(this, "Este campo es requerido", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Este campo es requerido", Toast.LENGTH_SHORT).show();
        }
    }
    private void validateData(String email, String pass){
        cd = new ConnectionDetector(this);
        if(!cd.isNetworkAvailable()){
            String msg = "VERIFIQUE LA CONEXION DE RED";
            Toast.makeText(this, ""+msg, Toast.LENGTH_SHORT).show();
            mainL.setAlpha(1);
            progress.setVisibility(View.INVISIBLE);
        }else{
            http = new http(this);
            http.getCredentials(email,pass,sedeid);
        }
    }

    @Override
    public void onPlacesListener(int identifier, Class gointo, JSONArray jsonArray) {
        mainL.setAlpha(1);
            progress.setVisibility(View.INVISIBLE);
        try {

            switch (identifier){
                case 1:
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean(Constants.IS_LOGGED, true);
                    editor.putString(Constants.NAME,jsonArray.getJSONObject(0).getString("name"));
                    editor.putString(Constants.EMAIL,jsonArray.getJSONObject(0).getString("email"));
                    editor.putString(Constants.SEDE, String.valueOf(sedeid));
                    editor.apply();
                    editor.commit();
                    Intent intent = new Intent(this, Dashboard.class);
                    startActivity(intent);
                    finish();
                    break;
                case 2:
                    for (int i =0 ; i < jsonArray.length() ; i++){
                        idsedes.add(jsonArray.getJSONObject(i).getString("identifier"));
                        sedesName.add(jsonArray.getJSONObject(i).getString("name"));
                    }

                    ArrayAdapter aa = new ArrayAdapter(LoginMain.this,R.layout.spinner_item, sedesName);
                    aa.setDropDownViewResource(
                            android.R.layout.simple_spinner_dropdown_item);
                    parking.setAdapter(aa);
                    break;
                case 0:
                    String msg = jsonArray.getJSONObject(0).getString("msg");
                    Toast.makeText(this, ""+msg, Toast.LENGTH_SHORT).show();
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        switch (adapterView.getId()){

            case R.id.idsede:
                sedeid = idsedes.get(i).toString();
                break;

        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }
}
