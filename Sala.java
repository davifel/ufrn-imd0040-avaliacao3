import java.time.LocalTime;

/** Classe abstrata: cada tipo de sala define seu próprio tipo e regras. */
public abstract class Sala {
    private static int contador = 1;

    private final int id;
    protected int numero;
    protected int lugares;      // assentos disponíveis
    protected int lugaresMax;   // capacidade total
    protected LocalTime horario;
    protected boolean trid;     // suporta 3D?

    /** Nova sala: gera ID automático e começa com todos os lugares livres. */
    protected Sala(int numero, int lugaresMax, LocalTime horario, boolean trid) {
        this(contador++, numero, lugaresMax, lugaresMax, horario, trid);
    }

    /** Restaura sala existente (usado ao carregar do arquivo). */
    protected Sala(int id, int numero, int lugaresMax, int lugares, LocalTime horario, boolean trid) {
        if (numero <= 0) throw new IllegalArgumentException("Número da sala deve ser positivo.");
        if (lugaresMax <= 0) throw new IllegalArgumentException("Capacidade deve ser positiva.");
        if (lugares < 0 || lugares > lugaresMax)
            throw new IllegalArgumentException("Lugares disponíveis inválidos.");
        if (horario == null) throw new IllegalArgumentException("Horário é obrigatório.");
        this.id = id;
        contador = Math.max(contador, id + 1); // evita IDs repetidos após carregar
        this.numero = numero;
        this.lugaresMax = lugaresMax;
        this.lugares = lugares;
        this.horario = horario;
        this.trid = trid;
    }

    public abstract String getTipo();

    /** Reserva assentos; retorna false se não houver lugares suficientes. */
    public boolean reservar(int qtd) {
        if (qtd <= 0 || qtd > lugares) return false;
        lugares -= qtd;
        return true;
    }

    public void status() {
        System.out.println(this);
    }

    public int getId() { return id; }
    public int getNumero() { return numero; }
    public int getLugares() { return lugares; }
    public int getLugaresMax() { return lugaresMax; }
    public LocalTime getHorario() { return horario; }
    public boolean isTrid() { return trid; }

    public void setHorario(LocalTime horario) {
        if (horario == null) throw new IllegalArgumentException("Horário é obrigatório.");
        this.horario = horario;
    }

    public void setTrid(boolean trid) { this.trid = trid; }

    @Override
    public String toString() {
        return String.format("[Sala #%d | nº %d | %s | %s | %s | %d/%d lugares livres]",
                id, numero, getTipo(), horario, trid ? "3D" : "2D", lugares, lugaresMax);
    }
}
