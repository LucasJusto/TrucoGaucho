/** 
	@author(Lucas Dimer Justo)
	Com certeza é a classe mais complexa do projeto, é aqui que é feito o controle de TUDO QUE PODE ACONTECER entre
	os pedidos do usuario e as possiveis respostas do computador para ele (respeitando a tabela de porcentagens feita
	para evitar cair sempre em blefes e ter a possibilidade do computador blefar).
*/

import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Random;
import java.io.File;
import java.io.PrintWriter;

public class Truco {
	private Baralho baralho;
	private Scanner in;
	private int rodada;
	private Random random;
	private PrintWriter printer;
	private Scanner file;
	//todas as variaveis abaixo, sao variaveis globais controladas pelos metodos da classe, para ajudar nas decisoes do pc.
	private boolean inicioPc;
	private boolean inicio;
	private String ultimoAumentador;
	private int aceita;
	private int escolha;
	private Jogador jogador;
	private Jogador computador;
	private String jogueiCarta;
	private String jogueiCartaPc;
	private Mesa mesa;


	public Truco(Scanner in)throws FileNotFoundException{
		this.in = in;
		baralho = new Baralho();
		rodada = 1;
		random = new Random();
	}

	public void jogar() throws FileNotFoundException{
		if(jogador==null){
			System.out.println("Informe seu primeiro nome: ");
			jogador = new Jogador(in.next());
			computador = new Jogador("computador");
		}

		while(true){
			rodada(jogador, computador);
		}
		
	}

	public void carregar()throws FileNotFoundException{
		//as coisas estao salvas no txt no formato:  rodada nomeJogador pontosJogador pontosComputador	
		File arq = new File("Salvar.txt");
		file = new Scanner(arq);
		rodada = file.nextInt();
		jogador = new Jogador(file.next());
		computador = new Jogador("computador");
		jogador.addPontos(file.nextInt());
		computador.addPontos(file.nextInt());
		System.out.println("Bem-vindo de volta "+jogador.getNome());
		jogar();
	}

	public void rodada(Jogador jogador, Jogador computador)throws FileNotFoundException{
		boolean vez;
		if(rodada%2==1)vez=true;
		else vez=false;
		System.out.println("***************************************************************************");
		System.out.println("Estamos na rodada: "+rodada+"   Pontos de "+jogador.getNome()+": "+jogador.getPontos()+"   Pontos do Computador: "+computador.getPontos());		
		baralho.shuffle();
		jogador.atribuiMao(baralho.getTop(),baralho.getTop(),baralho.getTop());
		computador.atribuiMao(baralho.getTop(),baralho.getTop(),baralho.getTop());
		inicio = true;
		inicioPc = true;
		ultimoAumentador = "ninguem";
		jogueiCarta="reset";
		jogueiCartaPc="reset";
		jogada(jogador,computador,vez,new Mesa(jogador,computador,rodada));

		//recicla cartas
		baralho.preencheBaralho();
		jogador.clearMao();
		computador.clearMao();
		computador.mostraMaoComputador();//mostra uma copia salva da mao do pc ao terminar a rodada.
		rodada++;
	}

	public void mostraAsMaos(Jogador j, Jogador c){
		j.mostraMao();
		c.mostraMao();
	}

