package jp.usapyonsoft.lesserpyon;

import java.io.*;
import java.net.*;
import java.util.*;

public class CSAProtocol implements Runnable,Constants {
  Socket s;
  InputStream is;
  OutputStream os;
  BufferedReader ir;
  BufferedWriter ow;
  Thread rcvThread=null;
  
  volatile boolean wait=true;
  volatile boolean waitTime=false;
  int time[]=new int[2];
  String recieved="";
  int turn=-1;
  Kyokumen k=new Kyokumen();
  
  String nameSente;
  String nameGote;

  // server:接続先サーバ名
  // port: 接続先サーバのポート番号
  public CSAProtocol(String server,int port)
    throws IOException
  {
    // サーバの名前を解決
    InetAddress address=
      InetAddress.getByName(server);
    // ソケットの生成
    s=new Socket(address,port);
    
    // 出力ストリームを取得
    os = s.getOutputStream();
    ow = new BufferedWriter(
           new OutputStreamWriter(os));

    // 入力ストリームを取得
    is = s.getInputStream();
    ir = new BufferedReader(
           new InputStreamReader(is));
  }
  
  // id:ログインID
  // passwd:ログインパスワード
  // 戻り値:1:ログイン成功 0:ログイン失敗
  public int login(String id,String passwd) 
    throws IOException
  {
    ow.write("LOGIN "+id+" "+passwd+"¥n");
    ow.flush();
    String recv=ir.readLine();
    if (recv.equals("LOGIN:"+id+" OK")) {
      return 1;
    }
    return 0;
  }

  // CHALLENGEコマンドを送付
  public void challenge()
    throws IOException
  {
    ow.write("CHALLNEGE¥n");
    ow.flush();
  }
  
  public void logout() 
    throws IOException
  {
    ow.write("LOGOUT¥n");
    ow.flush();
  }
  
  public void resign()
    throws IOException
  {
    ow.write("%TORYO¥n");
    ow.flush();
  }
  
  // ゲーム開始を待つ。
  // 本来なら、ゲームの条件を解析するべきだが、
  // ここでは、手番だけを解析する。
  public int waitGameStart(Kyokumen k)
    throws IOException
  {
    int teban=-1;
    k.initHirate();
    this.k.initHirate();
    String recv=ir.readLine();
    // ゲーム開始情報の解析：手番だけ解析。
    while(!recv.equals("END Game_Summary")) {
      System.out.println(recv);
      if (recv.equals("Your_Turn:+")) {
        teban=SENTE;
      }
      if (recv.equals("Your_Turn:-")) {
        teban=GOTE;
      }
      if (recv.startsWith("Name+:")) {
        nameSente=recv.substring(6);
      }
      if (recv.startsWith("Name-:")) {
        nameGote=recv.substring(6);
      }
      recv=ir.readLine();
      // 平手から、手を進めている可能性がある…。
      if (recv.equals("+")) {
        continue;
      }
      if (recv.startsWith("+")) {
        int from=Integer.parseInt(
            recieved.substring(1,3),16);
        int to=Integer.parseInt(
            recieved.substring(3,5),16);
        int koma;
        for(koma=0;koma<16;koma++) {
          if (recieved.substring(5,7).
              equals(KomaStr[koma])) {
            break;
          }
        }
        koma|=SENTE;
        boolean promote=(from!=0 && k.ban[from]!=koma);
        int capture=k.ban[to];
        Te ret=new Te(koma,from,to,promote,capture);
        k.move(ret);
        this.k.move(ret);
        int mytime=0;
        try {
          mytime=Integer.parseInt(recv.substring(recv.indexOf(",T")+2));
        }catch(NumberFormatException ne){
        }
        k.spentTime[0]+=mytime;
        k.teban=GOTE;
        k.tesu++;
      }
      if (recv.startsWith("-")) {
        int from=Integer.parseInt(
            recieved.substring(1,3),16);
        int to=Integer.parseInt(
            recieved.substring(3,5),16);
        int koma;
        for(koma=0;koma<16;koma++) {
          if (recieved.substring(5,7).
              equals(KomaStr[koma])) {
            break;
          }
        }
        koma|=GOTE;
        boolean promote=(from!=0 && k.ban[from]!=koma);
        int capture=k.ban[to];
        Te ret=new Te(koma,from,to,promote,capture);
        k.move(ret);
        this.k.move(ret);
        int mytime=0;
        try {
          mytime=Integer.parseInt(recv.substring(recv.indexOf(",T")+2));
        }catch(NumberFormatException ne){
        }
        k.spentTime[1]+=mytime;
        k.teban=SENTE;
        k.tesu++;
      }
    }
    System.out.println(recv);
    // ゲームの開始に同意する。
    ow.write("AGREE¥n");
    ow.flush();
    // 「START」が送られるのを待つ。
    recv=ir.readLine();
    if (!recv.startsWith("START:")) {
      teban=-1;
    }
    turn=teban;
    
    return teban;
  }

  private static final String KomaStr[]={
    "　",
    "FU","KY","KE","GI","KI","KA","HI","OU",
    "TO","NY","NK","NG","　","UM","RY"
  };
  
