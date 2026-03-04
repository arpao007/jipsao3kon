import java.io.Serializable;

public class GameLogic implements Serializable {

    private static final long serialVersionUID = 1L;

    private String playerName  = "";
    private String chosenGirl  = "";

    private int score     = 0;
    private int lives     = 3;
    private int day       = 1;
    private int affection = 0;

    private boolean gameStarted = false;
    private boolean gameOver    = false;

    public String getPlayerName()   { return playerName; }
    public String getChosenGirl()   { return chosenGirl; }
    public int    getScore()        { return score; }
    public int    getLives()        { return lives; }
    public int    getDay()          { return day; }
    public int    getAffection()    { return affection; }
    public boolean isGameStarted()  { return gameStarted; }
    public boolean isGameOver()     { return gameOver; }

    public void setPlayerName(String name)  { this.playerName = name; }
    public void setChosenGirl(String girlId){ this.chosenGirl = girlId; }

    public void startNewGame(String playerName, String girlId) {
        this.playerName  = playerName;
        this.chosenGirl  = girlId;
        this.score       = 0;
        this.lives       = 3;
        this.day         = 1;
        this.affection   = 0;
        this.gameStarted = true;
        this.gameOver    = false;
    }

    public void addScore(int points) {
        if (!gameOver) score += points;
    }

    public void loseLife() {
        if (!gameOver && lives > 0) {
            lives--;
            if (lives <= 0) triggerGameOver();
        }
    }

    public void increaseAffection(int amount) {
        affection = Math.min(100, affection + amount);
    }

    public void nextDay() {
        day++;
    }

    private void triggerGameOver() {
        gameOver = true;
    }

    @Override
    public String toString() {
        return String.format(
                "ผู้เล่น=%s สาว=%s คะแนน=%d ชีวิต=%d วัน=%d ความสัมพันธ์=%d",
                playerName, chosenGirl, score, lives, day, affection);
    }
}