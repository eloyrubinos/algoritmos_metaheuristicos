package busquedalmetaheuristica;

import static busquedalmetaheuristica.AlgoritmosHeuristicos.*;
import static modelo.Traza.*;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelo.*;

/**
 * 
 * @author Eloy
 * 
 * Clase principal del programa.
 * 
 * Analiza los argumentos: tres primeros if.
 * Actúa como corresponda según el modo (aleatorio o determinista): dos últimos if.
 * Dentro de los dos últimos if: ejecuta el algoritmo indicado (local o tabú).
 * Establece el nombre del fichero de salida según si nos lo proporcionan o usamos el predeterminado.
 * Avisa del fin de la ejecución.
 * 
 */

public class BusquedaMetaheuristica {
    
    public static void main(String[] args) {
        PrintWriter writer = null;
        String salida;
        if(args.length == 5) salida = args[4];
        else salida = "solucion.txt";
        
        if(args.length < 4 || args.length > 5) System.out.println("Número de argumentos incorrecto. Recuerde especificar:\n"
                                                + "1) archivo de población\n"
                                                + "2)-l/-t/-ts/-ce (local/tabú/temple/evolutivo)\n"
                                                + "3)-a/-d (aleatorio/determinista)\n"
                                                + "4) -a: número de ejecuciones del algoritmo (mínimo 1) / -d: archivo de índices\n"
                                                + "5) (opcional) ruta de la salida\n");
         
        
        else if(!args[2].equalsIgnoreCase("-a") && !args[2].equalsIgnoreCase("-d")) 
            System.out.println("No existe el modo indicado. Por favor, seleccione uno de los siguientes modos:\n"
                                + "-a : aleatorio\n"
                                + "-d: determinista\n");
        
        
        else if(!args[1].equalsIgnoreCase("-l") && !args[1].equalsIgnoreCase("-t") && !args[1].equalsIgnoreCase("-ts") & !args[1].equalsIgnoreCase("-ce")) 
            System.out.println("No existe el algoritmo indicado. Por favor, seleccione uno de los siguientes algoritmos:\n"
                                + "-l : búsqueda local del primer mejor\n"
                                + "-t: búsqueda tabú del mejor\n"
                                + "-ts: temple simulado"
                                + "-ce: computación evolutiva");
        
        
        
        /* MODO ALEATORIO */
        else if(args[2].equalsIgnoreCase("-a")){ 
                try {
                    
                    /* Creo la población */
                    Poblacion p = new Poblacion();
                    p.leerPoblacion(args[0]);
                    
                    /* Obtengo número de ejecuciones seleccionado (mínimo 1) */
                    int x = Integer.parseInt(args[3]);
                    if(x <= 0){
                        System.out.println("Se ha especificado un número de ejecuciones incompatible. Se ejecutará una sola vez.\n");
                        x = 1;
                    }
                    
                    /* Ejecuto el algoritmo que corresponda ese número de veces, e imprimo la traza con cada ejecución */
                    if(args[1].equalsIgnoreCase("-l")){
                        while(x > 0){
                            buscaLocal(p);
                            writer = new PrintWriter(x+"_"+salida, "UTF-8");
                            for(int i = 0; i < traza.size(); i++){
                                writer.println(traza.get(i));
                            }
                            writer.close();
                            traza.clear();
                            x--;
                        }
                    }
                    else if(args[1].equalsIgnoreCase("-t")){
                        while(x > 0){
                            buscaTabuVoluntaria(p);
                            writer = new PrintWriter(x+"_"+salida, "UTF-8");
                            for(int i = 0; i < traza.size(); i++){
                                writer.println(traza.get(i));
                            }
                            writer.close();
                            traza.clear();
                            x--;
                        }
                    }
                    else if(args[1].equalsIgnoreCase("-ts")){
                        while(x > 0){
                            templeSimuladoVoluntario(p);
                            writer = new PrintWriter(x+"_"+salida, "UTF-8");
                            for(int i = 0; i < traza.size(); i++){
                                writer.println(traza.get(i));
                            }
                            writer.close();
                            traza.clear();
                            x--;
                        }
                    }
                    else if(args[1].equalsIgnoreCase("-ce")){
                        while(x > 0){
                            computacionEvolutivaMejorada(p);
                            if(!traza.isEmpty()) {
                                writer = new PrintWriter(x+"_"+salida, "UTF-8");
                                for(int i = 0; i < traza.size(); i++){
                                    writer.println(traza.get(i));
                                }
                                writer.close();
                                traza.clear();
                            }
                            x--;
                        }
                    }
                    
                    /* Capturo errores */
                } catch (FileNotFoundException | UnsupportedEncodingException ex) {
                    Logger.getLogger(BusquedaMetaheuristica.class.getName()).log(Level.SEVERE, null, ex);
                }
        }
        
        
        
        /* MODO DETERMINISTA */
        else if(args[2].equalsIgnoreCase("-d")){
            try {
                
                /* Creo la población */
                Poblacion p = new Poblacion();
                p.leerPoblacion(args[0]);
                
                /* Leo la traza determinista */
                ArchivoAleatorios aa = new ArchivoAleatorios(args[3]);
                
                /* Ejecuto el algoritmo que corresponda con los índices predeterminados */
                if(args[1].equalsIgnoreCase("-l")) buscaLocal(p, aa);
                else if(args[1].equalsIgnoreCase("-t")) buscaTabu(p, aa);
                else if(args[1].equalsIgnoreCase("-ts")) templeSimulado(p, aa);
                else if(args[1].equalsIgnoreCase("-ce")) computacionEvolutiva(p, aa);
                
                /* Guardo la traza en un archivo */
                writer = new PrintWriter(salida, "UTF-8");
                for(int i = 0; i < traza.size(); i++){
                    writer.println(traza.get(i));
                }
                
                /* Trato los errores y termino cerrando el archivo */
            } catch (FileNotFoundException | UnsupportedEncodingException ex) {
                Logger.getLogger(BusquedaMetaheuristica.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                if(writer != null) writer.close();
            }
        }
        
        System.out.println("\nFin de la ejecución.\n");
    }
}