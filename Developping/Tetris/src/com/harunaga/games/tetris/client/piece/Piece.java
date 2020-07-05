package com.harunaga.games.tetris.client.piece;

import java.awt.Image;

/**
 * class of tetris-piece definition
 */
public class Piece {

    public Piece(int totalRotation, Image image, Rotation[] rotations) {
        this.totalRotation = totalRotation;
        this.image = image;
        this.rotations = rotations;
    }
    /** x-coordinate in the board */
    int currentX;
    /** y-coordinate in the board */
    int currentY;
    /** current rotation in the board */
    int currentRotations;
    /** number of rotation */
    int totalRotation;
    /** image of the piece block */
    Image image;
    /** array of piece rotations **/
    Rotation[] rotations;

//    void setType(int totalRotation, Image image, Rotation[] rotations) {
//        this.totalRotation = totalRotation;
//        this.image = image;
//        this.rotations = rotations;
//    }
//
//    void setType(Piece model) {
//        setType(model.totalRotation, model.image, model.rotations);
//    }
}
