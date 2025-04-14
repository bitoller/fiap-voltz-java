package br.com.voltz.users;

import com.google.gson.annotations.JsonAdapter;

import java.time.LocalDateTime;

public class Users {
    private int id;
    private String nome;
    private String cpfCnpj;
    private String email;
    private String telefone;
    private String senha;
    private boolean ativo;

    @JsonAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime dataCriacao;

    @JsonAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime dataAtualizacao;

    public Users(String nome, String cpfCnpj, String email, String telefone, String senha, boolean ativo) {
        this.nome = nome;
        this.cpfCnpj = cpfCnpj;
        this.email = email;
        this.telefone = telefone;
        this.senha = senha;
        this.ativo = ativo;
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
    }

    // ✅ Construtor vazio (útil para frameworks ou libs)
    public Users() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    // ✅ Método para atualizar a data de atualização
    public void atualizarData() {
        this.dataAtualizacao = LocalDateTime.now();
    }

    // ✅ Método para verificar se o usuário está ativo
    public boolean isUsuarioAtivo() {
        return this.ativo;
    }

    // ✅ Método para formatar o CPF ou CNPJ
    public String formatarCpfCnpj() {
        if (this.cpfCnpj == null || this.cpfCnpj.isEmpty()) {
            return this.cpfCnpj; // Caso não tenha CPF/CNPJ
        }

        if (this.cpfCnpj.length() == 11) { // CPF
            return this.cpfCnpj.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
        } else if (this.cpfCnpj.length() == 14) { // CNPJ
            return this.cpfCnpj.replaceAll("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
        }
        return this.cpfCnpj; // Caso não seja nem CPF nem CNPJ
    }

    // ✅ Método para validar CPF (implementação básica)
    public boolean validarCpf() {
        if (this.cpfCnpj == null || this.cpfCnpj.length() != 11 || !this.cpfCnpj.matches("\\d+")) {
            return false;
        }
        // Implementação simplificada (não inclui dígito verificador)
        return true;
    }

    // ✅ Método para validar CNPJ (implementação básica)
    public boolean validarCnpj() {
        if (this.cpfCnpj == null || this.cpfCnpj.length() != 14 || !this.cpfCnpj.matches("\\d+")) {
            return false;
        }
        // Implementação simplificada (não inclui dígito verificador)
        return true;
    }

    // ✅ Método para validar o CPF ou CNPJ
    public boolean validarCpfCnpj() {
        if (this.cpfCnpj == null) {
            return false;
        }
        if (this.cpfCnpj.length() == 11) {
            return validarCpf();
        } else if (this.cpfCnpj.length() == 14) {
            return validarCnpj();
        }
        return false; // Caso não seja nem CPF nem CNPJ
    }
}