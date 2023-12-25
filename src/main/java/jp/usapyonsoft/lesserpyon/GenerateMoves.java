package jp.usapyonsoft.lesserpyon;
import java.util.Vector;

public class GenerateMoves implements Constants,KomaMoves {

  // 各手について、自分の玉に王手がかかっていないかどうかチェックし、
  // 王手がかかっている手は取り除く。
  public static Vector removeSelfMate(Kyokumen k,Vector v) {
    Vector removed=new Vector();
    for(int i=0;i<v.size();i++) {
      // 手を取り出す。
      Te te=(Te)v.elementAt(i);

      // その手で１手進めてみる
      Kyokumen test=(Kyokumen)k.clone();
      test.move(te);

      // 自玉を探す

      int gyokuPosition=test.searchGyoku(k.teban);

      // 王手放置しているかどうかフラグ
      boolean isOuteHouchi=false;

      // 玉の周辺（１２方向）から相手の駒が利いていたら、その手は取り除く
      for(int direct=0;direct<12 && !isOuteHouchi;direct++) {
        // 方向の反対方向にある駒を取得
        int pos=gyokuPosition;
        pos-=diff[direct];
        int koma=test.get(pos);
        // その駒が敵の駒で、玉方向に動けるか？
        if (Koma.isEnemy(test.teban,koma) && canMove[direct][koma]) {
          // 動けるなら、この手は王手を放置しているので、
          // この手は、removedに追加しない。
          isOuteHouchi=true;
          break;
        }
      }

      // 玉の周り（８方向）から相手の駒の飛び利きがあるなら、その手は取り除く
      for(int direct=0;direct<8 && !isOuteHouchi;direct++) {
        // 方向の反対方向にある駒を取得
        int pos=gyokuPosition;
        int koma;
        // その方向にマスが空いている限り、駒を探す
        for(pos-=diff[direct],koma=test.get(pos);
        koma!=Koma.WALL;pos-=diff[direct],koma=test.get(pos)) {
          // 味方駒で利きが遮られているなら、チェック終わり。
          if (Koma.isSelf(test.teban,koma)) break;
          // 遮られていない相手の駒の利きがあるなら、王手がかかっている。
          if (Koma.isEnemy(test.teban,koma) && canJump[direct][koma]) {
            isOuteHouchi=true;
            break;
          }
          // 敵駒で利きが遮られているから、チェック終わり。
          if (Koma.isEnemy(test.teban,koma)) {
            break;
          }
        }
      }
      if (!isOuteHouchi) {
        removed.add(te);
      }
    }
    return removed;
  }

  // 与えられたVectorに、手番、駒の種類、移動元、移動先を考慮して、
  // 成る・不成りを判断しながら生成した手を追加する。
  public static void addTe(Kyokumen k,Vector v,int teban,int koma,int from,int to) {
    if (teban==SENTE) {
      // 先手番
      if ((Koma.getKomashu(koma)==Koma.KY || Koma.getKomashu(koma)==Koma.FU) && (to&0x0f)==1) {
        // 香車か歩が１段目に進むときには、成ることしか選べない。
        Te te=new Te(koma,from,to,true,k.get(to));
        v.add(te);
      } else if (Koma.getKomashu(koma)==Koma.KE && (to&0x0f)<=2) {
        // 桂馬が２段目以上に進む時には、成ることしか選べない。
        Te te=new Te(koma,from,to,true,k.get(to));
        v.add(te);
      } else if ( ( (to&0x0f)<=3 || (from&0x0f)<=3 ) && Koma.canPromote(koma)) {
        // 駒の居た位置が相手陣か、進む位置が相手陣で、
        // 駒が成ることが出来るなら
        // 成りと不成りの両方の手を生成
        Te te=new Te(koma,from,to,true,k.get(to));
        v.add(te);
        te=new Te(koma,from,to,false,k.get(to));
        v.add(te);
      } else {
        // 不成りの手のみ生成
        Te te=new Te(koma,from,to,false,k.get(to));
        v.add(te);
      }
    } else {
      // 後手番
      if ((Koma.getKomashu(koma)==Koma.KY || Koma.getKomashu(koma)==Koma.FU) && (to&0x0f)==9) {
        // 香車か歩が九段目に進むときには、成ることしか選べない。
        Te te=new Te(koma,from,to,true,k.get(to));
        v.add(te);
      } else if (Koma.getKomashu(koma)==Koma.KE && (to&0x0f)>=8) {
        // 桂馬が八段目以上に進む時には、成ることしか選べない。
        Te te=new Te(koma,from,to,true,k.get(to));
        v.add(te);
      } else if ( ( (to&0x0f)>=7 || (from&0x0f)>=7 ) && Koma.canPromote(koma)) {
        // 駒の居た位置が相手陣か、進む位置が相手陣で、
        // 駒が成ることが出来るなら
        // 成りと不成りの両方の手を生成
        Te te=new Te(koma,from,to,true,k.get(to));
        v.add(te);
        te=new Te(koma,from,to,false,k.get(to));
        v.add(te);
      } else {
        // 不成りの手のみ生成
        Te te=new Te(koma,from,to,false,k.get(to));
        v.add(te);
      }
    }
  }

