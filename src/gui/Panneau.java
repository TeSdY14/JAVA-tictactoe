package gui;



import javax.swing.*;

import modele.TicTacToe;


import java.awt.*;
import java.awt.event.*;

/**
 * Panneau tres simple pour l'affichage et la depose d'un pion
 */
@SuppressWarnings("serial")
class Panneau extends JPanel implements ActionListener
{  
	/**le jeu contenant l'IA*/
	TicTacToe jeu;
	/**l'affichage des jetons sous forme de labels !*/
	JButton [][] matriceJButtons;
	/**la matrice de jeu affichee*/
	 int[][] matriceJeu;

		/**constructeur 
		 * @param _jeu le jeu IA rattache a cette fenetre*/
	Panneau(TicTacToe _jeu)
	{
		jeu = _jeu;
		this.setLayout(new GridLayout(3,3));
		matriceJButtons = new JButton[3][3];
		for(int i=0; i<3; i++)
			for(int j=0; j<3; j++)
			{
				matriceJButtons[i][j] = new JButton(".");
				matriceJButtons[i][j].setActionCommand(""+i+","+j);
				matriceJButtons[i][j].addActionListener(this);
				this.add(matriceJButtons[i][j]);
			}
	}
	
	
	/**mise a jour de l'affichage a partir de la matrice de jeu recue
	 * @param _matriceJeu matrice de jeu a afficher
	 * */
	void updateJeu( int[][] _matriceJeu)
	{
		matriceJeu = _matriceJeu;
		for(int i=0; i<TicTacToe.HEIGHT; i++)
			for(int j=0; j<TicTacToe.WIDTH; j++)
			{
				if (matriceJeu[i][j]==1) matriceJButtons[i][j].setText("O");
				if (matriceJeu[i][j]==2) matriceJButtons[i][j].setText("X");
			}
	}
	

	/**
	 * reaction au clic de l'utilisateur sur un bouton -> ajout d'un jeton dans la colonne choisie
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) 
	{
		JButton button = (JButton)e.getSource();
		button.setText("O");
		String cmd = e.getActionCommand();
		String[] strCoor = cmd.split(",");
		int i = Integer.valueOf(strCoor[0]);
		int j = Integer.valueOf(strCoor[1]);
//		System.err.println("---nouveau tour de jeu --------");
		jeu.tourDeJeu(i,j);
	}
	

	
}
