package pattern;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import parameters.Symbols;

public class MapUtil {

	public static void orderAndWrite(Map<String, Double> map, FileWriter fw, int the_top_k) throws IOException {
		List list = new ArrayList(map.entrySet());
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Map.Entry<String, Double>) (o2)).getValue()
						.compareTo(((Map.Entry<String, Double>) (o1)).getValue());
			}
		});
		int count = 0;
		for (Iterator it = list.iterator(); it.hasNext();) {
			if (count++ < the_top_k) {
				Map.Entry<String, Double> entry = (Map.Entry<String, Double>) it.next();
				fw.write(entry.getKey() + Symbols.PATTERN_SEPARATOR_IT + entry.getValue().toString() + "\n");
//				System.out.println(entry.getKey()+Symbols.PATTERN_SEPARATOR_IT+entry.getValue().toString()+"\n");
			} else {
				break;
			}
		}
	}

	public static void main(String[] args) throws IOException {
		Map<String, Double> map = new HashMap<String, Double>();
		map.put("A", 0.5);
		map.put("B", 0.3);
		map.put("CD", 0.9);
		orderAndWrite(map, null, 2);

	}
}