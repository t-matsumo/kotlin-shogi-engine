package jp.usapyonsoft.lesserpyon;
import java.util.Vector;
import java.util.Random;
import java.io.*;

// 定跡データを読み込み、定跡にある局面であれば、そこで指された手を返す。
// 同一局面が複数ある場合、手の候補から乱数で選択する。
public class Joseki implements Constants {
  // 定跡を
  Joseki child=null;
  byte [][] josekiData;
  int numJoseki;

  // 乱数の生成
  Random random;
  
  Kyokumen josekiKyokumen;
  
  public Joseki(String josekiFileName) {
    random=new Random();
    if (josekiFileName.indexOf(",")>=0) {
      // 「子」定跡として、","以降のファイルを読み込む。
      child=new Joseki(josekiFileName.substring(josekiFileName.indexOf(",")+1));
      // 自分自身の読み込む定跡ファイルは、,の前までのファイル名
      josekiFileName=josekiFileName.substring(0,josekiFileName.indexOf(",")-1);
    }
    // ファイルから定跡データを読み込む。
    try {
      File f=new File(josekiFileName);
      FileInputStream in=new FileInputStream(f);
      numJoseki=(int)(f.length()/512);
      josekiData=new byte[numJoseki][512];
      for(int i=0;i<numJoseki && in.read(josekiData[i])>0;i++) {
      }
    } catch(Exception e) {
      numJoseki=0;
    }
  }
  
  public Te josekiByteToTe(byte to,byte from,Kyokumen k) {
    // byteは-128〜127と符号付で扱いにくいため、符号を取ったintにする。
    int f=((int)from)& 0xff;
    int t=((int)to)  & 0xff;
    int koma=0;
    boolean promote=false;

    if (f>100) {
      // fが100以上なら、(手番の側の)駒を打つ手。
      koma=(f-100)|k.teban;
      f=0;
    } else {
      // fを、このプログラムの中で使う座標の方式へ変換
      int fs = (f - 1) % 9 + 1; // 筋
      int fd = (f + 8) / 9;     // 段
      f=fs*16+fd;
      // 実際の駒は、盤面から得る。
      koma=k.ban[f];
    }
    // tが100以上なら、成る手。
    if (t>100) {
      promote=true;
      t=t-100;
    }
    // tを、このプログラムの中で使う座標の方式へ変換
    int ts = (t - 1) % 9 + 1; // 筋
    int td = (t + 8) / 9;     // 段
    t=ts*16+td;
    // 手を作成。
    return new Te(koma,f,t,promote,k.ban[t]);
  }
  
  public Te fromJoseki(Kyokumen k,int tesu) {
    // tesuには、実際の手数が渡される（1手目から始まる）が、
    // 定跡のデータは0手目から始まるので、1ずらしておく。
    tesu=tesu-1;
    // 定跡にあった候補手を入れる
    Vector v=new Vector();
    // 定跡で進めてみた局面を作成する
    // この局面と、渡された局面を比較する。
    Kyokumen josekiKyokumen;
    josekiKyokumen=new Kyokumen();
    for(int i=0;i<numJoseki;i++) {
      // 平手で初期化する。
      // 駒落ちなどを指させるには、改良が必要。
      josekiKyokumen.initHirate();
      int j=0;
      for(j=0;j<tesu;j++) {
        if (josekiData[i][j*2]==(byte)0 || josekiData[i][j*2]==(byte)0xff) {
          break;
        }
        Te te=josekiByteToTe(josekiData[i][j*2],josekiData[i][j*2+1],josekiKyokumen);
        josekiKyokumen.move(te);
        if (josekiKyokumen.teban==SENTE) {
          josekiKyokumen.teban=GOTE;
        } else {
          josekiKyokumen.teban=SENTE;
        }
      }
      // 局面が一致するか？
      if (j==tesu && josekiKyokumen.equals(k)) {
        // 局面が一致した。定跡データから次の手を引き出す。
        if (josekiData[i][tesu*2]==(byte)0 || josekiData[i][tesu*2]==(byte)0xff) {
          // 局面は一致していたが、ここで指す手がなかった。
          continue;
        }
        // 候補手を作成
        Te te=josekiByteToTe(josekiData[i][tesu*2],josekiData[i][tesu*2+1],k);
        v.add(te);
      }
    }
    if (v.size()==0) {
      // 候補手がなかった。
      if (child!=null) {
        // 子定跡があるときは、その結果を返す。
        return child.fromJoseki(k,tesu);
      }
      // 候補手がなかったので、nullを返す。
      return null;
    } else {
      // 候補手の中からランダムで選択する。
      return (Te)v.elementAt(random.nextInt(v.size()));
    }
  }
}
