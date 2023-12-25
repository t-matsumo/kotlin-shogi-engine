package jp.usapyonsoft.lesserpyon;

public interface KomaMoves {
  // 通常の８方向の定義(盤面上の動き)
  // 
  //  5   6  7
  //     ↑
  //  3←駒→4
  //     ↓
  //  2   1  0
  //
  // 桂馬飛びの方向の定義(盤面上の動き)
  //
  //   8    9
  //
  //     桂
  //
  //   11  10
  
  // 方向の定義に沿った、「段」の移動の定義
  public static final int diffDan[]={
     1, 1, 1, 0, 0,-1,-1,-1,-2,-2, 2, 2
  };

  // 方向の定義に沿った、「筋」の移動の定義
  public static final int diffSuji[]={
    -1, 0, 1, 1,-1, 1, 0,-1, 1,-1,-1, 1
  };

  // 方向の定義に沿った、「移動」の定義
  public static final int diff[]={
    diffSuji[0]*16+diffDan[0],
    diffSuji[1]*16+diffDan[1],
    diffSuji[2]*16+diffDan[2],
    diffSuji[3]*16+diffDan[3],
    diffSuji[4]*16+diffDan[4],
    diffSuji[5]*16+diffDan[5],
    diffSuji[6]*16+diffDan[6],
    diffSuji[7]*16+diffDan[7],
    diffSuji[8]*16+diffDan[8],
    diffSuji[9]*16+diffDan[9],
    diffSuji[10]*16+diffDan[10],
    diffSuji[11]*16+diffDan[11]
  };


