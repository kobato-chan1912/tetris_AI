package com.harunaga.games.tetris.client.states;

import com.harunaga.games.common.GameEvent;
import com.harunaga.games.common.client.font.Font;
import com.harunaga.games.common.client.input.InputManager;
import com.harunaga.games.common.client.effect.BarEffect;
import com.harunaga.games.common.client.effect.Effect;
import com.harunaga.games.common.client.effect.FadeEffect;
import com.harunaga.games.common.client.effect.FallingEffect;
import com.harunaga.games.common.client.miscellaneous.SizeFixedButton;
import com.harunaga.games.tetris.client.form.ComponentProvider;
import com.harunaga.games.common.client.net.NetManager;
import com.harunaga.games.common.client.resource.ResourceManager;
import com.harunaga.games.tetris.client.form.ChatCanvas;
import com.harunaga.games.tetris.TetrisGameConfig;
import com.harunaga.games.tetris.client.piece.Board;
import com.harunaga.games.tetris.client.TetrisResourceManager;
import com.harunaga.games.common.client.state.GameStateManager;
import com.harunaga.games.tetris.TetrisEvent;
import com.harunaga.games.tetris.TetrisGamePlayer;
import com.harunaga.games.tetris.client.form.ChatInput;
import com.harunaga.games.tetris.client.form.LoginBox;
import com.harunaga.games.tetris.client.piece.Brain;
import com.harunaga.games.tetris.client.piece.Piece;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.StringTokenizer;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class PlayingGameState extends TetrisGameState implements ActionListener {

    String remainTime;
    private static final Object lock = new Object();
    private final long NEXT_PIECE_TIME = 500;
    Board board;
    private boolean falling = false;
    private boolean gameOver = false;
    private int score;
    private int speed = 5;
    private int planeBoardX = 155;
    private int planeBoardY = 130;
    private int logoX = 600;
    private int logoY = 200;
    private long time = 0;
    private int sproutRows;
    private JPanel playButtonSpace;
    private SizeFixedButton pauseButton;
    private SizeFixedButton playButton;
    private SizeFixedButton saveBt;
    private SizeFixedButton backBt;
    private SizeFixedButton restartBt;
    private LoginBox loginbox;
    JPanel leftPane;
    private boolean scoreCalculating = false;
    private Color[] pColors = TetrisGameConfig.PLAYER_COLORS;
    public Image tetrisLogo;
    boolean notgaming;
    Effect barEffect;
    Effect strEffect;
    String drawStr;
//    Image tempImage;
    ChatInput input = ChatInput.instance;
    ChatCanvas chat;
//-------------------Pause----------
    InputManager inputManager;
    String pauseStr;
    int unpauseDisplayCount;
    int pauserColor;
    String[] topScore;
    String[] topPlayer;
    TetrisGameConfig config;
    int soundTime;
    JCheckBox autoPlay;
    JCheckBox allowShift;
    Brain brain;

    public PlayingGameState(TetrisGameConfig config) {
        this.config = config;
    }

    public String getName() {
        return STATE_PLAYING;
    }

    @Override
    public void loadResources(ResourceManager resourceManager) {
        resource = resourceManager;
        bg = resource.getImage(TetrisGameConfig.GAME_PLAYING_BG);
        TetrisResourceManager res = (TetrisResourceManager) resource;
        board = new Board(TetrisGameConfig.BOARD_ROWS, TetrisGameConfig.BOARD_COLUMNS,
                TetrisGameConfig.PIECE_SIZE, res.getPieceSet("tetris.xml"));
        board.setBackgrounds(res.getBoardImages());
        brain = new Brain(board);
    }

    /**
     * 
     * @param resourceManager
     */
    public void createGUI(ResourceManager resourceManager) {
        if (pauseButton == null) {
            ComponentProvider btProvider = new ComponentProvider(resourceManager);
            pauseButton = btProvider.createTranslucentButton("pause", this);
            playButton = btProvider.createTranslucentButton("continuous", this);
            restartBt = btProvider.createOvalButton("restart", TetrisGameConfig.SMALL_ON_FOCUS_IMG,
                    "restart", this, Cursor.HAND_CURSOR);
            backBt = btProvider.createOvalButton("back", TetrisGameConfig.SMALL_ON_FOCUS_IMG,
                    "return to menu", this, Cursor.HAND_CURSOR);

            saveBt = btProvider.createOvalButton("save", TetrisGameConfig.SMALL_ON_FOCUS_IMG,
                    "send score to server", this, Cursor.HAND_CURSOR);
            playButtonSpace = new JPanel();
            playButtonSpace.setOpaque(false);
            playButtonSpace.add(pauseButton);
            autoPlay = btProvider.getDefaultCheckbox("autoPlay", this, false);
            autoPlay.setOpaque(false);
            allowShift = btProvider.getDefaultCheckbox("allow shift", this, true);
            allowShift.setOpaque(false);
            autoPlay.setForeground(Color.YELLOW);
        }
        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new BorderLayout());
        JPanel menu = new JPanel(new FlowLayout(FlowLayout.RIGHT, 1, 1));
        menu.setOpaque(false);
        menu.add(playButtonSpace);
        leftPane = new JPanel();
        leftPane.setOpaque(false);
        leftPane.setLayout(new BoxLayout(leftPane, BoxLayout.Y_AXIS));
        ResultPlane result = new ResultPlane(resourceManager.getImage("resultBg.jpg"),
                resourceManager.getImage("skull-crossbones.png"), resourceManager.getImage("heart.png"),
                net.isInMultiplayerGame());
        result.setAlignmentX(ResultPlane.CENTER_ALIGNMENT);
        leftPane.add(result);
        //net.setInMultiplayerGame(true);
        if (net.isInMultiplayerGame()) {
            chat = new ChatCanvas();
            leftPane.add(chat);
        } else {
            if (net.isLogedin()) {
                leftPane.setVisible(true);
                tetrisLogo = null;
                net.send(new TetrisEvent(TetrisEvent.C_SAVE_SCORE, "1"));
            } else {
                tetrisLogo = resourceManager.getImage("logo.png");
                leftPane.setVisible(false);
            }
            topScore = new String[TetrisGameConfig.PLAYER_LIMIT];
            topPlayer = new String[TetrisGameConfig.PLAYER_LIMIT];
            menu.add(restartBt);
        }
        contentPane.add(leftPane, BorderLayout.EAST);
        menu.add(backBt);
        menu.add(saveBt);
        Container playControlPane = new Container();
        playControlPane.setLayout(new BoxLayout(playControlPane, BoxLayout.Y_AXIS));
        playControlPane.add(autoPlay);
        playControlPane.add(allowShift);
        menu.add(playControlPane);
        contentPane.add(menu, BorderLayout.NORTH);
        loginbox = new LoginBox(this, new ImageIcon(resourceManager.getImage("buttons/button.png")),
                new ImageIcon(resourceManager.getImage("buttons/button_rollover.png")));
        loginbox.setLocation((frame.getWidth() - loginbox.getWidth()) >> 1, frame.getHeight() >> 1);
        loginbox.setVisible(false);
        frame.getLayeredPane().add(loginbox, JLayeredPane.POPUP_LAYER);
        frame.getLayeredPane().add(input, JLayeredPane.MODAL_LAYER);
        input.setListener(this);
        input.setVisible(false);
        input.setLocation((frame.getWidth() - input.getWidth()) >> 1, frame.getHeight() >> 1);
        // frame.validate();
    }

    @Override
    public void start(InputManager inputManager, NetManager netManager) {
        super.start(inputManager, netManager);
        saveBt.setEnabled(true);
        this.inputManager = inputManager;
        score = 0;
        speed = 1;
        scoreCalculating = false;
        paused = gameOver = false;
        board.init();
        sproutRows = 0;
        falling = true;
        notgaming = false;
        remainTime = null;
        config.startSoundAndMusic();
        config.music(TetrisGameConfig.MUSIC_PLAYING, false);
        //------------test
    }

    @Override
    public void stop() {
        frame.getLayeredPane().remove(loginbox);
        config.stopSoundAndMusic();
        leftPane = null;
        super.stop();
        tetrisLogo = null;
        strEffect = null;
        barEffect = null;
    }

    public void update(long elapsedTime) {
        checkSystemInput();
        if (scoreCalculating || paused) {
            return;
        } else if (notgaming) {
            notgaming = gameOver;
            notgaming = barEffect.isProcessing(elapsedTime) || notgaming;
            notgaming = strEffect.isProcessing(elapsedTime) || notgaming;
            if (!notgaming) {
                barEffect = null;
                strEffect = null;
            }
            return;
        } else if (!falling) {
            board.next();
            falling = board.checkCanFalling();
            if (!falling) {
                gameOver("GAME OVER");
                if (net.isInMultiplayerGame()) {
                    net.send(new TetrisEvent(TetrisEvent.C_GAME_OVER));
                }
                config.sound(TetrisGameConfig.SOUND_GAME_OVER);
                return;
            }
            time = 0;
        }
        checkInput();
        time += elapsedTime;
        if (time >= NEXT_PIECE_TIME - (80 * speed)) {
//            System.out.println(NEXT_PIECE_TIME - (70 * speed));
            falling = board.down();
            time = 0;
        }
        if (!falling) {
            board.merge();
            /** do some calculate score */
            scoreCalculating = true;
            new ScoreChecker().start();
        }
    }

    private void gameOver(String str) {
        drawStr = str;
        int bw = board.getWidth();
        int bh = board.getHeight();
        Font f = Font.getFont(TetrisGameConfig.FONT_MENU_60);
        BarEffect fadeEffect = new BarEffect(6, planeBoardX, planeBoardY, bw, bh);
        fadeEffect.setVelocity(0.04f);
        FallingEffect fallingEffect = new FallingEffect(planeBoardY,
                bh - 50 - f.getHeight(), planeBoardX + ((bw - f.stringWidth(drawStr)) >> 1));
        fallingEffect.setFont(f);
        strEffect = fallingEffect;
        barEffect = fadeEffect;
//        tempImage = board.getRandomImage();
        gameOver = true;
        notgaming = true;
    }

    private void checkInput() {
        if (soundTime < 4) {
            soundTime++;
        }
        if (autoPlay.isSelected()) {
            brain.moveRationally(board, allowShift.isSelected());
        } else {
            if (TetrisGameConfig.ROTATE.isPressed()) {
                board.rotate();
                playSound(TetrisGameConfig.SOUND_ROTATE);
            }
            if (TetrisGameConfig.MOVE_LEFT.isPressed()) {
                board.left();
                playSound(TetrisGameConfig.SOUND_MOVE);
            }
            if (TetrisGameConfig.MOVE_RIGHT.isPressed()) {
                board.right();
                playSound(TetrisGameConfig.SOUND_MOVE);
            }
            if (TetrisGameConfig.MOVE_DOWN.isPressed()) {
                board.down();
                playSound(TetrisGameConfig.SOUND_MOVE);
            }
            if (TetrisGameConfig.SHIFT.isPressed()) {
                board.fall();
                config.sound(TetrisGameConfig.SOUND_FALL);
                falling = false;
            }
        }
    }

    private void playSound(int sound) {
        //to avoid play sound to much, here we just play one time every 2 loop
        if (soundTime > 3) {
            config.sound(sound);
            soundTime = 0;
        }
    }

    private void checkSystemInput() {
        if (TetrisGameConfig.EXIT.isPressed()) {
            if (JOptionPane.showConfirmDialog(frame, "Are you sure ?", "Quit",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                nextState = GameStateManager.EXIT_GAME;
                if (net.isInMultiplayerGame()) {
                    net.send(new TetrisEvent(TetrisEvent.C_QUIT_GAME));
                }
                nextState = STATE_GAME_MENU;
            }
            return;
        }
        if (TetrisGameConfig.PAUSE.isPressed()) {
            if (net.isInMultiplayerGame()) {
                net.send(new TetrisEvent(TetrisEvent.C_REQUEST_PAUSE, (!paused) + ""));
            } else {
                setPaused(!paused);
            }
            /** do some stuff here to PAUSE the game */
            return;
        }
        if (TetrisGameConfig.CHAT.isPressed()) {
            if (net.isInMultiplayerGame()) {
                input.setVisible(true);
            }
        }

    }

    public void setPaused(boolean aFlaf) {
        paused = aFlaf;
        playButtonSpace.removeAll();
        if (paused) {
            playButtonSpace.add(playButton);
        } else {
            playButtonSpace.add(pauseButton);
            inputManager.resetAllGameActions();
        }
    }

    public void sproutUp() {
        synchronized (lock) {
            sproutRows++;
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (nextState != null) {
            return;
        }
        Object src = e.getSource();
        if (src == playButton || src == pauseButton) {
            TetrisGameConfig.PAUSE.tap();
        } else if (src == saveBt) {
            if (net.isLogedin()) {
                saveScore();
            } else {
                paused = true;
                loginbox.setVisible(true);
            }
        } else if (src == backBt) {
            TetrisGameConfig.EXIT.tap();
        } else if (src == restartBt) {
            restart();
        } else if (src == input) {
            input.setVisible(false);
            frame.requestFocus();
            String text = e.getActionCommand();
            if (text != null && !text.isEmpty()) {
                chat.putMessage("me: " + text);
                input.clear();
                net.send(new TetrisEvent(TetrisEvent.C_CHAT_MSG, text));
            }
            log.info("send chat-event: " + text);
        } else if (src == loginbox) {
            final LoginBox login = loginbox;
            login.setVisible(false);
            if (LoginBox.LOG_IN.equals(e.getActionCommand())) {
                new Thread() {

                    @Override
                    public void run() {
                        if (net == null) {
                            log.error("Null net manager");
                        } else {
                            displayMessage("connectting to server. please be patient", -1);
                            net.shutdown();
                            String ip = login.getIP();
                            if (net.init(ip)) {
                                net.setPassword(login.getPassword());
                                net.setUsername(login.getUsername());
                                net.start();
                                displayMessage("logging in. please be patient", -1);
                                timer.schedule(new TimerTask() {

                                    @Override
                                    public void run() {
                                        if (paused) {
                                            paused = false;
                                            displayMessage("log in fail. reason : out of time",
                                                    TIME_DEFAULT_FOR_DISPLAY_MESSAGE);
                                        }
                                    }
                                }, TIME_FOR_WAIT_LOGIN_ACK);
                                net.login();

                            } else {
                                displayMessage("Cannot connect to server " + ip,
                                        TIME_DEFAULT_FOR_DISPLAY_MESSAGE);
                                paused = false;
                            }
                        }
                    }
                }.start();
            } else {
                paused = false;
            }
            frame.requestFocus();
        } else {
            frame.requestFocus();
            log.warn("wrong provider for the waiting input");
        }
    }

    private void restart() {
        paused = true;
        if (JOptionPane.showConfirmDialog(frame, "Are you sure ?", "Restart",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
            int bw = board.getWidth();
            int bh = board.getHeight();
            BarEffect effect = new BarEffect(6, planeBoardX, planeBoardY, bw, bh);
            effect.setRevert(true);
            effect.setVertival(false);
            effect.setVelocity(0.02f);
            Font font = Font.getFont(TetrisGameConfig.FONT_MENU_60);
            drawStr = "START";
            FadeEffect fadeEffect = new FadeEffect(1500,
                    planeBoardX + ((bw - font.stringWidth(drawStr)) >> 1), planeBoardY + (bh >> 2));
            fadeEffect.setFont(font);
            barEffect = effect;
            strEffect = fadeEffect;
            notgaming = true;
            gameOver = false;
            score = 0;
            speed = 1;
            sproutRows = 0;
            falling = true;
            board.init();
        }
        paused = false;
        config.music(TetrisGameConfig.MUSIC_PLAYING, true);
    }

    @Override
    public void processEvent(GameEvent e) {
        switch (e.getType()) {
            case TetrisEvent.SB_CLIENT_SCORE:
                updatePlayerScore(e);
                return;
            case TetrisEvent.S_LOGIN_ACK_OK:
                processLoginACK(true, null);
                return;
            case TetrisEvent.S_LOGIN_ACK_FAIL:
                processLoginACK(false, e.getMessage());
                return;
            case TetrisEvent.S_SAVE_SCORE_ACK_OK:
                processSaveACK(true);
                if (!net.isInMultiplayerGame()) {
                    getTopScore(e.getMessage());
                }
                return;
            case TetrisEvent.S_SAVE_SCORE_ACK_FAIL:
                processSaveACK(false);
                return;
            case TetrisEvent.SB_SPROUT_UP:
                sproutUp();
                return;
            case TetrisEvent.SB_TIME_UP:
                gameOver("TIME UP");
                return;
            case TetrisEvent.SB_PLAYER_QUIT:
                TetrisGamePlayer p = net.getPlayer(e.getMessage());
                if (p != null) {
                    p.alive = false;
                } else {
                    log.info("get null player...id=" + e.getMessage());
                }
                return;
            case TetrisEvent.SB_PAUSED:
                processPause(e);
                return;
            case TetrisEvent.SB_TIME:
                remainTime = e.getMessage();
                return;
            case TetrisEvent.SB_RESULT:
                net.removeAllPlayers();
                if (nextState == null) {
                    net.addPlayers(e.getMessage());
                    net.setWinnerId(((TetrisEvent) e).getUsername());
                    nextState = STATE_RESULT;
                }
                return;
            case TetrisEvent.SB_CHAT_MSG:
                String pid = e.getPlayerId();
                TetrisGamePlayer player = net.getPlayer(pid);
                if (player != null) {
                    chat.putMessage(player.name + " say : " + e.getMessage());
                } else {
                    log.warn("Null player");
                }
                return;
            case TetrisEvent.S_DISCONNECT:
                net.setLogin(false);
                if (net.isInMultiplayerGame()) {
                    nextState = STATE_GAME_MENU;
                }
//                else {
//                    displayMessage("server error", 100);
//                }
        }
    }

    private void processPause(GameEvent e) {
        TetrisGamePlayer p = net.getPlayer(((TetrisEvent) e).getUsername());
        if (p != null) {
            boolean flag = Boolean.parseBoolean(e.getMessage());
            System.out.println("flag=" + flag);
            System.out.println("Player: " + p.id + "    me: " + net.getMe().id);
            if (flag) {
                TetrisGamePlayer me = net.getMe();
                if (me != p) {
                    pauserColor = p.color;
                    pauseStr = "Chờ chút chờ chút";
                } else {
                    pauserColor = me.color;
                    pauseStr = "Paused";
                }
            } else {
                unpauseDisplayCount = 30;
                if (pauserColor == p.color) {
                    pauseStr = "ok. chiến tiếp nầu";
                } else if (!net.isMe(p)) {
                    pauseStr = "Không chờ";
                    pauserColor = p.color;
                } else {
                    pauseStr = null;
                }
            }
            setPaused(flag);
        } else {
            log.info("get null player...id=" + e.getMessage());
        }
    }

    private void updatePlayerScore(GameEvent e) {
        int pscore = 0;
        try {
            pscore = Integer.parseInt(e.getMessage());
        } catch (Exception exc) {
            log.warn("wrong score was received", exc);
        }
        TetrisGamePlayer p = net.getPlayer(((TetrisEvent) e).getUsername());
        if (pscore > 0 && p != null) {
            p.score = pscore;
        } else {
            log.warn("get score up date for null player");
        }
    }

    private void processSaveACK(boolean success) {
        timer.cancel();
        saveBt.setEnabled(true);
        if (success) {
            displayMessage("save score ok", 5000);
        } else {
            displayMessage("save score fail", 5000);
        }
    }

    private void getTopScore(String data) {
        if (data != null) {
            int i = 0;
            StringTokenizer st = new StringTokenizer(data, "" + TetrisGameConfig.TOPSCORE_DELIMITER);
            while (st.hasMoreTokens()) {
                StringTokenizer top = new StringTokenizer(st.nextToken(), "" + TetrisGameConfig.TOPSCORE_ATTRIBUTE_DELIMITER);
                if (top.countTokens() >= 2) {
                    topPlayer[i] = top.nextToken();
                    topScore[i] = top.nextToken();
                    i++;
                }
            }
            for (int j = topScore.length - 1; j >= i; j--) {
                topScore[j] = null;
            }
            tetrisLogo = null;
            leftPane.setVisible(true);
        }
    }

    private void processLoginACK(boolean success, String reason) {
        timer.cancel();
        paused = false;
        if (success) {
            net.setLogin(true);
            displayMessage("log in ok. sending your score", 5000);
            saveScore();
        } else {
            displayMessage("log in fail. reason : " + reason, 5000);
        }
        return;
    }

    private void saveScore() {
        saveBt.setEnabled(false);
        TetrisEvent event = new TetrisEvent(TetrisEvent.C_SAVE_SCORE, "" + score);
        event.setUsername(net.getUsername());
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                displayMessage("save score fail. reason : out of time",
                        TIME_DEFAULT_FOR_DISPLAY_MESSAGE);
                saveBt.setEnabled(true);
            }
        }, TIME_FOR_WAIT_LOGIN_ACK);
        net.send(event);
    }

    @Override
    public void draw(Graphics2D g) {
        if (bg != null) {
            g.drawImage(bg, 0, 0, null);
        }
        g.setColor(Color.BLUE);
        Font number = Font.getFont(TetrisGameConfig.FONT_NUMBER_20);
        //draw next piece
        defaultFont.drawString("NEXT: ", 3, planeBoardY, g);
        int boardHeight = board.getHeight();
        board.drawNextPiece(g, planeBoardX >> 2, planeBoardY + defaultFont.getHeight() + 3);
        //draw score board
        int y = planeBoardY + (boardHeight >> 2);
        defaultFont.drawString("SCORE: ", 3, y, g);
        number.drawString("" + score, 3, y + defaultFont.getHeight() + 3, g, 5, ' ');
        //draw speed board
        y = planeBoardY + (boardHeight >> 1);
        defaultFont.drawString("SPEED: ", 3, y, g);
        number.drawString("" + speed, 3, y + defaultFont.getHeight() + 3, g, 5, ' ');
        //draw tetris board
        g.translate(planeBoardX, planeBoardY);
        board.drawBackground(g);
        synchronized (lock) {
            board.drawPieces(g);
        }
        g.translate(-planeBoardX, -planeBoardY);
        if (tetrisLogo != null) {
            g.drawImage(tetrisLogo, logoX, logoY, null);
        }
        if (notgaming) {
            barEffect.draw(g);
//            barEffect.draw(tempImage, g, planeBoardX, planeBoardY);
//            if (strEffect != null) {
            g.setColor(Color.RED);
            strEffect.draw(drawStr, g);
            // }
//            g.setClip(0, 0, frame.getWidth(), frame.getHeight());
        } else if (pauseStr != null) {
            if (paused) {
                g.setColor(pColors[pauserColor]);
                defaultFont.drawString(pauseStr, 300, 500, g);
            } else {
                if (unpauseDisplayCount <= 0) {
                    pauseStr = null;
                } else {
                    g.setColor(pColors[pauserColor]);
                    defaultFont.drawString(pauseStr, 50, 50, g);
                    unpauseDisplayCount--;
                }
            }
        }
        frame.getLayeredPane().paintComponents(g);
        if (remainTime != null) {
            number.drawString(remainTime, 10, 50, g);
        }
    }

    class ScoreChecker extends Thread {

        ScoreChecker() {
            super("ScoreChecker" + score);
            setDaemon(true);
        }

        @Override
        public void run() {
            int award = 1;    //give player 1 point when he/she finish a piece
            int lines = 0;
            Image[][] tempBoard = board.getBoad();
            int rows = board.getRows();
            int columns = board.getColumns();
            int line = rows - 1;
            do {
                lines++;
                for (int r = line; r >= 0; r--) {
                    line--;
                    boolean all = true;
                    for (int c = 0; c < columns; c++) {
                        if (tempBoard[r][c] == null) {
                            all = false;
                            break;
                        }
                    }
                    if (all) {
                        line = r;
                        break;
                    }
                }
                if (line >= 0) {
                    config.sound(config.SOUND_GETSCORE);
                    //deleting the whole line from right to the left
                    for (int c = columns - 1; c >= 0; c--) {
                        synchronized (lock) {
                            tempBoard[line][c] = null;
                        }
                        try {
                            Thread.sleep(18 - (lines << 1));
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                    //give player score as award
                    award += ((speed >> 1) + lines) * columns;
                } else {
                    break;
                }
                //shift the rest downward
                synchronized (lock) {
                    for (int r = line; r >= 1; r--) {
                        System.arraycopy(tempBoard[r - 1], 0, tempBoard[r], 0, columns);
                    }
                    for (int c = columns - 1; c >= 0; c--) {
                        tempBoard[0][c] = null;
                    }
                }
            } while (true);
            if (sproutRows > 0) {
                synchronized (lock) {
                    board.sproutUp(sproutRows);
                    sproutRows = 0;
                }
            }
            scoreCalculating = false;
            addScore(award);
        }

        private void addScore(int amount) {
            int speedScoreBreakpoints[] = TetrisGameConfig.SCORE_BREAKPOINTS;
            int oldScore = score;
            score += amount;
            //if there is change in 1000 than change speed
            for (int i = 0; i < speedScoreBreakpoints.length; i++) {
                if (oldScore < speedScoreBreakpoints[i] && score >= speedScoreBreakpoints[i]) {
                    speed++;
                }
            }
            //just send the score incase player earn more than 1 score and is in competiton
            if (amount > 1 && net.isInMultiplayerGame()) {
                sendScore(score);
            }
        }

        private void sendScore(int score) {
            net.send(new TetrisEvent(TetrisEvent.C_SEND_SCORE, "" + score));
        }
    }

    class ResultPlane extends JLabel {

        Image deathImg;
        Image aliveImg;
        Color colors[] = TetrisGameConfig.PLAYER_COLORS;
        boolean drawPlayer;

        ResultPlane(Image background, Image deathImage, Image aliveImage, boolean drawPlayer) {
            super(new ImageIcon(background));
            deathImg = deathImage;
            aliveImg = aliveImage;
            setIgnoreRepaint(true);
            this.drawPlayer = drawPlayer;

//            Border empty = BorderFactory.createEmptyBorder();
//            TitledBorder title = BorderFactory.createTitledBorder(
//                    empty, "title");
//            title.setTitlePosition(TitledBorder.TOP);
//            title.setTitleJustification(TitledBorder.DEFAULT_JUSTIFICATION);
//            setBorder(title);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (drawPlayer) {
                drawPlayer((Graphics2D) g, TetrisGameConfig.resultPaneX, TetrisGameConfig.resultPaneY);
            } else {
                drawTopScore((Graphics2D) g, TetrisGameConfig.resultPaneX, TetrisGameConfig.resultPaneY);
            }
        }

        private void drawPlayer(Graphics2D g, int x, int y) {
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int imgW = aliveImg.getWidth(null) + 5;
            int imgH = aliveImg.getHeight(null);
            int scoreW = defaultFont.charWidth(' ') << 3;
            int oldX = x;
            for (TetrisGamePlayer player : net.getAllPlayers()) {
                if (player.alive) {
                    g.drawImage(aliveImg, x, y, null);
                } else {
                    g.drawImage(deathImg, x, y, null);
                }
                x += imgW;
                g.setColor(pColors[player.color]);
                defaultFont.drawString(player.score + "", x, y, g, 5, ' ');
                x += scoreW;
                defaultFont.drawString(player.name + "", x, y, g, 5, ' ');
                x = oldX;
                y += imgH;
            }
        }

        private void drawTopScore(Graphics2D g, int x, int y) {
            int w = getWidth() - (TetrisGameConfig.resultPaneX << 1);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            for (int i = topScore.length - 1; i >= 0; i--) {
                int textH = defaultFont.getHeight();
                String str = topScore[i];
                if (str != null) {
                    g.setColor(pColors[i]);
                    defaultFont.drawString(topPlayer[i], x, y, g);
                    defaultFont.drawString(str, x + w - defaultFont.stringWidth(str), y, g);
                    y += textH;
                }
            }
        }
    }
}
