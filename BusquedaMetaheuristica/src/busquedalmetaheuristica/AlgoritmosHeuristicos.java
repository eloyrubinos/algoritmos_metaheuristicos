package busquedalmetaheuristica;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.Random;
import modelo.*;
import static modelo.Traza.*;

/* Clase que encapsula algoritmos */
public class AlgoritmosHeuristicos {
    
    /**
     * Constantes que resulta interesante poder cambiar de forma rápida.
     * 
     * TENENCIA = Tiempo de vida de los elementos en la lista tabú.
     * ITE = Número de iteraciones que se usa como condición de parada.
     * MU = Grado de alteración que aplica la matriz de frecuencias sobre las distancias.
     * REINICIO = Condición de disparo para las estrategias de reinicialización,
     * cuando corresponda.
     * 
     * MU_TS = Constante usada en temple simulado como parte del criterio de aceptación de soluciones peores.
     * PHI = Constante usada en temple simulado como parte del criterio de aceptación de soluciones peores.
     * k = Número de veces que se ha realizado enfriamiento en el temple simulado.
     * TEMPLE = Temperatura actual en temnple simulado.
     * 
     * PC = Probabilidad de cruce en computación evolutiva.
     * PM = Probabilidad de mutación en computación evolutiva.
     */
    
    private static final int ITE = 1000;
    
    private static final int TENENCIA = 100;
    private static final long REINICIO = Math.round(ITE * 0.01);
    private static final float MU = 0.3F;
    
    private static int ITE_ACTUAL = 0;
    private static final double MU_TS = 0.01;
    private static final double PHI = 0.5;
    private static int k = 0;
    private static double TEMPLE;
    
    private static final double PC = 0.9;
    private static final double PM = 0.01;
    
    
    
    /* Cálculo del coste recorriendo un array de soluciones */
    private static double coste(ArrayList<Integer> solucionLocal, Poblacion p){
        double coste = p.getDistancia(0, solucionLocal.get(0));
        int i;
        for(i = 0; i < solucionLocal.size() - 1; i++){
            coste += p.getDistancia(solucionLocal.get(i), solucionLocal.get(i + 1));
        }
        coste += p.getDistancia(solucionLocal.get(i), 0);
        return coste;
    }
    
    /**
     * Cálculo del coste sabiendo el coste de una solución y el movimiento que se ha realizado
     * sobre la misma. Es decir, se calcula el componente del coste que ha cambiado, no todo.
     * 
     * Esta implementación hay que hacerla con cuidado, teniendo en cuenta varias cosas:
     *      1) si el índice X es el fin de array, su adyacente por la derecha es el punto final
     *      del trayecto, que es un detalle de implementación: la ciudad 0.
     *      2) si el índice Y es el inicio del array, su adyacente por la izquierda es el punto
     *      inicial del trayecto, que es un detalle de implementación: la ciudad 0.
     *      3) no hay que tener en cuenta la situación inversa porque, por cómo se implementa
     *      el algoritmo, X siempre va a ser mayor que 0 e Y siempre va a ser menor que la
     *      última posición del array.
     * 
     *      4) si X e Y son adyacentes para un caso particular, el cálculo cambia ligeramente,
     *      ya que se hace innecesario y además se presta a errores el sumar y restar las distancias
     *      del uno al otro.
     */
    private static double coste(ArrayList<Integer> solucionLocal, Poblacion p, double costeBase, Point point){
        double coste = costeBase;
        int xMasUno, yMenosUno;
        
        /* Aquí trato el caso de que los índices estén en algún extremo del array */
        if(point.x == solucionLocal.size() - 1) xMasUno = 0;
        else xMasUno = solucionLocal.get(point.x + 1);
        if(point.y == 0) yMenosUno = 0;
        else yMenosUno = solucionLocal.get(point.y - 1);
        
        /* Aquí hago los cálculos que se harían siempre */
        coste -= p.getDistancia(solucionLocal.get(point.x), xMasUno);
        coste -= p.getDistancia(yMenosUno, solucionLocal.get(point.y)); 
        coste += p.getDistancia(solucionLocal.get(point.y), xMasUno);
        coste += p.getDistancia(yMenosUno, solucionLocal.get(point.x));
        
        /* Aquí hago los cálculos que solo se hacen cuando los índices NO son adyacentes */
        if(point.x != point.y + 1){
            coste -= p.getDistancia(solucionLocal.get(point.x - 1), solucionLocal.get(point.x));
            coste -= p.getDistancia(solucionLocal.get(point.y), solucionLocal.get(point.y + 1));
            coste += p.getDistancia(solucionLocal.get(point.x - 1), solucionLocal.get(point.y));
            coste += p.getDistancia(solucionLocal.get(point.x), solucionLocal.get(point.y + 1));
        }
        
        return coste;
    }
    
    /* Función que me calcula la temperatura inicial para el temple simulado */
    private static void templeInicial(double coste) {
        TEMPLE = (MU_TS/-Math.log(PHI)) * coste;
    }
    
    /* Función de enfriamiento de la especificación obligatoria */
    private static void enfriar(double temple) {
        k++;
        TEMPLE = temple / (k + 1);
    }
    
    /* Función de enfriamiento voluntaria*/
    private static void enfriarVoluntario(double temple) {
        k++;
        TEMPLE = temple / (Math.log10(k) + 1);
    }
    
    /* Generación de la solución inicial totalmente aleatoria */
    private static void genSolIni(ArrayList<Integer> solucionLocal, int vecinos){
        ArrayList<Point> visitados = new ArrayList();
        Point p;
        Random rnd = new Random();
        int aux;
        
        while(visitados.size() < vecinos){
            p = new Point(rnd.nextInt(solucionLocal.size()), rnd.nextInt(solucionLocal.size()));
            if (p.y > p.x){
                aux = p.x;
                p.x = p.y;
                p.y = aux;
            }
            while(visitados.contains(p)){
                if(p.y < p.x) p.y++;
                else if(p.y == p.x){
                    p.y = 0;
                    if(p.x == solucionLocal.size() - 1) p.x = 0;
                    else p.x++;
                }
            }
            visitados.add(p);
            aux = solucionLocal.get(p.x);
            solucionLocal.set(p.x, solucionLocal.get(p.y));
            solucionLocal.set(p.y, aux);
        }
    }
     
    /* Otra forma de generar soluciones iniciales aleatorias*/
    private static void genSolIni2(ArrayList<Integer> solucion, int poblacion) {
        Random rnd = new Random();
        int aux, i;
        
        for(i = 0; i < poblacion; i++){
            aux = 1 + rnd.nextInt(poblacion);
            while(solucion.contains(aux)){
                if(aux == poblacion) aux = 1;
                else aux++;
            }
            solucion.add(aux);
        }
    }
    
    /* Generación voraz de la mejor solución inicial */
    private static void genSolIni(ArrayList<Integer> solucionLocal, Poblacion pob){
        ArrayList<Integer> inicial = new ArrayList();
        ArrayList<Integer> visitados = new ArrayList();
        int idCiudad;


        idCiudad = 0;
        visitados.add(idCiudad);
        while(inicial.size() < pob.getCiudades().size()){
            idCiudad = pob.minDist(idCiudad, visitados);
            visitados.add(idCiudad);
            inicial.add(idCiudad);
        }
        solucionLocal.clear();
        solucionLocal.addAll(inicial);
    }
    
