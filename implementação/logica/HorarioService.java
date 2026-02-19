package logica;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class HorarioService {
    private List<Aula> aulas;

    public HorarioService() {
        this.aulas = new ArrayList<>();
    }

    public boolean adicionarAulaParaAluno(Aula aula, Horario horario) {
        if (!verificarDisponibilidade(aula.getHorarioInicial(), aula.getHorarioFinal())) {
            System.out.println("Conflito detectado. Aula não pode ser adicionada.");
            return false;
        }
        horario.adicionarAula(aula);
        System.out.println("Aula adicionada com sucesso.");
        return true;
    }


    public boolean verificarDisponibilidade(LocalTime inicio, LocalTime fim) {
        for (Aula aula : aulas) {
            if (aula.getHorarioInicial().isBefore(fim) && aula.getHorarioFinal().isAfter(inicio)) {
                return false; // Existe conflito
            }
        }
        return true; // Nenhum conflito
    }

    public List<Aula> listarAulas() {
        return new ArrayList<>(aulas); // Retorna uma cópia para preservar o encapsulamento
    }

    public boolean removerAula(Aula aula) {
        if (aulas.remove(aula)) {
            System.out.println("Aula removida com sucesso.");
            return true;
        }
        System.out.println("Aula não encontrada.");
        return false;
    }

    public void listarTurnos() {
        System.out.println("--- Lista de Aulas e Turnos ---");
        for (Aula aula : aulas) {
            System.out.println(aula);
        }
    }
}
