package modelo;

import java.io.IOException;
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
 * Esta clase guarda una referencia a todos los elementos de la población, 
 * y dos atributos de implementación, que son los índices en que empezamos 
 * a leer del archivo.
 * La clase requiere que los datos de población vengan dados en una matriz 
 * diagonal que empiece en el elemento (1,0) y acabe en el (i, i-1).
 * 
 * Este comportamiento puede modificarse modificando las constantes de la clase.
 * 
 * Nótese que los elementos se leen, como se ha dicho, empezando en el 1, pero
 * según la implementación de arrays en Java estos empiezan en el índice 0. Por
 * tanto en esta clase el elemento 1 de la población estará en el índice 0 del
 * array de elementos.
 * 
 */

public class Poblacion {
    public final int INICIO_FILAS_MATRIZ = 1;
    public final int INICIO_COLUMNAS_MATRIZ = 0;
    
    private ArrayList<Ciudad> ciudades;
    
    public Poblacion(){
        this.ciudades = new ArrayList();
    }

    public Poblacion (ArrayList<Ciudad> ciudades){
        this.ciudades = new ArrayList(ciudades);
    }
    
    public ArrayList<Ciudad> getCiudades(){
        return this.ciudades;
    }
    
    public Ciudad getCiudad(int indice){
        return this.ciudades.get(indice);
    }
    
    public Ciudad getCiudadId(int indice){
        for(Ciudad c : this.ciudades){
            if(c.getIndice() == indice) return c;
        }
        return null;
    }
    
    /**
     * Función que nos devuelve el parámetro que relaciona a dos elementos
     * de la población.
     * Como la población se lee en forma de matriz diagonal, nos aseguramos de
     * pedir la información al elemento correcto (si "info(A, B)" para B>A, el 
     * dato está en (B, A) en realidad).
     * 
     * Se resta 1 en el índice de partida debido a la circunstancia explicada
     * en la descripción de la clase.
     */
    public double getDistancia(int indicePartida, int indiceDestino){
        if(indicePartida > indiceDestino) return this.getCiudad(indicePartida - 1).getDistancia(indiceDestino);
        else if(indiceDestino > indicePartida) return this.getCiudad(indiceDestino - 1).getDistancia(indicePartida);
        else return 0;
    }
    
    /**
     * Función que lee el archivo de población e interpreta los datos, creando
     * los elementos de población (en este caso Ciudades) y guardando en cada
     * uno la información proporcionada (en este caso distancias).
     */
    public void leerPoblacion(String dir){
        int i = INICIO_FILAS_MATRIZ, j = INICIO_COLUMNAS_MATRIZ;
        Ciudad c;
        try{
            Path path = Paths.get(dir);
            Charset charset = Charset.forName("UTF-8");
            Iterator it = Files.lines(path, charset).iterator();
            while(it.hasNext()){
                c = new Ciudad(i);
                String line = it.next().toString();
                String[] componentes = line.split("\t");
                for(String s : componentes){
                    c.getDistancias().put(j, Double.parseDouble(s));
                    j++;
                }
                this.ciudades.add(c);
                j = INICIO_COLUMNAS_MATRIZ;
                i++;
            }
        } catch (IOException | InvalidPathException | IllegalCharsetNameException ex) {
            Logger.getLogger(Poblacion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Función que, dada una ciudad recibida como entrada, nos dice cuál es la que
     * está más cerca.
     * También recibe un array por si hay un subgrupo de ciudades que no queremos
     * considerar para una búsqueda concreta, que será lo habitual.
     * En todo caso, si no queremos dejar ninguna ciudad fuera de la búsqueda
     * no tenemos más que pasar un array vacío.
     */
    public int minDist(int indice, ArrayList<Integer> visitados){
        int minC = -1;
        double aux, minDist = Integer.MAX_VALUE;
        
        for(int i = 0; i <= this.ciudades.size(); i++){
            if(i != indice && !visitados.contains(i)){
                if((aux = this.getDistancia(indice, i)) < minDist){
                    minDist = aux;
                    minC = i;
                }
            }
        }
        return minC;
    }
}