	public void jogada(Jogador jogador,Jogador computador, boolean vez,Mesa m)throws FileNotFoundException{
		escolha=0;
		aceita = random.nextInt(100)+1;//random int de 1 a 100
		mesa = m;
		mostraAsMaos(jogador,computador);
		//jogador
		if(vez){
			if(inicio){
				do{	
					System.out.println("1- Flor   |   2- Envido   |   3- Truco    |    4- Prosseguir  |  5- Salvar e Sair");
					escolha = in.nextInt();
				}while(escolha<1 || escolha >5);
				inicio = false;
			}
			else{
					if(mesa.valorRodada()==1){
						do{
							System.out.println("3- Truco  |  4-Prosseguir  |  5- Salvar e Sair");
							escolha=in.nextInt();
						}while(escolha<3 || escolha>5);
					}
					else if(mesa.valorRodada()==2 && !(ultimoAumentador.equals("jogador"))){
						do{
							System.out.println("3- Retruco  |  4-Prosseguir  |  5- Salvar e Sair");
							escolha=in.nextInt();
						}while(escolha<3 || escolha>5);
					}
					else if(mesa.valorRodada()==3 && !(ultimoAumentador.equals("jogador"))){
						do{
							System.out.println("3- Vale Quatro  |  4-Prosseguir  |  5- Salvar e Sair");
							escolha=in.nextInt();
						}while(escolha<3 || escolha>5);
					}
					else {
						do{
							System.out.println("4-Prosseguir  |  5- Salvar e Sair");
							escolha=in.nextInt();
						}while(escolha<3 || escolha>5);
					}
					
			}
			if(escolha==1){
				florEnvidoUsuario(1);
			}
			if(escolha==2){
				florEnvidoUsuario(2);
			}
			if(escolha==3){
				//truco retruco ou vale quatro
				int trucoPc = computador.getPontosDeTruco();
				if(inicioPc){
					//ver se o computador ja testou seu envido e flor antes de tudo.
					testaEnvidoFlorComputador();
				}
				if(mesa.valorRodada()==1){
					System.out.println("Você pediu Truco!");
					if(trucoPc>=28){
						System.out.println("O computador rejeitou o Truco, você ganhou 1 ponto!");
						finalizaRodada("jogador",1);
					}
					else if(trucoPc>=21){
						
						if(aceita%5==0){
							//20% chance pedir retruco
							do{
								System.out.println("O computador pediu Retruco!   1- Aceitar  |  2- Recusar  |  3- Vale Quatro");
								escolha=in.nextInt();
							}while(escolha<1 || escolha>3);
							if(escolha==1){
								System.out.println("Você aceitou o Retruco!");
								ultimoAumentador="computador";
								mesa.setValorRodada(3);
							}
							else if(escolha==2){
								System.out.println("Você recusou o Retruco, o computador ganhou 2 pontos!");
								finalizaRodada("computador",2);
							}
							else if(escolha==3){
								System.out.println("O computador recusou o Vale Quatro, você ganhou 3 pontos!");
								finalizaRodada("jogador",3);
							}
						}
						else if(aceita%2==0){
							//50% chance aceitar truco
							System.out.println("O computador aceitou o Truco!");
							ultimoAumentador="jogador";
							mesa.setValorRodada(2);
						}
						else{
							System.out.println("O computador recusou o Truco, você ganhou 1 ponto");
							finalizaRodada("jogador",1);
						}
					}
					else{
						//6 a 20 pontos (mao boa)
						if(!(aceita%5==0)){
							//80% chance de pedir retruco
							do{
								System.out.println("O computador pediu Retruco!   1- Aceitar  |  2- Recusar  |  3- Vale Quatro");
								escolha=in.nextInt();
							}while(escolha<1 || escolha>3);
							if(escolha==1){
								System.out.println("Você aceitou o Retruco!");
								ultimoAumentador="computador";
								mesa.setValorRodada(3);
							}
							else if(escolha==2){
								System.out.println("Você recusou o Retruco, o computador ganhou 2 pontos!");
								finalizaRodada("computador",2);
							}
							else if(escolha==3){
								if(aceita%2==1){
									//50% chance aceitar vale quatro
									System.out.println("O computador aceitou o Vale Quatro!");
									ultimoAumentador="jogador";
									mesa.setValorRodada(4);
								}
								else{
									System.out.println("O computador rejeitou o Vale Quatro, você ganhou 3 pontos!");
									finalizaRodada("jogador",3);
								}
							}
						}
						else{
							System.out.println("O computador aceitou o Truco!");
							ultimoAumentador="jogador";
							mesa.setValorRodada(2);
						}

					}
				}
				else if(mesa.valorRodada()==2){
					System.out.println("Você pediu Retruco!");
					if(trucoPc>=28){
						System.out.println("O computador recusou o Retruco, você ganhou 2 pontos!");
						finalizaRodada("jogador",2);
					}
					else if(trucoPc>=21){
						if(aceita%5==0){
							//20% chance aceitar retruco
							System.out.println("O computador aceitou o Retruco!");
							ultimoAumentador="jogador";
							mesa.setValorRodada(3);
						}
						else{
							System.out.println("O computador recusou o Retruco, você ganhou 2 pontos!");
							finalizaRodada("jogador",2);
						}
					}
					else{
						//6 a 20 mao boa
						if(aceita%2==0){
							//50% chance pedir vale Quatro
							do{
								System.out.println("O computador pediu Vale Quatro!   1- Aceitar  |  2- Recusar");
								escolha=in.nextInt();
							}while(escolha<1 || escolha>2);
							if(escolha==1){
								System.out.println("Você aceitou o Vale Quatro!");
								ultimoAumentador="computador";
								mesa.setValorRodada(4);
							}
							else if(escolha==2){
								System.out.println("Você recusou o Vale Quatro, o computador ganhou 3 pontos!");
								finalizaRodada("computador",3);
							}
						}
						else{
							System.out.println("O computador aceitou o Retruco!");
							ultimoAumentador="jogador";
							mesa.setValorRodada(3);
						}
					}
				}
				else if(mesa.valorRodada()==3){
					System.out.println("Você pediu Vale Quatro!");
					if(trucoPc<=20){
						if(aceita%2==0){
							//50% chance de aceitar Vale Quatro
							System.out.println("O computador aceitou o Vale Quatro!");
							ultimoAumentador="jogador";
							mesa.setValorRodada(4);
						}
						else{
							System.out.println("O computador recusou o Vale Quatro, você ganhou 3 pontos!");
							finalizaRodada("jogador",3);
						}
					}
					else{
						System.out.println("O computador recusou o Vale Quatro, você ganhou 3 pontos!");
						finalizaRodada("jogador",3);
					}
				}
				//vai pro prosseguir depois de acertar tudo sobre truco retruco e vale quatro
				escolha=4;

			}
			if(escolha==4){
				//prosseguir
				if(!jogueiCarta.equals("acabou")){
					do{
						jogador.mostraMao();
						System.out.println("Jogue uma carta (use o número ao lado esquerdo dela)!");
						escolha=in.nextInt();
					}while(escolha<1 || escolha>jogador.maoSize());
					jogueiCarta = mesa.addCarta(jogador.jogaCarta(escolha),"jogador");
				}
				if(jogueiCarta.equals("computador")){
					jogada(jogador,computador,false,m);
				}
				else if(jogueiCarta.equals("jogador")){
					jogada(jogador,computador,true,m);
				}
			}
			if(escolha==5){
				//salvar e sair
				File arq = new File("Salvar.txt");
				printer = new PrintWriter(arq);
				printer.print(rodada+" "+jogador.getNome()+" "+jogador.getPontos()+" "+computador.getPontos());
				printer.close();
				System.out.println("O jogo foi salvo com sucesso!");
				System.exit(0);
			}
		}
		//computador
		else{
			if(inicioPc){
				testaEnvidoFlorComputador();
			}
			//joga o truco normal ja testou envido e flor
			if(!inicio)computadorTomaDecisoesSobreTruco();//se o jogador ja teve chance de pedir suas coisas, o pc pode pedir truco.
			if(!jogueiCarta.equals("acabou")){
				int pcEscolhe = random.nextInt(computador.maoSize())+1;
				jogueiCartaPc = mesa.addCarta(computador.jogaCarta(pcEscolhe),"computador");
			}
			if(jogueiCartaPc.equals("computador")){
				jogada(jogador,computador,false,m);
			}
			else if(jogueiCartaPc.equals("jogador")){
				jogada(jogador,computador,true,m);
			}
		}
	}

	public void finalizaRodada(String ganhador, int pts)throws FileNotFoundException{
		if(ganhador.equals("jogador"))jogador.addPontos(pts);
		else computador.addPontos(pts);
		jogueiCarta="acabou";
		jogueiCartaPc="acabou";
	}

