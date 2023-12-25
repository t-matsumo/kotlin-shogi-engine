package jp.usapyonsoft.lesserpyon;
import java.util.Vector;

// コンピュータの思考ルーチン
public class Sikou implements Player,Constants,Runnable {
  // ∞を表すための定数
  static final int INFINITE=99999999;

  static final int STOPPED=111111111;

  // 読みの深さ
  static final int DEPTH_MAX=6;
  // 読みの最大深さ…これ以上の読みは絶対に不可能。
  static final int LIMIT_DEPTH=16;

  // 最大読み手数
  static final int teMax[]={50,50,32,32,24,24,16,16,16,16,16,16,16,16,16,16};

  // αβカットを起こす関係で、ランダム着手は出来なくなる。
  // 詳細は解説にて。

  // 最善手順を格納する配列
  public Te best[][]=new Te[LIMIT_DEPTH][LIMIT_DEPTH];

  // ここまでの探索手順を格納する配列
  public Te stack[]=new Te[LIMIT_DEPTH];

  // この思考用のTransPositionTable
  TranspositionTable tt=new TranspositionTable();

  int leaf=0;
  int node=0;

  // 定跡があれば定跡を利用。
  Joseki joseki;

  // 裏側で思考するための盤
  KyokumenKomagumi kk;

  // 予測読みを開始した盤
  KyokumenKomagumi yosoku;

  // 予測読みをするスレッド
  Thread sikou_ura;

  // 『手の評価』で使う手の配列
  Te teS[]={
    new Te(),new Te(),new Te(),new Te(),new Te(),
    new Te(),new Te(),new Te(),new Te(),new Te(),
    new Te(),new Te(),new Te(),new Te(),new Te(),
    new Te(),new Te(),new Te(),new Te(),new Te()
  };
  // 後手側
  Te teE[]={
    new Te(),new Te(),new Te(),new Te(),new Te(),
    new Te(),new Te(),new Te(),new Te(),new Te(),
    new Te(),new Te(),new Te(),new Te(),new Te(),
    new Te(),new Te(),new Te(),new Te(),new Te()
  };

  // 思考を止める。
  volatile boolean stop;
  volatile boolean stopped;
  // 裏思考が終了している。
  volatile boolean processed;

  // 思考を開始した時間
  long time;

  long sikoutime;

  public Sikou() {
    joseki=new Joseki("joseki.bin");
  }

  int min(int x,int y) {
    if (x<y) return x;
    return y;
  }

