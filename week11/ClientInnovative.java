import java.io.*;
import java.net.*;

public class ClientInnovative {

  // Server address and port number constants
  private static final String SERVER_ADDRESS = "127.0.0.1";
  private static final int PORT = 57799;

  public static void main(String[] args) {
    try {

      // Establishing connection with the server
      Socket clientSocket = new Socket(SERVER_ADDRESS, PORT);

      // Create PrintWriter and BufferedReader for sending and receiving messages
      PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
      BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

      // Send initial message to the server
      sendMessage(writer, "HELO");

      // Receive server's greetings response
      String greetingsResponse = receiveMessage(reader);

      // Send authentication message to the server
      sendMessage(writer, "AUTH 46237526");

      // Receive authentication response from the server
      String authenticationResponse = receiveMessage(reader);

      // Continuous communication with the server
      // (for job scheduling)
      while (true) {

        // Request job details from the server
        sendMessage(writer, "REDY");

        // Receive job details from the server
        String jobDetails = receiveMessage(reader);

        // Check the type of response received from the server and act accordingly
        if (jobDetails.startsWith("JOBN")) {

          // Process the job if it's a job information notification
          processJob(reader, writer, jobDetails);
        } else if (jobDetails.startsWith("JCPL")) {

          // If job is completed, then inform the
          // server that client is ready for next job(if any)
          sendMessage(writer, "REDY");
          receiveMessage(reader);
        } else if (jobDetails.startsWith("NONE")) {

          // Quitting and breaking out of the loop if no more
          // job requests are available
          sendMessage(writer, "QUIT");
          break;
        } else {

          // Unexpected response, break out of the loop
          break;
        }
      }
      // Close resources (socket, writer, and reader)
      closeResources(reader, writer, clientSocket);
    } catch (IOException e) {

      // Handle IO exceptions
      System.err.println("Error: " + e.getMessage());
    }
  }

  //// Process the job received from the server
  private static void processJob(BufferedReader reader, PrintWriter writer, String jobMessage) throws IOException {

    // Split the job message into parts
    String[] parts = jobMessage.split(" ");

    // Extract job ID and resource requirements
    int jobID = Integer.parseInt(parts[1]);
    int requiredCores = Integer.parseInt(parts[3]);
    int requiredRAM = Integer.parseInt(parts[4]);
    int requiredMemory = Integer.parseInt(parts[5]);

    // Request list of capable servers
    sendMessage(writer, "GETS Capable " + requiredCores + " " + requiredRAM + " " + requiredMemory);

    // Server List received
    String capableServers = receiveMessage(reader);

    // Split the server list into parts
    String[] partsTemp = capableServers.split(" ");

    // Extract the number of capable servers
    int number = Integer.parseInt(partsTemp[1]);

    // Find the best fit server for the job
    String[] bestFit = findBestFit(reader, writer, number);
    String acknowledgement = receiveMessage(reader);

    // Schedule the job on the best fit server
    sendMessage(writer, "SCHD " + jobID + " " + bestFit[0] + " " + bestFit[1]);
    receiveMessage(reader);
  }

  // Find the best fit server for the job based on available resources
  private static String[] findBestFit(BufferedReader reader, PrintWriter writer, int number) throws IOException {

    // Initialize variables to track maximum available cores and best fit server
    // details
    int maxCores = Integer.MIN_VALUE;
    String[] bestFit = new String[2];

    // Notify the server to send server details
    sendMessage(writer, "OK");

    // Iterate through the list of servers
    for (int i = 0; i < number; i++) {

      // Receive server details from the server
      String serverDetails = receiveMessage(reader);

      // Split the server details into parts
      String[] parts = serverDetails.split(" ");

      // Extract available cores of the server
      int availableCores = Integer.parseInt(parts[4]);

      // Update the best fit server if the current server has more available cores
      if (availableCores > maxCores) {
        maxCores = availableCores;
        bestFit[0] = parts[0];
        bestFit[1] = parts[1];
      }
    }
    // Notify the server that server details have been received
    sendMessage(writer, "OK");

    // Return the best fit server details
    return bestFit;
  }

  // for receiving server messages
  private static String receiveMessage(BufferedReader reader) throws IOException {
    return reader.readLine().trim();
  }

  // for sending messages to server
  private static void sendMessage(PrintWriter writer, String message) {
    writer.println(message);
  }

  // Close BufferedReader, PrintWriter, and Socket
  private static void closeResources(BufferedReader reader, PrintWriter writer, Socket clientSocket)
      throws IOException {
    reader.close();
    writer.close();
    clientSocket.close();
  }
}
