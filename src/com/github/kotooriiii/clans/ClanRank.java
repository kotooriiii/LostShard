package com.github.kotooriiii.clans;

public enum ClanRank {
    /*
    The order below matters
    In ascending order of rank.
     */
    MEMBER("Member"), COLEADER("Co-Leader"), LEADER("Leader");

    private String value;

    ClanRank(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.getValue();
    }
}


