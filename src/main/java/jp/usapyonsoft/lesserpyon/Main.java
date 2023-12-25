package jp.usapyonsoft.lesserpyon;
import java.util.Vector;
import java.io.*;

public class Main implements Constants {
  // 初期盤面を与える
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
  
  // player[0]が先手が誰か、player[1]が後手が誰か。
  static Player player[]=new Player[2];
  
  // 今までに使った累積時間（秒）
  static int spenttime[]=new int[2];
  
  // 思考時間の上限（秒）…大会ルールにあわせ、25分
  static int limitTime=1500;
  
  // 思考時間を過ぎた後の秒読み…大会ルールにあわせ、0
  static int byoyomi=0;
  
  
  static Vector kyokumenRireki=new Vector();

  // 使い方を表示する。
  static void usage() {
    System.out.println("使い方：");
    System.out.println("例：先手　人間　後手　コンピュータの場合");
    System.out.println("java jp.usapyonsoft.lesserpyon.Main HUMAN CPU");
    System.out.println("");
    System.out.println("初期局面を与えて対局開始することも可能です。");
    System.out.println("例：kyokumen.csaに初期局面が入っているとした場合");
    System.out.println("java jp.usapyonsoft.lesserpyon.Main HUMAN CPU kyokumen.csa");
    System.out.println("LAN対戦の場合、先手・後手が入れ替わることがあります。");
    System.out.println("例：先手　LAN　後手　コンピュータの場合");
    System.out.println("java jp.usapyonsoft.lesserpyon.Main LAN CPU");
    System.out.println("この場合、ログインしてから先手・後手が決まります。");
    System.out.println("また、先手・後手の両方にLANを指定することは出来ません。");
  }
  
