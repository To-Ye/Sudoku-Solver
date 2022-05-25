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



public class Sudoku {
	
	Random rand = new Random();
	
	public static void main(String[] args) throws FileNotFoundException {

		Sudoku sudoku = new Sudoku();
		sudoku.solve();

	}

	public void solve() throws FileNotFoundException {
		ArrayList[][] possibilities = getInput(new File("input.txt"));
		ezLogic(possibilities);

		if (solved(possibilities)) {
			printResult(possibilities);
		} else {
			printResult(recursiveSudoku(possibilities));
			//System.out.println("too hard :-(");
		}
	}

	public int HashSum(ArrayList[][] mat){
		int sum = 0;

		for(int i = 0; i < 9; i++){
			for(int j = 0; j < 9; j++){
				ArrayList<Integer> current = mat[i][j];
				for(int x : current){
					sum += x * (j+1) * (i + 1);
				}
				//sum = sum * (j+1) * (i+1);
			}

		}

		return sum;
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
					out[i][j] = new ArrayList<>(Arrays.asList(Character.getNumericValue(charLine[j])));
				}
			}
		}
		
		return out;
	}
	
	public void ezLogic(ArrayList[][] poss){
		boolean change = true;

		while(change) {

			ArrayList<Integer>[][] old = copy2dArrayList(poss);
			
			adjustDefRowCol(poss);
			adjustDefSquare(poss);
						
			change = change(old, poss);
		}
		
	}

	public ArrayList[][] recursiveSudoku(ArrayList[][] poss) {
		
		int x = minList(poss)[0];
		int y = minList(poss)[1];
		
		if(x == -1 || y == -1) {
			return null;
		}
		
		for(int i = 0; i < poss[y][x].size(); i++) {
			int temp = (int) poss[y][x].get(i);
			ArrayList[][] newPoss = copy2dArrayList(poss);
			newPoss[y][x] = new ArrayList<>(Arrays.asList(temp));
			ezLogic(newPoss);
			//printResult(newPoss);
			if(legal(newPoss)){
				if(solved(newPoss)){
					return newPoss;
				} else {
					ArrayList[][] copy = recursiveSudoku(newPoss);
					if(copy != null) {
						return copy;
					}
				}
			}
		}
		
		return null;
	}
	
	public int[] minList(ArrayList[][] list) {
		int[] out = new int[2];

		int minSize = Integer.MAX_VALUE;
		boolean stop = false;
		
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {
				if(list[i][j].size() == 2) {
					out[0] = j;
					out[1] = i;
					stop = true;
					break;
				}
				
				if( (list[i][j].size() > 1) && (list[i][j].size() < minSize) ) {
					out[0] = j;
					out[1] = i;
				}
			}
			if(stop) {
				break;
			}
		}
		
		return out;
	}

//	goes through the alrd filled-in numbers and calls adjustPoss whenever u is not 0, with the coordinates X, Y and u
	public void adjustDefRowCol(ArrayList[][] poss) {
		
		ArrayList[][] newPoss = copy2dArrayList(poss);
		
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {
				if(poss[i][j].size() == 1) {
					Integer u = (Integer) poss[i][j].get(0);
					adjustPoss(j, i, u, poss);
				}
			}
		}
		
		
	}
	
//	goes through the alrd filled-in numbers and calls adjustSquare whenever u is not 0, with the coordinates X, Y and u
	public void adjustDefSquare(ArrayList[][] poss) {
		
		ArrayList[][] newPoss = copy2dArrayList(poss);
		
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {
				if(poss[i][j].size() == 1) {
					int u = (int) poss[i][j].get(0);
					adjustSquare(j, i, u, poss);
				}
			}
		}
		
	}

//	removes the integer u from every column and row, except from the original position
	public void adjustPoss(int x, int y, Integer u, ArrayList[][] poss) {
		
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {
				if(i == y || j == x) {
					if(i == y && j == x) {
//						if(poss[i][j].size() > 1) {
							poss[i][j].removeIf(n -> (n != u));
//						}
					} else {
//						if(poss[i][j].size() > 1) {
							poss[i][j].removeIf(n -> (n == u));
//						}
						
					}
				}
			}
		}
		
	}
	
//	removes the integer u, in the possibilities of every position in the square, except his own
	public void adjustSquare(int x, int y, Integer u, ArrayList[][] poss) {
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
	}

//	checks if there were any adjustments to the matrix
	public boolean change(ArrayList[][] possOld, ArrayList[][] possNew) {
		
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {
				if(possOld[i][j].size() != possNew[i][j].size()) {
					return true;
				}

				for(int k = 0; k < possOld[i][j].size(); k++){
					if(possOld[i][j].get(k) != possNew[i][j].get(k)) {
						return true;
					}
				}


			}
		}
		
		return false;
	}

	public int listSum(ArrayList<Integer> list){
		int sum = 0;

		for(int x : list){
			sum += x;
		}

		return sum;
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
				ArrayList<Integer> current = poss[i][j];
				newPoss[i][j] = new ArrayList<>();
				for(int n : current) {
					newPoss[i][j].add(n);
				}
			}
		}
		
		return newPoss;
	}
	
	public boolean solved(ArrayList[][] list) {
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {
				if(list[i][j].size() != 1) {
					return false;
				}
			}
		}
		
		return true;
	}
	
//	checks if a given 9x9 matrix is legal according to sudoku rules
	public boolean legal(ArrayList[][] list) {		
		
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {
				if(list[i][j].size() == 0) {
					return false;
				}
			}
		}
		
		return true;
		
	}
	
	static void printResult(ArrayList[][] list) {
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {
				if(list[i][j].size() == 1) {
					System.out.print(list[i][j].toString() + " , ");
				} else {
					System.out.print("[0]" + " , ");
				}
				
			}
			System.out.println();
		}
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
