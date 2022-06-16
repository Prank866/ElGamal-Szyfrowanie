
import java.io.*;
import java.net.*;
import java.util.Scanner;



public class Klient{
    public static int port = 8888;

    public static void main(String[] args) throws Exception {new Klient();}


    public Klient() throws Exception{
        Socket socket = new Socket("localhost", port);
        Socket socketForFiles = new Socket("localhost", 4444);


        if (socket.isConnected() && socketForFiles.isConnected()) {
            System.out.println("Połączono z serwerem");
            System.out.println("Jesli chcesz wysłać plik naciśnij ENTER !  - pliki zapisywane są do pliku txt C:/plik.txt");

        }

        CipherClass cipherClassKlient = new CipherClass();


        Thread thread1 = new Thread(() -> {

            try {


                //wysyłanie
                PrintWriter pWriter = new PrintWriter(socket.getOutputStream());
                Scanner scannerZnakow = new Scanner(System.in);
                String wiadomoscDlaSerwera = scannerZnakow.nextLine();


                if (wiadomoscDlaSerwera != "") {

                    //SZYFROWANIE

                    //Drugi sposób zapisu bitow do zmienne
                    byte[] wiadomoscDlaSerweraBytes = wiadomoscDlaSerwera.getBytes();
                    pWriter.println("" + cipherClassKlient.Encrypt(wiadomoscDlaSerweraBytes, cipherClassKlient.getPublicKey()));
                    pWriter.flush();


                } else {
                    System.out.println("Podaj scieżke do pliku:  (np: C:/windows/windows32)");
                    String sciezka = scannerZnakow.nextLine();
                    if (sciezka == "") {
                        System.out.println("Podaj ścieżke do pliku :)");

                    } else {
                        Scanner scannerPlikow = new Scanner(new File(sciezka));

                        String linia = "";
                        while (scannerPlikow.hasNextLine()) {
                            linia += scannerPlikow.nextLine();
                            linia += " ";
                        }

                        pWriter.println("" + linia);
                        pWriter.flush();


                    }

                }





            } catch (Exception e) {
                e.printStackTrace();
                try {
                    socket.close();
                    socketForFiles.close();

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

        });



        Thread thread2 = new Thread(() -> {


            while (true){
                try {

                    //odbieranie wiadomsoci
                    InputStreamReader isr = new InputStreamReader(socket.getInputStream());
                    BufferedReader br = new BufferedReader(isr);
                    System.out.println("Serwer: " + br.readLine());

                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        socket.close();
                        socketForFiles.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }




        });

        Thread thread3 = new Thread(() -> {

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream("C:/plik.txt");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


            while (true) {
                try {

                    //odbieranie pliku


                    InputStream is = socketForFiles.getInputStream();


                    byte[] bytes = new byte[4096];
                    is.read(bytes, 0, bytes.length);
                    fos.write(bytes, 0, bytes.length);
                    fos.flush();

                    System.out.println("Plik nadpisany!");


                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        socket.close();
                        socketForFiles.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }


            }




        });

        thread1.start();
        thread2.start();
        thread3.start();


    }



}

