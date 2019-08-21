package com.food.projeto.solofood.activity;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.food.projeto.solofood.R;
import com.food.projeto.solofood.helper.ConfiguracaoFirebase;
import com.food.projeto.solofood.helper.UsuarioFirebase;
import com.food.projeto.solofood.model.Produto;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

public class NovoProdutoEmpresaActivity extends AppCompatActivity {

    private EditText editNomeProduto, editDescricao, editPreco;
    private String idUsuarioLogado;
    private ImageView imageProduto;
    private static final int SELECAO_GALERIA = 300;
    private String urlImagemSelecionada = "";
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_produto_empresa);

        storageReference = ConfiguracaoFirebase.getFirebaseStorage();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Novo produto");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        inicializarComponentes();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        imageProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if(i.resolveActivity(getPackageManager())!= null){
                    startActivityForResult(i, SELECAO_GALERIA);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            Bitmap imagem = null;
            try{

                switch (requestCode){
                    case SELECAO_GALERIA:
                        Uri localImagem = data.getData();
                        imagem = MediaStore.Images
                                .Media
                                .getBitmap(
                                        getContentResolver(),
                                        localImagem
                                );
                        break;
                }

                if(imagem != null){
                    imageProduto.setImageBitmap(imagem);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    final byte[] dadosImagem = baos.toByteArray();

                     StorageReference imagemRef = storageReference
                            .child("imagens")
                            .child("produtos")
                            .child(idUsuarioLogado + "jpeg");

                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(NovoProdutoEmpresaActivity.this,
                                    "Erro ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            //urlImagemSelecionada = taskSnapshot.getStorage().getDownloadUrl().toString();

                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while(!uriTask.isSuccessful());
                            Uri url = uriTask.getResult();
                            urlImagemSelecionada = url.toString();
                            Toast.makeText(NovoProdutoEmpresaActivity.this,
                                    "Sucesso ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    });

                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void validarDadosProduto(View view){

        String nome = editNomeProduto.getText().toString();
        String descricao = editDescricao.getText().toString();
        String preco = editPreco.getText().toString();

        String precoFormat = preco.replace("," , ".");

        if(!nome.isEmpty()){
            if(!descricao.isEmpty()){
                if(!preco.isEmpty()){

                    Produto produto = new Produto();
                    produto.setIdUsuario(idUsuarioLogado);
                    produto.setNome(nome);
                    produto.setDescricao(descricao);
                    produto.setPreco(Double.parseDouble(precoFormat));
                    produto.setUrlImagem(urlImagemSelecionada);
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
        imageProduto = findViewById(R.id.imageProduto);
    }
}
