# Kanflow — o que já tá pronto, o que tá rolando e o que falta

Use no Trello três listas: **Já pronto** · **Em andamento** · **Falta fazer**.  
Cada bloco abaixo já está no formato **título + descrição + tempo** pra copiar pros cards.

> **Lembrete:** este repositório é só o **backend** (API). O que for tela, arrastar cartão no site ou app entra em “em andamento” ou “falta” conforme o outro projeto do grupo.

---

## 1. Já está pronto (neste backend)

Aqui o **tempo pendente é zero** no código da API — já está implementado.  
*(Se quiser, no Trello você pode colocar etiqueta “Backend OK” em vez de tempo.)*

### Infra e projeto

**Título:** Ambiente do backend (Maven, banco, Docker)  
**Descrição:** Projeto Spring Boot com Postgres, migrations Flyway, `docker-compose`, arquivos de exemplo de config e README pra subir local ou com Docker.  
**Tempo pendente:** 0 h  

---

**Título:** Documentação da API (Swagger + README)  
**Descrição:** Página Swagger pra testar rotas, README com links e lista das URLs principais (login, workspaces, cards, sprints).  
**Tempo pendente:** 0 h  

---

### Conta e segurança

**Título:** Cadastro, login e “quem sou eu”  
**Descrição:** Criar usuário, entrar com e-mail e senha, receber token de acesso e consultar dados do usuário logado. Senha guardada com hash.  
**Tempo pendente:** 0 h  

---

**Título:** Rotas protegidas por login  
**Descrição:** Quase tudo na API só funciona com token válido; cadastro/login e documentação ficam abertos. Workspaces só aparecem pro **dono** — outro usuário não acessa o projeto de outra pessoa pela API.  
**Tempo pendente:** 0 h  

---

### Workspaces, quadro e cartões

**Título:** Workspaces (projetos)  
**Descrição:** Criar, listar, ver, renomear e apagar espaços de trabalho; ao criar, já nasce o quadro com colunas padrão ou as que você mandar na criação.  
**Tempo pendente:** 0 h  

---

**Título:** Colunas do quadro  
**Descrição:** Ver o quadro completo e atualizar nomes e ordem das colunas (tipo To Do, Doing, Done).  
**Tempo pendente:** 0 h  

---

**Título:** Cartões no Kanban  
**Descrição:** Criar tarefa no projeto, editar, ver detalhe, mudar de coluna e posição, apagar. Campos como título, descrição, dificuldade e status compatíveis com o fluxo do board.  
**Tempo pendente:** 0 h  

---

**Título:** Checklist dentro do cartão  
**Descrição:** Lista de subtarefas por cartão: adicionar, editar, marcar como feito e remover.  
**Tempo pendente:** 0 h  

---

**Título:** Comentários no cartão  
**Descrição:** CRUD de comentários ligados a cada cartão pra discussão da tarefa.  
**Tempo pendente:** 0 h  

---

### Sprints e busca

**Título:** Encerrar sprint e histórico no workspace  
**Descrição:** Fechar sprint salvando um “retrato” do quadro pra consultar depois; opção de limpar o quadro sem guardar histórico; listar e abrir sprints antigos desse workspace.  
**Tempo pendente:** 0 h  

---

**Título:** Sprints como entidade + cartões na sprint  
**Descrição:** Gerenciar sprints (criar, editar, listar, apagar) e associar ou remover cartões de uma sprint.  
**Tempo pendente:** 0 h  

---

**Título:** Busca no workspace  
**Descrição:** Buscar texto nos cartões daquele projeto pra achar tarefa rápido.  
**Tempo pendente:** 0 h  

---

### Pessoas e erros

**Título:** API de usuários  
**Descrição:** Listar e gerenciar usuários pela API (criar, ver, atualizar, remover conforme endpoints).  
**Tempo pendente:** 0 h  

---

**Título:** Erros padronizados na API  
**Descrição:** Respostas de erro consistentes (ex.: não encontrado, conflito) pra facilitar debug e integração com o front.  
**Tempo pendente:** 0 h  

---

## 2. Em andamento (costuma ser isso no meio do PI)

Ajuste pro seu grupo: se algo já acabou, muda pra **Já pronto** no Trello.

**Título:** Integrar o front (ou app) com esta API  
**Descrição:** Chamadas reais do site/app pra essa API, guardar o token no login, tratar erro de rede e mensagens do servidor. Garantir que cada tela usa a URL e o body certo.  
**Tempo estimado:** 12–24 h *(divide entre o time)*  

---

**Título:** Testar o fluxo inteiro na prática  
**Descrição:** Do cadastro até fechar sprint ou buscar cartão — anotar bug, comportamento estranho e corrigir junto com front/back.  
**Tempo estimado:** 4–8 h  

---

**Título:** Todo mundo com o mesmo ambiente  
**Descrição:** Instalar Java, Docker ou Postgres, copiar `.env`, mesma porta e mesmo segredo JWT em dev pra ninguém quebrar por config diferente.  
**Tempo estimado:** 2–4 h  

---

**Título:** Telas de workspaces, quadro e cartões *(se o front já começou)*  
**Descrição:** Mostrar projetos, colunas, cartões, checklist e comentários na interface — ligando aos endpoints que já existem.  
**Tempo estimado:** 16–30 h *(depende do quanto já foi feito)*  

---

## 3. Falta fazer (ainda não tá redondo)

**Título:** Testes automatizados da API  
**Descrição:** Hoje só existe teste mínimo que sobe a aplicação; falta testar endpoints importantes com banco de teste pra não quebrar silenciosamente no próximo deploy.  
**Tempo estimado:** 6–12 h  

---

**Título:** Interface completa e UX *(se não estiver em outro repo)*  
**Descrição:** Todas as telas, navegação boa, loading e mensagens claras pro usuário — o backend sozinho não entrega isso.  
**Tempo estimado:** 20–40 h *(bem variável)*  

---

**Título:** Regras finais de perfil (admin / membro / visualizador)  
**Descrição:** O modelo já tem tipo de perfil; falta decidir o que cada um pode fazer na API ou só na interface e implementar.  
**Tempo estimado:** 4–10 h  

---

**Título:** Entrega do trabalho (documento, slides, vídeo)  
**Descrição:** PDF ou relatório que o professor pediu, apresentação e ensaio da demo ao vivo com plano B se cair internet ou banco.  
**Tempo estimado:** 4–10 h  

---

**Título:** Revisão extra de segurança *(opcional)*  
**Descrição:** Passada final pra garantir que nenhum ID na URL vaza dado de outro usuário em caso de teste malicioso — mais auditoria do que feature nova.  
**Tempo estimado:** 2–4 h  

---

## Cola rápida no Trello (só títulos)

| Lista | Cards (títulos) |
|--------|-----------------|
| **Já pronto** | Infra + Docker · Swagger/README · Login JWT · Rotas protegidas · Workspaces · Colunas · Cartões · Checklist · Comentários · Sprints + histórico · Sprints + cartões · Busca · Usuários · Erros padronizados |
| **Em andamento** | Integrar front com API · Testes manuais ponta a ponta · Ambiente igual no grupo · Telas *(se aplicável)* |
| **Falta fazer** | Testes automáticos · UX/telas *(se faltar)* · Regras de perfil · Entrega documento/apresentação · Revisão segurança *(opcional)* |

Os tempos são **estimativas**; troca conforme o que o time já fechou.
