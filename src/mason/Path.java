package mason;

import java.util.LinkedList;
import java.util.stream.Collectors;

public class Path extends LinkedList<Node> {
	@java.io.Serial
	private static final long serialVersionUID = 114514L;

	@Override
	public String toString() {
		return this.stream().map(node -> node.name).collect(Collectors.joining("->"));
	}

	public static String gainToString(LinkedList<TransferFunction> gains) {
		return gains.stream().map(tf -> tf.name).collect(Collectors.joining("*"));
	}

	public String gainAsString() {
		return gainToString(gain());
	}

	public LinkedList<TransferFunction> gain() {
		LinkedList<TransferFunction> result = new LinkedList<>();
		Node previousNode = null;
		for (Node node: this) {
			if (previousNode == null) {
				previousNode = node;
				continue;
			}

			TransferFunction destTF = previousNode.destinations.get(node);
			if (destTF != null) {
				result.add(destTF);
			}
			previousNode = node;
		}
		return result;
	}
}
