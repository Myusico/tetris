import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

import java.util.*;

import java.util.LinkedList;

public class Tetromino {
    private Square[] squares = new Square[4];
    private int type;

    public Tetromino() {
    }

    public Tetromino(int type, double side) {
        this.type = type;
        int centerX = 4;
        int centerY = 0;
        squares[0] = new Square(centerX, centerY, side);
        switch (type) {
            case 0:
                squares[1] = new Square(centerX + 1, centerY, side, "yellow");
                squares[2] = new Square(centerX, centerY + 1, side, "yellow");
                squares[3] = new Square(centerX + 1, centerY + 1, side, "yellow");
                squares[0].setColor("yellow");
                break;
            case 1:
                squares[1] = new Square(centerX - 1, centerY, side, "red");
                squares[2] = new Square(centerX, centerY + 1, side, "red");
                squares[3] = new Square(centerX + 1, centerY + 1, side, "red");
                squares[0].setColor("red");
                break;
            case 2:
                squares[1] = new Square(centerX + 1, centerY, side, "green");
                squares[2] = new Square(centerX - 1, centerY + 1, side, "green");
                squares[3] = new Square(centerX, centerY + 1, side, "green");
                squares[0].setColor("green");
                break;
            case 3:
                squares[1] = new Square(centerX - 1, centerY, side, "orange");
                squares[2] = new Square(centerX + 1, centerY, side, "orange");
                squares[3] = new Square(centerX - 1, centerY + 1, side, "orange");
                squares[0].setColor("orange");
                break;
            case 4:
                squares[1] = new Square(centerX - 1, centerY, side, "blue");
                squares[2] = new Square(centerX + 1, centerY, side, "blue");
                squares[3] = new Square(centerX + 1, centerY + 1, side, "blue");
                squares[0].setColor("blue");
                break;
            case 5:
                squares[1] = new Square(centerX - 1, centerY, side, "purple");
                squares[2] = new Square(centerX + 1, centerY, side, "purple");
                squares[3] = new Square(centerX, centerY + 1, side, "purple");
                squares[0].setColor("purple");
                break;
            case 6:
                squares[1] = new Square(centerX - 1, centerY, side, "lightseagreen");
                squares[2] = new Square(centerX + 1, centerY, side, "lightseagreen");
                squares[3] = new Square(centerX + 2, centerY, side, "lightseagreen");
                squares[0].setColor("lightseagreen");
        }
    }

    public void addTo(Pane pane) {
        for (Square square : squares) {
            pane.getChildren().add(square.getRectangle());
        }
    }

    public void removeFrom(Pane pane) {
        for (Square square : squares) {
            pane.getChildren().remove(square.getRectangle());
        }
    }

    public void move(int dColumn, int dRow) {
        for (Square square : squares) {
            square.move(dColumn, dRow);
        }
    }

    public void move(double dColumn, double dRow) {
        for (Square square : squares) {
            Rectangle rectangle = square.getRectangle();
            rectangle.setX(rectangle.getX() + dColumn * rectangle.getWidth());
            rectangle.setY(rectangle.getY() + dRow * rectangle.getHeight());
        }
    }

    public boolean fall(LinkedList<Rectangle[]> rectangles) {
        move(0, 1);
        if (isValid(rectangles)) {
            return true;
        } else {
            move(0, -1);
            for (Square square : squares) {
                rectangles.get(square.getRow())[square.getColumn()] = square.getRectangle();
            }
            return false;
        }
    }

    public void rotate(LinkedList<Rectangle[]> rectangles) {
        if (type == 0) {
            return;
        }
        int centerColumn = squares[0].getColumn();
        int centerRow = squares[0].getRow();
        double centerX = squares[0].getRectangle().getX();
        double centerY = squares[0].getRectangle().getY();
        for (int i = 1; i < 4; i++) {
            int dColumn = squares[i].getColumn() - centerColumn;
            int dRow = squares[i].getRow() - centerRow;
            squares[i].setColumn(centerColumn - dRow);
            squares[i].setRow(centerRow + dColumn);
        }
        if (isValid(rectangles)) {
            for (int i = 1; i < 4; i++) {
                double dX = squares[i].getRectangle().getX() - centerX;
                double dY = squares[i].getRectangle().getY() - centerY;
                squares[i].getRectangle().setX(centerX - dY);
                squares[i].getRectangle().setY(centerY + dX);
            }
        } else {
            for (int i = 1; i < 4; i++) {
                int dColumn = squares[i].getColumn() - centerColumn;
                int dRow = squares[i].getRow() - centerRow;
                squares[i].setColumn(centerColumn + dRow);
                squares[i].setRow(centerRow - dColumn);
            }
        }
    }

    public boolean isValid(LinkedList<Rectangle[]> rectangles) {
        for (Square square : squares) {
            int column = square.getColumn();
            int row = square.getRow();
            if (column >= 0 && column < 10 && row < 0) {
                continue;
            }
            if (column < 0 || column >= 10 || row >= 20 || rectangles.get(row)[column] != null) {
                return false;
            }
        }
        return true;
    }

    public Set<Integer> getRows() {
        Set<Integer> rows = new TreeSet<>();
        for (Square square : squares) {
            rows.add(square.getRow());
        }
        return rows;
    }

    public int getType() {
        return type;
    }
}
