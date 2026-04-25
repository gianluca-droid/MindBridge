# 🧠 MindBridge

**Il ponte tra mente e benessere** — App Android che unisce il mondo del paziente e dello psicoterapeuta.

![Kotlin](https://img.shields.io/badge/Kotlin-2.0.20-purple?logo=kotlin)
![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-Material3-green?logo=android)
![API](https://img.shields.io/badge/API-26%2B-brightgreen)
![License](https://img.shields.io/badge/License-MIT-blue)

---

## 📱 Panoramica

MindBridge è una piattaforma digitale che facilita la comunicazione e il percorso terapeutico tra paziente e psicoterapeuta. Due interfacce distinte, un unico ecosistema.

### 🙋 Lato Paziente
- **Dashboard** con panoramica completa
- **Diario dell'Umore** con grafici e storico
- **Esercizi Terapeutici** assegnati dal terapeuta
- **Prenotazione Sedute** con calendario interattivo
- **Chat Sicura** con il proprio terapeuta

### 🧑‍⚕️ Lato Psicoterapeuta
- **Dashboard** con appuntamenti e statistiche
- **Gestione Pazienti** con profili dettagliati
- **Note di Sessione** private e strutturate
- **Assegnazione Esercizi** con template rapidi
- **Chat Sicura** con i pazienti

---

## 🛠 Stack Tecnologico

| Componente | Tecnologia |
|------------|------------|
| Linguaggio | Kotlin |
| UI | Jetpack Compose + Material Design 3 |
| Architettura | MVVM + Clean Architecture |
| Navigazione | Jetpack Navigation Compose |
| Async | Kotlin Coroutines + Flow |
| Min SDK | API 26 (Android 8.0) |
| Target SDK | API 35 (Android 15) |

---

## 🚀 Getting Started

### Prerequisiti
- Android Studio Hedgehog (2023.1.1) o superiore
- JDK 17
- Android SDK API 35

### Installazione

```bash
git clone https://github.com/YOUR_USERNAME/MindBridge.git
cd MindBridge
```

1. Apri il progetto in **Android Studio**
2. Sincronizza Gradle
3. Esegui su emulatore o dispositivo fisico

### Account Demo
- **Paziente**: `sofia.conti@email.it` (password qualsiasi)
- **Terapeuta**: `elena.rossi@mindbridge.it` (password qualsiasi)

---

## 📁 Struttura del Progetto

```
app/src/main/java/com/mindbridge/app/
├── data/
│   ├── model/          # Modelli dati (User, Appointment, MoodEntry...)
│   └── repository/     # Repository mock (pronto per Firebase)
├── ui/
│   ├── auth/           # Login e Registrazione
│   ├── chat/           # Chat in tempo reale
│   ├── components/     # Componenti UI condivisi
│   ├── navigation/     # Rotte e NavGraph
│   ├── patient/        # Schermate paziente
│   ├── therapist/      # Schermate terapeuta
│   └── theme/          # Design system Material3
├── MainActivity.kt
└── MindBridgeApp.kt
```

---

## 🗺 Roadmap

- [x] Autenticazione (login/registrazione)
- [x] Dashboard paziente e terapeuta
- [x] Diario dell'umore con grafici
- [x] Esercizi terapeutici
- [x] Prenotazione appuntamenti
- [x] Chat tra paziente e terapeuta
- [x] Note di sessione per il terapeuta
- [ ] Integrazione Firebase (Auth + Firestore)
- [ ] Notifiche push
- [ ] Videochiamate
- [ ] Crittografia end-to-end
- [ ] Multi-lingua

---

## 📄 Licenza

Questo progetto è distribuito sotto licenza MIT. Vedi il file [LICENSE](LICENSE) per maggiori dettagli.

---

## 🤝 Contribuire

1. Fork il progetto
2. Crea un branch (`git checkout -b feature/nuova-feature`)
3. Commit (`git commit -m 'Aggiunta nuova feature'`)
4. Push (`git push origin feature/nuova-feature`)
5. Apri una Pull Request

---

<p align="center">
  Fatto con ❤️ per il benessere mentale
</p>
