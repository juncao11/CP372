/**
 * Author(s): Jun Cao 150568530, Franchesco Livado 161904130
 * Group: 71
 * SBoard.java
 * Date: February 4 2019
 */

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.ArrayList;


//
public class SBoard {
  public static Vector<Note> vBoard = new Vector<Note>();
  public static String boardWidth;
  public static String boardHeight;
  public static String serverPort;
  public static String outMsg = "";
  public static int a;
  public static ArrayList<String> colours = new ArrayList<String>();
    public static void main(String[] args) throws Exception {
        System.out.println("Board is running.");
        
        //new board object
        SBoard board = new SBoard(Integer.parseInt(args[0]),Integer.parseInt(args[1]),Integer.parseInt(args[2]),args[3],args[4],args[5]);
        int clientNumber = 0;        

        serverPort = args[0];
        boardWidth = args[1];
        boardHeight = args[2];

        a = args.length;

        for (int i =3; i<a; i++){
          colours.add(args[i]);
        }
        
        //new socket
        ServerSocket listener = new ServerSocket(board.getPort());

        //try block for connections
        try {
            while (true){
                new Server(listener.accept(), clientNumber++).start();
            }
            
        }finally {
            listener.close();
        }
    }



    private static class Server extends Thread {
        private Socket socket;
        private int clientNumber;
        // private String outMsg = "";
        
        //new vector where the board will be
        // Iterator<Note> itr = vBoard.iterator();
        
        SBoard b = new SBoard();

        public Server(Socket socket, int clientNumber){
            this.socket = socket;
            this.clientNumber = clientNumber;
            log("New connection with client# " + clientNumber +" at " + socket);
        }

