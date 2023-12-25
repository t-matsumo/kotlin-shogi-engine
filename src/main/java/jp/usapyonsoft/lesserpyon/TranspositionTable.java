package jp.usapyonsoft.lesserpyon;

// 局面に与えたハッシュコードで格納するクラス
public class TranspositionTable {
  public TTEntry table[]=new TTEntry[0x100000];
  
  public TTEntry get(int HashVal) {
    if (table[HashVal & 0x0fffff]!=null && 
        table[HashVal & 0x0fffff].HashVal==HashVal) {
      return table[HashVal & 0x0fffff];
    }
    return null;
  }
  
  public void add(int HashVal,int value,int alpha,int beta,Te best,
      int depth,int remainDepth,int tesu) {
    TTEntry e=get(HashVal);
    if (e==null) {
      e=new TTEntry();
      e.second=null;
    } else {
      e.second=e.best;
    }
    e.best=best;
    e.value=value;
    if (value<=alpha) {
      e.flag=TTEntry.UPPER_BOUND;
    } else if (value>=beta) {
      e.flag=TTEntry.LOWER_BOUND;
    } else {
      e.flag=TTEntry.EXACTLY_VALUE;
    }
    e.depth=depth;
    e.remainDepth=remainDepth;
    e.tesu=tesu;
    table[HashVal & 0x0fffff]=e;
  }
      
}