  // ある方向にある駒が動けるかどうかを表すテーブル。
  // 添え字の１つめが方向で、２つめが駒の種類である。
  // 香車や飛車、角などの一直線に動く動きについては、後述のcanJumpで表し、
  // このテーブルではfalseとしておく。
  public static final boolean canMove[][]={
    // 方向 0 斜め左下への動き
    {
     false,false,false,false,false,false,false,false,// 先手でも後手でもない駒
     false,false,false,false,false,false,false,false,// 先手でも後手でもない駒
     false,false,false,false, true,false,false,false,// 空、先手の歩香桂銀金角飛
      true,false,false,false,false,false,false, true,// 先手の王、と杏圭全　馬竜
     false,false,false,false, true, true,false,false,// 空、後手の歩香桂銀金角飛
      true, true, true, true, true, true,false, true // 後手の王、と杏圭全　馬竜
    },
    // 方向 1 真下への動き
    {
     false,false,false,false,false,false,false,false,// 先手でも後手でもない駒
     false,false,false,false,false,false,false,false,// 先手でも後手でもない駒
     false,false,false,false,false, true,false,false,// 空、先手の歩香桂銀金角飛
      true, true, true, true, true,false, true,false,// 先手の王、と杏圭全　馬竜
     false, true,false,false, true, true,false,false,// 空、後手の歩香桂銀金角飛
      true, true, true, true, true,false, true,false // 後手の王、と杏圭全　馬竜
    },
    // 方向 2 斜め右下への動き
    {
     false,false,false,false,false,false,false,false,// 先手でも後手でもない駒
     false,false,false,false,false,false,false,false,// 先手でも後手でもない駒
     false,false,false,false, true,false,false,false,// 空、先手の歩香桂銀金角飛
      true,false,false,false,false,false,false, true,// 先手の王、と杏圭全　馬竜
     false,false,false,false, true, true,false,false,// 空、後手の歩香桂銀金角飛
      true, true, true, true, true, true,false, true // 後手の王、と杏圭全　馬竜
    },
    // 方向 3 左への動き
    {
     false,false,false,false,false,false,false,false,// 先手でも後手でもない駒
     false,false,false,false,false,false,false,false,// 先手でも後手でもない駒
     false,false,false,false,false, true,false,false,// 空、先手の歩香桂銀金角飛
      true, true, true, true, true,false, true,false,// 先手の王、と杏圭全　馬竜
     false,false,false,false,false, true,false,false,// 空、後手の歩香桂銀金角飛
      true, true, true, true, true,false, true,false // 後手の王、と杏圭全　馬竜
    },
    // 方向 4 右への動き
    {
     false,false,false,false,false,false,false,false,// 先手でも後手でもない駒
     false,false,false,false,false,false,false,false,// 先手でも後手でもない駒
     false,false,false,false,false, true,false,false,// 空、先手の歩香桂銀金角飛
      true, true, true, true, true,false, true,false,// 先手の王、と杏圭全　馬竜
     false,false,false,false,false, true,false,false,// 空、後手の歩香桂銀金角飛
      true, true, true, true, true,false, true,false // 後手の王、と杏圭全　馬竜
    },
    // 方向 5 斜め左上への動き
    {
     false,false,false,false,false,false,false,false,// 先手でも後手でもない駒
     false,false,false,false,false,false,false,false,// 先手でも後手でもない駒
     false,false,false,false, true, true,false,false,// 空、先手の歩香桂銀金角飛
      true, true, true, true, true,false,false, true,// 先手の王、と杏圭全　馬竜
     false,false,false,false, true,false,false,false,// 空、後手の歩香桂銀金角飛
      true,false,false,false,false,false,false, true // 後手の王、と杏圭全　馬竜
    },
    // 方向 6 真上への動き
    {
     false,false,false,false,false,false,false,false,// 先手でも後手でもない駒
     false,false,false,false,false,false,false,false,// 先手でも後手でもない駒
     false, true,false,false, true, true,false,false,// 空、先手の歩香桂銀金角飛
      true, true, true, true, true,false, true,false,// 先手の王、と杏圭全　馬竜
     false,false,false,false,false, true,false,false,// 空、後手の歩香桂銀金角飛
      true, true, true, true, true,false, true,false // 後手の王、と杏圭全　馬竜
     },
     // 方向 7 斜め右上への動き
     {
      false,false,false,false,false,false,false,false,// 先手でも後手でもない駒
      false,false,false,false,false,false,false,false,// 先手でも後手でもない駒
      false,false,false,false, true, true,false,false,// 空、先手の歩香桂銀金角飛
       true, true, true, true, true,false,false, true,// 先手の王、と杏圭全　馬竜
      false,false,false,false, true,false,false,false,// 空、後手の歩香桂銀金角飛
       true,false,false,false,false,false,false, true // 後手の王、と杏圭全　馬竜
     },
     // 方向 8 先手の桂馬飛び
     {
      false,false,false,false,false,false,false,false,// 先手でも後手でもない駒
      false,false,false,false,false,false,false,false,// 先手でも後手でもない駒
      false,false,false, true,false,false,false,false,// 空、先手の歩香桂銀金角飛
      false,false,false,false,false,false,false,false,// 先手の王、と杏圭全　馬竜
      false,false,false,false,false,false,false,false,// 空、後手の歩香桂銀金角飛
      false,false,false,false,false,false,false,false // 後手の王、と杏圭全　馬竜
     },
     // 方向 9 先手の桂馬飛び
     {
      false,false,false,false,false,false,false,false,// 先手でも後手でもない駒
      false,false,false,false,false,false,false,false,// 先手でも後手でもない駒
      false,false,false, true,false,false,false,false,// 空、先手の歩香桂銀金角飛
      false,false,false,false,false,false,false,false,// 先手の王、と杏圭全　馬竜
      false,false,false,false,false,false,false,false,// 空、後手の歩香桂銀金角飛
      false,false,false,false,false,false,false,false // 後手の王、と杏圭全　馬竜
     },
     // 方向10 後手の桂馬飛び
     {
      false,false,false,false,false,false,false,false,// 先手でも後手でもない駒
      false,false,false,false,false,false,false,false,// 先手でも後手でもない駒
      false,false,false,false,false,false,false,false,// 空、先手の歩香桂銀金角飛
      false,false,false,false,false,false,false,false,// 先手の王、と杏圭全　馬竜
      false,false,false, true,false,false,false,false,// 空、後手の歩香桂銀金角飛
      false,false,false,false,false,false,false,false // 後手の王、と杏圭全　馬竜
     },
     // 方向11 後手の桂馬飛び
     {
      false,false,false,false,false,false,false,false,// 先手でも後手でもない駒
      false,false,false,false,false,false,false,false,// 先手でも後手でもない駒
      false,false,false,false,false,false,false,false,// 空、先手の歩香桂銀金角飛
      false,false,false,false,false,false,false,false,// 先手の王、と杏圭全　馬竜
      false,false,false, true,false,false,false,false,// 空、後手の歩香桂銀金角飛
      false,false,false,false,false,false,false,false // 後手の王、と杏圭全　馬竜
     }
  };

