package br.com.alura.screenmatch.model;

public enum MediaGenre {
    ACAO("Action"), AVENTURA("Adventure"), COMEDIA("Comedy"),
    CRIME("Crime"), DRAMA("Drama"), HORROR("Terror"),
    ROMANCE("Romance");

    private String omdbGenre;

    MediaGenre(String omdbGenre) {
        this.omdbGenre = omdbGenre;
    }

    public static MediaGenre fromString(String text) {
        for (MediaGenre genre : MediaGenre.values()) {
            if (genre.omdbGenre.equalsIgnoreCase(text)) {
                return genre;
            }
        }
        throw new IllegalArgumentException("Nenuma categoria encontrada para a string fornecida");
    }
}
