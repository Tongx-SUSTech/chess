package controller;

import view.Chessboard;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameController {
    private Chessboard chessboard;

    // private List<String> chessHistory = new ArrayList<>();

    public GameController(Chessboard chessboard) {
        this.chessboard = chessboard;
    }

    public List<String> loadGameFromFile() {
        List<String> chessData = new ArrayList<>();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("*.txt", "txt");
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(filter);
        fc.setMultiSelectionEnabled(false);
        fc.setDialogTitle("Open File");
        // 这里显示打开文件的对话框
        int flag = 0;
        try {
            flag = fc.showOpenDialog(null);
        } catch (HeadlessException head) {
            System.out.println("Open File Dialog ERROR!");
        }

        // 如果按下确定按钮，则获得该文件。
        if (flag == JFileChooser.APPROVE_OPTION) {
            // 获得该文件
            File f = fc.getSelectedFile();
            if (!f.getPath().endsWith(".txt")) {
                JOptionPane.showMessageDialog(null, "文件格式错误\n" +
                        "错误编码： 104", "alert", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            InputStreamReader read = null;// 考虑到编码格式
            try {
                read = new InputStreamReader(
                        new FileInputStream(f), "GBK");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            BufferedReader bufferedReader = new BufferedReader(read);

            String lineTxt;

            while (true) {
                try {
                    if ((lineTxt = bufferedReader.readLine()) == null)
                        break;
                    chessData.add(lineTxt);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (checkChessData(chessData)) {
                chessboard.loadGame(chessData);
                chessboard.setGameOver(false);
                return chessData;
            }
        }
        return null;
    }

    private boolean checkChessData(List<String> chessDatas) {
        for (String str : chessDatas) {
            String[] strings = str.split("/");
            List<String> chessData = new ArrayList<>(Arrays.asList(strings));

            if (chessData.size() == 8) {
                JOptionPane.showMessageDialog(null, "缺少行棋方\n" +
                        "错误编码： 103", "alert", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            if (chessData.size() != 9) {
                JOptionPane.showMessageDialog(null, "数据错误\n" +
                        "棋盘数据长度错误", "alert", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            if (!(chessData.get(8).equals("w") || chessData.get(8).equals("b"))) {
                JOptionPane.showMessageDialog(null, "行棋方数据错误\n" +
                        "错误编码： 103", "alert", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            for (int i = 0; i < 8; i++) {
                if (chessData.get(i).length() != 8) {
                    JOptionPane.showMessageDialog(null, "棋盘并非8*8\n" +
                            "错误编码： 101", "alert", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                for (int j = 0; j < 8; j++) {
                    String str1 = String.valueOf(chessData.get(i).charAt(j));
                    String str2 = "rnbkqp_RNBKQP";
                    if (!str2.contains(str1)) {
                        JOptionPane.showMessageDialog(null, "棋子并非六种之一，棋子并非黑白棋子\n" +
                                "错误编码： 102", "alert", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void resetGame() {
        chessboard.clear();
        chessboard.clearHistory();
        chessboard.addHistory();
        chessboard.setGameOver(false);
    }

    public void saveGameToFile() {
        List<String> historyList = chessboard.getChessHistory();
        StringBuilder buf = new StringBuilder();
        for (String str : historyList) {
            buf.append(str);
            buf.append("\n");
        }

        FileNameExtensionFilter filter = new FileNameExtensionFilter("*.txt", "txt");
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(filter);
        fc.setMultiSelectionEnabled(false);
        int result = fc.showSaveDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            if (!file.getPath().endsWith(".txt")) {
                file = new File(file.getPath() + ".txt");
            }
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                fos.write(buf.toString().getBytes());
                fos.flush();
            } catch (IOException e) {
                System.err.println("文件创建失败：");
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void withdraw() {
        if (!chessboard.getGameOver()) {
            List<String> chessHistory = chessboard.getChessHistory();
            if (chessHistory.size() > 1) {
                chessboard.removeHistory();
                chessboard.loadGame(chessHistory);
            } else {
                JOptionPane.showMessageDialog(null, "无棋可悔", "alert", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    public void playback() {
        List<String> chessDatas = loadGameFromFile();
        // chessboard.setClickControllerPlayback();
        System.out.println(chessDatas);

    }

}
