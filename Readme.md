# 📱 EchoPosts - Modern Android Authentication App

<div align="center">
  <img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" />
  <img src="https://img.shields.io/badge/Kotlin-0095D5?style=for-the-badge&logo=kotlin&logoColor=white" />
  <img src="https://img.shields.io/badge/Material%20Design-757575?style=for-the-badge&logo=material-design&logoColor=white" />
  <img src="https://img.shields.io/badge/License-MIT-blue.svg?style=for-the-badge" />
</div>

<div align="center">
  <h3>🚀 EchoPosts – posts that "echo" back when favourited or searched.</h3>
  <p>A modern Android app showcasing Clean Architecture with comprehensive authentication and posts management</p>
</div>

---

## ✨ Features

### 🔐 **Authentication System**
- **Secure Registration** - Email validation, password strength checks
- **User Login** - Local authentication with session management
- **Offline Data Storage** - User credentials stored securely with Room database

### 📖 **Posts Management**
- **Browse Posts** - Fetch posts from JSONPlaceholder API
- **Infinite Scroll** - Seamless pagination with 10 posts per page
- **Search Functionality** - Real-time search through cached posts
- **Favorites System** - Mark and manage favorite posts
- **Offline Support** - Access cached posts without internet

### 🎨 **Modern UI/UX**
- **Material Design 3** - Latest design system implementation
- **Dark Mode Support** - System-aware theme switching
- **Responsive Design** - Optimized for all screen sizes
- **Smooth Animations** - Engaging user interactions
- **Pull-to-Refresh** - Intuitive content updates

### ⚙️ **Settings & Configuration**
- **Theme Management** - Light, Dark, and System themes
- **About Section** - App information and version details
- **User Preferences** - Customizable app settings

---

## 🏗️ Architecture

EchoPosts follows **Clean Architecture** principles with **MVVM (Model-View-ViewModel)** pattern:

```
📁 com.example.echoposts/
├── 🎯 data/
│   ├── local/           # Room Database, DAOs, Entities
│   ├── remote/          # API Services, DTOs
│   ├── mapper/          # Data Transformers
│   └── repository/      # Data Sources Management
├── 🧠 domain/
│   ├── model/           # Domain Models
│   └── usecase/         # Business Logic
├── 🎨 presentation/
│   ├── ui/              # Fragments, Activities
│   ├── viewmodel/       # ViewModels
│   └── adapter/         # RecyclerView Adapters
├── 🔧 di/               # Dependency Injection (Hilt)
└── 🛠️ util/            # Utilities, Extensions
```

### 🔄 **Data Flow**
```
API/Database → Repository → UseCase → ViewModel → UI
     ↑                                            ↓
     └── ← ← ← ← ← User Actions ← ← ← ← ← ← ← ← ← ←
```

---

## 🛠️ Tech Stack & Libraries

### **Core Technologies**
| Technology | Version | Purpose |
|------------|---------|---------|
| **Kotlin** | 2.0.21 | Primary programming language |
| **Android SDK** | API 24-36 | Platform framework |
| **Material Components** | 1.12.0 | UI components |

### **Architecture Components**
| Component | Version | Usage |
|-----------|---------|-------|
| **Room Database** | 2.6.1 | Local data persistence |
| **Navigation Component** | 2.9.3 | Fragment navigation |
| **ViewModel & LiveData** | 2.9.2 | Reactive UI updates |

### **Networking & Data**
| Library | Version | Purpose |
|---------|---------|---------|
| **Retrofit** | 2.11.0 | HTTP client for API calls |
| **OkHttp** | 4.12.0 | Network layer |
| **Gson** | 2.11.0 | JSON serialization |

### **Dependency Injection**
| Library | Version | Usage |
|---------|---------|--------|
| **Hilt** | 2.51.1 | Dependency injection framework |

### **Reactive Programming**
| Library | Version | Purpose |
|---------|---------|---------|
| **Kotlin Coroutines** | 1.8.1 | Asynchronous operations |
| **StateFlow/Flow** | 1.8.1 | Reactive data streams |

