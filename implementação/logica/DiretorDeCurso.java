// Classe DiretorDeCurso
package logica;
public class DiretorDeCurso implements Utilizador {
    private String nome;
    private String numero;
    private String email;
    private String password;

    public DiretorDeCurso(String nome, String numero, String email, String password) {
        this.nome = nome;
        this.numero = numero;
        this.email = email;
        this.password = password;
    }

    // Métodos da interface Utilizador
    @Override
    public String getNome() {
        return nome;
    }

    @Override
    public String getNumero() {
        return numero;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getPassword() { return password;}

    @Override
    public boolean login(String numero, String password) {
        return this.numero.equals(numero) && this.password.equals(password);
    }

    @Override
    public boolean logout(String numero) {
        return this.numero.equals(numero);
    }

    @Override
    public String toString() {
        return "DiretorDeCurso{" +
                "nome='" + nome + '\'' +
                ", numero='" + numero + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
