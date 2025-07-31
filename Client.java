import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client implements Runnable {
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private boolean done;
    
    public void run(){
        try{
            Socket client = new Socket("127.0.0.1",9999);
            out = new PrintWriter(client.getOutputStream(),true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            inputhandler inhandler = new inputhandler();
            Thread t = new Thread(inhandler);
            t.start();

            String inmessage ;
            while((inmessage=in.readLine())!=null){
                System.out.println(inmessage);
            }
        }catch(IOException e){
            shutdown();

        }
    }

    public void shutdown(){
        done = true;
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

    public class  inputhandler implements Runnable {
        public void run(){
            try{
                BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
                while(!done){
                    String message = inReader.readLine();
                    if(message.equals("/quit")){
                        inReader.close();
                        shutdown();
                    }
                    else{
                        out.println(message);
                    }

                }
            }catch(IOException e){
                shutdown();
            }
        }

        
        
    }
    public static void main(String[] args) {
            Client client = new Client();
            client.run();
        }
    
     
}
