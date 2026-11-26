import java.time.LocalTime;

public class SalaVIP extends Sala {
    private boolean possAtender; // sala com atendimento de garçom disponível

    public SalaVIP(int numero, int lugaresMax, LocalTime horario, boolean trid, boolean possAtender) {
        super(numero, lugaresMax, horario, trid);
        this.possAtender = possAtender;
    }

    /** Restauração a partir do arquivo. */
    public SalaVIP(int id, int numero, int lugaresMax, int lugares, LocalTime horario, boolean trid,
                   boolean possAtender) {
        super(id, numero, lugaresMax, lugares, horario, trid);
        this.possAtender = possAtender;
    }

    public void atenderSala() {
        if (possAtender) System.out.println("Atendimento VIP em andamento na sala " + numero + ".");
        else System.out.println("Sala " + numero + " não possui atendimento no momento.");
    }

    public boolean isPossAtender() { return possAtender; }

    @Override
    public String getTipo() { return "VIP"; }
}
