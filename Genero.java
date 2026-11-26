/**
 * Gêneros de filme. Cada gênero define se o filme é "intenso"
 * (ação/impacto), o que determina a compatibilidade com salas D-BOX.
 * Também servirá de base para recomendações por perfil de cliente.
 */
public enum Genero {
    ACAO("Ação", true),
    TERROR("Terror", true),
    FICCAO_CIENTIFICA("Ficção Científica", true),
    SUSPENSE("Suspense", true),
    AVENTURA("Aventura", false),
    FANTASIA("Fantasia", false),
    COMEDIA("Comédia", false),
    DRAMA("Drama", false),
    ROMANCE("Romance", false),
    ANIMACAO("Animação", false),
    MUSICAL("Musical", false),
    DOCUMENTARIO("Documentário", false);

    private final String nome;
    private final boolean intenso;

    Genero(String nome, boolean intenso) {
        this.nome = nome;
        this.intenso = intenso;
    }

    public String getNome() { return nome; }

    public boolean isIntenso() { return intenso; }

    @Override
    public String toString() { return nome; }
}
