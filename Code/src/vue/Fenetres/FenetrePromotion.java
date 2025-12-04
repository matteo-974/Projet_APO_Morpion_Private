package vue.Fenetres;

import modele.jeu.*;
import modele.jeu.Pieces.PiecesEchec.Cavalier;
import modele.jeu.Pieces.PiecesEchec.Fou;
import modele.jeu.Pieces.PiecesEchec.Reine;
import modele.jeu.Pieces.PiecesEchec.Tour;
import modele.plateau.Case;
import modele.plateau.Plateau;

import javax.swing.*;
import java.awt.*;

public class FenetrePromotion extends JDialog {
    private final String couleur;
    private final Case caseDestination;
    private Piece piecePromo = null;

    public FenetrePromotion(String couleur, Case caseDestination) {
        this.couleur = couleur;
        this.caseDestination = caseDestination;

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setTitle("Choisissez une pièce de promotion");
        setModal(true);
        setLayout(new GridLayout(2, 2));

        // Création des boutons avec les icônes correspondantes
        JButton dameButton = createButton("Dame");
        JButton tourButton = createButton("Tour");
        JButton cavalierButton = createButton("Cavalier");
        JButton fouButton = createButton("Fou");

        // Ajout des boutons à la fenêtre
        add(dameButton);
        add(tourButton);
        add(cavalierButton);
        add(fouButton);

        setSize(400, 400);
        setLocationRelativeTo(null); // Centrer la fenêtre
        setVisible(true);
    }

    private JButton createButton(String type) {
        String suffixe = switch (type) {
            case "Dame" -> "Q";
            case "Tour" -> "R";
            case "Cavalier" -> "N";
            case "Fou" -> "B";
            default -> "";
        };
        String prefixe = couleur.equalsIgnoreCase("Blanc") ? "w" : "b";
        String imagePath = "Images/" + prefixe + suffixe + ".png";

        JButton button = new JButton(new ImageIcon(imagePath));
        button.addActionListener(e -> {
            piecePromo = choisirPromotion(type);
            dispose(); // Ferme la fenêtre après la sélection
        });
        return button;
    }

    private Piece choisirPromotion(String type) {
        Piece pieceExistante = caseDestination.getPiece();
        Plateau plateau = (pieceExistante != null) ? pieceExistante.getPlateau() : null;

        if (plateau == null) {
            throw new IllegalStateException("Impossible de récupérer le plateau depuis la case de destination.");
        }

        return switch (type) {
            case "Dame" -> new Reine(couleur, plateau, caseDestination);
            case "Tour" -> new Tour(couleur, plateau, caseDestination);
            case "Cavalier" -> new Cavalier(couleur, plateau, caseDestination);
            case "Fou" -> new Fou(couleur, plateau, caseDestination);
            default -> null;
        };
    }

    public Piece getPiecePromo() {
        return piecePromo;
    }
}
