/** 
	@author(Lucas Dimer Justo)
	Classe que armazena as cartas que existem no jogo Truco (pega essas informacoes de um txt, e controla suas
	 operacoes atraves dos metodos.
*/

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;

public class Baralho {

	private Carta[] baralho;
	private int[] rankings;

	public Baralho()throws FileNotFoundException{
		baralho = new Carta[40];
		preencheBaralho();
	}

	public void preencheBaralho()throws FileNotFoundException{
		/** 
			@author(Lucas Dimer Justo)
			Instancia todas as cartas existentes do Truco (armazenadas em um txt) e armazena elas em um vetor.
		*/
		File arq = new File("Cartas.txt");
		Scanner in = new Scanner(arq);
		int indice = 0;
		rankings = new int[14];

		while(in.hasNext()){
			int numero = in.nextInt();
			String naipe = in.next();
			int rank = in.nextInt();
			if(numero==10 || numero==11 || numero==12)baralho[indice++] = new Carta(numero,naipe,rank,0);
			else baralho[indice++] = new Carta(numero,naipe,rank,numero);
			rankings[rank-1]++;
		}
	}

	public int melhoresQue(int rank){
		/** 
			@author(Lucas Dimer Justo)
			Retorna o numero de cartas que ganham do ranking da carta passado por parametro, usado para ajudar nas decisoes
			de jogada do computador.
		*/
		int res = 0;
		if(rank>1 && rank<=14){
			for(int i = rank-2;i>=0;i--){
				res+=rankings[i];
			}
		}
		return res;
	}

	public int rankQTD(int rank){
		return rankings[rank-1];
	}

	public void shuffle(){
		/** 
			@author(Lucas Dimer Justo)
			Embaralhar, usada na classe Truco antes de atribuir as maos.
		*/
		Random random = new Random();
		for(int i = 0;i<baralho.length;i++){
			int rand = random.nextInt(baralho.length);
			Carta aux = baralho[rand];
			baralho[rand] = baralho[i];
			baralho[i] = aux;
		}
	}

	public Carta getTop(){
		/** 
			@author(Lucas Dimer Justo)
			Remove e retorna a primeira carta sem alterar a ordem das outras, usada na classe Truco para
			montar as maos.
		*/
		Carta top = baralho[0];
		rankings[top.getRank()-1]--;
		for(int i = 0;i<baralho.length-1;i++){
			baralho[i]=baralho[i+1];
		}
		return top;
	}

	@Override
	public String toString(){
		String res = "[ ";
		for(int i = 0; i<baralho.length; i++){
			if(i==baralho.length-1)res+=baralho[i]+" ]";
			else res+=baralho[i]+", ";
		}
		return res;
	}

}