    /* Generación voraz de la mejor solución inicial con los dos primeros elementos aleatorios */
    private static void genSolIniParcialmenteVoraz(ArrayList<Integer> solucionLocal, Poblacion pob){
        ArrayList<Integer> inicial = new ArrayList();
        ArrayList<Integer> visitados = new ArrayList();
        Random rnd = new Random();
        int idCiudad;

        idCiudad = 0;
        visitados.add(idCiudad);
        
        idCiudad = rnd.nextInt(1 + pob.getCiudades().size());
        visitados.add(idCiudad);
        inicial.add(idCiudad);
        
        idCiudad = rnd.nextInt(1 + pob.getCiudades().size());
        while(visitados.contains(idCiudad)) idCiudad = rnd.nextInt(1 + pob.getCiudades().size());
        visitados.add(idCiudad);
        inicial.add(idCiudad);
        
        while(inicial.size() < pob.getCiudades().size()){
            idCiudad = pob.minDist(idCiudad, visitados);
            visitados.add(idCiudad);
            inicial.add(idCiudad);
        }
        solucionLocal.clear();
        solucionLocal.addAll(inicial);
    }
    
    /* Generación voraz de la mejor solución inicial con el primer elemento aleatorio para computación evolutiva en modo aleatorio */
    private static void genSolIniVorazCE(ArrayList<Integer> solucionLocal, Poblacion pob){
        ArrayList<Integer> inicial = new ArrayList();
        ArrayList<Integer> visitados = new ArrayList();
        Random rnd = new Random();
        int idCiudad;

        idCiudad = 0;
        visitados.add(idCiudad);
        
        idCiudad = 1 + rnd.nextInt(pob.getCiudades().size());
        visitados.add(idCiudad);
        inicial.add(idCiudad);
        
        while(inicial.size() < pob.getCiudades().size()){
            idCiudad = pob.minDist(idCiudad, visitados);
            visitados.add(idCiudad);
            inicial.add(idCiudad);
        }
        solucionLocal.clear();
        solucionLocal.addAll(inicial);
    }
    
    /* Función de inicialización voraz con primeros X elementos aleatorios */
    private static void genSolIniVorazCE2(ArrayList<Integer> solucionLocal, Poblacion pob, int aleatorios){
        ArrayList<Integer> inicial = new ArrayList();
        ArrayList<Integer> visitados = new ArrayList();
        Random rnd = new Random();
        int idCiudad, cont = aleatorios;

        idCiudad = 0;
        visitados.add(idCiudad);
        
        while(cont > 0) {
            idCiudad = 1 + rnd.nextInt(pob.getCiudades().size());
            while(visitados.contains(idCiudad)) idCiudad = 1 + rnd.nextInt(pob.getCiudades().size());
            visitados.add(idCiudad);
            inicial.add(idCiudad);
            cont--;
        }
              
        while(inicial.size() < pob.getCiudades().size()){
            idCiudad = pob.minDist(idCiudad, visitados);
            visitados.add(idCiudad);
            inicial.add(idCiudad);
        }
        solucionLocal.clear();
        solucionLocal.addAll(inicial);
    }
    
    /* Versión determinista de genSolIniVorazCE */
    private static int genSolIniVorazCE(int index, ArrayList<Integer> solucionLocal, Poblacion pob, ArchivoAleatorios alea){
        ArrayList<Integer> inicial = new ArrayList();
        ArrayList<Integer> visitados = new ArrayList();
        Point ciudad = new Point();
        int idCiudad, indice = index;

        idCiudad = 0;
        visitados.add(idCiudad);
        
        indice = alea.nextCiudad(indice, pob.getCiudades().size(), ciudad);
        idCiudad = ciudad.x;
        visitados.add(idCiudad);
        inicial.add(idCiudad);
        
        while(inicial.size() < pob.getCiudades().size()){
            idCiudad = pob.minDist(idCiudad, visitados);
            visitados.add(idCiudad);
            inicial.add(idCiudad);
        }
        solucionLocal.clear();
        solucionLocal.addAll(inicial);
        
        return indice;
    }
            
    /* Generación aleatoria de vecinos para BÚSQUEDA LOCAL según PRIMER MEJOR */
    private static void genVecinos(ArrayList<Integer> solucionLocal, int vecinos, Poblacion pob){
        ArrayList<Point> visitados = new ArrayList();
        ArrayList<Integer> vecino = new ArrayList(solucionLocal);
        Point p;
        Random rnd = new Random();
        int aux, contadorVecinos = 0;
        double coste, costeActual = coste(solucionLocal, pob);

        while(visitados.size() < vecinos){
            p = new Point(rnd.nextInt(vecino.size()), rnd.nextInt(vecino.size()));
            if (p.y > p.x){
                aux = p.x;
                p.x = p.y;
                p.y = aux;
            }
            while(visitados.contains(p) || p.x == p.y){
                if(p.y < p.x) p.y++;
                else if(p.y == p.x){
                    p.y = 0;
                    if(p.x == vecino.size() - 1) p.x = 0;
                    else p.x++;
                }
            }
            visitados.add(p);
            aux = vecino.get(p.x);
            vecino.set(p.x, vecino.get(p.y));
            vecino.set(p.y, aux);
            coste = coste(vecino, pob);
            traza.add("\tVECINO V_"+contadorVecinos+" -> Intercambio: ("+p.x+", "+p.y+"); "+vecino+"; "+(int)coste+"km");
            contadorVecinos++;
            if(coste < costeActual){
                solucionLocal.clear();
                solucionLocal.addAll(vecino);
                break;
            }
            else vecino = new ArrayList(solucionLocal);
        }
    }
    
    /* Generación determinista de vecinos para BÚSQUEDA LOCAL según PRIMER MEJOR */
    private static int genVecinos(ArrayList<Integer> solucionLocal, ArchivoAleatorios alea, int vecinos, Poblacion pob, int indice){
        ArrayList<Point> visitados = new ArrayList();
        int aux, index = indice, contadorVecinos = 0;
        double coste;
        
        double costeActual = coste(solucionLocal, pob);
        ArrayList<Integer> vecino = new ArrayList(solucionLocal);
        while(visitados.size() < vecinos){
            Point p = new Point();
            index = alea.getAleatoriosVecinos(pob.getCiudades().size(), index, p);
            if (p.y > p.x){
                aux = p.x;
                p.x = p.y;
                p.y = aux;
            }
            while(visitados.contains(p) || p.x == p.y){
                if(p.y < p.x) p.y++;
                else if(p.y == p.x){
                    p.y = 0;
                    if(p.x == vecino.size() - 1) p.x = 0;
                    else p.x++;
                }
            }
            visitados.add(p);
            aux = vecino.get(p.x);
            vecino.set(p.x, vecino.get(p.y));
            vecino.set(p.y, aux);
            coste = coste(vecino, pob);
            traza.add("\tVECINO V_"+contadorVecinos+" -> Intercambio: ("+p.x+", "+p.y+"); "+vecino+"; "+(int)coste+"km");
            contadorVecinos++;
            if(coste < costeActual){
                solucionLocal.clear();
                solucionLocal.addAll(vecino);
                break;
            }
            else vecino = new ArrayList(solucionLocal);
        }
        return index;
    }
    
    /* Generación voraz de vecinos para BÚSQUEAD TABÚ según EL MEJOR */
    private static void genVecinos(ArrayList<Integer> solucionLocal, Poblacion pob, Point point, ListaTabu lt){
        int aux, i = 1, j = 0;
        double coste, costeActual = Integer.MAX_VALUE, costeReferencia = coste(solucionLocal, pob);
        ArrayList<Integer> vecino = new ArrayList(solucionLocal);
        ArrayList <Integer> optimoLocal = new ArrayList();
        Point p;
        
        while(i < solucionLocal.size()){
            p = new Point(i, j);
            if(!lt.getLista().contains(p)){
                coste = coste(solucionLocal, pob, costeReferencia, p);
                if(coste < costeActual){
                    point.x = p.x;
                    point.y = p.y;
                    aux = vecino.get(p.x);
                    vecino.set(p.x, vecino.get(p.y));
                    vecino.set(p.y, aux);
                    optimoLocal = new ArrayList(vecino);
                    costeActual = coste;
                    vecino = new ArrayList(solucionLocal);
                }    
            }
            if(j < i-1) j++;
            else {
                j = 0;
                i++;
            }
        }
        solucionLocal.clear();
        solucionLocal.addAll(optimoLocal);   
    }
    