        public void run(){
            try {
                String client = "client" + clientNumber + ": ";
                
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                // BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                // PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                // out.println("Hello you are client #" + clientNumber + ".");
                
                String col = "";

                for (int i=0; i<colours.size(); i++){
                  col = col + " " + colours.get(i);
                }
                
                outMsg = outMsg + client + "connects to the server.\n"
                          + "\nBoard width:  " + boardWidth
                          + "\nBoard height: " + boardHeight
                          + "\nColours: "+ col + "\n";
                out.writeUTF(outMsg);
               
            

            while (true){
                String input = in.readUTF();
                  //gets the type of command
                  //get
                //GET ALL PINNED
                if (input.contains("GETALL")){
                  int j = 0;
                  for (Note note : vBoard){
                    SBoard.Note n = note;
                    //if note coordinate data equals string coordinate and note is pinned, print note data
                    if (n.pin >= 1){
                      outMsg = outMsg + client + "("+n.data +")" + "\n";
                      j++;
                    }
                  }
                  if (j==0){
                    outMsg = outMsg + client + "No notes pinned.\n";
                  }
                  out.writeUTF(outMsg);
                }
                  else if (input.contains("GET")){

                      //splits on new line into string array
                      String[] parts = input.split(" ");
                      String data ="";
                      String msg ="";
                      String colour="";
                      for (int i = 0; i<parts.length; i++){
                        //if not empty parameter set to variables to search
                        if (!parts[i].equals("x")){
                          if (i==1){
                            data = parts[i];
                          }
                          else if (i == 2){
                            colour = parts[i];
                          }
                          else if (i == 3){
                            msg = parts[i];
                          }
                        }
                      }
                    int j = 0;
                    //run through vector
                    for (Note note : vBoard){
                      SBoard.Note n = note;
                      //if none of the parameters are empty, then search for the one that matches
                      if (!data.equals("") & !colour.equals("") & !msg.equals("")){
                          if ((n.data.equals(data) & n.colour.equals(colour) & n.message.contains(msg))){
                            outMsg = outMsg + client +  "requests " + n.printNote() + "\n";
                            // out.println(n.printNote());
                            j++;
                          }
                      }
                      //all colours - data+msg 
                      else if (!data.equals("") & !msg.equals("")){
                        if (n.data.equals(data) & n.message.contains(msg)){
                          outMsg = outMsg + client + "requests "+ n.printNote() + "\n";                          j++;
                        }
                      }
                      //data+colour
                      else if (!data.equals("") & !colour.equals("")){
                        if (n.data.equals(data) & n.colour.equals(colour)){
                          outMsg = outMsg + client + "requests " + n.printNote() + "\n";                          j++;
                        }
                      }
                      //msg+colour
                      else if (!msg.equals("") & !colour.equals("")){
                        if (n.colour.equals(colour) & n.message.contains(msg)){
                          outMsg = outMsg + client + "requests " + n.printNote() + "\n";                          j++;
                        }
                      }
                      //msg
                      else if (!msg.equals("")){
                        if (n.message.contains(msg)){
                          outMsg = outMsg + client + "requests " + n.printNote() + "\n";                          j++;
                        }
                      }
                      //colour
                      else if (!colour.equals("")){
                        if (n.colour.equals(colour)){
                          outMsg = outMsg + client +  "requests " + n.printNote() + "\n";                          j++;
                        }
                      }
                      //data
                      else if (!data.equals("")){
                        if (n.data.equals(data)){
                          outMsg = outMsg + client + "requests " + n.printNote() + "\n";
                          j++;
                        }
                      }
                  }
                  if (j==0){
                    outMsg = outMsg + "No note found.\n";
                  }
                  out.writeUTF(outMsg);
                }

                  //POST - ADD NOTE 
                  else if (input.contains("POST")){
                      // splits on new line into string array
                      
                      String[] parts = input.split(" ");
                      if (parts.length != 6){
                        outMsg = outMsg + client + "Error: not all fields are filled for POST call.\n";
                      }
                      else{
                        //splice data
                        String[] noteData = parts[1].split(",");
                        if (noteData.length != 2){
                          outMsg = outMsg + client +  "Error: Wrong coordinate format.\n";
                        }
                        else{
                        String c = parts[4].toLowerCase();
                        boolean flag = false;
                        for (int i=0; i<colours.size(); i++){
                          if(c.equals(colours.get(i))){
                            flag = true;
                          }
                        }
                        if (flag){
                        int x = Integer.parseInt(noteData[0]);
                        int y = Integer.parseInt(noteData[1]);
                        int w = Integer.parseInt(parts[2]);
                        int h = Integer.parseInt(parts[3]);

                        int rightX = x+w;
                        int rightY = y+h;

                        if((rightX > Integer.parseInt(boardWidth)) || (rightY > Integer.parseInt(boardHeight))){
                          outMsg = outMsg + client + "Error: Not within board dimensions.\n";
                        }
                        else{
                        SBoard.Note n = b.new Note(parts[1],Integer.parseInt(parts[2]), Integer.parseInt(parts[3]),parts[4], parts[5]);
                        vBoard.add(n);
                        outMsg = outMsg + client + "Note posted" + "\n";
                      }
                      }else{
                        outMsg = outMsg + client + "Error: Incorrect colour.\n";
                      }
                        
                    }
                  }
                      out.writeUTF(outMsg);
                      // out.println(n.printNote());
                  }
                  //UNPIN -decrement pin
                  else if (input.contains("UNPIN")){
                    String[] parts = input.split(" ");
                    int j = 0;
                    for (Note note : vBoard){
                      SBoard.Note n = note;
                      //if note coordinate data is equal to string coordinate and note is pinned, decrement pin
                      if (n.data.equals(parts[1]) && n.pin >= 1){
                        n.unpin();
                        
                        outMsg = outMsg + client + "Pin unpinned\n"; 
                        // out.println("Note(s) unpinned.");
                        j++;
                      }
                    }
                    if (j==0){
                      outMsg = outMsg +client + "No pins unpinned.\n";
                    }
                    out.writeUTF(outMsg);

                  }
                  //PIN - increment pin
                  else if (input.contains("PIN")){
                    String[] parts = input.split(" ");
                    int j = 0;
                    for (Note note : vBoard){
                      SBoard.Note n = note;
                      //if note coordinate data is equal to string coordinate, increment pin
                      if (n.data.equals(parts[1])){
                        n.pin();
                        if (n.pin <=1){
                          outMsg = outMsg + client + "Note pinned\n";
                        }

                        // out.println("Note(s) pinned.");
                        j++;
                      }
                    }
                    if (j==0){
                      outMsg = outMsg + client + "No notes pinned.\n";
                    }
                    out.writeUTF(outMsg);
                  }

                  //CLEAR - clears all unpinned notes
                  else if(input.contains("CLEAR")){
                    for (int i =0; i<vBoard.size(); i++){
                      SBoard.Note n = vBoard.get(i);
                        //check if unpinned and then removes it from the vector 
                        if (n.pin < 1)
                          vBoard.removeElementAt(i);
                        }
                    outMsg = outMsg + client + "Unpinned notes cleared.\n" + "Notes left on the board: " + vBoard.size() + "\n";
                    out.writeUTF(outMsg);
                
              }
                  //disconnect
                  else if(input.contains("DISCONNECT")){
                      break;
                  }
                
            }
      }catch (IOException e){
            log("Error handling client# " + clientNumber + ": " + e); 
        } finally {
            try{
                socket.close();
            } catch (IOException e){
                log("Couldn't close the scoket.");
            }
            log("Connection with client# " + clientNumber + " closed");
        }
        }
        private void log(String message) {
            System.out.println(message);
        }
    }
        
