package vue.Fenetres;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import audio.SoundManager;

public class FenetreMenuPrincipal extends JFrame {

    private JButton btnJouer;
    private JButton btnParametres;
    private JButton btnQuitter;

    public FenetreMenuPrincipal() {

        // Configuration de la fenêtre
        this.setTitle("Menu Principal");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 800);
        this.setLocationRelativeTo(null);

        // Création du panel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        mainPanel.setBackground(new Color(200, 220, 250));

        // Création des boutons avec images
        btnJouer = new JButton(new ImageIcon("images/Boutons/jouer.png"));
        btnParametres = new JButton(new ImageIcon("images/Boutons/parametres.png"));
        btnQuitter = new JButton(new ImageIcon("images/Boutons/quitter.png"));

        // Paramètres esthétiques
        JButton[] boutons = {btnJouer, btnParametres, btnQuitter};
        Dimension buttonSize = new Dimension(256, 128);
        for (JButton b : boutons) {
            b.setBorderPainted(false);
            b.setContentAreaFilled(false);
            b.setFocusPainted(false);
            b.setPreferredSize(buttonSize);
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
        }

        // Actions des boutons via le contrôleur
        btnJouer.addActionListener(e -> afficherMenuJeu());
        btnParametres.addActionListener(e -> ouvrirParametres());
        btnQuitter.addActionListener(e -> quitterProgramme());

        // Ajout des boutons avec espacement
        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(btnJouer);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(btnParametres);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(btnQuitter);
        mainPanel.add(Box.createVerticalGlue());

        this.add(mainPanel);
    }

    /**
     * Ouvre la fenêtre de sélection des jeux et ferme le menu principal actuel.
     * @return toujours null (compatibilité avec l'usage existant)
     */
    private Object afficherMenuJeu() {
        this.dispose();
        SwingUtilities.invokeLater(() -> {
            // Création d'une instance de la fenêtre menu jeu
            FenetreMenuJeu menuJeu = new FenetreMenuJeu();
            menuJeu.setVisible(true);
        });
        return null;
    }

    /**
     * Affiche une boîte de dialogue de paramètres audio permettant de régler
     * le volume général et l'état muet, et de tester la sortie sonore.
     */
    private void ouvrirParametres() {
        JDialog dialog = new JDialog(this, "Paramètres", true);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel volumeLabel = new JLabel("Volume :");
        volumeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JSlider slider = new JSlider(0, 100, Math.round(SoundManager.getVolume() * 100f));
        slider.setMajorTickSpacing(25);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setAlignmentX(Component.LEFT_ALIGNMENT);

        JCheckBox muteBox = new JCheckBox("Muet", SoundManager.isMuted());
        muteBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton testBtn = new JButton("Tester le son");
        testBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Listeners
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                float v = slider.getValue() / 100f;
                SoundManager.setVolume(v);
                if (v == 0f) {
                    muteBox.setSelected(true);
                    SoundManager.setMuted(true);
                } else if (muteBox.isSelected()) {
                    muteBox.setSelected(false);
                    SoundManager.setMuted(false);
                }
            }
        });

        muteBox.addActionListener(e -> SoundManager.setMuted(muteBox.isSelected()));
        testBtn.addActionListener(e -> SoundManager.playSound("Sounds/game-start.wav"));

        panel.add(volumeLabel);
        panel.add(slider);
        panel.add(Box.createVerticalStrut(8));
        panel.add(muteBox);
        panel.add(Box.createVerticalStrut(12));
        panel.add(testBtn);

        dialog.getContentPane().add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }


    /**
     * Ferme proprement la fenêtre du menu principal.
     */
    private void quitterProgramme() {
        this.dispose();
    }
}
