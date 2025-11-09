Необходимо разработать пример проекта автотестов для сервиса petstore, доступного по ссылке: https://petstore.swagger.io.

Проект должен включать несколько функциональных тестов, проверяющих работу методов API. Важно, чтобы методы были разнообразными: простыми и с множеством параметров.
В будущем проект может содержать несколько сотен тестов и даже больше.

Стек на выбор:

* Python3, Pytest (https://github.com/pytest-dev/pytest) и requests (https://github.com/psf/requests)
* Java или Kotlin, Junit (https://junit.org/junit5/)

Требования к проекту:

* Разместите проект на GitHub (https://github.com/) и сделайте его доступным для просмотра.
* Проект должен запускаться локально.
* Включите тесты для методов GET, POST, PUT и DELETE.
* Можно расширить стек, но обязательный набор должен быть в каждом варианте.

Выбранный стек:
- Java
- Junit5
- RestAssured
- Hamcrest
- Awaitility
- Gradle

Структура проекта:
- "BaseClass.java" - базовый класс с настройкой RestAssured
- "PetTests.java" - автотесты для /pet
- "StoreTests.java" - автотесты для /store
- "UsersTests.java" - автотесты для /user

./gradlew test --tests "com.auto.tests.PetTests"   - запуск отдельного теста
./gradlew test  - запуск всех тестов 