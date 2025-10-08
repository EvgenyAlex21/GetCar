# 🚗 GetCar - Android приложение для поиска автомобилей

<div align="center">

![Platform](https://img.shields.io/badge/Platform-Android-green.svg)
![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-purple.svg)
![License](https://img.shields.io/badge/license-MIT-blue.svg)

Современное Android приложение для просмотра, поиска и управления каталогом автомобилей

</div>

---

## 📋 Описание

**GetCar** — это нативное Android приложение для работы с каталогом автомобилей. Приложение предоставляет удобный интерфейс для просмотра списка автомобилей, детальной информации, добавления новых объявлений и управления существующими.

### 🎯 Основные возможности

- ✅ **Просмотр каталога** — список всех доступных автомобилей с изображениями
- 🔍 **Поиск и фильтрация** — поиск по марке, модели, году выпуска и ценовому диапазону
- 📱 **Material Design 3** — современный UI/UX согласно гайдлайнам Google
- 🎨 **Адаптивный интерфейс** — поддержка различных размеров экранов и ориентаций
- 📸 **Работа с изображениями** — загрузка фотографий из галереи или камеры
- 💾 **Локальная база данных** — Room для кэширования и офлайн режима
- 🌐 **REST API интеграция** — Retrofit для работы с сервером
- 🔄 **Синхронизация данных** — автоматическое обновление информации
- 🎯 **MVVM архитектура** — чистая архитектура с разделением слоев
- 🚀 **Kotlin Coroutines** — асинхронные операции без блокировки UI

---

## 🛠 Технологии

### Язык программирования
- **Kotlin 1.9+** — основной язык разработки
- **Java 17** — совместимость

### Android Components
- **Android SDK** — минимум API 24 (Android 7.0), целевой API 34 (Android 14)
- **Jetpack Components:**
  - **Room** — локальная база данных
  - **LiveData** — реактивные данные
  - **ViewModel** — управление состоянием UI
  - **Navigation Component** — навигация между экранами
  - **DataBinding/ViewBinding** — связывание View и данных
  - **WorkManager** — фоновые задачи
  - **Paging 3** — пагинация списков

### Сетевое взаимодействие
- **Retrofit 2** — HTTP клиент для REST API
- **OkHttp** — сетевой слой
- **Gson/Moshi** — JSON сериализация

### UI/UX
- **Material Design 3** — компоненты интерфейса
- **RecyclerView** — оптимизированные списки
- **CardView** — карточки автомобилей
- **ConstraintLayout** — гибкая верстка
- **Glide/Coil** — загрузка изображений

### Dependency Injection
- **Hilt** — внедрение зависимостей на основе Dagger

### Асинхронность
- **Kotlin Coroutines** — асинхронное программирование
- **Flow** — реактивные потоки данных

### Архитектура
- **MVVM** — Model-View-ViewModel паттерн
- **Clean Architecture** — разделение на слои (Data, Domain, Presentation)
- **Repository Pattern** — абстракция источников данных

---

## 🚀 Установка и запуск

### Предварительные требования

- **Android Studio** Hedgehog (2023.1.1) или выше
- **JDK** 17 или выше
- **Android SDK** с API 24-34
- **Gradle** 8.0+
- **Устройство/эмулятор** с Android 7.0 (API 24) или выше

### Шаги установки

1. **Клонируйте репозиторий**
   ```bash
   git clone https://github.com/yourusername/GetCar.git
   cd GetCar
   ```

2. **Откройте проект в Android Studio**
   - Запустите Android Studio
   - Выберите `File → Open`
   - Выберите папку проекта `GetCar`
   - Дождитесь синхронизации Gradle

3. **Настройте API endpoint**
   
   В файле `local.properties` добавьте:
   ```properties
   BASE_URL=https://your-api-server.com/api/
   ```

   Или в `gradle.properties`:
   ```properties
   BASE_URL="https://your-api-server.com/api/"
   ```

4. **Синхронизируйте Gradle**
   ```bash
   ./gradlew build
   ```

5. **Запустите приложение**
   - Подключите Android устройство или запустите эмулятор
   - Нажмите `Run` (Shift+F10) или кнопку ▶️
   - Выберите целевое устройство

---

## 📁 Структура проекта

```
GetCar/
├── 📁 app/
│   ├── 📁 src/
│   │   ├── 📁 main/
│   │   │   ├── 📁 java/com/example/getcar/
│   │   │   │   ├── 📁 data/              # Слой данных
│   │   │   │   │   ├── 📁 local/         # Локальная БД (Room)
│   │   │   │   │   │   ├── CarDao.kt
│   │   │   │   │   │   ├── CarDatabase.kt
│   │   │   │   │   │   └── CarEntity.kt
│   │   │   │   │   ├── 📁 remote/        # Сетевые запросы (Retrofit)
│   │   │   │   │   │   ├── ApiService.kt
│   │   │   │   │   │   ├── CarDto.kt
│   │   │   │   │   │   └── ApiResponse.kt
│   │   │   │   │   └── 📁 repository/    # Репозитории
│   │   │   │   │       └── CarRepository.kt
│   │   │   │   │
│   │   │   │   ├── 📁 domain/            # Бизнес-логика
│   │   │   │   │   ├── 📁 model/         # Доменные модели
│   │   │   │   │   │   └── Car.kt
│   │   │   │   │   └── 📁 usecase/       # Use Cases
│   │   │   │   │       ├── GetCarsUseCase.kt
│   │   │   │   │       ├── GetCarByIdUseCase.kt
│   │   │   │   │       ├── AddCarUseCase.kt
│   │   │   │   │       └── DeleteCarUseCase.kt
│   │   │   │   │
│   │   │   │   ├── 📁 presentation/      # UI слой
│   │   │   │   │   ├── 📁 carlist/       # Экран списка
│   │   │   │   │   │   ├── CarListFragment.kt
│   │   │   │   │   │   ├── CarListViewModel.kt
│   │   │   │   │   │   ├── CarListAdapter.kt
│   │   │   │   │   │   └── CarViewHolder.kt
│   │   │   │   │   ├── 📁 cardetail/     # Детальный экран
│   │   │   │   │   │   ├── CarDetailFragment.kt
│   │   │   │   │   │   └── CarDetailViewModel.kt
│   │   │   │   │   ├── 📁 addcar/        # Добавление авто
│   │   │   │   │   │   ├── AddCarFragment.kt
│   │   │   │   │   │   └── AddCarViewModel.kt
│   │   │   │   │   └── MainActivity.kt
│   │   │   │   │
│   │   │   │   ├── 📁 di/                # Dependency Injection (Hilt)
│   │   │   │   │   ├── AppModule.kt
│   │   │   │   │   ├── DatabaseModule.kt
│   │   │   │   │   └── NetworkModule.kt
│   │   │   │   │
│   │   │   │   └── 📁 util/              # Утилиты
│   │   │   │       ├── Constants.kt
│   │   │   │       ├── Extensions.kt
│   │   │   │       └── ImageUtils.kt
│   │   │   │
│   │   │   ├── 📁 res/
│   │   │   │   ├── 📁 layout/            # XML layouts
│   │   │   │   │   ├── activity_main.xml
│   │   │   │   │   ├── fragment_car_list.xml
│   │   │   │   │   ├── fragment_car_detail.xml
│   │   │   │   │   ├── fragment_add_car.xml
│   │   │   │   │   └── item_car.xml
│   │   │   │   ├── 📁 drawable/          # Изображения и векторы
│   │   │   │   ├── 📁 values/            # Ресурсы
│   │   │   │   │   ├── colors.xml
│   │   │   │   │   ├── strings.xml
│   │   │   │   │   ├── themes.xml
│   │   │   │   │   └── dimens.xml
│   │   │   │   ├── 📁 navigation/        # Navigation graph
│   │   │   │   │   └── nav_graph.xml
│   │   │   │   └── 📁 menu/              # Меню
│   │   │   │
│   │   │   └── AndroidManifest.xml       # Манифест приложения
│   │   │
│   │   └── 📁 test/                      # Unit тесты
│   │       └── 📁 java/com/example/getcar/
│   │
│   ├── build.gradle.kts                  # Gradle конфигурация модуля
│   └── proguard-rules.pro                # ProGuard правила
│
├── 📁 gradle/                            # Gradle wrapper
├── build.gradle.kts                      # Gradle конфигурация проекта
├── settings.gradle.kts                   # Gradle настройки
├── gradle.properties                     # Gradle properties
└── local.properties                      # Локальные настройки
```

---

## 🏗 Архитектура

Приложение построено на **Clean Architecture** с использованием **MVVM** паттерна:

```
┌─────────────────────────────────────────────┐
│         Presentation Layer (UI)             │
│   ┌──────────┐  ┌──────────┐  ┌──────────┐ │
│   │ Activity │  │ Fragment │  │ ViewModel│ │
│   └──────────┘  └──────────┘  └──────────┘ │
└─────────────────────────────────────────────┘
                    ↕️
┌─────────────────────────────────────────────┐
│           Domain Layer (Business)           │
│   ┌──────────┐  ┌──────────┐               │
│   │  Models  │  │ Use Cases│               │
│   └──────────┘  └──────────┘               │
└─────────────────────────────────────────────┘
                    ↕️
┌─────────────────────────────────────────────┐
│            Data Layer (Sources)             │
│   ┌──────────┐  ┌──────────┐  ┌──────────┐ │
│   │Repository│  │Local DB  │  │Remote API│ │
│   └──────────┘  └──────────┘  └──────────┘ │
└─────────────────────────────────────────────┘
```

### Слои приложения

1. **Presentation Layer** — UI компоненты (Activities, Fragments, ViewModels)
2. **Domain Layer** — бизнес-логика (Use Cases, доменные модели)
3. **Data Layer** — работа с данными (Repository, Room, Retrofit)

---

## 📦 Gradle Dependencies

### app/build.gradle.kts (основные зависимости)

```kotlin
dependencies {
    // Core Android
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Lifecycle & ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.6")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.6")

    // Room Database
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    // Retrofit & OkHttp
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Hilt Dependency Injection
    implementation("com.google.dagger:hilt-android:2.48.1")
    kapt("com.google.dagger:hilt-compiler:2.48.1")

    // Image Loading
    implementation("io.coil-kt:coil:2.5.0")

    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
```

---

## 🎨 Ключевые экраны

### 1. Список автомобилей (CarListFragment)
- Отображение всех автомобилей в виде карточек
- RecyclerView с GridLayoutManager
- Pull-to-refresh для обновления данных
- Поиск по названию
- Фильтрация по цене и году
- Переход к детальной информации по клику

### 2. Детальная информация (CarDetailFragment)
- Полная информация об автомобиле
- Большое изображение с возможностью увеличения
- Кнопки редактирования и удаления
- Форматированная информация о характеристиках

### 3. Добавление/Редактирование (AddCarFragment)
- Форма с полями для ввода данных
- Валидация полей в реальном времени
- Выбор изображения из галереи или камеры
- Сохранение в локальную БД и на сервер

---

## 📱 Поддерживаемые экраны

- **Phone** — 5.0" - 6.7"
- **Tablet** — 7" - 10.1"
- **Foldable** — адаптивные layouts
- **Orientations** — Portrait и Landscape

---

## 🔐 Permissions

Приложение использует следующие разрешения:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" 
    android:maxSdkVersion="28" />
<uses-permission android:name="android.permission.CAMERA" />
```

---

## 🚀 API Integration

### Конфигурация Retrofit

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })
                    .build()
            )
            .build()
    }
    
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}
```

### API Endpoints

```kotlin
interface ApiService {
    @GET("cars")
    suspend fun getCars(
        @Query("search") search: String? = null,
        @Query("minPrice") minPrice: Double? = null,
        @Query("maxPrice") maxPrice: Double? = null,
        @Query("pageNumber") pageNumber: Int = 1,
        @Query("pageSize") pageSize: Int = 10
    ): Response<PagedResult<CarDto>>
    
    @GET("cars/{id}")
    suspend fun getCarById(@Path("id") id: Int): Response<CarDto>
    
    @POST("cars")
    suspend fun createCar(@Body car: CarDto): Response<CarDto>
    
    @PUT("cars/{id}")
    suspend fun updateCar(@Path("id") id: Int, @Body car: CarDto): Response<CarDto>
    
    @DELETE("cars/{id}")
    suspend fun deleteCar(@Path("id") id: Int): Response<Unit>
}
```

---

## 💾 Локальная база данных (Room)

### Структура БД

```kotlin
@Entity(tableName = "cars")
data class CarEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "brand") val brand: String,
    @ColumnInfo(name = "model") val model: String,
    @ColumnInfo(name = "year") val year: Int,
    @ColumnInfo(name = "price") val price: Double,
    @ColumnInfo(name = "mileage") val mileage: Int,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "image_url") val imageUrl: String?,
    @ColumnInfo(name = "created_at") val createdAt: Long
)

@Dao
interface CarDao {
    @Query("SELECT * FROM cars ORDER BY created_at DESC")
    fun getAllCars(): Flow<List<CarEntity>>
    
    @Query("SELECT * FROM cars WHERE id = :id")
    suspend fun getCarById(id: Int): CarEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCar(car: CarEntity)
    
    @Delete
    suspend fun deleteCar(car: CarEntity)
    
    @Query("DELETE FROM cars")
    suspend fun deleteAllCars()
}
```

---

## 🧪 Тестирование

### Unit Tests

```bash
./gradlew test
```

### Instrumented Tests

```bash
./gradlew connectedAndroidTest
```

### Пример теста

```kotlin
@Test
fun `test car insertion into database`() = runTest {
    val car = CarEntity(
        id = 1,
        brand = "BMW",
        model = "X5",
        year = 2022,
        price = 55000.0,
        mileage = 15000,
        description = "Test car",
        imageUrl = null,
        createdAt = System.currentTimeMillis()
    )
    
    carDao.insertCar(car)
    val result = carDao.getCarById(1)
    
    assertThat(result).isNotNull()
    assertThat(result?.brand).isEqualTo("BMW")
}
```

---

## 🔨 Сборка APK

### Debug Build

```bash
./gradlew assembleDebug
```

### Release Build

```bash
./gradlew assembleRelease
```

APK файлы будут находиться в:
```
app/build/outputs/apk/debug/
app/build/outputs/apk/release/
```

---

## 👨‍💻 Автор

**Разработчик**: [Евгений Александрин](https://t.me/x_evgenyalex_x)  
**Город**: Чебоксары, Россия  
**Последнее обновление README**: 08 октябрь 2025 г.  

---

</div>