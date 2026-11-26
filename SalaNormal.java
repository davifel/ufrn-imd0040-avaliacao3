import java.time.LocalTime;

public class SalaNormal extends Sala {
    public SalaNormal(int numero, int lugaresMax, LocalTime horario, boolean trid) {
        super(numero, lugaresMax, horario, trid);
    }

    /** Restauração a partir do arquivo. */
    public SalaNormal(int id, int numero, int lugaresMax, int lugares, LocalTime horario, boolean trid) {
        super(id, numero, lugaresMax, lugares, horario, trid);
    }

    @Override
    public String getTipo() { return "Normal"; }
}
