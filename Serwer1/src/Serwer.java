import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.lang.Exception;

public class Serwer{
    public static int port = 8888;

    public static void main(String[] args) throws Exception {new Serwer();}


    public Serwer()throws Exception{

        System.out.println("Czekanie na połączenie od klienta ...");
        ServerSocket serverSocket = new ServerSocket(port);
        ServerSocket serverSocketForFiles = new ServerSocket(4444);
        Socket socketForFIles = serverSocketForFiles.accept();
        Socket socket = serverSocket.accept();


        System.out.println("Klient Połączony");
        System.out.println("Czekaj Na wiadomosc od klienta!");
        System.out.println("Jesli chcesz wysłać plik naciśnij ENTER !");

        CipherClass cipherClassSerwer = new CipherClass();


        Thread thread1 = new Thread(() -> {


            while (true){
                try {


                    //wysyłanie
                    PrintWriter pWriter = new PrintWriter(socket.getOutputStream());
                    Scanner scannerZnakow = new Scanner(System.in);
                    String wiadomoscDlaKlienta = scannerZnakow.nextLine();

                    if (wiadomoscDlaKlienta == "") {

                        //wysyłanie pliku
                        System.out.println("Podaj sciezke do pliku: ");
                        String str = scannerZnakow.nextLine();
                        if(str == ""){
                            System.out.println("Podaj poprawną sciezke! ");
                        }else{
                            FileInputStream fis = new FileInputStream(str);
                            byte[] bytes = new byte[4096];
                            OutputStream os = socketForFIles.getOutputStream();
                            System.out.println("Wysyłanie...");
                            fis.read(bytes, 0, bytes.length);
                            os.write(bytes, 0, bytes.length);
                            os.flush();

                            System.out.println("Wysyłanie pliku zakonczone !");
                        }

                    }else{
                        pWriter.println("" + wiadomoscDlaKlienta);
                        pWriter.flush();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        socket.close();
                        serverSocket.close();
                        serverSocketForFiles.close();
                        socketForFIles.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }


        });


        Thread thread2 = new Thread(() -> {


            try {

                while (true) {
                    //odbieranie
                    InputStreamReader isReader = new InputStreamReader(socket.getInputStream());
                    BufferedReader buffReader = new BufferedReader(isReader);


                    String str = buffReader.readLine();


                    System.out.println("Klient zaszyfrowana wiadomosc: " + str);


                }


            } catch (Exception e) {
                e.printStackTrace();
                try {
                    socket.close();
                    serverSocket.close();
                    serverSocketForFiles.close();
                    socketForFIles.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

        });



        thread1.start();
        thread2.start();



    }

}
