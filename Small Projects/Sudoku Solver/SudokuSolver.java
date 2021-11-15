import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class SudokuSolver{
   public static void main(String[] args){
      int[][] sudokuBoard = readInSudokuBoard();
      printBoard(sudokuBoard);
      System.out.println();
      solveBoard(sudokuBoard);
      printBoard(sudokuBoard);
   }
   
   public static int[][] readInSudokuBoard(){
	   Scanner input = null;
	   try{
         input = new Scanner(new File("board2.txt"));
      }catch(FileNotFoundException e){
         System.out.println("File Not Found");
      }
      
      int[][] board = new int[9][9];
      for(int r=0;r<board.length;r++){
         for(int c=0;c<board[r].length;c++){
            board[r][c]=input.nextInt();
         }
      }
      return board;
   }
   
   public static void printBoard(int[][] board){
      for(int r=0;r<board.length;r++){
         for(int c=0;c<board[r].length;c++){
            System.out.print(board[r][c]);
         }
         System.out.println();
      }
   }
   
   public static void solveBoard(int[][] board){
      for(int row=0;row<board.length;row++){
         for(int col=0;col<board[row].length;col++){
            if(board[row][col]==0){
               solveNumber(row,col,board);
            }
         }
      }
      
      for(int row=0;row<board.length;row++){
         for(int col=0;col<board[row].length;col++){
            if(board[row][col]==0){
               solveBoard(board);
            }
         }
      }
   }
   
   public static void solveNumber(int r, int c, int[][] b){
      int[] testNumbers = new int[9];
      
      for(int i=0;i<testNumbers.length;i++){
         testNumbers[i]=i+1;
      }

      for(int i=0;i<b.length;i++){
         for(int tN=0;tN<testNumbers.length;tN++){
            if(b[i][c]==testNumbers[tN]){
               testNumbers[tN]=0;
            }
         }
      }
      
      for(int i=0;i<b.length;i++){
         for(int tN=0;tN<testNumbers.length;tN++){
            if(b[r][i]==testNumbers[tN]){
               testNumbers[tN]=0;
            }
         }
      }
      
      int rStart = (r/3)*3;
      int rFinish = rStart+2;
      int cStart = (c/3)*3;
      int cFinish = cStart+2;
      
      for(int row=rStart;row<=rFinish;row++){
         for(int col=cStart;col<=cFinish;col++){
            for(int tN=0;tN<testNumbers.length;tN++){
               if(b[row][col]==testNumbers[tN]){
                  testNumbers[tN]=0;
               }
            }
         }
      }
      
      int count=0;
      
      for(int i: testNumbers){
         if(i==0){
            count++;
         }
      }
      
      if(count==8){
         b[r][c]=remainingNumber(testNumbers);
      }
      
      
   }
   
   public static int remainingNumber(int[] test){
      for(int i=0;i<test.length;i++){
         if(test[i]!=0){
            return test[i];
         }
      }
      return 0;
   }
}
