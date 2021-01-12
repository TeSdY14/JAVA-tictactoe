package modele;
import java.util.ArrayList;

/**classe Situation, elle représente un noeud de l'arbre des états
 * @author emmanuel adam
 * @version mai 2014*/
public class Situation {

	/** nom de la situation */
	private String nom;

	/**nb d'instances*/
	private static int nbInstances = 0;

	/** no de ligne */
	private int line;
	/** no de colonne */
	private int column;

	/**indique si l'état est un état en mode Max*/
	private boolean max = true;

	/** liste des états accessibles a partir de l'état courant*/
	private ArrayList<Situation> successeurs;

	/** indique si l'état/la situation est une feuille de l'arbre */
	private boolean feuille;

	/** indique si l'état/la situation est une feuille définitive de l'arbre
	 * (i.e. impossible de créer des successeurs à cette situation) */
	private boolean close;

	/** heuristique, estimation de la valeur de la situation */
	private double heuristique;

	/** Grille de jeu correspondant a la situation */
	private int[][] matriceJeu;

	/** Constructeur par défaut */
	public Situation()
	{
		/** no de l'instance */
		int noInstances = nbInstances++;
		nom = "" + noInstances;
		heuristique = 0;
		matriceJeu = new int[3][3];
		successeurs = new ArrayList<>();
	}

	/**constructeur initialisant l'estimation h 
	 * @param _heuristique estimation de la situation*/
	public Situation(int _heuristique)
	{
		this();
		heuristique = _heuristique;
	}

	/**constructeur initialisant l'estimation h et le type de noeud
	 * @param _heuristique estimation de la situation
	 * @param _estMax determine si la valeur de la situation doit être maximisée ou non */
	public Situation(int _heuristique, boolean _estMax)
	{
		this(_heuristique);
		max = _estMax;
	}

	/**constructeur initialisant l'estimation heuristique, le type de noeud, et la position du noeud
	 * 	@param _heuristique estimation de la situation
	 * @param _estMax determine si la valeur de la situation doit être maximisée ou non
	 * @param _estFeuille determine si la situation est finale dans l'arbre
	 */
	public Situation(int _heuristique, boolean _estMax, boolean _estFeuille)
	{
		this(_heuristique, _estMax);
		feuille = _estFeuille;
	}

	/** Fonction évaluant la situation courante; calcul le 'heuristique' */
	void evaluer()
	{
		double eval = 0d;
		double coefSituation = (this.max?-1:1);
		// Les valeurs positives sont pour l'IA
		// et sont diminuées si le jeu suivant est pour l'humain
		// Elles sont augmentées sinon
		double valeur = dangerPossibles(true);
		valeur += 0.1 * coefSituation * Math.abs(valeur);
		eval += valeur;
		valeur = dangerPossibles(false);
		valeur += 0.1 * coefSituation * Math.abs(valeur);
		eval += valeur;
		valeur = dangerPossiblesDiagonale();
		valeur += 0.1 * coefSituation * Math.abs(valeur);
		eval += valeur;

		heuristique = eval;
		// afficheMatrice();
	}

