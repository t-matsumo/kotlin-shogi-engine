package jp.usapyonsoft.lesserpyon;
import java.util.Vector;

public class Kyokumen implements Constants,Cloneable {
  // 盤面
  int ban[];
  
  // 持ち駒
  int hand[];
  
  // 手番
  public int teban=SENTE;
  
  // 現在の先手からみた評価値
  int eval=0;
  
  // 先手玉の位置…盤外の、利きの届かないところ、(-2,-2)=-2*16-2=-34に設定。
  int kingS=-34;
  
  // 後手玉の位置…盤外の、利きの届かないところ、(-2,-2)=-2*16-2=-34に設定。
  int kingG=-34;
  
  // 使った時間
  int spentTime[]=new int[2];
  
  // 手数
  int tesu=0;
  
  public Kyokumen() {
    ban=new int[16*11];
    hand=new int[Koma.GHI+1];
    // 盤面全体を「カベ」で一旦埋める
    for(int i=0;i<16*11;i++) {
      ban[i]=Koma.WALL;
    }
    // 盤面にあたる場所を空白に設定する
    for(int suji=1;suji<=9;suji++) {
      for(int dan=1;dan<=9;dan++) {
        ban[(suji<<4)+dan]=Koma.EMPTY;
      }
    }
  }
  
  // 局面のコピーを行う
  public Object clone() {
    Kyokumen k=new Kyokumen();
    
    // 盤面のコピー
    for(int i=0;i<16*11;i++) {
      k.ban[i]=ban[i];
    }
    
    // 持ち駒のコピー
    for(int i=Koma.SFU;i<=Koma.GHI;i++) {
      k.hand[i]=hand[i];
    }
    
    // 手番のコピー
    k.teban=teban;
    
    // 評価値のコピー
    k.eval=eval;
    
    // 玉の位置のコピー
    k.kingS=kingS;
    k.kingG=kingG;
    
    return k;
  }
  
  // 局面が同一かどうか
  public boolean equals(Object o) {
    Kyokumen k=(Kyokumen)o;
    if (k==null) return false;
    return equals(k);
  }
  
  // 局面が同一かどうか
  public boolean equals(Kyokumen k) {
    // 手番の比較
    if (teban!=k.teban) {
      return false;
    }
    
    // 盤面の比較
    // 各マスについて…
    for(int suji=0x10;suji<=0x90;suji+=0x10) {
      for(int dan=1;dan<=9;dan++) {
        // 盤面上の筋と段にある駒が、比較対象の盤面上の同じ位置にある駒と
        // 同じかどうか比較する。
        if (ban[suji+dan]!=k.ban[suji+dan]) {
          // 違っていたら、falseを返す。
          return false;
        }
      }
    }
    
    // 持ち駒の比較
    // 持ち駒の枚数を比較する。
    for(int i=Koma.SFU;i<=Koma.GHI;i++) {
      if (hand[i]!=k.hand[i]) {
        // 違っていたら、falseを返す。
        return false;
      }
    }
    
    // 完全に一致した。
    return true;
  }
  
  // ある位置にある駒を取得する
  public int get(int p) {
    // 盤外なら、「盤外＝壁」を返す
    if (p<0 || 16*11<p) {
      return Koma.WALL;
    }
    return ban[p];
  }
  
  // ある位置にある駒を置く。
  public void put(int p,int koma) {
    ban[p]=koma;
  }
  
