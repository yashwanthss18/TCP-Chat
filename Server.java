import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {

    ArrayList<connectionhandler> connections;
    private ServerSocket server;
    private boolean done;
    private ExecutorService pool;

    public Server(){
        connections = new ArrayList<>();
        done = false;
    }

    @Override
    public void run(){
        try{
        server = new ServerSocket(9999);
        pool = Executors.newCachedThreadPool();
        while(!done){
        Socket client = server.accept();
        connectionhandler handler = new connectionhandler(client);
        connections.add(handler);
        pool.execute(handler);

        }
    }catch(Exception e){
           shutdown();
        }
    }

    public void broadcast(String message){
        for(connectionhandler ch: connections){
            if(ch!=null){
                ch.sendmessage(message);
            }
        }
    }


    public void shutdown(){
        try{
        done = true;
        if(!server.isClosed()){
            server.close();
        }
        for(connectionhandler ch : connections){
            ch.shutdown();
        }
    }
    catch(IOException e){

    }

    }

    class connectionhandler implements Runnable{

        private Socket client;
        private BufferedReader in;
        private PrintWriter out;
        private String nickname;

        public  connectionhandler(Socket client){
            this.client = client;
        }

        public void run(){
            try{
                out = new PrintWriter(client.getOutputStream(),true);
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                out.println("enter a name: ");
                nickname = in.readLine();
                System.out.println(nickname+ " Connected!");
                broadcast(nickname +" Joined the chat!");
                String message;
                while((message= in.readLine())!= null){
                    if(message.startsWith("/quit")){
                        broadcast(nickname +"left the chat");
                        shutdown();
                    }
                    else{
                        broadcast(nickname+":"+ message);
                    }
                }
                
            }
            catch(Exception e){
               shutdown();
            }

        }

        public void sendmessage(String message){
            out.println(message);
        }

        public void shutdown(){
            try{
            in.close();
            out.close();
            if(!client.isClosed()){
                client.close();
            }
        }
        catch(IOException e){

        }
    }

    
}
public static void main(String[] args) {
        
        Server server = new Server();
        server.run();
    }


    
}
