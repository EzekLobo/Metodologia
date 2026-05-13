import fs from "node:fs";

const contentPath = "app/src/main/assets/study_content.json";
const mainActivityPath = "app/src/main/java/br/com/metodologia/cards/MainActivity.kt";
const validationTestPath = "app/src/test/java/br/com/metodologia/cards/StudyContentValidationTest.kt";

const cp1252 = new Map([
  [0x20ac, 0x80], [0x201a, 0x82], [0x0192, 0x83], [0x201e, 0x84],
  [0x2026, 0x85], [0x2020, 0x86], [0x2021, 0x87], [0x02c6, 0x88],
  [0x2030, 0x89], [0x0160, 0x8a], [0x2039, 0x8b], [0x0152, 0x8c],
  [0x017d, 0x8e], [0x2018, 0x91], [0x2019, 0x92], [0x201c, 0x93],
  [0x201d, 0x94], [0x2022, 0x95], [0x2013, 0x96], [0x2014, 0x97],
  [0x02dc, 0x98], [0x2122, 0x99], [0x0161, 0x9a], [0x203a, 0x9b],
  [0x0153, 0x9c], [0x017e, 0x9e], [0x0178, 0x9f],
]);

function encodeCp1252(text) {
  return Buffer.from([...text].map((char) => {
    const code = char.codePointAt(0);
    if (code <= 0xff) return code;
    return cp1252.get(code) ?? 0x3f;
  }));
}

