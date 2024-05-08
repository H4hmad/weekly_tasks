import java.net.*;
import java.io.*;

public class ClientATL {
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

            String sendMsg2 = "AUTH 46237526";
            writer.println(sendMsg2);

            String receivedMsg2 = reader.readLine();

            String sendMsg7 = "REDY";
            writer.println(sendMsg7);

            String receivedMsg8 = reader.readLine();

            String sendMsg3 = "GETS All";
            writer.println(sendMsg3);

            String receivedMsg4 = reader.readLine();
            String[] parts = receivedMsg4.split(" ");

            int number = Integer.parseInt(parts[1]);

            int max = -1;
            int index = 0;
            int temp = 0;
            String[] x = new String[number];

            String sendMsg4 = "OK";
            writer.println(sendMsg4);

            for (int i = 0; i < number; i++) {
                String receivedMsg5 = reader.readLine().trim();
                x[i] = receivedMsg5;

                String[] partstemp = receivedMsg5.split(" ");

                temp = Integer.parseInt(partstemp[4]);

                if (max < temp) {
                    max = temp;
                    index = i;
                }
            }
            String sendMsg5 = "OK";
            writer.println(sendMsg5);

            String receivedMsg9 = reader.readLine();

            String sendMsg6 = "SCHD 0 " + x[index].split(" ")[0] + " 0";
            writer.println(sendMsg6);
            String receivedMsg14 = reader.readLine();

            int count = 0;
            while (true) {
                String sendMsg11 = "REDY";
                writer.println(sendMsg11);
                String receivedMsg12 = reader.readLine();

                if (receivedMsg12.startsWith("JOBN")) {
                    String[] jobInfo = receivedMsg12.split(" ");
                    int jobID = Integer.parseInt(jobInfo[1]);

                    String sendMsg13 = "SCHD " + jobID + " " + x[index].split(" ")[0] + " 0";
                    writer.println(sendMsg13);

                    String receivedMsg15 = reader.readLine();

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
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}