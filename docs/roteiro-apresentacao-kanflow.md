# Roteiro de apresentação — Kanflow

**Duração sugerida:** 8–15 minutos (ajusta cortando o “detalhamento” ou o demo ao vivo).  
**Quem fala:** 1 apresentador ou reparte blocos entre equipa (objetivo / demo / acessibilidade).

---

## 0. Abertura (30–45 s)

> “Boa tarde / Bom dia. Somos a equipa do **Kanflow**, uma solução de gestão de trabalho em estilo **Kanban**, pensada para equipas que precisam de visibilidade do que está a ser feito, priorização e histórico de entregas.  
> Vamos começar pelo **porquê** do sistema, depois uma **visão geral**, as **funcionalidades** com foco no essencial, e por fim o que fizemos em termos de **acessibilidade**.”

*(Opcional: partilhar ecrã com logo ou primeira tela da app.)*

---

## 1. Objetivo, públicos e necessidades (2–3 min)

### Objetivo do sistema

> “O **objetivo** do Kanflow é **organizar o trabalho em fluxo visual** — colunas e cartões — para que qualquer pessoa na equipa saiba **o que está planeado, em curso e concluído**, sem depender de folhas soltas ou mensagens dispersas.”

### Principais públicos-alvo

| Público | Porque usam o Kanflow |
|---------|------------------------|
| **Equipas de desenvolvimento / produto** | Backlog, sprint, revisão de código, testes, entrega — alinhado ao fluxo real de software. |
| **Gestores de projeto / Scrum Masters** | Visão do board, histórico de sprints, métricas de conclusão. |
| **Membros individuais** | Responsável por card, pontos, tempo estimado — clareza do “meu” trabalho. |
| **Pequenas equipas ou projetos académicos** | Plano gratuito limitado; planos pagos para histórico e mais projetos. |

### Necessidades que o sistema atende (ou melhora)

- **Clareza:** um só lugar para tarefas, descrição, checklist e comentários.  
- **Priorização:** status e colunas refletem o que entra primeiro no desenvolvimento.  
- **Rastreio:** responsável, story points e tempo estimado apoiam planeamento e retrospectivas.  
- **Continuidade:** ao **concluir sprint**, o sistema **guarda histórico e snapshot** do quadro — útil para relatórios e para não “perder” o contexto daquela iteração.  
- **Colaboração:** comentários e itens de checklist partilhados no card.  
- **Gestão de conta:** registo, login, planos e billing (incluindo fluxo de checkout simulado em desenvolvimento).

---

## 2. Visão geral do sistema (1,5–2,5 min)

### Narrativa sugerida (fluxo de interfaces)

> “Do ponto de vista do utilizador, o fluxo típico é:  
> **1)** Registo ou login → **2)** lista de **workspaces** (projetos) → **3)** dentro de um workspace, o **board** com colunas (ex.: To Do, Doing, Done) → **4)** **cards** que se movem entre estados ou colunas → **5)** detalhe do card com checklist e comentários → **6)** quando a equipa fecha um ciclo, **concluir sprint** para gravar histórico e preparar o próximo quadro.”

### Diagrama mental (podes desenhar no quadro ou mostrar slide)

```
[Auth] → [Workspaces] → [Board + Cards] → [Checklist / Comentários]
                              ↓
                    [Concluir sprint] → [Histórico / snapshots]
                              ↓
                    [Planos / billing] (se integrarem na demo)
```

**Nota para apresentação ao vivo:** segue esta ordem no ecrã — é a mesma ordem “essencial” da secção seguinte.

---

## 3. Detalhamento das funcionalidades (4–8 min)

### 3.1 Essenciais (fazer primeiro na demo)

1. **Autenticação**  
   - Registo, login, JWT.  
   - *Frase:* “Só utilizadores autenticados acedem aos dados do projeto.”

2. **Workspaces (projetos)**  
   - Criar, listar, editar, apagar (conforme permissões do teu front).  
   - *Frase:* “Cada workspace é um espaço isolado para um projeto ou equipa.”

3. **Board e colunas**  
   - Visualizar colunas; se o produto permitir, reorganizar nomes/ordem.  
   - *Frase:* “O board espelha o fluxo de trabalho da equipa.”

4. **Cards**  
   - Criar card com título, descrição, **pontos** (Fibonacci), **tempo estimado**, **status**, **responsável**.  
   - Mover card (status / posição); pesquisa no workspace.  
   - *Frase:* “O card é a unidade de trabalho; concentra planeamento e execução.”

