import java.util.List;
import java.util.AbstractMap;
import mason.*;

public class TestEntry {
    public static void main(String[] args) throws Exception {
    	Mason mason = new Mason("input.txt");
    	mason.updateForwardPathAndLoops();

    	System.out.println();
    	System.out.println("Forward paths:");
    	mason.forwardPaths.forEach(path -> System.out.println(path.toString() + " = " + path.gainAsString()));

    	System.out.println();
    	System.out.println("Loops:");
    	mason.loops.forEach(loop -> System.out.println(loop + " = " + loop.gainAsString()));

    	mason.updateNonTouchingLoops();
    	for (int i=0; i<mason.nonTouchingLoops.length; i++) {
    		List<AbstractMap.SimpleEntry<Loop, Loop>> nonTouchingLoops = mason.nonTouchingLoops[i];
        	System.out.println();
        	System.out.println("Non-touching Loops of " + (i+2) + " :");
        	nonTouchingLoops.forEach((pair) -> System.out.println(pair.getKey().toString() + " = " + pair.getKey().gainAsString() + " <---> " + pair.getValue().toString() + " = " + pair.getValue().gainAsString()));
    	}
    	return;
    }

}
