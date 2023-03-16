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
import com.r0adkll.slidr.Slidr;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EliminarCliente extends AppCompatActivity {
    EditText eUsuario, tNombre, tApellido, tUsuario, tAcademico;

    Button btnBuscar, btnRegresar, btnEliminar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eliminar_cliente);

        tNombre=findViewById(R.id.busNombre);
        tApellido=findViewById(R.id.busApellido);
        tUsuario=findViewById(R.id.busUsuarioo);
        eUsuario=findViewById(R.id.busUsuario);
        tAcademico=findViewById(R.id.busAcademico);
        btnBuscar=findViewById(R.id.busBuscar);

        btnEliminar=findViewById(R.id.busEliminar);

        Slidr.attach(this);
        try {


            btnBuscar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    buscarEstudiante("https://comedic.000webhostapp.com/buscarEstudiante.php?usuario=" + eUsuario.getText() + "");
                }
            });
        }
        catch (Exception e)
        {
            Intent intent = new Intent(getApplicationContext(), MenuCliente.class);
            startActivity(intent);
        }

        try {


            btnEliminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    eliminarEstudiante("https://comedic.000webhostapp.com/eliminarEstudiante.php");
                }
            });
        }
        catch (Exception e)
        {
            Intent intent = new Intent(getApplicationContext(), MenuCliente.class);
            startActivity(intent);
        }

    }

    private void buscarEstudiante(String URL)
    {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        tNombre.setText(jsonObject.getString("nombre"));
                        tApellido.setText(jsonObject.getString("apellido"));
                        tUsuario.setText(jsonObject.getString("usuario"));
                        tAcademico.setText(jsonObject.getString("nivelAcademico"));
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error de conexión", Toast.LENGTH_LONG).show();
            }
        });
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    private void eliminarEstudiante(String URl) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "Estudiante Eliminado", Toast.LENGTH_LONG).show(); //mensjae de que la operacion se realizo con exito
                limpiarFormulario();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                //estos parametros son los que se envian al archivo subido en el hosting
                //lo que esta en verde tiene que coincidir con el archivo
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("eliUsuario", eUsuario.getText().toString());
                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void limpiarFormulario()
    {
        eUsuario.setText("");
        tApellido.setText("");
        tNombre.setText("");
        tUsuario.setText("");
        tAcademico.setText("");
    }
}