function repairText(text) {
  let fixed = text;
  if (/Ã|Â|â[€†‡‘’“”]/.test(fixed)) {
    fixed = encodeCp1252(fixed).toString("utf8");
  }
  return fixed
    .replaceAll("??o", "ção")
    .replaceAll("??es", "ções")
    .replaceAll("Experimentação", "Experimentação")
    .replaceAll("experimenta??o", "experimentação")
    .replaceAll("Experimenta??o", "Experimentação")
    .replaceAll("formula??o", "formulação")
    .replaceAll("aquisi??o", "aquisição")
    .replaceAll("redu??o", "redução")
    .replaceAll("transmiss?o", "transmissão")
    .replaceAll("compreens?o", "compreensão")
    .replaceAll("padr?es", "padrões")
    .replaceAll("opini?o", "opinião")
    .replaceAll("cren?a", "crença")
    .replaceAll("sequ?ncia", "sequência")
    .replaceAll("conte?do", "conteúdo")
    .replaceAll("te?rico", "teórico")
    .replaceAll("te?rica", "teórica")
    .replaceAll("t?pico", "tópico")
    .replaceAll("gen?rica", "genérica")
    .replaceAll("m?xima", "máxima")
    .replaceAll("di?ria", "diária")
    .replaceAll("repeti??o", "repetição")
    .replaceAll("correla??es", "correlações")
    .replaceAll("espont?neas", "espontâneas")
    .replaceAll("metodol?gico", "metodológico")
    .replaceAll("metodol?gica", "metodológica")
    .replaceAll("compat?vel", "compatível")
    .replaceAll("ser?", "será")
    .replaceAll("tamb?m", "também")
    .replaceAll("car?ter", "caráter")
    .replaceAll("?til", "útil")
    .replaceAll("? o modo", "É o modo")
    .replaceAll("não ?", "não é")
    .replaceAll(" ? ", " é ")
    .replaceAll("est? ", "está ")
    .replaceAll("voc?", "você")
    .replaceAll("al?m", "além")
    .replaceAll("dom?nio", "domínio")
    .replaceAll("memoriza??o", "memorização")
    .replaceAll("articula??o", "articulação")
    .replaceAll("recupera??o", "recuperação")
    .replaceAll("conex?o", "conexão")
    .replaceAll("metacogni??o", "metacognição")
    .replaceAll("exig?ncia", "exigência")
    .replaceAll("relev?ncia", "relevância")
    .replaceAll("expl?citos", "explícitos")
    .replaceAll("sin?nimos", "sinônimos")
    .replaceAll("ju?zo", "juízo")
    .replaceAll("s?", "só")
    .replaceAll("evid?ncia", "evidência")
    .replaceAll("conclus?o", "conclusão")
    .replaceAll("avan?ada", "avançada")
    .replaceAll("vis?o", "visão")
    .replaceAll("mudan?a", "mudança")
    .replaceAll("Mudan?a", "Mudança")
    .replaceAll("come?a", "começa")
    .replaceAll("refer?ncia", "referência")
    .replaceAll("tens?o", "tensão")
    .replaceAll("tr?ade", "tríade")
    .replaceAll("cr?tica", "crítica")
    .replaceAll("cr?tico", "crítico")
    .replaceAll("p?blica", "pública")
    .replaceAll("emp?rica", "empírica")
    .replaceAll("acad?mica", "acadêmica")
    .replaceAll("bibliogr?fico", "bibliográfico")
    .replaceAll("investig?vel", "investigável")
    .replaceAll("provis?ria", "provisória")
    .replaceAll("import?ncia", "importância")
    .replaceAll("consequ?ncia", "consequência")
    .replaceAll("epistemol?gicos", "epistemológicos")
    .replaceAll("Compet?ncia", "Competência")
    .replaceAll("n?veis", "níveis")
    .replaceAll("mec?nica", "mecânica")
    .replaceAll("espont?neo", "espontâneo")
    .replaceAll("sistem?tica", "sistemática")
    .replaceAll("tem?tico", "temático")
    .replaceAll("visóo", "visão")
    .replaceAll("dogm?tica", "dogmática")
    .replaceAll("coer?ncia", "coerência")
    .replaceAll("l?gico", "lógico")
    .replaceAll("explic?vel", "explicável")
    .replaceAll("apare?am", "apareçam")
    .replaceAll("refutabilidade ?", "refutabilidade é")
    .replaceAll("car?ter", "caráter")
    .replaceAll("filos?fico", "filosófico")
    .replaceAll("espec?fica", "específica")
    .replaceAll("v?rias", "várias")
    .replaceAll("o qu?", "o quê")
    .replaceAll("por qu?", "por quê")
    .replaceAll("para qu?", "para quê")
    .replaceAll("alcan??vel", "alcançável")
    .replaceAll("diferen?a", "diferença")
    .replaceAll("diferenc?a", "diferença")
    .replaceAll("est?o", "estão")
    .replaceAll("Compara??o", "Comparação")
    .replaceAll("compara??o", "comparação")
    .replaceAll("defini??o", "definição")
    .replaceAll("Defini??o", "Definição")
    .replaceAll("distin??o", "distinção")
    .replaceAll("Distin??o", "Distinção")
    .replaceAll("produ??o", "produção")
    .replaceAll("investiga??o", "investigação")
    .replaceAll("sistematiza??o", "sistematização")
    .replaceAll("demarca??o", "demarcação")
    .replaceAll("verifica??o", "verificação")
    .replaceAll("confirma??o", "confirmação")
    .replaceAll("refuta??o", "refutação")
    .replaceAll("explica??o", "explicação")
    .replaceAll("Explica??o", "Explicação")
    .replaceAll("valida??o", "validação")
    .replaceAll("Valida??o", "Validação")
    .replaceAll("aplica??o", "aplicação")
    .replaceAll("informa??o", "informação")
    .replaceAll("quest?o", "questão")
    .replaceAll("Quest?o", "Questão")
    .replaceAll("quest?es", "questões")
    .replaceAll("Quest?es", "Questões")
    .replaceAll("ci?ncia", "ciência")
    .replaceAll("Ci?ncia", "Ciência")
    .replaceAll("cient?fico", "científico")
    .replaceAll("Cient?fico", "Científico")
    .replaceAll("cient?fica", "científica")
    .replaceAll("Cient?fica", "Científica")
    .replaceAll("m?todo", "método")
    .replaceAll("M?todo", "Método")
    .replaceAll("m?todos", "métodos")
    .replaceAll("M?todos", "Métodos")
    .replaceAll("hip?tese", "hipótese")
    .replaceAll("Hip?tese", "Hipótese")
    .replaceAll("hip?teses", "hipóteses")
    .replaceAll("Hip?teses", "Hipóteses")
    .replaceAll("Raz?o", "Razão")
    .replaceAll("raz?o", "razão")
    .replaceAll("experi?ncia", "experiência")
    .replaceAll("Experi?ncia", "Experiência")
    .replaceAll("estrat?gia", "estratégia")
    .replaceAll("Estrat?gia", "Estratégia")
    .replaceAll("crit?rio", "critério")
    .replaceAll("Crit?rio", "Critério")
    .replaceAll("crit?rios", "critérios")
    .replaceAll("Crit?rios", "Critérios")
    .replaceAll("revis?o", "revisão")
    .replaceAll("Revis?o", "Revisão")
    .replaceAll("fun??o", "função")
    .replaceAll("Fun??o", "Função")
    .replaceAll("n?o", "não")
    .replaceAll("N?o", "Não")
    .replaceAll("est?", "está")
    .replaceAll("? apenas", "é apenas")
    .replaceAll("? falso", "é falso")
    .replaceAll("? verdadeira", "é verdadeira")
    .replaceAll("? verdade", "é verdade")
    .replaceAll("? viabilidade", "à viabilidade")
    .replaceAll("t?picos", "tópicos")
    .replaceAll("t?cnica", "técnica")
    .replaceAll("pr?tica", "prática")
    .replaceAll("Pr?tica", "Prática")
    .replaceAll("pr?prio", "próprio")
    .replaceAll("pr?pria", "própria")
    .replaceAll("l?gica", "lógica")
    .replaceAll("L?gica", "Lógica")
    .replaceAll("an?lise", "análise")
    .replaceAll("An?lise", "Análise")
    .replaceAll("s?ntese", "síntese")
    .replaceAll("S?ntese", "Síntese")
    .replaceAll("est?o", "estão")
    .replaceAll("Est?o", "Estão")
    .replaceAll("Sugest?o", "Sugestão")
    .replaceAll("dura??o", "duração")
    .replaceAll("rela??o", "relação")
    .replaceAll("Rela??o", "Relação")
    .replaceAll("evolu??o", "evolução")
    .replaceAll("Evolu??o", "Evolução")
    .replaceAll("transform?-lo", "transformá-lo")
    .replaceAll("par?grafos", "parágrafos")
    .replaceAll("favor?veis", "favoráveis")
    .replaceAll("prov?veis", "prováveis")
    .replaceAll("poss?vel", "possível")
    .replaceAll("racioc?nio", "raciocínio")
    .replaceAll("corre??o", "correção")
    .replaceAll("combina??es", "combinações")
    .replaceAll("situa??o", "situação")
    .replaceAll("est?o coerentes", "estão coerentes");
}

function deepRepair(value) {
  if (typeof value === "string") return repairText(value);
  if (Array.isArray(value)) return value.map(deepRepair);
  if (value && typeof value === "object") {
    return Object.fromEntries(Object.entries(value).map(([key, item]) => [key, deepRepair(item)]));
  }
  return value;
}

const source = (pdf, page, excerpt) => ({ pdf, page, excerpt });

