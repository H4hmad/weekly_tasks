import java.net.*;
import java.io.*;

public class ClientEfficient {
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

      while (true) {
        writer.println("REDY");
        String receivedMsg12 = reader.readLine();

        if (receivedMsg12.startsWith("JOBN")) {
          String[] partsx = receivedMsg12.split(" ");
          int jobIDx = Integer.parseInt(partsx[1]);
          int requiredCores = Integer.parseInt(partsx[3]);
          int requiredRAM = Integer.parseInt(partsx[4]);
          int requiredMemory = Integer.parseInt(partsx[5]);

          // Send a GETS command to query available servers
          writer.println("GETS All " + requiredCores + " " + requiredRAM + " " + requiredMemory);
          String receivedMsg4 = reader.readLine();
          String[] parts = receivedMsg4.split(" ");
          int number = Integer.parseInt(parts[1]);

          // Find the server with the least workload
          int minWorkload = Integer.MAX_VALUE;
          String selectedServer = "";
          for (int i = 0; i < number; i++) {
            String receivedMsg5 = reader.readLine();
            String[] serverInfo = receivedMsg5.split(" ");
            int workload = Integer.parseInt(serverInfo[7]) + Integer.parseInt(serverInfo[8]); // Sum of waiting and
                                                                                              // running jobs
            if (workload < minWorkload) {
              minWorkload = workload;
              selectedServer = serverInfo[0] + " " + serverInfo[1]; // Server type and ID
            }
          }

          // Schedule the job on the selected server
          writer.println("SCHD " + jobIDx + " " + selectedServer);
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
