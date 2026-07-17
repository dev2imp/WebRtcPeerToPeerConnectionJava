# WebRTC Peer-to-Peer Connection (Android/Java) 📞

A native Android application demonstrating real-time peer-to-peer video calling, voice calling, and messaging using **WebRTC**, with **Firebase** used as the signaling and messaging backend.

## 📋 Overview

This app establishes direct peer-to-peer connections between two devices for real-time video/voice communication, handling the full WebRTC signaling flow — SDP offer/answer exchange and ICE candidate negotiation — via Firebase, with STUN servers used to establish connectivity across networks.

## ✨ Features

- 🎥 **Video calling** between two peers
- 🎙️ **Voice calling**
- 💬 **In-app chat/messaging**
- 🔗 **WebRTC signaling** — SDP offer/answer + ICE candidate exchange over Firebase
- 🌐 **STUN server integration** for NAT traversal / connectivity between devices
- 🔔 Push notifications via Firebase Cloud Messaging

## 🏗️ Architecture

Built using **MVVM (Model-View-ViewModel)** architecture:

```
com.example.phoneapp/
├── model/          # Data models + repositories (RTC + Firebase)
│   ├── CandidateModel.java
│   ├── SdpModel.java
│   ├── UserModel.java
│   ├── NotificationModel.java
│   ├── RtcRepository.java
│   └── FirebaseServiceRepository.java
├── viewmodel/
│   ├── RtcViewModel.java
│   └── FirebaseServiceViewModel.java
├── view/
│   ├── main/            # User list / entry screen
│   ├── videocall/       # Video call screen + WebRTC client & peer connection logic
│   ├── voicecall/       # Voice call screen
│   └── message/         # Chat screen
└── utils/
    ├── FirebaseMessagingService.java
    └── Singleton.java
```

Core WebRTC logic lives in `view/videocall/`:
- `RTCClient.java` — manages the WebRTC peer connection
- `PeerConnectionObserver.java` — listens for connection state changes
- `SdpObserver.java` — handles SDP creation/set events
- `SignalListener.java` — listens for incoming signaling data (offers, answers, candidates)

## 🛠️ Tech Stack

- **Java**, Android SDK (min SDK 21, target SDK 32)
- **WebRTC** (`org.webrtc:google-webrtc`)
- **Firebase** — Firestore (signaling data), Cloud Messaging (notifications)
- **MVVM architecture**

## 🚀 Getting Started

1. Clone the repo and open the `WeBrtc SampleApp` folder in Android Studio
2. Add your own `google-services.json` (Firebase project config) into `app/src/`
3. Build and run on two devices/emulators to test peer-to-peer connection

> ⚠️ This is a sample/learning project — the committed `google-services.json` should be replaced with your own Firebase project credentials before use.

## 👤 Author

**Osman Inci**
- GitHub: [@dev2imp](https://github.com/dev2imp)
- LinkedIn: [Osman Inci](https://www.linkedin.com/in/osman-inci-868435221/)
