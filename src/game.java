import java.awt.*;
import java.awt.event.*;
import java.util.Collections;
import java.util.TreeSet;
import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.io.File;

import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

//実行クラス　－　コンストラクタ　－　run(敵の移動処理)メソッド
//
//マウスクラス　MouseEv(MouseAdapterを継承しMouseMotionListenerを実装したクラス－　mouseClicked(玉の発射処理)メソッド
//                                                                    　ー　mouseMove(jikiの移動処理)メソッド
//敵の移動クラス　Teki_move（Threadを継承）
//
//球の移動クラス　Tamashot(Threadを継承)　－　run(玉の時間処理)メソッド
//
//音声クラス　SoundPlayer（受け取ったファイル名の再生準備）　play(繰り返し音声再生)　play1(効果音用の一回だけ再生)　stop(音声破棄)
//
//プラグ処理クラス　Hantei　－　move_teki_y(敵のy軸移動判定)　－　End_check(gameOver判定)　－　EndGame(gameOver処理)
//
//リセットクラス　Game_reset　ー　　Reset()　―　Result()


//JFrame機能を継承したgameクラスを作成

public class game extends JFrame {
    //起動画面関連
    JLabel lbl_Title = new JLabel(new ImageIcon("resources/image/Title.gif"));

    //画像ラベル
    JLabel lbl_jiki, lbl_teki;
    JLabel[] lbl_tama = new JLabel[3];

/*
	MyCanvas mc;
	Image off_img;
	Toolkit tkit;
	Graphics gg;
*/

    //スコアラベルとゲームオーバー用ラベルとスコアTreeSet＋表示用配列＋表示用文字列
    JLabel lbl_score, lbl_gv, lbl_gvbg, lbl_Result;
    TreeSet<Integer> scores = new TreeSet<Integer>(Collections.reverseOrder());

    //自機の初期位置x
    static int jiki_x = 134;
    static int jiki_y = 285;

    //スコア/gemeOver/玉カウント/teki位置の初期値
    int scr , tc, teki_x, teki_y = 0;
    int gameOver = 0;
    int gamestart = 1;

    //クリック時に初期化する弾の位置（時間処理）
    int tama_shot, tama_y = -20;

    //音声ファイル関連
    String[] Sound_list = {"resources/sound/start_sound.mp3", "resources/sound/main_sound.mp3"
            , "resources/sound/end_sound.mp3", "resources/sound/shot.mp3", "resources/sound/hit_sound.mp3"};
    SoundPlayer bgm, ef_sound;

    //マウスクラスのインスタンス
    MouseEv mev = new MouseEv();

    //判定クラス/リセットクラス/玉用Threadクラス/teki_y用Thread
    Hantei ht = new Hantei();
    Game_reset game_reset = new Game_reset();
    Tamashot[] shot = new Tamashot[3];
    Teki_move teki_run;

    game() {
        //フレーム基本
        setSize(310, 370);
        setTitle("Shooting Game!!");
        setLayout(null);
        setBackground(new Color(192, 192, 192));

        //オブジェクト設定
        //画像ファイルをアイコンに変更
        ImageIcon img_jiki = new ImageIcon("resources/image/jiki.gif");
        ImageIcon img_teki = new ImageIcon("resources/image/teki.gif");
        ImageIcon img_tama = new ImageIcon("resources/image/tama.gif");

        //tama_shotラベルのインスタンス
        for (int i = 0; i < 3; i++) {
            lbl_tama[i] = new JLabel(img_tama);
            lbl_tama[i].setBounds(tama_shot, tama_y, 8, 8);
        }

        //ラベル設定
        lbl_score = new JLabel("score：" + scr);

        lbl_Result = new JLabel();
        lbl_Result.setFont(new Font("Arial", Font.PLAIN, 16));
        lbl_Result.setForeground(Color.white);

        lbl_gvbg = new JLabel();
        lbl_gvbg.setBackground(new Color(105, 105, 105));

        lbl_gv = new JLabel("Game Over");
        lbl_gv.setFont(new Font("Arial", Font.PLAIN, 40));
        lbl_gv.setForeground(Color.white);

        lbl_jiki = new JLabel(img_jiki);
        lbl_teki = new JLabel(img_teki);

        //初期の非表示設定
        lbl_Title.setVisible(true);
        lbl_gvbg.setVisible(false);
        lbl_gv.setVisible(false);
        lbl_Result.setVisible(false);

        //透過処理
        lbl_Title.setOpaque(true);
        lbl_score.setOpaque(false);
        lbl_jiki.setOpaque(false);
        lbl_teki.setOpaque(false);
        lbl_gvbg.setOpaque(true);
        lbl_gv.setOpaque(false);
        lbl_Result.setOpaque(false);

        //初期の配置位置
        lbl_jiki.setBounds(jiki_x, jiki_y, 32, 32);
        lbl_teki.setBounds(teki_x, teki_y, 32, 32);
        lbl_score.setBounds(120, 140, 110, 20);
        lbl_Title.setBounds(0, 0, 300, 350);
        lbl_gvbg.setBounds(0, 0, 300, 350);
        lbl_gv.setBounds(40, 80, 220, 50);
        lbl_Result.setBounds(70, 40, 200, 260);

        //設置
        add(lbl_Title);
        add(lbl_score);
        add(lbl_gv);
        add(lbl_Result);
        add(lbl_gvbg);
        add(lbl_teki);
        add(lbl_jiki);

        //その他機能追加
        addMouseMotionListener(mev);
        addMouseListener(mev);

        addWindowListener(new java.awt.event.WindowAdapter() {      //windowイベント
            public void windowClosing(WindowEvent e) {
                System.exit(1);
            }
        });
    }

