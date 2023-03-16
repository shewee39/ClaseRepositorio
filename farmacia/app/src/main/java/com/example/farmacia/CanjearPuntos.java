package com.example.farmacia;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CanjearPuntos extends AppCompatActivity {

    EditText nombre, apellido, puntos2, codigo, puntosCanjear, dpi;
    Button btnBuscar, btnScaner, btnCanjearPuntos;
    int canjeTotal = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canjear_puntos);

        btnBuscar = (Button) findViewById(R.id.ediBuscar);
        btnScaner = (Button) findViewById(R.id.ediBuscarScanner);
        btnCanjearPuntos = (Button) findViewById(R.id.ediCanjear);

        nombre = (EditText) findViewById(R.id.ediNombreCliente);
        apellido = (EditText) findViewById(R.id.ediApellidoCliente);
        puntos2 = (EditText) findViewById(R.id.ediPuntosClienteActual);
        puntosCanjear = (EditText) findViewById(R.id.ediPuntosCanjear);
        codigo = (EditText) findViewById(R.id.ediCodigo);
        dpi = (EditText) findViewById(R.id.ediDpi);

        btnBuscar.setEnabled(false);
        btnCanjearPuntos.setEnabled(false);

        //SCANNER
        btnScaner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(CanjearPuntos.this);
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
                    btnCanjearPuntos.setEnabled(true);
                }
            });
        }
        catch (Exception e)
        {
            Intent intent = new Intent(getApplicationContext(), MenuCliente.class);
            startActivity(intent);
        }

        try {


            btnCanjearPuntos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    editarCliente("https://comedic.000webhostapp.com/editarCliente.php");

                }
            });
        }
        catch (Exception e)
        {
            Intent intent = new Intent(getApplicationContext(), MenuCliente.class);
            startActivity(intent);
        }

    }

    protected void onActivityResult(int requestCode, int resCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Lector cancelado", Toast.LENGTH_SHORT).show();
            } else {
                //Toast.makeText(this, result.getContents(), Toast.LENGTH_SHORT).show();
                codigo.setText(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resCode, data);
        }
    }

    private void buscarCliente(String URL) {
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
                        puntos2.setText(jsonObject.getString("puntos"));
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
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }


    private void editarCliente(String URl) {
        if (Integer.parseInt(puntos2.getText().toString()) >= Integer.parseInt(puntosCanjear.getText().toString())) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Toast.makeText(getApplicationContext(), "Puntos Canjeados", Toast.LENGTH_LONG).show(); //mensjae de que la operacion se realizo con exito
                    limpiarFormulario();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                }
            }) {

                protected Map<String, String> getParams() throws AuthFailureError {

                    canjeTotal = Integer.parseInt(puntos2.getText().toString()) - Integer.parseInt(puntosCanjear.getText().toString());
                    Map<String, String> parametros = new HashMap<String, String>();
                    // parametros.put("ediId", id);
                    parametros.put("ediCodigo", codigo.getText().toString());
                    parametros.put("ediPuntos", Integer.toString(canjeTotal));
                    return parametros;


                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        }
        else

        {
            Toast.makeText(getApplicationContext(), "INGRESE UNA CANTIDAD MINIMA O IGUAL A LA CANTIDAD DE PUNTOS ACTUALES", Toast.LENGTH_LONG).show(); //mensjae de que la operacion se realizo con exito }
        }
    }

    private void limpiarFormulario()
    {
        codigo.setText("");
        nombre.setText("");
        apellido.setText("");
        dpi.setText("");
        puntos2.setText("");
        puntosCanjear.setText("");
    }
}