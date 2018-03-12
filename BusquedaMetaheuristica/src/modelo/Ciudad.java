package modelo;

import java.util.HashMap;

/**
 * 
 * @author Eloy
 * 
 * Esta clase, que también podría llamarse de forma genérica "Elemento", es la clase a la que pertenecen los miembros
 * de una población, y guarda la información relevante. En el caso de esta asigantura, al menos por ahora, esto implica
 * guardar el índice del elemento y su distancia al resto de elementos según leída de un fichero.
 * 
 */

public class Ciudad {
    private int indice;
    private HashMap<Integer, Double> distancias;
    
    public Ciudad(){
        this.distancias = new HashMap();
    }
    
    public Ciudad(int indice){
        this.indice = indice;
        this.distancias = new HashMap();
    }
    
    public Ciudad(int indice, HashMap<Integer, Double> distancias){
        this.indice = indice;
        this.distancias = new HashMap(distancias);
    }
    
    public int getIndice(){
        return this.indice;
    }
    
    public HashMap<Integer, Double> getDistancias(){
        return this.distancias;
    }
    
    public double getDistancia(int indice){
        return distancias.get(indice);        
    }
}