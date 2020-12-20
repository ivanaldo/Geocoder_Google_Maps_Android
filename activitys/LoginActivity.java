package com.example.teste4vets.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.teste4vets.R;
import com.example.teste4vets.configuracoes.ConfiguracaoFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {


    private Button botaoAcessar;
    private EditText campoEmail, campoSenha;
    private Switch tipoAcesso;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //inicializa os componentes
        inicializaComponentes();

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        botaoAcessar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = campoEmail.getText().toString();
                String senha = campoSenha.getText().toString();

                if( !email.isEmpty() ){
                    if( !senha.isEmpty() ){

                        //Verifica estado do switch
                        if( tipoAcesso.isChecked() ){//Cadastro

                                        autenticacao.createUserWithEmailAndPassword(
                                                email, senha
                                        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {

                                                    Toast.makeText(LoginActivity.this,
                                                            "Cadastro realizado com sucesso!",
                                                            Toast.LENGTH_SHORT).show();

                                                    //Direcionar para a tela principal do App

                                                } else {

                                                    String erroExcecao = "";

                                                    try {
                                                        throw task.getException();
                                                    } catch (FirebaseAuthWeakPasswordException e) {
                                                        erroExcecao = "Digite uma senha mais forte!";
                                                    } catch (FirebaseAuthInvalidCredentialsException e) {
                                                        erroExcecao = "Por favor, digite um e-mail válido";
                                                    } catch (FirebaseAuthUserCollisionException e) {
                                                        erroExcecao = "Este conta já foi cadastrada";
                                                    } catch (Exception e) {
                                                        erroExcecao = "ao cadastrar usuário, verifique sua conexão com a internet ou tente mais tarde!";
                                                    }

                                                    Toast.makeText(LoginActivity.this,
                                                            "Erro: " + erroExcecao,
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                        }else {//Login

                            autenticacao.signInWithEmailAndPassword(
                                    email, senha
                            ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if( task.isSuccessful() ){

                                        Toast.makeText(LoginActivity.this,
                                                "Logado com sucesso",
                                                Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(), MapaActivity.class));

                                    }else {
                                        Toast.makeText(LoginActivity.this,
                                                "Erro ao fazer login, verifique seus dados ou sua conexão com a internet ou tente mais tarde!",
                                                Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

                        }

                    }else {
                        Toast.makeText(LoginActivity.this,
                                "Preencha a senha!",
                                Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(LoginActivity.this,
                            "Preencha o E-mail!",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private boolean usuarioLogado(){
        FirebaseUser logado = autenticacao.getCurrentUser();
        if (logado == null){
            return false;
        }else
        {
            return true;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (usuarioLogado()){
            logar();
        }
    }

    private void logar(){
        startActivity(new Intent(getApplicationContext(), MapaActivity.class));
        finish();
    }

    private void inicializaComponentes(){
        campoEmail = findViewById(R.id.editCadastroEmail);
        campoSenha = findViewById(R.id.editCadastroSenha);
        botaoAcessar = findViewById(R.id.buttonAcesso);
        tipoAcesso = findViewById(R.id.switchAcesso);
    }

}