  // 打ち歩詰めになっていないかどうかチェックする関数
  // 相手の玉頭に歩を打つ場合、
  // その手で一手進めてみて、相手の手番でGenerateLegalMoveを行い、
  // 帰ってくる手がなかったなら打ち歩詰めになっている。
  public static boolean isUtiFuDume(Kyokumen k,Te te) {
    if (te.from!=0) {
      // 駒を打つ手ではないので、打ち歩詰めではない。
      return false;
    }
    if (Koma.getKomashu(te.koma)!=Koma.FU) {
      // 歩を打つ手ではないので、打ち歩詰めではない。
      return false;
    }
    int teban;
    int tebanAite;
    if ((te.koma&SENTE)!=0) {
      // 先手の歩を打つから、自分の手番は先手、相手の手番は後手
      teban=SENTE;
      tebanAite=GOTE;
    } else {
      // そうでない時は、自分の手番は後手、相手の手番は先手
      teban=GOTE;
      tebanAite=SENTE;
    }
    int gyokuPositionAite=k.searchGyoku(tebanAite);
    if (teban==SENTE) {
      if (gyokuPositionAite!=te.to-1) {
        // 相手の玉の頭に歩を打つ手ではないので、打ち歩詰めになっていない。
        return false;
      }
    } else {
      if (gyokuPositionAite!=te.to+1) {
        // 相手の玉の頭に歩を打つ手ではないので、打ち歩詰めになっていない。
        return false;
      }
    }
    // 実際に一手進めてみる…。
    Kyokumen test=(Kyokumen)k.clone();
    test.move(te);
    test.teban=tebanAite;
    // その局面で、相手に合法手があるか？なければ、打ち歩詰め。
    Vector v=generateLegalMoves(test);
    if (v.size()==0) {
      // 合法手がないので、打ち歩詰め。
      return true;
    }
    return false;
  }

