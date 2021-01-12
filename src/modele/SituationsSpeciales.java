package modele;

public enum SituationsSpeciales {
	XXX(10000), XX_(1000), X_X(1000), _XX(1000), X__(10), _X_(10), __X(10),
	OOO(-10000), OO_(-1000), O_O(-1000), _OO(-1000), O__(-10), _O_(-10), __O(-10);
	double value;
	SituationsSpeciales(double _value){value = _value;}
	/**
	 * @return the value
	 */
	public double getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(double value) {
		this.value = value;
	}
}
