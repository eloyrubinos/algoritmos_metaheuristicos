package modelo;

import java.awt.Point;
import java.util.ArrayList;

/**
 * @author Eloy
 * 
 * Esta clase implementa el funcionamiento de una lista tabú, con un array de pares de índices,
 * un array que almacena el tiempo de vida (en enteros) de cada par de índices en cada momento
 * y la tenencia, que es el tiempo de vida que se le asigna a dichos pares.
 * 
 * El array de pares y el array de tiempo de vida son correlativos y su lógica se mantiene dentro
 * de la propia clase, asegurando que en el mismo índice de cada array se hace referencia al mismo dato
 * lógico.
 * 
 */

public class ListaTabu {
    private int tenencia;
    private ArrayList<Point> lista;
    private ArrayList<Integer> vida;
    
    /**
     * Para crear una instancia de la clase es obligatorio saber qué tenencia
     * va a manejar, y se le pasa como argumento al constructor.
     * 
     */
    public ListaTabu(int tenencia){
        this.tenencia = tenencia;
        lista = new ArrayList();
        vida = new ArrayList();
    }
    
    public ListaTabu(ListaTabu lt){
        this.tenencia = lt.getTenencia();
        this.lista = new ArrayList(lt.getLista());
        this.vida = new ArrayList(lt.getVida());
    }
    
    public int getTenencia(){
        return this.tenencia;
    }
    
    public ArrayList<Point> getLista(){
        return this.lista;
    }
    
    public ArrayList<Integer> getVida(){
        return this.vida;
    }
    
    /**
     * Función que llamamos para agregar un par de índices a la lista.
     * Añade el par en sí al array de índices y después pone el tiempo
     * de vida correlativo de ese par a 0.
     * 
     * Al añadir SIEMPRE ambos datos en la misma función, nos aseguramos de
     * que para un índice determinado ambos arrays hacen referencia al mismo par.
     * 
     */
    public void visitar(Point p){
        Point paux = new Point(p.x, p.y);
        this.lista.add(paux);
        this.vida.add(0);
    }
    
    /**
     * Esta función aumenta el tiempo de vida de cada par de índices en 1.
     * 
     * Al mismo tiempo comprueba si el tiempo de vida de un par determinado
     * iguala la tenencia de la instancia de ListaTabu, en cuyo caso borra ese
     * elemento tanto del array de tiempos como del array de pares, asegurando
     * así, de nuevo, la integridad de los índices correlativos.
     * 
     */
    public void iterar(){
        ArrayList<Integer> indices = new ArrayList(this.vida.size());
        this.vida.forEach(
                e -> {
                    if(e < this.tenencia) this.vida.set(this.vida.indexOf(e), this.vida.get(this.vida.indexOf(e)) + 1);
                    else{
                        this.lista.remove(this.vida.indexOf(e));
                        indices.add(this.vida.indexOf(e));
                    }
                }
        );
        indices.forEach(e -> this.vida.remove((int)e));
    }
    
    public void limpiar(){
        this.lista.clear();
        this.vida.clear();
    }
}