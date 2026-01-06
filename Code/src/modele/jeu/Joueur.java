package modele.jeu;

/**
 * Représente un joueur avec une couleur et un score.
 */
public class Joueur {

    private final Couleur couleur;
    private int points = 0;

    public enum Couleur {
        BLANC, NOIR
    }

    /**
     * Construit un joueur avec la couleur donnée.
     * @param couleur couleur du joueur
     */
    public Joueur(Couleur couleur) {
        this.couleur = couleur;
    }

    /**
     * Retourne la couleur du joueur.
     * @return couleur du joueur
     */
    public Couleur getCouleur() {
        return couleur;
    }

    /**
     * Indique si ce joueur est allié à un autre (même couleur).
     * @param autre autre joueur
     * @return true si même couleur, false sinon
     */
    public boolean estAllie(Joueur autre) {
        return this.couleur == autre.couleur;
    }
    
    /**
     * Retourne le score actuel du joueur.
     * @return nombre de points
     */
    public int getPoints() {
        return points;
    }
    
    /**
     * Incrémente le score du joueur d'un point.
     */
    public void ajouterPoint() {
        this.points++;
    }

}
