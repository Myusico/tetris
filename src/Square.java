import javafx.scene.shape.Rectangle;

public class Square {
    int column;
    int row;
    Rectangle rectangle;

    public Square() {
    }

    public Square(int column, int row, double side) {
        this.column = column;
        this.row = row;
        rectangle = new Rectangle(column * side, row * side, side, side);
        rectangle.setStyle("-fx-stroke:darkgrey");
    }

    public Square(int column, int row, double side, String color) {
        this.column = column;
        this.row = row;
        rectangle = new Rectangle(column * side, row * side, side, side);
        rectangle.setStyle("-fx-stroke:black;-fx-fill:" + color);
    }

    public void move(int dColumn, int dRow) {
        column += dColumn;
        row += dRow;
        rectangle.setX(rectangle.getX() + dColumn * rectangle.getWidth());
        rectangle.setY(rectangle.getY() + dRow * rectangle.getHeight());
    }

    public void setColor(String color) {
        rectangle.setStyle("-fx-stroke:black;-fx-fill:" + color);
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public void setRectangle(Rectangle rectangle) {
        this.rectangle = rectangle;
    }
}
