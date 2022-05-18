package model;

import controller.ClickController;
import view.ChessboardPoint;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 这个类表示国际象棋里面的车
 */
public class RookChessComponent extends ChessComponent {
    /**
     * 黑车和白车的图片，static使得其可以被所有车对象共享
     * <br>
     * FIXME: 需要特别注意此处加载的图片是没有背景底色的！！！
     */
    private static Image ROOK_WHITE;
    private static Image ROOK_BLACK;

    /**
     * 车棋子对象自身的图片，是上面两种中的一种
     */
    private Image rookImage;

    /**
     * 读取加载车棋子的图片
     *
     * @throws IOException
     */
    public void loadResource() throws IOException {
        if (ROOK_WHITE == null) {
            ROOK_WHITE = ImageIO.read(new File("./images/rook-white.png"));
        }

        if (ROOK_BLACK == null) {
            ROOK_BLACK = ImageIO.read(new File("./images/rook-black.png"));
        }
    }


    /**
     * 在构造棋子对象的时候，调用此方法以根据颜色确定rookImage的图片是哪一种
     *
     * @param color 棋子颜色
     */

    private void initiateRookImage(ChessColor color) {
        try {
            loadResource();
            if (color == ChessColor.WHITE) {
                rookImage = ROOK_WHITE;
            } else if (color == ChessColor.BLACK) {
                rookImage = ROOK_BLACK;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public RookChessComponent(ChessboardPoint chessboardPoint, Point location, ChessColor color, ClickController listener, int size) {
        super(chessboardPoint, location, color, listener, size);
        initiateRookImage(color);
    }

    /**
     * 车棋子的移动规则
     *
     * @param chessComponents 棋盘
     * @param destination     目标位置，如(0, 0), (0, 7)等等
     * @return 车棋子移动的合法性
     */

    @Override
    public boolean canMoveTo(ChessComponent[][] chessComponents, ChessboardPoint destination) {
        List<ChessboardPoint> ChessboardPointsList = canMoveTo(chessComponents);
        ChessboardPointsList.sort(Comparator.comparing(ChessboardPoint::getX).thenComparing(ChessboardPoint::getY));
        for (ChessboardPoint chessboardPoint : ChessboardPointsList) {
            if (chessboardPoint.toString().equals(destination.toString()))
                return true;
        }
        return false;
    }

    @Override
    public List<ChessboardPoint> canMoveTo(ChessComponent[][] chessComponents) {
        List<ChessboardPoint> ChessboardPointsList = new ArrayList<>();
        ChessboardPoint source = getChessboardPoint();
        // ChessColor chessColor = getChessColor();
        int row = source.getX();
        int col = source.getY();
        int[][] dxdyArray = { { 0, 1 }, { 0, -1 }, { 1, 0 }, { -1, 0 } };
        for (int[] dxdy : dxdyArray) {
            int dx = dxdy[0], dy = dxdy[1];
            for (int i = dx, j = dy; row + i >= 0 && col + j >= 0 && row + i < 8
                    && col + j < 8; i = i + dx, j = j + dy) {
                if (source.offset(i, j) != null) {
                    if (chessComponents[row + i][col + j] instanceof EmptySlotComponent) {
                        ChessboardPointsList.add(source.offset(i, j));
                    } else {
                        if (chessComponents[row + i][col + j].getChessColor() == getChessColor())
                            break;
                        if (chessComponents[row + i][col + j].getChessColor() != getChessColor()) {
                            ChessboardPointsList.add(source.offset(i, j));
                            break;
                        }
                    }
                }
            }
        }

        ChessboardPointsList.sort(Comparator.comparing(ChessboardPoint::getX).thenComparing(ChessboardPoint::getY));
        return ChessboardPointsList;
    }

    /**
     * 注意这个方法，每当窗体受到了形状的变化，或者是通知要进行绘图的时候，就会调用这个方法进行画图。
     *
     * @param g 可以类比于画笔
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
//        g.drawImage(rookImage, 0, 0, getWidth() - 13, getHeight() - 20, this);
        g.drawImage(rookImage, 0, 0, getWidth(), getHeight(), this);
        g.setColor(Color.BLACK);
        if (isSelected()) { // Highlights the model if selected.
            g.setColor(Color.RED);
            g.drawOval(0, 0, getWidth(), getHeight());
        }
    }

    @Override
    public String toString() {
        return chessColor == ChessColor.WHITE ? "r" : "R";
    }
}
