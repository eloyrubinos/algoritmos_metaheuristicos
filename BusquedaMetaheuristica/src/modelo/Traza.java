
package modelo;

import java.awt.Point;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * 
 * @author Eloy
 * 
 * Esta clase es la encargada de generar las trazas de ejecución de los algoritmos del programa.
 * 
 */

public class Traza {
    public static ArrayList<String> traza = new ArrayList();
    private static final NumberFormat FORMATO = new DecimalFormat("#0.000000");
    
    
    public static void genTraza(String s) {
        traza.add(s);
    }
    
    /**
     * FUNCIONES PARA GENERAR TRAZAS EN BÚSQUEDA TABÚ
     */
    public static void genTraza(ArrayList<Integer> solucion, int coste){
    traza.add("RECORRIDO INICIAL");
    String recorrido = "\tRECORRIDO: ";
    for(int z : solucion){
        recorrido = recorrido + z + " ";
    }
    traza.add(recorrido);
    traza.add("\tCOSTE (km): "+coste);
    }
    
    public static void genTraza(int iteracion, Point p, ArrayList<Integer> solucion, int coste, int sinMejora, ListaTabu lt){
        traza.add("\nITERACION: "+iteracion);
        traza.add("\tINTERCAMBIO: ("+p.x+", "+p.y+")");
        String recorrido = "\tRECORRIDO: ";
        for(int z : solucion){
            recorrido = recorrido + z + " ";
        }
        traza.add(recorrido);
        traza.add("\tCOSTE (km): "+coste);
        traza.add("\tITERACIONES SIN MEJORA: "+sinMejora);
        traza.add("\tLISTA TABU:");
        for(Point paux : lt.getLista()){
            traza.add("\t"+paux.x+" "+paux.y);
        }
    }
    
    public static void genTraza(int iteracion, ArrayList<Integer> solucion, int coste){
        traza.add("\n\nMEJOR SOLUCION:");
        String recorrido = "\tRECORRIDO: ";
        for(int z : solucion){
            recorrido = recorrido + z + " ";
        }
        traza.add(recorrido);
        traza.add("\tCOSTE (km): "+coste);
        traza.add("\tITERACION: "+iteracion);
    }
    
    public static void genTraza(int reinicio){
        traza.add("\n***************\nREINICIO: "+reinicio+"\n***************");
    }
    
    
    
    /**
     * FUNCIONES PARA GENERAR TRAZAS EN TEMPLE SIMULADO
     */
    public static void genTrazaTS(ArrayList<Integer> solucion, int coste, double temple){
    traza.add("SOLUCION INICIAL:");
    String recorrido = "\tRECORRIDO: ";
    for(int z : solucion){
        recorrido = recorrido + z + " ";
    }
    traza.add(recorrido);
    traza.add("\tFUNCION OBJETIVO (km): "+coste);
    traza.add("\tTEMPERATURA INICIAL: "+FORMATO.format(temple));
    }
    
    public static void genTrazaTS(int iteracion, int indice, int ciudad, int insercion, ArrayList<Integer> solucion, int coste, double delta, double temple, double exp){
        traza.add("\nITERACION: "+iteracion);
        traza.add("\tINDICE CIUDAD: "+indice);
        traza.add("\tCIUDAD: "+ciudad);
        traza.add("\tINDICE INSERCION: "+insercion);
        String recorrido = "\tRECORRIDO: ";
        for(int z : solucion){
            recorrido = recorrido + z + " ";
        }
        traza.add(recorrido);
        traza.add("\tFUNCION OBJETIVO (km): "+coste);
        traza.add("\tDELTA: "+(int)delta);
        traza.add("\tTEMPERATURA: "+FORMATO.format(temple));
        traza.add("\tVALOR DE LA EXPONENCIAL: "+FORMATO.format(exp));
    }
    
    public static void genTrazaTS() {
        traza.add("\tSOLUCION CANDIDATA ACEPTADA");
    }
    
    public static void genTrazaTS(Point enfriamiento) {
        traza.add("\tCANDIDATAS PROBADAS: "+enfriamiento.x+", ACEPTADAS: "+enfriamiento.y);
    }
    
    public static void genTrazaTS(int contEnfriamiento, double temple) {
        traza.add("\n============================");
        traza.add("ENFRIAMIENTO: "+contEnfriamiento);
        traza.add("============================");
        traza.add("TEMPERATURA: "+FORMATO.format(temple));
    }
    
    public static void genTraza(ArrayList<Integer> solucion, int coste, int iteracion, double mu, double phi){
        traza.add("\n\nMEJOR SOLUCION: ");
        String recorrido = "\tRECORRIDO: ";
        for(int z : solucion){
            recorrido = recorrido + z + " ";
        }
        traza.add(recorrido);
        traza.add("\tFUNCION OBJETIVO (km): "+coste);
        traza.add("\tITERACION: "+iteracion);
        traza.add("\tmu =  "+mu+", phi = "+phi);
    }
    
    
    