  // 与えられた手で一手進めてみる。
  public void move(Te te) {
    // 盤面からあった駒がなくなる
    BanHash^=HashSeed[get(te.to)][te.to];
    // 駒の行き先に駒があったなら…
    if (get(te.to)!=Koma.EMPTY) {
      // 盤面からその駒がなくなった分、評価値を減じる。
      eval-=komaValue[get(te.to)];
      // 持ち駒にする
      if (Koma.isSente(get(te.to))) {
        // 取った駒が先手の駒なら後手の持ち駒に。
        int koma=get(te.to);
        // 成りなどのフラグ、先手・後手の駒のフラグをクリア。
        koma=koma & 0x07;
        // 後手の駒としてのフラグをセット
        koma=koma | GOTE;
        // 持ち駒に追加。
        hand[koma]++;
        // 持ち駒に駒が追加される
        HandHash^=HandHashSeed[koma][hand[koma]];
        eval+=komaValue[koma];
      } else {
        // 取った駒が後手の駒なら先手の持ち駒に。
        int koma=get(te.to);
        // 成りなどのフラグ、先手・後手の駒のフラグをクリア。
        koma=koma & 0x07;
        // 先手の駒としてのフラグをセット
        koma=koma | SENTE;
        // 持ち駒に追加。
        hand[koma]++;
        // 持ち駒に駒が追加される
        HandHash^=HandHashSeed[koma][hand[koma]];
        eval+=komaValue[koma];
      }
    }
    if (te.from==0) {
      // 持ち駒を打った
      // 持ち駒を一枚減らす。
      HandHash^=HandHashSeed[te.koma][hand[te.koma]];
      hand[te.koma]--;
    } else {
      // 盤上の駒を進めた→元の位置は、EMPTYに。
      put(te.from,Koma.EMPTY);
      BanHash^=HashSeed[te.koma][te.from];
      BanHash^=HashSeed[Koma.EMPTY][te.from];
    }
    // 駒を移動先に進める。
    int koma=te.koma;
    if (te.promote) {
      // 「成り」の処理
      // 成る前の駒の価値を減じる
      eval-=komaValue[koma];
      koma=koma|Koma.PROMOTE;
      // 成った後の駒の価値を加える
      eval+=komaValue[koma];
    }
    put(te.to,koma);
    BanHash^=HashSeed[koma][te.to];
    if (te.koma==Koma.SOU) {
      kingS=te.to;
    } else if (te.koma==Koma.GOU) {
      kingG=te.to;
    }
    HashVal=BanHash^HandHash;
  }
  
  // 与えられた手で一手戻す。
  public void back(Te te) {
    // 盤面からあった駒がなくなる
    BanHash^=HashSeed[get(te.to)][te.to];
    // 取った駒を盤に戻す
    put(te.to,te.capture);
    // 盤面に駒を戻す
    BanHash^=HashSeed[te.capture][te.to];
    // 評価点も戻す
    eval+=komaValue[te.capture];
    
    // 取った駒がある時には…
    if (te.capture!=Koma.EMPTY) {
      // 持ち駒に入っているはずなので、減らす。
      if (Koma.isSente(te.capture)) {
        // 取った駒が先手の駒なら後手の持ち駒に。
        int koma=te.capture;
        // 成りなどのフラグ、先手・後手の駒のフラグをクリア。
        koma=koma & 0x07;
        // 後手の駒としてのフラグをセット
        koma=koma | GOTE;
        // 持ち駒から減らす
        HandHash^=HandHashSeed[koma][hand[koma]];
        hand[koma]--;
        eval-=komaValue[koma];
      } else {
        // 取った駒が後手の駒なら先手の持ち駒に。
        int koma=te.capture;
        // 成りなどのフラグ、先手・後手の駒のフラグをクリア。
        koma=koma & 0x07;
        // 先手の駒としてのフラグをセット
        koma=koma | SENTE;
        // 持ち駒から減らす。
        HandHash^=HandHashSeed[koma][hand[koma]];
        hand[koma]--;
        eval-=komaValue[koma];
      }
    }
    
    if (te.from==0) {
      // 駒打ちだったので、持ち駒に戻す
      hand[te.koma]++;
      HandHash^=HandHashSeed[te.koma][hand[te.koma]];
      BanHash^=HashSeed[Koma.EMPTY][te.from];
    } else {
      // 動かした駒を元の位置に戻す。
      put(te.from,te.koma);
      BanHash^=HashSeed[Koma.EMPTY][te.from];
      BanHash^=HashSeed[te.koma][te.from];
      if (te.promote) {
        // 成っていたので、その分の点数を計算しなおす。
        // 成った後の駒の価値を減じる
        int koma=te.koma|Koma.PROMOTE;
        eval-=komaValue[koma];
        // 成る前の駒の価値を加える
        eval+=komaValue[te.koma];
      }
    }
    if (te.koma==Koma.SOU) {
      kingS=te.from;
    } else if (te.koma==Koma.GOU) {
      kingG=te.from;
    }
    HashVal=BanHash^HandHash;
  }
  