  int negaMax(Te t,Kyokumen k,int alpha,int beta,int depth,int depthMax,
      boolean bITDeep) {
    // 深さが最大深さに達していたらそこでの評価値を返して終了。
    if (depth>=depthMax) {
      leaf++;
      if (k.teban==SENTE) {
        return k.evaluate();
      } else {
        return -k.evaluate();
      }
    }
    if (stop) {
      return STOPPED;
    }
    node++;
    TTEntry e=tt.get(k.HashVal);

    if (e!=null) {
      if (e.value>=beta && e.depth<=depth && e.remainDepth>=depthMax-depth &&
          e.flag!=TTEntry.UPPER_BOUND) {
        return e.value;
      }
      if (e.value<=alpha && e.depth<=depth && e.remainDepth>=depthMax-depth &&
          e.flag!=TTEntry.LOWER_BOUND) {
        return e.value;
      }
    }
    if (e==null && depthMax-depth>2 && bITDeep) {
      return ITDeep(k,alpha,beta,depth,depthMax);
    }

    // 現在の指し手の候補手の評価値を入れる。
    int value=-INFINITE;

    // 最初に軽い手の生成をしてみる。
    Vector v=GenerateMoves.makeMoveFirst(k,depth,this,e);

    for(int i=0;i<v.size();i++) {
      // 合法手を取り出す。
      Te te=(Te)v.elementAt(i);

      // その手で一手進める。
      stack[depth]=te;
      k.move(te);
      // moveでは、先手後手を入れ替えないので…。
      if (k.teban==SENTE) {
        k.teban=GOTE;
      } else {
        k.teban=SENTE;
      }

      // その局面の評価値を、さらに先読みして得る。
      Te tmpTe=new Te(0,0,0,false,0);
      int eval=-negaMax(tmpTe,k,-beta,-alpha,depth+1,depthMax,true);
      k.back(te);
      if (stop) return STOPPED;
      // backでは、先手後手を入れ替えないので…。
      if (k.teban==SENTE) {
        k.teban=GOTE;
      } else {
        k.teban=SENTE;
      }

      // 指した手で進めた局面が、今までよりもっと大きな値を返すか？
      if (eval>value) {
        // 返す値を更新
        value=eval;
        // α値も更新
        if (eval>alpha) {
          alpha=eval;
        }
        // 最善手を更新
        best[depth][depth]=te;
        t.koma   =te.koma;
        t.from   =te.from;
        t.to     =te.to;
        t.promote=te.promote;
        // 最善手順を更新
        for(int j=depth+1;j<depthMax;j++) {
          best[depth][j]=best[depth+1][j];
        }
        if (depth==0) {
          System.out.print("経過時間"+(System.currentTimeMillis()-time)+"ms  評価値:"+value);
          System.out.print("  最善手順:");
          for(int j=0;j<depthMax;j++) {
            System.out.print(best[0][j]);
          }
          System.out.println();
        }
        // βカットの条件を満たしていたら、ループ終了。
        if (eval>=beta) {
          tt.add(k.HashVal,value,alpha,beta,best[depth][depth],
              depth,depthMax-depth,0);
          return eval;
        }
      }
      if (depth==0 && value>-INFINITE && System.currentTimeMillis()-time>sikoutime) {
        break;
      }
    }

    // 現在の局面での合法手を生成
    v=GenerateMoves.generateLegalMoves(k);

    GenerateMoves.evaluateTe(k,v,teS,teE);

    // 合法手の中から、一手指してみて、一番よかった指し手を選択。
    for(int i=0;i<v.size();i++) {
      // 合法手を取り出す。
      Te te=(Te)v.elementAt(i);
      if ((te.value<-100 || i>teMax[depth]) && value>-INFINITE) {
        break;
      }

      // その手で一手進める。
      stack[depth]=te;
      k.move(te);
      // moveでは、先手後手を入れ替えないので…。
      if (k.teban==SENTE) {
        k.teban=GOTE;
      } else {
        k.teban=SENTE;
      }

      // その局面の評価値を、さらに先読みして得る。
      Te tmpTe=new Te(0,0,0,false,0);
      int eval=-negaMax(tmpTe,k,-beta,-alpha,depth+1,depthMax,true);
      k.back(te);
      if (depth>1 && te.to==stack[depth-1].to && stack[depth-1].value2<-100 && eval<beta) {
        // 水平線効果くさい手…
        // 延長探索
        /*
        System.out.print("延長探索: eval="+eval+" ");
        for(int j=0;j<=depth;j++){
          if (j==depth-1) {
            System.out.print("＊");
          }
          System.out.print(stack[j]);
          if (j==depth-1) {
            System.out.print("："+stack[depth-1].value2);
          }
        }
        for(int j=depth+1;j<depthMax;j++) {
          System.out.print(best[depth][j]);
        }
        System.out.println();
        */
        k.move(te);
        int enchou=-negaMax(tmpTe,k,-beta,-beta+1,depth+1,min(depthMax+2,LIMIT_DEPTH),true);
        if (enchou>beta) {
          eval=enchou;
        }
        k.back(te);
      }
      if (stop) return STOPPED;
      // backでは、先手後手を入れ替えないので…。
      if (k.teban==SENTE) {
        k.teban=GOTE;
      } else {
        k.teban=SENTE;
      }

      // 指した手で進めた局面が、今までよりもっと大きな値を返すか？
      if (eval>value) {
        // 返す値を更新
        value=eval;
        // α値も更新
        if (eval>alpha) {
          alpha=eval;
        }
        // 最善手を更新
        best[depth][depth]=te;
        t.koma   =te.koma;
        t.from   =te.from;
        t.to     =te.to;
        t.promote=te.promote;
        // 最善手順を更新
        for(int j=depth+1;j<depthMax;j++) {
          best[depth][j]=best[depth+1][j];
        }
        if (depth==0) {
          System.out.print("経過時間"+(System.currentTimeMillis()-time)+"ms  評価値:"+value);
          System.out.print("  最善手順:");
          for(int j=0;j<depthMax;j++) {
            System.out.print(best[0][j]);
          }
          System.out.println();
        }
        // βカットの条件を満たしていたら、ループ終了。
        if (eval>=beta) {
          break;
        }
      }
      if (depth==0 && value>-INFINITE && System.currentTimeMillis()-time>sikoutime) {
        break;
      }
    }
    tt.add(k.HashVal,value,alpha,beta,best[depth][depth],
        depth,depthMax-depth,0);
    return value;
  }

