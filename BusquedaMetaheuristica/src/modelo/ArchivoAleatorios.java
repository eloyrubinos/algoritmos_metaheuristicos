package modelo;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.IOException;
import static java.lang.Math.floor;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author Eloy
 * 
 * Clase que se encarga de leer, almacenar e interpretar los números de un archivo de índices.
 * Los números deben ser decimales entre 0 y 1.
 * Hay métodos para transformarlos en índices válidos para cualquier tamaño de población.
 * 
 */

public class ArchivoAleatorios {
    
    private ArrayList<Double> aleatorios;
    
/**
 * 
 * El constructor de la clase recibe una ruta y lee el archivo correspondiente
 * almacenando los datos.
 *
 */
    public ArchivoAleatorios(String dir){
        aleatorios = new ArrayList();
        try{
            Path path = Paths.get(dir);
            Charset charset = Charset.forName("UTF-8");
            Iterator it = Files.lines(path, charset).iterator();
            while(it.hasNext()){
                aleatorios.add(Double.parseDouble(it.next().toString()));
            }
        } catch (IOException | InvalidPathException | IllegalCharsetNameException ex) {
            Logger.getLogger(Poblacion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /* Nos devuelve los números leídos sin procesar */
    public ArrayList<Double> getAleatorios(){
        return this.aleatorios;
    }
    
    /* Genera la solución inicial a partir de los primeros índices del archivo */
    public int getAleatoriosInicial(int poblacion, ArrayList<Integer> solIni){
        int aux, i;
        for(i = 0; i < poblacion; i++){
            aux = 1 + (int)floor(aleatorios.get(i) * (poblacion));
            while(solIni.contains(aux)){
                if(aux == poblacion) aux = 1;
                else aux++;
            }
            solIni.add(aux);
        }
        return i;
    }
    
    public int getAleatoriosInicialCE(int index, int poblacion, ArrayList<Integer> solIni){
        int aux, i, indice = index;
        for(i = 0; i < poblacion; i++){
            aux = 1 + (int)floor(aleatorios.get(indice) * (poblacion));
            while(solIni.contains(aux)){
                if(aux == poblacion) aux = 1;
                else aux++;
            }
            solIni.add(aux);
            indice++;
        }
        return indice;
    }
    
    /* Genera un par de índices ajustado al rango en que trabajamos en un momento dado (tamaño de la población) */
    public int getAleatoriosVecinos(int poblacion, int index, Point vecino){
        if(index < aleatorios.size()){
            vecino.x = (int)floor(aleatorios.get(index) * (poblacion));
            vecino.y = (int)floor(aleatorios.get(index+1) * (poblacion));
            return index+2;
        }
        else return -1;
    }
    
    /* Caso particular para temple simulado en que el segundo índice de cada par se devuelve como un double sin tratar */
    public int getAleatoriosVecinosTS(int poblacion, int index, Point2D.Double vecino){
        if(index < aleatorios.size()){
            vecino.x = (int)floor(aleatorios.get(index) * (poblacion));
            vecino.y = aleatorios.get(index+1);
            return index+2;
        }
        else return -1;
    }
    
    /* Obtengo pares aleatorios para los índices de competidores en el torneo de computación evolutiva */
    public int getAleatoriosTorneo(int index, Point luchadores, int tam) {
        if(index < aleatorios.size()){
            luchadores.x = (int)floor(aleatorios.get(index) * tam);
            luchadores.y = (int)floor(aleatorios.get(index+1) * tam);
            return index+2;
        }
        else return -1;
    }
    
    /* Caso particular que consume un solo número aleatorio y devuelve un índice DE CIUDAD, no de posición, en la variable x del Point pasado como argumento */
    public int nextCiudad(int index, int poblacion, Point ciudad) {
        if(index < aleatorios.size()){
            ciudad.x = 1 + (int)floor(aleatorios.get(index) * (poblacion));
            return index+1;
        }
        else return -1;
    }
    
    /* Caso particular que consume un solo número aleatorio y devuelve un índice DE POSICIÓN en la variable x del Point pasado como argumento */
    public int nextIndice(int index, int poblacion, Point ciudad) {
        if(index < aleatorios.size()){
            ciudad.x = (int)floor(aleatorios.get(index) * (poblacion));
            return index+1;
        }
        else return -1;
    }
    
    /* Caso particular que consume un solo número aleatorio y lo devuelve sin tratar en la variable x del Point pasado como argumento */
    public int nextAleatorio(int index, Point2D.Double aleatorio) {
        if(index < aleatorios.size()){
            aleatorio.x = aleatorios.get(index);
            return index+1;
        }
        else return -1;
    }
}