package com.example.android_trabalho_anuncio_vendas.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android_trabalho_anuncio_vendas.R;
import com.example.android_trabalho_anuncio_vendas.helper.ConfiguracaoFirebase;
import com.google.firebase.auth.FirebaseAuth;

public class AnunciosActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anuncios);

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    }

    // criar itens do menu
    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //sair do sistema e meus anuncios
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch ( item.getItemId() ){
            case R.id.menu_sair :
                autenticacao.signOut();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                break;
            case R.id.menu_anuncios :
                startActivity(new Intent(getApplicationContext(),MeusAnunciosActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
