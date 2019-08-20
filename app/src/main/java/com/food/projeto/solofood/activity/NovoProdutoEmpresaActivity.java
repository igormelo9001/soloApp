package com.food.projeto.solofood.activity;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.food.projeto.solofood.R;

public class NovoProdutoEmpresaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_produto_empresa);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Novo produto");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