	public void computadorTomaDecisoesSobreTruco() throws FileNotFoundException{
		//como o computador toma decisoes sobre o truco retruco e vale quatro 3 vezes as porcentagens serao dividas por 3.
		int trucoPc = computador.getPontosDeTruco();
		if(trucoPc>=28){
			/**
				Se entrou aqui a mao ta horrivel, entao nem pensa em truco ou qualquer outra coisa. 
				Por que criar o if entao? Para manter a documentacao e o padrao das outras cadeias de ifs
				que tomam as decisoes do computador.
			*/
		}
		else if(trucoPc>=21){
			//21 a 27, mao media, 50% de chance truco, 20% retruco, 0% vale quatro. lembrando q divide por 3.
			if(mesa.valorRodada()==1){
				//só pode pedir truco
				if(aceita%10==0 || aceita%20==1){
					//15% chance pedir truco (aprox 50%/3)
					do{
						System.out.println("O computador pediu Truco!   1- Aceitar  |  2- Recusar  |  3- Retruco");
						escolha=in.nextInt();
					}while(escolha<1 || escolha>3);
					if(escolha==1){
						System.out.println("Você aceitou o Truco!");
						ultimoAumentador="computador";
						mesa.setValorRodada(2);
					}
					else if(escolha==2){
						System.out.println("Você recusou o Truco, o computador ganhou 1 ponto!");
						ultimoAumentador="computador";
						finalizaRodada("computador",1);
					}
					else if(escolha==3){
						System.out.println("O computador recusou o Retruco, você ganhou 2 pontos!");
						finalizaRodada("jogador",2);
					}
				}
			}
			else if(mesa.valorRodada()==2 && !(ultimoAumentador.equals("computador"))){
				//só pode pedir retruco
				if(aceita%20==3){
					//5% chance pedir retruco (aprox 20%/3)
					do{
						System.out.println("O computador pediu Retruco!   1-Aceitar  |  2- Recusar  |  3- Vale Quatro");
						escolha=in.nextInt();
					}while(escolha<1 || escolha>3);
					if(escolha==1){
						System.out.println("Você aceitou o Retruco!");
						ultimoAumentador="computador";
						mesa.setValorRodada(3);
					}
					else if(escolha==2){
						System.out.println("Você recusou o Retruco, o computador ganhou 2 pontos!");
						finalizaRodada("computador",2);
					}
					else if(escolha==3){
						System.out.println("O computador recusou o Vale Quatro, você ganhou 3 pontos!");
						finalizaRodada("jogador",3);
					}
				}
			}
			else if(mesa.valorRodada()==3 && !(ultimoAumentador.equals("computador"))){
				//só pode pedir vale quatro
				//o computador nao pede vale quatro com uma mao dessas, mas o if fica pela documentacao e padrao.
			}
		}

		else{
			//6 a 20, mao boa, 100% truco, 80% retruco, 50% vale quatro. lembrando q divide por 3.
			if(mesa.valorRodada()==1){
				//só pode pedir truco
				do{
					System.out.println("O computador pediu Truco!   1- Aceitar  |  2- Recusar  |  3- Retruco");
					escolha=in.nextInt();
				}while(escolha<1 || escolha>3);
				if(escolha==1){
					System.out.println("Você aceitou o Truco!");
					ultimoAumentador="computador";
					mesa.setValorRodada(2);
				}
				else if(escolha==2){
					System.out.println("Você recusou o Truco, o computador ganhou 1 ponto!");
					ultimoAumentador="computador";
					finalizaRodada("computador",1);
				}
				else if(escolha==3){
					System.out.println("Você pediu Retruco!");
					if(!(aceita%5==0)){
						//80% de verdade pq n foi o pc q pediu e sim o jogador.
						System.out.println("O computador aceitou o Retruco!");
						ultimoAumentador="jogador";
						mesa.setValorRodada(3);
					}
					else if(aceita%10==0){
						//10% chance pedir vale quatro dps do retruco
						do{	
							System.out.println("O computador pediu Vale Quatro!   1- Aceitar  |  2- Recusar");
							escolha=in.nextInt();
						}while(escolha<1 || escolha>2);
						if(escolha==1){
							System.out.println("Você aceitou o Vale Quatro!");
							ultimoAumentador="computador";
							mesa.setValorRodada(4);
						}
						else if(escolha==2){
							System.out.println("Você recusou o Vale Quatro, o computador ganhou 3 pontos!");
							finalizaRodada("computador",3);
						}
					}
					else{
						//10% chance recusar o Retruco
						System.out.println("O computador recusou o Retruco, você ganhou 2 pontos!");
						finalizaRodada("jogador",2);
					}
				}
			}
			else if(mesa.valorRodada()==2){
				//só pode pedir retruco
				if(aceita%4==0){
					//25%chance pedir retruco (aprox 80%/2)
					do{
						System.out.println("O computador pediu Retruco!   1- Aceitar  |  2- Recusar  |  3- Vale Quatro");
						escolha=in.nextInt();
					}while(escolha<1 || escolha>3);
					if(escolha==1){
						System.out.println("Você aceitou o Retruco!");
						ultimoAumentador="computador";
						mesa.setValorRodada(3);
					}
					else if(escolha==2){
						System.out.println("Você recusou o Retruco, o computador ganhou 2 pontos!");
						finalizaRodada("computador",2);
					}
					else if(escolha==3){
						if(aceita>50){
							System.out.println("O computador aceitou o Vale Quatro!");
							ultimoAumentador="Jogador";
							mesa.setValorRodada(4);
						}
						else{
							System.out.println("O computador recusou o Vale Quatro, você ganhou 3 pontos!");
							finalizaRodada("jogador",3);
						}
					}
				}
			}
			else if(mesa.valorRodada()==3){
				//só pode pedir vale quatro
				if(aceita%2==0){
					//50% chance pedir vale quatro
					do{
						System.out.println("O computador pediu Vale Quatro!   1- Aceitar  |  2- Recusar");
						escolha=in.nextInt();
					}while(escolha<1 || escolha>2);
					if(escolha==1){
						System.out.println("Você aceitou o Vale Quatro!");
						ultimoAumentador="computador";
						mesa.setValorRodada(4);
					}
					else if(escolha==2){
						System.out.println("Você recusou o Vale Quatro, o computador ganhou 3 pontos!");
						finalizaRodada("computador",3);
					}
				}
			}
		}
	}

