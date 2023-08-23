package gol;

public class GolInverter implements GolGenerator {

    private int[][] grid;
    int celulasVivas;

    public GolInverter(int[][] grid) {
        this.grid = grid;
    }

    // Invert 1->0 and 0->1 in a Matrix
    private void invertValues(int[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                matrix[i][j] = matrix[i][j] == 1 ? 0 : 1;
            }
        }
    }

    // Converts a matrix to a flat String
    // [0,0,0][1,1,1][0,1,0] => "...\nXXX\n.X."
    private String toGridString(int[][] matrix) {
        String grid = new String("");
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                grid += (matrix[i][j] == 1 ? "X" : ".");
            }
            grid += "\n";
        }

        return grid;
    }

    @Override
    public String getNextGenerationAsString(long generation) {


        if (generation != 0) {
            // STEP 7. We calculate the next generation
            aplicarReglas();
            celulasVivas = contarCelulasVivas();
            if (celulasVivas == 0) {
                System.out.println();
                System.out.println("Todas las celulas murieron");
                System.exit(1);
            }
        }
        // STEP 8. We return the next generation (as a String)
        return toGridString(grid);
    }

    private void aplicarReglas() {
        int[][] nuevaGeneracion = new int[grid.length][grid[0].length];
        for (int fila = 0; fila < grid.length; fila++) {
            for (int columna = 0; columna < grid[fila].length; columna++) {
                int cantidadVecinosVivos = contarVecinosVivos(grid, fila, columna);
                //Reglas para una celula viva
                if (grid[fila][columna] == 1) {
                    //Si hay 2 o 3 vecinos alrededor de la celula este sobrevive
                    if (cantidadVecinosVivos == 2 || cantidadVecinosVivos == 3) {
                        nuevaGeneracion[fila][columna] = 1;
                        //En caso contrario muere por sobrepoblacion o soledad
                    } else {
                        nuevaGeneracion[fila][columna] = 0;
                    }
                    //Reglas para una celula muerta
                } else {
                    //Si tiene 3 vecinos alrededor nacera una celula
                    if (cantidadVecinosVivos == 3) {
                        nuevaGeneracion[fila][columna] = 1;
                        //En caso contrario sigue muerta
                    } else {
                        nuevaGeneracion[fila][columna] = 0;
                    }
                }
            }
        }
        grid = nuevaGeneracion;
    }

    //Con este metodo valido los vecinos que esten en el rango de la celula actual
    private static int contarVecinosVivos(int[][] tablero, int fila, int columna) {
        int vecinosVivos = 0;

        // Guarde en una matriz los posibles valores donde puede haber un vecino
        int[][] posiblesVecinos = {
                {-1, -1},
                {-1, 0},
                {-1, 1},
                {0, -1},
                {0, 1},
                {1, -1},
                {1, 0},
                {1, 1}
        };

        //Con este for recorreo los valores en la matriz de los posibles vecinos
        for (int i = 0; i < 8; i++) {
            int posbibleFilaVecina = fila + posiblesVecinos[i][0];
            int posibleColumnaVecino = columna + posiblesVecinos[i][1];

            //Con este if verifico que las variables de filaVecino y columnaVecino no sopresen el rango establecido
            //Primero que tanto posbible fila o columna no sea menor que 0 ya que generaria una exepcion,
            // y que no supere el rango de la tabla
            if (posbibleFilaVecina >= 0 && posbibleFilaVecina < tablero.length && posibleColumnaVecino >= 0 && posibleColumnaVecino < tablero[0].length) {
                // Si paso la validacion anterior, valido que ese posible vecino este vivo
                if (tablero[posbibleFilaVecina][posibleColumnaVecino] == 1) {
                    vecinosVivos++;
                }
            }

        }

        return vecinosVivos;
    }

    public int contarCelulasVivas() {
        int celulasVivas = 0;
        for (int fila = 0; fila < grid.length; fila++) {
            for (int columna = 0; columna < grid[fila].length; columna++) {
                if (grid[fila][columna] == 1) {
                    celulasVivas++;
                }
            }
        }
        return celulasVivas;
    }
}
