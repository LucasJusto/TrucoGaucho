public class Mesa{
	private Carta[] mesa;
	private int topo;
	private int rodadaNaMesa;
	private String ganhador1;//resultado da primeira rodada (empate, jogador, computador)
	private String ganhador2;//resultado da segunda rodada (empate, jogador, computador)
	private String ganhador3;//resultado da terceira rodada (empate, jogador, computador)
	private int estaRodadaVale;//os pontos que serao adicionados ao vencedor da mesa (1 normal; 2 truco; 3 retruco; 4 vale quatro)
 	private Jogador jogador;
 	private Jogador computador;
 	private Jogador mao;
 	private String ultimoJogou;//"computador" ou "jogador"

	public Mesa(Jogador jogador, Jogador computador,int rodada){
		mesa = new Carta[6];
		topo=-1;
		rodadaNaMesa=0;
		estaRodadaVale=1;
		this.jogador = jogador;
		this.computador = computador;
		if(rodada%2==1){
			mao=jogador;
		}
		else{
			mao=computador;
		}
	}

	public String addCarta(Carta c, String quemJogou){
		/**
			@author(Lucas Dimer Justo)
			retorna:
			"jogador": se o proximo a jogar for o jogador
			"computador": se o proximo a jogar for o computador
			"acabou": se nao tiver proximo
		*/
		topo++;
		if(topo%2==0){
			//topo é par, não tem disputa pois há um número impar de cartas na mesa.
			mesa[topo]=c;
			ultimoJogou=quemJogou;
			rodadaNaMesa++;
			System.out.println(this);
			if(quemJogou.equals("jogador"))return "computador";
			else return "jogador";
		}
		else{
			//DISPUTA
			//topo é impar entao os 2 jogaram uma carta (vetor comeca em 0). TEMOS UMA DISPUTA
			mesa[topo]=c;
			System.out.println(this);
			Carta jg;
			Carta pc;
			if(ultimoJogou.equals("jogador")){
				jg=mesa[topo-1];
				pc=mesa[topo];
			}
			else{
				jg=mesa[topo];
				pc=mesa[topo-1];
			}
			if(jogadorVenceu(jg,pc)==1){
				//jogador ganhou rodada
				if(rodadaNaMesa==1){
					System.out.println("Você venceu a primeira rodada da mesa!");
					ganhador1 = jogador.getNome();
					return "jogador";
				}
				else if(rodadaNaMesa==2){
					if(ganhador1.equals("empate")||ganhador1.equals(jogador.getNome())){
						ganhador2 = jogador.getNome();
						System.out.println("Você venceu a Mesa e com isso ganhou "+estaRodadaVale+" pontos!");
						jogador.addPontos(estaRodadaVale);
						return "acabou";
					}
					else{
						System.out.println("Você venceu a segunda rodada da mesa!");
						ganhador2 = jogador.getNome();
						return "jogador";
					}
				}
				else if(rodadaNaMesa==3){
						ganhador3 = jogador.getNome();
						System.out.println("Você venceu a Mesa e com isso ganhou "+estaRodadaVale+" pontos!");
						jogador.addPontos(estaRodadaVale);
						return "acabou";
				}
				return "jogador";
			}
			else if(jogadorVenceu(jg,pc)==-1){
				//computador ganhou rodada
				if(rodadaNaMesa==1){
					System.out.println("O computador venceu a primeira rodada da mesa!");
					ganhador1 = "computador";
				}
				else if(rodadaNaMesa==2){
					if(ganhador1.equals("empate")||ganhador1.equals("computador")){
						ganhador2 = "computador";
						System.out.println("O computador venceu a Mesa e com isso ganhou "+estaRodadaVale+" pontos!");
						computador.addPontos(estaRodadaVale);
						return "acabou";
					}
					else{
						ganhador2 = "computador";
						System.out.println("O computador venceu a segunda rodada da mesa!");
						return "computador";
					}
				}
				else if(rodadaNaMesa==3){
						ganhador3 = "computador";
						System.out.println("O computador venceu a Mesa e com isso ganhou "+estaRodadaVale+" pontos!");
						computador.addPontos(estaRodadaVale);
						return "acabou";
				}
				return "computador";
			}
			else{
				//empate
				if(rodadaNaMesa==1){
					System.out.println("A primeira rodada da mesa empatou!");
					ganhador1="empate";
					if(mao.getNome().equals("computador"))return "computador";
					else return "jogador";
				}
				else if(rodadaNaMesa==2){
					if(ganhador1.equals(jogador.getNome())){
						System.out.println("Você venceu a mesa e com isso ganhou "+estaRodadaVale+" pontos!");
						jogador.addPontos(estaRodadaVale);
						return "acabou";
					}
					else if(ganhador1.equals("computador")){
						System.out.println("O computador venceu a mesa e com isso ganhou "+estaRodadaVale+" pontos!");
						computador.addPontos(estaRodadaVale);
						return "acabou";
					}
					else{
						System.out.println("A segunda rodada da mesa empatou!");
						ganhador2 = "empate";
					}
				}
				else{
					if(ganhador1.equals(jogador.getNome())){
						System.out.println("Você venceu a mesa e com isso ganhou "+estaRodadaVale+" pontos!");
						jogador.addPontos(estaRodadaVale);
						return "acabou";
					}
					else if(ganhador1.equals("computador")){
						System.out.println("O computador venceu a mesa e com isso ganhou "+estaRodadaVale+" pontos!");
						computador.addPontos(estaRodadaVale);
						return "acabou";
					}
					else{
						System.out.println("Todas rodadas empataram, "+mao+" ganhou "+estaRodadaVale+" pontos por ser o mão!");
						mao.addPontos(estaRodadaVale);
						return "acabou";
					}
				}
				if(mao.getNome().equals("computador"))return "computador";
				else return "jogador";
			}
		}
	}

	public int jogadorVenceu(Carta j,Carta c){
		//O espadao (melhor carta) tem rank 1, e os 4 (pior carta) tem rank 14. Ou seja o menor rank ganha.
		//-1 jogador perdeu, 0 empate, 1 jogador venceu. 
		if(j.getRank()<c.getRank()){
			return 1;
		}
		else if(j.getRank()>c.getRank()){
			return -1;
		}
		return 0;
	}

	public void setValorRodada(int pts){
		estaRodadaVale=pts;
	}

	public int valorRodada(){
		return estaRodadaVale;
	}

	@Override
	public String toString(){
		return 
		"________________________________________________________________________________________________________"+
		"\nEstamos na disputa "+rodadaNaMesa+":     \n"+
		"Disputa 1:   Primeira Carta: "+mesa[0]+"   X   Segunda Carta: "+mesa[1]+"     Ganhador: "+ganhador1+"\n"+
		"Disputa 2:   Terceira Carta: "+mesa[2]+"   X   Quarta Carta: "+mesa[3]+"     Ganhador: "+ganhador2+"\n"+
		"Disputa 3:   Quinta Carta: "+mesa[4]+"   X   Sexta Carta: "+mesa[5]+"     Ganhador: "+ganhador3+"\n"+
		"________________________________________________________________________________________________________";

	}
}