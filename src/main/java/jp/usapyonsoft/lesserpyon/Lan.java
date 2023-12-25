package jp.usapyonsoft.lesserpyon;
import java.io.*;

public class Lan implements Player,Constants {
  CSAProtocol csaProtocol;
  
  public Lan(CSAProtocol p) {
    csaProtocol=p;
  }
  
  public Te getNextTe(Kyokumen k,int tesu,int spenttime,int limittime,int byoyomi) {
    Te t=new Te();
    try {
      t=csaProtocol.recvTe();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return t;
  }

}
