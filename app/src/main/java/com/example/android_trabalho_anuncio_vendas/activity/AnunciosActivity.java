package com.example.android_trabalho_anuncio_vendas.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.android_trabalho_anuncio_vendas.R;
import com.example.android_trabalho_anuncio_vendas.adapter.AdapterAnuncios;
import com.example.android_trabalho_anuncio_vendas.helper.ConfiguracaoFirebase;
import com.example.android_trabalho_anuncio_vendas.model.Anuncio;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class AnunciosActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private RecyclerView recyclerAnunciosPublicos;
    private Button buttonRegiao;
    private AdapterAnuncios adapterAnuncios;
    private List<Anuncio> listaAnuncios = new ArrayList<>();
    private String filtroCidade ="";
    private AlertDialog dialog;
    private DatabaseReference anunciosPublicosRefencia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anuncios);

        Componentes();

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        anunciosPublicosRefencia = ConfiguracaoFirebase.getFirebase()
                .child("anuncios");

        //Configurando RecyclerView
        recyclerAnunciosPublicos.setLayoutManager(new LinearLayoutManager(this));
        recyclerAnunciosPublicos.setHasFixedSize(true);
        adapterAnuncios = new AdapterAnuncios(listaAnuncios, this);
        recyclerAnunciosPublicos.setAdapter( adapterAnuncios );

        recuperarAnunciosPublicos();

    }

    public void fltrarCidades (View view){
        final AlertDialog.Builder dialogCidade = new AlertDialog.Builder(this);
        dialogCidade.setTitle("Selecione a cidade:");

        View viewSpinner = getLayoutInflater().inflate(R.layout.dialog_spinner, null);
        final Spinner spinnerCidade = viewSpinner.findViewById(R.id.spinnerFiltroCidade);
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
        spinnerCidade.setAdapter( adapter );

        dialogCidade.setView( viewSpinner );

        dialogCidade.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                filtroCidade = spinnerCidade.getSelectedItem().toString();
                recuperarAnunciosCidade();

            }
        });
        dialogCidade.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = dialogCidade.create();
        dialog.show();

    }

    public void recuperarAnunciosCidade(){
        anunciosPublicosRefencia = ConfiguracaoFirebase.getFirebase()
                .child("anuncios")
                .child(filtroCidade);

        anunciosPublicosRefencia.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaAnuncios.clear();
                for (DataSnapshot categorias: dataSnapshot.getChildren() ){
                    for(DataSnapshot anuncios: categorias.getChildren() ){

                        Anuncio anuncio = anuncios.getValue(Anuncio.class);
                        listaAnuncios.add( anuncio );
                    }
                }

                Collections.reverse( listaAnuncios );
                adapterAnuncios.notifyDataSetChanged();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void recuperarAnunciosPublicos(){

        listaAnuncios.clear();
        anunciosPublicosRefencia.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot cidades: dataSnapshot.getChildren()){
                    for (DataSnapshot categorias: cidades.getChildren() ){
                        for(DataSnapshot anuncios: categorias.getChildren() ){

                            Anuncio anuncio = anuncios.getValue(Anuncio.class);
                            listaAnuncios.add( anuncio );
                        }
                    }
                }

                Collections.reverse( listaAnuncios );
                adapterAnuncios.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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

    public void Componentes (){
        recyclerAnunciosPublicos = findViewById(R.id.recyclerAnunciosPublicos);
    }

}