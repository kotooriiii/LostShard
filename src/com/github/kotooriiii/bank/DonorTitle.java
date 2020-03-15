package com.github.kotooriiii.bank;

public enum DonorTitle {

    BASE(27),
    SECOND(27*2);

    private int size;

    DonorTitle(final int size)
    {
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public static int getMaxSize()
    {
        return DonorTitle.values()[DonorTitle.values().length-1].getSize();
    }
}
