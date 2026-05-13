# Kanflow — Planos, assinaturas e limites comerciais

Valores **sugeridos** para apresentação / negociação (Brasil, **BRL**). Ajuste livremente antes de publicar.

| Plano | Mensal (por workspace / conta) | Anual (2 meses grátis) |
|-------|--------------------------------|-------------------------|
| **Free** | **R$ 0,00** | — |
| **Básico** | **R$ 39,00** / mês | **R$ 390,00** / ano (~R$ 32,50/mês) |
| **Full** | **R$ 119,00** / mês | **R$ 1.190,00** / ano (~R$ 99,17/mês) |

*Notas comerciais opcionais:* período de teste do Básico/Full (ex.: 14 dias); desconto para ONGs/educação; faturamento via nota para PJ.

---

## 3.1. Plano Free (Entrada)

**Preço:** **R$ 0,00** (sem fidelidade; pode exigir cadastro com e-mail).

**Inclui:**

- até **1** projeto (workspace) ativo;
- até **5** usuários no projeto;
- quadro Kanban básico (colunas e cartões);
- checklist simples por cartão;
- comentários básicos (conforme produto);
- suporte por **base de ajuda / e-mail** (tempo de resposta não garantido);
- **pequenos anúncios** na plataforma (faixas ou blocos discretos).

**Limitações típicas:**

- **sem** persistência de histórico de sprints passadas (ou retenção muito curta, ex.: última sprint apenas);
- armazenamento reduzido para anexos (ex.: até **100 MB** por workspace — valor ilustrativo);
- sem relatórios avançados, sem permissões granulares por cargo;
- sem integrações premium;
- possível marca d’água ou menção “Plano Free” em relatórios exportados (se existirem).

---

## 3.2. Plano Básico (Pequenas empresas)

**Preço:** **R$ 39,00 / mês** ou **R$ 390,00 / ano** (cobrança recorrente; cancelamento conforme termos).

**Inclui:**

- **projetos ilimitados** (workspaces) *ou* limite alto conforme regra vigente (ex.: até **10** workspaces — definir na política final);
- até **15** usuários **no total** na conta *ou* por workspace (escolher uma regra e manter consistente no contrato);
- quadro Kanban completo + checklist + comentários;
- **histórico de tarefas** e **histórico de sprints** com retenção padrão (ex.: **12 meses**);
- **relatórios simples** (exportação CSV/PDF básico, visão por status/coluna);
- **1** perfil administrativo + demais como membro/visualizador (acesso administrativo **restrito** ao dono da assinatura);
- **sem anúncios** na interface;
- armazenamento ampliado (ex.: **5 GB** por conta — ilustrativo);
- suporte por e-mail com SLA leve (ex.: até **2 dias úteis**).

**Não inclui (ou versão limitada):**

- dashboards executivos avançados;
- permissões por cargo muito finas (RBAC completo);
- integrações avançadas (API webhooks ilimitados, SSO corporativo, etc.).

---

## 3.3. Plano Full (Empresarial)

**Preço:** **R$ 119,00 / mês** ou **R$ 1.190,00 / ano** (recorrente; opção **contrato anual PJ** com NF e condições negociadas).

**Inclui:**

- **usuários ilimitados** *ou* limite alto (ex.: **100+**) conforme regra vigente e acordo comercial;
- **projetos ilimitados** (ou teto muito alto alinhado a infraestrutura);
- **dashboards avançados** (métricas, lead time, throughput, burndown simplificado, etc., conforme roadmap);
- **relatórios completos** (filtros, períodos, exportações, agendamento se disponível);
- **permissões por cargo** (admin, gestor, membro, visualizador — alinhado ao modelo `PerfilUsuario` + regras futuras);
- **integrações avançadas** (webhooks, API com limites maiores, conectores conforme disponibilidade);
- **histórico e auditoria** ampliados (retenção estendida, ex.: **24 meses** ou “conforme plano”);
- armazenamento elevado (ex.: **50 GB** ou pacotes adicionais);
- **suporte prioritário** (ex.: até **8 horas úteis** primeiro contato; canais adicionais conforme disponibilidade);
- **sem anúncios**; SLA e opções de **Customer Success** em contratos maiores.

**Add-ons comerciais (opcional):**

- pacotes de armazenamento;
- horas de implementação / treinamento;
- SSO (SAML/OIDC) e ambiente dedicado sob orçamento separado.

---

## Resumo para o front / produto

| | Free | Básico | Full |
|---|:---:|:---:|:---:|
| Preço mensal | R$ 0 | R$ 39 | R$ 119 |
| Preço anual | — | R$ 390 | R$ 1.190 |
| Projetos ativos | 1 | ilimitado* | ilimitado* |
| Usuários | até 5 | até 15 | ilimitado* |
| Histórico de sprints | não / mínimo | sim | sim + ampliado |
| Anúncios | sim | não | não |
| Relatórios | não | simples | completos |

\*Definir teto exato na política comercial e na implementação futura de **limites por plano** no backend.

---

*Documento conceitual; integração com gateway de pagamento (Stripe, Pagar.me, etc.) e tabelas `plano`, `assinatura`, `workspace` ficam para evolução técnica do produto.*
