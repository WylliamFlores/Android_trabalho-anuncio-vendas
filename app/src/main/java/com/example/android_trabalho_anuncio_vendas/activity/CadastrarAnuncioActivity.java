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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android_trabalho_anuncio_vendas.R;
import com.example.android_trabalho_anuncio_vendas.helper.ConfiguracaoFirebase;
import com.example.android_trabalho_anuncio_vendas.helper.Permissoes;
import com.example.android_trabalho_anuncio_vendas.model.Anuncio;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class CadastrarAnuncioActivity extends AppCompatActivity
        implements View.OnClickListener {

    private EditText Titulo, Valor, Descricao, Telefone;
    private ImageView image1, iimage2;
    private Spinner spinnerCidades, spinnerCategorias;
    private List<String> listaFotosRecuperadas = new ArrayList<>();
    private List<String> listaURLFotos = new ArrayList<>();
    private Anuncio anuncio;
    private StorageReference storage;
    private String [] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_anuncio);

        //Configurações Storage
        storage = ConfiguracaoFirebase.getFirebaseStorage();

        //Validando permissões
        Permissoes.validarPermissoes(permissoes, this, 1);

        inicializandoComponentes();
        carregarSpinner();
    }

    public void salvarAnuncio(){

        Toast.makeText(CadastrarAnuncioActivity.this, "Aguarde, o cadastro esta sendo processado e salvo.", Toast.LENGTH_LONG).show();
        //Salvar imagem
        for (int i=0; i < listaFotosRecuperadas.size(); i++){
            String urlImagem = listaFotosRecuperadas.get(i);
            int tamanhoLista = listaFotosRecuperadas.size();
            salvarFotoStorage(urlImagem, tamanhoLista, i );

        }
    }

    private void salvarFotoStorage(String urlString, final int totalFotos, int cont){

        //Criar nó no storage
        final StorageReference imagemAnuncio = storage.child("imagens")
                .child("anuncios")
                .child( anuncio.getIdAnuncio() )
                .child("imagem"+cont);

        //Fazer upload do arquivo
        UploadTask uploadTask = imagemAnuncio.putFile(Uri.parse(urlString));
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()){
                    throw task.getException();
                }
                return imagemAnuncio.getDownloadUrl();
            }
        }) .addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()){
                    Uri downloadUri = task.getResult();
                    listaURLFotos.add(downloadUri.toString());
                    if(totalFotos==listaURLFotos.size()){
                        anuncio.setFotos(listaURLFotos);
                        anuncio.salvar();
                        finish();
                    }
                }
            }
        }) .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CadastrarAnuncioActivity.this, "Falha no Upload", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Anuncio configurarAnuncio(){

        String cidades  = spinnerCidades.getSelectedItem().toString();
        String categoria  = spinnerCategorias.getSelectedItem().toString();
        String titulo  = Titulo.getText().toString();
        String valor = Valor.getText().toString();
        String telefone = Telefone.getText().toString();
        String descricao = Descricao.getText().toString();

        Anuncio anuncio = new Anuncio();
        anuncio.setCidades( cidades );
        anuncio.setCategoria(categoria);
        anuncio.setTitulo(titulo);
        anuncio.setValor(valor);
        anuncio.setTelefone( telefone );
        anuncio.setDescricao(descricao);

        return anuncio;

    }


    public void validarCamposAnuncio(View view){

        anuncio = configurarAnuncio();

        if( listaFotosRecuperadas.size() !=0
                && !anuncio.getCidades().isEmpty()
                && !anuncio.getCategoria().isEmpty()
                && !anuncio.getTitulo().isEmpty()
                && !anuncio.getValor().isEmpty()
                && !anuncio.getTelefone().isEmpty()
                && !anuncio.getDescricao().isEmpty()){
                    salvarAnuncio();
        }else{
            Toast.makeText(this, "É necessário preencher todos os campos", Toast.LENGTH_SHORT).show();
        }
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


    private void carregarSpinner() {

        //Spinner CIDADES
        String[] cidades = new String[]{
                "Porto Alegre",
                "Alvorada",
                "Cachoeirinha",
                "Campo Bom",
                "Canoas",
                "Estância Velha",
                "Esteio",
                "Gravataí",
                "Guaíba",
                "Novo Hamburgo",
                "São Leopoldo",
                "Sapiranga",
                "Sapucaia do Sul",
                "Viamão",
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cidades);
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        spinnerCidades.setAdapter( adapter );

        //Spinner CATEGORIAS
        String[] categorias = new String[]{
                "Longboard",
                "Semi Longboard",
                "Cruiser",
                "Street",
        };
        ArrayAdapter<String> adapterCategoria = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categorias);
        adapterCategoria.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        spinnerCategorias.setAdapter( adapterCategoria );
    }

    private void inicializandoComponentes(){
        Titulo = findViewById(R.id.Titulo);
        Valor = findViewById(R.id.Valor);
        Descricao = findViewById(R.id.Descricao);
        Telefone = findViewById(R.id.Telefone);
        spinnerCidades = findViewById(R.id.spinnerCidades);
        spinnerCategorias = findViewById(R.id.spinnerCategorias);
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
}