  // 与えられた局面における合法手を生成する。
  public static Vector generateLegalMoves(Kyokumen k) {
    return generateLegalMoves(k,16);
  }
  public static Vector generateLegalMoves(Kyokumen k,int remainDepth) {
    Vector v=new Vector();

    // 盤上の手番の側の駒を動かす手を生成
    for(int suji=0x10;suji<=0x90;suji+=0x10) {
      for(int dan=1;dan<=9;dan++) {
        int from=dan+suji;
        int koma=k.get(from);
        // 自分の駒であるかどうか確認
        if (Koma.isSelf(k.teban,koma)) {
          // 各方向に移動する手を生成
          for(int direct=0;direct<12;direct++) {
            if (canMove[direct][koma]) {
              // 移動先を生成
              int to=from+diff[direct];
              // 移動先は盤内か？
              if (1<=(to>>4) && (to>>4)<=9 && 1<=(to&0x0f) && (to&0x0f)<=9) {
                // 移動先に自分の駒がないか？
                if (Koma.isSelf(k.teban,k.get(to))) {
                  // 自分の駒だったら、次の方向を検討
                  continue;
                }
                // 成る・不成りを考慮しながら、手をvに追加
                addTe(k,v,k.teban,koma,from,to);
              }
            }
          }
          // 各方向に「飛ぶ」手を生成
          for(int direct=0;direct<8;direct++) {
            if (canJump[direct][koma]) {
              // そちら方向に飛ぶことが出来る
              for(int i=1;i<9;i++) {
                // 移動先を生成
                int to=from+diff[direct]*i;
                // 行き先が盤外だったら、そこには行けない
                if (k.get(to)==Koma.WALL) break;
                // 行き先に自分の駒があったら、そこには行けない
                if (Koma.isSelf(k.teban,k.get(to))) break;
                // 成る・不成りを考慮しながら、手をvに追加
                addTe(k,v,k.teban,koma,from,to);
                // 空き升でなければ、ここで終わり
                if (k.get(to)!=Koma.EMPTY) break;
              }
            }
          }
        }
      }
    }

    int gyokuPosition=k.searchGyoku(k.teban);
    boolean isOute=false;
    // 玉の周辺（１２方向）から相手の駒が利いていたら、その手は取り除く
    for(int direct=0;direct<12 && !isOute;direct++) {
      // 方向の反対方向にある駒を取得
      int pos=gyokuPosition;
      pos-=diff[direct];
      int koma=k.get(pos);
      // その駒が敵の駒で、玉方向に動けるか？
      if (Koma.isEnemy(k.teban,koma) && canMove[direct][koma]) {
        // 動けるなら、この手は王手を放置しているので、
        // この手は、removedに追加しない。
        isOute=true;
        break;
      }
    }

    // 玉の周り（８方向）から相手の駒の飛び利きがあるなら、その手は取り除く
    for(int direct=0;direct<8 && !isOute;direct++) {
      // 方向の反対方向にある駒を取得
      int pos=gyokuPosition;
      int koma;
      // その方向にマスが空いている限り、駒を探す
      for(pos-=diff[direct],koma=k.get(pos);
      koma!=Koma.WALL;pos-=diff[direct],koma=k.get(pos)) {
        // 味方駒で利きが遮られているなら、チェック終わり。
        if (Koma.isSelf(k.teban,koma)) break;
        // 遮られていない相手の駒の利きがあるなら、王手がかかっている。
        if (Koma.isEnemy(k.teban,koma) && canJump[direct][koma]) {
          isOute=true;
          break;
        }
        // 敵駒で利きが遮られているから、チェック終わり。
        if (Koma.isEnemy(k.teban,koma)) {
          break;
        }
      }
    }


    // 手番の側の駒を打つ手を生成
    // 残り深さ１以下で、王手がかかっていないなら、駒を打つ手は生成しない。
    if (remainDepth<=1 && !isOute) {

    } else {
      // 駒を打つ手の前向き枝刈りに使用
      Te teS[]=new Te[20];
      Te teE[]=new Te[20];
      if (remainDepth<3) {
          for(int i=0;i<20;i++) {
            teS[i]=new Te();
            teE[i]=new Te();
          }
      }

      // まず、駒の種類でループ
      for(int i=Koma.FU;i<=Koma.HI;i++) {
        // 打つ駒は、手番の側の駒
        int koma=i|k.teban;
        // その駒を持っているか？
        if (k.hand[koma]>0) {
          // 持っている。
          int komashu=Koma.getKomashu(koma);
          // 盤面の各升目でループ
          for(int suji=0x10;suji<=0x90;suji+=0x10) {
            // 二歩にならないかどうかチェック
            if (komashu==Koma.FU) {
              // 二歩のチェック用変数
              boolean isNifu=false;
              // 二歩チェック
              // 同じ筋に、手番の側の歩がいないことを確認する
              for(int dan=1;dan<=9;dan++) {
                int p=suji+dan;
                // 手番の側の歩が、同じ筋にいないかどうかチェックする
                if (k.get(p)==(k.teban|Koma.FU)) {
                  // 二歩になっている。
                  isNifu=true;
                  break;
                }
              }
              if (isNifu) {
                // 二歩になっているので、打つ手を生成しない。
                // 次の筋へ
                continue;
              }
            }
            for(int dan=1;dan<=9;dan++) {
              // 駒が桂馬の場合の扱い
              if (komashu==Koma.KE) {
                if (k.teban==SENTE && dan<=2) {
                  // 先手なら、二段目より上に桂馬は打てない
                  continue;
                } else if (k.teban==GOTE && dan>=8) {
                  // 後手なら、八段目より下に桂馬は打てない
                  continue;
                }
              }
              // 駒が歩、または香車の場合の扱い
              if (komashu==Koma.FU || komashu==Koma.KY) {
                if (k.teban==SENTE && dan==1) {
                  // 先手なら、一段目に歩と香車は打てない
                  continue;
                } else if (k.teban==GOTE && dan==9) {
                  // 後手なら、九段目に歩と香車は打てない
                  continue;
                }
              }
              // 移動元…駒を打つ手は、0
              int from=0;
              // 移動先、駒を打つ場所
              int to=suji+dan;
              // 空き升でなければ、打つ事は出来ない。
              if (k.get(to)!=Koma.EMPTY) {
                continue;
              }
              // 手の生成…駒を打つ際には、常に不成で、取る駒もなしである。
              Te te=new Te(koma,from,to,false,Koma.EMPTY);

              // 打ち歩詰めの特殊扱い
              if (isUtiFuDume(k,te)) {
                // 打ち歩詰めなら、そこに歩は打てない
                continue;
              }
              // 駒を打つとすぐに取られるような手は合法手として生成しない。
              if (!isOute && remainDepth<3) {
                k.move(te);

                if (EvalPos(k,te.to,k.teban,teS,teE)>400){
                  // 駒損する。
                  k.back(te);
                  continue;
                }
                k.back(te);
              }
              // 駒を打つ手が可能なことが分かったので、合法手に加える。
              v.add(te);
            }
          }
        }
      }
    }

    // 生成した各手について、指してみて
    // 自分の玉に王手がかかっていないかどうかチェックし、
    // 王手がかかっている手は取り除く。
    v=removeSelfMate(k,v);

    return v;
  }

