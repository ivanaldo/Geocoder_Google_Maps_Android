package com.example.teste4vets.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.teste4vets.R;
import com.example.teste4vets.classes.Usuarios;
import com.example.teste4vets.configuracoes.ConfiguracaoFirebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class CadastroActivity extends AppCompatActivity {

    private Button cadastrarPerfil;
    private EditText nome, telefone, cep, rua, bairro, cidade, numero;
    private ImageView foto;
    private Usuarios usuarios;
    private String idUsuario;
    private DatabaseReference perfil;
    private String fotos;
    private ImageView imagem;
    private StorageReference storage;
    private ProgressBar progressBar;

    Usuarios usuarioId = new Usuarios();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        //inicializa os componentes
        inicializarComponentes();
        idUsuario = ConfiguracaoFirebase.getIdUsuario();
        perfil = ConfiguracaoFirebase.getFirebase();
        storage = ConfiguracaoFirebase.getFirebaseStorage();
        recuperarDados();

        //esconde progressbar
        progressBar.setVisibility(View.GONE);

        cadastrarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nomes = nome.getText().toString();
                String telefones = telefone.getText().toString();
                String ceps = cep.getText().toString();
                String ruas = rua.getText().toString();
                String numeros = numero.getText().toString();
                String bairros = bairro.getText().toString();
                String cidades = cidade.getText().toString();

                if (fotos != null) {
                    if (!nomes.isEmpty()) {
                        if (!telefones.isEmpty()) {
                            if (!ruas.isEmpty()) {
                                if (!numeros.isEmpty()) {
                                    if (!bairros.isEmpty()) {
                                        if (!ceps.isEmpty()) {
                                            if (!cidades.isEmpty()) {

                                                Usuarios dados = new Usuarios();
                                                dados.setNome(nomes);
                                                dados.setTelefone(telefones);
                                                dados.setRua(ruas);
                                                dados.setBairro(bairros);
                                                dados.setNumero(numeros);
                                                dados.setCidade(cidades);
                                                dados.setCep(ceps);

                                                usuarios = dados;

                                                String urlAudio = fotos;
                                                salvarFoto(urlAudio);

                                                progressBar.setVisibility(View.VISIBLE);

                                            }else{
                                                Toast.makeText(CadastroActivity.this, "Preencha o campo Cidade!", Toast.LENGTH_SHORT).show();
                                            }
                                        }else{
                                            Toast.makeText(CadastroActivity.this, "Preencha o campo Cep!", Toast.LENGTH_SHORT).show();
                                        }
                                    }else{
                                        Toast.makeText(CadastroActivity.this, "Preencha o campo Bairro!", Toast.LENGTH_SHORT).show();
                                    }
                                }else{
                                    Toast.makeText(CadastroActivity.this, "Preencha o campo Numero!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(CadastroActivity.this, "Preencha o campo Rua!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(CadastroActivity.this, "Preencha o campo Telefone!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(CadastroActivity.this, "Preencha o campo Nome!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(CadastroActivity.this, "Coloque uma Foto!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void recuperarDados(){
        DatabaseReference dadosRef = perfil
                .child("usuarios")
                .child(idUsuario);
        dadosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null){
                    Usuarios  usuario = snapshot.getValue(Usuarios.class);
                    nome.setText(usuario.getNome());
                    telefone.setText(usuario.getTelefone());
                    cep.setText(usuario.getCep());
                    bairro.setText(usuario.getBairro());
                    rua.setText(usuario.getRua());
                    numero.setText(usuario.getNumero());
                    cidade.setText(usuario.getCidade());

                    foto.setImageURI(Uri.parse(usuario.getFoto()));
                    Glide.with(getApplicationContext()).load(usuario.getFoto()).into(foto);

                    imagem = foto;
                    //esconde progressbar
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void salvarFoto(String urlString){

        //Criar nó no storage
        final StorageReference imagemAnuncio = storage.child("imagens")
                .child("foto")
                .child( idUsuario )
                .child("imagem");

        //Fazer upload da foto
        UploadTask uploadTask = imagemAnuncio.putFile( Uri.parse(urlString) );
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                imagemAnuncio.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri downloadUrli) {

                        String urlConvertida = downloadUrli.toString();
                        usuarios.setFoto( urlConvertida );
                        usuarios.salvar();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CadastroActivity.this, "Falha ao fazer upload de foto", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    public void clickFoto(View view){
        Log.d("onClick", "onClick: " + view.getId() );
        switch ( view.getId() ){
            case R.id.fotoPerfil :
                Log.d("onClick", "onClick: " );
                escolherImagem(1);
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

            //Recuperar imagem
            Uri imagemSelecionada = data.getData();
            String caminhoImagem = imagemSelecionada.toString();

            //Configura imagem no ImageView
            if( requestCode == 1 ){
                foto.setImageURI( imagemSelecionada );

            }

            fotos = caminhoImagem;

        }

    }

    public void inicializarComponentes(){
        cadastrarPerfil = findViewById(R.id.botaoPerfil);
        nome = findViewById(R.id.nomeCadastro);
        telefone = findViewById(R.id.telefoneCadastro);
        cep = findViewById(R.id.cepCadastro);
        rua = findViewById(R.id.ruaCadastro);
        numero = findViewById(R.id.numeroCadastro);
        bairro = findViewById(R.id.bairroCadastro);
        cidade = findViewById(R.id.cidadeCadastro);
        foto =findViewById(R.id.fotoPerfil);
        progressBar=findViewById(R.id.progressBarPerfil);

    }
}