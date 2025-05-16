import java.util.LinkedList;
public class SnakeNode {
    private Object data;
    private SnakeNode link;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public SnakeNode getLink() {
        return link;
    }

    public void setLink(SnakeNode link) {
        this.link = link;
    }

    public SnakeNode(Object dataToAdd) {
        data = dataToAdd;
        link = null;
    }
}
