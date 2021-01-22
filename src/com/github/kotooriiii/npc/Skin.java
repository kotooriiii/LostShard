package com.github.kotooriiii.npc;

public enum Skin {
    GUARD("Shelvie","eyJ0aW1lc3RhbXAiOjE1ODQyNDQxMzM3MjMsInByb2ZpbGVJZCI6ImQyNDEzM2MwMGJlYTQ5OTFhZDJkNDRkZjNhYTg0MzUzIiwicHJvZmlsZU5hbWUiOiJTaGVsdmllIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS80NjEyNmE2Zjk5MmFlODhiNjAzM2JjMjEyY2ExMTJmZjNkZDNmNzI3ZWFhN2VkOTFjNzU0ZTY5NTViMDFjYjUwIn19fQ==",
            "kj6Av73Ai/lZ50GeoUmCSbH8S2uKauTiOpkm5zVBYlh43zTbOGu+hxsg6E170MfmbqcZfQmAX6CxhunwZDdWn2YVXXIn0AyKtY3NUsxGKeYJD9PHVIluewrrUXMyNFXJfNBYY1QTNjtHf0gaBtfwQ4+XzmfJFtBlivJOwZ8GR9070Nc29ZtwibRVP6uIYSK1cJhCiCN0klzM4w6mPaURlQPeIXZZVSQhJLKrIIkIbNYL4DCe+FqSG/VsYY3+KXM1qGSvybUwsEnlx330ia3MAr+XqIrZmDbSf0Fl+q4lOePQAYSEDcqkUWChpQEknY2NRUJnoDzOM4V7pGTQ9osiCxrTSkkvVQfvlb0ZZp+weMe1PndLpXHTGJA/rzpWSykEO+KnHFep1JJZYpZ0fxHM7rf2vbG7XsPF3GRmzXn3iwmYeS3ahyeHRuboI8/tKnmhs3cyRchECZyAMZV4NuSPX3HdlO8nln8KgG7KPwCadBReJO59rLXT/zCSttts2uKQSmmEcoS/iUBRcmU+shS+Ew+foUNCZLiyxaN7YQ63YEdIU/5+lrCtGj1P0lJKuDF5n/QOziQqXtEiX0tfLKCSOj6nS5gWudBS6zC1E0UXvqGGm4Yvg6crmdSGSDUm9dDEIjeufAywcrGQTC4zl7Qtr6LgpC8WNF60DMbfMVbDiCM="),
    BANKER("Nickolov","eyJ0aW1lc3RhbXAiOjE1ODQ1Njg3NDkxMTQsInByb2ZpbGVJZCI6IjQ4Yzk4ZmVhM2ZhYTQ4YWI4NWE0YmNmN2U2NDE1MjFjIiwicHJvZmlsZU5hbWUiOiJOaWNrb2xvdiIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmU5NjA0MDU4MTg5NjMyMTUxZDRjNWM5OGFkZTQwZWE4NWU3NjU2MGI3MmIyZDdmMDRlMzkzOGE4MDIyMDczNyJ9fX0=",
    "Xsepuctfy0yeSCfskxj9rCm0Ele5QUifkUhEIenEiLYZeSqf2cZGxZr29SKubfbriZtahxuZtjHKZ99MZ5UycWYItmUNp2LT5jW4t+4tUlzYSdG9wdl7ODfU6A8mPxejq0P+FNrlfJk2kbNBcKBbsY0sx1a3eSeSGLlNhDXbYCklMUOXkLoGSYMOqDmz8kcFdTLpaS/apMv7CLhMEh9WGzQyY8HvC8+D/4ayKpdQxuJWKvQtbA6mqKgtR+N6dcgWBB/uGOfh8h129yB/haLF4mLbwOSbK2jtfhPTNlm6te/z8AFafSrcYGxTx61zBSohrgjW+x8E0n8coS2C/TQuZ3U7JFg1xDyNRHZPmsoB497qwhE4a7tQjxBS0L8BjZqmEKJHfzmx4p8s44Rbs3haSEp53kUlmJSy4V519HwwqswG2ZILjgw2xKbZZ4EdAnlYm8IsmB20+tYLXy+LGKpGAn9YP/Z+KUtqps7wbWDUIgXNkLT4dU7oyjBApGHX3y2awt4XVqfxBmjC+OKtc4Q+hnbecUMJSA8LgaBgdnMV39QUNfS086xww7Bh+ZA/GVLoVOQ2YDwY6POLB9uLpg8tFqXCSynekFKsVORzWCnY1XsctaaxnZNEjI30Tc7ZloDhnvr+csTi+V3XrqLUqY8QkidNI2FtSUbDibp6xduG5wg=");

    private String name;
    private String texture;
    private String signature;

    Skin(final String name, final String texture, final String signature) {
        this.name = name;
        this.texture = texture;
        this.signature = signature;
    }

    public String getName()
    {
        return name;
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