  // kingS,kingGを初期化する
  void initKingPos() {
    // 先手と後手の玉の位置…盤外で、利きの届かない位置(-2,-2)にあたる
    // 位置で初期化しておく。
    kingS=-34;
    kingG=-34;
    // 筋、段でループ
    for(int suji=0x10;suji<=0x90;suji+=0x10) {
      for(int dan=1;dan<=9;dan++) {
        if (ban[suji+dan]==Koma.SOU) {
          // 見つかった
          kingS=suji+dan;
        }
        if (ban[suji+dan]==Koma.GOU) {
          // 見つかった
          kingG=suji+dan;
        }
      }
    }
  }
  
  // 玉の位置を返す
  public int searchGyoku(int teban) {
    if (teban==SENTE) {
      return kingS;
    } else {
      return kingG;
    }
  }
  
  // 局面を評価するための、駒の価値。
  // 先手の駒はプラス点、後手の駒はマイナス点にする。
  static final int komaValue[]={
    0,    0,    0,    0,    0,    0,    0,    0, // 何もない場所及び
    0,    0,    0,    0,    0,    0,    0,    0, // 先手でも後手でもない駒
    0,  100,  500,  600,  800,  900, 1300, 1500, // 何もない場所、先手の歩〜飛車
    10000, 1100,  800,  800,  900,    0, 1500, 1700, // 先手玉、及びと〜竜
    0, -100, -500, -600,-800,-900,-1300,-1500, // 何もない場所、後手の歩〜飛車
    -10000,-1100,-800,-800,-900,    0,-1500,-1700  // 後手玉、及びと〜竜
  };
  
  // 初期化した際に、局面を評価する関数
  void initEval() {
    eval=0;
    // まず、盤面の駒から。
    for(int suji=0x10;suji<=0x90;suji+=0x10) {
      for(int dan=1;dan<=9;dan++) {
        eval+=komaValue[ban[suji+dan]];
      }
    }
    // 次に、持ち駒
    for(int i=Koma.SFU;i<=Koma.SHI;i++) {
      eval+=komaValue[i]*hand[i];
    }
    for(int i=Koma.GFU;i<=Koma.GHI;i++) {
      eval+=komaValue[i]*hand[i];
    }
  }
  
  public void initAll() {
    initEval();
    initKingPos();
    CalcHash();
  }
  
  // 局面を評価する関数。
  public int evaluate() {
    return eval;
  }
  