  // ２つのものから最大を返す
  static int max(int x,int y) {
    if (x>y) {
      return x;
    } else {
      return y;
    }
  }

  // ２つのものから最小を返す
  static int min(int x,int y) {
    if (x<y) {
      return x;
    } else {
      return y;
    }
  }

  // 絶対値を返す
  static int abs(int x) {
    if (x<0) {
      return -x;
    } else {
      return x;
    }
  }

  // ある位置からある位置への距離を求める。
  static int kyori(int p1,int p2) {
    return max(abs(p1/16-p2/16),abs((p1 & 0x0f)-(p2 &0x0f)));
  }

  // 交換値を求める際に、正しい手かどうかチェックする。
  // captureが入っていない場合、captureを埋める。
  static boolean IsCorrectMove(Kyokumen k,Te te) {
    if (k.ban[te.from]==Koma.SKE || k.ban[te.from]==Koma.GKE) {
      te.capture=k.ban[te.to];
      return true;
    }
    int d=kyori(te.from,te.to);
    if (d==0) return false;
    int dir=(te.to-te.from)/d;
    if (d==1) {
      te.capture=k.ban[te.to];
      return true;
    }
    // 距離が２以上ならば、
    // ジャンプなので、途中に邪魔な駒がいないかどうかチェックする
    for(int i=1,pos=te.from+dir;i<d;i++,pos=pos+dir) {
      if (k.ban[pos]!=Koma.EMPTY) {
        return false;
      }
    }
    te.capture=k.ban[te.to];
    return true;
  }

  // ある地点における駒の取り合いの探索
  // 後手側ノード
  static int EvalMin(Kyokumen k,Te AtackS[],int NowAtackS,int NumAtackS,
      Te AtackE[],int NowAtackE,int NumAtackE) {
    int v=k.eval;
    int oldEval=k.eval;
    if (NumAtackE>NowAtackE) {
      // 邪魔駒の処理
      int j=NowAtackE;
      // その動きが正しいか？
      // 例えば、香車が前の駒を追い越したりしていないか？
      while(j<NumAtackE && !IsCorrectMove(k,AtackE[j])) {
        j++;
      }
      if (j==NowAtackE) {
        // 予定していた動きでＯＫ
      } else if (j<NumAtackE) {
        // 予定していた動きがＮＧで、別の動きと入れ替え
        Te t=AtackE[j];
        for(int i=j;i>NowAtackE;i--) {
          AtackE[i]=AtackE[i-1];
        }
        AtackE[NowAtackE]=t;
      } else {
        // 他に手がない＝取れない。
        return v;
      }
      AtackE[NowAtackE].capture=k.ban[AtackE[NowAtackE].to];
      k.move(AtackE[NowAtackE]);
      v=min(v,EvalMax(k,AtackS,NowAtackS,NumAtackS,
          AtackE,NowAtackE+1,NumAtackE));
      k.back(AtackE[NowAtackE]);
    }
    return v;
  }

  // ある地点における駒の取り合いの探索
  // 先手側ノードの探索
  static int EvalMax(Kyokumen k,Te AtackS[],int NowAtackS,int NumAtackS,
      Te AtackE[],int NowAtackE,int NumAtackE) {
    int v=k.eval;
    if (NumAtackS>NowAtackS) {
      // 邪魔駒の処理
      int j=NowAtackS;
      // その動きが正しいか？
      // 例えば、香車が前の駒を追い越したりしていないか？
      while(j<NumAtackS && !IsCorrectMove(k,AtackS[j])) {
        j++;
      }
      if (j==NowAtackS) {
        // 予定していた動きでＯＫ
      } else if (j<NumAtackS) {
        // 予定していた動きがＮＧで、別の動きと入れ替え
        Te t=AtackS[j];
        for(int i=j;i>NowAtackS;i--) {
          AtackS[i]=AtackS[i-1];
        }
        AtackS[NowAtackS]=t;
      } else {
        // 他に手がない＝取れない。
        return v;
      }
      AtackS[NowAtackS].capture=k.ban[AtackS[NowAtackS].to];
      k.move(AtackS[NowAtackS]);
      v=max(v,EvalMin(k,AtackS,NowAtackS+1,NumAtackS,
          AtackE,NowAtackE,NumAtackE));
      k.back(AtackS[NowAtackS]);
    }
    return v;
  }

