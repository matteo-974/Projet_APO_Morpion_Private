package modele.deplacements;

import modele.plateau.Case;
import java.util.List;

/**
 * Décorateur abstrait pour calculer les cases accessibles à partir d'une case donnée.
 * <p>
 * Le principe consiste à composer plusieurs stratégies de déplacement en chaînant des décorateurs.
 * Chaque implémentation fournit ses propres cases accessibles via {@link #getMesCA(Case)} et la
 * méthode finale {@link #getCA(Case)} fusionne ces résultats avec ceux de la base si elle existe.
 * </p>
 */
public abstract class DecCasesAccessibles {
    /** Décorateur de base (peut être null) dont on agrège également les résultats. */
    private DecCasesAccessibles base;

    /**
     * Construit un décorateur de calcul des cases accessibles.
     * @param base décorateur de base à chaîner (peut être null)
     */
    public DecCasesAccessibles(DecCasesAccessibles base) {
        this.base = base;
    }

    /**
     * Calcule les cases accessibles propres à ce décorateur à partir de la case fournie.
     * @param c case de départ
     * @return la liste des cases accessibles spécifiques à ce décorateur (peut être vide, mais non null de préférence)
     */
    public abstract List<Case> getMesCA(Case c);

    /**
     * Calcule les cases accessibles en agrégeant celles de ce décorateur et, si présent, celles de la base.
     * @param c case de départ
     * @return la liste des cases accessibles résultante
     */
    public List<Case> getCA(Case c){
        List<Case> cA = getMesCA(c);
        if(base != null) {
            List<Case> cB = base.getMesCA(c); // cas de la base
            if (cB != null) {
                cA.addAll(cB);
            }
        }
        return cA;
    }
}
