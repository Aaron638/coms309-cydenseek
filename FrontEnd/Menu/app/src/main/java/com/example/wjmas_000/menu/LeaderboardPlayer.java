package com.example.wjmas_000.menu;

public class LeaderboardPlayer {

    int GamesWonHider;
    int GamesWonSeeker;
    String Username;

    public LeaderboardPlayer(int gwHider, int gwSeeker, String user){
        GamesWonHider = gwHider;
        GamesWonSeeker = gwSeeker;
        Username = user;
    }

    public void setLeaderGamesWonHider(int x){
        GamesWonHider = x;
    }
    public int getLeaderGamesWonHider(){
        return GamesWonHider;
    }

    public void setLeaderGamesWonSeeker(int x){
        GamesWonSeeker = x;
    }
    public int getLeaderGamesWonSeeker(){
        return GamesWonSeeker;
    }

    public void setLeaderUsername(String x){
        Username = x;
    }
    public String getLeaderUsername(){
        return Username;
    }

}