	public void testaEnvidoFlorComputador(){
		//CHECA SE O COMPUTADOR QUER FLOR OU ENVIDO
		int florPc = computador.calculaFlor();
		int envidoPc = computador.calculaEnvido();
		int florJogador = jogador.calculaFlor();
		if(florPc>0){
			inicio=false;
			//CHECA Flor
			if(florPc==38 && rodada%2==0){
				do{
					System.out.println("O computador pediu Flor!   1- Aceitar  |  2- Recusar  |  3- Contra Flor  |  4- Falta Flor");
					escolha=in.nextInt();
				}while(escolha<1 || escolha>4);
				if(escolha==2){
					System.out.println("Você recusou a Flor, o computador ganhou 3 pontos!");
					computador.addPontos(3);
				}
				else if(jogador.calculaFlor()>0){
					if(escolha==1){
						System.out.println("Você aceitou a Flor!");
						disputaFlor(jogador,computador,rodada,4);
					}
					else if(escolha==3){
						do{
							System.out.println("O computador pediu Falta Flor!  1- Aceitar  |  2- Recusar");
							escolha=in.nextInt();
						}while(escolha<1 || escolha>2);
						if(escolha==1){
							System.out.println("Você aceitou a Faltar Flor!");
							disputaFlor(jogador,computador,rodada,-1);
						}
						else if(escolha==2){
							System.out.println("Você recusou a Falta Flor, o computador ganhou 5 pontos!");
							computador.addPontos(5);
						}
					}
				}
				else{
					System.out.println("Você não tem 3 cartas do mesmo naipe, o computador ganhou 3 pontos!");
					computador.addPontos(3);
				}
			}
			else if(florPc<=27){
				do{
					System.out.println("O computador pediu Flor!   1- Aceitar  |  2- Recusar  |  3- Contra Flor  |  4- Falta Flor");
					escolha=in.nextInt();
				}while(escolha<1 || escolha>4);
				if(escolha==2){
					System.out.println("O computador ganhou 3 pontos da Flor!");
					computador.addPontos(3);
				}
				else if(jogador.calculaFlor()>0){
					if(escolha==1){
						System.out.println("Você aceitou a Flor!");
						disputaFlor(jogador,computador,rodada,4);
					}
					else{
						System.out.println("O computador recusou, você ganhou 4 pontos!");
						jogador.addPontos(4);
					}
				}
				else{
					System.out.println("Você não tem 3 cartas do mesmo naipe, o computador ganhou 3 pontos!");
					computador.addPontos(3);
				}
			}
			else if(florPc<=32){
				do{
					System.out.println("O computador pediu Flor!   1- Aceitar  |  2- Recusar  |  3- Contra Flor  |  4- Falta Flor");
					escolha=in.nextInt();
				}while(escolha<1 || escolha>4);
				if(escolha==2){
					System.out.println("O computador ganhou 3 pontos da Flor!");
					computador.addPontos(3);
				}
				else if(jogador.calculaFlor()>0){
					if(escolha==1){
						System.out.println("Você aceitou a Flor!");
						disputaFlor(jogador,computador,rodada,4);
					}
					else if(escolha==3){
						if(aceita%5==2){
							//20% de chance de pedir falta flor
							do{
								System.out.println("O computador pediu Falta Flor!   1- Aceitar  |  2- Recusar");
								escolha=in.nextInt();
							}while(escolha<1 || escolha>2);
							if(escolha==1){
								System.out.println("Você aceitou a Falta Flor!");
								disputaFlor(jogador,computador,rodada,-1);
							}
							else if(escolha==2){
								System.out.println("Você recusou a Falta Flor, o computador ganhou 5 pontos!");
								computador.addPontos(5);
							}
						}
						else if(aceita%2==0){	
							//50% chance aceitar contra flor.
							System.out.println("O computador aceitou a Contra Flor!");
							disputaFlor(jogador,computador,rodada,5);
						}
						else{
							System.out.println("O computador rejeitou a Contra Flor, você ganhou 5 pontos!");
							jogador.addPontos(5);
						}

					}
				}
				else{
					System.out.println("Você não tem 3 cartas do mesmo naipe, o computador ganhou 3 pontos!");
					computador.addPontos(3);
				}
			}
			else if(florPc>32){
				do{
					System.out.println("O computador pediu Flor!   1- Aceitar  |  2- Recusar  |  3- Contra Flor  |  4- Falta Flor");
					escolha=in.nextInt();
				}while(escolha<1 || escolha>4);
				if(escolha==2){
					System.out.println("Você recusou a Flor, o computador ganhou 3 pontos!");
					computador.addPontos(3);
				}
				else if(jogador.calculaFlor()>0){
					if(escolha==1){
						System.out.println("Você aceitou a Flor!");
						disputaFlor(jogador,computador,rodada,4);
					}
					else if(escolha==3){
						if(!(aceita%10==0)){
							//90% de chance de pedir falta flor.
							do{
								System.out.println("O computador pediu Falta Flor!   1- Aceitar  |  2- Recusar");
								escolha=in.nextInt();
							}while(escolha<1 || escolha>2);
							if(escolha==1){
								System.out.println("Você aceitou a Falta Flor!");
								disputaFlor(jogador,computador,rodada,-1);
							}
							else if(escolha==2){
								System.out.println("Você recusou a Falta Flor, o computador ganhou 5 pontos!");
								computador.addPontos(5);
							}
						}
						else{
							//se não pedir a falta flor, aceita a contra flor com certeza.
							System.out.println("O computador aceitou a Contra Flor!");
							disputaFlor(jogador,computador,rodada,5);
						}
					}
					else if(escolha==4){
						if(!(aceita%10==0)){
							//90% de chance de aceitar Falta Flor.
							System.out.println("O computador aceitou a Falta Flor!");
							disputaFlor(jogador,computador,rodada,-1);
						}
						else{
							System.out.println("O computador recusou a Falta Flor, você ganhou 4 pontos!");
							jogador.addPontos(4);
						}
					}
				}
				else{
					System.out.println("Você não tem 3 cartas do mesmo naipe, o computador ganhou 3 pontos!");
					computador.addPontos(3);
				}
			}
		}
		else if(florJogador<=0){
			//TESTA ENVIDO SE O JOGADOR NAO TIVER FLOR (PORQUE SE TIVER NEM ADIANTA PEDIR ENVIDO)

			if(envidoPc==33 && rodada%2==0){
				inicio=false;
				do{
					System.out.println("O computador pediu Falta Envido!   1- Aceitar  |  2- Recusar");
					escolha=in.nextInt();
				}while(escolha<1 || escolha>2);
				if(escolha==1){
					System.out.println("Você aceitou o Falta Envido!");
					disputaEnvido(jogador,computador,rodada,-1);
				}
				else if(escolha==2){
					System.out.println("Você recusou o Falta Envido, o computador ganhou 1 ponto!");
					computador.addPontos(1);
				}
			}
			else if(envidoPc<=22){
				if(aceita%10==0){
					inicio=false;
					//10% de chance de pedir real envido
					do{
						System.out.println("O computador pediu Real Envido!   1- Aceitar  |  2- Recusar  |  3- Falta Envido");
						escolha = in.nextInt();
					}while(escolha<1 || escolha>3);
					if(escolha==1){
						System.out.println("Você aceitou o Real Envido!");
						disputaEnvido(jogador,computador,rodada,3);
					}
					else if(escolha==2){
						System.out.println("Você recusou o Real Envido, o computador ganhou 1 ponto!");
						computador.addPontos(1);
					}
					else if(escolha==3){
						System.out.println("O computador recusou o Falta Envido, você ganhou 3 pontos!");
						jogador.addPontos(3);
					}
				}
				else if(aceita%5==0){
					inicio=false;
					//20% de chance de pedir envido
					do{
						System.out.println("O computador pediu envido!   1- Aceitar  |  2- Recusar  |  3- Real Envido  | 4- Falta Envido");
						escolha = in.nextInt();
					}while(escolha<1 || escolha>4);
					if(escolha==1){
						System.out.println("Você aceitou o envido!");
						disputaEnvido(jogador,computador,rodada,2);
					}
					else if(escolha==2){
						System.out.println("Você recusou o envido, o computador ganhou 1 ponto!");
						computador.addPontos(1);
					}
					else if(escolha==3){
						if(aceita%10==2){
							//10% chance de aceitar real envido.
							System.out.println("O computador aceitou o Real Envido!");
							disputaEnvido(jogador,computador,rodada,5);
						}
						else{
							System.out.println("O computador recusou o Real Envido, você ganhou 2 pontos!");
							jogador.addPontos(2);
						}
					}
					else if(escolha==4){
						System.out.println("O computador recusou o Falta Envido, você ganhou 2 pontos!");
						jogador.addPontos(2);
					}
				}
			}
			else if(envidoPc<=27){
				if(aceita%2==0){
					inicio=false;
					//50% chance de pedir real envido
					do{
						System.out.println("O computador pediu Real Envido!   1- Aceitar  |  2- Recusar  |  3- Falta Envido");
						escolha=in.nextInt();
					}while(escolha<1 || escolha>3);
					if(escolha==1){
						System.out.println("Você aceitou o Real Envido!");
						disputaEnvido(jogador,computador,rodada,3);
					}
					else if(escolha==2){
						System.out.println("Você recusou o Real Envido, o computador ganhou 1 ponto!");
						computador.addPontos(1);
					}
					else if(escolha==3){
						System.out.println("O computador recusou o Falta Envido, você ganhou 3 pontos!");
						jogador.addPontos(3);
					}
				}
				else{
					inicio=false;
					do{
						System.out.println("O computador pediu envido!   1- Aceitar  |  2- Recusar  |  3- Real Envido  | 4- Falta Envido");
						escolha = in.nextInt();
					}while(escolha<1 || escolha>4);
					if(escolha==1){
						System.out.println("Você aceitou o envido!");
						disputaEnvido(jogador,computador,rodada,2);
					}
					else if(escolha==2){
						System.out.println("Você recusou o envido, o computador ganhou 1 ponto!");
						computador.addPontos(1);
					}
					else if(escolha==3){
						if(aceita%2==1){
							//50% chance de aceitar real envido.
							System.out.println("O computador aceitou o Real Envido!");
							disputaEnvido(jogador,computador,rodada,5);
						}
						else{
							System.out.println("O computador recusou o Real Envido, você ganhou 2 pontos!");
							jogador.addPontos(2);
						}
					}
					else if(escolha==4){
						System.out.println("O computador recusou o Falta Envido, você ganhou 2 pontos!");
						jogador.addPontos(2);
					}
				}
			}
			else if(envidoPc<=30){
				inicio=false;
				if(aceita%5==0 || aceita%5==2){
					//40% chance pedir falta envido.
					do{
						System.out.println("O computador pediu Falta Envido!   1- Aceitar  |  2- Recusar");
						escolha=in.nextInt();
					}while(escolha<1 || escolha>2);
					if(escolha==1){
						System.out.println("Você aceitou o Falta Envido!");
						disputaEnvido(jogador,computador,rodada,-1);
					}else if(escolha==2){
						System.out.println("Você recusou o Falta Envido, o computador ganhou 1 ponto!");
						computador.addPontos(1);
					}
				}
				else{
					do{
						System.out.println("O computador pediu Real Envido!   1- Aceitar  |  2- Recusar  |  3- Falta Envido");
						escolha=in.nextInt();
					}while(escolha<1 || escolha>3);
					if(escolha==1){
						System.out.println("Você aceitou o Real Envido!");
						disputaEnvido(jogador,computador,rodada,3);
					}
					else if(escolha==2){
						System.out.println("Você recusou o Real Envido, o computador ganhou 1 ponto!");
						computador.addPontos(1);
					}
					else if(escolha==3){
						//se estivesse nos 40% de chance de aceitar o falta envido, ja teria pedido direto. por isso rejeita
						System.out.println("O computador rejeitou o Falta Envido, você ganhou 3 pontos!");
						jogador.addPontos(3);
					}
				}
			}
			else{
				inicio=false;
				//31 a 33 sem ser mao
				if(!(aceita%10==0)){
					//90% chance pedir falta envido
					do{
						System.out.println("O computador pediu Falta Envido!   1- Aceitar  |  2- Recusar");
						escolha=in.nextInt();
					}while(escolha<1 || escolha>2);
					if(escolha==1){
						System.out.println("Você aceitou o Falta Envido!");
						disputaEnvido(jogador,computador,rodada,-1);
					}
					else if(escolha==2){
						System.out.println("Você recusou o Falta Envido, o computador ganhou 1 ponto!");
						computador.addPontos(1);
					}
				}
				else{
					do{
						System.out.println("O computador pediu Real Envido!   1- Aceitar  |  2- Recusar  |  3- Falta Envido");
						escolha=in.nextInt();
					}while(escolha<1 || escolha>3);
					if(escolha==1){
						System.out.println("Você aceitou o Real Envido!");
						disputaEnvido(jogador,computador,rodada,3);
					}
					else if(escolha==2){
						System.out.println("Você recusou o Real Envido, o computador ganhou 1 ponto!");
						computador.addPontos(1);
					}
					else if(escolha==3){
						if(!(aceita%10==5)){
							//90% chance de aceitar
							System.out.println("O computador aceitou o Falta Envido!");
							disputaEnvido(jogador,computador,rodada,-1);
						}
						else{
							System.out.println("O computador recusou o Falta Envido, você ganhou 3 pontos!");
							jogador.addPontos(3);
						}
					}
				}
			}
		}
		inicioPc=false;
	}

