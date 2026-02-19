// Classe AulaDAO
package data;

import logica.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AulaDAO {
    private Connection connection;

    public AulaDAO() {
        // Certifique-se de que a tabela 'turnos' existe antes de criar 'aulas'
        TurnoDAO turnoDAO = new TurnoDAO();
        this.connection = DatabaseConnection.getConnection();
        criarTabela();
    }

    public void criarTabela() {
        String sql = "CREATE TABLE IF NOT EXISTS aulas (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "codigoUC VARCHAR(50) NOT NULL, " +
                "horarioInicial TIME NOT NULL, " +
                "horarioFinal TIME NOT NULL, " +
                "diaSemana VARCHAR(15) NOT NULL, " + // Novo campo
                "idTurno INT NOT NULL, " +
                "FOREIGN KEY (idTurno) REFERENCES turnos(id))";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar tabela de Aulas", e);
        }
    }


    public void insert(Aula aula) throws SQLException {
        String sql = "INSERT INTO aulas (codigoUC, horarioInicial, horarioFinal, diaSemana, idTurno) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, aula.getCodigoUC());
            stmt.setTime(2, Time.valueOf(aula.getHorarioInicial()));
            stmt.setTime(3, Time.valueOf(aula.getHorarioFinal()));
            stmt.setString(4, aula.getDiaSemana()); // Novo campo
            stmt.setInt(5, aula.getTurno().getId());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    aula.setId(rs.getInt(1));
                }
            }
        }
    }



    public List<Aula> findByCodigoUC(String codigoUC) throws SQLException {
        List<Aula> aulas = new ArrayList<>();
        String sql = "SELECT * FROM aulas WHERE codigoUC = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, codigoUC);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Turno turno = new TurnoDAO().findById(rs.getInt("idTurno"));
                    Aula aula = new Aula(
                            rs.getInt("id"),
                            rs.getString("codigoUC"),
                            rs.getTime("horarioInicial").toLocalTime(),
                            rs.getTime("horarioFinal").toLocalTime(),
                            rs.getString("diaSemana"), // Adiciona o novo atributo
                            turno
                    );
                    aulas.add(aula);
                }
            }
        }
        return aulas;
    }


    public Aula findById(int id) throws SQLException {
        String sql = "SELECT * FROM aulas WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Turno turno = new TurnoDAO().findById(rs.getInt("idTurno"));
                    return new Aula(
                            rs.getInt("id"),
                            rs.getString("codigoUC"),
                            rs.getTime("horarioInicial").toLocalTime(),
                            rs.getTime("horarioFinal").toLocalTime(),
                            rs.getString("diaSemana"), // Adiciona o novo atributo
                            turno
                    );
                }
            }
        }
        return null;
    }

    public List<Aula> findAll() throws SQLException {
        List<Aula> aulas = new ArrayList<>();
        String sql = "SELECT * FROM aulas";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Turno turno = new TurnoDAO().findById(rs.getInt("idTurno"));
                Aula aula = new Aula(
                        rs.getInt("id"),
                        rs.getString("codigoUC"),
                        rs.getTime("horarioInicial").toLocalTime(),
                        rs.getTime("horarioFinal").toLocalTime(),
                        rs.getString("diaSemana"), // Novo campo
                        turno
                );
                aulas.add(aula);
            }
        }
        return aulas;
    }

}
