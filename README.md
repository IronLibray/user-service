# 👥 User Service - Iron Library

> Microservicio de gestión de usuarios y membresías para el sistema Iron Library

## 🎯 Descripción

Este microservicio forma parte de la arquitectura distribuida de Iron Library y se encarga de **gestionar usuarios del sistema, tipos de membresías, validaciones de préstamos y estadísticas de usuarios**. Controla quién puede acceder al sistema y establece los límites de préstamo según el tipo de membresía.

## 🚀 Características

- ✅ **CRUD completo** de usuarios con validaciones
- ✅ **Sistema de membresías** con diferentes niveles y privilegios
- ✅ **Validación de usuarios** para operaciones de préstamo
- ✅ **Gestión de estado** (activo/inactivo) de usuarios
- ✅ **Búsquedas avanzadas** por nombre, email, membresía y estado
- ✅ **Estadísticas de usuarios** por tipo de membresía
- ✅ **Validaciones de negocio** (email único, límites de préstamo)
- ✅ **API REST** documentada con filtros específicos
- ✅ **Integración con Loan Service** para validaciones

## 🛠️ Stack Tecnológico

- **Spring Boot** 3.4.6
- **Spring Data JPA** - Persistencia de datos
- **Spring Web** - API REST
- **Spring Cloud Netflix Eureka Client** - Service Discovery
- **MySQL** - Base de datos relacional
- **Bean Validation** - Validaciones de entrada
- **Lombok** - Reducción de código boilerplate
- **JUnit 5** - Testing unitario
- **Mockito** - Mocking para tests

## 📡 Endpoints Principales

### Base URL: `http://localhost:8082/api/users`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/users` | Obtener todos los usuarios |
| GET | `/api/users/{id}` | Obtener usuario por ID |
| GET | `/api/users/email/{email}` | Obtener usuario por email |
| GET | `/api/users/active` | Obtener solo usuarios activos |
| GET | `/api/users/inactive` | Obtener usuarios inactivos |
| GET | `/api/users/can-borrow` | Usuarios que pueden pedir prestado |
| GET | `/api/users/membership?type=PREMIUM` | Filtrar por tipo de membresía |
| GET | `/api/users/search/name?name=Juan` | Buscar por nombre |
| GET | `/api/users/{id}/validate` | Validar si puede pedir prestado |
| GET | `/api/users/stats` | Estadísticas de usuarios |
| POST | `/api/users` | Crear nuevo usuario |
| PUT | `/api/users/{id}` | Actualizar usuario completo |
| PATCH | `/api/users/{id}/toggle-status` | Activar/Desactivar usuario |
| PATCH | `/api/users/{id}/membership?type=BASIC` | Cambiar tipo de membresía |
| DELETE | `/api/users/{id}` | Eliminar usuario |
| GET | `/api/users/health` | Health check del servicio |

## 📊 Modelo de Datos

### Entidad Principal: User
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(unique = true, nullable = false, length = 150)
    private String email;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "membership_type", nullable = false)
    private MembershipType membershipType;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
    
    @Column(name = "registration_date")
    private LocalDate registrationDate;
    
    @Column(name = "phone", length = 20)
    private String phone;
    
    @Column(name = "address", length = 255)
    private String address;
    
    // Métodos de negocio
    public boolean canBorrowBooks() {
        return isActive != null && isActive && membershipType != null;
    }
    
    public int getMaxBooksAllowed() {
        if (membershipType == null) return 0;
        return switch (membershipType) {
            case BASIC -> 3;
            case PREMIUM -> 10;
            case STUDENT -> 5;
        };
    }
}
```

### Enum MembershipType
```java
public enum MembershipType {
    BASIC("Básica", 3, 14),      // 3 libros, 14 días
    PREMIUM("Premium", 10, 30),   // 10 libros, 30 días
    STUDENT("Estudiante", 5, 21); // 5 libros, 21 días
    
    private final String displayName;
    private final int maxBooks;
    private final int loanDurationDays;
    
    public String getDisplayName() { return displayName; }
    public int getMaxBooks() { return maxBooks; }
    public int getLoanDurationDays() { return loanDurationDays; }
}
```

## 🔧 Configuración

### Variables de Entorno
```properties
# Base de datos
spring.datasource.url=jdbc:mysql://localhost:3306/user_service
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=update

# Puerto del servicio
server.port=8082

# Eureka
eureka.client.serviceUrl.defaultZone=http://localhost:8761/eureka/
spring.application.name=user-service
```

### Configuración de Base de Datos
```sql
CREATE DATABASE user_service;
USE user_service;

