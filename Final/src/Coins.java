import java.awt.*;
class Coins extends GameEntity {
    public Coins(int x, int y, Image image) {
        super(x, y, image);
    }

    @Override
    public void render(Graphics g, int size) {
        g.drawImage(image, x * size, y * size, size, size, null);
    }
}
