package com.food.projeto.solofood.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.food.projeto.solofood.R;
import com.food.projeto.solofood.helper.ConfiguracaoFirebase;
import com.food.projeto.solofood.helper.UsuarioFirebase;
import com.food.projeto.solofood.model.Usuario;
import com.google.firebase.database.DatabaseReference;

public class ConfiguracoesUsuarioActivity extends AppCompatActivity {

    private EditText editUsuarioNome, editUsuarioEndereco;
    private String idUsuario;
    private DatabaseReference firebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes_usuario);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configurações usuário");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idUsuario = UsuarioFirebase.getIdUsuario();
        inicializarComponentes();

    }

    @SuppressLint("ShowToast")
    private void exibirMensagem(String texto){
        Toast.makeText(ConfiguracoesUsuarioActivity.this, texto, Toast.LENGTH_LONG).show();
    }

    public void validarDadosUsuario(View view){

        String nome = editUsuarioNome.getText().toString();
        String endereco = editUsuarioEndereco.getText().toString();

        if(!nome.isEmpty()){
            if(!endereco.isEmpty()){

                Usuario usuario = new Usuario();
                usuario.setIdUsuario(idUsuario);
                usuario.setNome(nome);
                usuario.setEndereco(endereco);
                usuario.salvar();

                exibirMensagem("Dados atualizados com sucesso");
                finish();

            }else {
                exibirMensagem("Digite seu endereço completo!");
            }
        }else {
            exibirMensagem("Digite seu nome");
        }

    }

    private void inicializarComponentes(){
        editUsuarioNome = findViewById(R.id.editUsuarioNome);
        editUsuarioEndereco = findViewById(R.id.editUsuarioEndereco);

    }
}
