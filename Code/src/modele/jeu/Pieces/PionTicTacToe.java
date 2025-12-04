package modele.jeu.Pieces;

import modele.deplacements.DecTicTacToe;
import modele.jeu.Piece;
import modele.plateau.Case;
import modele.plateau.Plateau;


/**
 * Représente un pion du jeu TicTacToe
 */
public class PionTicTacToe extends Piece {

    public PionTicTacToe(String couleur, Plateau p, Case c) {
        super(couleur, p, c, new DecTicTacToe());
    }
}
