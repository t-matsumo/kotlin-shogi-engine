package jp.usapyonsoft.lesserpyon;

public interface Player {
  // 次の手を返す関数。
  // 引数は、現在の局面、手数、今までの累積思考時間、与えられた思考時間、
  // 思考時間が切れた後の秒読み
  Te getNextTe(Kyokumen k,int tesu,int spenttime,int limittime,int byoyomi);
}
