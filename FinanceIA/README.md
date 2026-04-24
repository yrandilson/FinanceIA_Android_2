# 📱 FinanceIA — App Android de Finanças Pessoais com IA

Aplicativo Android nativo com Kotlin + Jetpack Compose, banco de dados local Room, e backend Flask com Claude AI.

---

## 📂 Estrutura do projeto

```
FinanceIA/
├── app/src/main/java/com/financeia/
│   ├── MainActivity.kt              ← Ponto de entrada
│   ├── FinanceApp.kt                ← Application class + DI
│   ├── data/
│   │   ├── db/
│   │   │   ├── AppDatabase.kt       ← Room database
│   │   │   ├── entities/Entities.kt ← Transaction, Goal, Category
│   │   │   └── dao/                 ← TransactionDao, GoalDao
│   │   ├── api/ApiService.kt        ← Retrofit + modelos
│   │   └── repository/Repositories.kt
│   └── ui/
│       ├── theme/                   ← Colors, Type, Theme
│       ├── navigation/NavGraph.kt
│       └── screens/
│           ├── dashboard/           ← Tela inicial com cards e IA
│           ├── transactions/        ← Lista + formulário
│           ├── goals/               ← Metas financeiras
│           └── chat/                ← Chat com Claude AI
└── backend/
    ├── server.py                    ← Flask + Anthropic
    └── requirements.txt
```

---

## 🚀 Como rodar

### 1. Backend (Python)

```bash
cd backend
pip install -r requirements.txt

# Configure sua chave da API Anthropic
export ANTHROPIC_API_KEY="sk-ant-..."

python server.py
# Roda em http://localhost:5000
```

### 2. App Android

**Pré-requisitos:**
- [Android Studio Hedgehog](https://developer.android.com/studio) ou superior
- JDK 17
- Android SDK 34

**Passos:**
1. Abra o Android Studio
2. `File → Open` → selecione a pasta `FinanceIA/`
3. Aguarde o Gradle sincronizar (~3 min primeira vez)
4. Conecte emulador Android (API 26+) ou celular físico
5. Clique em ▶️ Run

**Nota:** O emulador acessa o backend via `http://10.0.2.2:5000/` (que é o localhost da sua máquina). Se usar celular físico, mude o `BASE_URL` no `app/build.gradle` para o IP da sua máquina na rede local.

---

## ✨ Funcionalidades

| Tela | Funcionalidades |
|------|----------------|
| **Dashboard** | Saldo do mês, receitas/despesas, gráfico por categoria, insight IA, últimas transações, navegação por mês |
| **Transações** | Lista completa, busca, filtros (todos/receitas/despesas), swipe para deletar, editar |
| **Metas** | Criar metas com emoji, barra de progresso, depositar valor, marcar como concluída |
| **IA Chat** | Chat com Claude AI com contexto financeiro real, sugestões iniciais, histórico |

---

## 🛠 Stack técnica

**Android:**
- Kotlin + Coroutines
- Jetpack Compose + Material 3
- Room (SQLite local)
- Retrofit (HTTP)
- ViewModel + StateFlow (MVVM)
- Navigation Compose

**Backend:**
- Python + Flask
- Anthropic Claude API

---

## 🔮 Próximos passos (Roadmap)

- [ ] Importação de extrato PDF via câmera/galeria
- [ ] Gráficos de linha com histórico mensal (Vico)
- [ ] Notificações de metas e alertas de gastos
- [ ] Autenticação (login/senha ou Google)
- [ ] Sincronização na nuvem (Firebase)
- [ ] Widget na tela inicial
- [ ] **Kotlin Multiplatform** → porta para iOS

---

## 📖 Aprendizado — o que este projeto ensina

1. **Room** — CRUD com Flow reativo (como SQLite mas melhor)
2. **Retrofit** — chamadas HTTP tipadas (igual `requests` do Python)
3. **ViewModel + StateFlow** — estado reativo que sobrevive a rotações
4. **Jetpack Compose** — UI declarativa (parecido com React)
5. **Coroutines** — async/await do Kotlin
6. **MVVM** — separação limpa entre UI e lógica