	public void florEnvidoUsuario(int choice){
		//flor
		if(choice==1){
			int flor = jogador.calculaFlor();
			if(flor<1){
				System.out.println("Você não pode pedir flor, porque não tem.");
				do{
					System.out.println("2- Envido  |  3- Truco  |  4- Prosseguir  |  5- Salvar e Sair");
					escolha = in.nextInt();
				}while(escolha<2 || escolha>5);
			}
			else{
				inicioPc=false;
				System.out.println("Você pediu flor!");
				int florPc = computador.calculaFlor();
				if(florPc==38 && rodada%2==0){
					//computador tem o max point de flor e é o mão. com certeza ele ganha.
					do{
						System.out.println("O computador pediu Falta Flor! 1- Aceitar  |  2- Recusar");
						escolha=in.nextInt();
					}while(escolha<1 || escolha>2);
					if(escolha==1){
						System.out.println("Você aceitou a Falta Flor!");
						disputaFlor(jogador,computador,rodada,-1);
					}
					else if(escolha==2){
						System.out.println("Você recusou a Falta Flor, o computador ganhou 4 pontos!");
						computador.addPontos(4);
					}
				}
				else if(florPc==0){
					System.out.println("O computador recusou a flor, você ganhou 3 pontos.");
					jogador.addPontos(3);
				}
				else if(florPc<=27){
					System.out.println("O computador aceitou a flor!");
					disputaFlor(jogador,computador,rodada,4);

				}
				else if(florPc<=33){
					if(aceita%5==0){
						do{
							//20% de chance de pedir falta flor.
							System.out.println("O computador pediu Falta Flor!  1- Aceitar  |  2- Recusar");
							escolha = in.nextInt();
						}while(escolha<1 || escolha>2);
						if(escolha==1){
							System.out.println("Você aceitou a Falta Flor!");
							disputaFlor(jogador,computador,rodada,-1);
						}
						else if(escolha==2){
							System.out.println("Você recusou a Falta Flor, o computador ganhou 4 pontos!");
							computador.addPontos(4);
						}
					}
					else if(aceita%2==0){
						//50% de chance de pedir contra flor.
						do{
							System.out.println("O computador pediu Contra Flor!  1- Aceitar  |  2- Recusar  |  3- Falta Flor");
							escolha=in.nextInt();
						}while(escolha<1 || escolha>3);
						if(escolha==1){
							System.out.println("Você aceitou a Contra Flor!");
							disputaFlor(jogador,computador,rodada,5);
						}
						else if(escolha==2){
							System.out.println("Você recusou a Contra Flor, o computador ganhou 4 pontos!");
							computador.addPontos(4);
						}
						else if(escolha==3){
							System.out.println("Você pediu Falta Flor!");
							if(aceita%5==2){
								//20% de chance do pc aceitar a falta flor.
								System.out.println("O computador aceitou a Falta Flor!");
								disputaFlor(jogador,computador,rodada,-1);
							}
							else{
								System.out.println("O computador recusou a Falta Flor, você ganhou 4 pontos!");
								jogador.addPontos(4);
							}
						}
					}
				}
				else{
					if(!(aceita%10==0)){
						//90% de chance de pedir Falta Flor.
						do{
							System.out.println("O computador pediu Falta Flor!  1- Aceitar  |  2- Recusar");
							escolha=in.nextInt();
						}while(escolha<1 || escolha>2);
						if(escolha==1){
							System.out.println("Você aceitou a Falta Flor!");
							disputaFlor(jogador,computador,rodada,-1);
						}
						else if(escolha==2){
							System.out.println("Você recusou a Falta Flor, o computador ganhou 4 pontos!");
							computador.addPontos(4);
						}
					}
					else{
						//se não pedir o falta flor nos 90%, pede pelo menos contra flor.
						do{
							System.out.println("O computador pediu Contra Flor!  1- Aceitar  |  2- Recusar  |  3- Falta Flor");
							escolha=in.nextInt();
						}while(escolha<1 || escolha>3);
						if(escolha==1){
							System.out.println("Você aceitou a Contra Flor!");
							disputaFlor(jogador,computador,rodada,5);
						}
						else if(escolha==2){
							System.out.println("Você recusou a Contra Flor, o computador ganhou 4 pontos!");
							computador.addPontos(4);
						}
						else if(escolha==3){
							if(!(aceita%10==2)){
								//90% de chance de aceitar o falta envido
								System.out.println("O computador aceitou a Falta Flor!");
								disputaFlor(computador,jogador,rodada,-1);
							}
							else{
								System.out.println("O computador recusou a Falta Flor, você ganhou 5 pontos!");
								jogador.addPontos(5);
							}
						}
					}
				}
				do{
					System.out.println("3- Truco  |  4- Prosseguir  |  5- Salvar e Sair");
					escolha = in.nextInt();
				}while(escolha<3 || escolha>5);
			}
		}
		//envido
		if (choice==2){
			inicioPc=false;
			int envidoPc = computador.calculaEnvido();
			int florPc = computador.calculaFlor();
			if(florPc>0){
				System.out.println("Você pediu envido mas o computador tinha flor, ele ganhou 3 pontos!");
				computador.addPontos(3);
			}
			else{
				do{
					System.out.println("1- Envido  |  2- Real Envido  |  3- Falta Envido");
					escolha = in.nextInt();
				}while(escolha<1 || escolha>3);
				if(escolha==1){
					//envido
					inicioPc=false;
					System.out.println("Você pediu envido!");
					if(envidoPc==33 && rodada%2==0){
						//se entrou aqui é pq o pc tem 33 pontos (max possivel) e é o mao, ou seja com certeza ganha.
						do{
							System.out.println("O computador pediu Falta Envido! 1- Aceitar  |  2- Recusar");
							escolha=in.nextInt();
						}while(escolha<1 || escolha>2);
						if(escolha==1){
							System.out.println("Você aceitou o Falta Envido!");
							disputaEnvido(jogador,computador,rodada,-1);
						}
						else if(escolha==2){
							System.out.println("Você recusou o Falta Envido, o computador ganhou 2 pontos!");
							computador.addPontos(2);
						}

					}
					else if(envidoPc<23){
						if(aceita%5==0){
							//de 1 a 100, 20 numeros sao divisiveis por 5 (20% de chance de aceitar aqui)
							System.out.println("O computador aceitou o envido!");
							disputaEnvido(jogador,computador,rodada,2);
						}
						else{
							System.out.println("O computador recusou o envido, você ganhou 1 ponto!");
							jogador.addPontos(1);
						}
					}
					else if(envidoPc<=27){
						System.out.println("O computador aceitou o envido!");
						disputaEnvido(jogador,computador,rodada,2);
					}
					else if(envidoPc>27 && envidoPc<31){
						do{
							System.out.println("O computador pediu Real Envido! 1- Aceitar  |  2- Recusar  |  3- Falta Envido");
							escolha = in.nextInt();
						}while(escolha<1 || escolha>3);
						if(escolha==1){
							System.out.println("Você aceitou o Real Envido!");
							disputaEnvido(jogador,computador,rodada,5);
						}
						else if(escolha==2){
							System.out.println("Você recusou o Real Envido, o computador ganhou 2 pontos.");
							computador.addPontos(2);
						}
						else if(escolha==3){
							if(aceita%5==0 || aceita%5==2){
								//40 numeros com restos 0 ou 2 na divisao por 5, 40% de chance de aceitar.
								System.out.println("O computador aceitou seu Falta Envido!");
								disputaEnvido(jogador,computador,rodada,-1);
							}
							else{
								System.out.println("O computador recusou seu falta Envido, você ganhou 5 pontos!");
								jogador.addPontos(5);
							}
						}
					}
					else if(envidoPc>=31){
						aceita = random.nextInt(100)+1;//random int de 1 a 100
						if(!(aceita%10==0)){
							//só 10 numeros de 1 a 100 tem resto 0 na divisao por 10. 90% chance pedir falta envido.
							do{
								System.out.println("O computador pediu Falta Envido! 1- Aceitar  |  2- Recusar");
								escolha = in.nextInt();
							}while(escolha<1 || escolha>2);
							if(escolha==1){
								System.out.println("Você aceitou o Falta Envido!");
								disputaEnvido(jogador,computador,rodada,-1);
							}
							else if(escolha==2){
								System.out.println("Você recusou o Falta Envido, o computador ganhou 2 pontos!");
								computador.addPontos(2);
							}
						}
					}
				}
				else if(escolha==2){
					inicioPc=false;
					//Real Envido
					System.out.println("Você pediu Real Envido!");
					if(envidoPc==33 && rodada%2==0){
						do{
							System.out.println("O computador pediu Falta Envido! 1- Aceitar  |  2- Recusar");
							escolha=in.nextInt();
						}while(escolha<1 || escolha>2);
						if(escolha==1){
							System.out.println("Você aceitou o Falta Envido!");
							disputaEnvido(jogador,computador,rodada,-1);
						}
						else if(escolha==2){
							System.out.println("Você recusou o Falta Envido, o computador ganhou 3 pontos!");
							computador.addPontos(3);
						}
					}
					else if(envidoPc<23){
						if(aceita%10==0){
							//10% de chance de aceitar real envido com cartas de 0 a 22
							System.out.println("O computador aceitou o Real Envido!");
							disputaEnvido(jogador,computador,rodada,3);
						}
						else{
							System.out.println("O computador recusou o Real Envido, você ganhou 1 ponto.");
							jogador.addPontos(1);
						}
					}
					else if(envidoPc<=27){
						if(aceita%2==1){
							//50% chance aceitar real envido 23 a 27
							System.out.println("O computador aceitou o Real Envido!");
							disputaEnvido(jogador,computador,rodada,3);
						}
						else{
							System.out.println("O computador recusou o Real Envido, você ganhou 1 ponto.");
							jogador.addPontos(1);
						}
					}
					else if(envidoPc>27 && envidoPc<31){
						if(aceita%5==0 || aceita%5==2){
							do{
								//40% chance de pedir falta envido
								System.out.println("O computador pediu Falta Envido! 1- Aceitar  |  2- Recusar");
								escolha=in.nextInt();
							}while(escolha<1 || escolha>2);
							if(escolha==1){
								System.out.println("Você aceitou o Falta Envido!");
								disputaEnvido(jogador,computador,rodada,-1);
							}
							else if(escolha==2){
								System.out.println("Você recusou o Falta Envido, o computador ganhou 3 pontos!");
								computador.addPontos(3);
							}
						}
						else {
							System.out.println("O computador aceitou o seu Real Envido!");
							disputaEnvido(jogador,computador,rodada,3);
						}
					}
					else if(envidoPc>=31){
						if(!(aceita%10==0)){
							do{
								//90% de chance de pedir falta envido.
								System.out.println("O computador pediu Falta Envido! 1- Aceitar  |  2- Recusar");
								escolha=in.nextInt();
							}while(escolha<1 || escolha>2);
							if(escolha==1){
								System.out.println("Você aceitou o Falta Envido!");
								disputaEnvido(jogador,computador,rodada,-1);
							}
							else if(escolha==2){
								System.out.println("Você recusou o Falta Envido, o computador ganhou 3 pontos!");
								computador.addPontos(3);
							}
						}
						else{
							System.out.println("O computador aceitou seu Real Envido!");
							disputaEnvido(jogador,computador,rodada,3);
						}
					}
				}
				else if(escolha==3){
					inicioPc=false;
					System.out.println("Você pediu Falta Envido!");
					if(envidoPc==33 && rodada%2==0){
						System.out.println("O computador aceitou o Falta Envido!");
						disputaEnvido(jogador,computador,rodada,-1);
					}
					else if(envidoPc<28){
						System.out.println("O computador recusou o Falta Envido, você ganhou 1 ponto!");
						jogador.addPontos(1);
					}
					else if(envidoPc<31){
						if(aceita%5==0 || aceita%5==2){
							//40%de chance de aceitar falta envido.
							System.out.println("O computador aceitou o Falta Envido!");
							disputaEnvido(jogador,computador,rodada,-1);
						}
						else {
							System.out.println("O computador recusou o Falta Envido, você ganhou 1 ponto!");
							jogador.addPontos(1);
						}
					}
					else {
						if(!(aceita%10==0)){
							//90% de chance de aceitar falta envido.
							System.out.println("O computador aceitou o Falta Envido!");
							disputaEnvido(jogador,computador,rodada,-1);
						}
						else{
							System.out.println("O computador recusou o Falta Envido, você ganhou 1 ponto!");
							jogador.addPontos(1);
						}
					}
				}
			}
			do{
				System.out.println("3- Truco  |  4- Prosseguir  |  5- Salvar e Sair");
				escolha=in.nextInt();
			}while(escolha<3||escolha>5);
			inicio=false;
		}
	}

