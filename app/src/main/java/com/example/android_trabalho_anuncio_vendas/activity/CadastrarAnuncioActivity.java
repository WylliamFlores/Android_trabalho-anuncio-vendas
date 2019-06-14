package com.example.android_trabalho_anuncio_vendas.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;

import com.example.android_trabalho_anuncio_vendas.R;

public class CadastrarAnuncioActivity extends AppCompatActivity {

    private EditText Titulo, Valor, Descricao, Telefone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_anuncio);

        inicializandoComponentes();
    }

    public void salvarAnuncio(View view){

    }

    private void inicializandoComponentes(){
        Titulo = findViewById(R.id.Titulo);
        Valor = findViewById(R.id.Valor);
        Descricao = findViewById(R.id.Descricao);
        Telefone = findViewById(R.id.Telefone);
    }
}
