package gui;

import javax.swing.*;
import modele.TicTacToe;
import java.awt.*;

/**
 * Fenêtre tres basique pour le jeu du puissance 3  sur grille de 5 x 5
 */
@SuppressWarnings("serial")
public class FenetreTTT extends JFrame
{  
	/** panneau représentant le jeu et les captures d'événements */
	Panneau p;

	/**le jeu permettant la reflexion*/
	TicTacToe jeu;
	
	/**constructeur 
	 * @param _jeu le jeu IA rattache a cette fenêtre */
	public FenetreTTT(TicTacToe _jeu)
	{
		jeu = _jeu;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container content = getContentPane();
		content.setLayout(new BorderLayout());
		p = new Panneau(jeu);		
		content.add(p, BorderLayout.CENTER);
		pack();
	}
	
	/**met a jour la matrice de jeu du panneau graphique
	 * @param _matriceJeu matrice de jeu deduite par l'ia qui doit etre mise a jour dans le paneau*/
	public void updateJeu( int[][] _matriceJeu)
	{
		p.updateJeu(_matriceJeu);
		repaint();
	}
	
	
}
