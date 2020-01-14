/**
* Bejeweled.java (Skeleton)
*
* This class represents a Bejeweled (TM)
* game, which allows player to make moves
* by swapping two pieces. Chains formed after
* valid moves disappears and the pieces on top
* fall to fill in the gap, and new random pieces
* fill in the empty slots.  Game ends after a
* certain number of moves or player chooses to 
* end the game.
*/

   import java.awt.Color;

    public class Bejeweled {
   
      /* 
   	 * Constants
   	 */  
      final Color COLOUR_DELETE = Color.RED;
      final Color COLOUR_SELECT = Color.BLUE;
   
      final int CHAIN_REQ = 3;	// minimum size required to form a chain
      final int NUMMOVE = 10;		// number of moves to be play in one game
      final int EMPTY = -1; 		// represents a slot on the game board where the piece has disappear  
      
      final int NUMPIECESTYLE;   // number of different piece style
      final int NUMROW;		  		// number of rows in the game board
      final int NUMCOL;	 	  		// number of columns in the game boar
   
      final int CHAIN_END = 8;   // number of ends of possible chains
      final int CHAIN_LOCA = 2;  // maximum number of possible chain locations on each dimension 
      final int PAUSE = 10; 
   
   	
   	/* 
   	 * Global variables
   	 */   
      BejeweledGUI gui;	// the object referring to the GUI, use it when calling methods to update the GUI
      
      int board[][];		// the 2D array representing the current content of the game board
      
      boolean firstSelection;		// indicate if the current selection is the selection of the first piece
      int slot1Row, slot1Col;		// store the location of the first selection
      int slot2Row, slot2Col;    // store the location of the second selection
   //int count_row, count_col;  // store the length of the longest chain 
      int[] loca;                // store the location of ends of possible chains 
      int[] loca_row;     // store the row or column the chain is in when necessary
      int[] loca_col;     
      int chain_size;     // store the size of the total chain 
      
      int score;						// current score of the game
      int numMoveLeft;				// number of move left for the game
      
   	/**************************
       * Constructor: Bejeweled
       **************************/
       public Bejeweled(BejeweledGUI gui) {
         this.gui = gui;
         NUMPIECESTYLE = gui.NUMPIECESTYLE;
         NUMROW = gui.NUMROW;
         NUMCOL = gui.NUMCOL;
         
      	// TO DO:  
      	// - creation of arrays
      	// - initialization of variables 
      	// - initialization of game board (on 2D array and on GUI)   
      
         loca = new int[CHAIN_END];
         board = new int [NUMROW][NUMCOL]; 
         board = initBoard(); 
         updateGUIboard(board); 
         firstSelection = true;
         numMoveLeft = NUMMOVE; 
         score = 0; 
      //       count_row = 0; 
      //       count_col = 0; 
         loca_row = new int[CHAIN_LOCA]; 
         loca_col = new int[CHAIN_LOCA]; 
         loca_row[0] = EMPTY; 
         loca_row[1] = EMPTY; 
         loca_col[0] = EMPTY; 
         loca_col[1] = EMPTY; 
         slot1Row = 0; 
         slot1Col = 0; 
         slot2Row = 0; 
         slot2Col = 0; 
      }
   
   
      /*****************************************************
       * play
       * This method is called when a piece is clicked.  
   	 * Parameter "row" and "column" is the location of the 
   	 * piece that is clicked by the player
       *****************************************************/
       
   
       public void play (int row, int column) {
      	 // TO DO:  implement the logic of the game     
         if (firstSelection){ 
            slot1Row = row; 
            slot1Col = column; 
            firstSelection = false; 
            gui.highlightSlot(row,column,COLOUR_SELECT);
         } 
         else {  // second selection
            slot2Row = row; 
            slot2Col = column; 
            firstSelection = true; 
            gui.highlightSlot(slot1Row,slot1Col,COLOUR_SELECT); 
            gui.highlightSlot(slot2Row,slot2Col,COLOUR_SELECT);
         	
            if (!adjacentPieces(slot1Row,slot1Col,slot2Row,slot2Col)){  // if 2 pieces are apart
               gui.showInvalidMoveMessage();
            } 
            else {
               // when adjacent, swap icons before checking
               switchIcons(slot1Row,slot1Col,slot2Row,slot2Col); 
               
               // check if forms chain 
               if (checkRow(slot1Row,slot1Col,slot2Row,slot2Col) < CHAIN_REQ && checkCol(slot1Row,slot1Col,slot2Row,slot2Col) < CHAIN_REQ){  // if no chain formed
                  switchIcons(slot1Row,slot1Col,slot2Row,slot2Col);
                  gui.showInvalidMoveMessage();
               } 
               else {  // at least one chain formed
                  updateGUIchain(slot1Row,slot1Col,slot2Row,slot2Col); 
                  numMoveLeft--;  // count as a valid move
                  if (checkRow(slot1Row,slot1Col,slot2Row,slot2Col) >= CHAIN_REQ && checkCol(slot1Row,slot1Col,slot2Row,slot2Col) >= CHAIN_REQ){ // both horizontal and vertical chain 
                     chain_size = checkRow(slot1Row,slot1Col,slot2Row,slot2Col) + checkCol(slot1Row,slot1Col,slot2Row,slot2Col) - RepeatIcon();  // eliminate the repeated icon in chains
                  } 
                  else {
                     chain_size = Math.max(checkRow(slot1Row,slot1Col,slot2Row,slot2Col),checkCol(slot1Row,slot1Col,slot2Row,slot2Col));  // chain on only one dimension 
                  }
               	// unhighlight selections and highlight chain
                  gui.unhighlightSlot(slot1Row,slot1Col); 
                  gui.unhighlightSlot(slot2Row,slot2Col); 
                  highlightChain(loca,loca_row,loca_col); 
               	// show chain size and add score
                  gui.showChainSizeMessage(chain_size);
                  score += chain_size;  
               	// delete chain and update GUI
                  deleteChain(); 
                  updateGUIchain(slot1Row,slot1Col,slot2Row,slot2Col); 
                  falling(); 
                  // updating score and move left
                  gui.setMoveLeft(numMoveLeft); 
                  gui.setScore(score); 
               	// check if moves are used up
                  if (numMoveLeft == 0){
       					unhighlightChain(loca,loca_row,loca_col);              
							endGame(); 
                  }  
               }// end of one valid move
            }// end of one set of swaps of two adjacent pieces
            resetLoca();
         }// end of second selection
      }// end of play method
   
      /*****************************************************
       * endGame
       * This method is called when the player clicks on the
   	 * "End Game" button
   	 *****************************************************/
       public void endGame() {
         gui.showGameOverMessage(score,NUMMOVE-numMoveLeft);
      }
     
     
      
   // method that iniialize the whole gameboard array (including chain ends)
       public int[][] initBoard (){
         int[][] board= new int[NUMROW][NUMCOL]; 
         for (int i = 0; i < NUMROW; i++){
            for (int k = 0; k < NUMCOL; k++){
               board[i][k] = (int)(Math.random()*(NUMPIECESTYLE-1));
            }
         }
         for (int j = 0; j < CHAIN_END; j++){
            loca[j] = EMPTY; 
         }
         return board; 
      }
   
   // method that updates the whole gui(icons generated, moves, score)
       public void updateGUIboard (int[][] orig){
         for (int i = 0; i < orig.length; i++){
            for (int k = 0; k < orig[i].length; k++){
               gui.setPiece(i,k,orig[i][k]+1); 
            }
         }
         gui.setMoveLeft(NUMMOVE); 
         gui.setScore(score); 
      }
   
   // method that updates the column and row of the icon
       public void updateGUIchain (int x1, int y1, int x2, int y2){
         for (int i = Math.min(x1,x2); i <= Math.max(x1,x2); i++){
            for (int k = 0; k < NUMCOL; k++){
               gui.setPiece(i,k,board[i][k]+1); 
            }
         }
         for (int k = Math.min(y1,y2); k <= Math.max(y1,y2); k++){
            for (int i = 0; i < NUMROW; i++){
               gui.setPiece(i,k,board[i][k]+1); 
            }
         }
      }
   
   // method that check if the two selected pieces are adjacent
       public static boolean adjacentPieces (int x1, int y1, int x2, int y2) {
         return (x1==x2 && (y2==y1-1 || y2==y1+1) || y1==y2 && (x2==x1-1 || x2==x1+1)); 
      }
   
   // method that swap two icons only
       public void switchIcons (int x1, int y1, int x2, int y2){
         int temp = board[x1][y1]; 
         board[x1][y1] = board[x2][y2]; 
         board[x2][y2] = temp; 
      }
   
   // method that checks if chain is formed on the row of selected icons
       public int checkRow (int piece1Row,int piece1Col,int piece2Row,int piece2Col){
         int count1 = 1; 
         int count2 = 1; 
         int longest = 1; 
         int x1,x2,y1,y2,icon1,icon2;
       
         if (piece1Row == piece2Row){    // one on left and the other one on right
            if (piece1Col < piece2Col){
               y1 = piece1Col;
               y2 = piece2Col; 
               icon1 = board[piece1Row][piece1Col];   // icon1 is always the color of the one on left 
               icon2 = board[piece2Row][piece2Col];   // icon2 is always the color of the one on right 
            } 
            else {                                  // y1 is always the one on left
               y2 = piece1Col;                       // y2 is always the one on right
               y1 = piece2Col; 
               icon2 = board[piece1Row][piece1Col]; 
               icon1 = board[piece2Row][piece2Col]; 
            }
         
            while (y1-count1 >= 0 && board[piece1Row][y1-count1] == icon1){
               count1++;                       // count leftwards from the left icon 
            }
				loca[0] = y1 - count1 + 1;      // locate the col of most-left piece of the possible chain
				 
            while (y2+count2 < NUMCOL && board[piece2Row][y2+count2] == icon2){
               count2++;                       // count rightwards from the right icon
            }
				loca[1] = y2 + count2 - 1;      // locate the col of most-right piece of the possible chain
                     
            if (icon1==icon2 || count1>=CHAIN_REQ && count2>=CHAIN_REQ){  // two pieces are the same || chains on both sides
               longest = count1 + count2;      // count the total number of pieces in the chain
               loca_row[0] = piece1Row;         // locate the row of the chain
            } 
            else {  // chain on only one side formed || no chain formed
               if (count1 >= CHAIN_REQ){       // locate the chain when the chain is on the left
                  longest = count1; 
                  loca[1] = y1; 
                  loca_row[0] = piece1Row; 
               } 
               else if (count2 >= CHAIN_REQ){ // locate the chain when the chain is on the right
                  longest = count2; 
                  loca[0] = y2;   
                  loca_row[0] = piece2Row; 
               } 
               else {  // no chain formed
                  longest = Math.max(count1,count2); 
						loca[0] = EMPTY; 
						loca[1] = EMPTY; 
               }
            }
         } 
         else {                              // one on top of the other
            if (piece1Row < piece2Row){              // x1 is always the top one
               x1 = piece1Row;                      // x2 is always the bottom one
               x2 = piece2Row;                      // icon1 is always the color of the top one
               icon1 = board[piece1Row][piece1Col];  // icon2 is always the color of the bottom one
               icon2 = board[piece2Row][piece2Col]; 
            } 
            else {
               x2 = piece1Row; 
               x1 = piece2Row; 
               icon2 = board[piece1Row][piece1Col]; 
               icon1 = board[piece2Row][piece2Col]; 
            }
         
            while (piece1Col-count1 >= 0 && board[x1][piece1Col-count1] == icon1){
               count1++; 
            }
            loca[0] = piece1Col - count1 + 1; 
            while (loca[0]+count1 < NUMCOL && board[x1][loca[0]+count1] == icon1){
               count1++; 
            }
            loca[1] = loca[0] + count1 - 1; 
         
            while (piece2Col-count2 >= 0 && board[x2][piece2Col-count2] == icon2){
               count2++; 
            }
            loca[2] = piece2Col - count2 + 1; 
            while (loca[2]+count2 < NUMCOL && board[x2][loca[2]+count2] == icon2){
               count2++; 
            }
            loca[3] = loca[2] + count2 - 1;
         
            if (count1>=CHAIN_REQ && count2>=CHAIN_REQ){
               longest = count1 + count2; 
               loca_row[0] = x1; 
               loca_row[1] = x2; 
            } 
            else {
               if (count1 >= CHAIN_REQ){
                  longest = count1; 
                  loca[2] = EMPTY; 
                  loca[3] = EMPTY; 
                  loca_row[0] = x1; 
               } 
               else if (count2 >= CHAIN_REQ){
                  longest = count2; 
                  loca[0] = EMPTY; 
                  loca[1] = EMPTY; 
                  loca_row[1] = x2; 
               } 
               else {
                  longest = count1; 
                  loca[0] = EMPTY; 
                  loca[1] = EMPTY;
                  loca[2] = EMPTY;
                  loca[3] = EMPTY;
               }
            }
         }
         return longest; 
      }
   
   
      // method that checks if chain is formed in column
       public int checkCol (int piece1Row,int piece1Col,int piece2Row,int piece2Col){
         int count1 = 1; 
         int count2 = 1; 
         int longest = 1; 
         int x1,x2,y1,y2,icon1,icon2;
       
         if (piece1Col == piece2Col){                     // one is on top of the other 
            if (piece1Row < piece2Row){                   // x1 is always the one on the top 
               x1 = piece1Row;                           // x2 is always the one at the bottom
               x2 = piece2Row;                           // icon1 is always the color of the top one
               icon1 = board[piece1Row][piece1Col];       // icon2 is always the color of the bottom one
               icon2 = board[piece2Row][piece2Col]; 
            } 
            else {
               x2 = piece1Row; 
               x1 = piece2Row; 
               icon2 = board[piece1Row][piece1Col]; 
               icon1 = board[piece2Row][piece2Col]; 
            }
         
            while (x1-count1 >= 0 && board[x1-count1][piece1Col] == icon1){
               count1++; 
            }
            loca[4] = x1 - count1 + 1; 
         
            while (x2+count2 < NUMROW && board[x2+count2][piece2Col] == icon2){
               count2++; 
            }
            loca[5] = x2 + count2 - 1; 
         
            if (icon1==icon2 || count1>=CHAIN_REQ && count2>=CHAIN_REQ){
               longest = count1 + count2; 
               loca_col[0] = piece1Col; 
            } 
            else {
               if (count1 >= CHAIN_REQ){
                  longest = count1; 
                  loca[5] = x1; 
                  loca_col[0] = piece1Col; 
               } 
               else if (count2 >= CHAIN_REQ){
                  longest = count2; 
                  loca[4] = x2; 
                  loca_col[0] = piece1Col; 
               } 
               else {
                  longest = count1; 
                  loca[4] = EMPTY; 
                  loca[5] = EMPTY; 
               }
            }
         } 
         else {                                     // one on the left and the other on right
            if (piece1Col < piece2Col){                 // y1 is always the one on the left
               y1 = piece1Col;                         // y2 is always the one on the right
               y2 = piece2Col;                         // icon1 is always color of the left one
               icon1 = board[piece1Row][piece1Col];     // icon2 is always color of the right one
               icon2 = board[piece2Row][piece2Col]; 
            } 
            else {
               y2 = piece1Col; 
               y1 = piece2Col; 
               icon2 = board[piece1Row][piece1Col]; 
               icon1 = board[piece2Row][piece2Col]; 
            }
         
            while (piece1Row-count1 >= 0 && board[piece1Row-count1][y1] == icon1){
               count1++; 
            }
            loca[4] = piece1Row - count1 + 1; 
            while (loca[4]+count1 < NUMROW && board[loca[4]+count1][y1] == icon1){
               count1++; 
            }
            loca[5] = loca[4] + count1 - 1; 
         
            while (piece2Row-count2 >= 0 && board[piece2Row-count2][y2] == icon2){
               count2++; 
            }
            loca[6] = piece2Row - count2 + 1; 
            while (loca[6]+count2 < NUMROW && board[loca[6]+count2][y2] == icon2){
               count2++; 
            }
            loca[7] = loca[6] + count2 - 1;
         
            if (count1>=CHAIN_REQ && count2>=CHAIN_REQ){
               longest = count1 + count2; 
               loca_col[0] = y1; 
               loca_col[1] = y2; 
            } 
            else {
               if (count1 >= CHAIN_REQ){
                  longest = count1; 
                  loca[6] = EMPTY; 
                  loca[7] = EMPTY; 
                  loca_col[0] = y1; 
               } 
               else if (count2 >= CHAIN_REQ){
                  longest = count2; 
                  loca[4] = EMPTY; 
                  loca[5] = EMPTY; 
                  loca_col[1] = y2; 
               } 
               else {
                  longest = count1; 
                  loca[4] = EMPTY; 
                  loca[5] = EMPTY;
                  loca[6] = EMPTY;
                  loca[7] = EMPTY;
               }
            }
         }
         return longest; 
      }
   
   // method that highligh the chain
       public void highlightChain (int[] location, int[] row_location, int[] col_location) {
         // horizontal chains
			for (int i = 0; i < CHAIN_LOCA; i++){     
            if (row_location[i]!=EMPTY && location[2*i]!=EMPTY){
               for (int j = location[2*i]; j <= location[2*i+1]; j++){
                  gui.highlightSlot(row_location[i],j,COLOUR_DELETE); 
               }
            }
         
         }
          // vertical chains
         for (int k = 0; k < CHAIN_LOCA; k++){
            if (location[CHAIN_END/2+2*k]!=EMPTY && col_location[k]!=EMPTY){
               for (int j = location[CHAIN_END/2+2*k]; j <= location[CHAIN_END/2+2*k+1]; j++){
                  gui.highlightSlot(j,col_location[k],COLOUR_DELETE); 
               }
            }
         }
      
      }
       // unhilight chains
        public void unhighlightChain (int[] location, int[] row_location, int[] col_location) {
         // horizontal chains
			for (int i = 0; i < CHAIN_LOCA; i++){     
            if (row_location[i]!=EMPTY && location[2*i]!=EMPTY){
               for (int j = location[2*i]; j <= location[2*i+1]; j++){
                  gui.unhighlightSlot(row_location[i],j); 
               }
            }
         
         }
          // vertical chains
         for (int k = 0; k < CHAIN_LOCA; k++){
            if (location[CHAIN_END/2+2*k]!=EMPTY && col_location[k]!=EMPTY){
               for (int j = location[CHAIN_END/2+2*k]; j <= location[CHAIN_END/2+2*k+1]; j++){
                  gui.unhighlightSlot(j,col_location[k]); 
               }
            }
         }
      
      }  
   // method that checks if two chains repeat
       public int RepeatIcon () {
         int repeat = 0; 
         if (loca[0] <= loca_col[0] && loca_col[0] <= loca[1] && loca[0] <= loca_col[1] && loca_col[1] <= loca[1] && loca[4] <= loca_row[0] && loca_row[0] <= loca[5] && loca[6] <= loca_row[0] && loca_row[0] <= loca[7]
         || loca[4] <= loca_row[0] && loca_row[0] <= loca[5] && loca[4] <= loca_row[1] && loca_row[1] <= loca[5] && loca[0] <= loca_col[0] && loca_col[0] <= loca[1] && loca[2] <= loca_col[0] && loca_col[0] <= loca[3]){
            repeat = 2; 
         } 
         else if (loca[0] <= loca_col[0] && loca_col[0] <= loca[1] && loca[4] <= loca_row[0] && loca_row[0] <= loca[5]
              || loca[0] <= loca_col[1] && loca_col[1] <= loca[1] && loca[6] <= loca_row[0] && loca_row[0] <= loca[7]
              || loca[4] <= loca_row[0] && loca_row[0] <= loca[5] && loca[0] <= loca_col[0] && loca_col[0] <= loca[1]
              || loca[4] <= loca_row[1] && loca_row[1] <= loca[5] && loca[2] <= loca_col[0] && loca_col[0] <= loca[3]){
            repeat = 1; 
         } 
         return repeat; 
      }
   
   
   // method that deletes chain
       public void deleteChain (){
         for (int i = 0; i < CHAIN_LOCA; i++){     // horizontal chains
            if (loca_row[i]!=EMPTY && loca[2*i]!=EMPTY){
               for (int j = loca[2*i]; j <= loca[2*i+1]; j++){
                  board[loca_row[i]][j] = EMPTY; 
               }
            }
         
         }
          // vertical chains
         for (int k = 0; k < CHAIN_LOCA; k++){
            if (loca[CHAIN_END/2+2*k]!=EMPTY && loca_col[k]!=EMPTY){
               for (int j = loca[CHAIN_END/2+2*k]; j <= loca[CHAIN_END/2+2*k+1]; j++){
                  board[j][loca_col[k]] = EMPTY; 
               }
            }
         }
      
      }
   
   // method that makes the icons fall
       public void falling (){
         int bottom = Math.max(Math.max(loca[5],loca[7]),Math.max(loca_row[0],loca_row[1])); 
         int right = Math.max(Math.max(loca[1],loca[3]),Math.max(loca_col[0],loca_col[1]));
         int temp = Math.max(Math.max(loca[0],loca[2]),Math.max(loca_col[0],loca_col[1]));; 
         for (int i = 0; i < 2; i++){
            if (loca[i*2] != EMPTY && loca[i*2] < temp){
               temp = loca[i*2]; 
            }
            if (loca_col[i] != EMPTY && loca_col[i] < temp){
               temp = loca_col[i]; 
            }
         }
         int left = temp; 
      
         try{
            Thread.sleep(PAUSE); 
         } 
             catch (InterruptedException iox) {}
      
         for (int j = left; j <= right; j++){
            for (int i = bottom; i >= 0; i--){
               while (board[i][j] == EMPTY){
                  for (int k = i; k > 0; k--){
                     board[k][j] = board[k-1][j];
                     try{
                        Thread.sleep(PAUSE); 
                     } catch (InterruptedException iox) { }
                     gui.setPiece(k,j,board[k][j]+1);              
                  }
               
                  board[0][j] = (int)(Math.random()*(NUMPIECESTYLE-1)); 
                  try{
                     Thread.sleep(PAUSE); 
                  } catch (InterruptedException iox) {}
                  gui.setPiece(0,j,board[0][j]+1);
               }
            }
         }
      // for (int i = 0; i <= bottom; i++){
      //          for (int k = left; k <= right; k++){
      //             gui.setPiece(i,k,board[i][k]+1);
      //             try{
      //                Thread.sleep(5); 
      //             } catch (InterruptedException iox) {}
      //          }
      //       }
      }
   
   // method that resets location data
       public void resetLoca (){
         unhighlightChain(loca,loca_row,loca_col); 
         gui.unhighlightSlot(slot1Row,slot1Col); 
         gui.unhighlightSlot(slot2Row,slot2Col); 
         for (int i = 0; i < CHAIN_END; i++){             
            loca[i] = EMPTY; 
         }
         for (int i = 0; i < CHAIN_LOCA; i++){
            loca_row[i] = EMPTY; 
            loca_col[i] = EMPTY; 
         }
      }
   
   }