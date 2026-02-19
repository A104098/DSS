// Classe UC
package logica;
import java.util.ArrayList;
import java.util.List;

public class UC {
    private String codigoUC;
    private String nomeUC;
    private List<Aula> aulas;

    public UC(String codigoUC, String nomeUC) {
        this.codigoUC = codigoUC;
        this.nomeUC = nomeUC;
        this.aulas = new ArrayList<>();
    }

    public String getCodigoUC() {
        return codigoUC;
    }

    public String getNomeUC() {
        return nomeUC;
    }

    public List<Aula> getAulas() {
        return new ArrayList<>(aulas); // Retorna uma cópia para preservar o encapsulamento
    }

    public void adicionarAula(Aula aula) {
        this.aulas.add(aula);
    }

    public void removerAula(Aula aula) {
        this.aulas.remove(aula);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("UC{" + "codigoUC='" + codigoUC + '\'' + ", nomeUC='" + nomeUC + '\'' + ", aulas=[\n");
        for (Aula aula : aulas) {
            sb.append(aula.toString()).append("\n");
        }
        sb.append("]}");
        return sb.toString();
    }
}
