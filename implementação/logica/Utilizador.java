package logica;
public interface Utilizador {
    String getNome();
    String getNumero();
    String getEmail();
    String getPassword();
    boolean login(String numero, String password);
    boolean logout(String numero);
}