    /**
     * FUNCIONES PARA GENERAR TRAZAS EN COMPUTACIÓN EVOLUTIVA
     */
    
    
    public static void genTrazaCE(ArrayList<Solucion> soluciones) {
        Solucion aux;
        String recorrido;
        for(int i = 0; i < soluciones.size(); i++){
            aux = soluciones.get(i);
            recorrido = "RECORRIDO: ";
            for(int z : aux.solucion) {
                recorrido = recorrido + z + " ";
            }
            traza.add("INDIVIDUO "+i+" = {FUNCION OBJETIVO (km): "+(int)aux.coste+", "+recorrido+"}");
        }
    }
    
    public static void genTrazaCE(int index, Point participantes, int ganador) {
        traza.add("\tTORNEO "+index+": "+participantes.x+" "+participantes.y+" GANA "+ganador);
    }
    
    public static void genTrazaCE(int i, int j, double aleatorio, Solucion padre1, Solucion padre2, Point corte, Solucion hijo1, Solucion hijo2) {
        traza.add("\tCRUCE: ("+i+", "+j+") (ALEATORIO: "+FORMATO.format(aleatorio)+")");
        
        String recorrido = "RECORRIDO: ";
        for(int z : padre1.solucion) {
            recorrido = recorrido + z + " ";
        }
        traza.add("\t\tPADRE: = {FUNCION OBJETIVO (km): "+(int)padre1.coste+", "+recorrido+"}");
        
        recorrido = "RECORRIDO: ";
        for(int z : padre2.solucion) {
            recorrido = recorrido + z + " ";
        }
        traza.add("\t\tPADRE: = {FUNCION OBJETIVO (km): "+(int)padre2.coste+", "+recorrido+"}");
        
        traza.add("\t\tCORTES: ("+corte.x+", "+corte.y+")");
        
        recorrido = "RECORRIDO: ";
        for(int z : hijo1.solucion) {
            recorrido = recorrido + z + " ";
        }
        traza.add("\t\tHIJO: = {FUNCION OBJETIVO (km): "+(int)hijo1.coste+", "+recorrido+"}");
        
        recorrido = "RECORRIDO: ";
        for(int z : hijo2.solucion) {
            recorrido = recorrido + z + " ";
        }
        traza.add("\t\tHIJO: = {FUNCION OBJETIVO (km): "+(int)hijo2.coste+", "+recorrido+"}\n");
    }
    
    public static void genTrazaCE(int i, int j, double aleatorio, Solucion padre1, Solucion padre2) {
        traza.add("\tCRUCE: ("+i+", "+j+") (ALEATORIO: "+FORMATO.format(aleatorio)+")");
        
        String recorrido = "RECORRIDO: ";
        for(int z : padre1.solucion) {
            recorrido = recorrido + z + " ";
        }
        traza.add("\t\tPADRE: = {FUNCION OBJETIVO (km): "+(int)padre1.coste+", "+recorrido+"}");
        
        recorrido = "RECORRIDO: ";
        for(int z : padre2.solucion) {
            recorrido = recorrido + z + " ";
        }
        traza.add("\t\tPADRE: = {FUNCION OBJETIVO (km): "+(int)padre2.coste+", "+recorrido+"}");
        
        traza.add("\t\tNO SE CRUZA\n");
    }
    
    public static void genTrazaCE(int j, Solucion s) {
        traza.add("\tINDIVIDUO "+j);
        String recorrido = "\tRECORRIDO ANTES: ";
        for(int z : s.solucion) {
            recorrido = recorrido + z + " ";
        }
        traza.add(recorrido);
    }
    
    public static void genTrazaCE(int pos, double aleatorio) {
        traza.add("\t\tPOSICION: "+pos+" (ALEATORIO "+FORMATO.format(aleatorio)+") NO MUTA");
    }
    
    public static void genTrazaCE(int pos, double aleatorio, int cambio) {
        traza.add("\t\tPOSICION: "+pos+" (ALEATORIO "+FORMATO.format(aleatorio)+") INTERCAMBIO CON: "+cambio);
    }
    
    public static void genTrazaCE(Solucion s) {
        String recorrido = "\tRECORRIDO DESPUES: ";
        for(int z : s.solucion) {
            recorrido = recorrido + z + " ";
        }
        traza.add(recorrido+"\n");
    }
    
    public static void genTrazaCE(Solucion s, int ite) {
        traza.add("\n\nMEJOR SOLUCION: ");
        String recorrido = "RECORRIDO: ";
        for(int z : s.solucion) {
            recorrido = recorrido + z + " ";
        }
        traza.add(recorrido);
        traza.add("FUNCION OBJETIVO (km): "+(int)s.coste);
        traza.add("ITERACION: "+ite);
    }
    
    public static int size(){
        return traza.size();
    }
    
    public static String get(int i){
        return traza.get(i);
    }
    
    public static void clear(){
        traza.clear();
    }
}
