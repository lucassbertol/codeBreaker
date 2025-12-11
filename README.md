# Code Breaker

---

## Identificação

- Aluno: Lucas Salvini Bertol
- Curso: Sistemas de informação  
- Disciplina: [Paradigmas de Programação](https://github.com/AndreaInfUFSM/elc117-2025b/tree/main)

---

## Proposta 

- Criação de um jogo em java utilizando o framework [LibGDX](https://libgdx.com/) onde você precisa resolver problemas de lógica e códigos de 
programação (Na linguagem C) para aprimorar seus conhecimentos na área e avançar de nível. Cada partida, com 5 niveis, de dificuldades escolhidas previamente, tem um novo desafio.
Dependendo do tempo que o player  finalizar a fase, tentativas e acertos/erros, ele ganhará um **score** que ficará eternizado no **ranking** público do jogo.
---

## Processo de desenvolvimento

### <u>Funcionamento do LibGDX e criação do projeto</u>

- O primeiro passo foi ler a [documentação oficial WIKI Liibgdx](https://libgdx.com/wiki/) e criar o projeto com a ferrmenta deles. Inicialmenta não tinha entendido a estrutura do projeto com `core`, `lwjgl3`, `html` e outras, pesquisando melhor e analisando projetos de semestres anteriores entendi onde ficaria o código principal do jogo (`core`), sendo os outros apenas os lançadores para cada plataforma específica que ira rodar o projeto.

- Agora, era necessário entender os métodos `create()`, `render()`e `dispose()` que ja vinham no projeto libGDX. Descobri que `create()` configura o jogo, `render()`exibe tudo na tela, e `dispose()`é utilizado para liberar memória.

### <u>Estruturando a interface das telas</u>

- Pesquisando por padrões em projetos libGDX, descobri o padrão da pasta `Screen` presente  em todos  eles. Desse jeito não fica toda lógica na classe principal `MainGame`. Outra descoberta foi passar a instância principal da classe `Game` para todas as telas dentro da pasta screen, permitindo qualquer tela ser isolada separadamente e "chamada" por `game.setScreen(new 'OutraScreen'(game))`.

- Outro desafio foi resolver erros relacionados às [skins](https://stackoverflow.com/questions/16182844/default-skin-libgdx), descobrindo que elas dependem de uma estrutura de arquivos (`.json, .atlas e .png`) que precisavam estar na pasta correta.

- Inicialmente na implementação da primeira tela `MenuScreen`, exibi um background com dois botões "JOGAR" e "RANK" ainda sem funções, usando `Scene2D.UI`, no inicio estava tentando mapear a mão a posição de  tudo na tela mas vi que não funcionaria e seria muito trabalhoso, então, implementei com a classe `Table` deixando a interface organizada "automaticammente". No futuro, criei a classe `constants` para padronizar valores (tamanho de fontes, botões etc...) e alterá-los com muito mais praticidade. Para as outras telas, foi basicamente um "copia e cola" dessa primeira criada, por terem algumas semelhanças, mas destacando algumas diferenças pontuais.

```java
    // Na classe public class MenuScreen implements Screen {
    // Carrega a imagem de fundo da pasta assets
    background = new Texture(Constants.BG_MENU);
``` 

```java
    // No @override render()
    // Desenha a imagem de fundo
    batch.draw(background, 0, 0, Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);
``` 
```java
    // Cria a tabela para organizar os botões
    Table table = new Table();
    table.setFillParent(true);
    table.bottom().padBottom(100); // Posiciona na parte inferior com padding
    stage.addActor(table);
```    


- Na `UserInputScreen`, o usuário digita seu username, e salva-o na `MainGame` ao pressionar o botão com o `ClickListener` que foi adicionado nele.

```java
        TextField nameInput = new TextField("", skin);

        nameInput.getStyle().font.getData().setScale(Constants.INPUT_FONT_SCALE);
        nameInput.setMessageText("");

        TextButton nextButton = new TextButton(Constants.BTN_CONTINUE, skin);

        nextButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String playerName = nameInput.getText();
                if (!playerName.isEmpty()) {
                    // Salva o nome no MainGame
                    game.setPlayerName(playerName);

                    // Depois de salvar, vai para a próxima tela
                    game.setScreen(new DifficultSelectScreen(game));
                }
            }
        });
```

- A tela `QuestionScreen` acabou sendo a mais complexa, por ter varios `Labels` e  `TextFields` dinamicos e ainda ter que implementar um `ScrollPane` para quando alguma questão exceder o tamanho do layout e no final um timer, por outro lado, a maioria desses itens ja "vem pronto" com o libGDX.

```java
        // ScrollPane para permitir rolagem
        ScrollPane scrollPane = new ScrollPane(contentTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false); // Apenas scroll vertical
```

```java
        // Ajutar área do scrollpane na table
        mainTable.add(scrollPane).width(1770).height(900).pad(10);
```

### <u>Sistema de perguntas</u>

- O sistema de perguntas foi criado com 3 classes principais que trabalham juntas, `Question`, `QuestionsData` e `QuestionsParsing`.

- A classe `Question` foi usada pra representar uma questão completa com seus atributos, foi utilizado **encapsulamento**, todos atributos privados com getters públicos.

- Estrutura dos atributos:
    - `id`: identificador único da questão
    - `title`: título temático, que no final não foi utilizado :(
    - `text`: enunciado/contexto da questão
    - `question`: o código/texto da questão em si
    - `answer`: estrutura complexa ArrayList<ArrayList<String>> para suportar múltiplas respostas e múltiplas possibilidades por resposta
```java
public class Question {
    	private int id;
    	private String title;
    	private String text;
    	private String question;
    	private ArrayList<ArrayList<String>> answer;
    
    	// Getters públicos para acesso controlado
    	public int getId() { return id; }
    	public String getTitle() { return title; }
}
```

- Inicialmente tinha feito com `String[]`, mas como foram implementadas questões com multiplas lacunas e as respostas podem ter várias opções (Maiusculas, minusculas...), optei por usar `ArrayList<ArrayList<String>>` que é basicamente uma lista de strings, sendo no meu caso, a lista externa para cada campo de resposta e a lista interna para as variações das respostas.

- A classe `QuestionsData`, é uma classe simples que armazena uma lista de questões e serve como estrutura intermediaria para o parsing dos JSONs, com as questões.

```json
	{
      "id": 66,
      "title": "Troca de Chaves por Referencia [NIVEL MEDIO]",
      "text": "Um modulo de controle de acesso precisa trocar as chaves de criptografia para bypass. Complete a funcao 'swap' para que ela receba **ponteiros** para duas chaves e efetue a troca dos valores originais.",
      "question": "#include <stdio.h>\n\nvoid swap(int *a, int *b) {\n int temp = ??;\n *a = *b;\n *b = ??;\n}\n\nint main() {\n int chaveA = 123, chaveB = 987;\n swap(&chaveA, &chaveB);\n printf(\"A=%d, B=%d\\n\", chaveA, chaveB);\n     return 0;\n}",
      "answer": [
        [
          "*a"
        ],
        [
          "temp"
        ]
      ]
    },
```

- A classe `QuestionsParsing`, como o nome diz, foi implementada para parsear e carregar os arquivos JSON. Implementei uma lógica de seleção aleatória de questões.

- Inicialmente tinha tentado carregar as perguntas na main, mas causava problemas quando reiniciava o jogo sem reiniciar o programa, carregando sempre as mesmas questões.

- Tembém não estava conseguindo ler os arquivos no JSON, com erro `FileNotFoundException`. A solução final foi descobrir que deveria usar `Gdx.files.internal()` do LibGDX para buscar corretamente os arquivos na pasta `assets`.

```java
public QuestionsData loadEasyQuestions() {
    FileHandle file = Gdx.files.internal(Constants.QUESTIONS_EASY);
    String jsonContent = file.readString();
    return json.fromJson(QuestionsData.class, jsonContent);
}

public QuestionsData loadHardQuestions() {
    FileHandle file = Gdx.files.internal(Constants.QUESTIONS_HARD);
    String jsonContent = file.readString();
    return json.fromJson(QuestionsData.class, jsonContent);
}
```

### <u>Sistema de questões sem repetição</u>

- Como fazer para o jogador não receber a mesma questão duas vezes? Implementei o método `getRandonQuestionsExcluding()` que recebe os ID's das questões já usadas, filtra apenas as disponíveis e seleciona alguma aleatória das disponíveis.

```java
public Question getRandomQuestionExcluding(String difficulty, List<Integer> excludedIds) {
    QuestionsData data = loadQuestionsByDifficulty(difficulty);
    List<Question> questions = data.getQuestoes();
    
    // Filtra questões não usadas
    List<Question> availableQuestions = new java.util.ArrayList<>();
    for (Question question : questions) {
        if (!excludedIds.contains(question.getId())) {
            availableQuestions.add(question);
        }
    }
    
    // Se acabaram as questões, permite repetição
    if (availableQuestions.isEmpty()) {
        int randomIndex = random.nextInt(questions.size());
        return questions.get(randomIndex);
    }
    
    // Retorna questão aleatória das disponíveis
    int randomIndex = random.nextInt(availableQuestions.size());
    return availableQuestions.get(randomIndex);
}
```

### <u>Validação das respostas</u>

- Criei uma classe estática com dois métodos `validateAnswer()` e `clearInputFields()`, que verifica se todas as respostas digitadas pelo usuário batem com os gabaritos possíveis (para cada campo de reposta) e para limpar os campos pós resposta preenchida.

```java
public static boolean validateAnswers(List<TextField> inputFields, Question question) {
    ArrayList<ArrayList<String>> correctAnswers = question.getAnswer();
    
    // Para cada campo de resposta
    for (int i = 0; i < correctAnswers.size(); i++) {
        String userAnswer = inputFields.get(i).getText().trim().toLowerCase();
        List<String> possibleAnswers = correctAnswers.get(i);
        
        boolean isCorrect = false;
        // Testa contra todas as possibilidades
        for (String possibleAnswer : possibleAnswers) {
            if (userAnswer.equalsIgnoreCase(possibleAnswer.trim())) {
                isCorrect = true;
                break;
            }
        }
        
        if (!isCorrect) {
            return false;  // Se uma estiver errada, retorna false
        }
    }
    
    return true;  // Todas corretas
}
```
- `.trim()`: remove espaços do inicio ou fim da resposta.
- `.toLowerCase()` e `.equalsIgnoreCase()`: ignora diferença de letras maiúsculas e minúsculas.

```java
public static void clearInputFields(List<TextField> inputFields) {
    // Limpeza de campos quando jogador erra
    for (TextField field : inputFields) {
        field.setText("");
    }
}
```

### <u>Integração com a tela da questão</u>

- Foi implementado o método `createInputFields()` para criar um campo de texto para cada resposta, inicialmente estava estático, gerando apenas um campo, mas existiam questões com mais de 1 campo necessário, então, passei a utilizar com base no tamanho de `answer.size()`, para ficar flexível com a quantidade de repostas de cada questão.
```java
private void createInputFields(Table table) {
    inputFields.clear();
    ArrayList<ArrayList<String>> answers = currentQuestion.getAnswer();
    
    // Cria um TextField para cada resposta necessária
    for (int i = 0; i < answers.size(); i++) {
        TextField input = new TextField("", skin);
        input.getStyle().font.getData().setScale(Constants.INPUT_FONT_SCALE);
        inputFields.add(input);
        
        Label label = new Label("Resposta " + (i + 1) + ":", skin);
        label.setFontScale(Constants.BUTTON_FONT_SCALE);
        
        table.row().pad(15);
        table.add(label).padRight(20);
        table.add(input).width(500).height(80);
    }
}
```
- Também foi implementado o método `checkAnswer()` para avançar quando o jogador preencher corretamente com a(s) resposta(s) e um classe `loadNextQuestion()` para carregar a próxima questão, passando os ID's já usados para não repetir.
```java
    private void checkAnswer() {
        boolean allCorrect = AnswerValidator.validateAnswers(inputFields, currentQuestion);

        if (allCorrect) {
            handleCorrectAnswer();
        } else {
            handleWrongAnswer();
        }
    }
```

### <u>Sistema de Timer e Pontuação</u>

- Implementei um sistema de timer através da classe `TimerManager`, que controla o tempo limite baseado na dificuldade escolhida:
  - **Fácil**: 80 segundos (1 minuto e 20 segundos)
  - **Difícil**: 155 segundos (2 minutos e 35 segundos)

- O timer é atualizado a cada frame através do método `update(delta)` que recebe o tempo decorrido entre frames (`delta`). Quando o tempo restante chega a zero, `timeUp` é ativado e o jogador recebe *game over* volta ao menu principal automaticamente.

```java
public void update(float delta) {
    if (!stopped && timeRemaining > 0) {
        timeRemaining -= delta;
        if (timeRemaining <= 0) {
            timeRemaining = 0;
            timeUp = true;
        }
    }
}
```

- O método `getFormattedTime()` serve para converter o tempo restante em formato `MM:SS` para exibição na tela, e foi feito apenas com divisões e mod simples.

```java
public String getFormattedTime() {
    int minutes = (int) timeRemaining / 60;
    int seconds = (int) timeRemaining % 60;
    return String.format("%02d:%02d", minutes, seconds);
}
```

- Na `QuestionScreen`, o timer é atualizado constantemente no método `render()` através de `updateGameState(delta)`, que também verifica se o tempo acabou com `isTimeUp()`.

```java
private void updateGameState(float delta) {
    timerManager.update(delta);
    scoreManager.update(delta);
    timerLabel.setText(timerManager.getFormattedTime());
}
```

- O sistema de pontuação é gerenciado pela classe `ScoreManager`, que calcula o score baseado em três fatores pré definidos:

  - Pontos por acerto: no modo facil, coloquei para ganhar +100 pontos por acerto, e no dificil +250 pontos. 
  - Desconto por erro: -20 pontos nas duas dificuldades, mas não deixa o score ficar negativo.
  - Penalidade por tempo: jogador perde -2 pontos por segundo que passa.

- Esses dados de pontuação e outros estão todos na classe `Constants`, para facilitar ajustes quando for necessário de forma mais fácil.

```java
    // pontos por acerto
    public static final int EASY_CORRECT_POINTS = 100;
    public static final int HARD_CORRECT_POINTS = 250;

    // pontos perdidos por erro
    public static final int WRONG_PENALTY = 20;

    // pontos perdidos por tempo (2 pontos por segundo)
    public static final int TIME_PENALTY_PER_SECOND = 2;
```

```java
public int getScore() {
    int timePenalty = (int) elapsedTime * TIME_PENALTY_PER_SECOND;
    int finalScore = score - timePenalty;
    return Math.max(0, finalScore); // Nunca retorna score negativo
}
```

- Tive dificuldade para manter o timer e o score sincronizados entre as telas de questões. A solução foi passar as mesmas instâncias de `TimerManager` e `ScoreManager` através do construtor ao trocar de questão, evitando que o timer reiniciasse ou o score fosse perdido.

```java
// Ao carregar próxima questão, passa as mesmas instâncias
game.setScreen(new QuestionScreen(
    game,
    nextQuestion,
    difficulty,
    gameState.getQuestionsAnswered(),
    gameState.getUsedQuestionIds(),
    timerManager,  // Mesma instância
    scoreManager   // Mesma instância
));
```

### <u>Sistema de Ranking utilizando Google Sheets</u>

- Nessa parte do projeto, implementei, seguindo dicas da professora de analisar o projeto do jogo da guilhotina e mais ajuda de IA, pois estava passando bastante dificuldade, (principalmente na parte de comunicação API -> jogo), um sistema de ranking simples que salva o nome do jogador e sua pontuação final em uma planilha do Google Sheets, utilizando a API do Google Sheets.

- Tive problemas iniciais fazendo commit da chave API no github, o que não é uma boa prática, então coloquei ela na `Constants` e apago ela sempre antes de dar o commit para evitar problemas

- O sistema foi organizado em 3 classes principais: `LeaderboardConfig`, `LeaderboardService` e `ScoreScreenTable`.
    - `LeaderboardConfig` armazena a URL do Google apps script (intermediario)
    - `LeaderboardService` gerencia a comunicação com o Google Sheets http.
    - `ScoreScreenTable` exibe os dados na tela de ranking usando a requisição get.

- Google Apps Script tem um `doGet()` que retorna todos dados do ranking em JSON, e um `doPost()` que recebe o nome e pontuação do jogador e adiciona na planilha.

- Foi implementado verificação para não permitir nomes duplicados no ranking (na hora que o jogador escolhe um username já usado, da erro e pede pra trocar se ja existir no rank)
```java
public void checkNameExists(String nome, CheckNameCallback callback) {
    // GET request para buscar todos os dados
    // Parse manual do JSON para verificar se nome já existe
    // Retorna se ja existe ou não
}
```

- `LeaderboardService` faz o envio de score 
```java
public void submitScore(String nome, int pontos, SubmitCallback callback) {
    String jsonBody = "{\"nome\":\"" + escapeJson(nome) + "\",\"score\":" + pontos + "}";
    Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.POST);
    request.setUrl(LeaderboardConfig.getAppendUrl());
    request.setContent(jsonBody);
    
    Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
        // chamadas para sucesso ou erro
    });
}
```


- Na `ScoreScreenTable`, implementei a exibição dos dados em uma tabela ordenada por pontuação, utilizando `Table` e `ScrollPane` do LibGDX. 
 - Os dados são buscados via API
 - Parsing é feito e labels criadas para cada entrada
 - Scrollpane utilizado para quando tiver bastante nomes no ranking não ter erro de formatação.

- Fluxo completo:
  - `QuestionScreen.finishGame()` -> `submitScore()` é chamado ->`ScoreScreen` exibe pontuação -> `fetchScores()` atualiza tabela -> tabela de ranking é exibida em seu menu

### <u>Conclusão do Processo de Desenvolvimento</u>

- Durante o desenvolvimento deste projeto, aprofundei meus conhecimentos em Java, orientação a objetos e no framework LibGDX, enfrentando desafios reais de estruturação de código, integração de APIs externas e organização de interfaces. Aprendi a importância de separar responsabilidades entre classes, reutilizar componentes e documentar cada etapa do processo.

---

## <u>Recursos de Orientação a Objetos utilizados</u>

Durante o desenvolvimento, apliquei os princípios de OO:

**1. Encapsulamento**
- Classes como `Question`, `TimerManager` e `ScoreManager` mantêm atributos privados com acesso controlado através de getters públicos

**2. Polimorfismo**
- Todas as telas implementam a interface `Screen` do LibGDX
- Permite alternância simples entre telas: `game.setScreen(new MenuScreen(game))`
- Facilita manutenção
  
**3. Separação de Responsabilidades**
- `QuestionsParsing`: carregamento de dados
- `AnswerValidator`: validação de respostas
- `TimerManager`: controle de tempo
- `ScoreManager`: gerenciamento de pontuação
- Cada classe com propósito único e definido

**4. Composição**
- `MainGame` gerencia estado geral e telas


<img width="400" height="246" alt="Captura de tela 2025-11-25 005358" src="https://github.com/user-attachments/assets/b2d85280-f558-4247-a2af-1e1aa027d2e1" />

---

## <u>Assets e banco de questões</u>

- Os assets visuais foram criados utilizando a ferramenta de IA do [Gemini](https://gemini.google.com/app).
- O banco de questões foi gerado inicialmente com 3 questões (1 de cada tipo), elaboradas por mim, e depois, utilizei também o Gemini para criar mais questões, mantendo o mesmo estilo.
- As trilhas sonoras foram retiradas integralmente do tema do jogo [Bully Scolarship Edition](https://www.google.com/search?q=bully+theme+songs&sca_esv=e0bffaec294651cc&hl=pt-BR&source=hp&ei=y1ImaZSgHcP35OUPkZOj2QU&iflsig=AOw8s4IAAAAAaSZg2zvZ3U7nX_llsMCe7DPO245d7ziF&ved=0ahUKEwjUseWm0I6RAxXDO7kGHZHJKFsQ4dUDCBg&uact=5&oq=bully+theme+songs&gs_lp=Egdnd3Mtd2l6IhFidWxseSB0aGVtZSBzb25nczIFEC4YgAQyBhAAGBYYHjIGEAAYFhgeMgYQABgWGB4yBhAAGBYYHjIGEAAYFhgeMgYQABgWGB4yBhAAGBYYHjIGEAAYFhgeMgYQABgWGB5IpBVQAFi9FHABeACQAQCYAb4BoAHYEaoBBDAuMTe4AQPIAQD4AQGYAhKgAqESwgILEC4YgAQYsQMYgwHCAgUQABiABMICDhAAGIAEGLEDGIMBGIoFwgIREC4YgAQYsQMY0QMYgwEYxwHCAgsQABiABBixAxiDAcICCBAuGIAEGLEDwgIUEC4YgAQYsQMY0QMYgwEYxwEYigXCAggQABiABBixA8ICDhAuGIAEGLEDGNEDGMcBwgIMEC4YgAQYsQMYChgLwgIFECEYoAGYAwCSBwQxLjE3oAeLowGyBwQwLjE3uAeeEsIHCDAuNi4xMS4xyAc4&sclient=gws-wiz), pois gosto e achei que combinou com a tematica do jogo.


---

## ¹Diagrama de classes:

<img width="2021" height="1331" alt="classDiagram" src="https://github.com/user-attachments/assets/bf37d44b-7d44-4eeb-a36d-06222808db6b" />

¹ _O diagrama foi levemente simplificado por motivos de organização e de facilitar a visualização_


---

## Orientações para execução:

### Opção 1: Jogar Online (Recomendado)
- [Link para jogar WEB pelo Itch.io](https://lucassbertol.itch.io/codebreaker)
- Não requer instalação
- Compatível com qualquer navegador moderno

### Opção 2: Executar Localmente
**Pré-requisitos:**
- Java JDK 11 ou superior
- Gradle (incluído no projeto via wrapper)

**Passos:**
1. Clone o repositório:
```bash
   git clone https://github.com/elc117/gamification-2025b-lucas-salvini-bertol
   cd gamification-2025b-lucas-salvini-bertol
```

2. Execute o projeto:
```bash
   ./gradlew lwjgl3:run  # Linux/Mac
   gradlew.bat lwjgl3:run  # Windows
```

_(A chave da API do Google Sheets não está incluída no repositório por questões de politicas do Google e segurança. Para funcionalidade completa do ranking, configure sua própria chave em `Constants.java`.)_

---

## Resultado final: 

https://github.com/user-attachments/assets/8e6f05ec-2c71-47f3-a223-79e8882c11be

---

## Referências:

- **Documentação Oficial do LibGDX:**
  - [Wiki Principal](https://libgdx.com/wiki/): Ponto de partida para entender a estrutura e os conceitos básicos do framework.
  - [Scene2D.UI](https://libgdx.com/wiki/graphics/2d/scene2d/scene2d-ui): Guia essencial para a construção de interfaces de usuário, como menus e tabelas.
  - [Parsing de JSON](https://libgdx.com/wiki/utils/reading-and-writing-json): Documentação sobre a biblioteca interna do LibGDX para manipulação de arquivos JSON.

- **APIs e Serviços Web:**
  - [Google Sheets API](https://developers.google.com/sheets/api): Documentação oficial para integração com planilhas Google, utilizada no sistema de ranking.
  - [Google Apps Script](https://developers.google.com/apps-script): Guia para criar o script que serve como intermediário entre o jogo e a planilha.

- **Ferramentas e Disciplina:**
  - [Paradigmas de Programação | ELC117 - 2025b](https://github.com/AndreaInfUFSM/elc117-2025b/tree/main): Repositório da disciplina com materiais e exemplos.
  - [PlantUML](https://plantuml.com/): Ferramenta utilizada para a geração do diagrama de classes.
  - [Gemini](https://gemini.google.com/app): Utilizado para a criação dos assets.
  - [ChatGPT](https://chat.openai.com/): Auxílio na resolução de dúvidas e pesquisas rápidas durante o desenvolvimento.
  - [Bully Scolarship Edition](https://www.google.com/search?q=bully+theme+songs&sca_esv=e0bffaec294651cc&hl=pt-BR&source=hp&ei=y1ImaZSgHcP35OUPkZOj2QU&iflsig=AOw8s4IAAAAAaSZg2zvZ3U7nX_llsMCe7DPO245d7ziF&ved=0ahUKEwjUseWm0I6RAxXDO7kGHZHJKFsQ4dUDCBg&uact=5&oq=bully+theme+songs&gs_lp=Egdnd3Mtd2l6IhFidWxseSB0aGVtZSBzb25nczIFEC4YgAQyBhAAGBYYHjIGEAAYFhgeMgYQABgWGB4yBhAAGBYYHjIGEAAYFhgeMgYQABgWGB4yBhAAGBYYHjIGEAAYFhgeMgYQABgWGB5IpBVQAFi9FHABeACQAQCYAb4BoAHYEaoBBDAuMTe4AQPIAQD4AQGYAhKgAqESwgILEC4YgAQYsQMYgwHCAgUQABiABMICDhAAGIAEGLEDGIMBGIoFwgIREC4YgAQYsQMY0QMYgwEYxwHCAgsQABiABBixAxiDAcICCBAuGIAEGLEDwgIUEC4YgAQYsQMY0QMYgwEYxwEYigXCAggQABiABBixA8ICDhAuGIAEGLEDGNEDGMcBwgIMEC4YgAQYsQMYChgLwgIFECEYoAGYAwCSBwQxLjE3oAeLowGyBwQwLjE3uAeeEsIHCDAuNi4xMS4xyAc4&sclient=gws-wiz): Trilha sonora tema do jogo.

---

## I.A. / Prompts:

**Nota:** Durante o desenvolvimento, utilizei IA como ferramenta de consulta e auxílio, principalmente para:
- Esclarecimento de conceitos do LibGDX
- Resolução de problemas específicos
- Utilização da API do Google Sheets e APP Script
- Geração de assets visuais
- Criação do banco de questões

A seguir, alguns prompts utilizados:

- **Interface:**
    - Qual a melhor forma de gerenciar diferentes telas (menu, jogo, ranking) em um jogo LibGDX? Existe algum padrão de projeto comum para isso?
    - Qual a maneira correta de criar interfaces de usuário organizadas com o LibGDX?"
    - Como criar barra de rolagem libGDX Scene2D.UI?
    - Como usar a table corretamente libGDX
    - Viewport libGDX qual usar e como configura
    - O que são 'skins' no LibGDX e quais arquivos são necessários para usá-las (.json, .atlas, .png)? Onde devo colocá-los no projeto
    - Como funciona clicklistener Scene2D.UI

- **Assets**
  - Como colocar musica projetos libGDX
  
- **Banco de questões:**
    - Baseado nessas 3 questões e nessa estrutura json, crie mais questões no mesmo padrão e variando os estilos.  

- **Parsing JSON:**
    - Como funciona a biblioteca JSON do LibGDX e como fazer o parsing de um arquivo JSON para objetos Java
    - É melhor usar ArrayList ou Array (do LibGDX) para armazenar listas de dados dinâmicas?
    - Parsing de questões libGDX JSON

- **Jogo e controle de estados:**
  - Por que meu timer está reiniciando toda vez que eu mudo de tela? Como posso manter a mesma instância do TimerManager entre transições de tela?
  - Como implementar um timer no libGDX
  - Como posso verificar por id das questões se ja foram usadas e evitar repetições?
  - Estrutura de um validador de respostas em jogo de perguntas e respostas java

- **API:**
  - Como utilizar uma planilha google sheets como backend para um sistema de ranking em um jogo
  - Gere o código de um Google Apps Script que receba requisições POST com nome e pontuação e salve na planilha ja organizando em ordem decrescente de pontuação e também com
um método GET que retorne todos os dados em JSON.

- **Arquitetura do código:** 
  - Boas práticas de orientação a objetos em jogos Java com LibGDX
  - Como organizar corretamente classes e responsabilidades em um jogo Java usando LibGDX seguindo princípios de orientação
