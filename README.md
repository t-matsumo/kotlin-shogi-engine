# 使用ライブラリ
## [れさぴょん for Java](http://usapyon.game.coocan.jp/lesserpyon/#jav)
### 用途
- 合法手の列挙

### 変更点
jarのまま使用すると実行時にエラーが発生するため、Kotlinから呼び出せるように以下の変更を加えています。
- iconvコマンドで、文字コードをSJISからUTF-8に変換
- 文字コードの変換に失敗した箇所（`~`が`‾`になっていた）を手動で変更
- Kotlinから使いたいもので、package privateになっていた箇所をpublicへ変更