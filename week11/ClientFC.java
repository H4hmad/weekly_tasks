import java.net.*;
import java.io.*;

public class ClientFC {
  public static void main(String[] args) {
    try {
      int port = 57799;
      Socket clientSocket = new Socket("127.0.0.1", port);
      PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(),
          true);
      BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

      String sendMsg = "HELO";
      writer.println(sendMsg);

      String receivedMsg = reader.readLine();

      String sendMsg2 = "AUTH xxx";
      writer.println(sendMsg2);

      String receivedMsg2 = reader.readLine();

      while (true) {
        String sendMsg7 = "REDY";
        writer.println(sendMsg7);

        String receivedMsg12 = reader.readLine();

        if (receivedMsg12.startsWith("JOBN")) {

          String[] partsx = receivedMsg12.split(" ");
          int jobIDx = Integer.parseInt(partsx[1]);
          int requiredCores = Integer.parseInt(partsx[3]);
          int requiredRAM = Integer.parseInt(partsx[4]);
          int requiredMemory = Integer.parseInt(partsx[5]);

          String sendMsg3 = "GETS Capable " + requiredCores + " " + requiredRAM + " " + requiredMemory;
          writer.println(sendMsg3);

          String receivedMsg4 = reader.readLine();
          String[] parts = receivedMsg4.split(" ");
          int number = Integer.parseInt(parts[1]);
          int cores = 0;
          int id = -5;
          String[] partstemp = new String[number];

          String sendMsg4 = "OK";
          writer.println(sendMsg4);
          int count = 0;
          for (int i = 0; i < number; i++) {
            String receivedMsg5 = reader.readLine().trim();

            if (count == 0) {
              partstemp = receivedMsg5.split(" ");
              cores = Integer.parseInt(partstemp[4]);
              id = Integer.parseInt(partstemp[1]);
            }
            count++;
          }

          String sendMsg5 = "OK";
          writer.println(sendMsg5);

          String receivedMsg9 = reader.readLine();

          String sendMsg6 = "SCHD " + jobIDx + " " + partstemp[0] + " " + id;
          writer.println(sendMsg6);

          String receivedMsg14 = reader.readLine();

        } else if (receivedMsg12.startsWith("JCPL")) {
          String sendMsg16 = "REDY";
          writer.println(sendMsg16);

          String receivedMsg17 = reader.readLine();

        } else if (receivedMsg12.startsWith("NONE")) {
          String sendMsgQuit = "QUIT";
          writer.println(sendMsgQuit);
          break;
        } else {
          break;
        }
      }

      reader.close();
      writer.close();
      clientSocket.close();
    } catch (

    IOException e) {
      System.out.println(e.getMessage());
    }
  }
}