5. **Checklist e comentários**  
   - Itens de checklist no card; comentários para discussão assíncrona.  
   - *Frase:* “Partilham contexto sem sair do cartão.”

6. **Sprint — concluir e histórico**  
   - Ação de **concluir sprint**: grava histórico, snapshot e métricas (ex.: total de cards, concluídos, checklist).  
   - Listar sprints passados e consultar snapshot.  
   - *Frase:* “Fechamos o ciclo com registo para auditoria e retrospectiva.”

### 3.2 Complementares (se houver tempo)

- **Planos e billing:** Free vs Basic vs Full; limites de workspaces; histórico de sprints em planos pagos; checkout mock / confirmação em dev.  
- **Seed de dados** (se for demo técnica): script para popular sprints e cards para testes de performance ou UI cheia.

### 3.3 Frase de transição para acessibilidade

> “Além das funcionalidades, preocupámo-nos com **quem** usa o sistema — incluindo pessoas com **deficiência visual**, **dificuldade motora** ou que **navegam só por teclado**. Passo a explicar o que implementámos em **acessibilidade**.”

---

## 4. Acessibilidade — para quem, o quê, o que destacar (1,5–3 min)

### Para quem

- **Utilizadores cegos ou com baixa visão:** leitores de ecrã (NVDA, JAWS, VoiceOver) e zoom.  
- **Utilizadores com mobilidade reduzida:** navegação por **teclado** sem depender do rato.  
- **Utilizadores com daltonismo ou sensibilidade visual:** **contraste** e cores que não transmitam só informação por cor.  
- **Todos:** textos claros, foco visível, erros compreensíveis.

### O que dizer (ajusta ao que o **frontend** realmente tem)

Preenche esta lista com exemplos **concretos** do vosso repositório front / protótipo (títulos de commits, componentes, prints):

| Tema | O que podem dizer se estiver implementado |
|------|-------------------------------------------|
| **Estrutura semântica** | Uso de cabeçalhos (`h1`–`h3`), `main`, `nav`, listas para o board. |
| **Teclado** | Tab order lógico; atalhos ou Enter/Espaço para abrir card; arrastar com alternativa. |
| **ARIA / nomes acessíveis** | `aria-label` em botões de ícone; estado do card anunciável (ex.: “em desenvolvimento”). |
| **Contraste** | Texto e botões acima de 4,5:1 (WCAG AA) nos temas usados na demo. |
| **Formulários** | `label` associado a cada input; mensagens de erro ligadas ao campo (`aria-describedby`). |
| **Foco** | Contorno de foco visível; não remover com CSS sem substituto. |

**Se o backend for o foco da banca:** podes dizer que a **API devolve dados estruturados** (títulos, estados, textos) que o cliente web usa para montar interfaces **compatíveis com leitores de ecrã**, e que a **camada de acessibilidade** está no front conforme WCAG 2.1 referenciada na documentação do projeto.

**Se ainda não houver muito implementado:** sê honesto: “Temos **acessibilidade básica** no protótipo / roadmap: [listar 1–2 itens reais]. O objetivo é evoluir para WCAG 2.1 nível AA nas próximas iterações.”

---

## 5. Encerramento (30 s)

> “Em resumo: o Kanflow **apoia equipas** a ver trabalho, **priorizar** e **registar sprints**. As funcionalidades essenciais passam por **autenticação, projetos, board, cards, colaboração no card e histórico de sprints**. Quanto à **acessibilidade**, [resumo em uma frase do que está feito]. Obrigado — abrimos a **perguntas**.”

---

## Checklist rápido antes de “sair apresentando”

- [ ] Conta demo ou utilizador de teste criado e **login testado**.  
- [ ] Pelo menos **um workspace** e **vários cards** visíveis (ou script de seed).  
- [ ] **Concluir sprint** testado uma vez (sabes o que acontece ao quadro).  
- [ ] Lista de **2–3 bullets** de acessibilidade alinhada ao que está no **front**.  
- [ ] **Backup:** Swagger ou vídeo gravado se a rede falhar.

---

## Texto para slide único “visão + público” (opcional)

**Kanflow** — gestão visual de trabalho (Kanban) para equipas de software e projetos. **Público:** equipas, gestores, membros com tarefas atribuídas. **Valor:** clareza, histórico de sprints, colaboração no card, planos para crescer com o projeto.

---

*Este ficheiro é um guia de fala; adapta tempos e nomes de menus ao teu frontend real.*