  // 速度的にはまずいのだけれど、同時に２思考が走る場合に、staticに
  // しているとちょっとまずい…。
  // ある位置での駒の取り合い探索用の手の準備
  // 先手側
  /*
  static Te teS[]={
    new Te(),new Te(),new Te(),new Te(),new Te(),
    new Te(),new Te(),new Te(),new Te(),new Te(),
    new Te(),new Te(),new Te(),new Te(),new Te(),
    new Te(),new Te(),new Te(),new Te(),new Te()
  };
  // 後手側
  static Te teE[]={
    new Te(),new Te(),new Te(),new Te(),new Te(),
    new Te(),new Te(),new Te(),new Te(),new Te(),
    new Te(),new Te(),new Te(),new Te(),new Te(),
    new Te(),new Te(),new Te(),new Te(),new Te()
  };
  */

  // 与えられた局面　k　の与えられた位置 positionで、手番SorGが
  // 駒交換を行った場合に、何点を得られるか？を求める。
  // これも、ＭｉｎＭａｘ法である。
  static int EvalPos(Kyokumen k,int position,int SorG,Te teS[],Te teE[]) {
    int ret=0;
    int ToPos=position;

    // AtackCountを得るように、駒のリストを得る
    // 一個所への利きは、最大隣接8+桂馬2+飛飛角角香香香香=18だから、
    // ２０あれば十分。

    // AtackS,AtackCountSが先手側のその位置に動く手
    // AtackE,AtackCountEが後手側のその位置に動く手

    Te AtackS[]=teS;
    Te AtackE[]=teE;

    int AtackCountE=0;
    int AtackCountS=0;

    int pos2;

    // 先手側成りフラグ、後手側成りフラグ
    boolean PromoteS,PromoteE;

    int i;
    int pos=ToPos;
    if ((ToPos&0x0f)<=3) {
      PromoteS=true;
    } else {
      PromoteS=false;
    }
    if ((ToPos&0x0f)>=7) {
      PromoteE=true;
    } else {
      PromoteE=false;
    }

    // 桂馬の利きは別に数える
    for (i = 0; i < 8; i++) {
      // 周り８方向の位置がpos2に入る
      pos2=pos-diff[i];
      // pos2にあるのが、壁ならcontinue
      if (k.ban[pos2]==Koma.WALL) {
        continue;
      }
      if (canMove[i][k.ban[pos2]] && Koma.isSente(k.ban[pos2])) {
        // pos2にある駒がposの側に動く事が出来る先手の駒なら…
        // AtackS,AtackCountSに、その駒を動かす手を入れる。
        AtackS[AtackCountS].from=pos2;
        AtackS[AtackCountS].koma=k.ban[pos2];
        AtackS[AtackCountS].to=pos;
        if ((PromoteS || (pos2 & 0x0f)<=3) && Koma.canPromote[AtackS[AtackCountS].koma]) {
          AtackS[AtackCountS].promote=true;
        } else {
          AtackS[AtackCountS].promote=false;
        }
        AtackCountS++;
      } else if (canMove[i][k.ban[pos2]] && Koma.isGote(k.ban[pos2])) {
        // pos2にある駒がposの側に動く事が出来る後手の駒なら…
        // AtackE,AtackCountEに、その駒を動かす手を入れる。
        AtackE[AtackCountE].from=pos2;
        AtackE[AtackCountE].koma=k.ban[pos2];
        AtackE[AtackCountE].to=pos;
        if ((PromoteE || (pos2 & 0x0f)>=7) && Koma.canPromote[AtackE[AtackCountE].koma]) {
          AtackE[AtackCountE].promote=true;
        } else {
          AtackE[AtackCountE].promote=false;
        }
        AtackCountE++;
      }

      // 玉以外の駒は貫き通せることにしておく。
      if (k.ban[pos-diff[i]]!=Koma.SOU && k.ban[pos-diff[i]]!=Koma.GOU) {
        // 今度は、pos2をposにもう一度戻し、
        pos2=pos;
        // その方向に飛び利きのある駒を探していく。
        while(k.ban[pos2]!=Koma.WALL) {
          pos2-=diff[i];
          while(k.ban[pos2]==Koma.EMPTY) {
            pos2-=diff[i];
          }
          if (k.ban[pos2]==Koma.WALL) {
            break;
          }
          if (!canJump[i][k.ban[pos2]]) {
            break;
          }
          if (Koma.isSente(k.ban[pos2])) {
            AtackS[AtackCountS].from=pos2;
            AtackS[AtackCountS].koma=k.ban[pos2];
            AtackS[AtackCountS].to=pos;
            if ((PromoteS || (pos2 & 0x0f)<=3) && Koma.canPromote[AtackS[AtackCountS].koma]) {
              AtackS[AtackCountS].promote=true;
            } else {
              AtackS[AtackCountS].promote=false;
            }
            AtackCountS++;
          } else if (Koma.isGote(k.ban[pos2])) {
            AtackE[AtackCountE].from=pos2;
            AtackE[AtackCountE].koma=k.ban[pos2];
            AtackE[AtackCountE].to=pos;
            if ((PromoteE || (pos2 & 0x0f)>=7) && Koma.canPromote[AtackE[AtackCountE].koma]) {
              AtackE[AtackCountE].promote=true;
            } else {
              AtackE[AtackCountE].promote=false;
            }
            AtackCountE++;
          }
        }
      }
    }
    // 桂馬の利き
    for(i=8;i<12;i++) {
      pos2=pos-diff[i];
      if (pos2<0 || k.ban[pos2]==Koma.WALL) {
        continue;
      }
      if (canMove[i][k.ban[pos2]] && Koma.isSente(k.ban[pos2])) {
        AtackS[AtackCountS].from=pos2;
        AtackS[AtackCountS].koma=k.ban[pos2];
        AtackS[AtackCountS].to=pos;
        if ((PromoteS || (pos2 & 0x0f)<=3) && Koma.canPromote[AtackS[AtackCountS].koma]) {
          AtackS[AtackCountS].promote=true;
        } else {
          AtackS[AtackCountS].promote=false;
        }
        AtackCountS++;
      } else if (canMove[i][k.ban[pos2]] && Koma.isGote(k.ban[pos2])) {
        AtackE[AtackCountE].from=pos2;
        AtackE[AtackCountE].koma=k.ban[pos2];
        AtackE[AtackCountE].to=pos;
        if ((PromoteE || (pos2 & 0x0f)>=7) && Koma.canPromote[AtackE[AtackCountE].koma]) {
          AtackE[AtackCountE].promote=true;
        } else {
          AtackE[AtackCountE].promote=false;
        }
        AtackCountE++;
      }
    }
    // AtackSを駒の価値でソート。
    for (i=0; i < AtackCountS-1; i++) {
      int max_id = i; int max_val = k.komaValue[AtackS[i].koma];
      for (int j = i+1; j < AtackCountS ; j++) {
        int v=k.komaValue[AtackS[j].koma];
        if (v < max_val) {
          max_id = j;
          max_val= v;
        } else if (v==max_val) {
          if (k.komaValue[AtackS[j].koma]<k.komaValue[AtackS[max_id].koma]) {
            max_id=j;
          }
        }
      }
      //最大値との交換
      if (i!=max_id) {
        Te temp=AtackS[i];
        AtackS[i]=AtackS[max_id];
        AtackS[max_id]=temp;
      }
    }
    // AtackEを駒の価値でソート。
    for (i=0; i < AtackCountE-1; i++) {
      int max_id = i; int max_val = k.komaValue[AtackE[i].koma];
      for (int j = i+1; j < AtackCountE ; j++) {
        int v=k.komaValue[AtackE[j].koma];
        if (v> max_val) {
          max_id = j;
          max_val= v;
        } else if (v==max_val) {
          if (k.komaValue[AtackE[j].koma]>k.komaValue[AtackE[max_id].koma]) {
            max_id=j;
          }
        }
      }
      //最大値との交換
      if (i!=max_id) {
        Te temp=AtackE[i];
        AtackE[i]=AtackE[max_id];
        AtackE[max_id]=temp;
      }
    }

    boolean IsGote=Koma.isGote(k.ban[position]);
    boolean IsSente=Koma.isSente(k.ban[position]);
    if (k.ban[position]==Koma.EMPTY) {
      if (SorG==SENTE) {
        IsGote=true;
      } else {
        IsSente=true;
      }
    }
    try {
      if (IsGote && AtackCountS>0) {
        ret=EvalMax(k,AtackS,0,AtackCountS,AtackE,0,AtackCountE)-k.eval;
      } else if (IsSente && AtackCountE>0) {
        ret=k.eval-EvalMin(k,AtackS,0,AtackCountS,AtackE,0,AtackCountE);
      } else {
        ret=0;
      }
    } catch(Exception ex) {
      for(int d=0;d<AtackCountS;d++) {
        System.out.println("AtackS:");
        System.out.print(AtackS[d]);
        System.out.println();
      }
      for(int d=0;d<AtackCountE;d++) {
        System.out.println("AtackE:");
        System.out.print(AtackE[d]);
        System.out.println();
      }
    }
    return ret;
  }

