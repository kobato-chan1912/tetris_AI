///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.harunaga.games.common.client.miscellaneous;
//
//import java.awt.Graphics;
//import java.awt.Rectangle;
//import javax.swing.JButton;
//import javax.swing.JFrame;
//
///**
// *
// * @author MaiHoang146ThaiHa
// */
//public class test extends JButton {
//
//    @Override
//    public void paint(Graphics g) {
//        System.out.println("paint....");
//        super.paint(g);
//    }
//
//    @Override
//    public void paintAll(Graphics g) {
//        System.out.println("paintAll");
//        super.paintAll(g);
//    }
//
//    @Override
//    protected void paintBorder(Graphics g) {
//        System.out.println("paint border");
//        super.paintBorder(g);
//    }
//
//    @Override
//    protected void paintChildren(Graphics g) {
//        System.out.println("paint children");
//        super.paintChildren(g);
//    }
//
//    @Override
//    protected void paintComponent(Graphics g) {
//        System.out.println("paint component");
//        super.paintComponent(g);
//
//    }
//
//    @Override
//    public void paintComponents(Graphics g) {
//        System.out.println("paint components");
//        super.paintComponents(g);
//    }
//
//    @Override
//    public void paintImmediately(Rectangle r) {
//        System.out.println("paint immediately rectangle");
//        super.paintImmediately(r);
//    }
//
//    @Override
//    public void paintImmediately(int x, int y, int w, int h) {
//        System.out.println("paint immediately co-ordinate");
//        super.paintImmediately(x, y, w, h);
//    }
//
//    public static void main(String[] args) {
//        JFrame frame = new JFrame("test button paint");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.getContentPane().add(new test("hi hi hi"));
//        frame.pack();
//        frame.setVisible(true);
//    }
//
//    public test(String text) {
//        super(text);
//    }
//}
