# shooting_game

プログラミングスクールで作成した簡易シューティングを改良したものです。


https://user-images.githubusercontent.com/114842913/196948720-2f9e8dac-15cc-4586-9297-eb596077552c.mp4


＝＝＝＝＝＝＝＝＝＝＝＝＝後付けの機能＝＝＝＝＝＝＝＝＝＝＝＝＝

・BGM

・SE

・起動画面

・ゲームオーバー画面とランキング表示

・移動と攻撃手段をキーボードからマウスへ変更


＝＝＝＝＝＝＝＝＝＝＝＝＝大変だった点＝＝＝＝＝＝＝＝＝＝＝＝＝


・MouseMotionMovedを実行中にMouseListenerが実行出来なかったので

  mouseDraggedからも移動イベントを取得するMouseEventの処理方法に辿り着くまでに苦労した。

・音声ファイルを読み込む為の機能をJavaFXのライブラリから取得する作業で難航しました。

（今後、MP3等の音声ファイルを扱うことを考えて、利便性のあるJavaFXを導入）

・開発プラットフォームを導入し、GitHubとの連携機能の環境設定に時間が掛かった。

・プログラミングスクールではNotepadeでの作業だったので、新しい環境構築とその操作で時間が掛かりました。


＝＝＝＝＝＝＝＝＝＝＝＝＝改　善　点＝＝＝＝＝＝＝＝＝＝＝＝＝


・現在、すべての処理が別々のThreadで動いているのでかなりの無駄な処理が発生していると思われる。

　実行クラスでRunnableを実装して、そのThreadだけですべての処理を行えるか検証したい。

・上記に付随して溜め攻撃、場面のフェードインとフェードアウト、敵の縦移動＋、敵の体力、敵の同時処理の追加

　なども今後挑戦していきたい。

・無料の範囲で利用できるデータベースサービスを利用したオンラインスコアの実装。

　今後挑戦してみたい。