	public void disputaEnvido(Jogador jogador, Jogador computador, int rodada, int pontos){
		int pontosPCGanhou;
		int pontosJogadorGanhou;
		inicio = false;
		inicioPc = false;
		System.out.println("O computador tem "+computador.calculaEnvido()+" pontos de envido, você tem "+jogador.calculaEnvido()+" pontos de envido.");
		if(pontos==-1){
			//se entrar aqui é por que é um falta envido, ai quem ganhar leva os pontos que falta pro outro ganhar.
			pontosPCGanhou = 30-jogador.getPontos();
			pontosJogadorGanhou = 30-computador.getPontos();
		}
		else{
			pontosPCGanhou = pontos;
			pontosJogadorGanhou = pontos;
		}
		int jogadorEnvido = jogador.calculaEnvido();
		int computadorEnvido = computador.calculaEnvido();
		if(computadorEnvido > jogadorEnvido){
			System.out.println("O computador venceu, ele ganhou: "+pontosPCGanhou+" pontos!");
			computador.addPontos(pontosPCGanhou);
		}
		else if(jogadorEnvido > computadorEnvido){
			System.out.println("Você venceu, ganhou "+pontosJogadorGanhou+" pontos!");
			jogador.addPontos(pontosJogadorGanhou);
		}
		else {
			//se empatar ve quem tem prioridade (quem comeca) e ele leva os pontos.
			System.out.print("Houve um empate.");
			if(rodada%2==1){
				System.out.println(" Mas você ganhou "+pontosJogadorGanhou+" pontos por ser o mão.");
				jogador.addPontos(pontosJogadorGanhou);
			}
			else {
				System.out.println(" Mas o computador ganhou "+pontosPCGanhou+" pontos por ser o mão.");
				computador.addPontos(pontosPCGanhou);
			}
		}
	}

