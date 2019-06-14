package com.example.android_trabalho_anuncio_vendas.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;

import com.example.android_trabalho_anuncio_vendas.R;
import com.example.android_trabalho_anuncio_vendas.helper.Permissoes;

public class CadastrarAnuncioActivity extends AppCompatActivity {

    private EditText Titulo, Valor, Descricao, Telefone;
    private String [] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_anuncio);

        //Validando permissões
        Permissoes.validarPermissoes(permissoes, this, 1);

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


    private void alertaValidacaoPermissao(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para utilizar o app é necessário aceitar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for( int permissaoResultado : grantResults ){
            if( permissaoResultado == PackageManager.PERMISSION_DENIED){
                alertaValidacaoPermissao();
            }
        }
    }
}
