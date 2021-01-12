package modele;
import gui.FenetreTTT;
import java.util.ArrayList;
import algo.Resolution;

/**
 * classe permettant la gestion du jeu de TicTacToe<br>
 * contient la matrice 3x3, modèle du jeu, constituée d'entiers, <br>
 * permet la creation de l'arbre des situations,<br>
 * ainsi que la detection de fin de jeu
 * */
public class TicTacToe {

	/**
	 * Matrice représentant le jeu; le jeu se joue sur une grille 6x6,
	 * la dernière ligne contient le nb d'élément dans les colonnes,
	 * la dernière colonne contient le nb d'élément dans les lignes,
	 * */
	private final int[][] matriceJeu;

	/** number of cells in width */
	public  static final int WIDTH = 3;
	/**number of cells in height */
	public  static final int HEIGHT = 3;
	/**profondeur de l'arbre de recherche*/
	public final int PROFONDEUR_DE_JEU = 3;

	/** interface graphique associée au jeu */
	FenetreTTT gui;

	/**constructeur*/
	public TicTacToe()
	{
		matriceJeu = new int[HEIGHT][WIDTH];
		gui = new FenetreTTT(this);
		gui.setVisible(true);
	}

	/**
	 * tour de jeu = jeu de l'humain et jeu de l'ordi (par alphabeta)
	 * @param column no de colonne jouée */
	public void tourDeJeu(int line, int column)
	{
		boolean retour = true;
		assert (line >= 0 && line < HEIGHT ): "pb indice de ligne hors limites";
		assert (column >= 0 && column < WIDTH): "pb indice de colonne hors limites";
		retour = jeu(TypeJoueur.JOUEUR, line, column);
		if(retour)
		{
			Situation s = new Situation();
			s.setMax(true);
			s.setMatriceJeu(matriceJeu);
			creerArbreSituation(s, PROFONDEUR_DE_JEU);
			double bonneVal = Resolution.alphaBeta(s,-Double.MAX_VALUE, Double.MAX_VALUE );
			s.setH(bonneVal);

			boolean trouve = false;
			ArrayList<Situation> successeurs = s.getSuccesseurs();
			int nbSuccesseurs = successeurs.size();
			Situation goodSituation = null;

			for(int i = 0; i<nbSuccesseurs && !trouve; i++)
			{
				trouve = (successeurs.get(i).getHeuristique() == bonneVal);
				if(trouve) goodSituation = successeurs.get(i);
			}

			if(trouve) retour =jeu(TypeJoueur.MACHINE, goodSituation.getLine(), goodSituation.getColumn());
			else
			{
				System.err.println("bizarre, je n'ai pas trouvé la solution de valeur " + bonneVal);
			}

		}

	}

	/**
	 * action de jeu 
	 * @param joueur type de joueur
	 * @param colonne no de colonne jouee
	 * @return vrai si coup autorise
	 * */
	public boolean jeu(TypeJoueur joueur, int i, int j)
	{
		boolean retour = true;

		matriceJeu[i][j] = joueur.getType();
		gui.updateJeu(matriceJeu);
		Situation s = new Situation();
		s.setMatriceJeu(matriceJeu);
		retour = !isEndOfGame(s, joueur);

		return retour;
	}


	/**determine if it is the end of the game.<br>
	 * i.e. if someone win*/
	private boolean isEndOfGame(Situation s, TypeJoueur joueur)
	{
		boolean gain = false;
		String message = "";
		boolean full = false;
		if(s.isFull())
		{
			message += "Toute la grille est remplie !!!";
			full = true;
		}
		if (s.troisPionsAlignes(joueur, false))
		{
			gain = true;
			message +=  "Le joueur " + joueur + " a gagne sur une colonne !\n";
		}
		if (s.troisPionsAlignes(joueur, true) )
		{
			gain = true;
			message +=  "Le joueur " + joueur + " a gagne sur une ligne !\n";
		}
		if (s.troisPionsAlignesDiagonale(joueur) )
		{
			gain = true;
			message +=  "Le joueur " + joueur + " a gagne sur une diagonale !\n";
		}

		if (gain || full)
		{
			javax.swing.JOptionPane.showMessageDialog(gui, message, "fin du jeu!!", javax.swing.JOptionPane.INFORMATION_MESSAGE);
		}
		return gain;
	}

	/**
	 * action de jeu sur matrice passee en parametre
	 * @param joueur type de joueur
	 * @param colonne no de colonne jouee
	 * @param matriceJeu matrice de jeu sur laquelle il faut jouer (<> de la vraie matricede jeu)
	 * @return vrai si coup autorise
	 * */
	public boolean jeuPossible( int ligne, int colonne, int[][] _matriceJeu)
	{
		boolean retour = true;
		if (_matriceJeu[ligne][colonne]!=0) retour = false;
		return retour;
	}


	/**cree un arbre de situation sur 2 nbNiveaux a partir de la situation du jeu courant
	 * @param s situation a partir de laquelle il faut etendre l'arbre
	 * @param nbNiveaux nb de niveaux de l'arbre restants a creer*/
	void creerArbreSituation(Situation s, int nbNiveaux)
	{
		if (s.troisPionsAlignes( ) || s.isFull()) s.setClose(true);
		if(nbNiveaux==0) s.setFeuille(true);		
		if(s.isClose() || s.isFeuille() || nbNiveaux==0)
		{
			s.evaluer();
//			s.afficheMatrice();
		}
		else
		{			
			int[][] matriceS = s.getMatriceJeu();
			for (int i=0; i<HEIGHT; i++)
			{
				for (int j=0; j<WIDTH; j++)
				{
					if (jeuPossible(i, j,matriceS ))
					{
						Situation sprim = new Situation(0, !s.isMax());
						int[][] matriceJeuDeduite = new int[HEIGHT][WIDTH];
						copieMatrice(matriceS, matriceJeuDeduite);
						TypeJoueur tj = (s.isMax()?TypeJoueur.MACHINE:TypeJoueur.JOUEUR);
						matriceJeuDeduite[i][j] = tj.getType();
						sprim.setColumn(j);
						sprim.setLine(i);
						sprim.setMatriceJeu(matriceJeuDeduite);
						s.addSuccesseur(sprim);
						creerArbreSituation( sprim, nbNiveaux-1);
					}
				}
			}
		}
	}



	/**fonction de recopie de la matrice de jeu 6x5
	 * @param from matrice a recopier
	 * @param to matrice recopiee*/
	private void copieMatrice(int [][]from, int[][]to)
	{
		for(int i=0; i<HEIGHT; i++)
			System.arraycopy(from[i], 0, to[i], 0, WIDTH);
	}

	/**
	 * @return the matriceJeu
	 */
	public int[][] getMatriceJeu() {
		return matriceJeu;
	}

}
