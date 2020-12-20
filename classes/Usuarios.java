package com.example.teste4vets.classes;

import com.example.teste4vets.configuracoes.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

public class Usuarios {

    private String nome;
    private String telefone;
    private String cep;
    private String cidade;
    private String bairro;
    private String rua;
    private String numero;
    private String foto;
    private String idUsuario;

    public Usuarios(){
            DatabaseReference usuarioRef = ConfiguracaoFirebase.getFirebase()
                    .child("usuarios");
            setIdUsuario( usuarioRef.push().getKey() );
    }

    public void salvar(){

        String idUsuario = ConfiguracaoFirebase.getIdUsuario();
        DatabaseReference usuarioRef = ConfiguracaoFirebase.getFirebase()
                .child("usuarios");
        usuarioRef.child(idUsuario)
                .setValue(this);
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getIdUsuario(){
        return idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }
}
