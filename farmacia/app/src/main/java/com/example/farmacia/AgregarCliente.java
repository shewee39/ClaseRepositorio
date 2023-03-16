package com.example.farmacia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class AgregarCliente extends AppCompatActivity {
    EditText nombre, apellido, puntos, codigo, dpi;
    Button btnGuardar, btnScanear;
    String id ="0";
    public String auxiliarCodigo="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_cliente);

        btnGuardar = (Button) findViewById(R.id.guardar);
        btnScanear = (Button) findViewById(R.id.scanear);
        nombre = (EditText) findViewById(R.id.nombreCliente);
        apellido = (EditText) findViewById(R.id.apellidoCliente);
        puntos = (EditText) findViewById(R.id.puntosCliente);
        codigo = (EditText) findViewById(R.id.resultado);
        dpi = (EditText) findViewById(R.id.dpiCliente);

        codigo.setEnabled(false);
        btnGuardar.setEnabled(false);


        try {
            btnGuardar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (codigo.getText().toString().equals(auxiliarCodigo)) {
                        errorDuplicado();
                        codigo.setText("");
                        btnGuardar.setEnabled(false);
                    } else {
                        btnGuardar.setEnabled(true);
                        registrarCliente("https://comedic.000webhostapp.com/registarCliente.php"); //archivo subido al hosting
                    }
                }
            });
        }
        catch (Exception e)
        {
            Intent intent = new Intent(getApplicationContext(), MenuCliente.class);
            startActivity(intent);
        }

        btnScanear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(AgregarCliente.this);
                intentIntegrator.setDesiredBarcodeFormats(intentIntegrator.ALL_CODE_TYPES);
                intentIntegrator.setPrompt("Lector");
                intentIntegrator.setCameraId(0);
                intentIntegrator.setBeepEnabled(true);
                intentIntegrator.setBarcodeImageEnabled(true);
                intentIntegrator.initiateScan();
                btnGuardar.setEnabled(true);


            }


        });
    }


    public void onActivityResult(int requestCode,int resCode, Intent data)
        {
           try {


               IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resCode, data);
               if (result != null) {
                   if (result.getContents() == null) {
                       Toast.makeText(this, "Lector cancelado", Toast.LENGTH_SHORT).show();
                   } else {
                       //Toast.makeText(this, result.getContents(), Toast.LENGTH_SHORT).show();
                       codigo.setText(result.getContents());
                       buscarCodigoCliente("https://comedic.000webhostapp.com/buscarCliente.php?codigo=" + codigo.getText() + "");

                   }
               } else {
                   super.onActivityResult(requestCode, resCode, data);
               }
           }
           catch (Exception e)
           {
               Intent intent = new Intent(getApplicationContext(), MenuCliente.class);
               startActivity(intent);
           }
        }

    private void registrarCliente(String URl)
    {
       if(codigo.getText().toString().equals("") || nombre.getText().toString().equals("") || apellido.getText().toString().equals("") || dpi.getText().toString().equals("")  || puntos.getText().toString().equals("")) {
           Toast.makeText(getApplicationContext(), "Todos los campos deben estar llenos", Toast.LENGTH_LONG).show();
       }else
           {
           StringRequest stringRequest = new StringRequest(Request.Method.POST, URl, new Response.Listener<String>() {
               @Override
               public void onResponse(String response) {
                   Toast.makeText(getApplicationContext(), "Cliente Registrado", Toast.LENGTH_LONG).show(); //mensjae de que la operacion se realizo con exito
                   limpiarFormulario();
               }
           }, new Response.ErrorListener() {
               @Override
               public void onErrorResponse(VolleyError error) {
                   Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
               }
           }) {
               protected Map<String, String> getParams() throws AuthFailureError {
                   Map<String, String> parametros = new HashMap<String, String>();
                   parametros.put("ingId", id);
                   parametros.put("ingCodigo", codigo.getText().toString());
                   parametros.put("ingNombre", nombre.getText().toString());
                   parametros.put("ingApellido", apellido.getText().toString());
                   parametros.put("ingDPI", dpi.getText().toString());
                   parametros.put("ingPuntos", puntos.getText().toString());
                   return parametros;

               }
           };
           RequestQueue requestQueue = Volley.newRequestQueue(this);
           requestQueue.add(stringRequest);
       }


       }

    private void buscarCodigoCliente(String URL)
    {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        auxiliarCodigo=(jsonObject.getString("codigo"));
                    } catch (JSONException e) {

                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
             //   Toast.makeText(getApplicationContext(), "Codigo Repetido", Toast.LENGTH_LONG).show()
            }
        });
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    public void errorDuplicado()
    {
        Toast.makeText(this, "MONEDERO DUPLICADO, INGRESE OTRA TARJETA", Toast.LENGTH_LONG).show();
    }



    private void limpiarFormulario()
    {
        codigo.setText("");
        nombre.setText("");
        apellido.setText("");
        dpi.setText("");
        puntos.setText("");

    }

    }