  // ある方向にある駒が飛べるかどうかを表すテーブル。
  // 添え字の１つめが方向で、２つめが駒の種類である。
  // 香車や飛車、角、竜、馬の一直線に動く動きについては、こちらで表す。
  static final public boolean canJump[][]={
    // 方向 0 斜め左下への動き
    {
     false,false,false,false,false,false,false,false,// 先手でも後手でもない駒
     false,false,false,false,false,false,false,false,// 先手でも後手でもない駒
     false,false,false,false,false,false, true,false,// 空、先手の歩香桂銀金角飛
     false,false,false,false,false,false, true,false,// 先手の王、と杏圭全　馬竜
     false,false,false,false,false,false, true,false,// 空、後手の歩香桂銀金角飛
     false,false,false,false,false,false, true,false // 後手の王、と杏圭全　馬竜
    },
    // 方向 1 真下への動き
    {
     false,false,false,false,false,false,false,false,// 先手でも後手でもない駒
     false,false,false,false,false,false,false,false,// 先手でも後手でもない駒
     false,false,false,false,false,false,false, true,// 空、先手の歩香桂銀金角飛
     false,false,false,false,false,false,false, true,// 先手の王、と杏圭全　馬竜
     false,false, true,false,false,false,false, true,// 空、後手の歩香桂銀金角飛
     false,false,false,false,false,false,false, true // 後手の王、と杏圭全　馬竜
    },
    // 方向 2 斜め右下への動き
    {
     false,false,false,false,false,false,false,false,// 先手でも後手でもない駒
     false,false,false,false,false,false,false,false,// 先手でも後手でもない駒
     false,false,false,false,false,false, true,false,// 空、先手の歩香桂銀金角飛
     false,false,false,false,false,false, true,false,// 先手の王、と杏圭全　馬竜
     false,false,false,false,false,false, true,false,// 空、後手の歩香桂銀金角飛
     false,false,false,false,false,false, true,false // 後手の王、と杏圭全　馬竜
    },
    // 方向 3 左への動き
    {
     false,false,false,false,false,false,false,false,// 先手でも後手でもない駒
     false,false,false,false,false,false,false,false,// 先手でも後手でもない駒
     false,false,false,false,false,false,false, true,// 空、先手の歩香桂銀金角飛
     false,false,false,false,false,false,false, true,// 先手の王、と杏圭全　馬竜
     false,false,false,false,false,false,false, true,// 空、後手の歩香桂銀金角飛
     false,false,false,false,false,false,false, true // 後手の王、と杏圭全　馬竜
    },
    // 方向 4 右への動き
    {
     false,false,false,false,false,false,false,false,// 先手でも後手でもない駒
     false,false,false,false,false,false,false,false,// 先手でも後手でもない駒
     false,false,false,false,false,false,false, true,// 空、先手の歩香桂銀金角飛
     false,false,false,false,false,false,false, true,// 先手の王、と杏圭全　馬竜
     false,false,false,false,false,false,false, true,// 空、後手の歩香桂銀金角飛
     false,false,false,false,false,false,false, true // 後手の王、と杏圭全　馬竜
    },
    // 方向 5 斜め左上への動き
    {
     false,false,false,false,false,false,false,false,// 先手でも後手でもない駒
     false,false,false,false,false,false,false,false,// 先手でも後手でもない駒
     false,false,false,false,false,false, true,false,// 空、先手の歩香桂銀金角飛
     false,false,false,false,false,false, true,false,// 先手の王、と杏圭全　馬竜
     false,false,false,false,false,false, true,false,// 空、後手の歩香桂銀金角飛
     false,false,false,false,false,false, true,false // 後手の王、と杏圭全　馬竜
    },
    // 方向 6 真上への動き
    {
     false,false,false,false,false,false,false,false,// 先手でも後手でもない駒
     false,false,false,false,false,false,false,false,// 先手でも後手でもない駒
     false,false, true,false,false,false,false, true,// 空、先手の歩香桂銀金角飛
     false,false,false,false,false,false,false, true,// 先手の王、と杏圭全　馬竜
     false,false,false,false,false,false,false, true,// 空、後手の歩香桂銀金角飛
     false,false,false,false,false,false,false, true // 後手の王、と杏圭全　馬竜
    },
    // 方向 7 斜め右上への動き
    {
     false,false,false,false,false,false,false,false,// 先手でも後手でもない駒
     false,false,false,false,false,false,false,false,// 先手でも後手でもない駒
     false,false,false,false,false,false, true,false,// 空、先手の歩香桂銀金角飛
     false,false,false,false,false,false, true,false,// 先手の王、と杏圭全　馬竜
     false,false,false,false,false,false, true,false,// 空、後手の歩香桂銀金角飛
     false,false,false,false,false,false, true,false // 後手の王、と杏圭全　馬竜
    }
    // 桂馬の方向に飛ぶ駒はないので、以下は省略。
  };
}
