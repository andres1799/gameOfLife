package gol;

import java.util.Random;

public class GolSkeletonB {
    static boolean existeAncho = false, existeAltura = false, existeVelocidad = false, existeGeneracion = false, existePoblacion = false;
    static boolean anchoValido = false, alturaValida = false, velocidadValida = false, generacionValida = false, poblacionValida = false;
    static int variableAncho, variableAlto, variableVelocidad, variableGeneracion, valorGTemp;
    static String variablePoblacion = "";
    static int[][] tabla;

    public static void main(String[] args) {

        leerYvalidarParametros(args);
        mostrarMenu();
        mostrarInformacion();

        //Si todos los valores son validas el programa continua
        boolean esValido = anchoValido && alturaValida && velocidadValida && generacionValida && poblacionValida;
        if (esValido) {
            tabla = actualizarValoresEnTabla(variableAlto, variableAncho, variablePoblacion);
            final GolGenerator generator = new GolInverter(tabla);
            mostrarTabla(tabla);
            SwingRenderer.render(generator, new GolSettings(variableAlto, variableAncho, variableVelocidad, variableGeneracion));
        } else {
            System.out.println();
            System.out.println("Datos invalidos, por favor verificar los valores ingresados");
        }
    }


    private static void mostrarMenu() {
        System.out.println("-------------------------------------");
        System.out.println("| Juego de la vida                  |");
        System.out.println("-------------------------------------");

    }

    private static void mostrarInformacion() {
        mostrarDetalles(existeAncho, anchoValido, "Ancho : ", variableAncho);
        mostrarDetalles(existeAltura, alturaValida, "Altura : ", variableAlto);
        mostrarGeneracion(existeGeneracion, generacionValida, "Generacion: ", variableGeneracion);
        mostrarDetalles(existeVelocidad, velocidadValida, "Velocidad: ", variableVelocidad);
        mostrarPoblacion(existePoblacion, poblacionValida, "Poblacion: ", variablePoblacion);
    }

    //Con este metodo se leen, validan y almacenan los valores que se ingresaron por la linea de comandos
    public static void leerYvalidarParametros(String[] arregloParametros) {
        int indiceP = 0;
        for (int i = 0; i < arregloParametros.length + 1; i++) {
            if (i < arregloParametros.length) {
                if (arregloParametros[i].contains("w=")) {
                    variableAncho = validarDatos(arregloParametros[i].substring(2));
                    if (variableAncho == 10 || variableAncho == 20 || variableAncho == 40 || variableAncho == 80) {
                        anchoValido = true;
                    }
                    existeAncho = true;
                }
                if (arregloParametros[i].contains("h=")) {
                    variableAlto = validarDatos(arregloParametros[i].substring(2));
                    if (variableAlto == 10 || variableAlto == 20 || variableAlto == 40) {
                        alturaValida = true;
                    }
                    existeAltura = true;
                }
                if (arregloParametros[i].contains("s=")) {
                    variableVelocidad = validarDatos(arregloParametros[i].substring(2));
                    if (variableVelocidad >= 250 && variableVelocidad <= 1000) {
                        velocidadValida = true;
                    }
                    existeVelocidad = true;
                }
                if (arregloParametros[i].contains("g=")) {
                    variableGeneracion = validarDatos(arregloParametros[i].substring(2));
                    if (variableGeneracion >= 0) {
                        generacionValida = true;
                    }
                    existeGeneracion = true;
                }

                if (arregloParametros[i].contains("p=")) {
                    indiceP = i;
                    existePoblacion = true;
                }
            }
            //Toma la poblacion cuando ya exista tanto la altura, el ancho y la poblacion
            if (anchoValido && alturaValida && existePoblacion) {
                variablePoblacion = arregloParametros[indiceP].substring(2);
                //Cuando sea igual a rnd generara una poblacion aleatoria
                if (variablePoblacion.equals("rnd")) {
                    variablePoblacion = generarPoblacionAleatoria(variableAncho, variableAlto).toString();
                    poblacionValida = true;
                    //En caso contrario simplemente se valida la poblacion ingresada
                } else {
                    int totalColumnas = validarColumnasEnPoblacion(variablePoblacion);
                    int totalFilas = validarFilasEnPoblacion(variablePoblacion);
                    char[] poblacionTemp = variablePoblacion.toCharArray();
                    //Validar que la poblacion ingresada no supere el rango de la altura y el ancho
                    if (totalColumnas <= variableAncho && totalFilas <= variableAlto) {
                        for (int a = 0; a < poblacionTemp.length; a++) {
                            if (poblacionTemp[a] == '0' || poblacionTemp[a] == '1' || poblacionTemp[a] == '#') {
                                poblacionValida = true;
                            } else {
                                i = poblacionTemp.length - 1;
                            }
                        }
                    }
                }
            }
        }
    }