  static CSAProtocol csaProtocol;
  static final String server="wdoor.c.u-tokyo.ac.jp";
  static final int serverPort=4081;
  static final String myName="lesserpyon-fo-java";
  static final String myPassword="yowai_gps-1500-0,ikeike";
  
  
  // メイン関数　引数は、先手番が誰か、後手番が誰か、初期局面は何か。
  // 誰か、は、人間ならば "HUMAN" CPUならば "CPU"を与える。
  // 初期局面が与えられなかった場合、平手の初期局面から。
  public static void main(String argv[]) {
    // 先手番、後手番が引数で与えられているかどうかチェック
    if (argv.length<2) {
      // 引数が足りないようなら、使い方を表示して終わり。
      usage();
      return;
    }
    
    Kyokumen k=new Kyokumen();
    int myTurn=SENTE;
    boolean isLan=false;
    if (argv[0].equals("LAN") || argv[1].equals("LAN")) {
      // 引数のチェック
      if (argv[0].equals("LAN")) {
        if (!argv[1].equals("CPU") && !argv[1].equals("HUMAN")) {
          usage();
          return;
        }
      } else {
        if (!argv[0].equals("CPU") && !argv[0].equals("HUMAN")) {
          usage();
          return;
        }
      }
      isLan=true;
      try {
        System.out.println("ＬＡＮに接続を試みます…");
        csaProtocol=new CSAProtocol(server,serverPort);
        System.out.println("接続成功です。");
        System.out.println("ログインを試みます…");
        if (csaProtocol.login(myName,myPassword)==0) {
          System.out.println("ログイン失敗です。");
          return;
        }
        System.out.println("ログイン成功です。");
        System.out.println("ゲームの開始を待ちます…");
        myTurn=csaProtocol.waitGameStart(k);
        System.out.println("ゲーム開始です。");
        if (myTurn==SENTE) {
          System.out.println("先手になりました。");
        } else {
          System.out.println("後手になりました。");
        }
      } catch(IOException e) {
        e.printStackTrace();
        System.out.println("接続できませんでした。");
        return;
      }
    }
    
    if (isLan) {
      if (myTurn==SENTE) {
        if (argv[0].equals("HUMAN") || argv[1].equals("HUMAN")) {
          player[0]=new Human();
        }
        if (argv[0].equals("CPU") || argv[1].equals("CPU")) {
          player[0]=new Sikou();
        }
        player[1]=new Lan(csaProtocol);
      } else {
        player[0]=new Lan(csaProtocol);
        if (argv[0].equals("HUMAN") || argv[1].equals("HUMAN")) {
          player[1]=new Human();
        }
        if (argv[0].equals("CPU") || argv[1].equals("CPU")) {
          player[1]=new Sikou();
        }
      }
    } else {
      // 先手番が誰かを設定。
      if (argv[0].equals("HUMAN")) {
        player[0]=new Human();
      } else if (argv[0].equals("CPU")) {
        player[0]=new Sikou();
      } else {
        // 引数がおかしいようなら、使い方を表示して終わり。
        usage();
        return;
      }

      // 後手番が誰かを設定。
      if (argv[1].equals("HUMAN")) {
        player[1]=new Human();
      } else if (argv[1].equals("CPU")) {
        player[1]=new Sikou();
      } else {
        // 引数がおかしいようなら、使い方を表示して終わり。
        usage();
        return;
      }
    }
    
    try {
      if (isLan) {
        // 局面の初期化が行われている。
        
      } else if (argv.length==2) {
        // 引数の指定がない場合、初期配置を使う。
        k.initHirate();
      } else {
        // 引数で指定があった場合、CSA形式の棋譜ファイルを読み込む。
        String csaFileName=argv[2];
        File f=new File(csaFileName);
        BufferedReader in=new BufferedReader(new FileReader(f));
        Vector v=new Vector();
        String s;
        while((s=in.readLine())!=null) {
          System.out.println("Read:"+s);
          v.add(s);
        }
        String csaKifu[]=new String[v.size()];
        v.copyInto(csaKifu);
        k.ReadCsaKifu(csaKifu);
        // ReadCsaKifuの中で必要な初期化が行われている。
      }
      
      int tesu=0;
      
      // 対戦のメインループ
      while(true) {
        k.tesu++;
        tesu++;
        // 現在の局面を、局面の履歴に保存する。
        kyokumenRireki.add(k.clone());
        // 現在の局面での合法手を生成
        Vector v=GenerateMoves.generateLegalMoves(k);
        if (v.size()==0) {
          // 手番の側の負け
          if (k.teban==SENTE) {
            System.out.println("後手の勝ち！");
          } else {
            System.out.println("先手の勝ち！");
          }
          // 対局終了
          break;
        }
        // 千日手のチェック…連続王手の千日手には未対応。
        // 同一局面が何回出てきたか？
        int sameKyokumen=0;
        for(int i=0;i<kyokumenRireki.size();i++) {
          // 同一局面だったら…
          if (kyokumenRireki.elementAt(i).equals(k)) {
            // 同一局面の出てきた回数を増やす
            sameKyokumen++;
          }
        }
        if (sameKyokumen>=4) {
          // 同一局面４回以上の繰り返しなので、千日手。
          System.out.println("千日手です。");
          // 対局終了
          break;
        }
        
        // 局面を表示。
        System.out.println(k.toString());
        // ついでに、局面の評価値を表示
        System.out.println("現在の局面の評価値:"+k.evaluate());
        // 次の手を手番側のプレイヤーから取得
        Te te;
        long ltime=System.currentTimeMillis();
        if (k.teban==SENTE) {
          te=player[0].getNextTe(k,k.tesu,k.spentTime[0],limitTime,byoyomi);
          if (isLan) {
            if (myTurn==SENTE) {
              if (te.koma==0) {
                csaProtocol.resign();
              } else {
                csaProtocol.sendTe(te);
              }
            }
            k.spentTime[0]+=csaProtocol.time[0];
          } else {
            int spent=(int)((System.currentTimeMillis()-ltime)/1000);
            if (spent==0) spent=1;
            k.spentTime[0]+=spent;
          }
        } else {
          te=player[1].getNextTe(k,k.tesu,k.spentTime[1],limitTime,byoyomi);
          if (isLan) {
            if (myTurn==GOTE) {
              if (te.koma==0) {
                csaProtocol.resign();
              } else {
                csaProtocol.sendTe(te);
              }
            }
            k.spentTime[1]+=csaProtocol.time[1];
          } else {
            int spent=(int)((System.currentTimeMillis()-ltime)/1000);
            if (spent==0) spent=1;
            k.spentTime[1]+=spent;
          }
        }
        // 指された手を表示
        System.out.println(te.toString());
        // 合法手でない手を指した場合、即負け
        if (!v.contains(te)) {
          System.out.println("合法手でない手が指されました。");
          // 手番の側の負け
          if (k.teban==SENTE) {
            System.out.println("後手の勝ち！");
          } else {
            System.out.println("先手の勝ち！");
          }
          // 対局終了
          break;
        }
        // 指された手で局面を進める。
        k.move(te);
        // moveでは、手番が変わらないので、局面の手番を変更する。
        if (k.teban==SENTE) {
          k.teban=GOTE;
        } else {
          k.teban=SENTE;
        }
      }
      // 対局終了。最後の局面を表示して、終わる。
      System.out.println("対局終了です。");
      System.out.println("最後の局面は…");
      System.out.println(k.toString());
      
      if (isLan) {
        csaProtocol.logout();
      }
    } catch(Exception ex) {
      ex.printStackTrace();
    }
  }
}
