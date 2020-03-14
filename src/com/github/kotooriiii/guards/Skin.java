package com.github.kotooriiii.guards;

public enum Skin {
    GUARD("kr4ZSnFWaEevyj5yTFTOMNHDC1qy152p1GLTY+18W3vCymsUdifOF3dvvjaK+45M3Ghc/6+snEaL6dUt+Fc3X5/kLuT8avV5x" +
            "XVioCbyUxTc6yw3nu+JUTas5zQ3vzASQLORhRFwo57x/Nx6sqh2dko9ZIjX9A0pbAAHmZSae9rJCvA9Qz9Ev3Ak1gq24dY4pJjRut0iT9lXyJ7I8refb5lme1SsUV" +
            "/xEN9yDdmIVOdXSZjJpEI8bmpjgB/AA5eSojtp/tFc0FD7/CP2a8eYJtxqqMl5CBZROPKvgt/nJJErbW9lM+hp6V+aCDKn1Hcj/TIrayYZT4AmKLJrHFxibNayrENmhcRW1GT55wWxczazQLMP1H1I9h" +
            "+e9KrFZuB4RwHYgpYSaUmlktL1axV9so6C523OOdE22ZIY2sTP58sh1iQFYq35KXiy162O0Rt73stqzve2IoYLhjnaoRmopuhbBZbOuPceCsOg/" +
            "RkjTfiSc7bvIe5/6ZI7GSrCoCbTxjqWFyqgR2IwY25S2tXiwjwTzAxZeNeYt2wOA7EMRJPT5XIAVFuwa1f/bn9skX5uVrBgTpRxw2h/QNV2BoqgDUEg91vavkKXUlZS8PWYHJvvhg3N2d0dNAGqjm5nnYP4V3oqjiJfdSSi" +
            "+N4hpB1DLUBqKAOhqMy6SQ+QzF4IOb4=", "eyJ0aW1lc3RhbXAiOjE1ODQwNzExNDc5MzgsInByb2ZpbGVJZCI6ImQyNDEzM2MwMGJlYTQ5OTFhZDJkNDRkZjNhYTg0MzUzIiwicHJvZmlsZU5hbWUiOiJTaGVsdmllIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQv" +
            "dGV4dHVyZS80NjEyNmE2Zjk5MmFlODhiNjAzM2JjMjEyY2ExMTJmZjNkZDNmNzI3ZWFhN2VkOTFjNzU0ZTY5NTViMDFjYjUwIn19fQ==");



    private String texture;
    private String signature;

    Skin(final String texture, final String signature) {
        this.texture = texture;
        this.signature = signature;
    }

    public String getSignature() {
        return signature;
    }

    public String getTexture() {
        return texture;
    }

    @Override
    public String toString() {
        return getTexture() + " " + getSignature();
    }
}
