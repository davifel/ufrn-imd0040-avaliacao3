import java.time.LocalTime;

public class SalaDBOX extends Sala {
    public SalaDBOX(int numero, int lugaresMax, LocalTime horario, boolean trid) {
        super(numero, lugaresMax, horario, trid);
    }

    /** Restauração a partir do arquivo. */
    public SalaDBOX(int id, int numero, int lugaresMax, int lugares, LocalTime horario, boolean trid) {
        super(id, numero, lugaresMax, lugares, horario, trid);
    }

    /** D-BOX só exibe filmes intensos (definido pelo gênero). */
    public boolean filmeValido(Filme filme) {
        return filme != null && filme.isIntenso();
    }

    @Override
    public String getTipo() { return "D-BOX"; }
}
