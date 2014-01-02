package jp.tetra2000.droptweet.twitter;

public class Account {
    public String name, token, secret;
    public Account(String name, String token, String secret) {
        this.name = name;
        this.token = token;
        this.secret = secret;
    }
}
