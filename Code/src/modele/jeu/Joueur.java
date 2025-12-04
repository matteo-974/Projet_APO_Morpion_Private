package modele.jeu;

public class Joueur {

    private final Couleur couleur;
    private int points = 0;

    public enum Couleur {
        BLANC, NOIR
    }

    public Joueur(Couleur couleur) {
        this.couleur = couleur;
    }

    public Couleur getCouleur() {
        return couleur;
    }

    public boolean estAllie(Joueur autre) {
        return this.couleur == autre.couleur;
    }
    
    public int getPoints() {
        return points;
    }
    
    public void ajouterPoint() {
        this.points++;
    }

}
