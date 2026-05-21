# Atividade académica — 15 de maio de 2026  
## Auditoria de sistemas, ISO 27001, ciclo PDCA e Common Criteria (ISO/IEC 15408)

**Disciplina:** Segurança e Auditoria de Sistemas (conforme material *Isos PDca e fases da auditoria*).  
**Objetivo:** Reescrever as instruções em formato académico, responder às questões iniciais com fundamentação conceitual e apresentar seis questões originais (três dissertativas e três de múltipla escolha com gabarito comentado).

---

## Parte I — Reescrita académica das instruções da atividade

### I.1 Atividade de aplicação prática (cenário da fintech)

**Enunciado formalizado:**  
Foi estabelecido o seguinte cenário de auditoria: a organização é uma **instituição de pagamento de natureza fintech**, sediada no Brasil, que processa **pagamentos instantâneos via arranjo PIX**, tratando-se, portanto, de ambiente de **alta criticidade** em termos de confidencialidade, integridade, disponibilidade e conformidade regulatória perante o **Sistema Financeiro Nacional** e normas do **Banco Central do Brasil (BCB)**. A fintech implementa um **Sistema de Gestão de Segurança da Informação (SGSI)** alinhado à **ABNT NBR ISO/IEC 27001**, visando certificação. Adicionalmente, a custódia de **chaves criptográficas** materializa-se mediante **Hardware Security Module (HSM)** avaliado segundo **Common Criteria (ISO/IEC 15408)**, com **Evaluation Assurance Level (EAL)** declarado. O estudante assume o papel de **auditor líder**, devendo articular o **ciclo PDCA** (Plan-Do-Check-Act), os **controles do Anexo A** da ISO 27001, os **níveis de garantia EAL** e a **melhoria contínua** (ações corretivas), demonstrando capacidade de **levantamento de evidências** e de **raciocínio baseado em risco**.

### I.2 Atividade de fixação — Bloco 1 (fundamentos)

**Enunciado formalizado:**  
Complementarmente, propõe-se a **atividade de fixação** do Bloco 1, na qual se aplicam **fundamentos de auditoria de sistemas**: **taxonomia de abordagens** (ao redor, através e com o computador), **interpretação de trilhas de auditoria** (logs) em contexto de **ameaça** (ex.: força bruta seguida de sucesso) e **auditoria de controles de acesso lógico** (ciclo de vida de identidades, **princípio do menor privilégio** e **revisão periódica**). Espera-se **argumentação técnica** e articulação com **gestão de riscos** e **classificação de risco** (impacto e probabilidade).

---

## Parte II — Respostas às questões iniciais do material de aula

### II.1 Atividade de aplicação prática — Questão 1 (PDCA: CHECK e ACT)

**Enunciado (síntese):** Identificar atividades de auditoria nas fases **CHECK** e **ACT** no contexto da fintech com SGSI ISO 27001 e indicar **evidências** para validação.

**Resposta:**  
Na **ISO/IEC 27001**, o **PDCA** estrutura o SGSI: **CHECK** corresponde à **avaliação do desempenho** — em particular às **auditorias internas** (cláusula 9.2), **monitoramento e medição**, e à **análise crítica pela direção** (cláusula 9.3). No papel de auditor líder, atividades típicas da fase **CHECK** incluem: planejamento e execução da auditoria com base no **escopo** e na **Declaração de Aplicabilidade (SoA)**; **entrevistas**; **inspeção documental** de políticas, procedimentos e registros; **testes** (amostragem ou análise assistida por computador — CAATs) sobre logs de PIX, segregação de funções, MFA, backups; e **observação** de práticas operacionais. **Evidências** solicitáveis: relatórios de auditoria interna anteriores, planos de auditoria, papeis de trabalho, amostras de logs correlacionados (SIEM), resultados de pentest, atas de análise crítica pela direção, KPIs de segurança (MTTD, MTTR, vulnerabilidades corrigidas no prazo).

A fase **ACT** materializa a **melhoria contínua** e o tratamento de **não conformidades** e **ações corretivas** (cláusula 10). Em auditoria, verifica-se se achados foram **formalizados**, se há **análise de causa raiz**, **planos de ação** com responsáveis e prazos, e **eficácia** verificada (reauditoria ou teste de follow-up). **Evidências**: registros de não conformidade, 5 Porquês / Ishikawa, tickets encerrados com evidência de reteste, atualização de análise de riscos e da SoA após mudanças.

### II.2 Atividade de aplicação prática — Questão 2 (cinco controles do Anexo A)

**Enunciado (síntese):** Listar **cinco** controles do Anexo A **mais críticos** para fintech com PIX e justificar pelo **risco de negócio**.

