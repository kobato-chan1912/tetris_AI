package com.harunaga.games.tetris.client.piece;

/**
 * class for defining particular face of one piece rotation
 */
public class Rotation {

    public Rotation(int columns, int rows, int centerX, int centerY, String map) {
        this.columns = columns;
        this.rows = rows;
        this.centerX = centerX;
        this.centerY = centerY;
        this.map = map;
    }
    /** width of the piece in blocks */
    int columns;
    /** height of the piece in blocks */
    int rows;
    /** center of rotation of the piece in blocks */
    int centerX;
    /** center of rotation of the piece in blocks */
    int centerY;
    /** map of the piece face - it is array of width X height characters,
     *  where 0 specifies that the block is not there
     *  1 is the piece-block*/
    String map;
}
