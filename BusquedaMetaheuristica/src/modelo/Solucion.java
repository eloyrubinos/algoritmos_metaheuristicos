package modelo;

import java.util.ArrayList;

/**
 * @author Eloy
 * 
 * Esta clase viene motivada por la computación evolutiva. Para este algoritmo
 * resulta muy interesante disponer en una sola clase tanto de la solución como
 * de su coste, y esa es la funcionalidad que ofrece la clase Solucion.
 * 
 * De este modo no hay que calcular el coste de una solución cada vez que se
 * quiere comparar, algo que ralentizaría mucho el algoritmo.ç
 * Además, también nos facilita el ordenar las soluciones en base al coste.
 */

public class Solucion {
    public ArrayList<Integer> solucion;
    public double coste;
    
    public Solucion() {
        solucion = new ArrayList();
        coste = -1F;
    }
    
    public Solucion(Solucion s) {
        solucion = new ArrayList(s.solucion);
        coste = s.coste;
    }

    public ArrayList<Integer> getSolucion() {
        return solucion;
    }

    public void setSolucion(ArrayList<Integer> solucion) {
        this.solucion = new ArrayList(solucion);
    }

    public double getCoste() {
        return coste;
    }

    public void setCoste(float coste) {
        this.coste = coste;
    }
    
}
