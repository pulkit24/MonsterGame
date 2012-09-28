package monsterRun.common.model;

import monsterRun.common.model.enums.GameBoardType;

// DON'T FORMAT
public class GameGridPattern {

	private static final String DEFAULT = 	"*********\n" + 
											"*   *   *\n" + 
											"*   *   *\n" + 
											"*   *   *\n" + 
											"*********\n" + 
											"*   *   *\n" + 
											"*   *   *\n" + 
											"*   *   *\n" + 
											"*********\n" ;

	private static final String WIDE = 		"*********************\n" + 
											"*    *    *    *    *\n" + 
											"*    *    *    *    *\n" + 
											"*********************\n" + 
											"*         *         *\n" + 
											"*         *         *\n" + 
											"*********************\n" ;
	
	private static final String COMPLEX = 		"*************************\n" + 
												"*     *     *     *   	 *\n" + 
												"*     *     *     *     *\n" + 
												"*******     *     *******\n" + 
												"*   *       *       *   *\n" + 
												"*   *****************   *\n" +
												"*   *               *   *\n" +
												"*   *  **********   *   *\n" +
												"*   *  * *    * *   *   *\n" +
												"*   *  ***    ***   *   *\n" +
												"*   *  *        *   *   *\n" +
												"*   *  *  ***** *   *   *\n" +
												"*********** * ***********\n" +
												"*   *  *  ***** *   *   *\n" +
												"*   *  *        *   *   *\n" +
												"*   *  ***	   ***   *   *\n" +
												"*   *  * *    * *   *   *\n" +
												"*   *  **********   *   *\n" +
												"*   *               *   *\n" +
												"*   *****************   *\n" +
												"*   *     	 *       *   *\n" +
												"*******   	 *     *******\n" +
												"*     *   	 *     *     *\n" +
												"*     *   	 *     *     *\n" +
												"*************************\n" ;


	private static final String PACMAN = 	"************  ************\n" +
											"*    *     *  *     *    *\n" +
											"*    *     *  *     *    *\n" +
											"*    *     *  *     *    *\n" +
											"**************************\n" +
											"*    *  *        *  *    *\n" +
											"*    *  *        *  *    *\n" +
											"******  ****  ****  ******\n" +
											"     *     *  *     *     \n" +
											"     *     *  *     *     \n" +
											"     *  **********  *     \n" +
											"     *  *        *  *     \n" +
											"*    *  *        *  *    *\n" +
											"**************************\n" +
											"*    *  *        *  *    *\n" +
											"     *  **********  *     \n" +
											"     *  *        *  *     \n" +
											"************  ************\n" +
											"*    *     *  *     *    *\n" +
											"*    *     *  *     *    *\n" +
											"***  ****************  ***\n" +
											"  *  *  *        *  *  *  \n" +
											"  *  *  *        *  *  *  \n" +
											"******  ****  ****  ******\n" +
											"*          *  *          *\n" +
											"*          *  *          *\n" +
											"**************************\n" ;
	
	/**
	 * Returns the appropriate grid patterns string according
	 * to the {@link GameBoardType} enum
	 * 
	 * @param board
	 * @return
	 */
	private static String getPattern(GameBoardType board) {

		switch (board) {
		case PACMAN:
			return PACMAN;
		case COMPLEX:
			return COMPLEX;
		case WIDE:
			return WIDE;
		case DEFAULT:
			return DEFAULT;
		default:
			break;
		}

		return null;
	}

	public static boolean[][] parse(GameBoardType board) {
		return parse(getPattern(board));
	}

	/**
	 * Parses the game board string and converts it to a boolean array
	 * A '*' is a path and a ' ' is a blocked cell
	 * 
	 * @param gameBoard
	 * @return
	 */
	public static boolean[][] parse(String gameBoard) {

		int rows = 0, columns = 0;

		String[] rowTokens = gameBoard.split("\n");

		rows = rowTokens.length;

		for (String t : rowTokens) {
			columns = t.length() > columns ? t.length() : columns;
		}

		boolean[][] cells = new boolean[rows][columns];

		for (int i = 0; i < rows; i++) {
			int rowLength = rowTokens[i].length();

			for (int j = 0; j < rowLength; j++) {
				boolean isValid = false;

				if (rowTokens[i].charAt(j) == '*') {
					isValid = true;
				} else if (rowTokens[i].charAt(j) == ' ') {
					isValid = false;
				}

				cells[i][j] = isValid;
			}
		}

		return cells;
	}

}
