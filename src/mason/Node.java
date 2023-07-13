package mason;

import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;

public class Node implements IElement {
	public final String name;
	public final Set<TransferFunction> sources = new HashSet<>();
	public final Map<Node, TransferFunction> destinations = new HashMap<>();

	public Node(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name + " = sum(" + sources.stream().map(tf -> tf.name).collect(Collectors.joining(", ")) + ")";
	}
}
