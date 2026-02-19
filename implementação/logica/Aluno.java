package logica;
import java.util.ArrayList;
import java.util.List;

public class Aluno implements Utilizador {
    private String nome;
    private String numero;
    private String email;
    private String password;
    private Horario horario;
    private char genero;
    private int ano;
    private List<String> ucIds; // Lista de IDs das UCs associadas ao aluno

    public Aluno(String nome, String numero, String email, String password, char genero, int ano) {
        this.nome = nome;
        this.numero = numero;
        this.email = email;
        this.password = password;
        this.genero = genero;
        this.ano = ano;
        this.horario = new Horario(); // Inicializa o horário vazio
        this.ucIds = new ArrayList<>(); // Inicializa a lista de IDs de UCs
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
    public String getPassword() {
        return password;
    }

    @Override
    public boolean login(String numero, String password) {
        return this.numero.equals(numero) && this.password.equals(password);
    }

    @Override
    public boolean logout(String numero) {
        return this.numero.equals(numero);
    }

    // Métodos adicionais
    public Horario getHorario() {
        return horario;
    }

    public char getGenero() {
        return genero;
    }

    public int getAno() {
        return ano;
    }

    public List<String> getUcIds() {
        return new ArrayList<>(ucIds); // Retorna uma cópia para preservar o encapsulamento
    }

    public void adicionarUc(String ucId) {
        if (!ucIds.contains(ucId)) {
            this.ucIds.add(ucId);
        }
    }


    public void removerUc(String ucId) {
        this.ucIds.remove(ucId);
    }

    @Override
    public String toString() {
        return "Aluno{" +
                "nome='" + nome + '\'' +
                ", numero='" + numero + '\'' +
                ", email='" + email + '\'' +
                ", genero=" + genero +
                ", ano=" + ano +
                ", horario=" + horario +
                ", ucIds=" + ucIds +
                '}';
    }
    public void setPassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("A palavra-passe não pode ser vazia ou nula.");
        }
        this.password = password;
    }

}
