package com.example.farmacia;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText eUsuario, eContrasena;
    Button bSesion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initializing all variables.
        eUsuario=findViewById(R.id.logUsuario);
        eContrasena=findViewById(R.id.logPassword);
        bSesion=findViewById(R.id.logSesion);

        //ASIGNAR PUNTOS
        bSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarUsuario();
            }
        });

    }
    private void validarUsuario() //este metodo esta subido en 00webhosting
    {
        if (eUsuario.getText().toString().equals("user") && eContrasena.getText().toString().equals("123")) {
            Intent intent = new Intent(getApplicationContext(), MenuCliente.class);
            startActivity(intent);
            limpiarFormulario();
        } else {
            Toast.makeText(MainActivity.this, "Usuario o Contraseña incorrecta", Toast.LENGTH_LONG).show(); //valida si la informacion ingresada es valida
            limpiarFormulario();
        }
    }

    private void limpiarFormulario()
    {
        eUsuario.setText("");
        eContrasena.setText("");
    }

}