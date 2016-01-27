package common;

public class IntPair {

    public final int a, b;

    public IntPair(int a, int b) {
	this.a = a;
	this.b = b;
    }

    public int hashCode() {
	return a * 82483721 + b * 274837;
    }

    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (!(obj instanceof IntPair))
	    return false;
	IntPair other = (IntPair) obj;
	return this.a == other.a && this.b == other.b;
    }

}