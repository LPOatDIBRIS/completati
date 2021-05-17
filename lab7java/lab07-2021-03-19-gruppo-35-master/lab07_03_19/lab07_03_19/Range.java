package lab07_03_19;

import java.util.Iterator;

public class Range implements Iterable<Integer> {

    // aggiungere i campi necessari
    private final int start;
    private final int end;

    // ranges from start (inclusive) to end (exclusive)
    public Range(int start, int end) {
	this.start=start;
	this.end=end;
    }

    // ranges from 0 (inclusive) to end (exclusive)
    public Range(int end) {
	    this(0, end);
    }

    @Override
    public RangeIterator iterator() {
	return new RangeIterator(start, end);
    }

}
