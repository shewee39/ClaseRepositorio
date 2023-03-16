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
import com.r0adkll.slidr.Slidr;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditarCliente extends AppCompatActivity {

    EditText nombre, apellido, puntos, codigo, puntosNuevos, dpi;
    Button btnBuscar, btnScaner, btnAsignarPuntos;
    int total=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_cliente);

        btnBuscar = (Button) findViewById(R.id.ediBuscar);
        btnScaner = (Button) findViewById(R.id.ediBuscarScanner);
        btnAsignarPuntos = (Button) findViewById(R.id.ediAsignar);

        nombre = (EditText) findViewById(R.id.ediNombreCliente);
        apellido = (EditText) findViewById(R.id.ediApellidoCliente);
        puntos = (EditText) findViewById(R.id.ediPuntosCliente);
        puntosNuevos = (EditText) findViewById(R.id.ediPuntosNuevos);
        codigo = (EditText) findViewById(R.id.ediCodigo);
        dpi = (EditText) findViewById(R.id.editdpi);
        //SCANNER

        btnBuscar.setEnabled(false);
        btnAsignarPuntos.setEnabled(false);

        btnScaner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(EditarCliente.this);
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
                    btnAsignarPuntos.setEnabled(true);
                }
            });
        }
        catch (Exception e)
        {
            Intent intent = new Intent(getApplicationContext(), MenuCliente.class);
            startActivity(intent);
        }

        try {


            btnAsignarPuntos.setOnClickListener(new View.OnClickListener() {
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
             //   Toast.makeText(this, result.getContents(), Toast.LENGTH_SHORT).show();
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



    private void editarCliente(String URl)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "Puntos Asignados", Toast.LENGTH_LONG).show(); //mensjae de que la operacion se realizo con exito
                limpiarFormulario();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            protected Map<String, String> getParams() throws AuthFailureError
            {
                //estos parametros son los que se envian al archivo subido en el hosting
                //lo que esta en verde tiene que coincidir con el archivo
                total= Integer.parseInt(puntos.getText().toString()) + Integer.parseInt(puntosNuevos.getText().toString());

                Map<String, String> parametros = new HashMap<String, String>();
               // parametros.put("ediId", id);
                parametros.put("ediCodigo", codigo.getText().toString());
                parametros.put("ediPuntos", Integer.toString(total));

                return parametros;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void limpiarFormulario()
    {
        codigo.setText("");
        nombre.setText("");
        apellido.setText("");
        dpi.setText("");
        puntos.setText("");
        puntosNuevos.setText("");

    }

}