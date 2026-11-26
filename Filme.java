import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Filme {
    private static int contador = 1;

    private final int id;
    private String nome;
    private Genero genero;
    private int classificacao; // idade indicativa
    private int duracao;       // minutos
    private StsFilme status;
    private final List<Sala> salas = new ArrayList<>();

    /** Novo filme: gera ID automático. */
    public Filme(String nome, Genero genero, int classificacao, int duracao, StsFilme status) {
        this(contador++, nome, genero, classificacao, duracao, status);
    }

    /** Restaura filme existente (usado ao carregar do arquivo). */
    public Filme(int id, String nome, Genero genero, int classificacao, int duracao, StsFilme status) {
        this.id = id;
        contador = Math.max(contador, id + 1); // evita IDs repetidos após carregar
        setNome(nome);
        this.genero = exigirGenero(genero);
        setClassificacao(classificacao);
        setDuracao(duracao);
        this.status = status;
    }

    /** Filme intenso é derivado do gênero (não é mais informado manualmente). */
    public boolean isIntenso() {
        return genero.isIntenso();
    }

    /** Adiciona sala validando regras do tipo (ex.: D-BOX exige filme intenso). */
    public void adicionarSala(Sala sala) {
        if (sala instanceof SalaDBOX dbox && !dbox.filmeValido(this)) {
            throw new IllegalArgumentException("Sala D-BOX só aceita filmes intensos (gênero atual: " + genero + ").");
        }
        salas.add(sala);
    }

    public boolean removerSala(int idSala) {
        return salas.removeIf(s -> s.getId() == idSala);
    }

    public Sala buscarSala(int idSala) {
        return salas.stream().filter(s -> s.getId() == idSala).findFirst().orElse(null);
    }

    public boolean possuiSalaDBOX() {
        return salas.stream().anyMatch(s -> s instanceof SalaDBOX);
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public Genero getGenero() { return genero; }
    public int getClassificacao() { return classificacao; }
    public int getDuracao() { return duracao; }
    public StsFilme getStatus() { return status; }
    public List<Sala> getSalas() { return Collections.unmodifiableList(salas); }

    public void setNome(String nome) {
        if (nome == null || nome.isBlank()) throw new IllegalArgumentException("Nome é obrigatório.");
        this.nome = nome.trim();
    }

    /** Trocar o gênero pode mudar o "intenso"; bloqueia se houver sala D-BOX incompatível. */
    public void setGenero(Genero genero) {
        exigirGenero(genero);
        if (!genero.isIntenso() && possuiSalaDBOX())
            throw new IllegalStateException("Remova as salas D-BOX antes de mudar para o gênero "
                    + genero + " (não intenso).");
        this.genero = genero;
    }

    public void setClassificacao(int classificacao) {
        if (classificacao < 0) throw new IllegalArgumentException("Classificação inválida.");
        this.classificacao = classificacao;
    }

    public void setDuracao(int duracao) {
        if (duracao <= 0) throw new IllegalArgumentException("Duração deve ser positiva.");
        this.duracao = duracao;
    }

    public void setStatus(StsFilme status) { this.status = status; }

    private static Genero exigirGenero(Genero genero) {
        if (genero == null) throw new IllegalArgumentException("Gênero é obrigatório.");
        return genero;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(String.format(
                "Filme #%d: %s | %s | %d anos | %d min | %s | %s",
                id, nome, genero, classificacao, duracao, isIntenso() ? "intenso" : "leve", status));
        if (salas.isEmpty()) sb.append("\n   (sem salas cadastradas)");
        for (Sala s : salas) sb.append("\n   ").append(s);
        return sb.toString();
    }
}
