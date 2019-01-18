import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws Exception {
//        System.out.println("Enter the IP address of a machine running the capitalize server:");
        String serverAddress = "129.97.7.120";//new Scanner(System.in).nextLine();
        Socket socket = new Socket(serverAddress, 9898);

        // Streams for conversing with server
         BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
         PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        // Consume and display welcome message from the server
        System.out.println(in.readLine());

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nEnter a string to send to the server (empty to quit):");
            String message = scanner.nextLine();
            if (message == null || message.isEmpty()) {
                break;
            }
            out.println(message);
            System.out.println(in.readLine());
        }
    }
}

