package data;

import logica.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TurnoDAO {
    private Connection connection;

    public TurnoDAO() {
        this.connection = DatabaseConnection.getConnection();
        criarTabela();
    }

    private void criarTabela() {
        String sql = "CREATE TABLE IF NOT EXISTS turnos ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "tipo VARCHAR(2) NOT NULL,"
                + "capacidade INT NOT NULL,"
                + "sala VARCHAR(10) NOT NULL)";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar tabela de Turnos", e);
        }
    }

    public void insert(Turno turno) throws SQLException {
        String sql = "INSERT INTO turnos (tipo, capacidade, sala) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, turno.getTipo());
            stmt.setInt(2, turno.getCapacidade());
            stmt.setString(3, turno.getSala());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    turno.setId(keys.getInt(1));
                }
            }
        }
    }

    public Turno findById(int id) throws SQLException {
        String sql = "SELECT * FROM turnos WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Turno(
                            rs.getString("tipo"),
                            rs.getInt("capacidade"),
                            rs.getString("sala")
                    );
                }
            }
        }
        return null;
    }

    public List<Turno> findAll() throws SQLException {
        List<Turno> turnos = new ArrayList<>();
        String sql = "SELECT * FROM turnos";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Turno turno = new Turno(
                        rs.getString("tipo"),
                        rs.getInt("capacidade"),
                        rs.getString("sala")
                );
                turnos.add(turno);
            }
        }
        return turnos;
    }
}
