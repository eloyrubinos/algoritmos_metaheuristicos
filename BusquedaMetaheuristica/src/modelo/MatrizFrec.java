package modelo;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Eloy
 * 
 * Esta clase implementa la funcionalidad de una matriz de frecuencias para
 * algoritmos de búsqueda.
 * 
 */
public class MatrizFrec {
    private Poblacion pob;
    private HashMap<Point, Integer> frec;
    private final float GRADO_DE_ALTERACION;
    private final double Dmax;
    private final double Dmin;
    private final double D;
    public int Fmax;
    
    public MatrizFrec(Poblacion pob, float mu){
        this.pob = pob;
        frec = new HashMap();
        this.GRADO_DE_ALTERACION = mu;
        this.Dmax = this.getDMax();
        this.Dmin = this.getDMin();
        D = Dmax - Dmin;
        Fmax = 1;
    }
    
    /* La distancia máxima entre dos ciudades de la población */
    private double getDMax(){
        double dMax = 0;
        
        for(Ciudad c : pob.getCiudades()){
            for(Double f : c.getDistancias().values()){
                if(f > dMax) dMax = f;
            }
        }
        
        return dMax;
    }
    
    /* La distancia mínima entre dos ciudades de la población */
    private double getDMin(){
        double dMin = Integer.MAX_VALUE;
        
        for(Ciudad c : pob.getCiudades()){
            for(Double f : c.getDistancias().values()){
                if(f < dMin) dMin = f;
            }
        }
        
        return dMin;
    }
    
    /* Aumentamos la frecuencia entre dos ciudades */
    public void visitar(int origen, int destino){
        Point point;
        if( origen > destino) point = new Point(origen, destino);
        else point = new Point(destino, origen);
        
        if(!frec.containsKey(point)) frec.put(point, 1);
        else{
            int aux = frec.get(point) + 1;
            frec.put(point, aux);
            if(aux > Fmax) Fmax = aux;
        }
    }
    
    /**
     * Esta función crea una lista nueva de elementos en base a la población que
     * se le pasó como argumento a la matriz cuando la creamos, y los modifica con
     * los nuevos valores de distancias de acuerdo a las frecuencias y al grado de
     * alteración elegido, también durante la creación de la matriz.
     */
    public ArrayList<Ciudad> poblacionPonderada(){
        ArrayList<Ciudad> ciudades = new ArrayList();
        Ciudad c;
        Point p;
        double aux;
        
        for(int i = 0; i < pob.getCiudades().size(); i++){
            c = new Ciudad(pob.getCiudades().get(i).getIndice(), pob.getCiudades().get(i).getDistancias());
            for(int j = 0; j <= i; j++){
                p = new Point(i + 1, j);
                aux = c.getDistancia(j) + this.GRADO_DE_ALTERACION * this.D * (this.frec.getOrDefault(p, 0) / Fmax);
                c.getDistancias().put(j, aux);
            }
            ciudades.add(c);
        }
        
        return ciudades;
    }
    
    public void limpiar(){
        this.frec.clear();
    }
}