    /* Generación voraz MEJORADA de vecinos para BÚSQUEAD TABÚ según EL MEJOR */
    private static void genVecinosVoluntaria(ArrayList<Integer> solucionLocal, Poblacion pob, Point point, ListaTabu lt, double costeOptimo){
        int aux, i = 1, j = 0;
        double coste, costeActual = Integer.MAX_VALUE, costeReferencia = coste(solucionLocal, pob);
        ArrayList<Integer> vecino = new ArrayList(solucionLocal);
        ArrayList <Integer> optimoLocal = new ArrayList(solucionLocal);
        Point p;

        while(i < solucionLocal.size()){
            p = new Point(i, j);
            coste = coste(solucionLocal, pob, costeReferencia, p);
            
            /* Implemento aspiración cuando el coste mejora el óptimo actual */
            if((coste < costeOptimo && costeOptimo <= costeActual) || (coste < costeActual && !lt.getLista().contains(p))){
                point.x = p.x;
                point.y = p.y;
                aux = vecino.get(p.x);
                vecino.set(p.x, vecino.get(p.y));
                vecino.set(p.y, aux);
                optimoLocal = new ArrayList(vecino);
                costeActual = coste;
                vecino = new ArrayList(solucionLocal);
            }

            if(j < i-1) j++;
            else {
                j = 0;
                i++;
            }
        }
        solucionLocal.clear();
        solucionLocal.addAll(optimoLocal);   
    }
    
    /* Generación aleatoria de vecinos para TEMPLE SIMULADO */
    private static void genVecinosTS(ArrayList<Integer> solucionLocal, Poblacion pob, double costeMejor, double temple0, Point contEnfriamiento){
        boolean genTraza = false;
        int x = 0, insercion = 0, insercionElegida = 0, insercionAux, ciudadElegida = 0;
        double exp = Integer.MIN_VALUE, delta = Integer.MIN_VALUE, coste, costeActual = Integer.MAX_VALUE, costeBase = coste(solucionLocal, pob);
        ArrayList<Integer> solucion = new ArrayList();
        ArrayList<Integer> aux;
        Random rnd = new Random();

        while(costeMejor <= costeActual && 0.1 >= exp && ITE_ACTUAL < ITE) {
            
            if(genTraza) {
                genTrazaTS(ITE_ACTUAL, x, ciudadElegida, insercionElegida, solucion, (int)costeActual, delta, TEMPLE, exp);
                genTrazaTS(contEnfriamiento);
            }
            else genTraza = !genTraza;
            
            x = rnd.nextInt(solucionLocal.size());
            
            if(contEnfriamiento.x == 80 || contEnfriamiento.y == 20) {
                contEnfriamiento.x = 0;
                contEnfriamiento.y = 0;
                enfriar(temple0);
                genTrazaTS(k, TEMPLE);
            }
            
            costeActual = Integer.MAX_VALUE;

            while(insercion <= solucionLocal.size()){
                if(insercion != x && insercion != x + 1) {
                    aux = new ArrayList(solucionLocal);
                    insercionAux = insercion;
                    ciudadElegida = aux.get(x);
                    if(insercion == solucionLocal.size()) {
                        aux.add(ciudadElegida);
                        aux.remove(x);
                        insercionAux--;
                    }
                    else {
                        aux.add(insercion, ciudadElegida);
                        if(insercion > x) {
                            aux.remove(x);
                            insercionAux--;
                        }
                        else aux.remove(x + 1);
                    }
                    
                    if((coste = coste(aux, pob)) < costeActual) {
                        costeActual = coste;
                        insercionElegida = insercionAux;
                        solucion = new ArrayList(aux);
                    }
                }
                insercion++;
            }
            
            
            delta = costeActual - costeMejor;
            exp = Math.pow(Math.E, -delta / TEMPLE);
            insercion = 0;
            ITE_ACTUAL++;
            contEnfriamiento.x++;
        }
        contEnfriamiento.y++;
        genTrazaTS(ITE_ACTUAL, x, ciudadElegida, insercionElegida, solucion, (int)costeActual, delta, TEMPLE, exp);
        genTrazaTS();
        genTrazaTS(contEnfriamiento);

        solucionLocal.clear();
        solucionLocal.addAll(solucion);
    }
    
    /* Generación determinista de vecinos para TEMPLE SIMULADO */
    private static int genVecinosTS(ArrayList<Integer> solucionLocal, ArchivoAleatorios alea, Poblacion pob, int indice, double costeMejor, double temple0, Point contEnfriamiento){
        boolean genTraza = false;
        int index = indice, insercion = 0, insercionElegida = 0, insercionAux, ciudadElegida = 0;
        double exp = Integer.MIN_VALUE, delta = Integer.MIN_VALUE, coste, costeActual = Integer.MAX_VALUE, costeBase = coste(solucionLocal, pob);
        ArrayList<Integer> solucion = new ArrayList(); 
        ArrayList<Integer> aux;
        Point2D.Double p = new Point2D.Double(0.0, 0.0);
        
        /* Se acepta una solución candidata cuando:
            1) su coste mejora al óptimo actual
            2) p.y < exp
        */
        while(costeMejor <= costeActual && p.y >= exp && ITE_ACTUAL < ITE) {
            
            if(genTraza) {
                genTrazaTS(ITE_ACTUAL, (int)p.x, ciudadElegida, insercionElegida, solucion, (int)costeActual, delta, TEMPLE, exp);
                genTrazaTS(contEnfriamiento);
            }
            else genTraza = !genTraza;
            
            p = new Point2D.Double();
            index = alea.getAleatoriosVecinosTS(pob.getCiudades().size(), index, p);
            
            if(contEnfriamiento.x == 80 || contEnfriamiento.y == 20) {
                contEnfriamiento.x = 0;
                contEnfriamiento.y = 0;
                enfriar(temple0);
                genTrazaTS(k, TEMPLE);
                System.out.println("Enfriamos!");
            }
            
            costeActual = Integer.MAX_VALUE;
            
//            while(insercion <= solucionLocal.size()){
//                //coste = coste(solucionLocal, pob, costeBase, (int)p.x, insercion);
//                if(insercion != (int)p.x && insercion != (int)p.x + 1 && (coste = coste(solucionLocal, pob, costeBase, (int)p.x, insercion)) < costeActual) {
//                    insercionElegida = insercion;
//                    costeActual = coste;
//                    solucion = new ArrayList(solucionLocal);
//                    ciudadElegida = solucion.get((int)p.x);
//                    if(insercion == solucionLocal.size()) {
//                        solucion.add(ciudadElegida);
//                        solucion.remove((int)p.x);
//                        insercionElegida--;
//                        System.out.println("c1: "+coste+", c2: "+coste(solucion, pob));
//                    }
//                    else {
//                        solucion.add(insercion, ciudadElegida);
//                        if(insercion > (int)p.x) {
//                            solucion.remove((int)p.x);
//                            insercionElegida--;
//                        }
//                        else solucion.remove((int)p.x + 1);
//                    }
//                    System.out.println("c1: "+coste+", c2: "+coste(solucion, pob));
//                }
////                if(ITE_ACTUAL == 91) {
////                    aux = new ArrayList(solucionLocal);
////                    aux.add(insercion, aux.get((int)p.x));
////                    if(insercion > (int)p.x) {
////                        aux.remove((int)p.x);
////                    }
////                    else aux.remove((int)p.x + 1);
////                    System.out.println("i: "+insercion+", c1: "+coste+", c2: "+coste(aux, pob));
////                }
//                insercion++;
//            }

            while(insercion <= solucionLocal.size()){
                if(insercion != (int)p.x && insercion != (int)p.x + 1) {
                    aux = new ArrayList(solucionLocal);
                    insercionAux = insercion;
                    ciudadElegida = aux.get((int)p.x);
                    if(insercion == solucionLocal.size()) {
                        aux.add(ciudadElegida);
                        aux.remove((int)p.x);
                        insercionAux--;
                    }
                    else {
                        aux.add(insercion, ciudadElegida);
                        if(insercion > (int)p.x) {
                            aux.remove((int)p.x);
                            insercionAux--;
                        }
                        else aux.remove((int)p.x + 1);
                    }
                    
                    if((coste = coste(aux, pob)) < costeActual) {
                        costeActual = coste;
                        insercionElegida = insercionAux;
                        solucion = new ArrayList(aux);
                    } 
                    //System.out.println("c1: "+coste+", c2: "+coste(solucion, pob));
                }
                insercion++;
            }
            
            System.out.println("Candidato elegido.");
            
            delta = costeActual - costeMejor;
            exp = Math.pow(Math.E, -delta / TEMPLE);
            insercion = 0;
            ITE_ACTUAL++;
            contEnfriamiento.x++;
        }
        contEnfriamiento.y++;
        System.out.println("Candidato aceptado.");
        genTrazaTS(ITE_ACTUAL, (int)p.x, ciudadElegida, insercionElegida, solucion, (int)costeActual, delta, TEMPLE, exp);
        genTrazaTS();
        genTrazaTS(contEnfriamiento);

        solucionLocal.clear();
        solucionLocal.addAll(solucion);
        
        return index;
    }
    
