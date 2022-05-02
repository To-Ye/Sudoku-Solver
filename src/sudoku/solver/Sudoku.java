package sudoku.solver;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Sudoku {
	
	ArrayList[][] possibilities;
	HashSet<int[][]> alrdProcessed = new HashSet<>();
	Random rand = new Random();
	
	public static void main(String[] args) {
		
		
//		int[][] solMat =  { { 4, 1, 0, 0, 6, 0, 0, 7, 0 }, 
//							{ 0, 0, 3, 0, 8, 5, 0, 0, 9 }, 
//							{ 0, 2, 0, 3, 7, 0, 5, 0, 1 },
//							{ 0, 3, 0, 6, 0, 9, 2, 5, 0 }, 
//							{ 6, 0, 0, 5, 0, 1, 0, 0, 0 }, 
//							{ 0, 0, 9, 0, 2, 0, 0, 0, 3 },
//							{ 0, 0, 6, 2, 0, 0, 7, 4, 5 }, 
//							{ 0, 0, 0, 4, 0, 6, 8, 0, 0 }, 
//							{ 2, 8, 4, 0, 0, 0, 1, 9, 6 } };
		
		int[][] solMat =  {{0,7,0,0,0,5,0,0,0},
							{5,0,0,7,0,2,0,0,0},
							{0,3,0,0,8,0,0,7,0},
							{0,9,0,0,6,0,7,5,8},
							{0,0,0,0,3,0,0,0,0},
							{0,0,5,0,0,0,4,0,6},
							{0,0,9,0,0,0,6,0,5},
							{0,0,0,0,0,8,0,2,1},
							{8,0,0,0,9,0,0,0,0}	};
		
//		int[][] solMat = {{1,2,3,4,5,6,7,8,9},
//						 {6,7,8,9,1,2,3,4,5},
//						 {2,3,4,5,6,7,8,9,1},
//						 {7,8,9,1,2,3,4,5,6},
//						 {3,4,5,6,7,8,9,1,2},
//						 {8,9,1,2,3,4,5,6,7},
//						 {4,5,6,7,8,9,1,2,3},
//						 {9,1,2,3,4,5,6,7,8},
//						 {0,6,7,8,9,1,2,3,4}};
		
		
		
		
		
		Sudoku sudoku = new Sudoku();
		int[][] out = sudoku.solver(solMat, sudoku.possibilities);
		out = sudoku.recursiveSudoku(out, sudoku.possibilities);
		//sudoku.possGoThrough(solMat, sudoku.possibilities);
		
		
		
//		ArrayList[][] test = sudoku.copy2dArrayList(sudoku.possibilities);
//		
//		for(int i = 0; i < 9; i++) {
//			for(int j = 0; j < 9; j++) {
//				System.out.print(sudoku.possibilities[i][j].toString() + " , ");
//			}
//			System.out.println();
//		}
//		
//		System.out.println();
//		
//		for(int i = 0; i < 9; i++) {
//			for(int j = 0; j < 9; j++) {
//				System.out.print(test[i][j].toString() + " , ");
//			}
//			System.out.println();
//		}
		
		print2dArray(out);
		
		
	}
	
//	constructor (fills the possibilities matrix)
	public Sudoku() {
		
		this.possibilities = new ArrayList[9][9];
		
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {
				ArrayList<Integer> newList = new ArrayList<>();
				for(int k = 0; k < 9; k++) {
					newList.add(k+1);
				}
				
				this.possibilities[i][j] = newList;
				
			}
		}
		
		
	}
	
	
	public int[][] solver(int[][] mat, ArrayList[][] poss){
		boolean change = true;
		int[][] current = copy2dArray(mat);
		ArrayList[][] newPoss = copy2dArrayList(poss);
			
		while(change) {
			
			int[][] old = copy2dArray(current);
			
			newPoss = adjustDefRowCol(current, newPoss);
			newPoss = adjustDefSquare(current, newPoss);
			
			current = checkPoss(current, newPoss);
			
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
				if(mat1[i][j] == 0) {
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
		

		for(int i = 0; i < poss[y][x].size(); i++) {
			mat1[y][x] = (int) poss[y][x].get(i);
			int[][] mat2 = solver(mat1, newPoss);
			
			if(legal(mat2)) {
				return mat1;
			} else {
				return recursiveSudoku(mat1, newPoss);
			}	
		}
		
		return null;
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
	
	public int[][] processed(int[][] mat){
		int[][] current = randomSol(mat);
		
//		while(this.alrdProcessed.contains(current)) {
//			current = randomSol(mat);
//		}
//		
//		this.alrdProcessed.add(current);
		
		return current;
	}
	
//	removes the integer u from every column and row, except from the original position
	public ArrayList<Integer>[][] adjustPoss(int x, int y, Integer u, ArrayList[][] poss) {
		
		ArrayList[][] newPoss = copy2dArrayList(poss);
		
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {
				if(i == y || j == x) {
					if(i == y && j == x) {
						if(newPoss[i][j].size() > 1) {
							newPoss[i][j].removeIf(n -> (n != u));
						}
					} else {
						if(newPoss[i][j].size() > 1) {
							newPoss[i][j].removeIf(n -> (n == u));
						}
						
					}
				}
			}
		}
		
		return newPoss;
		
	}
	
//	removes the integer u, in the possibilities of every position in the square, except his own
	public ArrayList<Integer>[][] adjustSquare(int x, int y, Integer u, ArrayList[][] poss) {
		int square = corresSquare(x, y);
		
		int xStart = startingCoordinates(square, "x");
		int yStart = startingCoordinates(square, "y");
		
		ArrayList[][] newPoss = copy2dArrayList(poss);
		
		for(int i = yStart; i < yStart+3; i++) {
			for(int j = xStart; j < xStart+3; j++) {
					if(newPoss[i][j].size() > 1) {
						if(i == y && j == x) {
							newPoss[i][j].removeIf(n -> (n != u));
						} else {
							newPoss[i][j].removeIf(n -> (n == u));
						}
					}
			}
		}
		
		return newPoss;		
	}
	
//	goes through the possibility-Matrix, and wherever it is only one possibility, it fills it in the out-Matrix
	public int[][] checkPoss(int[][] mat, ArrayList[][] poss){
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {
				if(poss[i][j].size() == 1) {
					mat[i][j] = (int) poss[i][j].get(0);
				}
			}
		}
		
		return mat;
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
	
//	creates a random solution for the sudoku (the rules get tested in an other step)
	public int[][] randomSol(int [][] mat){
		int[][] out = new int[9][9];
		
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if(mat[i][j] == 0) {
					int maxBound = this.possibilities[i][j].size();
					int randIndex = rand.nextInt(maxBound);
					int randElement = (int) this.possibilities[i][j].get(randIndex);
					out[i][j] = randElement;
				} else {
					out[i][j] = mat[i][j];
				}
				
			}
		}
		
		return out;
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
