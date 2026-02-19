// Classe AlunoDAO atualizada
package data;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import logica.*;


public class AlunoDAO implements IGenericDAO<Aluno> {
    private Connection connection;

    public AlunoDAO() {
        UCDAO ucDAO = new UCDAO(); // Certifique-se de que a tabela 'ucs' é criada primeiro
        this.connection = DatabaseConnection.getConnection();
        criarTabelas();
    }

        private void criarTabelas() {
            try (Statement stmt = connection.createStatement()) {
                // Tabela de alunos
                String sqlAlunos = "CREATE TABLE IF NOT EXISTS alunos ("
                        + "numero VARCHAR(50) PRIMARY KEY, "
                        + "nome VARCHAR(100) NOT NULL, "
                        + "email VARCHAR(100) NOT NULL, "
                        + "password VARCHAR(100) NOT NULL, "
                        + "genero CHAR(1) NOT NULL, "
                        + "ano INT NOT NULL, "
                        + "regimeEspecial VARCHAR(45)"
                        + ")";
                stmt.execute(sqlAlunos);

                // Tabela associativa entre alunos e UCs
                String sqlAlunoUCs = "CREATE TABLE IF NOT EXISTS aluno_ucs ("
                        + "numeroAluno VARCHAR(50) NOT NULL, "
                        + "codigoUC VARCHAR(50) NOT NULL, "
                        + "PRIMARY KEY (numeroAluno, codigoUC), "
                        + "FOREIGN KEY (numeroAluno) REFERENCES alunos(numero), "
                        + "FOREIGN KEY (codigoUC) REFERENCES ucs(codigoUC)"
                        + ")";
                stmt.execute(sqlAlunoUCs);
            } catch (SQLException e) {
                throw new RuntimeException("Erro ao criar tabelas de Alunos e UCs", e);
            }
        }

    @Override
    public void insert(Aluno aluno) throws SQLException {
        String sql = "INSERT INTO alunos (numero, nome, email, password, genero, ano, regimeEspecial) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String sqlUtilizador = "INSERT INTO utilizadores (id, password, tipo) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
            PreparedStatement stmtUtilizador = connection.prepareStatement(sqlUtilizador)){
            stmt.setString(1, aluno.getNumero());
            stmt.setString(2, aluno.getNome());
            stmt.setString(3, aluno.getEmail());
            stmt.setString(4, aluno.getPassword());
            stmt.setString(5, String.valueOf(aluno.getGenero()));
            stmt.setInt(6, aluno.getAno());

            if (aluno instanceof AlunoComEstatutoEspecial) {
                stmt.setString(7, ((AlunoComEstatutoEspecial) aluno).getMotivoEstatuto());
            } else {
                stmt.setString(7, null);
            }

            stmt.executeUpdate();
            stmtUtilizador.setString(1, aluno.getNumero());
            stmtUtilizador.setString(2, aluno.getPassword());
            stmtUtilizador.setString(3, "A");
            stmtUtilizador.executeUpdate();
        }

        // Criar um horário vazio para o aluno
        HorarioDAO horarioDAO = new HorarioDAO();
        horarioDAO.criarHorario(aluno.getNumero());

