package torservers;


import java.io.File;
import java.io.PrintStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.ServerSocket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.concurrent.CountDownLatch;
import net.sf.T0rlib4j.controller.network.JavaTorRelay;
import net.sf.T0rlib4j.controller.network.TorServerSocket;
import net.sf.T0rlib4j.samples.Server.ServerSocketViaTor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MultiThreadChatServer {
  
    private static final Logger LOG = LoggerFactory.getLogger(ServerSocketViaTor.class);
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final int hiddenservicedirport = 80;
    private static final int localport = 2096;
    private static CountDownLatch serverLatch = new CountDownLatch(2);
  
    
    /*private static ServerSocket serverSocket = null;*/  
    private static Socket clientSocket = null;
    
    private static final int maxClientsCount = 30;
    private static final clientThread[] threads = new clientThread[maxClientsCount];

  public static void main(String args[]) throws IOException, InterruptedException, ClassNotFoundException, CloneNotSupportedException {
        ServerGUI gui = new ServerGUI(LOG);
        File dir = new File("torfiles");
        
        JavaTorRelay node = new JavaTorRelay(dir);
        TorServerSocket torServerSocket = node.createHiddenService(localport, hiddenservicedirport);

        System.out.println("Hidden Service Binds to   " + torServerSocket.getHostname() + " ");
        System.out.println("Tor Service Listen to Port  " + torServerSocket.getServicePort());

        ServerSocket ssocks = torServerSocket.getServerSocket();
        Server server = new Server(ssocks);
        new Thread(server).start();
        serverLatch.await(); 
    }
 
  private static class Server implements Runnable {
        private final ServerSocket socket;
        private int count = 0;
        private static final Calendar cal = Calendar.getInstance();
        private LocalDateTime now;

        private Server(ServerSocket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {

            System.out.println("Wating for incoming connections...");
            while (true) {
      try {
        clientSocket = socket.accept();
        int i;
        for (i = 0; i < maxClientsCount; i++) {
          if (threads[i] == null) {
            (threads[i] = new clientThread(clientSocket, threads)).start();
            break;
          }
        }
        
       
        if (i == maxClientsCount) {
          PrintStream os = new PrintStream(clientSocket.getOutputStream());
          os.println("Server occupato, riprova più tardi.");
          os.close();
          clientSocket.close();
        
        }
      } catch (IOException e) {
        System.out.println(e);
      }
    }

        }
    }
  
}  
      
      
      
      
      
      
      
    
  