    /* Generación aleatoria MEJORADA de vecinos para TEMPLE SIMULADO */
    private static void genVecinosTSVoluntaria(ArrayList<Integer> solucionLocal, Poblacion pob, double costeMejor, double temple0, Point contEnfriamiento){
        boolean genTraza = false;
        int x = 0, insercion = 0, insercionElegida = 0, insercionAux, ciudadElegida = 0;
        double exp = Integer.MIN_VALUE, delta = Integer.MIN_VALUE, coste, costeActual = Integer.MAX_VALUE, costeBase = coste(solucionLocal, pob);
        ArrayList<Integer> solucion = new ArrayList(); 
        ArrayList<Integer> visitados = new ArrayList();
        ArrayList<Integer> aux;
        Random rnd = new Random();
        
        while(costeMejor <= costeActual && rnd.nextDouble() >= exp && ITE_ACTUAL < ITE && visitados.size() < solucionLocal.size()) {
            
            if(genTraza) {
                genTrazaTS(ITE_ACTUAL, x, ciudadElegida, insercionElegida, solucion, (int)costeActual, delta, TEMPLE, exp);
                genTrazaTS(contEnfriamiento);
            }
            else genTraza = !genTraza;
            
            x = rnd.nextInt(solucionLocal.size());
            while(visitados.contains(x)){
                if(x < solucionLocal.size() - 1) x++;
                else x = 0;
            }
            visitados.add(x);
            
            if(contEnfriamiento.x == 80 || contEnfriamiento.y == 20) {
                contEnfriamiento.x = 0;
                contEnfriamiento.y = 0;
                enfriarVoluntario(temple0);
                genTrazaTS(k, TEMPLE);
            }
            
            costeActual = Integer.MAX_VALUE;

            while(insercion <= solucionLocal.size()){
                if(insercion != x && insercion != x + 1) {
                    aux = new ArrayList(solucionLocal);
                    insercionAux = insercion;
                    ciudadElegida = aux.get(x);
                    if(insercion == solucionLocal.size()) {
                        aux.add(ciudadElegida);
                        aux.remove(x);
                        insercionAux--;
                    }
                    else {
                        aux.add(insercion, ciudadElegida);
                        if(insercion > x) {
                            aux.remove(x);
                            insercionAux--;
                        }
                        else aux.remove(x + 1);
                    }
                    
                    if((coste = coste(aux, pob)) < costeActual) {
                        costeActual = coste;
                        insercionElegida = insercionAux;
                        solucion = new ArrayList(aux);
                    }
                }
                insercion++;
            }
            
            delta = costeActual - costeMejor;
            exp = Math.pow(Math.E, -delta / TEMPLE);
            insercion = 0;
            ITE_ACTUAL++;
            contEnfriamiento.x++;
        }
        contEnfriamiento.y++;
        genTrazaTS(ITE_ACTUAL, x, ciudadElegida, insercionElegida, solucion, (int)costeActual, delta, TEMPLE, exp);
        genTrazaTS();
        genTrazaTS(contEnfriamiento);

        solucionLocal.clear();
        solucionLocal.addAll(solucion);
    }
    
    /* Función principal de búsqueda local con primer mejor y en modo aleatorio */
    public static void buscaLocal(Poblacion p){
        int contadorSoluciones = 1, vecinos;
        double costeMejor, costeAux;
        boolean seguir;
        
        /* Obtengo el espacio de elementos */
        ArrayList<Integer> solucion = new ArrayList();
        for(Ciudad c : p.getCiudades()){
            solucion.add(c.getIndice());
        }
        
        /* Cuántos vecinos puedo generar */
        vecinos = ((solucion.size()) * (solucion.size()-1)) / 2;

        System.out.println("Entrada: "+solucion+"\n");
        /* Generar solución inicial */
        genSolIni(solucion, vecinos);
                
        costeMejor = coste(solucion, p);
        System.out.println("SOLUCION S_0 -> "+solucion+"; "+(int)costeMejor+"km");
        traza.add("SOLUCION S_0 -> "+solucion+"; "+(int)costeMejor+"km");

        /* Generamos una solución mejor mientras podamos */
        seguir = true;
        while(seguir){
            genVecinos(solucion, vecinos, p);
            costeAux = coste(solucion, p);
            if(costeAux == costeMejor) seguir = !seguir;
            else{
                System.out.println("\nSOLUCION S_"+contadorSoluciones+" -> "+solucion+"; "+(int)costeAux+"km");
                traza.add("\nSOLUCION S_"+contadorSoluciones+" -> "+solucion+"; "+(int)costeAux+"km");
                contadorSoluciones++;
                costeMejor = costeAux;
            }
        }
    }
    
    /* Función principal de búsqueda local con primer mejor y en modo determinista (leyendo índices de un archivo) */
    public static void buscaLocal(Poblacion p, ArchivoAleatorios alea) {
        int index, contadorSoluciones = 1, vecinos;
        double costeMejor, costeAux;
        boolean seguir;
        
        /* Obtengo solución inicial */
        ArrayList<Integer> solucion = new ArrayList();
        index = alea.getAleatoriosInicial(p.getCiudades().size(), solucion);
        
        
        costeMejor = coste(solucion, p);
        System.out.println("SOLUCION S_0 -> "+solucion+"; "+(int)costeMejor+"km");
        traza.add("SOLUCION S_0 -> "+solucion+"; "+(int)costeMejor+"km");
        
        /* Cuántos vecinos puedo generar */
        vecinos = ((solucion.size()) * (solucion.size()-1)) / 2;

        /* Generamos una solución mejor mientras podamos */
        seguir = true;
        while(seguir){
            index = genVecinos(solucion, alea, vecinos, p, index);
            costeAux = coste(solucion, p);
            if(costeAux == costeMejor) seguir = !seguir;
            else{
                System.out.println("\nSOLUCION S_"+contadorSoluciones+" -> "+solucion+"; "+(int)costeAux+"km");
                traza.add("\nSOLUCION S_"+contadorSoluciones+" -> "+solucion+"; "+(int)costeAux+"km");
                contadorSoluciones++;
                costeMejor = costeAux;
            }
        }
    }
    
