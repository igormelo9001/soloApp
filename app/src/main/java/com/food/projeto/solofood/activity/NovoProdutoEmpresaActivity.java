package com.food.projeto.solofood.activity;


import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.food.projeto.solofood.R;
import com.food.projeto.solofood.helper.UsuarioFirebase;
import com.food.projeto.solofood.model.Produto;

public class NovoProdutoEmpresaActivity extends AppCompatActivity {

    private EditText editNomeProduto, editDescricao, editPreco;
    private String idUsuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_produto_empresa);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Novo produto");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        inicializarComponentes();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();
    }

    public void validarDadosProduto(View view){

        String nome = editNomeProduto.getText().toString();
        String descricao = editDescricao.getText().toString();
        String preco = editPreco.getText().toString();

        if(!nome.isEmpty()){
            if(!descricao.isEmpty()){
                if(!preco.isEmpty()){

                    Produto produto = new Produto();
                    produto.setIdUsuario(idUsuarioLogado);
                    produto.setNome(nome);
                    produto.setDescricao(descricao);
                    produto.setPreco(Double.parseDouble(preco));
                    produto.salvar();

                    finish();
                    exibirMensagem("Produto salvo com sucesso!");

                }else {
                    exibirMensagem("digite um preço para o produto");
                }

            }else {
                exibirMensagem("Digite uma descrição para o produto");
            }
        }else {
            exibirMensagem("Digite o nome do produto");
        }

    }

    private void exibirMensagem(String texto){

        Toast.makeText(this, texto, Toast.LENGTH_LONG).show();

    }

    private void inicializarComponentes(){

        editNomeProduto = findViewById(R.id.editNomeProduto);
        editDescricao = findViewById(R.id.editDescricao);
        editPreco = findViewById(R.id.editPreco);

    }
}