  int ITDeep(Kyokumen k,int alpha,int beta,int depth,int depthMax) {
    if (depth==0) {
      time=System.currentTimeMillis();
    }
    int retval=-INFINITE;
    int i;
    Te te=new Te(0,0,0,false,0);
    for(i=depth+1;i<=depthMax && !stop ;i++) {
      retval=negaMax(te,k,alpha,beta,depth,i,false);
      if (depth==0 && System.currentTimeMillis()-time>sikoutime) {
        break;
      }
    }
    if (depth==0) {
      if (stop) {
        stop=false;
        stopped=true;
      } else {
        processed=true;
      }
    }
    return retval;
  }

  public Te getNextTe(Kyokumen k,int tesu,int spenttime,int limittime,int byoyomi) {
    leaf=node=0;

    Te te;

    if (limittime-spenttime<120) {
      sikoutime=1800; // 1.8秒
    } else if (limittime-spenttime<180) {
      sikoutime=18000; // 18秒
    } else {
      sikoutime=40000; // 40秒
    }

    if ((te=joseki.fromJoseki(k,tesu))!=null) {
      System.out.println("定跡より:"+te.toString());
      return te;
    }

    if (yosoku!=null && yosoku.equals(k)) {
      // 予測が当たっているので、このまま進める。
      System.out.println("予測あたり");
      while(!processed) {
        try {
          Thread.sleep(100);
        }catch(Exception e){
        }
      }
      te=best[0][0];
    } else {
      if (sikou_ura!=null && !processed) {
        stop=true;
        while(!stopped) {
          try {
            Thread.sleep(100);
          }catch(Exception e){
          }
        }
      }
      kk=new KyokumenKomagumi(k);

      // 評価値最大の手を得る
      // 投了にあたるような手で初期化。
      te=new Te(0,0,0,false,0);
      int v=ITDeep(kk,-INFINITE,INFINITE,0,DEPTH_MAX);
      if (v>-INFINITE) {
        te=best[0][0];
      }
    }
    System.out.print("  最善手順:");
    for(int i=0;i<DEPTH_MAX;i++) {
      System.out.print(best[0][i]);
    }
    System.out.println();

    time=System.currentTimeMillis()-time;
    System.out.println("leaf="+leaf+" node="+node+" time="+time+"ms");

    // 自分の手と、予測した相手の手で先に進める。
    kk.move(best[0][0]);
    kk.move(best[0][1]);
    yosoku=new KyokumenKomagumi((Kyokumen)kk.clone());
    sikou_ura=new Thread(this);
    sikou_ura.start();

    return te;
  }

  // 相手の思考中に考える。
  public void run() {
    processed=false;
    stop=false;
    stopped=false;
    int v=ITDeep(kk,-INFINITE,INFINITE,0,DEPTH_MAX);
    sikou_ura=null;
  }
}
