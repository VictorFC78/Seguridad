import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.regex.Matcher;

public class Principal {
    public static void main(String[] args) {
        Scanner sc=new Scanner(System.in);
        ValidadorFormato formato=new ValidadorFormato();
        System.out.print("Inserte el nombre del archivo:");
        //se pide el nombre del archivo mientras que el formato no sea correcto
        String nombreFichero;
        boolean formatoOk=false;
        boolean ficheroExiste=false;
        do {
            do {
                nombreFichero = sc.nextLine();
                if (formato.formatoCorrecto(nombreFichero)) {
                    formatoOk = true;
                } else {
                    System.out.print("Formato incorrecto, introduzca el nombre del archivo:");
                }
            } while (!formatoOk);
            //se comprueba que el fichero existe
            if (existeFichero(nombreFichero)) ficheroExiste = true;
            else {
                System.out.println("El archivo no existe");
                System.out.println("Debe de insertar el nombre de alguno de los siguientes archivos:");
                listarArchivos();
                formatoOk=false;
            }
        } while(!ficheroExiste);

        //retorna el cifrado escogido
        String cifrado=tipoCifrado();
        //genera una llave de cifrado segun el tipo escogido
        SecretKey llave=generarClave(cifrado);
        //realiza el encryptado del archiovo
        String archivoEncryptado=encryptarArchivo(llave,nombreFichero,cifrado);
        //desencrytamos el archivo
        desencryptadoArchivo(llave,archivoEncryptado,cifrado);
    }
//metodo que comprueba si el archivo existe
    public static boolean existeFichero(String nombreFichero){
        File file=new File(nombreFichero);
        return file.exists();
    }
    //metodo que muestra los archivos disponibles
    public static void listarArchivos(){
        File file=new File("./");
        File[] archivos=file.listFiles();
        for(File f:archivos){
            if(f.isFile()) {
                if(!f.getName().startsWith(".")) System.out.println(f.getName());
            }
        }
    }
    //retorna el tipo de cifrado del archivo
    public static String tipoCifrado(){
        Scanner  sc=new Scanner(System.in);
        String cifrado;
        boolean correcto=false;
        System.out.println("Inserte el tipo de cifrado 'DES o AES:");
        do{
            cifrado=sc.nextLine();
            if(cifrado.equalsIgnoreCase("DES") ||cifrado.equalsIgnoreCase("AES") ) correcto=true;
            else System.out.println("Cifrado no permitido,inserte el tipo cifrado otra vez:");
        }while(!correcto);
        return cifrado;
    }
    //metodo que genera una llave para encrytar y descrytar
    private static SecretKey generarClave(String cifrado){
        int valorCifrado=0;
        SecretKey llave=null;
        KeyGenerator key=null;
        if(cifrado.equalsIgnoreCase("DES")){
            valorCifrado=56;
            try {
                key=KeyGenerator.getInstance("DES");
                key.init(valorCifrado);
                llave= key.generateKey();

            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
        else{
            valorCifrado=128;
            try {
                key=KeyGenerator.getInstance("AES");
                key.init(valorCifrado);
                llave= key.generateKey();
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }

        }
    return llave;
    }
    private static String encryptarArchivo(SecretKey llave,String archivo,String cifrado){
        //abrimod un stream para la escritura y lectura del archivo
        String archivoEncrytado=null;
        try {
            FileInputStream fis = null;
            FileOutputStream fos = null;
            int bytesLeidos;//variable par leer los bytes del archivo
            byte[] bufferLectura = new byte[1000];//contendra los datos del archivo original
            byte[] bufferCifrado;//contendra los datos del archivo cifrado
            //creamos un objeto para cifrar
            Cipher cifrador = Cipher.getInstance(cifrado);//objeto pra cifrar
            cifrador.init(Cipher.ENCRYPT_MODE,llave);//iniciar el cifrador en modo encryptacion
            archivoEncrytado=archivoSinExtension(archivo)+".cif";
            fis=new FileInputStream(archivo);
            fos=new FileOutputStream(archivoEncrytado);
            bytesLeidos= fis.read(bufferLectura,0,100);//indica la longitud a leer en cada metodo read
            //leemos del archivo original hasta el final y vamos grabando en el cifrado
            while(bytesLeidos!=-1){
                //pasa los datos del buffer de lectura al buffer de escritura
                bufferCifrado=cifrador.update(bufferLectura,0,bytesLeidos);
                fos.write(bufferCifrado);//escribe en el archivo cifrado
                //inicializamos el tamaño de bytes de lectura a la posicion inicial y la misma longiyud
                bytesLeidos=fis.read(bufferLectura,0,100);
            }
            //compltamos el cifrado del archivo con los ultimods datos
            bufferCifrado=cifrador.doFinal();
            fos.write(bufferCifrado);
            fis.close();
            fos.close();
        }catch (Exception e){
            System.out.print("Se ha producido un error");
        }finally{
            return archivoEncrytado;
        }
    }
   private static void desencryptadoArchivo(SecretKey llave,String archivo,String cifrado){
       try {
           FileInputStream fis = null;
           FileOutputStream fos = null;
           int bytesLeidos;//variable par leer los bytes del archivo
           byte[] buffer = new byte[1000];//contendra los datos del archivo original
           byte[] bufferDescifrado;//contendra los datos del archivo cifrado
           //creamos un objeto para cifrar
           Cipher cifrador = Cipher.getInstance(cifrado);//objeto pra cifrar
           cifrador.init(Cipher.DECRYPT_MODE, llave);//iniciar el cifrador en modo dessencryptador
           fis=new FileInputStream(archivo);
           fos=new FileOutputStream(archivoSinExtension(archivo)+".claro");
           bytesLeidos= fis.read(buffer,0,100);
           while(bytesLeidos!=-1){
               //pasa los datos del buffer de lectura al buffer de escritura
               bufferDescifrado=cifrador.update(buffer,0,bytesLeidos);
               fos.write(bufferDescifrado);//escribe en el archivo cifrado
               //inicializamos el tamaño de bytes de lectura a la posicion inicial y la misma longiyud
               bytesLeidos=fis.read(buffer,0,100);
           }
           //compltamos el cifrado del archivo con los ultimods datos
           bufferDescifrado=cifrador.doFinal();
           fos.write(bufferDescifrado);
           fis.close();
           fos.close();
       }catch (Exception e){
            System.out.print("Error al descifrar archivo");
       }
   }
    private static String archivoSinExtension(String archivo){
        String []division=archivo.split("\\.");
        return division[0];
    }
}
