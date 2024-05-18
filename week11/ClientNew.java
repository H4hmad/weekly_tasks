import java.net.*;
import java.io.*;

public class ClientNew {
  public static void main(String[] args) {
    try {
      int port = 57799;
      Socket clientSocket = new Socket("127.0.0.1", port);
      PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
      BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

      // Initial messages
      writer.println("HELO");
      String receivedMsg = reader.readLine();

      writer.println("AUTH xxx");
      String receivedMsg2 = reader.readLine();

      // Main loop
      while (true) {
        writer.println("REDY");
        String receivedMsg12 = reader.readLine();

        if (receivedMsg12.startsWith("JOBN")) {
          String[] partsx = receivedMsg12.split(" ");
          int jobIDx = Integer.parseInt(partsx[1]);
          int requiredCores = Integer.parseInt(partsx[3]);
          int requiredRAM = Integer.parseInt(partsx[4]);
          int requiredMemory = Integer.parseInt(partsx[5]);

          writer.println("GETS Capable " + requiredCores + " " + requiredRAM + " " + requiredMemory);
          String receivedMsg4 = reader.readLine();
          String[] parts = receivedMsg4.split(" ");
          int number = Integer.parseInt(parts[1]);
          String[] partstemp = null;
          int cores = 0;
          int id = -5;

          writer.println("OK");
          for (int i = 0; i < number; i++) {
            String receivedMsg5 = reader.readLine();
            if (i == 0) {
              partstemp = receivedMsg5.split(" ");
              cores = Integer.parseInt(partstemp[4]);
              id = Integer.parseInt(partstemp[1]);
            }
          }
          writer.println("OK");
          reader.readLine(); // Discard OK message

          writer.println("SCHD " + jobIDx + " " + partstemp[0] + " " + id);
          reader.readLine(); // SCHD response

        } else if (receivedMsg12.startsWith("JCPL")) {
          writer.println("REDY");
          reader.readLine(); // Discard REDY message

        } else if (receivedMsg12.startsWith("NONE")) {
          writer.println("QUIT");
          break;
        } else {
          break;
        }
      }

      // Cleanup
      reader.close();
      writer.close();
      clientSocket.close();
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }
}
