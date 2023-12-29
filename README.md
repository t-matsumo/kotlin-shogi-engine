# 概要
[USIプロトコル](http://shogidokoro.starfree.jp/usi.html)に対応した将棋の思考エンジンです。

[将棋GUIソフト「将棋所」](http://shogidokoro.starfree.jp/index.html)に登録して動かすことを想定しています。

USIプロトコルのうち、以下には対応していません
## 未対応の機能
- 詰将棋（開始直後に「checkmate notimplemented」を返します）
- 初期局面が平手でない場合（開始直後に「bestmove resign」を返します）
- go ponderを用いた先読み

# 将棋GUIソフト「将棋所」に思考エンジンとして追加する方法
事前にリリースページから[image.zip](./image.zip)をダウンロードして、任意の場所に展開しておく。

[将棋GUIソフト「将棋所」](http://shogidokoro.starfree.jp/index.html)のページを参考に「エンジン管理」の画面を表示する。

エンジンを追加する際に、`image\bin\shogiai.bat`を選択する。

# 将棋に関して使用したライブラリ
独自の思考エンジンを作成したかったため、ライブラリ内にある評価関数は使用していません。
## [れさぴょん for Java](http://usapyon.game.coocan.jp/lesserpyon/#jav)
### 用途
- 合法手の列挙

### 変更点
jarのまま使用すると実行時にエラーが発生するため、Kotlinから呼び出せるように以下の変更を加えています。
- iconvコマンドで、文字コードをSJISからUTF-8に変換
- 文字コードの変換に失敗した箇所（`~`が`‾`になっていた）を手動で変更
- Kotlinから使いたい処理で、package privateになっていた箇所をpublicへ変更

# ビルドと実行の方法（デバッグ用）
## コンソール上で実行する方法
※ 以下のコマンドを実行できれば良いので、IntelliJ IDEAなどのIDE上からでも実行できます。
```sh
# sh (bash, zsh, etc...) 
./gradlew run

# コマンドプロンプト or PowerShell
.\gradlew run

# 上記のコマンドを実行後、標準入力でUSIプロトコルのコマンドを待ち受けます。
# 「quit」と入力すると終了します。
```

## 「custom runtime image」を作成して実行する方法
将棋所に登録して実行する思考エンジンは、この方法で作成した「custom runtime image」をzip化したものです。(Windows環境向けのみ)
### 「custom runtime image」を作成する
```sh
# sh (bash, zsh, etc...) 
./gradlew jlink

# コマンドプロンプト or PowerShell
.\gradlew jlink

# zip化する際は、「jlink」を「jlinkZip」に置き換える
```
### 「custom runtime image」から実行する
`./gradlew jlink`を実行すると`build/image`に「custom runtime image」が配置される。

`build/image/bin/shogiai`（Windowsでは`build/image/bin/shogiai.bat`）を実行するとコンソール上で動きます。

「quit」と入力すると終了します。