package mason;

import java.util.List;
import java.util.LinkedList;
import java.util.Comparator;

public class Loop extends Path {
	@java.io.Serial
	private static final long serialVersionUID = 114514114514L;
	// Node0->Node1->...->NodeN->Node0

	// Sort the nodes according to ASCII
	public Loop(List<Node> nodes) {
		List<Node> wipNodes = new LinkedList<>();
		Node startNode = getSmallestNode(nodes);
		wipNodes.addAll(nodes);
		wipNodes.addAll(nodes);

		boolean foundStartNode = false;
		for (Node node: wipNodes) {
			if (foundStartNode) {
				this.add(node);
				if (node == startNode) {
					break;
				}
			} else {
				if (node == startNode) {
					foundStartNode = true;
					this.add(node);
				}
			}
		}
	}

	public boolean touches(Loop loop) {
		for (Node node: this) {
			if (loop.contains(node)) {
				return true;
			}
		}

		return false;
	}

	@Override
    public boolean equals(Object obj) {
		if (!(obj instanceof Loop)) {
			return false;
		}

        return areLoopsEqual(this, (Loop)obj);
    }

	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}

	private final static Comparator<Node> nodeComparator = new Comparator<>() {
        @Override
        public int compare(Node node1, Node node2) {
            return node1.name.compareTo(node2.name);
        }
    };

	public static Node getSmallestNode(List<Node> nodes) {
		return nodes.stream().sorted(nodeComparator).findFirst().get();
	}

	public static boolean areLoopsEqual(List<Node> loop1, List<Node> loop2) {
		return loop1.containsAll(loop2);
	}
}
