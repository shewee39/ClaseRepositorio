package com.example.farmacia;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class BuscarCliente extends AppCompatActivity {
    EditText nombre, apellido, puntos, codigo, dpi;
    Button btnBuscar, btnScaner;
    List elements;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_cliente);

        btnBuscar = (Button) findViewById(R.id.buscar);
        btnScaner = (Button) findViewById(R.id.buscarScanner);

        nombre = (EditText) findViewById(R.id.buscarNombreCliente);
        apellido = (EditText) findViewById(R.id.buscarApellidoCliente);
        puntos = (EditText) findViewById(R.id.buscarPuntosCliente);
        codigo = (EditText) findViewById(R.id.buscarCodigo);
        dpi = (EditText) findViewById(R.id.buscarDpi);

        btnBuscar.setEnabled(false);

        //SCANNER
        btnScaner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(BuscarCliente.this);
                intentIntegrator.setDesiredBarcodeFormats(intentIntegrator.ALL_CODE_TYPES);
                intentIntegrator.setPrompt("Lector");
                intentIntegrator.setCameraId(0);
                intentIntegrator.setBeepEnabled(true);
                intentIntegrator.setBarcodeImageEnabled(true);
                intentIntegrator.initiateScan();
                btnBuscar.setEnabled(true);
            }
        });

        //BUSCAR CODIGO SCANEADO
        try {


            btnBuscar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    buscarCliente("https://comedic.000webhostapp.com/buscarCliente.php?codigo=" + codigo.getText() + "");

                }
            });
        }
        catch (Exception e)
        {
            Intent intent = new Intent(getApplicationContext(), MenuCliente.class);
            startActivity(intent);
        }
    }

    protected void onActivityResult(int requestCode,int resCode, Intent data)
    {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resCode, data);
        if(result!=null)
        {
            if(result.getContents()==null)
            {
                Toast.makeText(this, "Lector cancelado", Toast.LENGTH_SHORT).show();
            }
            else
            {
               // Toast.makeText(this, result.getContents(), Toast.LENGTH_SHORT).show();
                codigo.setText(result.getContents());
            }
        }
        else
        {
            super.onActivityResult(requestCode, resCode, data);
        }
    }
    private void buscarCliente(String URL)
    {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        nombre.setText(jsonObject.getString("nombre"));
                        apellido.setText(jsonObject.getString("apellido"));
                        dpi.setText(jsonObject.getString("dpi"));
                        puntos.setText(jsonObject.getString("puntos"));
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Cliente no encontrado", Toast.LENGTH_LONG).show();
            }
        });
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }



}