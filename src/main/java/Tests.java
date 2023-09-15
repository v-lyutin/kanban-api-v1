import server.HttpTaskServer;
import java.io.IOException;

public class Tests {
    public static void main(String[] args) throws IOException {
        new HttpTaskServer().start();
    }
}