**Resposta (exemplo fundamentado; temas ISO 27001:2022):**  
1. **Controles criptográficos** — PIX e chaves em HSM exigem **proteção criptográfica** adequada (geração, armazenamento, rotação, **integridade** de instruções de pagamento). Risco: fraude, repúdio, violação de confidencialidade.  
2. **Gestão de acesso** — **identidades**, **privilégios** mínimos e **revisão** periódica reduzem abuso interno e comprometimento de contas com poder de movimentação. Risco: transações não autorizadas.  
3. **Registo de eventos / monitoramento** — **deteção** de padrões anómalos (força bruta, alteração de beneficiário, volume atípico). Risco: incidente prolongado sem deteção.  
4. **Gestão de vulnerabilidades** — superfície de APIs e integrações PIX; **patch** e testes. Risco: exploração técnica.  
5. **Continuidade, backups e disponibilidade** — indisponibilidade do arranjo afeta **resiliência** operacional e regulatória. Risco: perda de disponibilidade e impacto sistêmico.

*(A numeração exata dos itens do Anexo A pode ser mapeada na SoA da organização; a argumentação permanece válida sob a lógica de risco da norma.)*

### II.3 Atividade de aplicação prática — Questão 3 (EAL 4+ no HSM e BCB)

**Enunciado (síntese):** Significado de **EAL 4+** para o auditor; suficiência para pagamentos críticos; referência a requisitos do BCB.

**Resposta:**  
Nos **Common Criteria (ISO/IEC 15408)**, o **EAL** expressa o **grau de rigor da avaliação** (profundidade de análise e evidências), não uma “nota de segurança absoluta” do produto. **EAL 4+** indica avaliação **metodicamente projetada**, com análise de *design* e testes rigorosos — patamar **comum** para firewalls, smartcards e produtos de mercado de elevada exigência. Para o **auditor**, o certificado CC constitui **evidência de terceira parte** de que reivindicações de segurança do **TOE** (HSM) foram **verificadas** em laboratório credenciado; deve-se confrontar o **Security Target (ST)** com o **contexto** da fintech (funções criptográficas usadas, integração, políticas de chaves).

Quanto à **suficiência** isolada: o EAL **não substitui** o cumprimento do **SGSI** nem das **exigências prudenciais** do BCB (ex.: **Resolução BCB nº 85, de 2021**, que dispõe sobre política de segurança cibernética e gestão de risco cibernético nas instituições financeiras). O HSM EAL 4+ **fortalece** a linha de defesa para **custódia de chaves**, mas a **suficiência** depende da **arquitetura**, **procedimentos**, **segregação**, **logs**, **resposta a incidentes** e **governança** — todos objeto de auditoria ISO 27001 e de conformidade regulatória. O auditor deve **cruzar** certificação de produto com **controles organizacionais e processuais** exigidos ao arranjo de pagamentos.

### II.4 Atividade de aplicação prática — Questão 4 (plano de ação corretiva — logs HSM)

**Enunciado (síntese):** **Não conformidade:** logs de acesso ao HSM **não revistos** regularmente. Elaborar **plano de ação corretiva** com **causa raiz** e **indicadores de eficácia**.

**Resposta:**  
**1) Registo da não conformidade:** desvio face a controles de **registo e monitoramento** e à operacionalização do SGSI (revisão de registos de segurança).  
**2) Análise de causa raiz (ex.: 5 Porquês):** Porque não há revisão regular? → Falta de procedimento formal / responsável / ferramenta. Porque? → Ausência de trigger no processo de SIEM/governança. Porque? → Subestimação de risco ou lacuna de KPI. **Causa raiz típica:** **processo** não definido ou **recurso** não alocado (não apenas “falha humana pontual”).  
**3) Ação corretiva:** aprovar **procedimento** de revisão (frequência, amostragem ou 100%, responsável, escalação); integrar **alertas** no SIEM; **treinamento** da equipe de operações; **checklist** de revisão mensal assinado.  
**4) Indicadores de eficácia:** percentual de ciclos de revisão concluídos no prazo; número de **achados** críticos detetados e tratados; zero repetição da NC em **reauditoria**; redução de **tempo entre evento e deteção** (MTTD) em cenários de teste.  
**5) Verificação:** follow-up em **90 dias** com amostragem de revisões e entrevista ao responsável.

### II.5 Atividade de fixação — Bloco 1 (três questões curtas)

