package logica;
public class Turno {
    private int id; // Identificador único do turno
    private String tipo; // T, TP ou PL
    private int capacidade; // Capacidade do turno
    private String sala; // Número da sala no formato X.Y


    // Construtor adicional para uso no banco de dados
    public Turno(int id, String tipo, int capacidade, String sala) {
        if (!tipo.equals("T") && !tipo.equals("TP") && !tipo.equals("PL")) {
            throw new IllegalArgumentException("Tipo de turno inválido. Deve ser 'T', 'TP' ou 'PL'.");
        }
        if (!sala.matches("\\d+\\.\\d{1,2}")) { // Regex para validar formato X.Y
            throw new IllegalArgumentException("Número da sala inválido. Deve estar no formato X.Y, como 2.2 ou 0.05.");
        }
        this.id = id; // Inicializa o ID como 0
        this.tipo = tipo;
        this.capacidade = capacidade;
        this.sala = sala;
    }

    public Turno(String tipo, int capacidade, String sala) {
        this(0,tipo,capacidade,sala);
    }




    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public int getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(int capacidade) {
        this.capacidade = capacidade;
    }

    public String getSala() {
        return sala;
    }

    public void setSala(String sala) {
        if (!sala.matches("\\d+\\.\\d{1,2}")) {
            throw new IllegalArgumentException("Número da sala inválido. Deve estar no formato X.Y, como 2.2 ou 0.05.");
        }
        this.sala = sala;
    }


    @Override
    public String toString() {
        return "Turno{" +
                "id=" + id +
                ", tipo='" + tipo + '\'' +
                ", capacidade=" + capacidade +
                ", sala='" + sala + '\'' +
                '}';
    }
}

