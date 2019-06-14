package com.example.android_trabalho_anuncio_vendas.activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.android_trabalho_anuncio_vendas.R;
import com.example.android_trabalho_anuncio_vendas.helper.Permissoes;

import java.util.ArrayList;
import java.util.List;

public class CadastrarAnuncioActivity extends AppCompatActivity
        implements View.OnClickListener {

    private EditText Titulo, Valor, Descricao, Telefone;
    private ImageView image1, iimage2;
    private List<String> listaFotosRecuperadas = new ArrayList<>();
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

    @Override
    public void onClick(View v) {
        Log.d("onClick", "onClick: " + v.getId() );
        switch ( v.getId() ){
            case R.id.image1 :
                Log.d("onClick", "onClick: " );
                escolherImagem(1);
                break;
            case R.id.image2 :
                escolherImagem(2);
                break;
        }
    }

    public void escolherImagem(int requestCode){
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( resultCode == Activity.RESULT_OK){

            //Recuperando imagem
            Uri imagemSelecionada = data.getData();
            String caminhoImagem = imagemSelecionada.toString();

            //Configurando imagem
            if( requestCode == 1 ){
                image1.setImageURI( imagemSelecionada );
            }else if( requestCode == 2 ){
                iimage2.setImageURI( imagemSelecionada );
        }
            listaFotosRecuperadas.add( caminhoImagem );

        }
    }

    private void inicializandoComponentes(){
        Titulo = findViewById(R.id.Titulo);
        Valor = findViewById(R.id.Valor);
        Descricao = findViewById(R.id.Descricao);
        Telefone = findViewById(R.id.Telefone);
        image1 = findViewById(R.id.image1);
        iimage2 = findViewById(R.id.image2);
        image1.setOnClickListener(this);
        iimage2.setOnClickListener(this);
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


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}