  // 駒を動かした際に変化する地点での交換値を元に、
  // 手の価値を計算して、ソートしてみる。
  public static void evaluateTe(Kyokumen k,Vector v,Te teS[],Te teE[]) {
    // 現在の局面の評価値
    int nowEval=k.eval;
    // 相手玉
    int EnemyKing;
    if (k.teban==SENTE) {
      EnemyKing=Koma.GOU;
    } else {
      EnemyKing=Koma.SOU;
    }

    // 全部の手について…
    for(int i=0;i<v.size();i++) {
      // 手を取り出す。
      Te te=(Te)v.elementAt(i);

      // GainSPosは、GainSを元の局面で計算しないとならない位置
      int GainSPos[]=new int [30];
      int GainSNum=0;

      // LossSが自分から見た、駒損をする脅威
      // LossEが相手から見た、駒損をする脅威
      // GainSが自分から見た、駒損をする脅威
      // GainEが相手から見た、駒得をする脅威（計算していない。）
      int LossS,LossE,GainS,GainE;
      LossS=LossE=GainS=GainE=0;

      // 成った後の駒を覚えておく。
      int newKoma=te.koma;
      if (te.promote) newKoma|=Koma.PROMOTE;
      // その手で進めてみる
      k.move(te);

      // 手の仮の価値は、元の局面との評価値の差分
      te.value=k.eval-nowEval;
      // 移動先での脅威
      LossS+=EvalPos(k,te.to,k.teban,teS,teE);
      // 相手に与える脅威と、新しく自分の駒にヒモをつけることで、減る脅威を計算
      for(int dir=0;dir<12;dir++) {
        // 動かした駒が12方向へ、動くことを計算
        if (canMove[dir][newKoma]) {
          // その方向に動けるなら…
          int p=te.to+diff[dir];
          // 玉じゃないのなら、
          if (k.ban[p]!=EnemyKing) {
            if (Koma.isEnemy(k.teban,k.ban[p])) {
              // 相手の駒なら、相手から見た駒損をする脅威
              LossE+=EvalPos(k,p,k.teban,teS,teE);
            } else if (Koma.isSelf(k.teban,k.ban[p])) {
              // 自分の駒なら、自分から見た駒損をする脅威
              GainS-=EvalPos(k,p,k.teban,teS,teE);
              GainSPos[GainSNum++]=p; // この地点の元の脅威を後で計算する
            }
          } else {
            // 玉に与える脅威は1000点で計算しておく。
            LossE+=1000;
          }
        }
      }
      if (te.from!=0) {
        // 他の駒の飛び利きを通した？
        for(int dir=0;dir<8;dir++) {
          // その駒から「逆」方向へ…
          int pos=te.from-diff[dir];
          while(k.ban[pos]==Koma.EMPTY) {
            pos-=diff[dir];
          }
          // 何か見つかった。
          // それが、この方向に飛び利きを持つ駒なら…
          if(k.ban[pos]!=Koma.WALL && canJump[dir][k.ban[pos]]) {
            pos=te.from+diff[dir];
            while(k.ban[pos]==Koma.EMPTY){
              pos+=diff[dir];
            }
            // 飛び利きの通った先の交換値を求める。
            if (k.ban[pos]!=Koma.WALL){
              if (Koma.isEnemy(k.teban,k.ban[pos])) {
                LossE+=EvalPos(k,pos,k.teban,teS,teE);
              } else if (Koma.isSelf(k.teban,k.ban[pos])) {
                GainS-=EvalPos(k,pos,k.teban,teS,teE);
                GainSPos[GainSNum++]=pos; // 元の脅威を後で計算する
              }
            }
          }
        }
      }
      // 同様に、８方向の飛び利きを求める。
      for(int dir=0;dir<8;dir++) {
        if (canJump[dir][newKoma]) {
          int p=te.to+diff[dir];
          while(k.ban[p]==Koma.EMPTY) {
            p+=diff[dir];
          }
          // 例によって、玉に対する脅威は大きく評価されすぎるので調整する。
          if (k.ban[p]!=EnemyKing) {
            if (Koma.isEnemy(k.teban,k.ban[p])) {
              LossE+=EvalPos(k,p,k.teban,teS,teE);
            } else if (Koma.isSelf(k.teban,k.ban[p])) {
              GainS+=-EvalPos(k,p,k.teban,teS,teE);
              GainSPos[GainSNum++]=p; // 元の脅威を後で計算する
            }
          } else {
            // 玉に与える脅威は1000点で計算しておく。
            LossE+=1000;
          }
        }
      }
      // 局面を元に戻す。
      k.back(te);
      // 後手番なら、先に求めた評価値の差分が、正負が逆になるので…
      if (k.teban==GOTE) {
        te.value=-te.value;
      }
      // 元の脅威について、後で計算しないとならない点について、
      // 溜め込んでおいた情報に基づいて計算
      for(int j=0;j<GainSNum;j++) {
        GainS+=EvalPos(k,GainSPos[j],k.teban,teS,teE);
      }
      // 駒を動かす手であれば…
      if (te.from!=0) {
        // 移動元にあった、脅威はなくなる
        LossS-=EvalPos(k,te.from,k.teban,teS,teE);
      }

      // GainS,LossSは、そのまま手の価値に加算してしまう。
      te.value+=GainS-LossS;
      te.value2=te.value;

      // 駒を取る手が読みに入りやすくするように、点数を加算する。
      if (te.capture!=Koma.EMPTY &&
          te.capture!=Koma.SFU && te.capture!=Koma.GFU) {
        // 歩以外の駒を取る手は無条件に1500点プラスして、読みに入れるようにする
        te.value+=1500;
      }
      // 相手に与える脅威は1/10位にして加算すると、実験上ちょうど良い。
      te.value+=LossE/10;

//    デバッグ用…出力すると、膨大になります。
//      System.out.print(te);
//      System.out.println("value:"+te.value+" GainS:"+GainS+
//          " GainE:"+GainE+" LossS:"+LossS+" LossE:"+LossE);
    }
    // ソートする。
    for(int i=0;i<v.size();i++) {
      Te te=(Te)v.elementAt(i);
      int maxValue=te.value;
      int maxIndex=i;
      for(int j=i+1;j<v.size();j++) {
        te=(Te)v.elementAt(j);
        if (te.value>maxValue) {
          maxValue=te.value;
          maxIndex=j;
        }
      }
      Te tmp=(Te)v.elementAt(maxIndex);
      Te old=(Te)v.elementAt(i);
      v.setElementAt(tmp,i);
      v.setElementAt(old,maxIndex);
    }
//    デバッグ用…出力すると、膨大になります。
    /*
    for(int i=0;i<v.size();i++) {
      Te te=(Te)v.elementAt(i);
      System.out.print(te);
      System.out.println(te.value);
    }
     */
  }

