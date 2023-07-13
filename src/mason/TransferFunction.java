package mason;

public class TransferFunction implements IElement {
	public final String name;
	public Node source;
	public Node destination;

	public TransferFunction(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name + "(" + this.source.name + ")";
	}
}
