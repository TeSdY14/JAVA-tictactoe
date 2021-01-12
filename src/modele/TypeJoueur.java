package modele;

/**simple type enumere pour distinguer le joueur IA du joueur humain
 * @author emmanuel adam*/
public enum TypeJoueur {
	JOUEUR(1), MACHINE(2);
	private int type;

	TypeJoueur(int _type) {
		type = _type;
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

}
