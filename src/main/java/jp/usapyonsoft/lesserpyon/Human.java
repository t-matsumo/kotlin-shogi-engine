package jp.usapyonsoft.lesserpyon;
import java.util.Vector;
import java.io.*;

public class Human implements Player,Constants {
  // 一行入力用の読み込み元を用意しておく。
  // static メンバー変数で用意しておくのは、人間対人間の
  // 対戦時に、同じ読み込み元を使いたいため。
  // こうすることで、標準入力にファイルを使用できる。
  static BufferedReader reader=
    new BufferedReader(new InputStreamReader(System.in));

  public Te getNextTe(Kyokumen k,int tesu,int spenttime,int limittime,int byoyomi) {
    // 現在の局面での合法手を生成
    Vector v=GenerateMoves.generateLegalMoves(k);
    // 返却する手の初期化…「投了」にあたるような、
    // 合法手でない手を生成しておく。
    Te te=new Te(0,0,0,false,0);

    do {
      if (k.teban==SENTE) {
        System.out.println("先手番です。");
      } else {
        System.out.println("後手番です。");
      }
      System.out.println("指し手を入力して下さい。");
      // 一行入力
      String s="";
      try {
        s=reader.readLine();
      }catch(Exception e){
        // 読み込みエラー？
        e.printStackTrace();
        break;
      }
      // 入力された手が%TORYOだったら、投了して終わり。
      if (s.equals("%TORYO")) {
        break;
      }
      if (s.equals("p")) {
        // 合法手の一覧と局面を出力。
        for(int i=0;i<v.size();i++) {
          Te t=(Te)v.elementAt(i);
          System.out.println(t);
        }
        System.out.println(k);
        continue;
      }
      boolean promote=false;
      if (s.length()==5) {
        if (s.substring(4,5).equals("*")) {
          // ５文字目が'*'だったら、『成り』
          promote=true;
        } else {
          // 何かおかしい…。
          System.out.println("入力が異常です。");
          // 局面を表示して、再入力を求める。
          System.out.println(k);
          continue;
        }
      }
      int fromSuji=0,fromDan=0,toSuji=0,toDan=0;
      try {
        fromSuji=Integer.parseInt(s.substring(0,1));
        fromDan =Integer.parseInt(s.substring(1,2));
        toSuji  =Integer.parseInt(s.substring(2,3));
        toDan   =Integer.parseInt(s.substring(3,4));
      }catch(Exception e){
        // 数値として読み込めなかったので、何か間違っている。
        System.out.println("手を読み込めませんでした。");
        System.out.println(""+fromSuji+""+fromDan+""+toSuji+""+toDan);
        // 局面を表示して、再入力を求める。
        System.out.println(k);
        continue;
      }
      // 駒
      int koma=0;
      // 最初の一桁が０の場合、駒打ち
      if (fromSuji==0) {
        // この場合、二桁目に打つ駒の種類が入っている。
        // 駒は、手番の側の駒。
        koma=fromDan|k.teban;
        // fromDanをクリア。
        fromDan=0;
      }
      int from=fromSuji*16+fromDan;
      int to  =toSuji  *16+toDan;
      if (fromSuji!=0) {
        koma=k.get(from);
      }
      te=new Te(koma,from,to,promote,k.get(to));
      if (!v.contains(te)) {
        // 合法手でないので、何か間違っている…。
        System.out.println(te);
        System.out.println("合法手ではありません。");
        // 局面を再表示。
        System.out.println(k);
      }
    } while(!v.contains(te));
    
    return te;
  }
}
