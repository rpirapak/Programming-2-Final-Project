import java.io.*;
public class GameData {
    public void saveGame(int score, int xPos, int yPos) {
        try {
            FileWriter fileWriter = new FileWriter("Final/SaveFile/game_save.txt");
            BufferedWriter writer = new BufferedWriter(fileWriter);

            writer.write("Score: " + score);
            writer.newLine();
            writer.write("Position: " + xPos + "," + yPos);
            writer.newLine();
            writer.write("Highest Score: " + score);

            writer.close();

            System.out.println("Game saved successfully!");
        } catch (IOException e) {
            System.out.println("Error saving game: " + e.getMessage());
        }
    }

    public void saveHighestScore(int highestScore) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Final/SaveFile/game_save.txt", true))) {
            writer.newLine();
            writer.write("Highest Score: " + highestScore);
        } catch (IOException e) {
            System.out.println("Error saving highest score: " + e.getMessage());
        }
    }

    public int loadHighestScore() {
        int highestScore = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader("Final/SaveFile/game_save.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Highest Score:")) {
                    highestScore = Integer.parseInt(line.split(": ")[1]);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading highest score: " + e.getMessage());
        }
        return highestScore;
    }
}