    /* Función principal de búsqueda tabú en modo aleatorio */
    public static void buscaTabu(Poblacion p){
        int vecinos, sinMejora, contParada, contReinicio, itMejor;
        double costeMejor, costeAux;
        Point point;
        ArrayList<Integer> solucion = new ArrayList();
        ArrayList<Integer> optimoGlobal;
        ListaTabu lt = new ListaTabu(TENENCIA);
        
        /* Obtengo el espacio de elementos */
        for(Ciudad c : p.getCiudades()){
            solucion.add(c.getIndice());
        }
        
        /* Cuántos vecinos puedo generar */
        vecinos = ((solucion.size()) * (solucion.size()-1)) / 2;
        
        /* Generar solución inicial */
        genSolIni(solucion, vecinos);
        
        optimoGlobal = new ArrayList(solucion);
        costeMejor = coste(optimoGlobal, p);
        itMejor = 0;
        
        genTraza(solucion, (int)costeMejor);

        /* Generamos una solución mejor mientras podamos */
        point = new Point();
        contParada = 1;
        sinMejora = 0;
        contReinicio = 0;
        while(contParada <= ITE){
            System.out.println("It"+contParada);
            if(sinMejora == 100){
                System.out.println("Reiniciamos!");
                contReinicio++;
                solucion = new ArrayList(optimoGlobal);
                sinMejora = 0;
                genTraza(contReinicio);
                lt.limpiar();
            }
            genVecinos(solucion, p, point, lt);
            costeAux = coste(solucion, p);
            System.out.println("Buscando...");
            if(costeAux < costeMejor) {
                optimoGlobal = new ArrayList(solucion);
                costeMejor = costeAux;
                itMejor = contParada;
                sinMejora = 0;
                System.out.println("Nuevo óptimo!");
            }
            else sinMejora++;
            lt.visitar(point);
            lt.iterar();
            
            genTraza(contParada, point, solucion, (int)costeAux, sinMejora, lt);
            contParada++;   
        }
        genTraza(itMejor, optimoGlobal, (int)costeMejor);
        System.out.println("Terminamos!\n");
    }
    
    /* Función MEJORADA de búsqueda tabú en modo aleatorio */
    public static void buscaTabuVoluntaria(Poblacion p){
        int sinMejora, contParada, contReinicio, itMejor;
        double costeMejor, costeAux;
        Point point;
        ArrayList<Integer> solucion = new ArrayList();
        ArrayList<Integer> optimoGlobal;
        ListaTabu lt = new ListaTabu(TENENCIA);
        Deque<ArrayList> optimos = new ArrayDeque();
        Deque<ListaTabu> tabus = new ArrayDeque();
        MatrizFrec frec = new MatrizFrec(p, MU);
        Poblacion pobDiver; // Población que usaré para la matriz de distancias ponderada en base a la matriz de frecuencias

        /* Generar solución inicial */
        genSolIni(solucion, p);
        
        optimoGlobal = new ArrayList(solucion);
        costeMejor = coste(optimoGlobal, p);
        
        genTraza(solucion, (int)costeMejor);
        
        /* Generamos una solución mejor mientras podamos */
        point = new Point();
        contParada = 1;
        sinMejora = 0;;
        contReinicio = 0;
        itMejor = 0;
        while(contParada <= ITE){
            System.out.println("It"+contParada);
            if(sinMejora == REINICIO){
                System.out.println("Reiniciamos!");
                contReinicio++;

                /* Implemento reincialización por intensificación */
                if(!optimos.isEmpty()) {
                    solucion = new ArrayList(optimos.pollFirst());
                    lt = new ListaTabu(tabus.pollFirst());
                    System.out.println("Ser intensos nunca está de más...");
                }
                /* o por diversificación cuando la pila esté vacía */
                else{
                    /* Implemento diversificación por matriz de frecuencias */
                    pobDiver = new Poblacion(frec.poblacionPonderada());
                    genSolIni(solucion, pobDiver);
                    System.out.println("Diversificar mola (Y)");
                }
                sinMejora = 0;
                genTraza(contReinicio); 
            }
            
            genVecinosVoluntaria(solucion, p, point, lt, costeMejor);
            costeAux = coste(solucion, p);
            
            /* Implemento matriz de frecuencias como parte de la memoria a largo plazo */
            frec.visitar(solucion.get(point.x), solucion.get(point.y));
            
            System.out.println("Buscando...");
            if(costeAux < costeMejor) {
                optimoGlobal = new ArrayList(solucion);
                optimos.addFirst(optimoGlobal);
                tabus.addFirst(new ListaTabu(lt));
                costeMejor = costeAux;
                itMejor = contParada;
                sinMejora = 0;
                System.out.println("Nuevo óptimo!");
            }
            else sinMejora++;

            lt.visitar(point);
            lt.iterar();
            
            genTraza(contParada, point, solucion, (int)costeAux, sinMejora, lt);
            contParada++;   
        }
        genTraza(itMejor, optimoGlobal, (int)costeMejor);
    }
    
    /* Función principal de búsqueda tabú en modo determinista */
    public static void buscaTabu(Poblacion p, ArchivoAleatorios alea){
        int sinMejora, contParada, contReinicio, itMejor;
        double costeMejor, costeAux;
        Point point;
        ArrayList<Integer> solucion = new ArrayList();
        ArrayList<Integer> optimoGlobal;
        ListaTabu lt = new ListaTabu(TENENCIA);
        
        /* Generar solución inicial */
        alea.getAleatoriosInicial(p.getCiudades().size(), solucion);
        
        optimoGlobal = new ArrayList(solucion);
        costeMejor = coste(optimoGlobal, p);
        itMejor = 0;
        
        genTraza(solucion, (int)costeMejor);

        /* Generamos una solución mejor mientras podamos */
        point = new Point();
        contParada = 1;
        sinMejora = 0;
        contReinicio = 0;
        while(contParada <= ITE){
            System.out.println("It"+contParada);
            if(sinMejora == 100){
                System.out.println("Reiniciamos!");
                contReinicio++;
                solucion = new ArrayList(optimoGlobal);
                sinMejora = 0;
                genTraza(contReinicio);
                lt.limpiar();
            }
            genVecinos(solucion, p, point, lt);
            costeAux = coste(solucion, p);
            System.out.println("Buscando...");
            if(costeAux < costeMejor) {
                optimoGlobal = new ArrayList(solucion);
                costeMejor = costeAux;
                itMejor = contParada;
                sinMejora = 0;
                System.out.println("Nuevo óptimo!");
            }
            else sinMejora++;
            lt.visitar(point);
            lt.iterar();
            
            genTraza(contParada, point, solucion, (int)costeAux, sinMejora, lt);
            contParada++;   
        }
        genTraza(itMejor, optimoGlobal, (int)costeMejor);
        System.out.println("Terminamos!\n");
    }
    
    /* Función principal de temple simulado en modo aleatorio */
    public static void templeSimulado(Poblacion p){
        int itMejor = 0, vecinos; 
        double costeMejor, costeReferencia, temple0;
        Point contEnfriamiento = new Point(0, 0);
        
        
        ArrayList<Integer> solucion = new ArrayList();
        ArrayList<Integer> optimoGlobal;
        
        /* Generar solución inicial */
        for(Ciudad c : p.getCiudades()){
            solucion.add(c.getIndice());
        }
        vecinos = ((solucion.size()) * (solucion.size()-1)) / 2;
        genSolIni(solucion, vecinos);
        
        optimoGlobal = new ArrayList(solucion);
        costeMejor = coste(optimoGlobal, p);
        costeReferencia = costeMejor;
        
        templeInicial(costeMejor);
        temple0 = TEMPLE;
        
        genTrazaTS(solucion, (int)costeMejor, TEMPLE);

        /* Generamos una solución mejor mientras podamos */
        while(ITE_ACTUAL < ITE){
            genVecinosTS(solucion, p, costeReferencia, temple0, contEnfriamiento);
            costeReferencia = coste(solucion, p);
            if(costeReferencia < costeMejor) {
                optimoGlobal = new ArrayList(solucion);
                costeMejor = costeReferencia;
                itMejor = ITE_ACTUAL;
            }
        }
        genTraza(optimoGlobal, (int)costeMejor, itMejor, MU_TS, PHI);
        System.out.println(costeMejor+", "+k);
        
        ITE_ACTUAL = 0;
        k = 0;
    }
    