        // public void run() {
        //     try {
        //         BufferedReader in = new BufferedReader(
        //             new InputStreamReader(socket.getInputStream()));
        //     PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        //     out.println("Hello, you are client #" + clientNumber + ".");

        //     while (true) {
        //         String input = in.readLine();
        //         if (input == null || input.equals(".")) {
        //             break;
        //         }
        //         out.println(input.toUpperCase());
        //     }
        // } catch (IOException e) {
        //     log("Error handling client# " + clientNumber + ": " + e);
        // } finally {
        //     try {
        //         socket.close();
        //     } catch (IOException e) {
        //         log("Couldn't close a socket, what's going on?");
        //     }
        //     log("Connection with client# " + clientNumber + " closed");
        // }
        //     }
    

    //Note Class
    public class Note{
    	private String data;
    	private int height;
    	private int width;
    	private String colour;
    	private String message;
    	private int pin = 0;
    	
        //Note constructor with all parameters
        public Note(String data, int height, int width, String colour, String message){
            this.data = data;
            this.height = height;
            this.width = width;
            this.colour = colour;
            this.message = message;
        }

        //Note constructor with no colour parameter
        public Note(String data, int height, int width, String message){
            this.data = data;
            this.height = height;
            this.width = width;
            this.message = message;
        }

        //set pin status of note
        public void unpin(){
					this.pin--;
          
        }
      
      	public void pin(){
          this.pin++;
        }
        
        //get coordinates
        public String getData() {
        	return this.data;
        }
      
        //print note 
      	public String printNote(){
          String s = this.data + " " + Integer.toString(this.height) + " " + Integer.toString(this.width) +" "+ this.colour + " " + this.message;
          return s;
        }
        
    }

    //SBoard Class
    	private int port;
    	private int width;
    	private int height;
    	private String c1;
    	private String c2;
    	private String c3;
    	
    	public SBoard() {
    		
    	}
        public SBoard(int port, int width, int height, String c1, String c2, String c3){
            this.port = port;
            this.width = width;
            this.height = height;
            this.c1 = c1;
            this.c2 = c2;
            this.c3 = c3;
        }

        private int getPort(){
            return this.port;
        }
    }