-- La tabla se crea automáticamente por JPA
-- Estructura resultante:
-- users (id, name, email, membership_type, is_active, registration_date, phone, address)
```

## 🚀 Instalación y Ejecución

### Prerrequisitos
- Java 21
- Maven 3.6+
- MySQL 8.0+
- Discovery Server ejecutándose en puerto 8761

### Pasos de Instalación
```bash
# Clonar el repositorio
git clone https://github.com/IronLibray/user-service.git
cd user-service

# Configurar base de datos
mysql -u root -p -e "CREATE DATABASE user_service;"

# Instalar dependencias
./mvnw clean install

# Ejecutar el servicio
./mvnw spring-boot:run
```

### Verificar Instalación
```bash
# Health check
curl http://localhost:8082/api/users/health

# Verificar registro en Eureka
# Ir a http://localhost:8761 y verificar que aparece USER-SERVICE
```

## 🧪 Testing

### Ejecutar Tests
```bash
# Todos los tests
./mvnw test

# Solo tests unitarios
./mvnw test -Dtest="*Test"

# Solo tests de integración
./mvnw test -Dtest="*IntegrationTest"
```

### Cobertura de Tests
- ✅ **UserController** - Tests con MockMvc (@WebMvcTest)
- ✅ **UserService** - Tests unitarios con @Mock
- ✅ **UserRepository** - Tests de integración con @DataJpaTest
- ✅ **Validaciones** - Tests de Bean Validation
- ✅ **Membresías** - Tests de lógica de negocio

## 🔗 Comunicación con Otros Servicios

### Servicios que consumen User Service
- **Loan Service** - Para validar usuarios y obtener límites de préstamo
- **Gateway Service** - Para enrutamiento de peticiones

### APIs expuestas para otros servicios
```java
// Validar usuario para préstamos (usado por Loan Service)
GET /api/users/{id}/validate → Boolean

// Obtener datos completos del usuario
GET /api/users/{id} → User
```

## 📈 Lógica de Negocio

### Reglas de Membresía
| Tipo | Límite Libros | Duración Préstamo | Descripción |
|------|---------------|-------------------|-------------|
| **BASIC** | 3 libros | 14 días | Membresía básica gratuita |
| **PREMIUM** | 10 libros | 30 días | Membresía premium con beneficios |
| **STUDENT** | 5 libros | 21 días | Membresía especial para estudiantes |

### Validaciones de Préstamo
- Usuario debe estar **activo** (`isActive = true`)
- Usuario debe tener **membresía asignada**
- Usuario no debe exceder **límite de libros** según membresía
- **Email** debe ser único en el sistema

### Flujo de Validación (integración)
1. **Loan Service** consulta: `GET /users/{id}/validate`
2. **User Service** verifica estado activo y membresía
3. **Loan Service** consulta: `GET /users/{id}` para límites
4. **Loan Service** verifica préstamos actuales vs límite

## 📚 Documentación API

### Crear Usuario
```bash
curl -X POST http://localhost:8082/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Juan Pérez",
    "email": "juan.perez@email.com",
    "membershipType": "PREMIUM",
    "isActive": true,
    "phone": "123456789",
    "address": "Calle Mayor 123"
  }'
```

### Respuesta Exitosa
```json
{
  "id": 1,
  "name": "Juan Pérez",
  "email": "juan.perez@email.com",
  "membershipType": "PREMIUM",
  "isActive": true,
  "registrationDate": "2025-01-27",
  "phone": "123456789",
  "address": "Calle Mayor 123"
}
```

### Validar Usuario para Préstamo
```bash
curl http://localhost:8082/api/users/1/validate
# Respuesta: true/false
```

### Obtener Estadísticas
```bash
curl http://localhost:8082/api/users/stats
```

### Respuesta de Estadísticas
```json
{
  "totalUsers": 150,
  "activeUsers": 120,
  "basicUsers": 60,
  "premiumUsers": 50,
  "studentUsers": 40
}
```

### Cambiar Membresía
```bash
curl -X PATCH "http://localhost:8082/api/users/1/membership?type=STUDENT"
```

## 🔒 Validaciones

### Validaciones de Entrada
- **Name**: No vacío, máximo 100 caracteres
- **Email**: Formato email válido, único, máximo 150 caracteres
- **MembershipType**: Debe ser BASIC, PREMIUM o STUDENT
- **Phone**: Opcional, máximo 20 caracteres
- **Address**: Opcional, máximo 255 caracteres

### Manejo de Errores
- **400 Bad Request**: Datos de entrada inválidos
- **404 Not Found**: Usuario no encontrado
- **409 Conflict**: Email duplicado
- **500 Internal Server Error**: Error del servidor

## 🚀 Próximas Mejoras

- [ ] **Autenticación** - Sistema de login con JWT
- [ ] **Roles y permisos** - Administradores vs usuarios
- [ ] **Historial de membresías** - Tracking de cambios
- [ ] **Notificaciones** - Alertas por email/SMS
- [ ] **Foto de perfil** - Upload y gestión de imágenes
