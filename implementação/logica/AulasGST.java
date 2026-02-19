// Classe AulasGST atualizada
package logica;

import java.util.ArrayList;
import java.util.List;

public class AulasGST {
    private List<Aula> aulas;

    public AulasGST() {
        this.aulas = new ArrayList<>();
    }

    public void adicionarAula(Aula aula) {
        aulas.add(aula);
    }

    public List<Aula> getAulas() {
        return new ArrayList<>(aulas); // Retorna uma cópia para preservar o encapsulamento
    }

    public List<Aula> listarAulasPorUC(String codigoUC) {
        List<Aula> aulasUC = new ArrayList<>();
        for (Aula aula : aulas) {
            if (aula.getCodigoUC().equals(codigoUC)) {
                aulasUC.add(aula);
            }
        }
        return aulasUC;
    }

    public List<Aula> listarAulasPorDia(String diaSemana) {
        List<Aula> aulasPorDia = new ArrayList<>();
        for (Aula aula : aulas) {
            if (aula.getDiaSemana().equalsIgnoreCase(diaSemana)) {
                aulasPorDia.add(aula);
            }
        }
        return aulasPorDia;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Aulas:");
        for (Aula aula : aulas) {
            sb.append("\n").append(aula);
        }
        return sb.toString();
    }
}