	/** Fonction qui retourne la valeur d'une ligne ou colonne (attention fonctionne seulement si WIDTH = HEIGHT)
	 * @param ligne vrai si test d'une ligne, faux si test d'une colonne
	 * @return valeur du danger possible
	 * */
	private double dangerPossibles(boolean ligne)
	{
		double result = 0;
		SituationsSpeciales situation;
		// balayage des lignes
		StringBuffer strLineB = new StringBuffer();
		for(int i = 0; i < TicTacToe.HEIGHT; i++) {
			strLineB.delete(0, strLineB.length());
			for(int j = 0; j < TicTacToe.WIDTH; j++) {
				int typeJeu=(ligne?matriceJeu[i][j]:matriceJeu[j][i]);
				construireSymbole( typeJeu, strLineB);
			}
			try {
				situation = SituationsSpeciales.valueOf(strLineB.toString());
				result += situation.getValue();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		return result;
	}

	/** Construit symbole danger :
	 *  Ajoute a un string buffer un caractere en fonction du type de jeu
	 * */
	private void construireSymbole(int typeJeu, StringBuffer sb)
	{
		if (typeJeu == TypeJoueur.JOUEUR.getType())  sb.append('O');
		else
		if (typeJeu == TypeJoueur.MACHINE.getType()) sb.append('X');
		else if (typeJeu == 0)  sb.append('_');
	}

	/**
	 * Fonction qui retourne la valeur d'une diagonale
	 * */
	private double dangerPossiblesDiagonale() {
		double result = 0;
		SituationsSpeciales situation;
		// balayage des lignes
		StringBuffer strLineB = new StringBuffer();
		construireSymbole(matriceJeu[0][0], strLineB);
		construireSymbole(matriceJeu[1][1], strLineB);
		construireSymbole(matriceJeu[2][2], strLineB);
		try {
			situation = SituationsSpeciales.valueOf(strLineB.toString());
			result += situation.getValue();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		strLineB.delete(0, strLineB.length());
		construireSymbole(matriceJeu[0][2], strLineB);
		construireSymbole(matriceJeu[1][1], strLineB);
		construireSymbole(matriceJeu[2][0], strLineB);
		try {
			situation = SituationsSpeciales.valueOf(strLineB.toString());
			result += situation.getValue();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return result;
	}



	/**
	 * calcule si 3 pions sont alignes en ligne ou en colonne
	 * @param tj type du joueur dont il faut verifier s'il a trois pions alignes dans une ligne
	 * @param ligne true si test sur lignes, false si test sur colonnes
	 * @return false si pas d'alignement, true si trois pions sont alignes
	 * */
	boolean  troisPionsAlignes(TypeJoueur tj, boolean ligne)
	{
		boolean contigue = false;
		int valJoueur = tj.getType();
		// balayage des lignes
		for(int i=0; i<TicTacToe.HEIGHT && !contigue; i++)
		{
			contigue = true;
			for(int j=0; j<TicTacToe.WIDTH && contigue; j++)
			{
				int typeJeu = (ligne?matriceJeu[i][j]:matriceJeu[j][i]);
				contigue = contigue && (typeJeu == valJoueur) ;
			}
		}
		return contigue;
	}



	/**
	 * calcule si 3 pions sont alignes en diagonale
	 * @param tj type du joueur dont il faut verifier s'il a trois pions alignes dans une ligne
	 * @return false si pas d'alignement, true si trois pions sont alignes
	 * */
	boolean  troisPionsAlignesDiagonale(TypeJoueur tj)
	{
		boolean contigue = false;
		int valJoueur = tj.getType();
		for(int d=0; d<2 && !contigue; d++)
		{
			contigue = true;
			for(int j=0; j<TicTacToe.WIDTH && contigue; j++)
			{
				int i = (d==0?j:2-j);
				contigue = contigue && (matriceJeu[i][j] == valJoueur) ;
			}
		}
		return contigue;
	}

	/**
	 * calcule si 3 pions sont alignes en ligne ou en colonne ou diagonale, quelque soit le joueur
	 * @return false si pas d'alignement, true si trois pions sont alignes
	 * */
	boolean  troisPionsAlignes( )
	{
		boolean result = troisPionsAlignes( TypeJoueur.JOUEUR, false);
		result = result ||  troisPionsAlignes( TypeJoueur.JOUEUR, true);
		result = result ||  troisPionsAlignes( TypeJoueur.MACHINE, false);
		result = result ||  troisPionsAlignes( TypeJoueur.MACHINE, true);
		result = result ||  troisPionsAlignesDiagonale( TypeJoueur.JOUEUR);
		result = result ||  troisPionsAlignesDiagonale( TypeJoueur.MACHINE);
		return result;
	}
	
	/**teste si la situation possede encore un degré de liberté
	 * @return true s'il n'est plus possible de jouer*/
	boolean isFull()
	{
		boolean full = true;
		for(int i=0; i<TicTacToe.HEIGHT && full; i++)
			for(int j=0; j<TicTacToe.WIDTH && full; j++)
				full = full && (this.matriceJeu[i][j] != 0);
		return full;
	}


	/**ajoute un successeur a la situation courante
	 * @param s successeur a la situation courante*/
	public void addSuccesseur(Situation s)
	{
		successeurs.add(s);
		s.nom = nom+"."+s.nom;
	}



	/**@return vrai si l'état est une feuille */
	public boolean estFeuille(){
		return feuille || close;
	}



	/**
	 * @param successeurs Successeur à définir
	 */
	public void setSuccesseurs(ArrayList<Situation> successeurs) {
		this.successeurs = successeurs;
	}

	/**
	 * @param index index of the successeur
	 * @return the successeur no i
	 */
	public Situation getSuccesseurs(int index) {
		Situation retour = null;
		if(successeurs!=null && index<successeurs.size())
			retour = successeurs.get(index);
		return retour;
	}

	/**
	 * @return the successeurs
	 */
	public ArrayList<Situation> getSuccesseurs() {
		return successeurs;
	}

	/**
	 * @return the heuristique
	 */
	public double getHeuristique() {
		return heuristique;
	}

	/**
	 * @param heuristique the h to set
	 */
	public void setH(double heuristique) {
		this.heuristique = heuristique;
	}

	/**
	 * @return the matriceJeu
	 */
	public int[][] getMatriceJeu() {
		return matriceJeu;
	}

	/**
	 * @param matriceJeu the matriceJeu to set
	 */
	public void setMatriceJeu(int[][] matriceJeu) {
		this.matriceJeu = matriceJeu;
	}

	/**
	 * @return the estMax
	 */
	public boolean isMax() {
		return max;
	}

	/**
	 * @param estMax the estMax to set
	 */
	public void setMax(boolean estMax) {
		this.max = estMax;
	}

	public String toString()
	{
		StringBuilder retour = new StringBuilder(" S" + nom + ", heuristique = " + heuristique + ", est max = " + max + "---");
		if (!successeurs.isEmpty())
		{
			retour.append("\n::::: J'ai ").append(successeurs.size()).append(" fils :::::");
			for (Situation s:successeurs)
				retour.append(s.nom).append("(").append(s.heuristique).append(") ; ");
		}
		retour.append("\n ");
		return retour.toString();
	}

	/** Affiche la matrice associée à la situation sur la console */
	public void afficheMatrice() {
		StringBuilder retour = new StringBuilder();
		for(int i = 0; i < TicTacToe.HEIGHT; i++)
		{
			retour.append("\n|");
			for(int j = 0; j < TicTacToe.WIDTH; j++)
			{
				retour.append(matriceJeu[i][j]).append("|");
			}
		}
		retour.append("\n val=").append(heuristique).append("\n------------\n");
		System.out.println(retour);
	}


	/**
	 * @return the feuille
	 */
	public boolean isFeuille() {
		return feuille || close;
	}

	/**
	 * @param feuille the feuille to set
	 */
	public void setFeuille(boolean feuille) {
		this.feuille = feuille;
	}

	/**
	 * @return the nom
	 */
	public String getNom() {
		return nom;
	}

	/**
	 * @return the close
	 */
	public boolean isClose() {
		return close;
	}

	/**
	 * @param close the close to set
	 */
	public void setClose(boolean close) {
		this.close = close;
	}

	/**
	 * @return the line
	 */
	public int getLine() {
		return line;
	}

	/**
	 * @param line the line to set
	 */
	public void setLine(int line) {
		this.line = line;
	}

	/**
	 * @return the column
	 */
	public int getColumn() {
		return column;
	}

	/**
	 * @param column the column to set
	 */
	public void setColumn(int column) {
		this.column = column;
	}



}



