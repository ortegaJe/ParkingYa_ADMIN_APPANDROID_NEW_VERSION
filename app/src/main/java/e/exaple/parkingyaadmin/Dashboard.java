package e.exaple.parkingyaadmin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

import static com.loopj.android.http.AsyncHttpClient.LOG_TAG;

public class Dashboard extends AppCompatActivity implements interfaceListener {

    http http;
    ConnectionDetector cd;
    private ListView listView;
    SharedPreferences pref;
    public  final String MyPREFERENCES = "MyPrefs";
    private CoordinatorLayout layout_dashboard;
    ArrayList<String> placa = new ArrayList<>();
    ArrayList<String> rowi = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = findViewById(R.id.listview);
        layout_dashboard =findViewById(R.id.layout_dashboard);
        registerForContextMenu(listView);
        pref = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        getReserv(pref.getString(Constants.SEDE,""));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Toasty.success(getApplicationContext(), "Reserva con numero de placa: " + placa.get(position) + " en la posición " + rowi.get(position), Toasty.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        Log.d(LOG_TAG, "onDestroy()");
        super.onDestroy();

        Dashboard.this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout_admin) {
            Snackbar snackbar;
            snackbar= Snackbar.make(layout_dashboard,"Desea cerrar sesión?",Snackbar.LENGTH_LONG);
            View snackBarView = snackbar.getView();
            snackbar.setAction("SI", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentExit = new Intent(Dashboard.this, LoginMain.class);
                    startActivity(intentExit);
                    finish();
                }
            });

            snackBarView.setBackgroundColor(getResources().getColor(R.color.colorYellowForSnack));
            TextView textView = snackBarView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(getResources().getColor(R.color.colorGreenForSnack));
            snackbar.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getReserv(String idsede) {
        cd = new ConnectionDetector(this);
        if(!cd.isNetworkAvailable()){
            Toast.makeText(this, "VERIFIQUE LA CONEXIÓN DE RED", Toast.LENGTH_SHORT).show();
        }else{
            http = new http(this);
            http.getReservations(idsede);
        }
    }

    @Override
    public void onPlacesListener(int identifier, Class gointo, JSONArray jsonArray) {
        switch (identifier){
            case 1:
                try {
                for(int i = 0; i < jsonArray.length(); i++) {
                        placa.add(jsonArray.getJSONObject(i).get("placaClient").toString());
                        rowi.add(jsonArray.getJSONObject(i).get("name").toString());
                }
                listView.setAdapter(new Dashboard.ImagenAdapter(getApplicationContext()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private class ImagenAdapter extends BaseAdapter {
        Context ctx;
        LayoutInflater layoutInflater;

        TextView tv_placa, tv_row;

        public ImagenAdapter(Context applicationContext){
            this.ctx=applicationContext;
            layoutInflater= (LayoutInflater)ctx.getSystemService(LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            return rowi.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row   = layoutInflater.inflate(R.layout.activity_item,parent,false);
            tv_placa  = row.findViewById(R.id.tv_placa);
            tv_row = row.findViewById(R.id.tv_row);

            tv_placa.setText(placa.get(position));
            tv_row.setText(rowi.get(position));

            return row;
        }
    }
}
