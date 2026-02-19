package logica;
import java.util.ArrayList;
import java.util.List;

public class Horario {
    private List<Aula> aulas;

    public Horario() {
        this.aulas = new ArrayList<>();
    }

    public void adicionarAula(Aula aula) {
        this.aulas.add(aula);
    }

    public void removerAula(Aula aula) {
        this.aulas.remove(aula);
    }

    public List<Aula> getAulas() {
        return new ArrayList<>(aulas); // Retorna uma cópia para preservar encapsulamento
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Horario:\n");
        for (Aula aula : aulas) {
            sb.append(aula).append("\n");
        }
        return sb.toString();
    }
}
