package mason;

import java.util.*;

public class Mason {
	// The Graph
	public final Collection<Node> nodes;
	public final Collection<TransferFunction> transferFunctions;

	// Input and output
	public Node input, output;

	// States
	public List<Path> forwardPaths = new LinkedList<>();
	public HashSet<Loop> loops = new HashSet<>();
	public List<AbstractMap.SimpleEntry<Loop, Loop>>[] nonTouchingLoops = null;

	public Mason(String path) throws Exception {
		HashMap<Node, String[]> nodeSources = new HashMap<>();
		HashMap<TransferFunction, String> tfSources = new HashMap<>();
		Set<String> inputs = new HashSet<>();	// Output TF names
		Set<String> outputs = new HashSet<>();	// Output TF names

    	TextFileParser.parse(path, (literals) -> {
    		switch (literals[0].toLowerCase()) {
    		case "node":
    			nodeSources.put(new Node(literals[1]), Arrays.copyOfRange(literals, 2, literals.length));
    			break;
    		case "tf":
    			tfSources.put(new TransferFunction(literals[1]), literals[2]);
    			break;
    		case "io":
    			inputs.add(literals[1]);
    			outputs.add(literals[2]);
    			break;
    		default:
    			System.out.print("Unknown statement:" + literals[1]);
    			break;
    		}

            // Print the literals
//            for (String arg : literals) {
//                System.out.print(arg);
//                System.out.print(", ");
//            }
//            System.out.println();
    	});

    	HashMap<String, Node> nodes = new HashMap<>();
    	HashMap<String, TransferFunction> transferFunctions = new HashMap<>();
    	nodeSources.forEach((node, sourceNames) -> nodes.put(node.name, node));
    	tfSources.forEach((tf, sourceName) -> transferFunctions.put(tf.name, tf));

    	nodeSources.forEach((node, sourceNames) -> {
    		for (String sourceName: sourceNames) {
    			TransferFunction tf = transferFunctions.get(sourceName);
    			node.sources.add(tf);
    			tf.destination = node;
    		}
    	});

    	tfSources.forEach((tf, sourceName) -> {
    		Node node = nodes.get(sourceName);
    		tf.source = node;
    		node.destinations.put(tf.destination, tf);
    	});

    	if (outputs.size() > 1) {
    		throw new Exception("More than 1 output present!");
    	} else if (outputs.size() == 0) {
    		throw new Exception("No output speficied!");
    	}

    	if (inputs.size() > 1) {
    		throw new Exception("More than 1 input present!");
    	} else if (inputs.size() == 0) {
    		throw new Exception("No input speficied!");
    	}

    	this.nodes = nodes.values();
    	this.transferFunctions = transferFunctions.values();
    	this.input = nodes.get(inputs.toArray()[0]);
    	this.output = nodes.get(outputs.toArray()[0]);
	}

	public void updateForwardPathAndLoops() {
		this.forwardPaths.clear();
		this.loops.clear();

		Deque<Path> possiblePaths = new LinkedList<>();
		Path firstPath = new Path();
		firstPath.add(this.input);
		possiblePaths.add(firstPath);

		while (!possiblePaths.isEmpty()) {
			Path currentPath = possiblePaths.pop();
			Node currentNode = currentPath.getLast();
			if (currentNode == this.output) {
				this.forwardPaths.add(currentPath);
			}

			for (TransferFunction destTF: currentNode.destinations.values()) {
				Node head = destTF.destination;
				Path newPath = new Path();
				newPath.addAll(currentPath);
				newPath.add(head);
				if (currentPath.contains(head)) {
					// Loop detected
					// System.out.println("Raw:" + pathToString(newPath));

					// Extract the loop part
					Node tail = null;
					do {
						tail = newPath.pop();
					} while (tail != head);

					// System.out.println(newPath.getLast().name + "  " + pathToString(newPath));
					Loop loop = new Loop(newPath);
					// System.out.println(loop.toString());
					this.loops.add(loop);
				} else {
					possiblePaths.push(newPath);
				}
			}
		}
	}

	private List<AbstractMap.SimpleEntry<Loop, Loop>> findNonTouchingPairs() {
		List<AbstractMap.SimpleEntry<Loop, Loop>> result = new LinkedList<>();
		HashSet<Loop> visited = new HashSet<>();
		for (Loop loop: this.loops) {
			visited.add(loop);
			for (Loop loopCmp: this.loops) {
				if (!visited.contains(loopCmp) && !loop.touches(loopCmp)) {
					result.add(new AbstractMap.SimpleEntry<>(loop, loopCmp));
				}
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public void updateNonTouchingLoops() {
		this.nonTouchingLoops = new List[this.loops.size() - 1];

		// All possible two non-touching loops
		this.nonTouchingLoops[0] = this.findNonTouchingPairs();

		// TODO: Fix three non-touching and more
		for (int i=1; i<this.nonTouchingLoops.length; i++) {
			this.nonTouchingLoops[i] = new LinkedList<>();



		}
	}
}