### **Testing Libraries**
| Library | Version | Purpose |
|---------|---------|---------|
| **JUnit** | 4.13.2 | Unit testing framework |
| **Mockito** | 5.8.0 | Mocking dependencies |
| **Espresso** | 3.7.0 | UI testing |
| **Turbine** | 1.0.0 | Flow testing |
| **Hilt Testing** | 2.51.1 | DI testing support |

---

## ⚡ Setup & Build Instructions

### **Prerequisites**
- 🟢 **Android Studio** Hedgehog (2023.1.1) or newer
- ☕ **JDK** 11 or higher
- 🤖 **Android SDK** with API levels 24-36
- 📱 **Device/Emulator** running Android 7.0+ (API 24)

### **1. Clone Repository**
```bash
git clone https://github.com/Kayuemkhan/echoposts.git
cd echoposts
```

### **2. Open in Android Studio**
- Launch Android Studio
- Select "Open an existing project"
- Navigate to the cloned directory and select it

### **3. Sync Dependencies**
```bash
# Android Studio will automatically sync Gradle dependencies
# If not, click "Sync Now" in the notification bar
```

### **4. Build the Project**
```bash
# Via Android Studio UI
# Build → Make Project (Ctrl+F9)

# Via Command Line
./gradlew assembleDebug
```

### **5. Run the App**
```bash
# Select your device/emulator
# Click the Run button (▶️) or press Ctrl+R (Cmd+R on Mac)

# Via Command Line
./gradlew installDebug
```

### **6. Run Tests**
```bash
# Unit Tests
./gradlew test

# Instrumented Tests (requires connected device/emulator)
./gradlew connectedAndroidTest

# All Tests
./gradlew check
```

---

## 🔍 Testing

### **🧪 Testing Strategy**
The project implements comprehensive testing at multiple levels:

#### **Unit Tests (src/test/)**
- **ViewModel Tests** - LoginViewModel, RegisterViewModel
- **Use Case Tests** - Validation logic
- **Repository Tests** - Data layer logic
- **Coverage:** ~80% of business logic

#### **UI Tests (src/androidTest/)**
- **Espresso Tests** - LoginFragment, RegisterFragment interactions
- **Navigation Tests** - Fragment transitions
- **Database Tests** - Room integration
- **Coverage:** Key user journeys

### **📊 Current Test Coverage**
```
├── 🧪 Unit Tests
│   ├── LoginViewModelTest.kt ✅ (8 test cases)
│   ├── RegisterViewModelTest.kt ✅ (9 test cases)
│   └── ValidationUseCaseTests.kt ⚠️ (Planned)
│
├── 🤖 UI Tests  
│   ├── LoginFragmentTest.kt ✅ (10 test cases)
│   ├── LoginFragmentSimpleTest.kt ✅ (12 test cases)
│   └── RegisterFragmentTest.kt ⚠️ (Planned)
│
└── 🔧 Integration Tests
    ├── AuthFlowTest.kt ⚠️ (Planned)
    └── DatabaseMigrationTest.kt ⚠️ (Planned)
```

### **🚀 Running Tests**
```bash
# Run all unit tests with coverage
./gradlew testDebugUnitTest --info

# Run UI tests on connected device
./gradlew connectedAndroidTest --info

# Generate test reports
./gradlew test connectedAndroidTest
# Reports: app/build/reports/tests/
```

---

## 🔧 Configuration

### **API Configuration**
```kotlin
object Constants {
    const val BASE_URL = "https://jsonplaceholder.typicode.com/"
    const val PAGE_SIZE = 10
    const val TOTAL_POSTS = 100
}
```

### **Database Setup**
```kotlin
@Database(
    entities = [User::class, Post::class, UserPostCrossRef::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun postDao(): PostDao
}
```

---

## ⚠️ Assumptions & Limitations

### **Current Assumptions**
- **Mock API Usage** - JSONPlaceholder for posts data
- **Local Authentication** - No real backend integration
- **Simple Validation** - Basic email/password validation rules
- **English Only** - No internationalization support
- **Portrait Mode** - Optimized primarily for portrait orientation

