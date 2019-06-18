/** 
	@author(Lucas Dimer Justo)
	Classe que armazena os atributos do jogador, e controla suas operacoes atraves dos metodos.
*/

public class Jogador {
	private String nome;
	private Mao mao;
	private Mao maoComputador;//mao que so armazena a do pc pra printar no fim da rodada (pro usuario checar).
	private int pontos;
	private int pontosDeEnvido;//variaveis pra auxuiliar na decisao do computador
	private int pontosDeFlor;//variaveis pra auxuiliar na decisao do computador
	private int pontosDeTruco;//variaveis pra auxuiliar na decisao do computador
	
	public Jogador(String nome){
		this.nome = nome;
		this.pontos = 0;
		mao = new Mao();
		if(nome.equals("computador"))maoComputador = new Mao();
	}

	public int getPontos(){
		return pontos;
	}

	public void mostraMao(){
		/** 
			@author(Lucas Dimer Justo)
			Printa a mao do jogador na tela, ou mostra o numero de cartas do computador.
		*/
		if(nome.equals("computador"))System.out.println("O computador tem "+mao.size()+" cartas.");
		else{
			System.out.println("Sua Mão: "+mao);
		}
	}

	public void mostraMaoComputador(){
		System.out.println("Essa era a mão do computador nessa rodada: "+maoComputador.mostraMaoComputador());
		maoComputador.clear();
	}

	public String getNome(){
		return nome;
	}

	public void atribuiMao(Carta a, Carta b, Carta c){
		mao.add(a);
		mao.add(b);
		mao.add(c);
		pontosDeEnvido = calculaEnvidoA();
		pontosDeFlor = calculaFlorA();
		pontosDeTruco = calculaPontosDeTruco();
		if(nome.equals("computador")){
			maoComputador.add(a);
			maoComputador.add(b);
			maoComputador.add(c);
		}
	}

	public void clearMao(){
		mao.clear();
	}

	public void addPontos(int pontos){
		/**
			@author(Lucas Dimer Justo)
			Adiciona os pontos no jogador, e se passar ou igualar a 30 ja finaliza o jogo porque este jogador ganhou.
		*/
		this.pontos += pontos;
		if(this.pontos >= 30){
			System.out.println("O jogo acabou. "+this.nome+" venceu!");
			System.exit(0);
		}
	}

	public int getPontosDeTruco(){
		return pontosDeTruco;
	}

	private int calculaPontosDeTruco(){
		return mao.calculaPontosDeTruco();
	}

	public int calculaFlor(){
		return pontosDeFlor;
	}

	public int calculaEnvido(){
		return pontosDeEnvido;
	}

	private int calculaFlorA(){
		return mao.calculaFlor();
	}

	private int calculaEnvidoA(){
		return mao.calculaEnvido();
	}

	public int maoSize(){
		return mao.size();
	}

	public Carta jogaCarta(int escolha){
		return mao.remove(escolha);
	}

}