  // CSA形式の棋譜ファイル文字列
  static final String csaKomaTbl[] = {
    "   ","FU","KY","KE","GI","KI","KA","HI",
    "OU","TO","NY","NK ","NG","","UM","RY",
    ""  ,"+FU","+KY","+KE","+GI","+KI","+KA","+HI",
    "+OU","+TO","+NY","+NK","+NG",""   ,"+UM","+RY",
    ""  ,"-FU","-KY","-KE","-GI","-KI","-KA","-HI",
    "-OU","-TO","-NY","-NK","-NG",""   ,"-UM","-RY"
  };
  
  
  // CSA形式の棋譜ファイルから、局面を読み込む
  public void ReadCsaKifu(String[] csaKifu) {
    // 駒箱に入っている残りの駒。残りを全て持ち駒にする際などに使用する。
    int restKoma[]=new int[Koma.HI+1];
    
    // 駒箱に入っている駒＝その種類の駒の枚数
    restKoma[Koma.FU]=18;
    restKoma[Koma.KY]=4;
    restKoma[Koma.KE]=4;
    restKoma[Koma.GI]=4;
    restKoma[Koma.KI]=4;
    restKoma[Koma.KA]=2;
    restKoma[Koma.HI]=2;
    
    // 盤面を空に初期化
    for(int suji=0x10;suji<=0x90;suji+=0x10) {
      for(int dan=1;dan<=9;dan++) {
        ban[suji+dan]=Koma.EMPTY;
      }
    }
    
    // 文字列から読み込み
    for(int i=0;i<csaKifu.length;i++) {
      String line=csaKifu[i];
      System.out.println(""+i+" :"+line);
      if (line.startsWith("P+")) {
        if (line.equals("P+00AL")) {
          // 残りの駒は全部先手の持ち駒
          for(int koma=Koma.FU;koma<=Koma.HI;koma++) {
            hand[SENTE|koma]=restKoma[koma];
          }
        } else {
          // 先手の持ち駒
          for(int j=0;j<=line.length()-6;j+=4) {
            int koma=0;
            String komaStr=line.substring(j+2+2,j+2+4);
            for(int k=Koma.FU;k<=Koma.HI;k++) {
              if(komaStr.equals(csaKomaTbl[k])) {
                koma=k;
                break;
              }
            }
            hand[SENTE|koma]++;
          }
        }
      } else if (line.startsWith("P-")) {
        if (line.equals("P-00AL")) {
          // 残りの駒は全部後手の持ち駒
          for(int koma=Koma.FU;koma<=Koma.HI;koma++) {
            hand[GOTE|koma]=restKoma[koma];
          }
        } else {
          // 後手の持ち駒
          for(int j=0;j<line.length();j+=4) {
            int koma=0;
            for(int k=Koma.FU;k<=Koma.HI;k++) {
              if(line.substring(j+2,j+4).equals(csaKomaTbl[k])) {
                koma=k;
                break;
              }
            }
            hand[GOTE|koma]++;
          }
        }
      } else if (line.startsWith("P")) {
        // 盤面の表現
        // P1〜P9まで。
        String danStr=line.substring(1,2);
        int dan=0;
        try {
          dan=Integer.parseInt(danStr);
        } catch(Exception e) {
          // …握りつぶすことにしておく。
        }
        String komaStr;
        for(int suji=1;suji<=9;suji++) {
          // ややこしいが、左側が９筋、右側が１筋…
          // 文字列の頭の方が９筋で、後ろの方が１筋。
          // そのため、読み込みの時に逆さに読み込む。
          komaStr=line.substring(2+(9-suji)*3,2+(9-suji)*3+3);
          int koma=Koma.EMPTY;
          for(int k=Koma.EMPTY;k<=Koma.GRY;k++) {
            if (komaStr.equals(csaKomaTbl[k])) {
              koma=k;
              // 成のフラグを取って、残りの駒から
              // その種類の駒を一枚ひいておく。
              restKoma[(Koma.getKomashu(koma) & ~Koma.PROMOTE)]--;
              break;
            }
          }
          ban[(suji<<4)+dan]=koma;
        }
      } else if (line.equals("-")) {
        teban=GOTE;
      } else if (line.equals("+")) {
        teban=SENTE;
      }
    }
    initAll();
  }
  
  // 局面を表示用に文字列化
  public String toString() {
    String s="";
    // 後手持ち駒表示
    s+="後手持ち駒：";
    for(int i=Koma.GFU;i<=Koma.GHI;i++) {
      if (hand[i]==1) {
        s+=Koma.toString(i);
      } else if (hand[i]>1) {
        s+=Koma.toString(i)+hand[i];
      }
    }
    s+="¥n";
    // 盤面表示
    s+=" ９　８　７　６　５　４　３　２　１¥n";
    s+="+---+---+---+---+---+---+---+---+---+¥n";
    for(int dan=1;dan<=9;dan++) {
      for(int suji=9;suji>=1;suji--) {
        s+="|";
        s+=Koma.toBanString(ban[(suji<<4)+dan]);
      }
      s+="|";
      s+=danStr[dan];
      s+="¥n";
      s+="+---+---+---+---+---+---+---+---+---+¥n";
    }
    // 先手持ち駒表示
    s+="先手持ち駒：";
    for(int i=Koma.SFU;i<=Koma.SHI;i++) {
      if (hand[i]==1) {
        s+=Koma.toString(i);
      } else if (hand[i]>1) {
        s+=Koma.toString(i)+hand[i];
      }
    }
    s+="¥n";
    return s;
  }
  