const flashcards = [
  // Ciência e conhecimento científico
  { id: "card_ciencia_001", topicId: "ciencia_conhecimento", type: "definição", difficulty: 1, front: "O que é Metodologia Científica?", back: "É uma ciência auxiliar das demais: estuda a própria ciência, seus critérios de demarcação, métodos, técnicas de investigação e normas para comunicar conhecimento científico.", source: source("METODOLOGIA 1.pdf", 1, "Metodologia Científica é uma ciência auxiliar das demais, destinada a estudar a própria ciência") },
  { id: "card_ciencia_002", topicId: "ciencia_conhecimento", type: "função", difficulty: 1, front: "Qual é a função da metodologia no estudo científico?", back: "Ela organiza o modo de produzir, avaliar e comunicar conhecimento, evitando que a pesquisa vire opinião solta, improviso ou simples coleção de informações.", source: source("METODOLOGIA 1.pdf", 1, "definindo os critérios de demarcação científica, estudando os métodos e técnicas de investigação") },
  { id: "card_ciencia_003", topicId: "ciencia_conhecimento", type: "definição", difficulty: 1, front: "O que é método no contexto científico?", back: "É o processo de aquisição do conhecimento científico: um caminho de investigação que orienta observação, análise, crítica e comunicação dos resultados.", source: source("METODOLOGIA 1.pdf", 2, "O método: processo de aquisição do conhecimento científico") },
  { id: "card_ciencia_004", topicId: "ciencia_conhecimento", type: "relação", difficulty: 2, front: "Como método e crítica se relacionam?", back: "O método fornece elementos para analisar criticamente descobertas e comunicações científicas, permitindo avaliar se uma conclusão foi bem construída.", source: source("METODOLOGIA 1.pdf", 2, "O método fornece os elementos de análise crítica das descobertas e das comunicações") },
  { id: "card_ciencia_005", topicId: "ciencia_conhecimento", type: "distinção", difficulty: 2, front: "Por que pesquisa científica não é o mesmo que consulta?", back: "Consulta apenas reúne informações. Pesquisa científica exige investigação sistematizada e busca produzir conhecimento, indo além da compilação de conceitos.", source: source("METODOLOGIA 1.pdf", 2, "Pesquisa Científica não é Consulta ou Estudo") },
  { id: "card_ciencia_006", topicId: "ciencia_conhecimento", type: "distinção", difficulty: 2, front: "Por que pesquisa científica não é apenas estudo?", back: "Estudo pode ser leitura e compreensão de conteúdos. Pesquisa implica problema, método, investigação e produção de resposta fundamentada.", source: source("METODOLOGIA 1.pdf", 2, "Uma investigação científica vai muito além da compilação de conceitos ou teorias") },
  { id: "card_ciencia_007", topicId: "ciencia_conhecimento", type: "conceito-chave", difficulty: 1, front: "O que significa sistematizar uma investigação?", back: "Significa organizar etapas, conceitos, dados e critérios de análise para que a resposta não dependa apenas de impressão pessoal.", source: source("METODOLOGIA 1.pdf", 2, "busca a produção de um conhecimento inédito por meio da sistematização do processo de investigação") },
  { id: "card_ciencia_008", topicId: "ciencia_conhecimento", type: "aplicação", difficulty: 2, front: "Como reconhecer uma resposta que parece pesquisa, mas não é?", back: "Ela costuma listar definições sem problema, método, análise própria ou articulação entre dados e conclusão.", source: source("METODOLOGIA 1.pdf", 2, "vai muito além da compilação de conceitos ou teorias") },
  { id: "card_ciencia_009", topicId: "ciencia_conhecimento", type: "demarcação", difficulty: 2, front: "O que são critérios de demarcação científica?", back: "São parâmetros usados para distinguir conhecimento científico de outras formas de explicação, considerando método, rigor, testabilidade e comunicação.", source: source("METODOLOGIA 1.pdf", 1, "definindo os critérios de demarcação científica") },
  { id: "card_ciencia_010", topicId: "ciencia_conhecimento", type: "armadilha", difficulty: 3, front: "Qual erro evitar ao definir ciência?", back: "Evite dizer que ciência é apenas verdade absoluta. O material enfatiza método, rigor, investigação e critérios de validação, não certeza intocável.", source: source("METODOLOGIA 1.pdf", 2, "A diferença básica entre eles não está na veracidade do conhecimento") },
  { id: "card_ciencia_011", topicId: "ciencia_conhecimento", type: "comunicação", difficulty: 2, front: "Por que comunicar bem também faz parte da ciência?", back: "Porque a metodologia inclui padrões e normas para transmitir conhecimento científico de forma clara, verificável e discutível por outros.", source: source("METODOLOGIA 1.pdf", 1, "padrões e normas para transmitir conhecimento científico") },
  { id: "card_ciencia_012", topicId: "ciencia_conhecimento", type: "processo", difficulty: 2, front: "Que sequência básica ajuda a pensar uma pesquisa científica?", back: "Delimitar um problema, escolher método, investigar dados ou argumentos, analisar criticamente e comunicar uma resposta fundamentada.", source: source("METODOLOGIA 1.pdf", 2, "processo de aquisição do conhecimento científico") },
  { id: "card_ciencia_013", topicId: "ciencia_conhecimento", type: "aplicação", difficulty: 3, front: "Como transformar uma curiosidade em investigação científica?", back: "Converta a curiosidade em problema delimitado, defina conceitos, escolha procedimentos e indique como a resposta será avaliada.", source: source("METODOLOGIA 1.pdf", 11, "O que será pesquisado? O que se espera obter? Por que pesquisar?") },
  { id: "card_ciencia_014", topicId: "ciencia_conhecimento", type: "raciocínio", difficulty: 2, front: "Por que o método não é uma receita mecânica?", back: "Porque ele orienta a análise crítica; sua função não é decorar passos, mas justificar decisões de investigação e avaliação.", source: source("METODOLOGIA 1.pdf", 2, "fornece os elementos de análise crítica") },
  { id: "card_ciencia_015", topicId: "ciencia_conhecimento", type: "finalidade", difficulty: 1, front: "Qual é a finalidade central da pesquisa científica?", back: "Produzir conhecimento fundamentado por investigação sistemática, com critérios que permitam crítica, revisão e comunicação.", source: source("METODOLOGIA 1.pdf", 2, "busca a produção de um conhecimento inédito") },
  { id: "card_ciencia_016", topicId: "ciencia_conhecimento", type: "exemplo", difficulty: 2, front: "Um resumo de autores é automaticamente pesquisa científica?", back: "Não. Pode ser etapa de estudo, mas só vira pesquisa quando responde a um problema com método, análise e contribuição própria.", source: source("METODOLOGIA 1.pdf", 2, "Pesquisa Científica não é Consulta ou Estudo") },
  { id: "card_ciencia_017", topicId: "ciencia_conhecimento", type: "prova", difficulty: 3, front: "Como responder em prova sobre metodologia como ciência auxiliar?", back: "Defina metodologia, explique que ela estuda a ciência e conecte com método, demarcação, investigação e comunicação do conhecimento.", source: source("METODOLOGIA 1.pdf", 1, "ciência auxiliar das demais, destinada a estudar a própria ciência") },
  { id: "card_ciencia_018", topicId: "ciencia_conhecimento", type: "síntese", difficulty: 2, front: "Qual frase resume ciência, método e pesquisa?", back: "Ciência produz conhecimento por investigação; método organiza esse caminho; metodologia estuda e qualifica os critérios desse processo.", source: source("METODOLOGIA 1.pdf", 1, "estudar a própria ciência") },
  { id: "card_ciencia_019", topicId: "ciencia_conhecimento", type: "metacognição", difficulty: 2, front: "Que pergunta ajuda a avaliar se você entendeu método científico?", back: "Eu consigo explicar por que uma conclusão foi aceita, criticada ou recusada, em vez de apenas repetir o resultado?", source: source("METODOLOGIA 1.pdf", 2, "análise crítica das descobertas") },
  { id: "card_ciencia_020", topicId: "ciencia_conhecimento", type: "integração", difficulty: 3, front: "Como ligar metodologia científica e conhecimento inédito?", back: "A metodologia dá critérios e procedimentos; a pesquisa usa esses critérios para investigar um problema e produzir uma resposta nova ou melhor fundamentada.", source: source("METODOLOGIA 1.pdf", 2, "produção de um conhecimento inédito por meio da sistematização") },

  // Tipos de conhecimento
  { id: "card_tipos_001", topicId: "tipos_conhecimento", type: "definição", difficulty: 1, front: "O que é conhecimento de senso comum?", back: "É o modo comum, espontâneo e empírico de aprender, baseado em experiência diária, observação, repetição e associação de acontecimentos.", source: source("METODOLOGIA 1.pdf", 3, "É o modo comum, corrente e espontâneo de aprender empiricamente") },
  { id: "card_tipos_002", topicId: "tipos_conhecimento", type: "característica", difficulty: 1, front: "Quais marcas aparecem no senso comum?", back: "Praticidade, experiência cotidiana, repetição, tradição, linguagem simples e ausência de controle metodológico rigoroso.", source: source("METODOLOGIA 1.pdf", 3, "experiência diária, observação, repetição") },
  { id: "card_tipos_003", topicId: "tipos_conhecimento", type: "distinção", difficulty: 2, front: "O que diferencia conhecimento científico de senso comum?", back: "Não é simplesmente a verdade do conteúdo, mas a forma de produção: método, rigor, precisão de instrumentos e análise sistemática.", source: source("METODOLOGIA 1.pdf", 2, "não está na veracidade do conhecimento nem na natureza do objeto estudado, mas sim na forma") },
  { id: "card_tipos_004", topicId: "tipos_conhecimento", type: "armadilha", difficulty: 2, front: "Por que é errado dizer que senso comum é sempre falso?", back: "Porque ele pode acertar em muitas situações; a diferença para a ciência está nos critérios de produção e validação, não em ser sempre falso.", source: source("METODOLOGIA 1.pdf", 2, "não está na veracidade do conhecimento") },
  { id: "card_tipos_005", topicId: "tipos_conhecimento", type: "definição", difficulty: 1, front: "O que caracteriza o conhecimento científico?", back: "Ele busca explicações fundamentadas por método, rigor, controle, precisão conceitual e possibilidade de crítica.", source: source("METODOLOGIA 1.pdf", 2, "na forma, no modo, no rigor dos métodos e na precisão dos instrumentos") },
  { id: "card_tipos_006", topicId: "tipos_conhecimento", type: "comparação", difficulty: 2, front: "Como comparar ciência e senso comum sem simplificar?", back: "Compare pelo modo de construção: cotidiano e espontâneo no senso comum; sistemático, crítico e metodológico na ciência.", source: source("METODOLOGIA 1.pdf", 3, "modo comum, corrente e espontâneo") },
  { id: "card_tipos_007", topicId: "tipos_conhecimento", type: "religioso", difficulty: 2, front: "Como reconhecer o conhecimento religioso?", back: "Ele se apoia na fé, na autoridade da tradição e em verdades aceitas por crença, não por teste científico.", source: source("METODOLOGIA 1.pdf", 3, "Diferenças entre conhecimento popular, senso comum, religioso, filosófico e científico") },
  { id: "card_tipos_008", topicId: "tipos_conhecimento", type: "filosófico", difficulty: 2, front: "Como reconhecer o conhecimento filosófico?", back: "Ele trabalha com reflexão racional, argumentação e questionamento de fundamentos, sem depender necessariamente de experimento controlado.", source: source("METODOLOGIA 1.pdf", 3, "conhecimento popular, senso comum, religioso, filosófico e científico") },
  { id: "card_tipos_009", topicId: "tipos_conhecimento", type: "popular", difficulty: 1, front: "Como o conhecimento popular se aproxima do senso comum?", back: "Ambos nascem da vida cotidiana, da prática social e de saberes transmitidos pela experiência.", source: source("METODOLOGIA 1.pdf", 3, "modo comum, corrente e espontâneo de aprender empiricamente") },
  { id: "card_tipos_010", topicId: "tipos_conhecimento", type: "critério", difficulty: 2, front: "Que critério separa tipos de conhecimento em uma resposta discursiva?", back: "Use o fundamento de validação: experiência cotidiana, fé, razão filosófica ou método científico.", source: source("METODOLOGIA 1.pdf", 2, "na forma, no modo, no rigor dos métodos") },
  { id: "card_tipos_011", topicId: "tipos_conhecimento", type: "exemplo", difficulty: 2, front: "Um remédio caseiro pertence automaticamente à ciência?", back: "Não. Pode vir do senso comum; só será científico se for investigado por método, controle, análise e validação adequada.", source: source("METODOLOGIA 1.pdf", 3, "experiência diária, observação, repetição") },
  { id: "card_tipos_012", topicId: "tipos_conhecimento", type: "exemplo", difficulty: 2, front: "Uma crença religiosa pode ser analisada cientificamente?", back: "A crença em si pertence ao campo religioso, mas seus efeitos sociais podem ser objeto de pesquisa científica se houver método adequado.", source: source("METODOLOGIA 1.pdf", 3, "religioso, filosófico e científico") },
  { id: "card_tipos_013", topicId: "tipos_conhecimento", type: "prova", difficulty: 3, front: "Como iniciar uma resposta comparando tipos de conhecimento?", back: "Comece dizendo que eles diferem pelos critérios de validação e pela forma de produção, não apenas pelo tema tratado.", source: source("METODOLOGIA 1.pdf", 2, "não está na natureza do objeto estudado, mas sim na forma") },
  { id: "card_tipos_014", topicId: "tipos_conhecimento", type: "precisão", difficulty: 3, front: "Por que a precisão dos instrumentos importa?", back: "Porque a ciência depende de procedimentos e instrumentos capazes de reduzir ambiguidades, controlar observações e tornar a análise mais rigorosa.", source: source("METODOLOGIA 1.pdf", 2, "precisão dos instrumentos usados no aprendizado") },
  { id: "card_tipos_015", topicId: "tipos_conhecimento", type: "linguagem", difficulty: 2, front: "Como a linguagem muda entre senso comum e ciência?", back: "No senso comum, tende a ser cotidiana e flexível; na ciência, precisa ser conceitualmente precisa e ligada ao método.", source: source("METODOLOGIA 1.pdf", 2, "na forma, no modo, no rigor dos métodos") },
  { id: "card_tipos_016", topicId: "tipos_conhecimento", type: "integração", difficulty: 3, front: "Os tipos de conhecimento podem conviver?", back: "Sim. Eles podem tratar da vida humana por caminhos diferentes, mas não usam os mesmos critérios de validação.", source: source("METODOLOGIA 1.pdf", 3, "conhecimento popular, senso comum, religioso, filosófico e científico") },
  { id: "card_tipos_017", topicId: "tipos_conhecimento", type: "diagnóstico", difficulty: 2, front: "Como identificar uma resposta fraca sobre tipos de conhecimento?", back: "Ela costuma dizer apenas que ciência é verdade e senso comum é erro, sem explicar método, rigor e validação.", source: source("METODOLOGIA 1.pdf", 2, "não está na veracidade do conhecimento") },
  { id: "card_tipos_018", topicId: "tipos_conhecimento", type: "síntese", difficulty: 2, front: "Qual frase resume a diferença entre ciência e senso comum?", back: "O senso comum nasce da experiência cotidiana; a ciência transforma problemas em investigação metódica e criticável.", source: source("METODOLOGIA 1.pdf", 3, "modo comum, corrente e espontâneo") },
  { id: "card_tipos_019", topicId: "tipos_conhecimento", type: "metacognição", difficulty: 2, front: "Que pergunta mostra domínio do assunto?", back: "Eu sei apontar qual critério valida cada tipo de conhecimento e explicar por que ele não é igual ao científico?", source: source("METODOLOGIA 1.pdf", 2, "rigor dos métodos e precisão dos instrumentos") },
  { id: "card_tipos_020", topicId: "tipos_conhecimento", type: "aplicação", difficulty: 3, front: "Como responder se a questão pedir diferença entre objeto e forma?", back: "Diga que o mesmo objeto pode ser tratado por saberes diferentes; o que muda é a forma de investigar, justificar e validar.", source: source("METODOLOGIA 1.pdf", 2, "não está na natureza do objeto estudado, mas sim na forma") },

  // Projeto de pesquisa
  { id: "card_projeto_001", topicId: "projeto_pesquisa", type: "processo", difficulty: 1, front: "Quais perguntas um projeto de pesquisa deve responder?", back: "Quem pesquisa, onde, o que será pesquisado, o que se espera obter, por que e para que pesquisar, quais conceitos, como pesquisar, recursos e tempo.", source: source("METODOLOGIA 1.pdf", 11, "Quem vai pesquisar? Onde? O que será pesquisado? O que se espera obter?") },
  { id: "card_projeto_002", topicId: "projeto_pesquisa", type: "definição", difficulty: 1, front: "O que é tema de pesquisa?", back: "É a definição mais geral do tópico investigado, o campo inicial que ainda precisa ser delimitado.", source: source("METODOLOGIA 1.pdf", 12, "O tema se caracteriza pela definição mais genérica do tópico") },
  { id: "card_projeto_003", topicId: "projeto_pesquisa", type: "definição", difficulty: 2, front: "O que é problema de pesquisa?", back: "É a delimitação teórica e operacional da questão que a pesquisa pretende responder.", source: source("METODOLOGIA 1.pdf", 12, "Problema se define pela máxima delimitação teórica e operacional") },
  { id: "card_projeto_004", topicId: "projeto_pesquisa", type: "definição", difficulty: 2, front: "O que é hipótese no projeto?", back: "É uma resposta inicial, elementar ou solução possível para o problema, a ser examinada pela investigação.", source: source("METODOLOGIA 1.pdf", 12, "hipótese como a resposta elementar ao problema") },
  { id: "card_projeto_005", topicId: "projeto_pesquisa", type: "encadeamento", difficulty: 2, front: "Como tema, problema e hipótese se encadeiam?", back: "O tema abre o campo; o problema delimita a pergunta; a hipótese propõe uma resposta inicial para orientar a pesquisa.", source: source("METODOLOGIA 1.pdf", 12, "tema... Problema... hipótese como a resposta elementar") },
  { id: "card_projeto_006", topicId: "projeto_pesquisa", type: "objetivo", difficulty: 2, front: "Qual é o papel dos objetivos no projeto?", back: "Eles indicam o que a pesquisa pretende alcançar e precisam conversar com problema, hipótese e metodologia.", source: source("METODOLOGIA 1.pdf", 11, "O que se espera obter?") },
  { id: "card_projeto_007", topicId: "projeto_pesquisa", type: "justificativa", difficulty: 2, front: "Que pergunta a justificativa responde?", back: "Responde por que a pesquisa deve ser feita e qual relevância teórica, prática ou social justifica o estudo.", source: source("METODOLOGIA 1.pdf", 11, "Por que pesquisar? Para que pesquisar?") },
  { id: "card_projeto_008", topicId: "projeto_pesquisa", type: "metodologia", difficulty: 2, front: "Que pergunta a metodologia do projeto responde?", back: "Responde como pesquisar: quais procedimentos, materiais, fontes, técnicas e caminhos serão usados para investigar o problema.", source: source("METODOLOGIA 1.pdf", 11, "Como pesquisar?") },
  { id: "card_projeto_009", topicId: "projeto_pesquisa", type: "viabilidade", difficulty: 2, front: "Por que recursos e tempo entram no projeto?", back: "Porque uma pesquisa precisa ser viável: deve caber nos meios disponíveis, no prazo e nas condições reais de execução.", source: source("METODOLOGIA 1.pdf", 11, "Quais as implicações? Quais recursos? Quanto tempo?") },
  { id: "card_projeto_010", topicId: "projeto_pesquisa", type: "armadilha", difficulty: 3, front: "Qual erro evitar ao formular um problema?", back: "Evite deixar o problema amplo demais. Ele deve delimitar teoricamente e operacionalmente o que será investigado.", source: source("METODOLOGIA 1.pdf", 12, "máxima delimitação teórica e operacional") },
  { id: "card_projeto_011", topicId: "projeto_pesquisa", type: "armadilha", difficulty: 3, front: "Por que hipótese não é conclusão?", back: "Porque ela é uma resposta inicial que orienta a investigação; a conclusão só aparece depois da análise.", source: source("METODOLOGIA 1.pdf", 12, "hipótese como a resposta elementar ao problema") },
  { id: "card_projeto_012", topicId: "projeto_pesquisa", type: "aplicação", difficulty: 3, front: "Como transformar 'educação' em tema pesquisável?", back: "Delimite: por exemplo, evasão em cursos noturnos de determinada instituição, período e grupo, até chegar a um problema investigável.", source: source("METODOLOGIA 1.pdf", 12, "definição mais genérica do tópico") },
  { id: "card_projeto_013", topicId: "projeto_pesquisa", type: "coerência", difficulty: 3, front: "Como avaliar a coerência interna de um projeto?", back: "Confira se tema, problema, hipótese, objetivos, justificativa e metodologia respondem uns aos outros sem contradição.", source: source("METODOLOGIA 1.pdf", 11, "O que será pesquisado? O que se espera obter? Por que pesquisar?") },
  { id: "card_projeto_014", topicId: "projeto_pesquisa", type: "conceitos", difficulty: 2, front: "Por que conceitos envolvidos precisam aparecer?", back: "Porque a pesquisa deve deixar claro o sentido dos termos centrais que sustentam problema, hipótese e análise.", source: source("METODOLOGIA 1.pdf", 11, "Quais os conceitos envolvidos na pesquisa?") },
  { id: "card_projeto_015", topicId: "projeto_pesquisa", type: "prova", difficulty: 3, front: "Como responder uma questão sobre projeto completo?", back: "Organize a resposta em sequência: tema, problema, hipótese, objetivos, justificativa, metodologia, recursos e cronograma.", source: source("METODOLOGIA 1.pdf", 11, "Quem vai pesquisar? Onde? O que será pesquisado?") },
  { id: "card_projeto_016", topicId: "projeto_pesquisa", type: "diagnóstico", difficulty: 2, front: "O que costuma faltar em um projeto fraco?", back: "Falta delimitação, coerência entre partes, justificativa clara, método adequado ou viabilidade de tempo e recursos.", source: source("METODOLOGIA 1.pdf", 11, "Por que pesquisar? Para que pesquisar? Como pesquisar?") },
  { id: "card_projeto_017", topicId: "projeto_pesquisa", type: "síntese", difficulty: 2, front: "Qual frase resume o projeto de pesquisa?", back: "É o plano que organiza o que investigar, por que investigar, como investigar e com quais condições realizar a pesquisa.", source: source("METODOLOGIA 1.pdf", 11, "O que será pesquisado? O que se espera obter?") },
  { id: "card_projeto_018", topicId: "projeto_pesquisa", type: "metacognição", difficulty: 2, front: "Que pergunta ajuda a revisar um projeto?", back: "Se eu mudar o problema, hipótese, objetivos e método ainda fazem sentido ou precisam ser ajustados?", source: source("METODOLOGIA 1.pdf", 12, "Problema se define pela máxima delimitação teórica e operacional") },
  { id: "card_projeto_019", topicId: "projeto_pesquisa", type: "aplicação", difficulty: 3, front: "Como ligar justificativa e objetivo?", back: "A justificativa explica a relevância da pesquisa; o objetivo diz o que será feito para responder ao problema relevante.", source: source("METODOLOGIA 1.pdf", 11, "Por que pesquisar? Para que pesquisar? O que se espera obter?") },
  { id: "card_projeto_020", topicId: "projeto_pesquisa", type: "integração", difficulty: 3, front: "Como demonstrar domínio sobre projeto em prova?", back: "Mostre encadeamento: tema amplo vira problema delimitado; hipótese orienta; método investiga; objetivos e justificativa dão direção e relevância.", source: source("METODOLOGIA 1.pdf", 12, "tema... problema... hipótese") },

  // Evolução do método
  { id: "card_metodo_001", topicId: "evolucao_metodo", type: "Bacon", difficulty: 1, front: "Que lógica de investigação aparece em Bacon?", back: "A ciência deve se apoiar em experimentação, registros repetidos de dados, análise posterior e formulação conceitual.", source: source("METODOLOGIA 3.pdf", 4, "o método científico deveria se pautar pela experimentação, com registros repetitivos de dados") },
  { id: "card_metodo_002", topicId: "evolucao_metodo", type: "experimentação", difficulty: 2, front: "Por que a repetição de registros é importante?", back: "Porque diminui a dependência de observações isoladas e permite análise mais controlada antes de formular conceitos.", source: source("METODOLOGIA 3.pdf", 4, "registros repetitivos de dados e posterior análise") },
  { id: "card_metodo_003", topicId: "evolucao_metodo", type: "Popper", difficulty: 1, front: "Qual critério de demarcação Popper defende?", back: "Popper desloca o foco da verificabilidade para a falseabilidade: uma teoria científica deve poder ser submetida a testes que possam refutá-la.", source: source("METODOLOGIA 3.pdf", 7, "o critério de demarcação não deve ser a verificabilidade, mas sim a falseabilidade") },
  { id: "card_metodo_004", topicId: "evolucao_metodo", type: "falseabilidade", difficulty: 2, front: "O que significa falseabilidade?", back: "Significa que uma teoria precisa admitir condições de teste capazes de mostrar que ela está errada.", source: source("METODOLOGIA 3.pdf", 7, "falseabilidade de um sistema") },
  { id: "card_metodo_005", topicId: "evolucao_metodo", type: "armadilha", difficulty: 3, front: "Qual erro evitar ao explicar falseabilidade?", back: "Não confunda com juntar muitos exemplos favoráveis. O ponto é submeter a teoria a testes que possam refutá-la.", source: source("METODOLOGIA 3.pdf", 7, "não deve ser a verificabilidade, mas sim a falseabilidade") },
  { id: "card_metodo_006", topicId: "evolucao_metodo", type: "verificabilidade", difficulty: 2, front: "Como Popper critica a verificabilidade?", back: "Ele questiona a ideia de que confirmar positivamente uma teoria basta para demarcar ciência; o teste decisivo é sua abertura à refutação.", source: source("METODOLOGIA 3.pdf", 7, "não deve ser a verificabilidade") },
  { id: "card_metodo_007", topicId: "evolucao_metodo", type: "Kuhn", difficulty: 1, front: "O que é paradigma em Kuhn?", back: "É um conjunto de referências, expectativas e modos de resolver problemas que orienta a ciência normal.", source: source("METODOLOGIA 3.pdf", 8, "paradigma que governa a ciência normal") },
  { id: "card_metodo_008", topicId: "evolucao_metodo", type: "ciência normal", difficulty: 2, front: "O que é ciência normal?", back: "É a prática científica feita dentro de um paradigma aceito, resolvendo problemas conforme suas regras e expectativas.", source: source("METODOLOGIA 3.pdf", 8, "paradigma que governa a ciência normal") },
  { id: "card_metodo_009", topicId: "evolucao_metodo", type: "anomalia", difficulty: 2, front: "O que é anomalia para Kuhn?", back: "É a percepção de que a natureza violou expectativas criadas pelo paradigma que orienta a ciência normal.", source: source("METODOLOGIA 3.pdf", 8, "consciência da anomalia... a natureza violou as expectativas") },
  { id: "card_metodo_010", topicId: "evolucao_metodo", type: "mudança", difficulty: 3, front: "Como anomalias podem levar a mudança de paradigma?", back: "Quando anomalias se tornam relevantes, o paradigma pode entrar em crise e abrir espaço para outra forma de explicar e investigar.", source: source("METODOLOGIA 3.pdf", 8, "uma descoberta científica começa com a consciência da anomalia") },
  { id: "card_metodo_011", topicId: "evolucao_metodo", type: "comparação", difficulty: 3, front: "Como comparar Popper e Kuhn?", back: "Popper enfatiza teste crítico e refutação; Kuhn enfatiza paradigmas, ciência normal, anomalias e mudanças históricas na ciência.", source: source("METODOLOGIA 3.pdf", 7, "falseabilidade de um sistema") },
  { id: "card_metodo_012", topicId: "evolucao_metodo", type: "correntes", difficulty: 2, front: "Como racionalismo e empirismo diferem?", back: "O racionalismo valoriza a razão e a dedução; o empirismo valoriza experiência, observação e dados como base do conhecimento.", source: source("METODOLOGIA 2.pdf", 1, "Correntes do Racionalismo, do Empirismo") },
  { id: "card_metodo_013", topicId: "evolucao_metodo", type: "mecanicismo", difficulty: 2, front: "O que sugere o conceito mecanicista da natureza?", back: "Sugere compreender a natureza como sistema ordenado, regular e explicável por relações de funcionamento.", source: source("METODOLOGIA 2.pdf", 1, "conceito mecanicista da natureza") },
  { id: "card_metodo_014", topicId: "evolucao_metodo", type: "integração", difficulty: 3, front: "Como ligar Bacon, Popper e Kuhn em uma resposta?", back: "Bacon destaca experimentação; Popper, teste e falseabilidade; Kuhn, paradigmas e anomalias na mudança científica.", source: source("METODOLOGIA 3.pdf", 4, "experimentação, com registros repetitivos de dados") },
  { id: "card_metodo_015", topicId: "evolucao_metodo", type: "aplicação", difficulty: 3, front: "Uma teoria que explica qualquer resultado é forte para Popper?", back: "Não. Se nada puder refutá-la, ela perde força científica no critério popperiano de falseabilidade.", source: source("METODOLOGIA 3.pdf", 7, "falseabilidade de um sistema") },
  { id: "card_metodo_016", topicId: "evolucao_metodo", type: "aplicação", difficulty: 2, front: "Como identificar um exemplo de anomalia?", back: "Procure um resultado que contraria o que o paradigma esperava e obriga os pesquisadores a reconsiderar explicações.", source: source("METODOLOGIA 3.pdf", 8, "a natureza violou as expectativas de um paradigma") },
  { id: "card_metodo_017", topicId: "evolucao_metodo", type: "prova", difficulty: 3, front: "Como responder uma questão sobre falseabilidade?", back: "Defina o conceito, contraste com verificabilidade e explique que teorias científicas precisam poder ser refutadas por testes.", source: source("METODOLOGIA 3.pdf", 7, "não deve ser a verificabilidade, mas sim a falseabilidade") },
  { id: "card_metodo_018", topicId: "evolucao_metodo", type: "prova", difficulty: 3, front: "Como responder uma questão sobre Kuhn?", back: "Explique paradigma, ciência normal e anomalia; depois mostre como anomalias podem abrir caminho para mudança científica.", source: source("METODOLOGIA 3.pdf", 8, "paradigma que governa a ciência normal") },
  { id: "card_metodo_019", topicId: "evolucao_metodo", type: "síntese", difficulty: 2, front: "Qual frase resume a evolução do método no material?", back: "O método científico passa por valorização da experimentação, crítica à simples verificação e compreensão histórica das mudanças de paradigma.", source: source("METODOLOGIA 3.pdf", 7, "critério de demarcação") },
  { id: "card_metodo_020", topicId: "evolucao_metodo", type: "metacognição", difficulty: 2, front: "Que pergunta ajuda a dominar Popper e Kuhn?", back: "Eu sei dizer se a questão pede teste crítico de teorias ou mudança histórica de paradigmas?", source: source("METODOLOGIA 3.pdf", 8, "consciência da anomalia") },
];

