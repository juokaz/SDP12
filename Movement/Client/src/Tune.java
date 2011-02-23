import lejos.nxt.*;

public class Tune {

private static int C5 =	523; 
private static int Db5 =	554;
private static int D5 = 587	;
private static int Eb5 =	622 ;
private static int E5 =	659	;
private static int F5 	=698;
private static int Gb5 	=740 ;	
private static int G5 =	784 	;
private static int Ab5 =	830 ;
private static int A5 	=880 	;
private static int Bb5 =	932 	;
private static int B5 =	988	;
private static int C6 	=1047;
private static int Db6 =	1109;
private static int D6 =	1175;
private static int Eb6 =	1245;
private static int E6 	=1319 ;
private static int F6 	=1397 ;
private static int Gb6 	=1480;
private static int G6 	=1568 ;
private static int Ab6 	=1661 ;
private static int A6 	=1760 ;
private static int Bb6 	=1864 ;
private static int B6 	=1976 ;
private static int C7 	=2093;
	
   // NOTE: This tune was generated from a midi using Guy 
   // Truffelli's Brick Music Studio www.aga.it/~guy/lego
   private static final int [] note = {
    G6, 50, Gb6, 50, G6, 50, E6, 50, Eb6, 50, E6, 50,
	C6, 50, B5, 50, C6, 50, G5, 150, E5, 50, F5, 50,
	G5, 50, A5, 50, B5, 50, C6, 50, D6, 50, E6, 50,
	F6, 50, D6, 150, F6, 50, E6, 50, F6, 50, D6, 50,
	Db6, 50, D6, 50, B5, 50, Bb5, 59, B5, 50, G5, 150,
	G6, 50, Gb6, 50, G6, 50, A6, 50, G6, 50, F6, 50,
	E6, 50, D6, 50, C6, 50, C6, 6, D6, 6, E6, 6, F6, 6,
	E6, 6, F6, 6, G6, 6, A6, 6, B6, 6, C7, 50
	};

   public static void Tune() {
		Sound.setVolume(50);
      for(int i=0;i<note.length; i+=2) {
         final int w = note[i+1];
         final int n = note[i];
         if (n != 0) Sound.playTone(n, w*3);
         try { Thread.sleep(w*3); } catch (InterruptedException e) {}
      }
	  Sound.setVolume(0);
   }
}
