/** 
	@author(Lucas Dimer Justo)
	Classe onde tudo comeca (Menu).
*/

import java.util.Random;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
	public static void main(String[] args){
		try{
			Scanner in = new Scanner(System.in);
			Truco truco = new Truco(in);
			System.out.println("1 - Começar o jogo\n2 - Carregar jogo\nQualquer outra entrada para parar o programa");
			int escolha = in.nextInt();
			if(escolha==1)truco.jogar();
			else if(escolha==2)truco.carregar();
		}catch(FileNotFoundException e){
			System.out.println("Não conseguimos achar o arquivo Cartas.txt ou Salvar.txt! \nLembre-se que eles devem estar na mesma pasta dos arquivos.java e sem .txt no nome!");
			e.printStackTrace();
		}
		
	}
}