let root = JSON.parse(fs.readFileSync(contentPath, "utf8"));
root = deepRepair(root);
root.title = "Metodologia Científica";
root.topics = [
  { id: "ciencia_conhecimento", name: "Ciência e conhecimento científico", description: "Fundamentos de ciência, metodologia, método, pesquisa e critérios de conhecimento científico." },
  { id: "tipos_conhecimento", name: "Tipos de conhecimento", description: "Diferenças entre senso comum, conhecimento popular, religioso, filosófico e científico." },
  { id: "projeto_pesquisa", name: "Projeto de pesquisa", description: "Elementos, critérios e encadeamento de tema, problema, hipótese, objetivos e metodologia." },
  { id: "evolucao_metodo", name: "Evolução do método", description: "Racionalismo, empirismo, mecanicismo, Bacon, Popper, falseabilidade, Kuhn e paradigmas." },
];
root.flashcards = flashcards;

fs.writeFileSync(contentPath, `${JSON.stringify(root, null, 2)}\n`, "utf8");

const mainActivity = fs.readFileSync(mainActivityPath, "utf8");
fs.writeFileSync(mainActivityPath, repairText(mainActivity), "utf8");

const validationTest = fs.readFileSync(validationTestPath, "utf8");
fs.writeFileSync(validationTestPath, repairText(validationTest), "utf8");
