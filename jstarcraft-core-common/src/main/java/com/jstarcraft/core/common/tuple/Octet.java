package com.jstarcraft.core.common.tuple;

/**
 * 八元
 * 
 * @author Birdy
 *
 */
public class Octet<A, B, C, D, E, F, G, H> extends Septet<A, B, C, D, E, F, G> {

    public Octet(Object... datas) {
        if (datas.length != 8) {
            throw new IllegalArgumentException();
        }
        this.datas = datas;
    }

    public H getH() {
        return (H) datas[7];
    }
    
    public void setH(H data) {
        datas[7] = data;
    }

}
