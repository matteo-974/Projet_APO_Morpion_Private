package modele.jeu;

import modele.plateau.Case;

/**
 * Listener d'événements du jeu (pattern Observer spécifique au jeu).
 * La vue/contrôleur implémente cette interface pour être notifiée des
 * actions et états importants du modèle, sans couplage à la console ni au son.
 */
public interface JeuEventListener {
    /**
     * Appelé lorsqu'un coup valide a été joué.
     * @param joueur le joueur qui a joué
     * @param destination la case d'arrivée jouée
     */
    void onCoupJoue(Joueur joueur, Case destination);

    /**
     * Appelé lorsqu'un coup est invalide.
     * @param raison description brève de la cause
     */
    void onCoupInvalide(String raison);

    /**
     * Appelé lorsque la partie est terminée avec un gagnant.
     * @param gagnant le joueur gagnant
     */
    void onPartieTerminee(Joueur gagnant);

    /**
     * Appelé lorsque la partie se termine par un match nul.
     */
    void onMatchNul();
}
