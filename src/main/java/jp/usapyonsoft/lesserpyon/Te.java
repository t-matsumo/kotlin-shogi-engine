package jp.usapyonsoft.lesserpyon;

public class Te implements Cloneable,Constants {
  public int koma;                 // どの駒が動いたか
  public int from;                 // 動く前の位置（持ち駒の場合、0）
  int to;                   // 動いた先の位置
  boolean promote;         // 成る場合、true 成らない場合 false
  int capture;              // 取った駒（Kyokumenのback関数で利用する）
  int value;                // 手の価値
  int value2;               // 手の価値：攻撃点を加味しない価値。
  
  public Te(int _koma,int _from,int _to,boolean _promote,int _capture) {
    koma=_koma;
    from=_from;
    to=  _to;
    promote=_promote;
    capture=_capture;
    value=0;
  }
  
  public Te() {
    koma=from=to=capture=0;
    promote=false;
    value=0;
  }
  
  public boolean equals(Te te) {
    return (te.koma==koma && te.from==from && te.to==to && te.promote==promote);
  }
  
  public boolean equals(Object _te) {
    Te te=(Te)_te;
    if (te==null) return false;
    return equals(te);
  }
  
  public Object clone() {
    return new Te(koma,from,to,promote,capture);
  }
  
  // 手を文字列で表現する。
  public String toString() {
    return sujiStr[to>>4]+danStr[to&0x0f]+
            Koma.toString(koma)+(promote?"成":"")+
            (from==0?"打　　":"("+sujiStr[from>>4]+danStr[from&0x0f]+")")+
            (promote?"":"　");
  }
}
