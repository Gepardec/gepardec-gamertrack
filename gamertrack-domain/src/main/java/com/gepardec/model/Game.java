package com.gepardec.model;

public class Game{
    private Long id;
    private String title;
    private String rules;

    public Game(Long id) {
        this.id = id;
    }

    public Game(Long id, String title, String rules) {
        this.id = id;
        this.title = title;
        this.rules = rules;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }
}
