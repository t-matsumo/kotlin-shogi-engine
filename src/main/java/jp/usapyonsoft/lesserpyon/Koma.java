package jp.usapyonsoft.lesserpyon;

// 駒
public class Koma implements Constants,Cloneable {
  // 駒の種類の定義
  public static final int EMPTY=0;          // 「空」
  public static final int EMP=EMPTY;        // 「空」の別名。
  public static final int PROMOTE=8;        // 「成り」フラグ

  public static final int FU= 1;            // 「歩」
  public static final int KY= 2;            // 「香車」
  public static final int KE= 3;            // 「桂馬」
  public static final int GI= 4;            // 「銀」
  public static final int KI= 5;            // 「金」
  public static final int KA= 6;            // 「角」
  public static final int HI= 7;            // 「飛車」
  public static final int OU= 8;            // 「玉将」
  public static final int TO=FU+PROMOTE;    // 「と金」＝「歩」＋成り
  public static final int NY=KY+PROMOTE;    // 「成り香」＝「香車」＋成り
  public static final int NK=KE+PROMOTE;    // 「成り桂」＝「桂馬」＋成り
  public static final int NG=GI+PROMOTE;    // 「成り銀」＝「銀」＋成り
  public static final int UM=KA+PROMOTE;    // 「馬」＝「角」＋成り
  public static final int RY=HI+PROMOTE;    // 「竜」＝「飛車」＋成り

  public static final int SFU=SENTE+FU;     // 「先手の歩」＝「歩」＋「先手」
  public static final int SKY=SENTE+KY;     // 「先手の香」
  public static final int SKE=SENTE+KE;     // 「先手の桂」
  public static final int SGI=SENTE+GI;     // 「先手の銀」
  public static final int SKI=SENTE+KI;     // 「先手の金」
  public static final int SKA=SENTE+KA;     // 「先手の角」
  public static final int SHI=SENTE+HI;     // 「先手の飛」
  public static final int SOU=SENTE+OU;     // 「先手の玉」
  public static final int STO=SENTE+TO;     // 「先手のと金」
  public static final int SNY=SENTE+NY;     // 「先手の成香」
  public static final int SNK=SENTE+NK;     // 「先手の成桂」
  public static final int SNG=SENTE+NG;     // 「先手の成銀」
  public static final int SUM=SENTE+UM;     // 「先手の馬」
  public static final int SRY=SENTE+RY;     // 「先手の竜」

  public static final int GFU=GOTE +FU;     // 「後手の歩」＝「歩」＋「後手」
  public static final int GKY=GOTE +KY;     // 「後手の香」
  public static final int GKE=GOTE +KE;     // 「後手の桂」
  public static final int GGI=GOTE +GI;     // 「後手の銀」
  public static final int GKI=GOTE +KI;     // 「後手の金」
  public static final int GKA=GOTE +KA;     // 「後手の角」
  public static final int GHI=GOTE +HI;     // 「後手の飛」
  public static final int GOU=GOTE +OU;     // 「後手の玉」
  public static final int GTO=GOTE +TO;     // 「後手のと金」
  public static final int GNY=GOTE +NY;     // 「後手の成香」
  public static final int GNK=GOTE +NK;     // 「後手の成桂」
  public static final int GNG=GOTE +NG;     // 「後手の成銀」
  public static final int GUM=GOTE +UM;     // 「後手の馬」
  public static final int GRY=GOTE +RY;     // 「後手の竜」
  
  public static final int WALL=64;          // 盤の外を表すための定数

  // 先手の駒かどうかの判定
  static public boolean isSente(int koma) {
    return (koma & SENTE)!=0;
  }
  
  // 後手の駒かどうかの判定
  static public boolean isGote(int koma) {
    return (koma & GOTE)!=0;
  }
  
  // 手番から見て自分の駒かどうか判定
  static public boolean isSelf(int teban,int koma) {
    if (teban==SENTE) {
      return isSente(koma);
    } else {
      return isGote(koma);
    }
  }
  
  // 手番から見て相手の駒かどうか判定
  static public boolean isEnemy(int teban,int koma) {
    if (teban==SENTE) {
      return isGote(koma);
    } else {
      return isSente(koma);
    }
  }
  
  // 駒の種類の取得
  static public int getKomashu(int koma) {
    // 先手後手のフラグをビット演算でなくせば良い。
    return koma & 0x0f;
  }
  
  // 駒の文字列化用の文字列
  static public final String komaString[]={
    "　",
    "歩",
    "香",
    "桂",
    "銀",
    "金",
    "角",
    "飛",
    "王",
    "と",
    "杏",
    "圭",
    "全",
    "",
    "馬",
    "竜"
  };
  
  // 駒の文字列化…盤面の表示用
  static public String toBanString(int koma) {
    if ( koma==EMPTY ) {
      return "   ";
    } else if ( (koma & SENTE) !=0 ) {
      // 先手の駒には、" "を頭に追加
      return " "+komaString[getKomashu(koma)];
    } else {
      // 後手の駒には、"v"を頭に追加
      return "v"+komaString[getKomashu(koma)];
    }
  }
  
  // 駒の文字列化…持ち駒、手などの表示用
  static public String toString(int koma) {
    return komaString[getKomashu(koma)];
  }
  
  // 駒が成れるかどうかを表す
  public static final boolean canPromote[]={
     false,false,false,false,false,false,false,false,// 先手でも後手でもない駒
     false,false,false,false,false,false,false,false,// 先手でも後手でもない駒
     false, true, true, true, true,false, true, true,// 空、先手の歩香桂銀金角飛
     false,false,false,false,false,false,false,false,// 先手の王、と杏圭全　馬竜
     false, true, true, true, true,false, true, true,// 空、後手の歩香桂銀金角飛
     false,false,false,false,false,false,false,false // 後手の王、と杏圭全　馬竜
  };
  
  static public boolean canPromote(int koma) {
    return canPromote[koma];
  }
}
