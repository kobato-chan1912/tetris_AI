/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harunaga.games.tetris.client.piece;

import java.awt.Image;

/**
 *
 * @author truongnx
 */
public class Brain {

    int rows, columns;

    public Brain(Board board) {
        this.rows = board.rows;
        this.columns = board.columns;
        backup = new Image[rows][columns];
    }
    private int heighestrow;
    Image[][] backup;
    int targetX;
    int targetRotation;
//    Piece bestPiece = new Piece(0, null, null);

    private void backup(Board aboard) {
        Image[][] board = aboard.board;
//        for (int r = 0; r < rows; r++) {
//            for (int c = 0; c < columns; c++) {
//                if (board[r][c] != null) {
//                    heighestrow = r;
//                    break;
//                }
//            }
//        }
        for (int r = 0; r < rows; r++) {
            System.arraycopy(board[r], 0, backup[r], 0, columns);
        }
    }

    private void rollback(Board board) {
        for (int r = 0; r < rows; r++) {
            System.arraycopy(backup[r], 0, board.board[r], 0, columns);
        }
    }
    int bestScore;

    public void moveRationally(Board board, boolean preferredDrop) {
        bestScore = 0;
        backup(board);
        Piece fallingpiece = board.fallingPiece;
        int curRot = fallingpiece.currentRotations, curX = fallingpiece.currentX, curY = fallingpiece.currentY;
        do {
            board.fall();
            board.merge();
            rate(board);
            rollback(board);
            fallingpiece.currentY = curY;
            while (board.left()) {
                board.fall();
                board.merge();
                rate(board);
                rollback(board);
                fallingpiece.currentY = curY;
            }
            fallingpiece.currentX = curX;
            while (board.right()) {
                board.fall();
                board.merge();
                rate(board);
                rollback(board);
                fallingpiece.currentY = curY;
            }
            fallingpiece.currentX = curX;
        } while (board.rotate() && curRot != fallingpiece.currentRotations);
        fallingpiece.currentRotations = curRot;
        if (curRot != targetRotation) {
            board.rotate();
        }
        if (targetX < curX) {
            board.left();
        } else if (targetX > curX) {
            board.right();
        } else if (preferredDrop) {
            board.fall();
        }
    }

    private void rate(Board board) {
        int sum = 0;
        sum += ratePosition(board.board);
        sum -= countHoles(board.board) * 10000;
        if (sum > bestScore) {
            bestScore = sum;
            targetRotation = board.fallingPiece.currentRotations;
            targetX = board.fallingPiece.currentX;
        }
    }

    private int countHoles(Image board[][]) {
        int holes = 0;
        for (int c = 0; c < columns; c++) {
            boolean swap = false;
            for (int r = 0; r < rows; r++) {
                if (board[r][c] != null) {
                    swap = true;
                } else {
                    if (swap) {
                        holes++;
                    }
                    swap = false;
                }
            }
        }
//        System.out.println("Holes: " + holes);
        return holes;
    }

    private int ratePosition(Image board[][]) {
        int rate = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                if (board[r][c] != null) {
                    rate += r * r * r;
                }
            }
        }
        return rate;
    }
}
