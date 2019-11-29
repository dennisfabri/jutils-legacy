/*
 * Created on 02.08.2005
 */
package de.df.jutils.util;

import java.util.BitSet;

public final class BitArray {

    private BitArray() {
        super();
    }

    public static BitSet create(int bits, long data) {
        BitSet bitset = new BitSet(bits);
        for (int x = 0; x < bits; x++) {
            bitset.set(x, (data % 2) > 0);
            data = data >> 1;
        }
        return bitset;
    }
}