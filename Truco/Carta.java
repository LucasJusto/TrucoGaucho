/** 
	@author(Lucas Dimer Justo)
	Classe que armazena os atributos das cartas, e controla suas operacoes atraves dos metodos.
*/

public class Carta {
	
	private String naipe;
	private int numero;
	private int rank;
	private int pontos;

	public Carta(int numero, String naipe, int rank,int pontos){
		this.naipe = naipe;
		this.numero = numero;
		this.rank = rank;
		this.pontos = pontos;
	}

	public int getRank(){
		return rank;
	}

	@Override
	public String toString(){
		return numero + "  " + naipe;
	}

	public String getNaipe(){
		return naipe;
	}

	public int getPontos(){
		return pontos;
	}

}