# BookShelfs - Spring Boot Backend API

## 📖 О проекте

BookShelfs — это backend-приложение (REST API) для управления личной электронной библиотекой. Пользователи (читатели) могут создавать виртуальные книжные полки, загружать книги, отслеживать прогресс чтения, а также запрашивать и делиться книгами с другими пользователями.
Также есть возможность потыкаться в шаблоне регистрации - пройти капчу.

## 🚀 Основные возможности

*   **Аутентификация и авторизация** (JWT)
*   **Управление профилем пользователя (читателя)**
*   **CRUD операции для книжных полок**
*   **Загрузка и управление электронными книгами**
*   **Отслеживание прогресса чтения (последняя прочитанная страница)**
*   **Система запросов - обмен книг между пользователями**
*   **Система уведомлений для обработки запросов**
*   **Документирование API с помощью Swagger/OpenAPI**

## 🛠 Технологический стек

*   **Java 17+**
*   **Spring Boot 3.x**
*   **Spring Security + JWT**
*   **Spring Mail**
*   **Spring Data JPA**
*   **Spring thymeleaf**
*   **Lombok**
*   **Mapstruct**
*   **Swagger/OpenAPI 3**
*   **Maven**


## 🚀 Запуск приложения

1.  **Клонируйте репозиторий:**
    ```bash
    git clone https://github.com/ChipchiIinka/bookshelfs
    cd bookshelfs
    ```

2.  **Настройте переменные окружения:**
    Перед запуском необходимо задать следующие переменные окружения:

    ```bash
    # База данных PostgreSQL
    export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/bookshelfs_db
    export SPRING_DATASOURCE_USERNAME=your_db_user
    export SPRING_DATASOURCE_PASSWORD=your_db_password

    # Почта для отправки писем (Yandex)
    export MAIL_USERNAME=your-email@yandex.ru
    export MAIL_PASSWORD=your-app-mail-password

    # JWT для безопасности
    export JWT_SECRET=your-super-secure-random-jwt-secret-key-here

    # hCaptcha для регистрации
    export RECAPTCHA_SECRET=your-hcaptcha-secret-key
    ```

3.  **Соберите и запустите приложение:**
    ```bash
    # Используя Maven Wrapper
    ./mvnw spring-boot:run

    # Или соберите JAR и запустите
    ./mvnw clean package
    java -jar target/bookshelfs-0.0.1-SNAPSHOT.jar
    ```

4.  **Приложение будет доступно по адресу:** `http://127.0.0.1:8080`

## 📚 Документация API (Swagger UI)

После запуска приложения интерактивная документация API доступна по адресу:
**http://127.0.0.1:8080/swagger-ui.html**

Здесь вы можете просмотреть все энд-поинты, их параметры, модели запросов/ответов и протестировать их напрямую.

Есть шаблоны для формы регистрации:
**http://127.0.0.1:8080/auth/login**
**http://127.0.0.1:8080/auth/register**

Для входа в систему зарегистрируйте пользователя по ссылке выше, авторизироваться и получить jwt токен можно уже в swagger

---

## 🔐 API Endpoints (Обзор)

### 1. Аутентификация (`AuthController`)

| Метод | Endpoint | Описание | Требуется Auth |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/register` | Регистрация нового пользователя | Нет |
| `GET` | `/api/auth/verify-email?token={token}` | Подтверждение email | Нет |
| `POST` | `/api/auth/login` | Вход в систему (получение JWT токена) | Нет |
| `POST` | `/api/auth/logout` | Выход из системы | Да (JWT) |

### 2. Читатели (`ReaderController`)

| Метод | Endpoint | Описание | Требуется Auth |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/readers` | Получить список всех читателей | Да (JWT) |
| `GET` | `/api/readers/{readerId}/bookshelfs` | Получить данные конкретного читателя | Да (JWT) |
| `PUT` | `/api/readers/{readerId}/bookshelfs` | Обновить данные читателя | Да (JWT) |

### 3. Книжные полки (`BookshelfController`)

*Все endpoints требуют JWT токен и проверку прав доступа (`@CheckUserPermission`).*

| Метод | Endpoint | Описание |Требуется Auth |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/readers/{readerId}/bookshelfs?bookshelfTitle={title}` | Создать новую полку | Да (JWT) |
| `GET` | `/api/readers/{readerId}/bookshelfs/{bookshelfId}/books` | Получить данные полки и список книг на ней | Да (JWT) |
| `PATCH`| `/api/readers/{readerId}/bookshelfs/{bookshelfId}/books?bookshelfNewTitle={title}` | Изменить название полки | Да (JWT) |
| `DELETE`| `/api/readers/{readerId}/bookshelfs/{bookshelfId}/books` | Удалить полку | Да (JWT) |

### 4. Книги (`BookController`)

*Endpoints на изменение/удаление требуют проверку прав (`@CheckUserPermission`).*

| Метод | Endpoint | Описание | Требуется Auth |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/readers/{readerId}/bookshelfs/{bookshelfId}/books` | Загрузить книгу (multipart/form-data) | Да (JWT) |
| `GET` | `/api/readers/{readerId}/bookshelfs/{bookshelfId}/books/{bookId}` | Получить информацию о книге | Да (JWT) |
| `GET` | `/api/readers/{readerId}/bookshelfs/{bookshelfId}/books/{bookId}/read` | Прочитать книгу (получить контент) | Да (JWT) |
| `POST` | `/api/readers/{readerId}/bookshelfs/{bookshelfId}/books/{bookId}/read?lastReadPage={page}` | Обновить прогресс чтения | Да (JWT) |
| `PATCH`| `/api/readers/{readerId}/bookshelfs/{bookshelfId}/books/{bookId}` | Изменить данные книги (например, описание) | Да (JWT) |
| `DELETE`| `/api/readers/{readerId}/bookshelfs/{bookshelfId}/books/{bookId}` | Удалить книгу | Да (JWT) |

### 5. Общий доступ к книгам (`BookShareController`)

| Метод | Endpoint | Описание | Требуется Auth |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/readers/{readerId}/bookshelfs/{bookshelfId}/books/{bookId}/request` | Отправить запрос на доступ к книге | Да (JWT) |
| `GET` | `/api/readers/{readerId}` | Посмотреть все свои уведомления | Да (JWT) |
| `POST` | `/api/readers/{readerId}/notifications/{notificationId}` | Ответить на запрос (`status`, `rejectionReason`) | Да (JWT) |


