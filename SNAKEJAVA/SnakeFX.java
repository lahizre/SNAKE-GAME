import javafx.animation.KeyFrame;
import java.util.List;
import java.util.ArrayList;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SnakeFX extends Application {

    private static final int WIDTH = 600;
    private static final int HEIGHT = 600;
    private static final int SNAKE_SIZE = 10;

    private Canvas canvas;
    private GraphicsContext gc;
    private Timeline timeline;
    private Snake snake;
    private Food food;

    @Override
    public void start(Stage primaryStage) {
        canvas = new Canvas(WIDTH, HEIGHT);
        gc = canvas.getGraphicsContext2D();

        timeline = new Timeline(new KeyFrame(Duration.millis(100), e -> update()));
        timeline.setCycleCount(Timeline.INDEFINITE);

        snake = new Snake();
        food = new Food();

        Scene scene = new Scene(new Group(canvas));
        scene.setOnKeyPressed(this::processKeyEvent);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Snake Game");
        primaryStage.show();

        timeline.play();
    }

    private void update() {
        snake.move();

        if (snake.hasEatenFood(food)) {
            food.relocate();
            snake.grow();
        }

        if (snake.hasHitWall()) {
            timeline.stop();
        }

        draw();
    }

    private void draw() {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        gc.setFill(Color.GREEN);
        snake.draw(gc);

        gc.setFill(Color.RED);
        food.draw(gc);
    }
    
    private void restartGame() {
        snake = new Snake();
        food.relocate();
        timeline.play();
    }

    private void processKeyEvent(KeyEvent event) {
        switch (event.getCode()) {
            case UP:
                snake.setDirection(Direction.UP);
                break;
            case DOWN:
                snake.setDirection(Direction.DOWN);
                break;
            case LEFT:
                snake.setDirection(Direction.LEFT);
                break;
            case RIGHT:
                snake.setDirection(Direction.RIGHT);
                break;
            case SPACE:
                restartGame();
                break;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    private enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    private class Snake {

        private List<Point> body;
        private Direction direction;

        public Snake() {
            body = new ArrayList<>();
            body.add(new Point(WIDTH / 2, HEIGHT / 2));
            direction = Direction.RIGHT;
        }

        public void move() {
            Point head = body.get(0);
            Point newHead = null;

            switch (direction) {
                case UP:
                    newHead = new Point(head.getX(), head.getY() - SNAKE_SIZE);
                    break;
                case DOWN:
                    newHead = new Point(head.getX(), head.getY() + SNAKE_SIZE);
                    break;
                case LEFT:
                    newHead = new Point(head.getX() - SNAKE_SIZE, head.getY());
                    break;
                case RIGHT:
                    newHead = new Point(head.getX() + SNAKE_SIZE, head.getY());
                    break;
            }

            body.add(0, newHead);

            if (body.size() > SNAKE_SIZE) {
                body.remove(body.size() - 1);
            }
        }

        public void draw(GraphicsContext gc) {
            for (Point point : body) {
                gc.fillRect(point.getX(), point.getY(), SNAKE_SIZE, SNAKE_SIZE);
            }
        }

        public boolean hasEatenFood(Food food) {
            Point head = body.get(0);
            return head.getX() == food.getX() && head.getY() == food.getY();
        }

        public boolean hasHitWall() {
            Point head = body.get(0);
            return head.getX() < 0 || head.getX() >= WIDTH || head.getY() < 0 || head.getY() >= HEIGHT;
        }

        public void setDirection(Direction direction) {
            this.direction = direction;
        }

        public void grow() {
            Point tail = body.get(body.size() - 1);
            body.add(new Point(tail.getX(), tail.getY()));
        }
    }

    private class Food {

        private Point location;

        public Food() {
            relocate();
        }

        public void relocate() {
            int x = (int) (Math.random() * (WIDTH / SNAKE_SIZE)) * SNAKE_SIZE;
            int y = (int) (Math.random() * (HEIGHT / SNAKE_SIZE)) * SNAKE_SIZE;
            location = new Point(x, y);
        }

        public void draw(GraphicsContext gc) {
            gc.fillRect(location.getX(), location.getY(), SNAKE_SIZE, SNAKE_SIZE);
        }

        public int getX() {
            return location.getX();
        }

        public int getY() {
            return location.getY();
        }
    }

    private class Point {

        private int x;
        private int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }
}