        // Inserir relações com as UCs
        for (String ucId : aluno.getUcIds()) {
            String sqlAlunoUC = "INSERT INTO aluno_ucs (numeroAluno, codigoUC) VALUES (?, ?)";
            try (PreparedStatement stmtUC = connection.prepareStatement(sqlAlunoUC)) {
                stmtUC.setString(1, aluno.getNumero());
                stmtUC.setString(2, ucId);
                stmtUC.executeUpdate();
            }
        }

    }


    @Override
        public void update(Aluno aluno) throws SQLException {
            String sql = "UPDATE alunos SET nome = ?, email = ?, password = ?, genero = ?, ano = ?, regimeEspecial = ? WHERE numero = ?";
            UtilizadorDAO utilizadorDAO = new UtilizadorDAO();

             try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, aluno.getNome());
                stmt.setString(2, aluno.getEmail());
                stmt.setString(3, aluno.getPassword());
                stmt.setString(4, String.valueOf(aluno.getGenero()));
                stmt.setInt(5, aluno.getAno());

                if (aluno instanceof AlunoComEstatutoEspecial) {
                    stmt.setString(6, ((AlunoComEstatutoEspecial) aluno).getMotivoEstatuto());
                } else {
                    stmt.setString(6, null);
                }

                stmt.setString(7, aluno.getNumero());
                stmt.executeUpdate();
            }

            // Atualizar relações com as UCs (remover todas e reinserir)
            String deleteSql = "DELETE FROM aluno_ucs WHERE numeroAluno = ?";
            try (PreparedStatement deleteStmt = connection.prepareStatement(deleteSql)) {
                deleteStmt.setString(1, aluno.getNumero());
                deleteStmt.executeUpdate();
            }

            for (String ucId : aluno.getUcIds()) {
                String sqlAlunoUC = "INSERT INTO aluno_ucs (numeroAluno, codigoUC) VALUES (?, ?)";
                try (PreparedStatement stmtUC = connection.prepareStatement(sqlAlunoUC)) {
                    stmtUC.setString(1, aluno.getNumero());
                    stmtUC.setString(2, ucId);
                    stmtUC.executeUpdate();
                }
            }
        utilizadorDAO.update(aluno.getNumero(), aluno.getPassword());

    }


        @Override
        public void delete(String numero) throws SQLException {
            String deleteAlunoUCs = "DELETE FROM aluno_ucs WHERE numeroAluno = ?";
            UtilizadorDAO utilizadorDAO = new UtilizadorDAO();

            try (PreparedStatement stmt = connection.prepareStatement(deleteAlunoUCs)) {
                stmt.setString(1, numero);
                stmt.executeUpdate();
                utilizadorDAO.delete(numero);

            }

            String deleteAluno = "DELETE FROM alunos WHERE numero = ?";
            try (PreparedStatement stmt = connection.prepareStatement(deleteAluno)) {
                stmt.setString(1, numero);
                stmt.executeUpdate();
                utilizadorDAO.delete(numero);

            }
        }

        @Override
        public Aluno findById(String numero) throws SQLException {
            String sql = "SELECT * FROM alunos WHERE numero = ?";

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, numero);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Aluno aluno;
                        if (rs.getString("regimeEspecial") != null) {
                            aluno = new AlunoComEstatutoEspecial(
                                    rs.getString("nome"),
                                    rs.getString("numero"),
                                    rs.getString("email"),
                                    rs.getString("password"),
                                    rs.getString("genero").charAt(0),
                                    rs.getInt("ano"),
                                    rs.getString("regimeEspecial")
                            );
                        } else {
                            aluno = new Aluno(
                                    rs.getString("nome"),
                                    rs.getString("numero"),
                                    rs.getString("email"),
                                    rs.getString("password"),
                                    rs.getString("genero").charAt(0),
                                    rs.getInt("ano")
                            );
                        }

                        // Carregar UCs associadas
                        String sqlAlunoUCs = "SELECT codigoUC FROM aluno_ucs WHERE numeroAluno = ?";
                        try (PreparedStatement stmtUC = connection.prepareStatement(sqlAlunoUCs)) {
                            stmtUC.setString(1, numero);
                            try (ResultSet rsUC = stmtUC.executeQuery()) {
                                while (rsUC.next()) {
                                    aluno.adicionarUc(rsUC.getString("codigoUC"));
                                }
                            }
                        }

                        return aluno;
                    }
                }
            }

            return null;
        }

        @Override
        public List<Aluno> findAll() throws SQLException {
            List<Aluno> alunos = new ArrayList<>();
            String sql = "SELECT * FROM alunos";

            try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    Aluno aluno;
                    if (rs.getString("regimeEspecial") != null) {
                        aluno = new AlunoComEstatutoEspecial(
                                rs.getString("nome"),
                                rs.getString("numero"),
                                rs.getString("email"),
                                rs.getString("password"),
                                rs.getString("genero").charAt(0),
                                rs.getInt("ano"),
                                rs.getString("regimeEspecial")
                        );
                    } else {
                        aluno = new Aluno(
                                rs.getString("nome"),
                                rs.getString("numero"),
                                rs.getString("email"),
                                rs.getString("password"),
                                rs.getString("genero").charAt(0),
                                rs.getInt("ano")
                        );
                    }

                    // Carregar UCs associadas
                    String sqlAlunoUCs = "SELECT codigoUC FROM aluno_ucs WHERE numeroAluno = ?";
                    try (PreparedStatement stmtUC = connection.prepareStatement(sqlAlunoUCs)) {
                        stmtUC.setString(1, aluno.getNumero());
                        try (ResultSet rsUC = stmtUC.executeQuery()) {
                            while (rsUC.next()) {
                                aluno.adicionarUc(rsUC.getString("codigoUC"));
                            }
                        }
                    }

                    alunos.add(aluno);
                }
            }

            return alunos;
        }

    public List<Aluno> findAlunosComHorarioVazio() throws SQLException {
        List<Aluno> alunosComHorarioVazio = new ArrayList<>();
        String sql = "SELECT * FROM alunos WHERE NOT EXISTS (" +
                "SELECT * FROM aluno_ucs WHERE aluno_ucs.numeroAluno = alunos.numero)";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Aluno aluno = new Aluno(
                        rs.getString("nome"),
                        rs.getString("numero"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("genero").charAt(0),
                        rs.getInt("ano")
                );
                alunosComHorarioVazio.add(aluno);
            }
        }

        return alunosComHorarioVazio;
    }

}
