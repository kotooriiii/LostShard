package com.github.kotooriiii.guards;

public enum Skin {
    GUARD("eyJ0aW1lc3RhbXAiOjE1ODQyNDQxMzM3MjMsInByb2ZpbGVJZCI6ImQyNDEzM2MwMGJlYTQ5OTFhZDJkNDRkZjNhYTg0MzUzIiwicHJvZmlsZU5hbWUiOiJTaGVsdmllIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS80NjEyNmE2Zjk5MmFlODhiNjAzM2JjMjEyY2ExMTJmZjNkZDNmNzI3ZWFhN2VkOTFjNzU0ZTY5NTViMDFjYjUwIn19fQ==",
            "kj6Av73Ai/lZ50GeoUmCSbH8S2uKauTiOpkm5zVBYlh43zTbOGu+hxsg6E170MfmbqcZfQmAX6CxhunwZDdWn2YVXXIn0AyKtY3NUsxGKeYJD9PHVIluewrrUXMyNFXJfNBYY1QTNjtHf0gaBtfwQ4+XzmfJFtBlivJOwZ8GR9070Nc29ZtwibRVP6uIYSK1cJhCiCN0klzM4w6mPaURlQPeIXZZVSQhJLKrIIkIbNYL4DCe+FqSG/VsYY3+KXM1qGSvybUwsEnlx330ia3MAr+XqIrZmDbSf0Fl+q4lOePQAYSEDcqkUWChpQEknY2NRUJnoDzOM4V7pGTQ9osiCxrTSkkvVQfvlb0ZZp+weMe1PndLpXHTGJA/rzpWSykEO+KnHFep1JJZYpZ0fxHM7rf2vbG7XsPF3GRmzXn3iwmYeS3ahyeHRuboI8/tKnmhs3cyRchECZyAMZV4NuSPX3HdlO8nln8KgG7KPwCadBReJO59rLXT/zCSttts2uKQSmmEcoS/iUBRcmU+shS+Ew+foUNCZLiyxaN7YQ63YEdIU/5+lrCtGj1P0lJKuDF5n/QOziQqXtEiX0tfLKCSOj6nS5gWudBS6zC1E0UXvqGGm4Yvg6crmdSGSDUm9dDEIjeufAywcrGQTC4zl7Qtr6LgpC8WNF60DMbfMVbDiCM="),
    BANKER("","");

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
