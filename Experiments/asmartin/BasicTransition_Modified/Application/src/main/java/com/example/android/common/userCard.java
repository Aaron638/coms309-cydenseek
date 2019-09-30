package com.example.android.common;

public class userCard {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIdnum() {
        return idnum;
    }

    public void setIdnum(int idnum) {
        this.idnum = idnum;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    private int idnum, wins, losses;

    public userCard(){

    }

    public userCard(String name, int idnum, int wins, int losses){
        this.name = name;
        this.idnum = idnum;
        this.wins = wins;
        this.losses = losses;
    }


}
