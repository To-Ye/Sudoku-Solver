package sudoku.solver;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

//To-Do:
//-nicht mehr mit Matrizen arbeiten
//-nur einen Aufruf zum lösen
//-Liste automatisch generieren
//-Input lesen

public class Sudoku {
	
	Random rand = new Random();
	
	public static void main(String[] args) throws FileNotFoundException {
		
				
		
		
		Sudoku sudoku = new Sudoku();
		ArrayList[][] possibilities = sudoku.getInput(new File("input.txt"));
		sudoku.print2dArrayList(possibilities);

		
		
	}
	

	
	public ArrayList[][] getInput(File input) throws FileNotFoundException {
		ArrayList[][] out = new ArrayList[9][9];
		Scanner fileScanner = new Scanner(input);
		
		for(int i = 0; i < 9; i++) {
			String currentLine = fileScanner.nextLine();
			char[] charLine = currentLine.toCharArray();
			for(int j = 0; j < 9; j++) {
				if(charLine[j] == '0') {
					out[i][j] = new ArrayList<>(Arrays.asList(1,2,3,4,5,6,7,8,9));
				} else {
					out[i][j] = new ArrayList<>(Arrays.asList(charLine[j]));
				}
			}
		}
		
		return out;
	}
	
	public int[][] solver(int[][] mat, ArrayList[][] poss){
		boolean change = true;
		int[][] current = copy2dArray(mat);
		ArrayList[][] newPoss = copy2dArrayList(poss);
			
		while(change) {
			
			int[][] old = copy2dArray(current);
			
			newPoss = adjustDefRowCol(current, newPoss);
			newPoss = adjustDefSquare(current, newPoss);
			
			current = filler(current, newPoss);
			
			change = changes(old, current);
			
		}
		
		return current;
	}
	
	
	public int[][] recursiveSudoku(int[][] mat, ArrayList[][] poss) {
		
		int[][] mat1 = copy2dArray(mat);
		
		ArrayList[][] newPoss = copy2dArrayList(poss);
		
		
		int x = -1;
		int y = -1;
		
		boolean stop = false;
		
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {
				if(poss[i][j].size() > 1 ) {
					x = j;
					y = i;
					stop = true;
					break;
				}
			}
			if(stop) {
				break;
			}
		}
		
		
		for(int i = 0; i < newPoss[y][x].size(); i++) {
			mat1[y][x] = (int) newPoss[y][x].get(i);
	
			if(helper(mat1, poss) != null) {
				return helper(mat1, poss);
			}
		}
		
		return null;
	}
	
	public int[][] helper(int[][] mat, ArrayList[][] poss){
		ArrayList[][] poss1 = copy2dArrayList(poss);
		int[][] mat1 = solver(mat, poss1);
		
		
		if(solved(mat1)) {
			if(legal(mat1)) {
				return mat1;
			} else {
				return null;
			}
		} else {
			return recursiveSudoku(mat1, poss1);
		}
		
	}
	
	
	public int[][] filler(int [][] mat, ArrayList[][] poss){
		int[][] out = copy2dArray(mat);
		
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {
				if(poss[i][j].size() == 1) {
					out[i][j] = (int) poss[i][j].get(0);
				}
			}
		}
		
		return out;
	}
	
//	goes through the alrd filled-in numbers and calls adjustPoss whenever u is not 0, with the coordinates X, Y and u
	public ArrayList<Integer>[][] adjustDefRowCol(int[][] mat, ArrayList[][] poss) {
		
		ArrayList[][] newPoss = copy2dArrayList(poss);
		
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {
				int u = mat[i][j];
				if(u != 0) {
					newPoss = adjustPoss(j, i, u, newPoss);
				}
			}
		}
		
		return newPoss;
	}
	
//	goes through the alrd filled-in numbers and calls adjustSquare whenever u is not 0, with the coordinates X, Y and u
	public ArrayList<Integer>[][] adjustDefSquare(int[][] mat, ArrayList[][] poss) {
		
		ArrayList[][] newPoss = copy2dArrayList(poss);
		
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {
				int u = mat[i][j];
				if(u != 0) {
					newPoss = adjustSquare(j, i, u, newPoss);
				}
			}
		}
		
		return newPoss;
	}
	
	
//	removes the integer u from every column and row, except from the original position
	public ArrayList<Integer>[][] adjustPoss(int x, int y, Integer u, ArrayList[][] poss) {
		
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {
				if(i == y || j == x) {
					if(i == y && j == x) {
						if(poss[i][j].size() > 1) {
							poss[i][j].removeIf(n -> (n != u));
						}
					} else {
						if(poss[i][j].size() > 1) {
							poss[i][j].removeIf(n -> (n == u));
						}
						
					}
				}
			}
		}
		
		return poss;
		
	}
	
//	removes the integer u, in the possibilities of every position in the square, except his own
	public ArrayList<Integer>[][] adjustSquare(int x, int y, Integer u, ArrayList[][] poss) {
		int square = corresSquare(x, y);
		
		int xStart = startingCoordinates(square, "x");
		int yStart = startingCoordinates(square, "y");
		
		for(int i = yStart; i < yStart+3; i++) {
			for(int j = xStart; j < xStart+3; j++) {
					if(poss[i][j].size() > 1) {
						if(i == y && j == x) {
							poss[i][j].removeIf(n -> (n != u));
						} else {
							poss[i][j].removeIf(n -> (n == u));
						}
					}
			}
		}
		
		return poss;		
	}

	
