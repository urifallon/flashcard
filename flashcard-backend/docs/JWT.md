############################################################################
#                                                                          #
#      TÀI LIỆU PHÂN TÍCH CHUYÊN SÂU VỀ LUỒNG HOẠT ĐỘNG CỦA JWT             #
#                  TRONG DỰ ÁN SPRING BOOT NÀY                             #
#                                                                          #
############################################################################


Đây là một hướng dẫn giới thiệu cách thích hợp JWT vào một dự án đăng nhập và đăng ký người dùng . Trong hướng dẫn step by step bao gồm các hàm và hướng dẫn lý giải đi kèm. Hướng dẫn này chưa bao gồm kết nối database.
- [Part 1: Setup manual mvc project](#part-1-setup-manual-mvc-project)
- [Part 2: Setup JWT(findByEmail or findByUsername)](#part-2-setup-jwtfindbyemail-or-findbyusername)

-----------------------------
## Part 1: Setup manual mvc project
Phần này thiết lập các phần cơ bản liên quan đến một đối tượng (user) bao gồm @Entity(model), @Repository, @Service, @Dto, @Controller và @Config.

### Step 1: Bạn cần tạo một model cho user (User.java)
Đại diện cho dữ liệu trong ứng dụng, thường ánh xạ với bảng trong database.<br>
Chứa các thuộc tính (fields) và annotation JPA.<br>
Là “hình dáng” của dữ liệu trong hệ thống.


``` java
@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //id user

    @Column(nullable = false)
    private String username; // username user

    @Column(nullable = false)
    private String email; //email user

    @Column(nullable = false)
    private String password; // password user

    @Column(nullable = false, unique = true)
    private LocalDateTime createdDate; // ngày tạo

    @Column(nullable = false)
    private LocalDateTime updatedDate; // ngày update
}
```
- @Data cho phép lombok tự tạo @Getter, @Setter, @ToString, @EqualsAndHashCode, @RequiredArgsConstructor cho các biến field
- @Entity đây là cách đánh dấu trong một model để giúp spring boot có thể phát hiện
- @Table đây là một bảng được khởi tạo trong db với tên là users tương ứng cho đối tượng User
- @Id đây là đánh dấu key chính cho một bảng ở đây là id của User
- @GeneratedValue cho phép tự động khởi tạo id cho user một cách tự động
- @Column tương ứng với các cột sẽ được định nghĩa trong bảng users (đi kèm với các mở rộng như nullable không thể null, unique không thể cập nhật,...etc)

### Step 2: Tạo interface repository cho user (UserRepository.java)
Là tầng DAO (Data Access Object).<br>
Chịu trách nhiệm làm việc với database (CRUD).<br>
Thường extends JpaRepository hoặc CrudRepository.

``` Java
@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUsername(String username);
}
```

- Repository như một cánh cửa thao tác với db, nó chứa các hàm query cơ bản nhưng không chứa các logic
- @Repository đây là cách đánh dấu trong một repository để giúp spring boot có thể phát hiện
- Trong Spring Boot, JpaRepository đã cung cấp sẵn các hàm CRUD cơ bản (findAll, findById, save, delete...).
- `Optional<User> findByUsername(String username);`
    - Đây là custom query method.
    - Spring Data JPA tự động hiểu bạn muốn tìm User theo cột username trong bảng users.
    - Nó sẽ generate câu SQL như: SELECT * FROM users WHERE username = ?;

### Step 3: Tạo DTO cho phương thức đăng ký
DTO là một object dùng để truyền dữ liệu giữa các lớp hoặc các tầng (layer) trong ứng dụng, đặc biệt giữa client ↔ server.<br>
DTO chỉ chứa dữ liệu, không chứa logic nghiệp vụ.<br>
Nó thường được dùng để ẩn bớt thông tin nhạy cảm (như password), hoặc chuyển đổi dữ liệu từ entity sang dạng mà client dễ hiểu.

``` Java
@Data
public class RegisterDto {
    private String username;
    private String email;
    private String password;
}
```
- @Data cho phép lombok tự tạo @Getter, @Setter, @ToString, @EqualsAndHashCode, @RequiredArgsConstructor cho các biến field
- DTO là “gói dữ liệu trung gian” dùng để truyền giữa client và server, chỉ mang những thông tin cần thiết thay vì toàn bộ dữ liệu gốc trong Entity.

### Step 4: Tạo class service cho user (UserService.java)
Service là lớp trung gian giữa Controller và Repository.<br>
Nó chứa business logic (luật nghiệp vụ) của ứng dụng.

``` Java
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User getUserByUsername(String username) throws Exception {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new Exception("User Not Found"));
    }

    public User registerUser(RegisterDto registerDto) throws Exception {
        userRepository.findByUsername(registerDto.getUsername()).ifPresent(user -> {
            throw new IllegalStateException(registerDto.getUsername() + "Username Already Exists");
        });

        User user = new User();
        user.setUsername(registerDto.getUsername());
        user.setPassword(registerDto.getPassword());
        user.setEmail(registerDto.getEmail());
		user.setCreatedDate(LocalDateTime.now());
        user.setUpdatedDate(LocalDateTime.now());
        return userRepository.save(user);
    }
}
```

- @Service đây là cách đánh dấu trong một service để giúp spring boot có thể phát hiện
- @RequiredArgsConstructor sẽ tự động sinh ra constructor cho tất cả các trường final hoặc có @NonNull trong class.
- ở đây có một hàm ví dụ đó là `getUserByUsername` nó thực hiện một logic nghiệp vụ đó là lấy user thông qua UserRepository, Service giống như một một người đầu bếp và Repository giống như một người lấy nguyên liệu từ kho.
- Service không chỉ gọi repository mà còn có thể kiểm tra điều kiện, xử lý logic, validate dữ liệu.

### Step 5: Tạo class controller cho phương thức đăng nhập (AuthController.java)
Controller trong Spring Boot là nơi tiếp nhận request từ client (frontend, app, Postman...) và trả response lại.<br>
Lắng nghe request từ client (GET, POST, PUT, DELETE...).<br>
Nhận dữ liệu từ client (qua @RequestBody, @PathVariable, @RequestParam...).<br>
Gọi Service để xử lý logic nghiệp vụ.<br>
Trả dữ liệu (Response) về cho client (thường dưới dạng JSON) qua DTO response.

``` Java
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDto registerDto){
        try{
            User newUser = userService.registerUser(registerDto);
            newUser.setPassword(null);
            return new ResponseEntity<>(newUser, HttpStatus.CREATED);

        } catch (IllegalAccessException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
```

- @RestController là một annotation trong Spring Boot, dùng để đánh dấu một class là Controller chuyên trả về dữ liệu (JSON/XML) thay vì trả về view (HTML).
- @RequiredArgsConstructor sẽ tự động sinh ra constructor cho tất cả các trường final hoặc có @NonNull trong class.
- @RequestMapping("/api/auth") dùng để định nghĩa đường dẫn gốc (base URL) cho tất cả các API trong controller đó. Các method bên trong controller sẽ nối thêm đường dẫn riêng vào base URL này.
- @ResponseEntity là một class trong Spring Boot (thuộc package org.springframework.http) được dùng để đại diện cho toàn bộ HTTP response trả về cho client.
- @PostMapping khi client gửi HTTP request dạng POST đến endpoint /register, thì Spring Boot sẽ gọi tới method trong Controller có gắn annotation này.
- Endpoint thực tế sẽ là: `POST /api/auth/register`
- Khi có một phương thức POST được gửi tới `/api/auth/register` phương thức register sẽ được chạy. Nó nhận thông tin của người dùng nhập từ `registerDto` dạng json trong phần body được gửi tới.
- Nó sẽ gọi hàm `registerUser` và truyền cho nó nội dung từ registerDto mà tạ nhận được - [Xem lại DTO](#step-3-tạo-dto-cho-phương-thức-đăng-ký) . Trong đó `userService` là nơi mà chúng ta đã định nghĩa các logic nghiệp vụ (ở đây là phương thức đăng ký người dùng) - [Xem lại Service](#step-4-tạo-class-service-cho-user-userservicejava).

### Step 6: Cấu hình SecurityConfig giúp kiểm soát truy cập tới trang web (SecurityConfig.java)
Ai (user nào, role nào) được truy cập API nào.<bt>
Dùng cơ chế xác thực nào (username/password, JWT, OAuth2, API key…).<bt>
Dùng session hay stateless (JWT).<bt>
Có bật CSRF không, có bật CORS không.<bt>
Các filter (ví dụ: filter kiểm tra JWT) được chạy như thế nào.

``` Java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers(
                                "/",
                                "/api/auth/**"
                        ).permitAll()
                        .anyRequest().authenticated());
        return http.build();
    }
}
```

- @Configuration đánh dấu class này là config của Spring — Spring sẽ đọc và đăng ký các @Bean bên trong.
- @EnableWebSecurity bật cấu hình Web Security của Spring Security (thường dùng để tùy chỉnh bảo mật).
- public SecurityFilterChain securityFilterChain(HttpSecurity http)
- Đây là bean trả về một SecurityFilterChain — chuỗi các filter bảo mật mà Spring sẽ áp dụng cho request.
- http.csrf(csrf -> csrf.disable()) Tắt CSRF protection.
- .authorizeHttpRequests(authorizeRequests -> authorizeRequests.requestMatchers(...).permitAll().anyRequest().authenticated()), requestMatchers(...) liệt kê các đường dẫn được cho phép không cần xác thực (permitAll()), .anyRequest().authenticated() — mọi request khác phải xác thực (login/token).

## Part 2: Setup JWT(findByEmail or findByUsername)

### Step 1: Implements UserDetails và override các phương thức của UserDetails trong model User (User.java)
UserDetails là giao diện chuẩn mà Spring Security dùng để quản lý thông tin người dùng.<br>
Dù bạn có model User như thế nào trong database, chỉ cần nó implement UserDetails thì Spring Security sẽ hiểu và sử dụng được.<br>
Kết hợp với UserDetailsService, bạn có thể load user từ DB và để Spring Security xác thực, phân quyền.

``` Java
public class User implements UserDetails {
	...

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

```

- getUsername() → trả về tên đăng nhập của user.
- getPassword() → trả về mật khẩu đã mã hóa, Spring Security sẽ dùng để so khớp khi login.
- getAuthorities() → trả về danh sách quyền (authorities) mà user có, dùng cho phân quyền (hasRole("ADMIN"), hasAuthority("READ_PRIVILEGE")...).
- isAccountNonExpired() → kiểm tra tài khoản có hết hạn không.
- isAccountNonLocked() → kiểm tra tài khoản có bị khóa không.
- isCredentialsNonExpired() → kiểm tra mật khẩu có hết hạn không.
- isEnabled() → kiểm tra user có được kích hoạt hay không.

### Step 2: Khởi tạo một file AuthConfig để tạo các bean cho JWT sử dụng (AuthConfig.java)
Bao gồm các hàm sau
1. Sử dụng BCryptPasswordEncoder để mã hóa mật khẩu
2. Sử dụng UserDetailsService để lấy được thông tin của user từ repository
3. Sử dụng AuthenticationProvider để xác minh người dùng dựa trên (1, 2)
4. Sử dụng AuthenticationManager để quản lý tất cả các cấu hình được định nghĩa trong AuthConfig


``` Java
@Configuration
public class AuthConfig {

    private UserRepository userRepository;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("User not found with username: " + username)
        );
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService());
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return  authenticationConfiguration.getAuthenticationManager();
    }
}
```

- @Configuration Đánh dấu class này là class cấu hình (configuration class). Khi Spring Boot khởi động, nó sẽ quét (component scan) và xử lý các class có @Configuration. Tất cả các method có @Bean trong đây sẽ được gọi để tạo ra các Bean (đối tượng) và đưa vào Spring Context (IoC Container).
- @Bean ánh dấu một method trả về một đối tượng → đối tượng đó sẽ được Spring quản lý trong IoC Container. Sau này ở bất kỳ đâu trong ứng dụng, bạn có thể @Autowired hoặc @RequiredArgsConstructor để lấy ra đối tượng này mà không cần new.
- BCryptPasswordEncoder Là một mã hóa mật khẩu dùng thuật toán BCrypt.
- AuthenticationProvider Là người chịu trách nhiệm xác thực. Ở đây sử dụng daoAuthenticationProvider và cung cấp cho nó thông tin User lấy từ UserDetailsService (UserDetails được implements trong model user - Xem lại ở đây)
- Tất cả các cấu hình bean trong nay được tổng hợp lại trong AuthenticationManager

### Step 3: Khởi tạo một file JwtService (JwtService.java)

``` Java
@Service
public class JwtService {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private Long expirationTime;

    public String generateToken(UserDetails user) {
        return generateToken(new HashMap<>(), user);
    }

    public String generateToken(HashMap<String, Object> map, UserDetails user) {
        return buideToken(map,user,expirationTime);
    }

    private String buildToken(HashMap<String, Object> map, UserDetails user, Long expirationTime) {
        return Jwts.builder()
                .claims(map)
                .issuedAt(new Date(System.currentTimeMillis()))
                .setSubject(user.getUsername())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS256, secretKey).compact();
    }

    public String getExpirationTime() {
        return expirationTime.toString();
    }
}
```

- @Service đây là cách đánh dấu trong một service để giúp spring boot có thể phát hiện
- @Value đây không phải thư việc lombok mà là của spring boot, nó cho phép lấy các biến được cấu hình trong properties.
- generateToken có 2 hàm 1 tham số và 2 tham số:
	- generateToken(UserDetails userDetails) hàm này nhận vào một object thông tin về user sau đó trả về một generateToken khác đó chính là hàm thứ 2 và cho nó thông tin về một HashMap, userDetails. map ở đây có thể không có giá trị.
	- generateToken(HashMap<String, Object> map, UserDetails user) nhận được tham số chi tiết từ cái 1 hoặc được nhận khi mà map được truyền từ ngoài vào.
	- Tại sao lại phải có 2 generateToken? -> bởi khi chỉ muốn tạo một token với thông tin của user thì có thể chỉ truyền UserDetails đó, trong trường hợp muốn thêm nội dung vào token ngoài UserDetails thì có thể gán cho map và tạo token với thông tin đã được bao gồm.
- buildToken với hàm này nó nhận một map từ generateToken (đầu vào của nó có thể là một map rỗng hoặc một map có giá trị, các giá trị ở đây là claim được thêm vào).
	- .claim đây là các giá trị được thêm vào thông qua map, các giá trị bên ngoài UserDetails. claim là thông tin lưu trong payload của JWT, ngoài sub ra có thể là email, role, userId…
	- .issuedAt đây là thời gian tạo token
	- .setSubject đây là thông tin nhận dạng chính ở đây là username
	- .setExpiration đây là thời hạn của token nó bằng thời gian tạo + thời gian của token được set trong properties
	- .signWith đây là phương ký của JWT với secretKey được set trong properties, signWith + secretKey giúp xác thực token, tránh người khác tạo token giả
	- .compact đây là cách đóng gói toàn bộ trong jwt tạo chuỗi token final sẵn sàng trả về client.
- getExpirationTime hàm này lấy ra khoảng thời gian hết hạn của token lấy từ application.properties

### Step 4: Bổ xung mã hóa mật khẩu trong UserService (UserService.java - Xem lại tại đây)

``` Java
public class UserService {
	...
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public User getUserByUsername(String username) throws Exception {
        ...
    }

    public User registerUser(RegisterDto registerDto) throws IllegalAccessException {
        ...
        user.setPassword(bCryptPasswordEncoder.encode(registerDto.getPassword()));
        ...
    }
}
```

- Bổ xung bCryptPasswordEncoder đã được cấu hình trong AuthConfig, nếu không bổ xung khi đăng ký mật khẩu sẽ không được mã hóa trước khi lưu vào database.

### Step 5: Tạo DTO cho phương thức đăng nhập (gửi lên server)
DTO là một object dùng để truyền dữ liệu giữa các lớp hoặc các tầng (layer) trong ứng dụng, đặc biệt giữa client ↔ server.<br>
DTO chỉ chứa dữ liệu, không chứa logic nghiệp vụ.<br>
Nó thường được dùng để ẩn bớt thông tin nhạy cảm (như password), hoặc chuyển đổi dữ liệu từ entity sang dạng mà client dễ hiểu.

``` Java
@Data
public class LoginDto
{
    private String username;
    private String password;
}
```
- @Data cho phép lombok tự tạo @Getter, @Setter, @ToString, @EqualsAndHashCode, @RequiredArgsConstructor cho các biến field
- DTO là “gói dữ liệu trung gian” dùng để truyền giữa client và server, chỉ mang những thông tin cần thiết thay vì toàn bộ dữ liệu gốc trong Entity.

### Step 6: Tạo DTO cho phương thức đăng đăng nhập (server phản hồi)
DTO là một object dùng để truyền dữ liệu giữa các lớp hoặc các tầng (layer) trong ứng dụng, đặc biệt giữa client ↔ server.<br>
DTO chỉ chứa dữ liệu, không chứa logic nghiệp vụ.<br>
Nó thường được dùng để ẩn bớt thông tin nhạy cảm (như password), hoặc chuyển đổi dữ liệu từ entity sang dạng mà client dễ hiểu.

``` Java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto
{
    private String token;
    private String username;
    private String time;
}
```
- @Data cho phép lombok tự tạo @Getter, @Setter, @ToString, @EqualsAndHashCode, @RequiredArgsConstructor cho các biến field
- @Builder Tạo pattern Builder cho class. Giúp bạn khởi tạo object dễ đọc, rõ ràng hơn thay vì truyền nhiều tham số trong constructor.
- @@NoArgsConstructor Tạo constructor rỗng (không tham số).
- @@AllArgsConstructor Tạo constructor với tất cả các tham số tương ứng với field trong class.
- DTO là “gói dữ liệu trung gian” dùng để truyền giữa client và server, chỉ mang những thông tin cần thiết thay vì toàn bộ dữ liệu gốc trong Entity.


### Step 7: Tạo api POST /login trong AuthController (AuthController.java - Xem lại tại đây)

``` Java
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginDto loginDto){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword())
        );
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String jwtToken = jwtService.generateToken(userDetails);
        LoginResponseDto loginResponse = LoginResponseDto.builder()
                .username(loginDto.getUsername())
                .token(jwtToken)
                .time(jwtService.getExpirationTime())
                .build();
        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }
```

- @PostMapping khi clientgửi HTTP request dạng POST đến endpoint /login, thì Spring Boot sẽ gọi tới method trong Controller có gắn annotation này.
- @ResponseEntity là một class trong Spring Boot (thuộc package org.springframework.http) được dùng để đại diện cho toàn bộ HTTP response trả về cho client. Ở đây nó nhận vào một mẫu trả về (object) là LoginResponseDto - Xem lại tại đây. Nó dữ liệu nhập vào từ một object là loginDto - xem lại tại đây.
- Xác nhận đăng nhập, so sánh mật khẩu và lấy ra thông tin người dùng đã xác thực:
	1. Khởi tạo một biến một biến tham chiếu authentication kiểu Authentication. Biến này nhận kết quả từ authenticationManager.authenticate(...), trong đó ta truyền vào một UsernamePasswordAuthenticationToken chứa username và password lấy từ loginDto. Nếu username/password hợp lệ → biến authentication sẽ chứa đầy đủ thông tin user đã xác thực.
	2. Khởi tạo một đối tượng biến tham chiếu userDetails kiểu UserDetails. Khi biến authentication có được thông tin user đã xác thực ta lấy thông tin bằng cách sử dụng .getPrincipal() và ép kiểu về (UserDetails). Vậy giờ đã có được thông tin của người đã xác thực.
- Tạo, gửi JwtToken và gửi lại cho người dùng thông tin cần thiết:
	1. Tạo một biến tham chiếu tới lớp JwtService và sử dụng phương thức .generateToken() truyền cho nó thông của UserDetails đã được xác thực, sau đó trả về token đã được tạo - xem lại tại đây.
	2. Khởi tạo đối tượng trả về LoginResponseDto sử dụng @build đã được cấu hình trong file LoginResponseDto.java - xem lại tại đây, ta truyền các tham số cần trả về cho người dùng như token, thời gian, tên người dùng...

### Step 8: Tạo filter
