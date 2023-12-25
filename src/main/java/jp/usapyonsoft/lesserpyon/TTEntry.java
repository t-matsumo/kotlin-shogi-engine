package jp.usapyonsoft.lesserpyon;

public class TTEntry {
  // 定数定義
  // αβ探索で得た値が、局面の評価値そのもの
  static final public int EXACTLY_VALUE=0;
  // αβ探索で得た値が、β以上だった（valueは下限の値）
  static final public int LOWER_BOUND=1;
  // αβ探索で得た値が、α以下だった（valueは上限の値）
  static final public int UPPER_BOUND=2;
  
  // ハッシュ値
  public int HashVal;
  // 前回の探索での最善手
  public Te best;
  // 前々回以前の探索での最善手
  public Te second;
  // 前回の探索時の評価地
  public int value;
  // その評価値がどのようなものか？
  // 上記の定数に従う。
  public int flag;
  // 評価値を得た際の、手数
  public int tesu;
  // 評価値を得た際の深さ
  public int depth;
  // 評価値を得た際の読みの残り深さ
  public int remainDepth;
}
