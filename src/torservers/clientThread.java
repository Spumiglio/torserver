package torservers;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

/*Per ogni connessione di client viene chiamata questa classe*/
  public class clientThread extends Thread{
  private String clientName = null;
  private DataInputStream is = null;
  private PrintStream os = null;
  private Socket clientSocket = null;
  private final clientThread[] threads;
  private int maxClientsCount;
  private String ipAddress = null;

  
  public clientThread(Socket clientSocket, clientThread[] threads) {
    this.clientSocket = clientSocket;
    this.threads = threads;
    maxClientsCount = threads.length;
  } 
  
  public void run() {
    int maxClientsCount = this.maxClientsCount;
    clientThread[] threads = this.threads;

    try {
    	
      /* Crea un is che riceve gli input e un os che manda output */
      is = new DataInputStream(clientSocket.getInputStream());
      os = new PrintStream(clientSocket.getOutputStream());
      
      /* Legge il nome attraverso is, se contiene @ lo richiede*/

      String token;
      String name;
      /*while (true) {
        //os.println("Metti il tuo nome.");
        name = is.readLine().trim();
        if (name.indexOf('@') == -1) {		//ritorna -1 se il nome non inizia con @
          break;
        } else {							
          os.println("Il nome non deve contenere il carattere '@'.");
        }
      }*/
      token = is.readLine().trim();
      String request = "http://18.184.144.163:9100/verify?token="+token;
      String risposta_login = Chat.executePost(request);
      JSONObject ss = new JSONObject(risposta_login);

      name = ss.getString("Utente");
      os.println("Benvenuto " + name
          + " nella nostra chat.\nPer uscire digitare /quit in una nuova riga.\n");
      synchronized (this) {
    	  
    	  /* Assegna a QUESTO Thread un nome riconosciuto dalla @*/
        for (int i = 0; i < maxClientsCount; i++) {
          if (threads[i] != null && threads[i] == this) {
            clientName = "@" + name;            
            break;
          }
        }
        
        /* A tutti i Thread eccetto QUESTO, viene printato l'arrivo di un nuovo client*/
        for (int i = 0; i < maxClientsCount; i++) {
          if (threads[i] != null && threads[i] != this) {
            threads[i].os.println("*** Un nuovo utente " + name
                + " e' entrato nella chat !!! ***");
          }
        }
      }
      
      /* Inizia la conversazione */
      while (true) {
        String line = is.readLine();		
        if (line.startsWith("/quit")) {		//se true termina il while
          break;
        }
        
   
        /* Messaggio privato deve iniziare con @ */
        if (line.startsWith("@")) {
        	
        	/* Es: "@user ciao" splitta il nome dal testo e controlla che ci sia un testo*/
          String[] words = line.split("\\s", 2);		//divide il messaggio in 2 parti, di cui la prima parte � il nome e la seconda il testo
          if (words.length > 1 && words[1] != null) {
            words[1] = words[1].trim();			//toglie i primi e gli ultimi spazi 
            if (!words[1].isEmpty()) {
              synchronized (this) {
            	  
            	  /* Cerca il client con lo stesso nome scritto prima a cui viene printato il messaggio */
                for (int i = 0; i < maxClientsCount; i++) {
                  if (threads[i] != null && threads[i] != this
                      && threads[i].clientName != null
                      && threads[i].clientName.equals(words[0])) {
                    threads[i].os.println("<" + name + "> " + words[1]);		//printa al destinatario il messaggio
                    
                    /* Avvisa che il messaggio � inviato in modo privato */
                    this.os.println(">" + name + "> " + words[1]);
                    break;
                  }
                }
              }
            }
          }
        }
        else {	//se il messaggio non inizia con @
        	
          /* Il messaggio arriva a tutti i client connessi. */
          synchronized (this) {
            for (int i = 0; i < maxClientsCount; i++) {
              if (threads[i] != null && threads[i].clientName != null) {
                threads[i].os.println("<"  +name +   "> " + line);
              }
            }
          }
        }
      }
      /*Se un client scrive /quit, viene disconnesso dalla chat*/
      synchronized (this) {
        for (int i = 0; i < maxClientsCount; i++) {
          if (threads[i] != null && threads[i] != this
              && threads[i].clientName != null) {
            threads[i].os.println("*** L'utente " + name
                + " ha abbandonato la chat !!! ***");
          }
        }
      }
      /*All'utente disconnesso viene printato questo:*/
      os.println("*** Ciao " + name + " ***");

      /* Elimina il client uscito */
      synchronized (this) {
        for (int i = 0; i < maxClientsCount; i++) {
          if (threads[i] == this) {
            threads[i] = null;
          }
        }
      }
      /* Chiude tutto */
      is.close();
      os.close();
      clientSocket.close();
    } catch (IOException e) {
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }
}