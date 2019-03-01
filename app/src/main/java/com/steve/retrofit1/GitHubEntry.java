package com.steve.retrofit1;

public class GitHubEntry {
    private int id;
    private String login;

    public GitHubEntry(int id, String login) {
        this.id = id;
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLogin(String login) {
        this.login = login;
    }
}