//	checks if there were any adjustments to the matrix
	public boolean changes(int[][] matOld, int[][] matNew) {
		
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {
				if(matOld[i][j] != matNew[i][j]) {
					return true;
				}
			}
		}
		
		return false;
	}
	
//	returns the corresponding square(1-9) to the given coordinates
	public static int corresSquare(int x, int y) {
		if(x < 3) {
			if(y < 3) {
				return 1;
			} else if(y < 6) {
				return 4;
			} else {
				return 7;
			}
		} else if(x < 6) {
			if(y < 3) {
				return 2;
			} else if(y < 6) {
				return 5;
			} else {
				return 8;
			}
		} else {
			if(y < 3) {
				return 3;
			} else if(y < 6) {
				return 6;
			} else {
				return 9;
			}
		}
	}
	
//	returns the corresponding starting Coordinates to the given square
	public int startingCoordinates(int n, String s) {
		
		if(s.equals("x")) {
			int x = ((n-1) * 3) - ((((n-1) * 3)/9) * 9);
			return x;
		} else {
			int y = ((n-1)/3) * 3;
			return y;
		}
		
	}
	
//	copies a matrix (to avoid reference-type problems)
	public int[][] copy2dArray(int[][] mat){
		int[][] out = new int[9][9];
		
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {
				int current = mat[i][j];
				out[i][j] = current;
			}
		}
		
		
		return out;
	}
	
	public ArrayList<Integer>[][] copy2dArrayList(ArrayList[][] poss){
		ArrayList[][] newPoss = new ArrayList[9][9];
		
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {
				newPoss[i][j] = poss[i][j];
			}
		}
		
		return newPoss;
	}
	
	
	
	public boolean solved(int[][] mat) {
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {
				if(mat[i][j] == 0) {
					return false;
				}
			}
		}
		
		return true;
	}
	
//	checks if a given 9x9 matrix is legal according to sudoku rules
	public boolean legal(int[][] mat) {		
		
		for(int i = 0; i < 9; i += 3) {
			for(int j = 0; j < 9; j += 3) {
				if(!squareLegal(j, i, mat)) {
					return false;
				}
			}
		}
		
		for(int i = 0; i < 9; i++) {
			if(!rowLegal(i, mat) || !colLegal(i, mat)) {
				return false;
			}
		}
		
		return true;
	}
	
//	checks if a given 3x3 Square is legal according to sudoku rules
	public boolean squareLegal(int x, int y, int[][] mat) {
		int square = corresSquare(x, y);
		int xStart = startingCoordinates(square, "x");
		int yStart = startingCoordinates(square, "y");
		
		int[] count = new int[9];
		
		for(int i = yStart; i < yStart+3; i++) {
			for(int j = xStart; j < xStart+3; j++) { 
				if(mat[i][j] == 0) {
					return false;
				}
				count[(mat[i][j]) - 1] += 1;
			}
		}
		
		for(int i = 0; i < 9; i++) {
			if(count[i] > 1 || count[i] < 1) {
				return false;
			}
		}
		
		return true;
	}
	
//	checks if a given row is legal according to sudoku rules
	public boolean rowLegal(int row,int[][] mat) {
		int[] count = new int[9];
		
		
		for(int i = 0; i < 9; i++) {
			count[(mat[row][i])-1] += 1;
		}
		
		for(int i = 0; i < 9; i++) {
			if(count[i] > 1 || count[i] < 1) {
				return false;
			}
		}
		
		return true;
	}
	
//	checks if a given column is legal according to sudoku rules
	public boolean colLegal(int col,int[][] mat) {

		int[] count = new int[9];
		
		
		for(int i = 0; i < 9; i++) {
			count[(mat[i][col])-1] += 1;
		}
		
		for(int i = 0; i < 9; i++) {
			if(count[i] > 1 || count[i] < 1) {
				return false;
			}
		}
		
		return true;
	}

	static void print2dArrayList(ArrayList[][] list) {
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {
				System.out.print(list[i][j].toString() + " , ");
			}
			System.out.println();
		}
	}
	
	static void print2dArray(int[][] array) {

		String[][] strings = new String[array.length][];
		int biggestSize = 0;
		for (int i = 0; i < array.length; i += 1) {
			int[] row = array[i];
			strings[i] = new String[row.length];
			for (int j = 0; j < row.length; j += 1) {
				strings[i][j] = Integer.toString(row[j]);
				if (strings[i][j].length() > biggestSize) {
					biggestSize = strings[i][j].length();
				}
			}
		}

		System.out.print("{");
		for (int i = 0; i < strings.length; i += 1) {
			String[] row = strings[i];
			if (i != 0) {
				System.out.print(" ");
			}
			System.out.print("{");
			for (int j = 0; j < row.length; j += 1) {
				if (j != 0) {
					System.out.print(", ");
				}
				int remainder = biggestSize - row[j].length();
				for (int k = 0; k < remainder; k += 1) {
					System.out.print(" ");
				}
				System.out.print(row[j]);
			}
			System.out.print("}");
			if (i == strings.length - 1) {
				System.out.println("}");
			} else {
				System.out.println(",");
			}
		}
	}

}