**Q1 — SaaS folha sem código:** A abordagem mais adequada é, em regra, **“ao redor do computador”** (*around the computer*): comparação de **entradas e saídas** (totais da folha, encargos, eSocial, consistência temporal) sem acesso ao processamento interno, conforme o material. **Limitação:** resultados aparentemente corretos podem mascarar manipulação interna — combinar com **cláusulas contratuais** (SOC 2, direito de auditoria) e, quando possível, **“com o computador”** (CAATs sobre exportações).

**Q2 — 500 falhas de login e 1 sucesso às 3h:** Trata-se de **indício** de **força bruta** seguida de **comprometimento** possível. Próximos passos: **preservar evidências**; **correlacionar** IP, contas e janela temporal; **entrevistar** administradores; verificar **bloqueio** de conta e **MFA**; avaliar **ações pós-login** (comandos privilegiados, alteração de logs); **escalar** a resposta a incidentes; documentar no **papel de trabalho** da auditoria.

**Q3 — Ex-funcionário com acesso após 45 dias:** **Risco:** em geral **alto** (integridade e confidencialidade de dados financeiros; violação de **menor privilégio** e **ciclo de vida de identidade**). **Controle falho:** **provisão/revogação** e **revisão periódica de acessos**. **Solução sistémica:** integração **RH–IAM** (desligamento automático), **revisões trimestrais** certificadas, **alertas** para contas inativas, **métricas** de tempo médio de revogação.

---

## Parte III — Seis questões originais (3 dissertativas + 3 múltipla escolha)

### III.A Questões dissertativas

**D1.** Discuta a distinção entre **eficácia da avaliação** (nível **EAL** nos Common Criteria) e **eficácia do controlo** no contexto do SGSI ISO 27001. Por que a certificação de um HSM em **EAL elevado** não dispensa a organização de implementar **revisão de logs** e **gestão de mudanças**?

**D2.** Analise o papel da **Declaração de Aplicabilidade (SoA)** na **auditoria de certificação** ISO 27001 de uma fintech. Como o auditor utiliza a SoA para **direcionar testes** e para **lidar com exclusões** de controles do Anexo A sem comprometer a **aceitação de risco** documentada?

**D3.** Uma organização concentra melhorias apenas na fase **DO** (aquisição de ferramentas: SIEM, EDR, DLP), negligenciando **CHECK** e **ACT**. Argumente, com base no PDCA e nas cláusulas 9 e 10 da ISO 27001, por que essa postura compromete a **melhoria contínua** e a **conformidade sustentável**.

### III.B Questões de múltipla escolha (gabarito comentado)

**M1.** No modelo da ISO/IEC 15408 (Common Criteria), o produto ou sistema que é objeto da avaliação de segurança denomina-se:

| Alternativa | Texto |
|-------------|--------|
| A | TOE (Target of Evaluation) |
| B | ST (Security Target) |
| C | PP (Protection Profile) |
| D | SAR (Security Assurance Requirement) |

**Gabarito: A.** Comentário: O **TOE** é o alvo material da avaliação. O **ST** documenta as propriedades de segurança de um TOE específico; o **PP** perfila requisitos para uma classe de produtos; **SAR** refere-se a requisitos de garantia na Parte 3 da norma.

---

**M2.** Segundo o material e a estrutura usual do SGSI ISO 27001, as **auditorias internas** com vistas a verificar a conformidade e a eficácia do SGSI concentram-se primariamente na fase:

| Alternativa | Texto |
|-------------|--------|
| A | PLAN |
| B | DO |
| C | CHECK |
| D | ACT |

**Gabarito: C.** Comentário: A fase **CHECK** compreende monitoramento, medição, análise, avaliação — **incluindo auditoria interna** (cláusula 9.2). **PLAN** define o SGSI; **DO** implementa; **ACT** trata melhorias e ações corretivas com base nos resultados do CHECK.

---

**M3.** A legislação norte-americana associada à reforço de **governança corporativa**, **controles internos** e **mecanismos de auditoria** após os escândalos **Enron** e **WorldCom** é:

| Alternativa | Texto |
|-------------|--------|
| A | HIPAA |
| B | Sarbanes-Oxley Act (SOX) |
| C | GLBA |
| D | FISMA |

**Gabarito: B.** Comentário: A **SOX** (2002) reforça transparência e controles sobre relatórios financeiros e governança. **HIPAA** versa sobre saúde; **GLBA** sobre serviços financeiros e privacidade de consumidores nos EUA; **FISMA** sobre segurança em sistemas federais — não são o foco histórico indicado no material para Enron/WorldCom.

---

*Documento elaborado com base no PDF fornecido e nos conceitos de auditoria de sistemas, ISO 27001, PDCA, ISO/IEC 15408, SOX e gestão de risco abordados na aula.*
 