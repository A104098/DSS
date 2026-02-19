// Classe AlunoComEstatutoEspecial
package logica;

public class AlunoComEstatutoEspecial extends Aluno {
    private String motivoEstatuto; // Motivo pelo qual o aluno possui estatuto especial

    public AlunoComEstatutoEspecial(String nome, String numero, String email, String password, char genero, int ano, String motivoEstatuto) {
        super(nome, numero, email, password, genero, ano); // Chamando o construtor de Aluno com os novos parâmetros

        this.motivoEstatuto = motivoEstatuto; // Inicializando o atributo específico
    }

    public String getMotivoEstatuto() {
        return motivoEstatuto;
    }

    @Override
    public String toString() {
        return super.toString() + ", Estatuto: " + motivoEstatuto;
    }
}
