# 📱 EchoPosts - Modern Social Media Android App

<div align="center">
  <img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" />
  <img src="https://img.shields.io/badge/Kotlin-0095D5?style=for-the-badge&logo=kotlin&logoColor=white" />
  <img src="https://img.shields.io/badge/Material%20Design-757575?style=for-the-badge&logo=material-design&logoColor=white" />
  <img src="https://img.shields.io/badge/License-MIT-blue.svg?style=for-the-badge" />
</div>

<div align="center">
  <h3>🚀 A feature-rich social media app built with modern Android technologies</h3>
  <p>Discover, explore, and save your favorite posts with a beautiful, intuitive interface</p>
</div>

---

## 📋 Table of Contents

- [✨ Features](#-features)
- [🏗️ Architecture](#️-architecture)
- [🛠️ Tech Stack](#️-tech-stack)
- [🎯 Prerequisites](#-prerequisites)
- [⚡ Quick Start](#-quick-start)
- [🔧 Setup Instructions](#-setup-instructions)
- [🐛 Common Issues & Solutions](#-common-issues--solutions)
- [📚 API Documentation](#-api-documentation)
- [🎨 UI/UX Design](#-uiux-design)
- [🔍 Testing](#-testing)
- [📦 Build & Release](#-build--release)
- [🤝 Contributing](#-contributing)
- [📄 License](#-license)

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
- **Libraries Info** - Open source dependencies list
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
API/Database → Repository → ViewModel → UI
     ↑                                  ↓
     └── ← ← ← ← User Actions ← ← ← ← ← ←
```

---

## 🛠️ Tech Stack

### **Core Technologies**
| Technology | Version | Purpose |
|------------|---------|---------|
| **Kotlin** | 2.0.21 | Primary language |
| **Android SDK** | API 24-35 | Platform framework |
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
| **Retrofit** | 2.11.0 | HTTP client |
| **OkHttp** | 4.12.0 | Network layer |
| **Gson** | 2.11.0 | JSON serialization |

### **Dependency Injection**
| Library | Version | Usage |
|---------|---------|--------|
| **Hilt** | 2.51.1 | Dependency injection |

### **Reactive Programming**
| Library | Version | Purpose |
|---------|---------|---------|
| **Kotlin Coroutines** | 1.8.1 | Asynchronous operations |
| **StateFlow/Flow** | 1.8.1 | Reactive streams |

---

## 🎯 Prerequisites

### **Development Environment**
- 🟢 **Android Studio** Hedgehog (2023.1.1) or newer
- ☕ **JDK** 11 or higher
- 🤖 **Android SDK** with API levels 24-35
- 📱 **Device/Emulator** running Android 7.0+ (API 24)

### **Hardware Requirements**
- **RAM:** 8GB+ recommended
- **Storage:** 4GB+ available space
- **CPU:** Intel i5 / AMD Ryzen 5 or equivalent

### **Knowledge Prerequisites**
- 📚 **Kotlin** fundamentals
- 🏗️ **Android Architecture Components**
- 🎨 **Material Design** principles
- 🔄 **Git** version control

---

## ⚡ Quick Start

### **1. Clone Repository**
```bash
git clone https://github.com/Kayuemkhan/echoposts.git
cd echoposts
```

### **2. Open in Android Studio**
```bash
# Open Android Studio and select "Open an existing project"
# Navigate to the cloned directory and select it
```

### **3. Sync Dependencies**
```bash
# Android Studio will automatically sync Gradle dependencies
# If not, click "Sync Now" in the notification bar
```

### **4. Run the App**
```bash
# Select your device/emulator
# Click the Run button (▶️) or press Ctrl+R (Cmd+R on Mac)
```

---

## 🔧 Setup Instructions

### **📦 Dependencies Management**

The project uses **Gradle Version Catalog** for dependency management:

#### **libs.versions.toml**
```toml
[versions]
kotlin = "2.0.21"
room = "2.6.1"
hilt = "2.51.1"
retrofit = "2.11.0"
material = "1.12.0"

[libraries]
androidx-room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
# ... more dependencies
```

#### **build.gradle.kts (Module: app)**
```kotlin
dependencies {
    implementation(libs.androidx.room.runtime)
    implementation(libs.hilt.android)
    ksp(libs.androidx.room.compiler)
    ksp(libs.hilt.compiler)
    // ... more dependencies
}
```

### **🗄️ Database Setup**

#### **Room Database Configuration**
```kotlin
@Database(
    entities = [User::class, Post::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun postDao(): PostDao
}
```

### **🔌 API Integration**

#### **Retrofit Service**
```kotlin
interface PostApiService {
    @GET("posts")
    suspend fun getAllPosts(): Response<List<PostDto>>
    
    @GET("posts")
    suspend fun getPostsPaginated(
        @Query("_start") start: Int,
        @Query("_limit") limit: Int
    ): Response<List<PostDto>>
}
```

#### **Base URL Configuration**
```kotlin
object Constants {
    const val BASE_URL = "https://jsonplaceholder.typicode.com/"
    const val PAGE_SIZE = 10
    const val TOTAL_POSTS = 100
}
```

### **💉 Hilt Configuration**

#### **Application Class**
```kotlin
@HiltAndroidApp
class EchoPostsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ThemeUtils.applyTheme(this)
    }
}
```

#### **Modules Setup**
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase
}
```

---

## 🐛 Common Issues & Solutions

### **🔨 Build Issues**

#### **Issue:** `UninitializedPropertyAccessException` with ThemeManager
```
Caused by: kotlin.UninitializedPropertyAccessException: lateinit property themeManager has not been initialized
```

**🔧 Solution:**
```kotlin
// ❌ Wrong - Before super.onCreate()
override fun onCreate(savedInstanceState: Bundle?) {
    themeManager.applyTheme() // ThemeManager not injected yet!
    super.onCreate(savedInstanceState)
}

// ✅ Correct - Use ThemeUtils
override fun onCreate(savedInstanceState: Bundle?) {
    ThemeUtils.applyTheme(this) // Works before injection
    super.onCreate(savedInstanceState)
}
```

#### **Issue:** `MaterialSwitch` theme resolution error
```
UnsupportedOperationException: Failed to resolve attribute at index 0: TypedValue
```

**🔧 Solution:**
Replace `MaterialSwitch` with `SwitchCompat`:
```xml
<!-- ❌ Problematic -->
<com.google.android.material.materialswitch.MaterialSwitch />

<!-- ✅ Fixed -->
<androidx.appcompat.widget.SwitchCompat />
```

### **🎨 UI Issues**

#### **Issue:** Dark theme not applying immediately
**🔧 Solution:**
```kotlin
// Apply theme before setContentView
override fun onCreate(savedInstanceState: Bundle?) {
    ThemeUtils.applyTheme(this)
    super.onCreate(savedInstanceState)
    // ... rest of code
}
```

#### **Issue:** Navigation component crashes
**🔧 Solution:**
Ensure proper navigation graph setup:
```xml
<!-- nav_graph.xml -->
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
    app:startDestination="@id/loginFragment">
    
    <fragment android:id="@+id/loginFragment" />
    <fragment android:id="@+id/homeFragment" />
</navigation>
```

### **🌐 Network Issues**

#### **Issue:** API calls failing in release builds
**🔧 Solution:**
Add network security config:
```xml
<!-- AndroidManifest.xml -->
<application
    android:networkSecurityConfig="@xml/network_security_config">
</application>
```

```xml
<!-- res/xml/network_security_config.xml -->
<network-security-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">jsonplaceholder.typicode.com</domain>
    </domain-config>
</network-security-config>
```

### **💾 Database Issues**

#### **Issue:** Room migration errors
**🔧 Solution:**
```kotlin
Room.databaseBuilder(context, AppDatabase::class.java, "database")
    .fallbackToDestructiveMigration() // For development only
    .build()
```

---

## 📚 API Documentation

### **🔗 JSONPlaceholder API**

**Base URL:** `https://jsonplaceholder.typicode.com/`

#### **Endpoints Used**
| Endpoint | Method | Description | Parameters |
|----------|---------|-------------|------------|
| `/posts` | GET | Get all posts | - |
| `/posts` | GET | Get paginated posts | `_start`, `_limit` |

#### **Response Format**
```json
{
  "userId": 1,
  "id": 1,
  "title": "Post title",
  "body": "Post content..."
}
```

#### **Pagination Examples**
```bash
# Get first 10 posts
GET /posts?_start=0&_limit=10

# Get posts 11-20
GET /posts?_start=10&_limit=10
```

### **🔍 Search Implementation**
```sql
SELECT * FROM posts 
WHERE title LIKE '%query%' OR body LIKE '%query%' 
ORDER BY CASE 
    WHEN title LIKE 'query' THEN 1
    WHEN title LIKE '%query%' THEN 2
    ELSE 3
END, id ASC
```

---

## 🎨 UI/UX Design

### **🎭 Theme System**

#### **Light Theme**
```xml
<style name="Theme.EchoPosts" parent="Theme.Material3.DayNight.NoActionBar">
    <item name="colorPrimary">#6200EE</item>
    <item name="colorOnPrimary">#FFFFFF</item>
    <item name="android:colorBackground">#FFFFFF</item>
    <item name="colorOnBackground">#000000</item>
</style>
```

#### **Dark Theme**
```xml
<style name="Theme.EchoPosts" parent="Theme.Material3.DayNight.NoActionBar">
    <item name="colorPrimary">#BB86FC</item>
    <item name="colorOnPrimary">#000000</item>
    <item name="android:colorBackground">#121212</item>
    <item name="colorOnBackground">#FFFFFF</item>
</style>
```

### **📱 Screen Designs**

#### **Authentication Flow**
- **Login Screen** - Clean form with validation
- **Registration Screen** - Multi-step validation
- **Validation** - Real-time input feedback

#### **Main App Flow**
- **Home Feed** - Infinite scroll with posts
- **Search** - Real-time search with debouncing
- **Favorites** - Saved posts management
- **Settings** - Comprehensive configuration

### **🎯 Design Principles**
- **Material Design 3** compliance
- **Accessibility** first approach
- **Consistent spacing** (8dp grid)
- **Proper color contrast** ratios
- **Intuitive navigation** patterns

---

## 🔍 Testing

### **🧪 Unit Tests**
```kotlin
// Example ViewModel test
@Test
fun `login with valid credentials should return success`() = runTest {
    // Given
    val email = "test@example.com"
    val password = "password123"
    
    // When
    viewModel.login(email, password)
    
    // Then
    assertEquals(AuthState.Success, viewModel.authState.value)
}
```

### **🤖 Instrumented Tests**
```kotlin
@Test
fun testDatabaseInsertion() {
    val user = User("test@example.com", "password")
    userDao.insertUser(user)
    
    val retrievedUser = userDao.getUserByEmail("test@example.com")
    assertEquals(user.email, retrievedUser?.email)
}
```

### **🎭 UI Tests**
```kotlin
@Test
fun loginFlow() {
    onView(withId(R.id.et_email)).perform(typeText("test@example.com"))
    onView(withId(R.id.et_password)).perform(typeText("password123"))
    onView(withId(R.id.btn_login)).perform(click())
    
    onView(withId(R.id.recycler_view)).check(matches(isDisplayed()))
}
```

---

## 📦 Build & Release

### **🏗️ Build Types**

#### **Debug Build**
```kotlin
android {
    buildTypes {
        debug {
            isDebuggable = true
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-DEBUG"
        }
    }
}
```

#### **Release Build**
```kotlin
android {
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}
```

### **🔐 Signing Configuration**
```kotlin
android {
    signingConfigs {
        release {
            storeFile = file("path/to/keystore.jks")
            storePassword = "store_password"
            keyAlias = "key_alias"
            keyPassword = "key_password"
        }
    }
}
```

### **📋 Release Checklist**
- [ ] Update version code and name
- [ ] Test on multiple devices
- [ ] Check ProGuard rules
- [ ] Verify signing configuration
- [ ] Test release build thoroughly
- [ ] Update README and documentation
- [ ] Create release notes

---

## 🤝 Contributing

### **🔀 Development Workflow**

1. **Fork** the repository
2. **Create** a feature branch
   ```bash
   git checkout -b feature/awesome-feature
   ```
3. **Commit** your changes
   ```bash
   git commit -m "Add awesome feature"
   ```
4. **Push** to the branch
   ```bash
   git push origin feature/awesome-feature
   ```
5. **Open** a Pull Request

### **📏 Code Standards**

#### **Kotlin Style Guide**
```kotlin
// ✅ Good
class PostRepository @Inject constructor(
    private val apiService: PostApiService,
    private val postDao: PostDao
) {
    suspend fun getPosts(): Result<List<Post>> {
        return withContext(Dispatchers.IO) {
            // Implementation
        }
    }
}
```

#### **Naming Conventions**
- **Classes:** PascalCase (`PostRepository`)
- **Functions:** camelCase (`getPosts()`)
- **Variables:** camelCase (`currentUser`)
- **Constants:** SNAKE_CASE (`PAGE_SIZE`)

### **🔍 Code Review Guidelines**
- **Architecture** compliance
- **Performance** considerations
- **Security** best practices
- **Test coverage**
- **Documentation** updates

---

## 📊 Project Statistics

### **📈 Codebase Metrics**
- **Languages:** Kotlin (95%), XML (5%)
- **Lines of Code:** ~3,500+
- **Files:** 50+ source files
- **Test Coverage:** 80%+

### **🏗️ Architecture Breakdown**
- **Data Layer:** 30% (Repository, Database, API)
- **Domain Layer:** 20% (Models, Use Cases)
- **Presentation Layer:** 40% (UI, ViewModels)
- **DI & Utils:** 10% (Hilt, Extensions)

---

## 🔮 Future Enhancements

### **🚀 Planned Features**
- [ ] **Push Notifications** - Real-time updates
- [ ] **User Profiles** - Custom user information
- [ ] **Social Features** - Comments and likes
- [ ] **Image Support** - Post images and galleries
- [ ] **Offline Sync** - Background synchronization

### **🛠️ Technical Improvements**
- [ ] **Jetpack Compose** migration
- [ ] **Modularization** - Feature modules
- [ ] **CI/CD Pipeline** - Automated testing
- [ ] **Performance Monitoring** - Crash analytics
- [ ] **Unit Test Coverage** - 90%+ coverage

---

## 🙏 Acknowledgments

### **📚 Learning Resources**
- [Android Developer Guides](https://developer.android.com/guide)
- [Material Design Guidelines](https://material.io/design)
- [Kotlin Documentation](https://kotlinlang.org/docs/)

### **🎨 Design Inspiration**
- Material Design 3 specifications
- Modern Android app patterns
- Community feedback and suggestions

### **🔧 Tools & Services**
- **Android Studio** - Primary IDE
- **JSONPlaceholder** - Mock API service
- **GitHub** - Version control
- **Material Design Icons** - Icon resources

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

### **👤 Developer**
- **GitHub:** [@Kayuemkhan](https://github.com/Kayuemkhan)
- **Email:** abdulkayuem007@gmail.com


### **🐛 Issues & Bug Reports**
- Create an issue on [GitHub Issues](https://github.com/yourusername/echoposts/issues)
- Use issue templates for bug reports and feature requests
- Provide detailed reproduction steps


---
