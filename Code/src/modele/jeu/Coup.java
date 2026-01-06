package modele.jeu;

import modele.plateau.Case;

/**
 * Représente un coup joué entre deux cases (départ et arrivée).
 * <p>
 * Cet objet est immuable une fois créé et sert d’interface entre la vue et le modèle.
 * </p>
 */
public class Coup{
    private Case caseDepart;
    private Case caseArrivee;

    /**
     * Crée un coup entre une case de départ et une case d'arrivée.
     * @param caseDepart case source du déplacement
     * @param caseArrivee case cible du déplacement
     */
    public Coup(Case caseDepart, Case caseArrivee) {
        this.caseDepart = caseDepart;
        this.caseArrivee = caseArrivee;
    }

    /**
     * Retourne la case de départ du coup.
     * @return la case source
     */
    public Case getDepart() {
        return caseDepart;
    }

    /**
     * Retourne la case d'arrivée du coup.
     * @return la case cible
     */
    public Case getArrivee() {
        return caseArrivee;
    }


    /**
     * Retourne le couple (départ, arrivée) sous forme de tableau de 2 cases.
     * @return un tableau {caseDepart, caseArrivee}
     */
    public Case[] getCoup() {
        return new Case[] {caseDepart, caseArrivee};
    }
}