  // 平手の初期盤面を与える
  static final int ShokiBanmen[][]={
    {Koma.GKY,Koma.GKE,Koma.GGI,Koma.GKI,Koma.GOU,Koma.GKI,Koma.GGI,Koma.GKE,Koma.GKY},
    {Koma.EMP,Koma.GHI,Koma.EMP,Koma.EMP,Koma.EMP,Koma.EMP,Koma.EMP,Koma.GKA,Koma.EMP},
    {Koma.GFU,Koma.GFU,Koma.GFU,Koma.GFU,Koma.GFU,Koma.GFU,Koma.GFU,Koma.GFU,Koma.GFU},
    {Koma.EMP,Koma.EMP,Koma.EMP,Koma.EMP,Koma.EMP,Koma.EMP,Koma.EMP,Koma.EMP,Koma.EMP},
    {Koma.EMP,Koma.EMP,Koma.EMP,Koma.EMP,Koma.EMP,Koma.EMP,Koma.EMP,Koma.EMP,Koma.EMP},
    {Koma.EMP,Koma.EMP,Koma.EMP,Koma.EMP,Koma.EMP,Koma.EMP,Koma.EMP,Koma.EMP,Koma.EMP},
    {Koma.SFU,Koma.SFU,Koma.SFU,Koma.SFU,Koma.SFU,Koma.SFU,Koma.SFU,Koma.SFU,Koma.SFU},
    {Koma.EMP,Koma.SKA,Koma.EMP,Koma.EMP,Koma.EMP,Koma.EMP,Koma.EMP,Koma.SHI,Koma.EMP},
    {Koma.SKY,Koma.SKE,Koma.SGI,Koma.SKI,Koma.SOU,Koma.SKI,Koma.SGI,Koma.SKE,Koma.SKY},
  };
  
  public void initHirate() {
    teban=SENTE;
    for(int dan=1;dan<=9;dan++) {
      for(int suji=9;suji>=1;suji--) {
        ban[(suji<<4)+dan]=ShokiBanmen[dan-1][9-suji];
      }
    }
    for(int koma=Koma.SFU;koma<=Koma.GHI;koma++) {
      hand[koma]=0;
    }
    // 諸々の初期化を行う。
    initAll();
  }
  
  // ハッシュ値関連
  // 盤面のハッシュ値の種
  static private int HashSeed[][];
  // 「手」のハッシュ値の種
  static private int HandHashSeed[][];
  
  public int HashVal;
  public int BanHash;
  public int HandHash;
  
  static long seed=0;
  
  // bits数の乱数を得る。
  static protected int rand(int bits) {
    seed = (seed * 0x5DEECE66DL + 0xBL) & ((1L << 48) - 1);
    return (int)(seed >>> (48 - bits));
  }
  
  // ハッシュの種の初期化
  static {
    seed=0;
    HashSeed=new int [Koma.GRY+1][16*11];
    HandHashSeed=new int[Koma.GHI+1][20];
    for(int i=0;i<=Koma.GRY;i++) {
      for(int j=0;j<16*11;j++) {
        HashSeed[i][j]=rand(30);
      }
    }
    for(int i=0;i<=Koma.GHI;i++) {
      for(int j=0;j<20;j++) {
        HandHashSeed[i][j]=rand(30);
      }
    }
  }
  
  void CalcHash() {
    HandHash=0;
    BanHash=0;
    int i,j;
    for(i=0;i<=Koma.GHI;i++) {
      for(j=0;j<=hand[i];j++) {
        HandHash^=HandHashSeed[i][j];
      }
    }
    for(i=1;i<=9;i++) {
      for(j=1;j<=9;j++) {
        BanHash^=HashSeed[ban[i*16+j]][i*16+j];
      }
    }
    HashVal=HandHash^BanHash;
    //System.out.println(Integer.toHexString(HashVal));
  }
}