    /* Función principal MEJORADA de temple simulado en modo aleatorio */
    public static void templeSimuladoVoluntario(Poblacion p){
        int itMejor = 0, vecinos; 
        double costeMejor, costeReferencia, temple0;
        Point contEnfriamiento = new Point(0, 0);
        
        
        ArrayList<Integer> solucion = new ArrayList();
        ArrayList<Integer> optimoGlobal;
        
        /* Generar solución inicial */
        genSolIniParcialmenteVoraz(solucion, p);
        
        optimoGlobal = new ArrayList(solucion);
        costeMejor = coste(optimoGlobal, p);
        costeReferencia = costeMejor;
        
        templeInicial(costeMejor);
        temple0 = TEMPLE;
        
        genTrazaTS(solucion, (int)costeMejor, TEMPLE);

        /* Generamos una solución mejor mientras podamos */
        while(ITE_ACTUAL < ITE){
            genVecinosTSVoluntaria(solucion, p, costeReferencia, temple0, contEnfriamiento);
            costeReferencia = coste(solucion, p);
            if(costeReferencia < costeMejor) {
                optimoGlobal = new ArrayList(solucion);
                costeMejor = costeReferencia;
                itMejor = ITE_ACTUAL;
            }
        }
        genTraza(optimoGlobal, (int)costeMejor, itMejor, MU_TS, PHI);
        System.out.println("Coste: "+costeMejor+", Iteracion: "+itMejor+", Enfriamientos: "+k);
        
        ITE_ACTUAL = 0;
        k = 0;
    }
    
    /* Función principal de temple simulado en modo determinista */
    public static void templeSimulado(Poblacion p, ArchivoAleatorios alea){
        int indice, itMejor = 0; 
        double costeMejor, costeReferencia, temple0;
        Point contEnfriamiento = new Point(0, 0);
        
        
        ArrayList<Integer> solucion = new ArrayList();
        ArrayList<Integer> optimoGlobal;
        
        /* Generar solución inicial */
        indice = alea.getAleatoriosInicial(p.getCiudades().size(), solucion);

        System.out.println("Solución inicial generada.");
        
        optimoGlobal = new ArrayList(solucion);
        costeMejor = coste(optimoGlobal, p);
        costeReferencia = costeMejor;
        
        templeInicial(costeMejor);
        temple0 = TEMPLE;
        
        genTrazaTS(solucion, (int)costeMejor, TEMPLE);

        System.out.println("Empezamos a buscar.");
        /* Generamos una solución mejor mientras podamos */
        while(ITE_ACTUAL < ITE){
            indice = genVecinosTS(solucion, alea, p, indice, costeReferencia, temple0, contEnfriamiento);
            costeReferencia = coste(solucion, p);
            if(costeReferencia < costeMejor) {
                optimoGlobal = new ArrayList(solucion);
                costeMejor = costeReferencia;
                itMejor = ITE_ACTUAL;
                System.out.println("Encontrado nuevo óptimo!");
            }
        }
        genTraza(optimoGlobal, (int)costeMejor, itMejor, MU_TS, PHI);
        System.out.println("Terminamos!\n");
    }

    
    
    
    
    /* Función que compara soluciones aleatorias entre sí y devuelve un conjunto de soluciones del tamaño indicado en tamTorneo con las ganadoras */
    private static void torneo(ArrayList<Solucion> soluciones, int tamTorneo, int tamPob) {
        ArrayList<Solucion> aux = new ArrayList();
        Random rnd = new Random();
        Point p;
        
        for(int i = 0; i < tamTorneo; i++) {
            p = new Point(rnd.nextInt(tamPob), rnd.nextInt(tamPob));
            if(soluciones.get(p.x).coste <= soluciones.get(p.y).coste) {
                aux.add(new Solucion(soluciones.get(p.x)));
                //genTrazaCE(i, p, p.x);
            }
            else {
                aux.add(new Solucion(soluciones.get(p.y)));
                //genTrazaCE(i, p, p.y);
            }
        }
        
        soluciones.clear();
        soluciones.addAll(aux);
    }
    
    /* Versión del torneo en que se comparan tres soluciones en lugar de dos */
    private static void torneoTernario(ArrayList<Solucion> soluciones, int tamTorneo, int tamPob) {
        ArrayList<Solucion> aux = new ArrayList();
        Random rnd = new Random();
        Point p;
        int w;
        
        for(int i = 0; i < tamTorneo; i++) {
            p = new Point(rnd.nextInt(tamPob), rnd.nextInt(tamPob));
            w = rnd.nextInt(tamPob);
            if(soluciones.get(p.x).coste <= soluciones.get(p.y).coste) {
                if(soluciones.get(p.x).coste <= soluciones.get(w).coste) {
                    aux.add(new Solucion(soluciones.get(p.x)));
                } else aux.add(new Solucion(soluciones.get(w)));
            }
            else {
                if(soluciones.get(p.y).coste <= soluciones.get(w).coste) {
                    aux.add(new Solucion(soluciones.get(p.y)));
                } else aux.add(new Solucion(soluciones.get(w)));
            }
        }
        
        soluciones.clear();
        soluciones.addAll(aux);
    }
    
    /* Versión determinista del torneo binario */
    private static int torneo(int index, ArrayList<Solucion> soluciones, ArchivoAleatorios alea, int tamTorneo, int tamPob) {
        ArrayList<Solucion> aux = new ArrayList();
        Point p;
        int indice = index;
        
        for(int i = 0; i < tamTorneo; i++) {
            p = new Point();
            indice = alea.getAleatoriosTorneo(indice, p, tamPob);
            if(soluciones.get(p.x).coste <= soluciones.get(p.y).coste) {
                aux.add(new Solucion(soluciones.get(p.x)));
                genTrazaCE(i, p, p.x);
            }
            else {
                aux.add(new Solucion(soluciones.get(p.y)));
                genTrazaCE(i, p, p.y);
            }
        }
        
        soluciones.clear();
        soluciones.addAll(aux);
        
        return indice;
    }
    