  // 自分の指し手を送る
  public int sendTe(Te t) 
    throws IOException,NumberFormatException
  {
    if (rcvThread==null) {
      // スレッドの開始
      wait=true;
      rcvThread = new Thread(this);
      rcvThread.start();
    }
    
    wait=true;
    waitTime=true;
    k.move(t);
    if (turn==SENTE) {
      ow.write("+");
    } else {
      ow.write("-");
    }
    if (t.from==0) {
      ow.write("00");
    } else {
      ow.write(Integer.toHexString(t.from));
    }
    ow.write(Integer.toHexString(t.to));
    if (t.promote) {
      // 成り駒
      ow.write(KomaStr[(t.koma+8)%16]);
    } else {
      ow.write(KomaStr[t.koma%16]);
    }
    ow.write("¥n");
    ow.flush();
    while(waitTime) {
      // 所要時間が帰ってくるのを待つ
      try {
        Thread.sleep(100);
      }catch(Exception e){
      }
    }
    if (turn==SENTE) {
      return time[0];
    } else {
      return time[1];
    }
  }

  // 相手の手を待つ
  public Te recvTe() 
    throws IOException,NumberFormatException
  {
    if (rcvThread==null) {
      // スレッドの開始
      wait=true;
      rcvThread = new Thread(this);
      rcvThread.start();
    }
    while(wait) {
      // 待つ…。
      try {
        Thread.sleep(100);
      }catch(Exception e){
      }
    }
    // recievedに相手の手が入ってるので解析
    int from=Integer.parseInt(
      recieved.substring(1,3),16);
    int to=Integer.parseInt(
      recieved.substring(3,5),16);
    int koma;
    for(koma=0;koma<16;koma++) {
      if (recieved.substring(5,7).
           equals(KomaStr[koma])) {
        break;
      }
    }
    if (turn==SENTE) {
      koma|=GOTE;
    } else {
      koma|=SENTE;
    }
    boolean promote=(from!=0 && k.ban[from]!=koma);
    int capture=k.ban[to];
    if (promote) koma=koma & ~Koma.PROMOTE;
    
    Te ret=new Te(koma,from,to,promote,capture);
    k.move(ret);
    return ret;
  }
  
  public void fireWinEvent(String cause) {
    System.out.println(cause+"により、");
    System.out.println("あなたの勝ち！");
    wait=false;
  }
  
  public void fireLoseEvent(String cause) {
    System.out.println(cause+"により、");
    System.out.println("あなたの負け！");
    wait=false;
  }

  public void fireDrawEvent(String cause) {
    System.out.println(cause+"により、");
    System.out.println("引き分け！");
    wait=false;
  }
  
  public void run() {
  try {
    PrintWriter pw=new PrintWriter(
        new OutputStreamWriter(new FileOutputStream("log.csa"),"MS932"));
    pw.println("N+"+nameSente);
    pw.println("N-"+nameGote);
    pw.println("P1-KY-KE-GI-KI-OU-KI-GI-KE-KY");
    pw.println("P2 * -HI *  *  *  *  * -KA * ");
    pw.println("P3-FU-FU-FU-FU-FU-FU-FU-FU-FU");
    pw.println("P4 *  *  *  *  *  *  *  *  * ");
    pw.println("P5 *  *  *  *  *  *  *  *  * ");
    pw.println("P6 *  *  *  *  *  *  *  *  * ");
    pw.println("P7+FU+FU+FU+FU+FU+FU+FU+FU+FU");
    pw.println("P8 * +KA *  *  *  *  * +HI * ");
    pw.println("P9+KY+KE+GI+KI+OU+KI+GI+KE+KY");
    pw.println("'先手番");
    pw.println("+");
    pw.flush();
    while(true) {
      String recv=ir.readLine();
      pw.println(recv);
      pw.flush();
      if (recv.equals("#SENNICHITE")) {
        // 次の１行は必ず#DRAWのはず。
        String result=ir.readLine();
        fireDrawEvent(recv);
        break;
      }
      if (recv.equals("#OUTE_SENNICHITE") || 
          recv.equals("#ILLEGAL_MOVE") ||
          recv.equals("#TIME_UP") ||
          recv.equals("#RESIGN") ||
          recv.equals("#JISHOGI")) {
        // 次の１行は必ず#WINか#LOSEのはず。
        String result=ir.readLine();
        if (result.equals("#WIN")) {
          fireWinEvent(recv);
        } else {
          fireLoseEvent(recv);
        }
        break;
      }
      // recvしたものが相手の手。
      if ((recv.startsWith("+") &&
            turn==GOTE) ||
          (recv.startsWith("-") &&
            turn==SENTE)
          ) {
          recieved=recv;
          int histime=0;
          try {
            histime=Integer.parseInt(recv.substring(recv.indexOf(",T")+2));
          }catch(NumberFormatException ne){
          }
          if (turn==SENTE) {
            time[1]=histime;
          } else {
            time[0]=histime;
          }
          wait=false;
      }
      // recvしたものが、自分の手。
      if ((recv.startsWith("-") &&
            turn==GOTE) ||
          (recv.startsWith("+") &&
            turn==SENTE)
          ) {
          int mytime=0;
          try {
            mytime=Integer.parseInt(recv.substring(recv.indexOf(",T")+2));
          }catch(NumberFormatException ne){
          }
          if (turn==SENTE) {
            time[0]=mytime;
          } else {
            time[1]=mytime;
          }
          waitTime=false;
      }
    }
    pw.close();
  }catch(Exception e) {
    e.printStackTrace();
  }
  }
}
