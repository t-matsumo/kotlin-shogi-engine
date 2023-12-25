package jp.usapyonsoft.lesserpyon;

// 駒組みを評価する局面クラス
public class KyokumenKomagumi extends Kyokumen {

  // 各駒が何段目にいるか、でのボーナス
  static final int DanValue[][]={
  //空
      {0,0,0,0,0,0,0,0,0,0},
  //空
      {0,0,0,0,0,0,0,0,0,0},
  //空
      {0,0,0,0,0,0,0,0,0,0},
  //空
      {0,0,0,0,0,0,0,0,0,0},
  //空
      {0,0,0,0,0,0,0,0,0,0},
  //空
      {0,0,0,0,0,0,0,0,0,0},
  //空
      {0,0,0,0,0,0,0,0,0,0},
  //空
      {0,0,0,0,0,0,0,0,0,0},
  //空
      {0,0,0,0,0,0,0,0,0,0},
  //空
      {0,0,0,0,0,0,0,0,0,0},
  //空
      {0,0,0,0,0,0,0,0,0,0},
  //空
      {0,0,0,0,0,0,0,0,0,0},
  //空
      {0,0,0,0,0,0,0,0,0,0},
  //空
      {0,0,0,0,0,0,0,0,0,0},
  //空
      {0,0,0,0,0,0,0,0,0,0},
  //空
      {0,0,0,0,0,0,0,0,0,0},
  //空
      {0,0,0,0,0,0,0,0,0,0},
  //歩
      { 0,  0,15,15,15,3,1, 0, 0, 0},
  //香
      { 0, 1,2,3,4,5,6,7,8,9},
  //桂
      { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
  //銀
      { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
  //金
      { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
  //角
      { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
  //飛
      { 0,10,10,10, 0, 0, 0,  -5, 0, 0},
  //王
      { 0,1200,1200,900,600,300,-10,0,0,0},
  //と
      { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
  //杏
      { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
  //圭
      { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
  //全
      { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
  //金
      { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
  //馬
      { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
  //龍
      { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
  //空
      { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
  //歩
      { 0, 0, 0, 0, -1, -3,-15,-15,-15, 0},
  //香
      { 0,-9,-8,-7, -6, -5, -4, -3, -2,-1},
  //桂
      { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
  //銀
      { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
  //金
      { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
  //角
      { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
  //飛
      { 0, 0, 0, 5, 0, 0, 0,-10,-10,-10},
  //王
      { 0, 0, 0, 0,10,-300,-600,-900,-1200,-1200},
  //と
      { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
  //杏
      { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
  //圭
      { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
  //全
      { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
  //金
      { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
  //馬
      { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
  //龍
      { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
  };
  
  // 戦形の定義
  static final int IvsFURI=0;		// 居飛車対振り飛車
  static final int IvsNAKA=1;		// 居飛車対中飛車
  static final int FURIvsFURI=2;	// 相振り飛車
  static final int FURIvsI=3;		// 振り飛車対居飛車
  static final int NAKAvsI=4;		// 中飛車対居飛車
  static final int KAKUGAWARI=5;	// 角換り
  static final int AIGAKARI=6;		// 相掛かり（または居飛車の対抗系）
  static final int HUMEI=7;		// 戦形不明

  // 各戦形別に、自分の駒に与える、位置によるボーナス点
  // まず、銀
  static final int JosekiKomagumiSGI[][][]={
    { // IvsFURI 舟囲い、美濃、銀冠
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10, -7,-10,-10,-10,-10,-10,  7,-10},
      {-10,  7, -8, -7, 10,-10, 10,  6,-10},
      {-10, -2, -6, -5,-10,  6,-10,-10,-10},
      {-10, -7,  0,-10,-10,-10,-10,-10,-10}
    },{	// IvsNAKA　舟囲い
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10, -7,-10,-10, -7,-10,-10,  7,-10},
      {-10, -5, -8, -7, 10,-10, 10,  6,-10},
      {-10, -2, -3,  0,-10,  6,-10,-10,-10},
      {-10, -7, -5,-10,-10,-10,-10,-10,-10}
    },{ // FURIvsFURI　矢倉（逆）、美濃、銀冠
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10, -7, -7,-10},
      {-10,-10,-10,-10,-10,  5, 10, 10,-10},
      {-10,-10,-10,-10,-10,-10,  0,-10,-10},
      {-10,-10,-10,-10,-10,-10, -5,-10,-10}
    },{ // FURIvsI 美濃囲い、銀冠
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10, -3, -7,-10,-10,-10,-10,-10},
      {-10, -7,  4,  6,-10,-10,-10,  6,-10},
      {-10,  2,  3,  3,-10,-10,  4,-10,-10},
      {-10,-10,-10,  0,-10,-10,  0,-10,-10}
    },{ // NAKAvsI 中飛車
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,  8,  5,  8,-10,-10,-10},
      {-10,-10,  4,  4,  3,  4,  4,-10,-10},
      {-10,-10,  0,-10,-10,-10,  0,-10,-10}
    },{ // KAKUGAWARI
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,  7,  5, -3,-10,-10},
      {-10,  8, 10,  7,  4,  0, -4,-10,-10},
      {-10,  0,-8,  -4,-10,-10, -5,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10}
    },{ // AIGAKARI
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,  0,-10,-10,-10,-10,-10,-10},
      {-10, -5,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10}
    },{ // HUMEI
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,  5,-10,-10},
      {-10,-10,-10,-10,-10,-10, -4,  0,-10},
      {-10,-10,  0,-10,-10,-10, -4, -3,-10},
      {-10, -5,-10, -5,-10,-10, -5,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10}
    }
  };

  // 金
  static final int JosekiKomagumiSKI[][][]={
    { // IvsFURI
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,  1,  2,-10,-10,-10,-10},
      {-10,-10,-10,  0,-10, -4,-10,-10,-10}
    },{	// IvsNAKA
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,  1,  2,-10,-10,-10,-10},
      {-10,-10,-10,  0,-10, -4,-10,-10,-10}
    },{ // FURIvsFURI
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,  7, -3,-10,-10},
      {-10,-10,-10,-10,  5,  3,  6,-10,-10},
      {-10,-10,-10,-10,-10,  5,  4,-10,-10}
    },{ // FURIvsI
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,  5,  1,-10,-10},
      {-10,-10,-10,-10,  4,  3,  7, -3,-10},
      {-10,-10,-10,  0,  1,  5,  2, -7,-10}
    },{ // NAKAvsI
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10, -7, -4, -4,-10, -4, -4, -7,-10},
      {-10, -5, 10,  6,-10,  8, 10, -5,-10},
      {-10, -7, -6, -3, -6, -3, -6, -7,-10}
    },{ // KAKUGAWARI
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,  6, -4, -4, -4, -8,-10},
      {-10,-10, 10,-10,  3,  0,  0, -7,-10},
      {-10,-10,-10,  0,-10,  0, -5, -7,-10}
    },{ // AIGAKARI
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,  6,-10,-10,-10,-10,-10},
      {-10,-10, 10,-10,  3,-10,-10,-10,-10},
      {-10,-10,-10,  0,-10,  0,-10,-10,-10}
    },{ // HUMEI
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,  3,-10,  5,-10,-10,-10,-10},
      {-10,-10,-10,  0,-10,  0,-10,-10,-10}
    }
  };

  // 玉
  static final int JosekiKomagumiSOU[][][]={
    {
      // IvsFURI
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {- 7,  9,-10,-10,-10,-10,-10,-10,-10},
      {  5,  7,  8,  4,-10,-10,-10,-10,-10},
      { 10,  5,  3,-10,-10,-10,-10,-10,-10}
    },{	// IvsNAKA
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {- 7,  9,-10,-10,-10,-10,-10,-10,-10},
      {  5,  7,  8,  4,-10,-10,-10,-10,-10},
      { 10,  5,  3,-10,-10,-10,-10,-10,-10}
    },{ // FURIvsFURI
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,  4,  6, 10,  6},
      {-10,-10,-10,-10,-10,  4,  6,  5, 10}
    },{ // FURIvsI
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,  4,  6, 10,  6},
      {-10,-10,-10,-10,-10,  4,  6,  5, 10}
    },{ // NAKAvsI
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,  4,  6, 10,  6},
      {-10,-10,-10,-10,-10,  4,  6,  5, 10}
    },{ // KAKUGAWARI
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {- 3, -4, -3,-10,-10,-10,-10,-10,-10},
      {  6,  8, -2,  0, -3,-10,-10,-10,-10},
      { 10,  6, -4,- 6,- 7,-10,-10,-10,-10}
    },{ // AIGAKARI
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {- 3, -4, -3,-10,-10,-10,-10,-10,-10},
      {  6,  8,  0,- 4,-10,-10,-10,-10,-10},
      { 10,  6, -4,- 6,- 7,-10,-10,-10,-10}
    },{ // HUMEI
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {-10,-10,-10,-10,-10,-10,-10,-10,-10},
      {- 3, -4, -3,-10,-10,-10,-10,-10,-10},
      {  6,  8,  0,- 4,-10,-10,-10,-10,-10},
      { 10,  6, -4,- 6,- 7,-10,-10,-10,-10}
    }
  };
  
  // 各駒の駒組みによるボーナス点のテーブル
  static int JosekiKomagumi[][][]=new int[9][Koma.GRY+1][16*11];
  static int komagumiValue[][]=new int[Koma.GRY+1][16*11];
  
  public KyokumenKomagumi() {
  }
  
  public KyokumenKomagumi(Kyokumen k) {
    // 盤面のコピー
    for(int i=0;i<16*11;i++) {
      ban[i]=k.ban[i];
    }

    // 持ち駒のコピー
    for(int i=Koma.SFU;i<=Koma.GHI;i++) {
      hand[i]=k.hand[i];
    }
    
    // 手番のコピー
    teban=k.teban;
    
    // 評価値のコピー
    eval=k.eval;
    
    // 玉の位置のコピー
    kingS=k.kingS;
    kingG=k.kingG;

    // 必要な駒組みテーブルなどを初期化
    initTbl();
    senkeiInit();
    initShuubando();
    initBonus();
  }
  
  public void initTbl() {
    int suji,dan,koma;
    for(suji=0x10;suji<=0x90;suji+=0x10) {
      for(dan=1;dan<=9;dan++) {
        for(koma=Koma.SFU;koma<=Koma.GRY;koma++) {
          komagumiValue[koma][suji+dan]=0;
          JosekiKomagumi[0][koma][suji+dan]=DanValue[koma][dan];
        }
      }
    }
  }
  
  public void senkeiInit() {
    int SHI1,SHI2;
    int GHI1,GHI2;
    int SKA1,SKA2;
    int GKA1,GKA2;
    int suji,dan,koma;
    SHI1=SHI2=GHI1=GHI2=SKA1=SKA2=GKA1=GKA2=0;
    for(suji=0x10;suji<=0x90;suji+=0x10) {
      for(dan=1;dan<=9;dan++) {
        if (ban[suji+dan]==Koma.SHI) {
          if (SHI1==0) SHI1=suji+dan; else SHI2=suji+dan;
        }
        if (ban[suji+dan]==Koma.GHI) {
          if (GHI1==0) GHI1=suji+dan; else GHI2=suji+dan;
        }
        if (ban[suji+dan]==Koma.SKA) {
          if (SKA1==0) SKA1=suji+dan; else SKA2=suji+dan;
        }
        if (ban[suji+dan]==Koma.GKA) {
          if (GKA1==0) GKA1=suji+dan; else GKA2=suji+dan;
        }
      }
    }
    if (hand[Koma.SHI]==1) if (SHI1==0) SHI1=1; else SHI2=1;
    if (hand[Koma.SHI]==2) SHI1=SHI2=1;
    if (hand[Koma.GHI]==1) if (GHI1==0) GHI1=1; else GHI2=1;
    if (hand[Koma.GHI]==2) GHI1=GHI2=1;
    if (hand[Koma.SKA]==1) if (SKA1==0) SKA1=1; else SKA2=1;
    if (hand[Koma.SKA]==2) SKA1=SKA2=1;
    if (hand[Koma.GKA]==1) if (GKA1==0) GKA1=1; else GKA2=1;
    if (hand[Koma.GKA]==2) GKA1=GKA2=1;
    
    int Senkei,GyakuSenkei;
    if (SHI1<=0x50 && GHI1<=0x50) {
      Senkei=IvsFURI;
      GyakuSenkei=FURIvsI;
    } else if (0x50<=GHI1 && GHI1<=0x5f && SHI1<=0x50) {
      Senkei=IvsNAKA;
      GyakuSenkei=NAKAvsI;
    } else if (SHI1<=0x5f && GHI1<=0x5f) {
      Senkei=FURIvsFURI;
      GyakuSenkei=FURIvsFURI;
    } else if (GHI1>=0x60 && SHI1>=0x60) {
      Senkei=FURIvsI;
      GyakuSenkei=IvsFURI;
    } else if (0x50<=SHI1 && SHI1<=0x5f && GHI1<=0x50) {
      Senkei=NAKAvsI;
      GyakuSenkei=IvsNAKA;
    } else if (SKA1==1 && GKA1==1) {
      Senkei=KAKUGAWARI;
      GyakuSenkei=KAKUGAWARI;
    } else if (0x20<=SHI1 && SHI1<=0x2f && 0x80<=GHI1 && GHI1<=0x8f) {
      Senkei=AIGAKARI;
      GyakuSenkei=AIGAKARI;
    } else {
      Senkei=HUMEI;
      GyakuSenkei=HUMEI;
    }
    for(suji=0x10;suji<=0x90;suji+=0x10) {
      for(dan=1;dan<=9;dan++) {
        eval-=komagumiValue[ban[suji+dan]][suji+dan];
        for(koma=Koma.SFU;koma<=Koma.GRY;koma++) {
          if (koma==Koma.SGI) {
            JosekiKomagumi[Senkei][koma][suji+dan]=JosekiKomagumiSGI[Senkei][dan-1][9-(suji/0x10)];
          } else if (koma==Koma.GGI) {
            JosekiKomagumi[Senkei][koma][suji+dan]=-JosekiKomagumiSGI[GyakuSenkei][9-dan][suji/0x10-1];
          } else if (koma==Koma.SKI) {
            JosekiKomagumi[Senkei][koma][suji+dan]=JosekiKomagumiSKI[Senkei][dan-1][9-(suji/0x10)];
          } else if (koma==Koma.GKI) {
            JosekiKomagumi[Senkei][koma][suji+dan]=-JosekiKomagumiSKI[GyakuSenkei][9-dan][suji/0x10-1];
          } else if (koma==Koma.SOU) {
            JosekiKomagumi[Senkei][koma][suji+dan]=JosekiKomagumiSOU[Senkei][dan-1][9-(suji/0x10)];
          } else if (koma==Koma.GOU) {
            JosekiKomagumi[Senkei][koma][suji+dan]=-JosekiKomagumiSOU[GyakuSenkei][9-dan][suji/0x10-1];
          } else {
            JosekiKomagumi[Senkei][koma][suji+dan]=DanValue[koma][dan];
          }
          komagumiValue[koma][suji+dan]=JosekiKomagumi[Senkei][koma][suji+dan];
        }
        eval+=komagumiValue[ban[suji+dan]][suji+dan];
      }
    }
  }

  // 自玉近くの自分の金銀の価値
  static final int Mamorigoma[][]={
    { 50, 50, 50, 50, 50, 50, 50, 50, 50},
    { 56, 52, 50, 50, 50, 50, 50, 50, 50},
    { 64, 61, 55, 50, 50, 50, 50, 50, 50},
    { 79, 77, 70, 65, 54, 51, 50, 50, 50},
    {100, 99, 95, 87, 74, 58, 50, 50, 50},
    {116,117,101, 95, 88, 67, 54, 50, 50},
    {131,129,124,114, 90, 71, 59, 51, 50},
    {137,138,132,116, 96, 76, 61, 53, 50},
    {142,142,136,118, 98, 79, 64, 52, 50},
    {132,132,129,109, 95, 75, 60, 51, 50},
    {121,120,105, 97, 84, 66, 54, 50, 50},
    { 95, 93, 89, 75, 68, 58, 51, 50, 50},
    { 79, 76, 69, 60, 53, 50, 50, 50, 50},
    { 64, 61, 55, 51, 50, 50, 50, 50, 50},
    { 56, 52, 50, 50, 50, 50, 50, 50, 50},
    { 50, 50, 50, 50, 50, 50, 50, 50, 50},
    { 50, 50, 50, 50, 50, 50, 50, 50, 50},
  };

  // 相手玉近くの自分の金銀の価値
  static final int Semegoma[][]={
    { 50, 50, 50, 50, 50, 50, 50, 50, 50},
    { 50, 50, 50, 50, 50, 50, 50, 50, 50},
    { 50, 50, 50, 50, 50, 50, 50, 50, 50},
    { 54, 53, 51, 51, 50, 50, 50, 50, 50},
    { 70, 66, 62, 55, 53, 50, 50, 50, 50},
    { 90, 85, 80, 68, 68, 60, 53, 50, 50},
    {100, 97, 95, 85, 84, 71, 51, 50, 50},
    {132,132,129,102, 95, 71, 51, 50, 50},
    {180,145,137,115, 91, 75, 57, 50, 50},
    {170,165,150,121, 94, 78, 58, 52, 50},
    {170,160,142,114, 98, 80, 62, 55, 50},
    {140,130,110,100, 95, 75, 54, 50, 50},
    {100, 99, 95, 87, 78, 69, 50, 50, 50},
    { 80, 78, 72, 67, 55, 51, 50, 50, 50},
    { 62, 60, 58, 52, 50, 50, 50, 50, 50},
    { 50, 50, 50, 50, 50, 50, 50, 50, 50},
    { 50, 50, 50, 50, 50, 50, 50, 50, 50},
  };
  
  // 絶対値を求める。
  static int abs(int x) {
    if (x<0) return -x;
    return x;
  }
  
  // 金駒の価値の計算をする。
  void initKanagomaValue() {
    for(int kingSdan=1;kingSdan<=9;kingSdan++) {
      for(int kingSsuji=0x10;kingSsuji<=0x90;kingSsuji+=0x10) {
        for(int kingEdan=1;kingEdan<=9;kingEdan++) {
          for(int kingEsuji=0x10;kingEsuji<=0x90;kingEsuji+=0x10) {
            for(int suji=0x10;suji<=0x90;suji+=0x10) {
              for(int dan=1;dan<=9;dan++) {
                int DiffSujiS=abs(kingSsuji-suji)/0x10;
                int DiffSujiE=abs(kingEsuji-suji)/0x10;
                int DiffDanSS=8+(dan-kingSdan);
                int DiffDanES=8+(dan-kingEdan);
                int DiffDanSE=8+(-(dan-kingSdan));
                int DiffDanEE=8+(-(dan-kingEdan));
                int kingS=kingSsuji+kingSdan;
                int kingE=kingEsuji+kingEdan;
                
                SemegomaValueS[suji+dan][kingE]=Semegoma[DiffDanES][DiffSujiE]-100;
                MamorigomaValueS[suji+dan][kingS]=Mamorigoma[DiffDanSS][DiffSujiS]-100;
                SemegomaValueE[suji+dan][kingS]=-(Semegoma[DiffDanSE][DiffSujiS]-100);
                MamorigomaValueE[suji+dan][kingE]=-(Mamorigoma[DiffDanEE][DiffSujiE]-100);
              }
            }
          }
        }
      }
    }
  }

  // 攻め駒が近付くと終盤度がこれだけあがる。
  static final int ShuubandoByAtack[]={
  //空歩香桂銀金角飛王と杏圭全金馬龍
    0,1,1,2,3,3,3,4,4,3,3,3,3,3,4,5
  };
                            
  // 守り駒がいれば、終盤度がこれだけ下がる
  static final int ShuubandoByDefence[]={
  //空歩香桂銀 金角 飛王 と 杏 圭 全 金 馬 龍
    0,0,0,0,-1,-1,0,-1,0,-1,-1,-1,-1,-1,-2,-1
  };

  // 手持ちの駒による終盤度の上昇
  static final int ShuubandoByHand[]={
  //空歩香桂銀金角飛王と杏圭全金馬龍
    0,0,1,1,2,2,2,3,0,0,0,0,0,0,0,0
  };
  
  static int SemegomaValueS[][]=new int[16*11][16*11];
  static int SemegomaValueE[][]=new int[16*11][16*11];
  static int MamorigomaValueS[][]=new int[16*11][16*11];
  static int MamorigomaValueE[][]=new int[16*11][16*11];
  
  static {
    for(int kingSdan=1;kingSdan<=9;kingSdan++) {
      for(int kingSsuji=0x10;kingSsuji<=0x90;kingSsuji+=0x10) {
        for(int kingEdan=1;kingEdan<=9;kingEdan++) {
          for(int kingEsuji=0x10;kingEsuji<=0x90;kingEsuji+=0x10) {
            for(int suji=0x10;suji<=0x90;suji+=0x10) {
              for(int dan=1;dan<=9;dan++) {
                int DiffSujiS=abs(kingSsuji-suji)/0x10;
                int DiffSujiE=abs(kingEsuji-suji)/0x10;
                int DiffDanSS=8+(dan-kingSdan);
                int DiffDanES=8+(dan-kingEdan);
                int DiffDanSE=8+(-(dan-kingSdan));
                int DiffDanEE=8+(-(dan-kingEdan));
                int kingS=kingSsuji+kingSdan;
                int kingE=kingEsuji+kingEdan;
                
                SemegomaValueS[suji+dan][kingE]=Semegoma[DiffDanES][DiffSujiE]-100;
                MamorigomaValueS[suji+dan][kingS]=Mamorigoma[DiffDanSS][DiffSujiS]-100;
                SemegomaValueE[suji+dan][kingS]=-(Semegoma[DiffDanSE][DiffSujiS]-100);
                MamorigomaValueE[suji+dan][kingE]=-(Mamorigoma[DiffDanEE][DiffSujiE]-100);
              }
            }
          }
        }
      }
    }
  
  }
  
  int Shuubando[]=new int[2];
  int SemegomaBonus[]=new int[2];
  int MamorigomaBonus[]=new int[2];

  // 終盤度の計算
  void initShuubando() {
    // 終盤度を求めると同時に、終盤度によるボーナスの付加、駒の加点も行う。
    int suji,dan;
    Shuubando[0]=0;
    Shuubando[1]=0;
    for(suji=0x10;suji<=0x90;suji+=0x10) {
      for(dan=1;dan<=4;dan++) {
        if (Koma.isSente(ban[suji+dan])) {
          Shuubando[1]+=ShuubandoByAtack[ban[suji+dan] & ~SENTE];
        }
        if (Koma.isGote(ban[suji+dan])) {
          Shuubando[1]+=ShuubandoByDefence[ban[suji+dan] & ~GOTE];
        }
      }
      for(dan=6;dan<=9;dan++) {
        if (Koma.isGote(ban[suji+dan])) {
          Shuubando[0]+=ShuubandoByAtack[ban[suji+dan] & ~GOTE];
        }
        if (Koma.isSente(ban[suji+dan])) {
          Shuubando[0]+=ShuubandoByDefence[ban[suji+dan] & ~SENTE];
        }
      }
    }
    int koma;
    for(koma=Koma.FU;koma<=Koma.HI;koma++) {
      Shuubando[0]+=ShuubandoByHand[koma]*hand[GOTE|koma];
      Shuubando[1]+=ShuubandoByHand[koma]*hand[SENTE|koma];
    }
  }
  
  static final int IsKanagoma[]={
//  空空空空空空空空空空空空空空空空空歩香桂銀金角飛王と杏圭全金馬龍
    0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,1,1,1,1,1,0,0,
//  空歩香桂銀金角飛王と杏圭全金馬龍壁空空空空空空空空空空空空空空空
    0,0,0,0,1,1,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
  };
  
  void initBonus() {
    int suji,dan;
    SemegomaBonus[0]=SemegomaBonus[1]=0;
    MamorigomaBonus[0]=MamorigomaBonus[1]=0;
    
    for(suji=0x10;suji<=0x90;suji+=0x10) {
      for(dan=1;dan<=9;dan++) {
        if (IsKanagoma[ban[suji+dan]]!=0) {
          if (Koma.isSente(ban[suji+dan])) {
            SemegomaBonus[0]+=SemegomaValueS[suji+dan][kingG];
            MamorigomaBonus[0]+=MamorigomaValueS[suji+dan][kingS];
          } else {
            SemegomaBonus[1]+=SemegomaValueE[suji+dan][kingS];
            MamorigomaBonus[1]+=MamorigomaValueE[suji+dan][kingG];
          }
        }
      }
    }
  }
  
  // 必要な変数を初期化する。
  public void initAll() {
    initTbl();
    senkeiInit();
    initShuubando();
    initBonus();
    super.initAll();
  }
  
  public void move(Te te) {
    int self,enemy;
    if (Koma.isSente(te.koma)) {
      self=0;
      enemy=1;
    } else {
      self=1;
      enemy=0;
    }
    if (te.koma==Koma.SOU || te.koma==Koma.GOU) {
    } else {
      if (IsKanagoma[te.koma]!=0 && te.from>0) {
        if (self==0) {
          SemegomaBonus[0]-=SemegomaValueS[te.from][kingG];
          MamorigomaBonus[0]-=MamorigomaValueS[te.from][kingS];
        } else {
          SemegomaBonus[1]-=SemegomaValueE[te.from][kingS];
          MamorigomaBonus[1]-=MamorigomaValueE[te.from][kingG];
        }
      }
      if (te.capture!=Koma.EMPTY) {
        if (IsKanagoma[te.capture]!=0) {
          if (self==0) {
            SemegomaBonus[1]-=SemegomaValueE[te.to][kingS];
            MamorigomaBonus[1]-=MamorigomaValueE[te.to][kingG];
          } else {
            SemegomaBonus[0]-=SemegomaValueS[te.to][kingG];
            MamorigomaBonus[0]-=MamorigomaValueS[te.to][kingS];
          }
        }
      }
      if (!te.promote) {
        if (IsKanagoma[te.koma]!=0) {
          if (self==0) {
            SemegomaBonus[0]+=SemegomaValueS[te.to][kingG];
            MamorigomaBonus[0]+=MamorigomaValueS[te.to][kingS];
          } else {
            SemegomaBonus[1]+=SemegomaValueE[te.to][kingS];
            MamorigomaBonus[1]+=MamorigomaValueE[te.to][kingG];
          }
        }
      } else {
        if (IsKanagoma[te.koma|Koma.PROMOTE]!=0) {
          if (self==0) {
            SemegomaBonus[0]+=SemegomaValueS[te.to][kingG];
            MamorigomaBonus[0]+=MamorigomaValueS[te.to][kingS];
          } else {
            SemegomaBonus[1]+=SemegomaValueE[te.to][kingS];
            MamorigomaBonus[1]+=MamorigomaValueE[te.to][kingG];
          }
        }
      }
    }

    if (te.from>0 && (te.from&0x0f)<=4) {
      // ４段目以下・終盤度の計算
      if (self==0) {
        Shuubando[1]-=ShuubandoByAtack[te.koma & ~SENTE];
      } else {
        Shuubando[1]-=ShuubandoByDefence[te.koma & ~GOTE];
      }
    }
    if (te.from>0 && (te.from&0x0f)>=6) {
      // ６段目以上・終盤度の計算
      if (self==0) {
        Shuubando[0]-=ShuubandoByDefence[te.koma & ~SENTE];
      } else {
        Shuubando[0]-=ShuubandoByAtack[te.koma & ~GOTE];
      }
    }
    if (te.from==0) {
      // 打つことによる終盤度の減少
      if (self==0) {
        Shuubando[1]-=ShuubandoByHand[te.koma&~SENTE];
      } else {
        Shuubando[0]-=ShuubandoByHand[te.koma&~GOTE];
      }
    }
    if (te.capture!=Koma.EMPTY) {
      if ((te.to&0x0f)<=4) {
        // ４段目以下・終盤度の計算
        if (self==0) {
          Shuubando[1]-=ShuubandoByDefence[te.capture & ~GOTE];
        } else {
          Shuubando[1]-=ShuubandoByAtack[te.capture & ~SENTE];
        }
      }
      if ((te.to&0x0f)>=6) {
        // ６段目以上・終盤度の計算
        if (self==0) {
          Shuubando[0]-=ShuubandoByAtack[te.capture & ~GOTE];
        } else {
          Shuubando[0]-=ShuubandoByDefence[te.capture & ~SENTE];
        }
      }
      // Handに入ったことによる終盤度の計算
      if (self==0) {
        Shuubando[1]+=ShuubandoByHand[te.capture&~GOTE&~Koma.PROMOTE];
      } else {
        Shuubando[0]+=ShuubandoByHand[te.capture&~SENTE&~Koma.PROMOTE];
      }
    }
    if (!te.promote) {
      if ((te.to&0x0f)<=4) {
        // ４段目以下・終盤度の計算
        if (self==0) {
          Shuubando[1]+=ShuubandoByAtack[te.koma & ~SENTE];
        } else {
          Shuubando[1]+=ShuubandoByDefence[te.koma & ~GOTE];
        }
      }
      if ((te.to&0x0f)>=6) {
        // ６段目以上・終盤度の計算
        if (self==0) {
          Shuubando[0]+=ShuubandoByDefence[te.koma & ~SENTE];
        } else {
          Shuubando[0]+=ShuubandoByAtack[te.koma & ~GOTE];
        }
      }
    } else {
      if ((te.to&0x0f)<=4) {
        // ４段目以下・終盤度の計算
        if (self==0) {
          Shuubando[1]+=ShuubandoByAtack[(te.koma|Koma.PROMOTE) & ~SENTE];
        } else {
          Shuubando[1]+=ShuubandoByDefence[(te.koma|Koma.PROMOTE) & ~GOTE];
        }
      }
      if ((te.to&0x0f)>=6) {
        // ６段目以上・終盤度の計算
        if (self==0) {
          Shuubando[0]+=ShuubandoByDefence[(te.koma|Koma.PROMOTE) & ~SENTE];
        } else {
          Shuubando[0]+=ShuubandoByAtack[(te.koma|Koma.PROMOTE) & ~GOTE];
        }
      }
    }

    
    eval-=komagumiValue[te.koma][te.from];
    if (te.capture!=Koma.EMPTY) {
      eval-=komagumiValue[te.capture][te.to];
    }
    if (!te.promote) {
      eval+=komagumiValue[te.koma][te.to];
    } else {
      eval+=komagumiValue[te.koma|Koma.PROMOTE][te.to];
    }
    super.move(te);
    if (te.koma==Koma.SOU || te.koma==Koma.GOU) {
      // 全面的に金駒のBonusの計算しなおし。
      initBonus();
    }
  }
  
  public void back(Te te) {
    int self,enemy;
    if (Koma.isSente(te.koma)) {
      self=0;
      enemy=1;
    } else {
      self=1;
      enemy=0;
    }
    if (te.koma==Koma.SOU || te.koma==Koma.GOU) {
    } else {
      if (IsKanagoma[te.koma]!=0 && te.from>0) {
        if (self==0) {
          SemegomaBonus[0]+=SemegomaValueS[te.from][kingG];
          MamorigomaBonus[0]+=MamorigomaValueS[te.from][kingS];
        } else {
          SemegomaBonus[1]+=SemegomaValueE[te.from][kingS];
          MamorigomaBonus[1]+=MamorigomaValueE[te.from][kingG];
        }
      }
      if (te.capture!=Koma.EMPTY) {
        if (IsKanagoma[te.capture]!=0) {
          if (self==0) {
            SemegomaBonus[1]+=SemegomaValueE[te.to][kingS];
            MamorigomaBonus[1]+=MamorigomaValueE[te.to][kingG];
          } else {
            SemegomaBonus[0]+=SemegomaValueS[te.to][kingG];
            MamorigomaBonus[0]+=MamorigomaValueS[te.to][kingS];
          }
        }
      }
      if (!te.promote) {
        if (IsKanagoma[te.koma]!=0) {
          if (self==0) {
            SemegomaBonus[0]-=SemegomaValueS[te.to][kingG];
            MamorigomaBonus[0]-=MamorigomaValueS[te.to][kingS];
          } else {
            SemegomaBonus[1]-=SemegomaValueE[te.to][kingS];
            MamorigomaBonus[1]-=MamorigomaValueE[te.to][kingG];
          }
        }
      } else {
        if (IsKanagoma[te.koma|Koma.PROMOTE]!=0) {
          if (self==0) {
            SemegomaBonus[0]-=SemegomaValueS[te.to][kingG];
            MamorigomaBonus[0]-=MamorigomaValueS[te.to][kingS];
          } else {
            SemegomaBonus[1]-=SemegomaValueE[te.to][kingS];
            MamorigomaBonus[1]-=MamorigomaValueE[te.to][kingG];
          }
        }
      }
    }

    if (te.from>0 && (te.from&0x0f)<=4) {
      // ４段目以下・終盤度の計算
      if (self==0) {
        Shuubando[1]+=ShuubandoByAtack[te.koma & ~SENTE];
      } else {
        Shuubando[1]+=ShuubandoByDefence[te.koma & ~GOTE];
      }
    }
    if (te.from>0 && (te.from&0x0f)>=6) {
      // ６段目以上・終盤度の計算
      if (self==0) {
        Shuubando[0]+=ShuubandoByDefence[te.koma & ~SENTE];
      } else {
        Shuubando[0]+=ShuubandoByAtack[te.koma & ~GOTE];
      }
    }
    if (te.from==0) {
      // 打つことによる終盤度の減少
      if (self==0) {
        Shuubando[1]+=ShuubandoByHand[te.koma&~SENTE];
      } else {
        Shuubando[0]+=ShuubandoByHand[te.koma&~GOTE];
      }
    }
    if (te.capture!=Koma.EMPTY) {
      if ((te.to&0x0f)<=4) {
        // ４段目以下・終盤度の計算
        if (self==0) {
          Shuubando[1]+=ShuubandoByDefence[te.capture & ~GOTE];
        } else {
          Shuubando[1]+=ShuubandoByAtack[te.capture & ~SENTE];
        }
      }
      if ((te.to&0x0f)>=6) {
        // ６段目以上・終盤度の計算
        if (self==0) {
          Shuubando[0]+=ShuubandoByAtack[te.capture & ~GOTE];
        } else {
          Shuubando[0]+=ShuubandoByDefence[te.capture & ~SENTE];
        }
      }
      // Handに入ったことによる終盤度の計算
      if (self==0) {
        Shuubando[1]-=ShuubandoByHand[te.capture&~GOTE&~Koma.PROMOTE];
      } else {
        Shuubando[0]-=ShuubandoByHand[te.capture&~SENTE&~Koma.PROMOTE];
      }
    }
    if (!te.promote) {
      if ((te.to&0x0f)<=4) {
        // ４段目以下・終盤度の計算
        if (self==0) {
          Shuubando[1]-=ShuubandoByAtack[te.koma & ~SENTE];
        } else {
          Shuubando[1]-=ShuubandoByDefence[te.koma & ~GOTE];
        }
      }
      if ((te.to&0x0f)>=6) {
        // ６段目以上・終盤度の計算
        if (self==0) {
          Shuubando[0]-=ShuubandoByDefence[te.koma & ~SENTE];
        } else {
          Shuubando[0]-=ShuubandoByAtack[te.koma & ~GOTE];
        }
      }
    } else {
      if ((te.to&0x0f)<=4) {
        // ４段目以下・終盤度の計算
        if (self==0) {
          Shuubando[1]-=ShuubandoByAtack[(te.koma|Koma.PROMOTE) & ~SENTE];
        } else {
          Shuubando[1]-=ShuubandoByDefence[(te.koma|Koma.PROMOTE) & ~GOTE];
        }
      }
      if ((te.to&0x0f)>=6) {
        // ６段目以上・終盤度の計算
        if (self==0) {
          Shuubando[0]-=ShuubandoByDefence[(te.koma|Koma.PROMOTE) & ~SENTE];
        } else {
          Shuubando[0]-=ShuubandoByAtack[(te.koma|Koma.PROMOTE) & ~GOTE];
        }
      }
    }

    eval+=komagumiValue[te.koma][te.from];
    if (te.capture!=Koma.EMPTY) {
      eval+=komagumiValue[te.capture][te.to];
    }
    if (!te.promote) {
      eval-=komagumiValue[te.koma][te.to];
    } else {
      eval-=komagumiValue[te.koma|Koma.PROMOTE][te.to];
    }
    super.back(te);
    if (te.koma==Koma.SOU || te.koma==Koma.GOU) {
      // 全面的に金駒のBonusの計算しなおし。
      initBonus();
    }
  }
  
  // 局面を評価する関数。
  public int evaluate() {
    // 終盤度を０〜１６の範囲に補正する。
    int Shuubando0,Shuubando1;
    
    if (Shuubando[0]<0) {
      Shuubando0=0;
    } else if (Shuubando[0]>16) {
      Shuubando0=16;
    } else {
      Shuubando0=Shuubando[0];
    }
    if (Shuubando[1]<0) {
      Shuubando1=0;
    } else if (Shuubando[1]>16) {
      Shuubando1=16;
    } else {
      Shuubando1=Shuubando[1];
    }
    int ret=0;

    ret+=SemegomaBonus[0]*Shuubando1/16;
    ret+=MamorigomaBonus[0]*Shuubando0/16;
    ret+=SemegomaBonus[1]*Shuubando0/16;
    ret+=MamorigomaBonus[1]*Shuubando1/16;
    
    
    return eval+ret;
  }
}
