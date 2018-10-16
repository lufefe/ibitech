package com.divide.ibitech.divide_ibitech;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.divide.ibitech.divide_ibitech.Adapter.AllergyListAdapter;
import com.divide.ibitech.divide_ibitech.Models.AllergyList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocPatientAllergies extends AppCompatActivity {

    List<AllergyList> alleList;
    ListView allegyListView;

    android.support.v7.widget.Toolbar toolbar;
    String patientID = "",patientName="";
    String URLGETALLRGY = "http://sict-iis.nmmu.ac.za/ibitech/app/getallergy.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_patient_allergies);

        allegyListView=findViewById(R.id.listAllergy);

        //shared prefs for patient data
        SharedPreferences prefs = getSharedPreferences("PATIENT",MODE_PRIVATE);
        patientID = prefs.getString("pID", "");
        patientName = prefs.getString("pName","");

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(patientName + "\'s Allergies");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ShowList(patientID);

    }

    private void ShowList(final String id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLGETALLRGY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray array = obj.getJSONArray("server_response");

                            //Parallel arrays
                            final String[] allergyID = new String[array.length()];
                            final String[] allergyName = new String[array.length()];
                            final String[] allergyType = new String[array.length()];
                            final String[] species = new String[array.length()];
                            final String[] dateAdded = new String[array.length()];
                            final String[] treatment = new String[array.length()];
                            final String[] tested = new String[array.length()];
                            alleList= new ArrayList<>();
                            for (int x= 0; x < array.length(); x++){
                                JSONObject allergyObject = array.getJSONObject(x);

                                allergyID[x] = allergyObject.getString("allergy_id");
                                allergyName[x] = allergyObject.getString("allergy_name");
                                allergyType[x] = allergyObject.getString("allergy_type");
                                species[x] = allergyObject.getString("species");
                                dateAdded[x] = allergyObject.getString("date_added");
                                treatment[x] = allergyObject.getString("treatment_id");
                                tested[x] = allergyObject.getString("tested");

                                AllergyList allergy = new AllergyList(allergyObject.getString("allergy_name"),
                                        allergyObject.getString("date_added"));
                                alleList.add(allergy);

                            }

                            AllergyListAdapter adapter =  new AllergyListAdapter(alleList,getApplication());
                            allegyListView.setAdapter(adapter);


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(DocPatientAllergies.this,"Error "+e.toString(),Toast.LENGTH_LONG).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(DocPatientAllergies.this,"Error 2"+error.toString(),Toast.LENGTH_LONG).show();
            }
        }){

            @Override
            protected Map<String,String> getParams() {
                Map<String,String> params = new HashMap<>();

                params.put("id",id);
                return params;
            }

        };
        Singleton.getInstance(DocPatientAllergies.this).addToRequestQue(stringRequest);
    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home)
            this.finish();

        return super.onOptionsItemSelected(item);
    }
}
