package modele.jeu;

import modele.plateau.Case;

public class Coup{
    private Case caseDepart;
    private Case caseArrivee;

    public Coup(Case caseDepart, Case caseArrivee) {
        this.caseDepart = caseDepart;
        this.caseArrivee = caseArrivee;
    }

    public Case getDepart() {
        return caseDepart;
    }

    public Case getArrivee() {
        return caseArrivee;
    }


    public Case[] getCoup() {
        return new Case[] {caseDepart, caseArrivee};
    }
}