  public static boolean isLegalMove(Kyokumen k,Te t) {
    if (t.from>0 && k.ban[t.from]!=t.koma) {
      // 移動元の駒が違う
      return false;
    }
    if (t.from==0 && k.hand[t.koma]==0) {
      // 持ち駒に持っていない
      return false;
    }
    if (t.from==0 && k.ban[t.to]!=Koma.EMPTY) {
      // 空いてないので打てない
      return false;
    }
    if (Koma.isSelf((t.koma & (SENTE|GOTE)),k.ban[t.to])) {
      // 自分の駒のあるところには進めない
      return false;
    }
    if (isUtiFuDume(k,t)) {
      // 打ち歩詰め
      return false;
    }
    if (!IsCorrectMove(k,t)) {
      return false;
    }
    // 王手放置になっていないかどうかチェック
    // その手で１手進めてみる
    Kyokumen test=(Kyokumen)k.clone();
    test.move(t);

    // 自玉を探す

    int gyokuPosition=test.searchGyoku(k.teban);

    // 王手放置しているかどうかフラグ
    boolean isOuteHouchi=false;

    // 玉の周辺（１２方向）から相手の駒が利いていたら、その手は取り除く
    for(int direct=0;direct<12 && !isOuteHouchi;direct++) {
      // 方向の反対方向にある駒を取得
      int pos=gyokuPosition;
      pos-=diff[direct];
      int koma=test.get(pos);
      // その駒が敵の駒で、玉方向に動けるか？
      if (Koma.isEnemy(test.teban,koma) && canMove[direct][koma]) {
        // 動けるなら、この手は王手を放置しているので、
        // この手は、removedに追加しない。
        isOuteHouchi=true;
        break;
      }
    }

    // 玉の周り（８方向）から相手の駒の飛び利きがあるなら、その手は取り除く
    for(int direct=0;direct<8 && !isOuteHouchi;direct++) {
      // 方向の反対方向にある駒を取得
      int pos=gyokuPosition;
      int koma;
      // その方向にマスが空いている限り、駒を探す
      for(pos-=diff[direct],koma=test.get(pos);
      koma!=Koma.WALL;pos-=diff[direct],koma=test.get(pos)) {
        // 味方駒で利きが遮られているなら、チェック終わり。
        if (Koma.isSelf(test.teban,koma)) break;
        // 遮られていない相手の駒の利きがあるなら、王手がかかっている。
        if (Koma.isEnemy(test.teban,koma) && canJump[direct][koma]) {
          isOuteHouchi=true;
          break;
        }
        // 敵駒で利きが遮られているから、チェック終わり。
        if (Koma.isEnemy(test.teban,koma)) {
          break;
        }
      }
    }
    if (isOuteHouchi) {
      return false;
    }

    return true;
  }

  // 軽く手を生成してみる。
  public static Vector makeMoveFirst(
      Kyokumen k,int depth,Sikou s,TTEntry e) {
    Vector v=new Vector();
    if (e!=null && e.best!=null && isLegalMove(k,e.best)) {
      v.add(e.best);
    }
    if (depth>0 && s.best[depth-1][depth]!=null &&
        isLegalMove(k,s.best[depth-1][depth])){
      v.add(s.best[depth-1][depth]);
    }
    if (e!=null && e.second!=null && isLegalMove(k,e.second)) {
      v.add(e.second);
    }
    return v;
  }

}