    //ゲームOP画面の表示
    public void OP() {
        //起動音声の実行
        bgm = new SoundPlayer(new File(Sound_list[0]));
        bgm.play();

        //画面をクリックするまで停止状態
        while (true) {
            System.out.print("");
            if (gamestart == 0) break;
        }

        //コンストラクタのlbl_Titleを破棄
        lbl_Title.setVisible(false);
        lbl_Title=null;

        //ゲーム開始メソッド実行
        GameStart();
    }

    //ゲーム開始
    void GameStart(){
        //メイン音声再生
        bgm.stop();
        bgm = new SoundPlayer(new File(Sound_list[1]));
        bgm.play();

        //敵の移動を開始
        teki_run = new Teki_move();
        teki_run.start();
    }


    /*	ラベルだけでもイメージバッファが利用できるかテスト用
	class MyCanvas extends Canvas {
		public void paint(Graphics g) {
			off_img = image.toolkit
			g = off_img.Graphics();

			g.drawImage(off_img, 0, 0, this);
		}
	public void update() {
		paint(g)
	}
    */

    //敵の移動とGameOver判定　すべての時間処理をこのスレッドで出来るようにしたい
    class Teki_move extends Thread {
        public void run() {

            //時間処理前にエラー処理を書く
            try {
                //while ( gameOver == 1 ) {	//本来のメソッド処理
                while (true) {
                    if (gameOver == 1) {
                        break;
                    }
                    System.out.println("右へ移動開始");
                    //右へ移動　268を超えたら下の処理に移動
                    while (teki_x < 268) {
                        teki_run.sleep(50);
                        teki_x = teki_x + 4;
                        lbl_teki.setBounds(teki_x, teki_y, 32, 32);
                        ht.move_teki_y();

                        if (gameOver == 1) {
                            break;
                        }
                    }
                    System.out.println("左へ移動開始");
                    //左へ移動　０を下回ったら上の処理に移動
                    while (teki_x > 0) {
                        teki_run.sleep(50);
                        teki_x = teki_x - 4;
                        lbl_teki.setBounds(teki_x, teki_y, 32, 32);
                        ht.move_teki_y();

                        if (gameOver == 1) {
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            //ゲームオーバーを表示する
            game_reset.EndGame();
        }
    }

    //マウス処理　課題　マウス位置とマウスクリックの同時処理方法を見つける　
    //重要な欠点　MouseMovedだけだと、クリック時にマウスの移動が反映されなくなるのでDraggedが必須になる
    // 候補「Component c = ～;
    //		Point pos = c.getMousePosition();
    //		System.out.println(pos);」
    //
    //チャージショットを導入するなら、Threadで　ボタンをBUTTON3に変えるかBUTTON1を長押しした時間で切り替える
    class MouseEv extends MouseInputAdapter {
        private int resalt_switch = 0;  //mouseClicked用リザルト画面の切り替えスイッチ

        public void mouseMoved(MouseEvent e) { Moving(e); }   //移動処理へ

        public void mouseDragged(MouseEvent e) {
            Shooting(e);    //クリック処理へ
            Moving(e);      //移動処理へ
        }

        public void mouseClicked(MouseEvent e) { Shooting(e);  }   //クリック処理へ移動

        //フレーム内のマウスポインタ先に自機を表示し続ける
        public void Moving(MouseEvent e) {
            if ((gameOver != 1) & (gamestart != 1)) {
                jiki_x = e.getX();
                lbl_jiki.setBounds(jiki_x - 23, jiki_y, 32, 32);
            }
        }

        //玉を発射実行クラス 配列かHashSetがらputした方が効率がいいかも　球残は配列がnullか如何かで処理する
        public void Shooting(MouseEvent e){

            //gameOver判定が１じゃない時は処理
            if ( gameOver != 1 ){       //gamestart判定が１じゃない時 & 直前に発射した球位置が250を下回った時（連射対策）に処理
                if ( gamestart != 1 && lbl_tama[tc].getY() <= 150 )  {
                    tc += 1;
                    System.out.println("１：０現在値： " + lbl_tama[0].getY() + "  " + tama_y + "  ");
                    System.out.println("１：１現在値： " + lbl_tama[1].getY() + "  " + tama_y + "  ");
                    System.out.println("１：２現在値： " + lbl_tama[2].getY() + "  " + tama_y + "  ");

                    System.out.println("１：clicked: " + tc);
                    //最大連射数3まで　2だった場合は０に戻す
                    if (tc > 2) {
                        tc = 0;
                        System.out.println("２：1if_reset_tc: " + tc);
                    }
                    System.out.println("２：not_1if: " + tc);

                    //玉が場外の場合は処理する
                    if (lbl_tama[tc].getY() == -20) {

                        //連射数＋１クリックした時の自機の位置に玉を設置
                        tama_shot = jiki_x - 10;
                        tama_y = 277;
                        System.out.println("３：2if; " + lbl_tama[tc].getY() + "  " + tama_shot + "  " + tama_y + "  " + tc);

                        //玉の時間処理をThreadに渡す
                        shot[tc] = new Tamashot();
                        shot[tc].start();
                    }

                } else {         //起動画面に移行
                    gamestart = 0;
                    System.out.println("３：not_2if: " + tc);
                }

                //連射対策でMouseClickedだった場合のみ実行
            } else if ( resalt_switch != 1 && e.getClickCount() != 0 ) {         //リザルト画面に移行
                game_reset.Result();
                resalt_switch = 1;

            } else if (e.getClickCount() != 0 ){
                game_reset.Reset();     //リスタート処理開始
                resalt_switch = 0;

            }

        }
    }

    //位置の時間処理クラス　玉：縦
    class Tamashot extends Thread {
        private int ttc, ts, ty;

        public void run() {

            this.ttc = tc;
            this.ts = tama_shot;
            this.ty = tama_y;

            System.out.println("４：start_lbl_tama["+ttc+"]"+"  "+tama_y);

            //球発射音
            ef_sound = new SoundPlayer(new File(Sound_list[3]));
            ef_sound.play1();

            //球表示
            lbl_tama[ttc].setBounds(ts, ty, 8, 8);
            add(lbl_tama[ttc]);

            //球が発射して敵に当たるか端に到達するまで移動しながら描画し続ける
            try{
                while ( ty > 0 ) {

                    //tekiがtamaの範囲内に存在した場合にAND処理する（玉と敵のサイズも考慮する）
                    if ((ty < teki_y +25) && (ty +7 > teki_y)) {			//y軸の当り判定
                        if (( ts < teki_x +28) && (ts +7 > teki_x)) {	//x軸の当たり判定

                            //hit音を鳴らしてscrに+1し、玉の画面上から消しwhileを終了する。
                            ef_sound = new SoundPlayer(new File(Sound_list[4]));
                            ef_sound.play1();

                            scr += 1;
                            lbl_score.setText( "score：" + scr );

                            break;

                        } else {
                            //球を移動させる
                            shot[ttc].sleep(10);

                            ty -= 5;
                            lbl_tama[ttc].setBounds( ts, ty, 8, 8);
                        }
                    } else {
                        //球を移動させる
                        shot[ttc].sleep(10);

                        ty -= 5;
                        lbl_tama[ttc].setBounds( ts, ty, 8, 8);
                    }
                }
                //whileが終了したら球位置をリセットしてラベル位置を再設定

            //lbl_tama[ttc].setVisible(false);

            }catch (Exception e) { System.out.println(e); }

            ty = -20;
            lbl_tama[ttc].setBounds( ts, ty, 8, 8);
            System.out.println("５：End_lbl_tama["+ttc+"]"+"  "+lbl_tama[ttc].getY());
        }
    }

    //音声再生クラス（JavaFXのライブラリ使用）
    class SoundPlayer {
        final JFXPanel p = new JFXPanel();      //JavaFXのエラー対策
        private MediaPlayer media_player;

        //再生中の音を破棄して新しい音声ファイルの準備をする
        SoundPlayer(File fname) {
            try {

                Media mp3 = new Media(fname.toURI().toString());
                media_player = new MediaPlayer(mp3);

            }catch (Exception e) {
                e.printStackTrace();
            }

        }
        //効果音用　単発
        public void play1() {
            media_player.setVolume(0.5);
            media_player.play();

        }

        //BGM用
        public void play() {
            media_player.setCycleCount(MediaPlayer.INDEFINITE);
            media_player.setAutoPlay(true);
            media_player.setVolume(0.0);
            media_player.play();

            new Thread(() -> {
                for (double i = 1; i <= 100; ++i) {
                    media_player.setVolume(i / 100);
                    try {
                        Thread.sleep(30);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println("start" + i);
                }
            }).start();
        }

        public void stop() {
/*
            new Thread(() -> {
                for (int i = 100; i >= 0; i--) {
                    media_player.setVolume((double) i / 100);
                    try {
                        Thread.sleep(100);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println("stop" + i);
                }
            }).start();
*/
            media_player.stop();
            media_player = null;

        }
    }

    //スイッチ等の判定クラス
    class Hantei {
        private int i = 0;

        //特定の位置でtekiをjikiに近づける処理。最終的にGameOver()に移動
        void move_teki_y() {

            if ((i == 0) && (teki_x < 100)) {
                i += 1;
            }
            if (((teki_x == 100) && (i == 1)) || (teki_x == 0)) {
                teki_y += 10;
                i -= 1;
                lbl_teki.setBounds(teki_x, teki_y, 32, 32);
                System.out.println("前に移動");
            }
            End_check();
        }

        //jikiとtekiが接触したら処理を実行する　なければtt_xに戻る
        void End_check() {
            if (jiki_y < teki_y + 32) {
                if ((jiki_x < teki_x + 32) && (jiki_x + 32 > teki_x)) {
                    gameOver += 1;
                }
            }
        }
    }

    //GameOver処理クラス
    class Game_reset {
        void EndGame() {
            //音声切り替え
            bgm.stop();
            bgm = new SoundPlayer(new File(Sound_list[2]));
            bgm.play();

            lbl_gvbg.setVisible(true);
            lbl_gv.setVisible(true);
            lbl_score.setForeground(Color.white);
            System.out.println("ゲームオーバーです\nclick_to_Result");
        }

        //ランキングメソッド
        void Result() {
            System.out.println("result画面です");

            //ゲームオーバー関連ラベルの非表示
            lbl_gv.setVisible(false);
            lbl_score.setVisible(false);

            //リザルトSetに現在のスコアを追加しTop５以下は破棄し、ラベル用のテキストを構築
            StringBuilder result_text = new StringBuilder();
            String now_scr = "";    //スコアがランクインした時用の変数（未使用）
            int i = 0;
            scores.add(scr);

            for (int a : scores) {
                //リザルト用のスコア配列の作成
                if (i <= 4) {
                    if (a == scr) { now_scr = "　Now!"; }     //スコアがランクインした時の処理

                    //ストリングビルダーに追加
                    if (i <= 4) {
                        result_text.append(String.format("<HTML>Score %d  :　%04d%4s <br><br>", i + 1, a, now_scr));

                    } else if (i > 4) { scores.remove(a); }   //ランク５以降は削除

                    if (a == scr) { now_scr = ""; }     //スコアがランクインした時の書き換え処理処理
                    i += 1;     //次のランクに移動
                }
                System.out.println("整理しました" + i);

                lbl_Result.setText(String.valueOf(result_text));
                lbl_Result.setVisible(true);
            }
        }

        //リセットメソッド
        public void Reset() {

            //スコアをリザルトに追加し、リザルトスコア以外をゲーム開始時の状態に初期化して再開
            lbl_Result.setVisible(false);
            lbl_gv.setVisible(false);
            lbl_gvbg.setVisible(false);
            scr = 0;    tc = 0;     teki_x = 0;     teki_y = 0;     gameOver = 0;

            lbl_score.setText("score：" + scr);
            lbl_score.setForeground(Color.black);
            lbl_score.setVisible(true);

            lbl_jiki.setBounds(jiki_x,jiki_y,32,32);
            lbl_teki.setBounds(teki_x,teki_y,32,32);

            bgm.stop();
            bgm = new SoundPlayer(new File(Sound_list[1]));
            bgm.play();

            //敵移動スレッドを実行
            teki_run = new Teki_move();
            teki_run.start();
        }
    }

    public static void main(String[] args){
        game g = new game();
        g.setVisible(true);
        g.OP();
    }
}