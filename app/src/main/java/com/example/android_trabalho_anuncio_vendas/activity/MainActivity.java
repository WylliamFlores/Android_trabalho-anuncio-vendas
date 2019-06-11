package com.example.android_trabalho_anuncio_vendas.activity;

import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android_trabalho_anuncio_vendas.R;
import com.example.android_trabalho_anuncio_vendas.helper.ConfiguracaoFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;


public class MainActivity extends AppCompatActivity {

    private Button botaoEntrar;
    private Button botaoCadastrar;
    private EditText cadastroEmail, cadastroSenha;
    private FirebaseAuth autenticacao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        componentesLogin();
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        botaoCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = cadastroEmail.getText().toString();
                String senha = cadastroSenha.getText().toString();

                if (!email.isEmpty() ){
                    if (!senha.isEmpty() ){
                        autenticacao.createUserWithEmailAndPassword(email, senha)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(MainActivity.this,"Cadastro realizado com sucesso =)", Toast.LENGTH_SHORT).show();
                                }else{
                                    String erroExcecao ="";

                                    try{
                                        throw task.getException();
                                    }catch (FirebaseAuthWeakPasswordException e){
                                        erroExcecao = "Digite uma senha mais forte!";
                                    }catch (FirebaseAuthInvalidCredentialsException e){
                                        erroExcecao = "Digite um e-mail válido";
                                    }catch (FirebaseAuthUserCollisionException e){
                                        erroExcecao = "Esta conta já existe";
                                    } catch (Exception e) {
                                        erroExcecao = " ao cadastrar usuário: "  + e.getMessage();
                                        e.printStackTrace();
                                    }
                                    Toast.makeText(MainActivity.this, "Erro: " + erroExcecao, Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                    }else {
                        Toast.makeText(MainActivity.this, "Preencha o campo Senha!", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(MainActivity.this, "Preencha o campo E-mail!", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private  void componentesLogin (){
        botaoEntrar = findViewById(R.id.botaoEntrar);
        botaoCadastrar = findViewById(R.id.botaoCadastrar);
        cadastroEmail = findViewById(R.id.cadastroEmail);
        cadastroSenha = findViewById(R.id.cadastroSenha);
    }
}
