package e.exaple.parkingyaadmin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
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

public class Dashboard extends AppCompatActivity implements interfaceListener {

    http http;
    ConnectionDetector cd;
    private ListView listView;
    SharedPreferences pref;
    public  final String MyPREFERENCES = "MyPrefs";
    ArrayList<String> placa = new ArrayList<>();
    ArrayList<String> rowi = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView = findViewById(R.id.listview);
        registerForContextMenu(listView);
        pref = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        getReserv(pref.getString(Constants.SEDE,""));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(Dashboard.this, "Reserva con numero de placa: " + placa.get(position) + " en la posición " + rowi.get(position), Toast.LENGTH_SHORT).show();
            }
        });
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