    /* Función que implementa el cruce entre dos soluciones a partir de un punto de corte, devolviendo en hijo1 e hijo2 las soluciones resultantes */
    private static void cruzar(Poblacion pob, Solucion padre1, Solucion padre2, Solucion hijo1, Solucion hijo2, Point p) {
        int i, j, puntero, punteroaux, idCiudad;
        boolean principio = false;
        
        if(p.x < p.y) {
            i = p.x;
            j = p.y;
        }
        else {
            i = p.y;
            j = p.x;
        }
        
        for(int aux = i; aux <= j; aux++) {
            hijo1.solucion.add(padre1.solucion.get(aux));
            hijo2.solucion.add(padre2.solucion.get(aux));
        }
        
        puntero = j + 1;
        punteroaux = j + 1;
        if(puntero >= padre1.solucion.size()) {
            puntero = 0;
            punteroaux = 0;
            principio = true;
        }
        while(hijo1.solucion.size() < padre1.solucion.size()) {
            idCiudad = padre2.solucion.get(punteroaux);
            while(hijo1.solucion.contains(idCiudad)) {
                if(punteroaux < padre2.solucion.size() - 1) punteroaux++;
                else punteroaux = 0;
                idCiudad = padre2.solucion.get(punteroaux);
            }
            if(!principio) hijo1.solucion.add(idCiudad);
            else hijo1.solucion.add(puntero, idCiudad);
            if(puntero < padre1.solucion.size() - 1) puntero++;
            else {
                puntero = 0;
                principio = true;
            }
            if(punteroaux < padre2.solucion.size() - 1) punteroaux++;
            else punteroaux = 0;
        }
        hijo1.coste = coste(hijo1.solucion, pob);
        
        puntero = j + 1;
        punteroaux = j + 1;
        principio = false;
        if(puntero >= padre2.solucion.size()) {
            puntero = 0;
            punteroaux = 0;
            principio = true;
        }
        while(hijo2.solucion.size() < padre2.solucion.size()) {
            idCiudad = padre1.solucion.get(punteroaux);
            while(hijo2.solucion.contains(idCiudad)) {
                if(punteroaux < padre1.solucion.size() - 1) punteroaux++;
                else punteroaux = 0;
                idCiudad = padre1.solucion.get(punteroaux);
            }
            if(!principio) hijo2.solucion.add(idCiudad);
            else hijo2.solucion.add(puntero, idCiudad);
            if(puntero < padre2.solucion.size() - 1) puntero++;
            else {
                puntero = 0;
                principio = true;
            }
            if(punteroaux < padre1.solucion.size() - 1) punteroaux++;
            else punteroaux = 0;
        }
        hijo2.coste = coste(hijo2.solucion, pob);
        
    }
    
    /* Función que determina si se debe realizar un cruce y que genera el nuevo conjunto de soluciones */
    private static void cruce(Poblacion pob, ArrayList<Solucion> soluciones) {
        ArrayList<Solucion> aux = new ArrayList();
        Random rnd = new Random();
        double pcruce;
        Solucion hijo1, hijo2;
        Point p;

        for(int i = 0; i < soluciones.size(); i = i + 2) {
            pcruce = rnd.nextDouble();

            if(pcruce < PC) {
                p = new Point(rnd.nextInt(pob.getCiudades().size()), rnd.nextInt(pob.getCiudades().size()));

                hijo1 = new Solucion();
                hijo2 = new Solucion();
                cruzar(pob, soluciones.get(i), soluciones.get(i+1), hijo1, hijo2, p);
                aux.add(hijo1);
                aux.add(hijo2);
                //genTrazaCE(i, i+1, pcruce, soluciones.get(i), soluciones.get(i+1), p, hijo1, hijo2);
            } else {
                aux.add(new Solucion(soluciones.get(i)));
                aux.add(new Solucion(soluciones.get(i+1)));
                //genTrazaCE(i, i+1, pcruce, soluciones.get(i), soluciones.get(i+1));
            }
        }
        
        soluciones.clear();
        soluciones.addAll(aux);
    }
    
    /* Versión determinista de la funciáon de cruce */
    private static int cruce(Poblacion pob, int index, ArrayList<Solucion> soluciones, ArchivoAleatorios alea) {
        ArrayList<Solucion> aux = new ArrayList();
        Point2D.Double pcruce;
        Solucion hijo1, hijo2;
        Point p;
        int indice = index;

        for(int i = 0; i < soluciones.size(); i = i + 2) {
            pcruce = new Point2D.Double();
            indice = alea.nextAleatorio(indice, pcruce);

            if(pcruce.x < PC) {
                p = new Point();
                indice = alea.getAleatoriosVecinos(pob.getCiudades().size(), indice, p);

                hijo1 = new Solucion();
                hijo2 = new Solucion();
                cruzar(pob, soluciones.get(i), soluciones.get(i+1), hijo1, hijo2, p);
                aux.add(hijo1);
                aux.add(hijo2);
                genTrazaCE(i, i+1, pcruce.x, soluciones.get(i), soluciones.get(i+1), p, hijo1, hijo2);
            } else {
                aux.add(new Solucion(soluciones.get(i)));
                aux.add(new Solucion(soluciones.get(i+1)));
                genTrazaCE(i, i+1, pcruce.x, soluciones.get(i), soluciones.get(i+1));
            }
        }
        
        soluciones.clear();
        soluciones.addAll(aux);
        
        return indice;
    }
    
    /* Fuinción que determina si se produce mutación y modifica la solución en caso afirmativo */
    private static void mutar(Poblacion pob, ArrayList<Solucion> soluciones) {
        Random rnd = new Random();
        double pmut;
        Point p;
        int auxi, idMut;
        
        for(int i = 0; i < soluciones.size(); i++) {
            //genTrazaCE(i, soluciones.get(i));
            
            for(int j = 0; j < soluciones.get(i).solucion.size(); j++) {
                pmut = rnd.nextDouble();

                if(pmut < PM) {
                    p = new Point();
                    p.x = rnd.nextInt(soluciones.get(i).solucion.size());
                    p.y = j;
                    idMut = p.x;
                    
                    if(p.x != p.y) {
                        if (p.y > p.x){
                            auxi = p.x;
                            p.x = p.y;
                            p.y = auxi;
                        }
                        soluciones.get(i).coste = coste(soluciones.get(i).solucion, pob, soluciones.get(i).coste, p);
                        auxi = soluciones.get(i).solucion.get(p.x);
                        soluciones.get(i).solucion.set(p.x, soluciones.get(i).solucion.get(p.y));
                        soluciones.get(i).solucion.set(p.y, auxi);
                        soluciones.get(i).coste = coste(soluciones.get(i).solucion, pob);
                    }
                    
                    //genTrazaCE(j, pmut, idMut);
                }
                //else genTrazaCE(j, pmut);
            }
            //genTrazaCE(soluciones.get(i));
        }
    }
    
    /* Versión determinista de la función de mutación */
    private static int mutar(Poblacion pob, int index, ArrayList<Solucion> soluciones, ArchivoAleatorios alea) {
        Point2D.Double pmut;
        Point p;
        int indice = index, auxi, idMut;
        
        for(int i = 0; i < soluciones.size(); i++) {
            genTrazaCE(i, soluciones.get(i));
            
            for(int j = 0; j < soluciones.get(i).solucion.size(); j++) {
                pmut = new Point2D.Double();
                indice = alea.nextAleatorio(indice, pmut);

                if(pmut.x < PM) {
                    p = new Point();
                    indice = alea.nextIndice(indice, soluciones.get(i).solucion.size(), p);
                    p.y = j;
                    idMut = p.x;
                    
                    if(p.x != p.y) {
                        if (p.y > p.x){
                            auxi = p.x;
                            p.x = p.y;
                            p.y = auxi;
                        }
                        soluciones.get(i).coste = coste(soluciones.get(i).solucion, pob, soluciones.get(i).coste, p);
                        auxi = soluciones.get(i).solucion.get(p.x);
                        soluciones.get(i).solucion.set(p.x, soluciones.get(i).solucion.get(p.y));
                        soluciones.get(i).solucion.set(p.y, auxi);
                        //soluciones.get(i).coste = coste(soluciones.get(i).solucion, pob);
                    }
                    
                    genTrazaCE(j, pmut.x, idMut);
                }
                else genTrazaCE(j, pmut.x);
            }
            genTrazaCE(soluciones.get(i));
        }
        
        return indice;
    }
    
    /* Función que reemplaza el conjunto de soluciones anterior por el nuevo teniendo en cuenta la herencia de las dos mejores soluciones */
    private static void reemplazo(ArrayList<Solucion> viejas, ArrayList<Solucion> nuevas, int herencia) {
        viejas.sort(Comparator.comparing(Solucion::getCoste));
        nuevas.sort(Comparator.comparing(Solucion::getCoste));
        
        for(int i = 0; i < herencia; i++) {
            nuevas.add(0, new Solucion(viejas.get(i)));
        }
    }
    
