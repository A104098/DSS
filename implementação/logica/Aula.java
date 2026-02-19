package logica;

import java.time.LocalTime;

public class Aula {
    private int id; // Identificador único da aula
    private String codigoUC; // Referência ao código da UC
    private LocalTime horarioInicial;
    private LocalTime horarioFinal;
    private String diaSemana; // Novo atributo para o dia da semana
    private Turno turno; // Tipo, capacidade e sala associados ao turno

    public Aula(int id, String codigoUC, LocalTime horarioInicial, LocalTime horarioFinal, String diaSemana, Turno turno) {
        if (horarioInicial.isAfter(horarioFinal)) {
            throw new IllegalArgumentException("Horário inicial não pode ser depois do horário final.");
        }
        this.id = id;
        this.codigoUC = codigoUC;
        this.horarioInicial = horarioInicial;
        this.horarioFinal = horarioFinal;
        this.diaSemana = diaSemana;
        this.turno = turno;
    }

    public Aula(String codigoUC, LocalTime horarioInicial, LocalTime horarioFinal, String diaSemana, Turno turno) {
        this(0, codigoUC, horarioInicial, horarioFinal, diaSemana, turno); // ID será atribuído pelo banco de dados
    }

    public int getId() {
        return id;
    }

    public String getCodigoUC() {
        return codigoUC;
    }

    public LocalTime getHorarioInicial() {
        return horarioInicial;
    }

    public LocalTime getHorarioFinal() {
        return horarioFinal;
    }

    public String getDiaSemana() {
        return diaSemana;
    }

    public Turno getTurno() {
        return turno;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTurno(Turno turno) {
        this.turno = turno;
    }

    @Override
    public String toString() {
        return "Aula{" +
                "id=" + id +
                ", codigoUC='" + codigoUC + '\'' +
                ", horarioInicial=" + horarioInicial +
                ", horarioFinal=" + horarioFinal +
                ", diaSemana='" + diaSemana + '\'' +
                ", turno=" + turno +
                '}';
    }
}