### **Known Limitations**
- **No Real Authentication** - Uses local Room database only
- **Limited Offline Support** - Basic caching, no conflict resolution
- **No Push Notifications** - Local app only
- **Basic Error Handling** - Limited retry mechanisms
- **No User Management** - Single user session

### **Performance Considerations**
- **Database Queries** - Optimized with proper indexing
- **Image Loading** - No image optimization (posts are text-only)
- **Memory Usage** - Pagination helps but could be optimized further
- **Network Calls** - Basic retry logic, could be enhanced

---

## 🚀 Future Development

### **🎯 Immediate Roadmap**

#### **1. 100% Testing Coverage**
- **Complete Unit Tests** - All ViewModels, UseCases, Repositories
- **Integration Tests** - End-to-end user flows
- **Performance Tests** - Database and network layer
- **Accessibility Tests** - UI accessibility compliance
- **Screenshot Tests** - Visual regression testing

#### **2. Dark Mode Efficiency Improvement**
- **Theme Caching** - Reduce theme switch delays
- **Dynamic Colors** - Material You color extraction
- **Custom Theme Engine** - More granular theme controls
- **Power Optimization** - OLED-friendly dark themes
- **Smooth Transitions** - Animated theme switching

#### **3. Modular Architecture Implementation**
```
📁 EchoPosts/
├── 🏗️ app/                    # Main app module
├── 🔐 feature-auth/            # Authentication feature
├── 📖 feature-posts/           # Posts management feature
├── ⚙️ feature-settings/        # Settings feature
├── 🧠 core-domain/             # Shared domain models
├── 🎯 core-data/               # Data layer
├── 🎨 core-ui/                 # Shared UI components
└── 🛠️ core-common/             # Utilities & extensions
```

#### **4. Migration to Jetpack Compose**
- **UI Modernization** - Replace XML layouts with Compose
- **State Management** - Compose state and side effects
- **Navigation** - Compose Navigation
- **Theming** - Material 3 Compose theming
- **Testing** - Compose testing utilities
- **Animation** - Advanced Compose animations

### **🔮 Long-term Vision**

#### **Backend Integration**
- **Real Authentication** - Firebase Auth or custom backend
- **Cloud Database** - Firestore or custom API
- **Real-time Updates** - WebSocket or Firebase Realtime Database
- **Push Notifications** - Firebase Cloud Messaging

#### **Advanced Features**
- **Social Features** - Comments, likes, sharing
- **User Profiles** - Customizable user information
- **Content Creation** - Create and edit posts
- **Multimedia Support** - Images, videos in posts
- **Advanced Search** - Full-text search with filters

#### **Performance & Scalability**
- **Caching Strategy** - Multi-layer caching
- **Offline-First** - Comprehensive offline support
- **Performance Monitoring** - Crashlytics, Performance monitoring
- **CI/CD Pipeline** - Automated testing and deployment

---

## 📊 Project Statistics

### **📈 Current Metrics**
- **Languages:** Kotlin (95%), XML (5%)
- **Lines of Code:** ~4,000+
- **Source Files:** 60+ files
- **Test Coverage:** 70%+ (Target: 100%)
- **Build Time:** ~45 seconds (clean build)

---

## 🤝 Contributing

### **Development Workflow**
1. Fork the repository
2. Create a feature branch: `git checkout -b feature/awesome-feature`
3. Make your changes with proper tests
4. Ensure all tests pass: `./gradlew check`
5. Commit changes: `git commit -m "Add awesome feature"`
6. Push to branch: `git push origin feature/awesome-feature`
7. Create a Pull Request

### **Code Standards**
- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Add tests for new features
- Update documentation for API changes
- Maintain architectural patterns

---

## 📄 License

```
MIT License

Copyright (c) 2025 EchoPosts

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

## 📞 Contact & Support

**👤 Developer:** [@Kayuemkhan](https://github.com/Kayuemkhan)  
**📧 Email:** abdulkayuem007@gmail.com  
**🐛 Issues:** [GitHub Issues](https://github.com/Kayuemkhan/echoposts/issues)

---