    //Metodo para validar que los valores no contengan letras
    public static int validarDatos(String valor) {
        int valorTemporal;
        try {
            valorTemporal = Integer.parseInt(valor);
        } catch (Exception e) {
            //Retorna -1 cuando sea letra
            return -1;
        }
        return valorTemporal;
    }

    //Con este metodo valido la cantidad de columnas presentes en la poblacion
    public static int validarColumnasEnPoblacion(String arg) {
        int totalcolumnas = 0;
        int columnamayor = 0;
        for (char element : arg.toCharArray()) {
            if (element == '1' || element == '0') {
                totalcolumnas++;
            } else if (element == '#') {
                if (totalcolumnas > columnamayor) {
                    columnamayor = totalcolumnas;
                    totalcolumnas = 0;
                } else {
                    totalcolumnas = 0;
                }
            }
        }
        return columnamayor;
    }

    //Con este metodo valido las filas presentes en la poblacion
    public static int validarFilasEnPoblacion(String arg) {
        int totalfilas = 1;
        for (char element : arg.toCharArray()) {
            if (element == '#') {
                totalfilas++;
            }
        }
        return totalfilas;
    }

    //Metodo generico para mostrar valores
    public static void mostrarDetalles(Boolean existe, Boolean valorValido, String nombre, int valor) {
        if (existe) {
            if (valorValido) {
                System.out.println(nombre + "[" + valor + "]");
            } else {
                System.out.println(nombre + "Invalido");
            }
        } else {
            System.out.println(nombre + " No presente");
        }
    }

    //Ya que las generaciones podian ser infinitas cree otro metodo para mostrar el respectivo mensaje
    public static void mostrarGeneracion(Boolean existe, Boolean valorValido, String nombre, int valor) {
        if (existe) {
            if (valorValido) {
                if (valor != 0) {
                    System.out.println(nombre + "[" + valor + "]");
                } else {
                    System.out.println(nombre + "Generaciones infinitas");
                }
            } else {
                System.out.println(nombre + "Invalido");
            }
        } else {
            System.out.println(nombre + " No presente");
        }
    }

    //Este metodo es similar al de mostrar igual a mostrarDetalles pero con un parametro diferente
    public static void mostrarPoblacion(Boolean existe, Boolean valorValido, String nombre, String poblacion) {
        if (existe) {
            if (valorValido) {
                System.out.println(nombre + "[" + poblacion + "]");
            } else {
                System.out.println(nombre + "Invalido");
            }
        } else {
            System.out.println(nombre + " No presente");
        }
    }

    //Con este metodo genero una poblacion aleatoria dentro del rango de la altura y el ancho
    public static String generarPoblacionAleatoria(int ancho, int altura) {
        Random random = new Random();
        String variableTemporal = "";
        for (int i = 0; i < altura; i++) {
            for (int f = 0; f < ancho; f++) {
                String numeroAleatorio = Integer.toString(random.nextInt(2));
                variableTemporal = variableTemporal + numeroAleatorio;
            }
            if (i < altura - 1) {
                variableTemporal = variableTemporal + "#";
            }
        }
        return variableTemporal;
    }

    //Con este motodo muestro los valores que esten contenidos en la tabla
    public static void mostrarTabla(int[][] matriz) {
        System.out.println();
        System.out.println("Generacion: " + valorGTemp);
        for (int filas = 0; filas < matriz.length; filas++) {
            for (int columnas = 0; columnas < matriz[filas].length; columnas++) {
                System.out.print(matriz[filas][columnas] == Integer.parseInt("1") ? "⬜" : "⬛");
            }
            System.out.println();
        }
    }

    //Con este metodo actualizo los valores de la tabla en base a los valores de la poblacion
    public static int[][] actualizarValoresEnTabla(int alto, int ancho, String poblacion) {
        int indice = 0;
        int[][] nuevosValores = new int[alto][ancho];
        for (int fila = 0; fila < nuevosValores.length; fila++) {
            for (int colum = 0; colum < nuevosValores[fila].length + 1; colum++) {
                if (indice < poblacion.length()) {
                    char iteradorNumeros = poblacion.charAt(indice);
                    if (iteradorNumeros == '1' || iteradorNumeros == '0') {
                        nuevosValores[fila][colum] = Character.getNumericValue(iteradorNumeros);
                    } else {
                        fila++;
                        colum = -1;
                    }
                    indice++;
                }
            }
        }
        return nuevosValores;
    }
}