# MindCard App 📚🧠

O **MindCard** é um aplicativo Android, desenvolvido para ajudar estudantes a memorizarem conteúdos de forma eficiente através de flashcards. O projeto utiliza as melhores práticas de desenvolvimento Android, com uma interface intuitiva e integração com backend.

## 🚀 Funcionalidades

- **Autenticação Completa**: Login e Cadastro de usuários com persistência de sessão (JWT).
- **Gerenciamento de Decks**: Visualização de baralhos de estudo (decks) organizados por categorias.
- **Criação de Flashcards**: Tela dedicada para criar novos decks com múltiplas perguntas e respostas.
- **Modo Prática**: Interface interativa para revisão de conteúdos com sistema de feedback (Certo/Errado).
- **Resultados**: Resumo de desempenho após cada sessão de estudo.

## 🛠 Tecnologias Utilizadas

- **Linguagem**: [Kotlin](https://kotlinlang.org/)
- **UI**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (Interface declarativa e moderna)
- **Arquitetura**: MVVM (Model-View-ViewModel)
- **Navegação**: [Compose Navigation](https://developer.android.com/jetpack/compose/navigation)
- **Rede**: [Retrofit](https://square.github.io/retrofit/) + OkHttp para consumo de API REST.
- **Concorrência**: [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & [Flow](https://kotlinlang.org/docs/flow.html) para operações assíncronas.
- **Persistência Local**: SharedPreferences (via SessionManager) para tokens de acesso.
- **Injeção de Dependência**: Pattern de Factory para ViewModels.

## 📦 Estrutura do Projeto

```text
com.mindcard
├── data
│   ├── local      # Gerenciamento de sessão e cache
│   ├── model      # Modelos de dados (DTOs e Entities)
│   ├── repository # Lógica de acesso a dados (API/Local)
│   └── service    # Definições das APIs Retrofit
├── navigation     # Configuração de rotas e NavGraph
├── ui
│   ├── components # Componentes reutilizáveis (Botões, Inputs, Cards)
│   ├── screens    # Telas do aplicativo (Home, Login, Add, etc)
│   └── theme      # Definições de cores, tipografia e temas
└── viewmodel      # Lógica de negócio e estado da UI
```

## ⚙️ Como Rodar o Projeto

1. Clone o repositório.
2. Certifique-se de ter o **Android Studio Jellyfish** ou superior instalado.
3. No arquivo `build.gradle`, verifique se as configurações de `BASE_URL` da API estão corretas.
4. Sincronize o Gradle e execute o app em um emulador ou dispositivo físico.
---
## Desenvolvido por Rafael Araujo e Filipe Bencio.
---
