/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harunaga.games.tetris.client.piece;

import java.awt.Graphics2D;
import java.awt.Image;
import java.util.Random;
import org.apache.log4j.Logger;

/**
 *
 * @author Harunaga
 */
public class Board {

    static final Logger log = Logger.getLogger(Board.class);
    Image background;
    Random rnd;
    int rows, columns;
    int pieceSize;
    Piece[] pieceSet;
    Piece fallingPiece;
    Image[][] board;
    Image[] backgrounds = null;
    int currentBg = 0;
    Piece nextPiece;
    int nextRotation = 0;

    public Board(int rows, int columns, int pieceSize, Piece[] pieceSet) {
        this.rows = rows;
        this.columns = columns;
        this.pieceSize = pieceSize;
        this.pieceSet = pieceSet;
    }

    public void setBackgrounds(Image[] backgrounds) {
        if (backgrounds != null && backgrounds.length != 0) {
            this.backgrounds = backgrounds;
        }
    }

    public int getWidth() {
        return columns * pieceSize;
    }

    public int getHeight() {
        return rows * pieceSize;
    }

    public Random getRnd() {
        return rnd;
    }

    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }

    public Image getRandomImage() {
        if (backgrounds == null) {
            return null;
        }
        if (++currentBg >= backgrounds.length) {
            currentBg = 0;
        }
        return backgrounds[currentBg];
    }

    public void nextBackground() {
        if (backgrounds == null) {
            return;
        }
        if (++currentBg >= backgrounds.length) {
            currentBg = 0;
        }
        this.background = backgrounds[currentBg];
    }

    public void previousBackground() {
        if (backgrounds == null) {
            return;
        }
        if (--currentBg < 0) {
            currentBg = backgrounds.length - 1;
        }
        this.background = backgrounds[currentBg];
    }

    public void init() {
        if (rnd == null) {
            rnd = new Random(System.currentTimeMillis());
        }
        board = new Image[rows][columns];
        nextPiece = pieceSet[rnd.nextInt(pieceSet.length)];
        nextRotation = rnd.nextInt(nextPiece.totalRotation);
        if (backgrounds != null) {
            currentBg = rnd.nextInt(backgrounds.length);
        }
        nextBackground();
        next();
    }

    public Image[][] getBoad() {
        return board;
    }

    public void next() {
        fallingPiece = nextPiece;
        fallingPiece.currentRotations = nextRotation;
        fallingPiece.currentX = rows >> 1;
        fallingPiece.currentY = 0;
        //next
        nextPiece = pieceSet[rnd.nextInt(pieceSet.length)];
        nextRotation = rnd.nextInt(nextPiece.totalRotation);
    }

    public void drawBackground(Graphics2D g) {
        if (background != null) {
            int boardW = getWidth();
            int boardH = getHeight();
            g.drawImage(background, 0, 0, boardW, boardH, null);
        }
    }

    /** the board draw itself */
    public void drawPieces(Graphics2D g) {
        final int x = 0;
        final int y = 0;
        if (board == null) {
            return;
        }
        int tempY = y;
        for (int r = 0; r < rows; r++) {
            int tempX = x;
            for (int c = 0; c < columns; c++) {
                Image img = board[r][c];
                if (img != null) {
                    g.drawImage(img, tempX, tempY, null);
                }
                tempX += pieceSize;
            }
            tempY += pieceSize;
        }
        drawFallingPiece(g, x, y);
    }

    /**
     * draws the tetris piece at specific position
     */
    private void drawFallingPiece(Graphics2D g, int x, int y) {
        if (fallingPiece == null) {
            return;
        }
        Rotation rotation = fallingPiece.rotations[fallingPiece.currentRotations];
        int pieceC = rotation.columns;
        int pieceR = rotation.rows;
        int dataPos = 0;

        //for rows
        int tempY = y + (fallingPiece.currentY - rotation.centerY) * pieceSize;
        for (int r = 0; r < pieceR; r++) {
            //for columns
            int tempX = x + (fallingPiece.currentX - rotation.centerX) * pieceSize;
            for (int c = 0; c < pieceC; c++) {
                char flag = rotation.map.charAt(dataPos);
                if (flag == '1' && tempY >= y) {
                    g.drawImage(fallingPiece.image, tempX, tempY, null);
                }
                tempX += pieceSize;
                dataPos++;
            }
            tempY += pieceSize;
        }
//        System.out.println("falling piece:" + fallingPiece.currentX + " " + fallingPiece.currentY);
    }

    public void drawNextPiece(Graphics2D g, int x, int y) {
        if (nextPiece == null) {
            return;
        }
        Rotation rotation = nextPiece.rotations[nextRotation];
        int pieceC = rotation.columns;
        int pieceR = rotation.rows;
        int dataPos = 0;
        //for rows
        for (int r = 0; r < pieceR; r++) {
            //for columns
            int tempX = x;
            for (int c = 0; c < pieceC; c++) {
                char flag = rotation.map.charAt(dataPos);
                if (flag == '1') {
                    g.drawImage(nextPiece.image, tempX, y, null);
                }
                tempX += pieceSize;
                dataPos++;
            }
            y += pieceSize;
        }
    }

    /**
     * rotate if possible
     * @return true if rotate successfully
     */
    public boolean rotate() {
        if (fallingPiece == null) {
            return false;
        }
        int r = fallingPiece.currentRotations + 1;
        if (fallingPiece.totalRotation <= r) {
            r = 0;
        }
        Rotation rotation = fallingPiece.rotations[r];
        //is left side OK ?
        if (fallingPiece.currentX - rotation.centerX >= 0) {
            //is right side OK ?
            if (fallingPiece.currentX + (rotation.columns - rotation.centerX) <= columns) {
                //is bottom OK ?
                if (fallingPiece.currentY + (rotation.rows - rotation.centerY) <= rows) {
                    //is there placed anything ?
                    if (canExists(rotation, fallingPiece.currentX, fallingPiece.currentY)) {
                        fallingPiece.currentRotations = r;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * move left, if possible
     * @return true if move left successfully
     */
    public boolean left() {
        if (fallingPiece == null) {
            return false;
        }
        Rotation rotation = fallingPiece.rotations[fallingPiece.currentRotations];
        if (fallingPiece.currentX - rotation.centerX > 0) {
            if (canExists(rotation, fallingPiece.currentX - 1, fallingPiece.currentY)) {
                fallingPiece.currentX--;
                return true;
            }
        }
        return false;
    }

    /**
     * move right, if possible
     */
    public boolean right() {
        if (fallingPiece == null) {
            return false;
        }
        Rotation rotation = fallingPiece.rotations[fallingPiece.currentRotations];
        if (fallingPiece.currentX + (rotation.columns - rotation.centerX) < columns) {
            if (canExists(rotation, fallingPiece.currentX + 1, fallingPiece.currentY)) {
                fallingPiece.currentX++;
                return true;
            }
        }
        return false;
    }

    /**
     * move down, if possible
     */
    public boolean down() {
        if (fallingPiece == null) {
            return false;
        }
        Rotation rotation = fallingPiece.rotations[fallingPiece.currentRotations];
        if (fallingPiece.currentY + (rotation.rows - rotation.centerY) < rows) {
            if (canExists(rotation, fallingPiece.currentX, fallingPiece.currentY + 1)) {
                fallingPiece.currentY++;
                return true;
            }
        }
        return false;
    }

    public void fall() {
        while (down());
    }

    public boolean checkCanFalling() {
        if (down()) {
            fallingPiece.currentY--;
            return true;
        }
        return false;
    }

    /**
     * checks, if the moving piece can lie on the specific position - if there is not
     * another piece already
     */
    boolean canExists(Rotation aRotation, int aX, int aY) {
        if (board == null) {
            log.error("Board null");
            return false;
        }

        int xStart = (aX - aRotation.centerX);
        int x = xStart;
        int y = (aY - aRotation.centerY);
        int dataPos = 0;

        //for rows
        for (int r = 0; r < aRotation.rows; r++) {
            //for columns
            for (int c = 0; c < aRotation.columns; c++) {
                //I do not check for top boundary when rotating the piece
                //it may happend, that the positions of some part gets below
                //zero, but it does not matter and it is OK to ignore it.
                if (x >= 0 && y >= 0) {
                    char flag = aRotation.map.charAt(dataPos);
                    if (flag == '1') {
                        if (board[y][x] != null) {
                            return false;
                        }
                    }
                }
                x++;
                dataPos++;
            }
            x = xStart;
            y++;
        }
        return true;
    }

    public void merge() {
        if (fallingPiece == null) {
            return;
        }
        Rotation rotation = fallingPiece.rotations[fallingPiece.currentRotations];
        int xStart = (fallingPiece.currentX - rotation.centerX);
        int x = xStart;
        int y = (fallingPiece.currentY - rotation.centerY);
        int dataPos = 0;
        Image img = fallingPiece.image;
        //for rows
        for (int r = 0; r < rotation.rows; r++) {
            //for columns
            for (int c = 0; c < rotation.columns; c++) {
                char flag = rotation.map.charAt(dataPos);
                if (flag == '1' && y >= 0) {
                    board[y][x] = img;
                }
                x++;
                dataPos++;
            }
            x = xStart;
            y++;
        }
//        fallingPiece = null;
    }

    public void sproutUp(int numberOfRows) {
        if (pieceSet == null) {
            return;
        }
        //place some new background image
        for (int r = numberOfRows; r < rows; r++) {
            int tagetRow = r - numberOfRows;
            System.arraycopy(board[r], 0, board[tagetRow], 0, columns);
        }
        //there will be from 1 to 15 holes on the line
        for (int r = rows - numberOfRows; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                if (rnd.nextInt(2) == 0) {
                    board[r][c] = null;
                } else {
                    board[r][c] = pieceSet[rnd.nextInt(pieceSet.length)].image;
                }
            }
        }
    }
}
