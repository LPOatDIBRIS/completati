package lab07_03_19;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RangeTest {

    public static void main(String[] args) {
	Range r = new Range(3);

	List<Integer> l = new ArrayList<Integer>();
		for (int i =0; i<=3; i++)
		{
			l.add(i*2);
		}

	/* prints
	 * 0 0
	 * 0 1
	 * 0 2
	 * 1 0
	 * 1 1
	 * 1 2
	 * 2 0
	 * 2 1
	 * 2 2
	 */

	for (int i=0;i<l.size();i++){
		System.out.println(l.get(i));
	}

	    RangeIterator rt= r.iterator();


	    while(rt.hasNext())
		{
			int x=rt.next();
			RangeIterator rt2= r.iterator();
			while(rt2.hasNext()){
					System.out.println(x + " " + rt2.next());
			}

		}
    }
}
