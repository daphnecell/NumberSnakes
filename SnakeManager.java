public class SnakeManager {
    public SingleLinkedList snakes;
    private enigma.console.Console cn;

    public SnakeManager(enigma.console.Console console) {
        this.snakes = new SingleLinkedList();
        this.cn = console;
    }

    public void addSnake(int x, int y) {
        Snake newSnake = new Snake(x, y, cn);
        snakes.addNode(newSnake);
    }

    public void moveAllSnakes() {
        Node current = snakes.head;
        while (current != null) {
            Snake snake = (Snake) current.getData();
            snake.move();
            current = current.getLink();
        }

        checkAllCollisions();
    }

    private void checkAllCollisions() {
        Node node1 = snakes.head;
        SingleLinkedList snakesToRemove = new SingleLinkedList();

        while (node1 != null) {
            Snake snake1 = (Snake) node1.getData();

            Node node2 = node1.getLink();
            while (node2 != null) {
                Snake snake2 = (Snake) node2.getData();

                if (snake1.collidesWith(snake2)) {
                    Snake.handleHeadToHeadCollision(snake1, snake2);
                    snakesToRemove.addNode(snake1);
                    snakesToRemove.addNode(snake2);
                    break;
                }

                int hitValue1 = snake1.checkBodyCollision(snake2);
                if (hitValue1 != -1) {
                    snake1.handleBodyCollision(snake2, hitValue1);
                    if (hitValue1 == 1) {
                        snakesToRemove.addNode(snake1);
                    }
                    break;
                }

                int hitValue2 = snake2.checkBodyCollision(snake1);
                if (hitValue2 != -1) {
                    snake2.handleBodyCollision(snake1, hitValue2);
                    if (hitValue2 == 1) {
                        snakesToRemove.addNode(snake2);
                    }
                    break;
                }

                node2 = node2.getLink();
            }

            node1 = node1.getLink();
        }

        Node removeNode = snakesToRemove.head;
        while (removeNode != null) {
            Snake snakeToRemove = (Snake) removeNode.getData();
            removeSnake(snakeToRemove);
            removeNode = removeNode.getLink();
        }
    }

    public void checkSnakeTrapCollisions(int[][] activeTrapLocations, int activeTrapCount) {
        Node current = snakes.head;
        SingleLinkedList snakesToRemove = new SingleLinkedList();

        while (current != null) {
            Snake snake = (Snake) current.getData();

            if (snake.isTrapped(activeTrapLocations, activeTrapCount)) {
                snake.destroy();
                snakesToRemove.addNode(snake);
            }

            current = current.getLink();
        }

        Node removeNode = snakesToRemove.head;
        while (removeNode != null) {
            Snake snakeToRemove = (Snake) removeNode.getData();
            removeSnake(snakeToRemove);
            removeNode = removeNode.getLink();
        }
    }

    public void checkPlayerDamage(int playerX, int playerY) {
        Node current = snakes.head;
        int damage = 0;

        while (current != null) {
            Snake snake = (Snake) current.getData();

            if (snake.isAdjacentToPlayer(playerX, playerY)) {
                damage += 1;
            }

            current = current.getLink();
        }

        if (damage > 0) {
        }
    }

    public int getSnakeCount() {
        return snakes.size();
    }

    public void removeSnake(Snake snakeToRemove) {
        Node current = snakes.head;
        Node previous = null;

        while (current != null) {
            Snake currentSnake = (Snake) current.getData();

            if (currentSnake.getSnakeId() == snakeToRemove.getSnakeId()) {
                if (previous == null) {
                    snakes.head = current.getLink();
                } else {
                    previous.setLink(current.getLink());
                }
                return;
            }

            previous = current;
            current = current.getLink();
        }
    }

    public void removeAllSnakes() {
        Node current = snakes.head;
        while (current != null) {
            Snake snake = (Snake) current.getData();
            snake.removeFromMaze();
            current = current.getLink();
        }
        snakes.head = null;
    }

    public boolean hasSnakes() {
        return snakes.head != null;
    }

    public Snake getSnakeAt(int x, int y) {
        Node current = snakes.head;
        while (current != null) {
            Snake snake = (Snake) current.getData();
            if (snake.isAt(x, y)) {
                return snake;
            }
            current = current.getLink();
        }
        return null;
    }

    public void printAllSnakes() {
        Node current = snakes.head;
        System.out.print("Active snakes: ");
        while (current != null) {
            Snake snake = (Snake) current.getData();
            System.out.print("Snake#" + snake.getSnakeId() + " ");
            current = current.getLink();
        }
        System.out.println();
    }
}