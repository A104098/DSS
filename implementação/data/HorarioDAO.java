// Classe HorarioDAO
package data;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import logica.*;

public class HorarioDAO {
    private Connection connection;

    public HorarioDAO() {
        this.connection = DatabaseConnection.getConnection();
        criarTabelas();
    }

    private void criarTabelas() {
        try {
            // Garantir que a tabela de aulas seja criada primeiro
            AulaDAO aulaDAO = new AulaDAO();
            aulaDAO.criarTabela();

            try (Statement stmt = connection.createStatement()) {
                // Tabela de horários
                String sqlHorarios = "CREATE TABLE IF NOT EXISTS horarios ("
                        + "idHorario INT AUTO_INCREMENT PRIMARY KEY, "
                        + "numeroAluno VARCHAR(50) NOT NULL, "
                        + "FOREIGN KEY (numeroAluno) REFERENCES alunos(numero)"
                        + ")";
                stmt.execute(sqlHorarios);

                // Tabela associativa entre horários e aulas
                String sqlHorarioAulas = "CREATE TABLE IF NOT EXISTS horario_aulas ("
                        + "idHorario INT NOT NULL, "
                        + "idAula INT NOT NULL, "
                        + "PRIMARY KEY (idHorario, idAula), "
                        + "FOREIGN KEY (idHorario) REFERENCES horarios(idHorario), "
                        + "FOREIGN KEY (idAula) REFERENCES aulas(id)"
                        + ")";
                stmt.execute(sqlHorarioAulas);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar tabelas de horários", e);
        }
    }


    public int criarHorario(String numeroAluno) throws SQLException {
        String sql = "INSERT INTO horarios (numeroAluno) VALUES (?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, numeroAluno);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1); // Retorna o ID do horário recém-criado
                }
            }
        }
        throw new SQLException("Erro ao criar horário para o aluno " + numeroAluno);
    }
    public int buscarIdHorarioPorAluno(String numeroAluno) throws SQLException {
        String sql = "SELECT idHorario FROM horarios WHERE numeroAluno = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, numeroAluno);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("idHorario");
                }
            }
        }
        return -1; // Indica que o horário não existe
    }

    public void adicionarAulaAoHorario(String numeroAluno, int idAula) throws SQLException {
        String sql = "INSERT INTO horarios (numeroAluno, idAula) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, numeroAluno);
            stmt.setInt(2, idAula);
            stmt.executeUpdate();
        }
    }

    public List<String> buscarHorarioDoAluno(String numeroAluno) throws SQLException {
        String sql = "SELECT a.id, a.horarioInicial, a.horarioFinal, a.diaSemana, " +
                "t.tipo AS turno, t.sala, uc.nomeUC " +
                "FROM horarios h " +
                "INNER JOIN horario_aulas ha ON h.idHorario = ha.idHorario " +
                "INNER JOIN aulas a ON ha.idAula = a.id " +
                "INNER JOIN turnos t ON a.idTurno = t.id " +
                "INNER JOIN ucs uc ON a.codigoUC = uc.codigoUC " +
                "WHERE h.numeroAluno = ? " +
                "ORDER BY a.horarioInicial, a.id"; // Ordena por horário e ID da aula

        List<String> horario = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, numeroAluno);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String linhaHorario = String.format(
                            "Aula ID: %d | Dia: %s | Hora: %s - %s | Turno: %s | Sala: %s | UC: %s",
                            rs.getInt("id"),
                            rs.getString("diaSemana"),
                            rs.getTime("horarioInicial"),
                            rs.getTime("horarioFinal"),
                            rs.getString("turno"),
                            rs.getString("sala"),
                            rs.getString("nomeUC")
                    );
                    horario.add(linhaHorario);
                }
            }
        }
        return horario;
    }









    public void adicionarAulasAoHorario(int idHorario, List<Integer> idsAulas) throws SQLException {
        String sql = "INSERT INTO horario_aulas (idHorario, idAula) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (int idAula : idsAulas) {
                stmt.setInt(1, idHorario);
                stmt.setInt(2, idAula);
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    private void ordenarHorarioAulasPorIdHorario() throws SQLException {
        String sql = "ALTER TABLE horario_aulas ORDER BY idHorario, idAula"; // Garante que o idHorario fique ordenado
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }




    public List<Aula> buscarAulasPorAluno(String numeroAluno) throws SQLException {
        List<Aula> aulas = new ArrayList<>();
        String sql = "SELECT a.* FROM aulas a "
                + "INNER JOIN horarios h ON a.id = h.idAula "
                + "WHERE h.numeroAluno = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, numeroAluno);
            try (ResultSet rs = stmt.executeQuery()) {
                AulaDAO aulaDAO = new AulaDAO();
                while (rs.next()) {
                    aulas.add(aulaDAO.findById(rs.getInt("id")));
                }
            }
        }
        return aulas;
    }



    public List<Aula> buscarAulasPorHorario(int idHorario) throws SQLException {
        String sql = "SELECT a.id, a.codigoUC, a.horarioInicial, a.horarioFinal, a.diaSemana, " +
                "t.id AS turnoId, t.tipo, t.capacidade, t.sala " +
                "FROM aulas a " +
                "INNER JOIN horario_aulas ha ON a.id = ha.idAula " +
                "INNER JOIN turnos t ON a.idTurno = t.id " +
                "WHERE ha.idHorario = ? " +
                "ORDER BY a.diaSemana, a.horarioInicial, a.id";

        List<Aula> aulas = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idHorario);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Turno turno = new Turno(
                            rs.getInt("turnoId"),
                            rs.getString("tipo"),
                            rs.getInt("capacidade"),
                            rs.getString("sala")
                    );

                    Aula aula = new Aula(
                            rs.getInt("id"),
                            rs.getString("codigoUC"),
                            rs.getTime("horarioInicial").toLocalTime(),
                            rs.getTime("horarioFinal").toLocalTime(),
                            rs.getString("diaSemana"),
                            turno
                    );

                    aulas.add(aula);
                }
            }
        }

        return aulas;
    }


    public boolean alunoPossuiHorario(String numeroAluno) throws SQLException {
        String sql = "SELECT COUNT(*) FROM horarios WHERE numeroAluno = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, numeroAluno);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return true;
                }
            }
        }
        return false;
    }
    public boolean horarioPossuiAulas(int idHorario) throws SQLException {
        String sql = "SELECT COUNT(*) FROM horario_aulas WHERE idHorario = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idHorario);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean alunoPossuiHorarioEAulas(String numeroAluno) throws SQLException {
        String sql = "SELECT idHorario FROM horarios WHERE numeroAluno = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, numeroAluno);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int idHorario = rs.getInt("idHorario");
                    return horarioPossuiAulas(idHorario);
                }
            }
        }
        return false;
    }




}