    /* Función principal que implementa el algoritmo de computación evolutiva en modo aleatorio */
    public static void computacionEvolutiva(Poblacion p) {
        int itMejor = 0, tamPoblacion = (p.getCiudades().size()+1)*10, herencia = 2;
        ArrayList<Solucion> soluciones = new ArrayList(), solucionesNuevas;
        Solucion sol, mejorSolucion = new Solucion();
        mejorSolucion.coste = Integer.MAX_VALUE;

        for(int i = 0; i < tamPoblacion; i++) {
            sol = new Solucion();
            if(i < tamPoblacion/2) {
                genSolIni2(sol.solucion, p.getCiudades().size());
                sol.coste = coste(sol.solucion, p);
            }
            else {
                genSolIniVorazCE(sol.solucion, p);
                sol.coste = coste(sol.solucion, p);
            }
            soluciones.add(sol);
            if(sol.coste < mejorSolucion.coste) mejorSolucion = new Solucion(sol);
        }
        
        genTraza("POBLACION INICIAL");
        genTrazaCE(soluciones);

        ITE_ACTUAL = 1;
        while(ITE_ACTUAL <= ITE) {
            
            //System.out.println("ITE "+ITE_ACTUAL);

            solucionesNuevas = new ArrayList();
            for(Solucion s : soluciones) {
                solucionesNuevas.add(new Solucion(s));
            }
            
            
            
            genTraza("\nITERACION: "+ITE_ACTUAL+", SELECCION");
            torneo(solucionesNuevas, tamPoblacion - herencia, tamPoblacion);

            genTraza("\nITERACION: "+ITE_ACTUAL+", CRUCE ");
            cruce(p, solucionesNuevas);

            genTraza("ITERACION: "+ITE_ACTUAL+", MUTACION");
            mutar(p, solucionesNuevas);
            
            genTraza("\nITERACION: "+ITE_ACTUAL+", REEMPLAZO");
            reemplazo(soluciones, solucionesNuevas, herencia);
            genTrazaCE(solucionesNuevas);
            
            if(solucionesNuevas.get(herencia - 1).coste < mejorSolucion.coste) {
                mejorSolucion = new Solucion(solucionesNuevas.get(1));
                itMejor = ITE_ACTUAL;
            }
            if(solucionesNuevas.get(herencia).coste < mejorSolucion.coste) {
                mejorSolucion = new Solucion(solucionesNuevas.get(2));
                itMejor = ITE_ACTUAL;
            }

            soluciones = solucionesNuevas;
            
            ITE_ACTUAL++;
        }
        
        genTrazaCE(mejorSolucion, itMejor);
        System.out.println(mejorSolucion.coste);
        System.out.println(itMejor);
        System.out.println("\n");
        
        ITE_ACTUAL = 0;
    }
    
    /* VErsión mejorada del algoritmo obligatorio */
    public static void computacionEvolutivaMejorada(Poblacion p) {
        int itMejor = 0, tamPoblacion = (p.getCiudades().size()+1)*10, herencia = 100;
        ArrayList<Solucion> soluciones = new ArrayList(), solucionesNuevas;
        Solucion sol, mejorSolucion = new Solucion();
        mejorSolucion.coste = Integer.MAX_VALUE;

        // Inicializo la mitad de las soluciones iniciales de forma totalmente aleatorio, y del resto inicializo parte híbrida y parte totalmente voraz
        for(int i = 0; i < tamPoblacion; i++) {
            sol = new Solucion();
            if(i < tamPoblacion/2) {
                genSolIni2(sol.solucion, p.getCiudades().size());
                sol.coste = coste(sol.solucion, p);
            }
            else if(i < tamPoblacion/1.2) {
                genSolIniVorazCE2(sol.solucion, p, 10);
                sol.coste = coste(sol.solucion, p);
            }
            else {
                genSolIni(sol.solucion, p);
                sol.coste = coste(sol.solucion, p);
            }
            soluciones.add(sol);
            if(sol.coste < mejorSolucion.coste) mejorSolucion = new Solucion(sol);
        }

        ITE_ACTUAL = 1;
        while(ITE_ACTUAL <= ITE) {
            
            //System.out.println("ITE "+ITE_ACTUAL);

            solucionesNuevas = new ArrayList();
            for(Solucion s : soluciones) {
                solucionesNuevas.add(new Solucion(s));
            }
            
            torneoTernario(solucionesNuevas, tamPoblacion - herencia, tamPoblacion);

            cruce(p, solucionesNuevas);

            mutar(p, solucionesNuevas);
            
            reemplazo(soluciones, solucionesNuevas, herencia);
            
            if(solucionesNuevas.get(herencia - 1).coste < mejorSolucion.coste) {
                mejorSolucion = new Solucion(solucionesNuevas.get(1));
                itMejor = ITE_ACTUAL;
            }
            if(solucionesNuevas.get(herencia).coste < mejorSolucion.coste) {
                mejorSolucion = new Solucion(solucionesNuevas.get(2));
                itMejor = ITE_ACTUAL;
            }

            soluciones = solucionesNuevas;
            
            ITE_ACTUAL++;
        }
        
        System.out.println(mejorSolucion.coste);
        System.out.println(itMejor);
        System.out.println("\n");
        
        ITE_ACTUAL = 0;
    }
    
    /* Función principal que implementa el algoritmo de computación evolutiva en modo determinista */
    public static void computacionEvolutiva(Poblacion p, ArchivoAleatorios alea) {
        int index = 0, itMejor = 0, tamPoblacion = (p.getCiudades().size()+1)*10, herencia = 2;
        ArrayList<Solucion> soluciones = new ArrayList(), solucionesNuevas;
        Solucion sol, mejorSolucion = new Solucion();
        mejorSolucion.coste = Integer.MAX_VALUE;

        for(int i = 0; i < tamPoblacion; i++) {
            sol = new Solucion();
            if(i < tamPoblacion/2) {
                index = alea.getAleatoriosInicialCE(index, p.getCiudades().size(), sol.solucion);
                sol.coste = coste(sol.solucion, p);
            }
            else {
                index = genSolIniVorazCE(index, sol.solucion, p, alea);
                sol.coste = coste(sol.solucion, p);
            }
            soluciones.add(sol);
            if(sol.coste < mejorSolucion.coste) mejorSolucion = new Solucion(sol);
        }
        
        genTraza("POBLACION INICIAL");
        genTrazaCE(soluciones);

        ITE_ACTUAL = 1;
        while(ITE_ACTUAL <= ITE) {

            solucionesNuevas = new ArrayList();
            for(Solucion s : soluciones) {
                solucionesNuevas.add(new Solucion(s));
            }
            
            genTraza("\nITERACION: "+ITE_ACTUAL+", SELECCION");
            index = torneo(index, solucionesNuevas, alea, tamPoblacion - herencia, tamPoblacion);

            genTraza("\nITERACION: "+ITE_ACTUAL+", CRUCE ");
            index = cruce(p, index, solucionesNuevas, alea);

            genTraza("ITERACION: "+ITE_ACTUAL+", MUTACION");
            index = mutar(p, index, solucionesNuevas, alea);
            
            genTraza("\nITERACION: "+ITE_ACTUAL+", REEMPLAZO");
            reemplazo(soluciones, solucionesNuevas, herencia);
            genTrazaCE(solucionesNuevas);
            
            if(solucionesNuevas.get(herencia - 1).coste < mejorSolucion.coste) {
                mejorSolucion = new Solucion(solucionesNuevas.get(1));
                itMejor = ITE_ACTUAL;
            }
            if(solucionesNuevas.get(herencia).coste < mejorSolucion.coste) {
                mejorSolucion = new Solucion(solucionesNuevas.get(2));
                itMejor = ITE_ACTUAL;
            }

            soluciones = solucionesNuevas;
            
            ITE_ACTUAL++;
        }
        
        genTrazaCE(mejorSolucion, itMejor);
        
        ITE_ACTUAL = 0;
    }
}