	public void disputaFlor(Jogador jogador, Jogador computador, int rodada,int pontos){
		int pontosPCGanhou;
		int pontosJogadorGanhou;
		inicio = false;
		inicioPc = false;
		System.out.println("O computador tem "+computador.calculaFlor()+" pontos, você tem "+jogador.calculaFlor()+" pontos.");
		if(pontos==-1){
			//se entrar aqui é por que é um falta-flor, ai quem ganhar leva os pontos que falta pro outro ganhar.
			pontosPCGanhou = 30-jogador.getPontos();
			pontosJogadorGanhou = 30-computador.getPontos();
		}
		else{
			pontosPCGanhou = pontos;
			pontosJogadorGanhou = pontos;
		}
		int jogadorFlor = jogador.calculaFlor();
		int computadorFlor = computador.calculaFlor();
		if(computadorFlor > jogadorFlor){
			System.out.println("O computador venceu, ele ganhou: "+pontosPCGanhou+" pontos!");
			computador.addPontos(pontosPCGanhou);
		}
		else if(jogadorFlor > computadorFlor){
			System.out.println("Você venceu, ganhou "+pontosJogadorGanhou+" pontos!");
			jogador.addPontos(pontosJogadorGanhou);
		}
		else {
			//se empatar ve quem tem prioridade (quem comeca) e ele leva os pontos.
			System.out.print("Houve um empate.");
			if(rodada%2==1){
				System.out.println(" Mas você ganhou "+pontosJogadorGanhou+" pontos por ser o mão.");
				jogador.addPontos(pontosJogadorGanhou);
			}
			else{
				System.out.println(" Mas o computador ganhou "+pontosPCGanhou+" pontos por ser o mão.");
				computador.addPontos(pontosPCGanhou);
			}
		}
	}

}
