/** 
	@author(Lucas Dimer Justo)
	Classe que armazena a mao do jogador, e controla suas operacoes atraves dos metodos.
*/

public class Mao{

	private Carta[] mao;
	private int size;

	public Mao(){
		size = 0;
		mao = new Carta[3];
	}

	public void add(Carta c){
		mao[size++] = c;
	}

	public Carta remove(int pos){
		/** 
			@author(Lucas Dimer Justo)
			Remove a carta escolhida, sem mexer na posiçao das outras.
		*/
		Carta res = mao[pos-1];
		for(int i = pos-1;i<size;i++){
			if(i<2)mao[i]=mao[i+1];
		}
		size--;
		return res;
	}

	public Carta get(int pos){
		return mao[pos];
	}

	public int size(){
		return size;
	}

	public void clear(){
		/** 
			@author(Lucas Dimer Justo)
			Atribui 0 ao tamanho da mao, para poder adicionar no inicio novamente (atraves do metodo add).
		*/
		size=0;
	}

	public int calculaFlor(){
		/** 
			@author(Lucas Dimer Justo)
			Metodo que checa se as 3 cartas tem o mesmo naipe. Se tiver retorna seus pontos, se nao retorna 0 (zero).
		*/
		String naipe = mao[0].getNaipe();
		int res = 20+mao[0].getPontos();

		for(int i = 1;i<3;i++){
			if(naipe.equals(mao[i].getNaipe()))res+=mao[i].getPontos();
			else return 0;
		}
		return res;
	}

	public int calculaEnvido(){
		/** 
			@author(Lucas Dimer Justo)
			Metodo para calcular o envido, ele calcula os pontos de cada naipe e retorna o maior.
		*/
		int qtdPaus = 0;
		int qtdOuros = 0;
		int qtdEspadas = 0;
		int qtdCopas = 0;
		int paus = 0;
		int ouros = 0;
		int espadas = 0;
		int copas = 0;

		for(int i = 0;i<3;i++){
			if(mao[i].getNaipe().equals("paus")){
				if(++qtdPaus==2)paus+= 20+mao[i].getPontos();
				else paus+= mao[i].getPontos();
			}
			else if(mao[i].getNaipe().equals("copas")){
				if(++qtdCopas==2)copas+= 20+mao[i].getPontos();
				else copas+= mao[i].getPontos();
			}
			else if(mao[i].getNaipe().equals("ouros")){
				if(++qtdOuros==2)ouros+= 20+mao[i].getPontos();
				else ouros+= mao[i].getPontos();
			}
			else if(mao[i].getNaipe().equals("espadas")){
				if(++qtdEspadas==2)espadas+= 20+mao[i].getPontos();
				else espadas+= mao[i].getPontos();
			}
		}

		return max(paus,ouros,espadas,copas);
	}

	public int max(int a, int b, int c, int d){
		/** 
			@author(Lucas Dimer Justo)
			Recebe os 4 envidos (1 de cada naipe) do metodo calcula envido e retorna o maior.
			
		*/
		int[] aux = new int[3];
		int maior = a;
		aux[0]=b;
		aux[1]=c;
		aux[2]=d;
		for(int i = 0;i<3;i++){
			if(aux[i]>maior)maior=aux[i];
		}
		return maior;
	}

	public int calculaPontosDeTruco(){
		//retorna de 6 (espadao, bastiao e 7 espadas) ate 42(4 4 4). para ajudar o computador a decidir se aceita truco
		int res = 0;
		for(int i=0;i<size;i++){
			res+=mao[i].getRank();
		}
		return res;
	}

	public String mostraMaoComputador(){
		String res = "";
		for(int i = 0; i<size;i++){
			res+= "Carta "+(i+1)+": "+mao[i]+"   ";
		}
		return res;		
	}
	@Override
	public String toString(){
		String res = "";
		for(int i = 0; i<size;i++){
			res+= "Carta "+(i+1)+": "+mao[i]+"   ";
		}
		return res+"          <------- Sua mão está aqui!";
	}
}