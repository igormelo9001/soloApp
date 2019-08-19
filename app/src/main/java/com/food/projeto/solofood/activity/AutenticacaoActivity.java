package com.food.projeto.solofood.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.food.projeto.solofood.R;
import com.food.projeto.solofood.helper.ConfiguracaoFirebase;
import com.food.projeto.solofood.helper.UsuarioFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class AutenticacaoActivity extends AppCompatActivity {

    private Button botaoAcessar;
    private EditText campoEmail, campoSenha;
    private Switch tipoAcesso, tipoUsuario;
    private LinearLayout linearTipoUsuario;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autenticacao);

        inicializarComponentes();
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        //autenticacao.signOut();

        verificarUsuarioLogado();

        tipoAcesso.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
               if(isChecked){//empresa
                    linearTipoUsuario.setVisibility(View.VISIBLE);
               }else {//usuario
                    linearTipoUsuario.setVisibility(View.GONE);
               }
            }
        });

        botaoAcessar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = campoEmail.getText().toString();
                String senha = campoSenha.getText().toString();

                if(!email.isEmpty()){
                    if(!senha.isEmpty()){

                        if(tipoAcesso.isChecked()){

                            autenticacao.createUserWithEmailAndPassword(
                                    email, senha
                            ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if(task.isSuccessful()){
                                        Toast.makeText(AutenticacaoActivity.this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show();
                                        String tipoUsuario = getTipoUsuario();
                                        UsuarioFirebase.atualizarTipoUsuario(tipoUsuario);
                                        abrirTelaPrincipal(tipoUsuario);
                                    }else {

                                        String errorExcecao = "";
                                        try {
                                            throw  task.getException();
                                        } catch (FirebaseAuthWeakPasswordException e){
                                          errorExcecao = "Digite uma senha mais forte";
                                        } catch (FirebaseAuthInvalidCredentialsException e){
                                           errorExcecao = "Por favor, digite um e-mail válido";
                                        } catch (FirebaseAuthUserCollisionException e){
                                            errorExcecao = "Esta conta ja foi cadastrada";
                                        } catch (Exception e){
                                            errorExcecao = "ao cadastrar usuario: " + e.getMessage();
                                        }

                                        Toast.makeText(AutenticacaoActivity.this,
                                                "Erro: " + errorExcecao,
                                                    Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

                        }else {

                            autenticacao.signInWithEmailAndPassword(email, senha)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if(task.isSuccessful()){

                                                Toast.makeText(AutenticacaoActivity.this, "Logado com sucesso!",
                                                        Toast.LENGTH_SHORT).show();

                                                String tipoUsuario = task.getResult().getUser().getDisplayName();
                                                abrirTelaPrincipal(tipoUsuario);

                                            }else {
                                                Toast.makeText(AutenticacaoActivity.this, "Erro ao fazer login"
                                                        + task.getException(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                        }

                    }else {
                        Toast.makeText(AutenticacaoActivity.this, "Preencha a sennha", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(AutenticacaoActivity.this, "Preencha o E-mail", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void verificarUsuarioLogado(){
        FirebaseUser usuarioAtual = autenticacao.getCurrentUser();
        if( usuarioAtual != null){
            String tipoUsuario = usuarioAtual.getDisplayName();
            abrirTelaPrincipal(tipoUsuario);
        }
    }

    private String getTipoUsuario(){
        return tipoUsuario.isChecked() ? "E" : "U";
    }

    private void abrirTelaPrincipal(String tipoUsuario){
        if(tipoUsuario.equals("E")){//EMPRESA
            startActivity(new Intent(getApplicationContext(), EmpresaActivity.class));
        }else {//usuario
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }
    }

    private void inicializarComponentes(){
        campoEmail = findViewById(R.id.editCadastroEmail);
        campoSenha = findViewById(R.id.editCadastroSenha);
        botaoAcessar = findViewById(R.id.buttonAcesso);
        tipoAcesso = findViewById(R.id.switchAcesso);
        tipoUsuario = findViewById(R.id.switchTipoUsuario);
        linearTipoUsuario = findViewById(R.id.linearTipoUsuario);
    }

}
