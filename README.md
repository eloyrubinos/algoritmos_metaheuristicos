# algoritmos_metaheuristicos

###### IDE
>NetBeans

###### Principales conocimientos aplicados
- Java
- Estrategias metaheurísticas
- Algoritmos de búsqueda

###### Descripción
Este proyecto contiene las cuatro prácticas realizadas en el segundo módulo de la asignatura Ingeniería del Conocimiento. En ellas implementamos diferentes algoritmos para **hallar una solución al problema del viajante** entre N ciudades.

Aunque estos problemas de "juguete" son muy simples, ya para búsquedas en un espacio de 100 ciudades se observa un deterioro notable del tiempo de búsqueda voraz. Esto nos hace ser conscientes de la necesidad de tener algoritmos capaces de encontrar soluciones **lo bastante buenas** en un **tiempo razonable**, encontrando un equilibrio entre la calidad de las soluciones y el tiempo de procesamiento requerido.

En este contexto vamos a aprender a implementar algoritmos de búsqueda local, tabú, por temple simulado y con computación evolutiva.

###### Instrucciones
1. Búsqueda local del primer mejor
2. Búsqueda tabú
3. Búsqueda por temple simulado
4. Búsqueda con computación evolutiva

Con la referencia a cada algoritmo se puede encontrar el guion correspondiente en [Documentación](./Documentación/).
Además, excepto para el primero, también se encuentran, en la misma carpeta, razonamientos de mejoras implementadas al algoritmo básico propuesto en el guion. Estas versiones mejoradas son las que están activas por defecto en el proyeto, aunque está presente el código para todas las versiones.

En [Entradas](./Entradas/) tenemos matrices de distancias de ejemplo. Es importante que **se presupone que el viajante empieza y acaba siempre en la ciudad de índice 0**; esto es algo impuesto por el guion.
También veremos en esta carpeta unos ficheros de números aleatorios. Esto es porque los algoritmos pueden ejecutarse de forma que reciban los números "aleatorios" de este archivo en vez de generarlos. Se hace así para poder comprobar que el algoritmo funciona correctamente.

Si el algoritmo está bien implementado la salida debería corresponderse con su traza correspondiente cuando se usan los archivos de números "aleatorios". Estas trazas están en [Trazas](./Trazas/).

Para más información sobre cómo usar el programa consultar la [ayuda](./AYUDA.txt).
