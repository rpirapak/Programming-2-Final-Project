import java.awt.*;
public class Enemy extends GameEntity {
    public Enemy(int x, int y, Image image) {
        super(x, y, image);
    }

    @Override
    public void render(Graphics g, int size) {
        g.drawImage(image, x * size, y * size, size, size, null);
    }
}
