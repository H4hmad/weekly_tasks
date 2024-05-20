import java.io.*;
import java.net.*;

public class ClientInnovative {
  private static final String SERVER_ADDRESS = "127.0.0.1";
  private static final int PORT = 57799;

  public static void main(String[] args) {
    try {
      Socket clientSocket = new Socket(SERVER_ADDRESS, PORT);
      PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
      BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

      sendMessage(writer, "HELO");
      String receivedMsg = receiveMessage(reader);

      sendMessage(writer, "AUTH 46237526");
      String receivedMsg2 = receiveMessage(reader);

      while (true) {
        String sendMsg7 = "REDY";
        writer.println(sendMsg7);
        String receivedMsg12 = receiveMessage(reader);

        if (receivedMsg12.startsWith("JOBN")) {
          processJob(reader, writer, receivedMsg12);
        } else if (receivedMsg12.startsWith("JCPL")) {
          sendMessage(writer, "REDY");
          receiveMessage(reader);
        } else if (receivedMsg12.startsWith("NONE")) {
          sendMessage(writer, "QUIT");
          break;
        } else {
          break;
        }
      }

      closeResources(reader, writer, clientSocket);
    } catch (IOException e) {
      System.err.println("Error: " + e.getMessage());
    }
  }

  private static void processJob(BufferedReader reader, PrintWriter writer, String jobMessage) throws IOException {
    String[] parts = jobMessage.split(" ");
    int jobID = Integer.parseInt(parts[1]);
    int requiredCores = Integer.parseInt(parts[3]);
    int requiredRAM = Integer.parseInt(parts[4]);
    int requiredMemory = Integer.parseInt(parts[5]);

    sendMessage(writer, "GETS Capable " + requiredCores + " " + requiredRAM + " " + requiredMemory);
    String receivedMsg = receiveMessage(reader);
    String[] partsTemp = receivedMsg.split(" ");
    int number = Integer.parseInt(partsTemp[1]);
    String[] bestFit = findBestFit(reader, writer, number);
    String receivedMsgx = receiveMessage(reader);
    sendMessage(writer, "SCHD " + jobID + " " + bestFit[0] + " " + bestFit[1]);
    receiveMessage(reader);
  }

  private static String[] findBestFit(BufferedReader reader, PrintWriter writer, int number) throws IOException {
    int maxCores = Integer.MIN_VALUE;
    String[] bestFit = new String[2];

    sendMessage(writer, "OK");
    for (int i = 0; i < number; i++) {
      String receivedMsg = receiveMessage(reader);
      String[] parts = receivedMsg.split(" ");
      int availableCores = Integer.parseInt(parts[4]);
      if (availableCores > maxCores) {
        maxCores = availableCores;
        bestFit[0] = parts[0];
        bestFit[1] = parts[1];
      }
    }
    sendMessage(writer, "OK");

    return bestFit;
  }

  private static String receiveMessage(BufferedReader reader) throws IOException {
    return reader.readLine().trim();
  }

  private static void sendMessage(PrintWriter writer, String message) {
    writer.println(message);
  }

  private static void closeResources(BufferedReader reader, PrintWriter writer, Socket clientSocket)
      throws IOException {
    reader.close();
    writer.close();
    clientSocket.close();
  }
}
