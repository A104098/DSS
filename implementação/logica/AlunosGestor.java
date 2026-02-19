package logica;

import java.util.HashMap;
import java.util.Map;
import data.AlunoDAO;
import data.UCDAO;
import data.UtilizadorDAO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AlunosGestor {

    private Map<String, Aluno> alunos; // Mapa para armazenar os alunos pelo número

    public AlunosGestor() {
        this.alunos = new HashMap<>();
    }

    public void carregarAlunosDeCSV(String caminhoCSV, AlunoDAO alunoDAO, UCDAO ucDAO, Map<String, Utilizador> usuarios, UtilizadorDAO users_dao) {
        try {
            // Passando as DAOs para o método parseCSV
            List<Aluno> listaAlunos = CSVParser.parseCSV(caminhoCSV, alunoDAO, ucDAO);

            for (Aluno aluno : listaAlunos) {
                try {
                    Aluno alunoExistente = alunoDAO.findById(aluno.getNumero());
                    if (alunoExistente == null) {
                        // Definir senha padrão para o aluno
                        aluno.setPassword("passaluno");

                        // Inserir o aluno no banco de dados
                        alunoDAO.insert(aluno);
                    } else {
                        // Atualizar as UCs do aluno existente
                        for (String ucId : aluno.getUcIds()) {
                            if (!alunoExistente.getUcIds().contains(ucId)) {
                                alunoExistente.adicionarUc(ucId);
                            }
                        }
                        alunoDAO.update(alunoExistente);
                    }

                    // Adicionar aluno à lista de utilizadores
                    usuarios.put(aluno.getNumero(), aluno);
                    users_dao.update(aluno.getNumero(),aluno.getPassword());
                } catch (SQLException e) {
                    System.out.println("Erro ao processar aluno " + aluno.getNumero() + ": " + e.getMessage());
                }
            }

            System.out.println("Alunos carregados no mapa e sincronizados com o banco de dados.");
        } catch (IOException e) {
            System.err.println("Erro ao carregar alunos do CSV: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Erro ao acessar o banco de dados ao carregar alunos do CSV: " + e.getMessage());
        }
    }


    // Método para adicionar um aluno manualmente
    public void adicionarAluno(Aluno aluno) {
        this.alunos.put(aluno.getNumero(), aluno);
        System.out.println("Aluno adicionado ao mapa: " + aluno);
    }

    // Método para acessar um aluno pelo número
    public Aluno obterAlunoPorNumero(String numero) {
        return this.alunos.get(numero);
    }

    // Método para remover um aluno pelo número
    public void removerAlunoPorNumero(String numero) {
        Aluno removido = this.alunos.remove(numero);
        if (removido != null) {
            System.out.println("Aluno removido: " + removido);
        } else {
            System.out.println("Aluno com número " + numero + " não encontrado.");
        }
    }

    // Método para listar todos os alunos
    public void listarAlunos() {
        if (this.alunos.isEmpty()) {
            System.out.println("Nenhum aluno cadastrado no sistema.");
        } else {
            System.out.println("--- Lista de Alunos ---");
            for (Aluno aluno : this.alunos.values()) {
                System.out.println(aluno);
            }
        }
    }

    // Getter para o mapa (se necessário)
    public Map<String, Aluno> getAlunos() {
        return new HashMap<>(this.alunos); // Retorna uma cópia para preservar o encapsulamento
    }



}
