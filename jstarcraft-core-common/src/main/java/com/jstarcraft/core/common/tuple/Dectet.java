package com.jstarcraft.core.common.tuple;

/**
 * 十元
 * 
 * @author Birdy
 *
 */
public class Dectet<A, B, C, D, E, F, G, H, I, J> extends Nonet<A, B, C, D, E, F, G, H, I> {

    public Dectet(Object... datas) {
        if (datas.length != 10) {
            throw new IllegalArgumentException();
        }
        this.datas = datas;
    }

    public J getJ() {
        return (J) datas[9];
    }

    public void setJ(J data) {
        datas[9] = data;
    }

}
