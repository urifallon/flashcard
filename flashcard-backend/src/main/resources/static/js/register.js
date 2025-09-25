document.getElementById('registerForm').addEventListener('submit', function(event) {
      // Ngăn chặn hành vi mặc định của form là tải lại trang
      event.preventDefault();

      // Lấy giá trị từ các ô input
      const name = document.getElementById('name').value;
      const email = document.getElementById('email').value;
      const password = document.getElementById('password').value;

      const messageDiv = document.getElementById('message');

      // Tạo đối tượng dữ liệu để gửi đi, cấu trúc phải khớp với RegisterDto
      const data = {
          name: name,
          email: email,
          password: password
      };

      // Dùng Fetch API của trình duyệt để gọi đến backend
      fetch('/api/auth/register', {
          method: 'POST', // Phương thức là POST
          headers: {
              'Content-Type': 'application/json' // Khai báo nội dung gửi đi là JSON
          },
          body: JSON.stringify(data) // Chuyển đối tượng JavaScript thành chuỗi JSON
      })
      .then(response => {
          // Khi nhận được phản hồi từ server, kiểm tra xem nó thành công hay thất bại
          if (response.ok) {
              // Nếu response.ok là true (tức là status code 2xx)
              return response.json(); // Chuyển body của response thành đối tượng JSON
          } else {
              // Nếu thất bại (status code 4xx, 5xx)
              // Đọc response dưới dạng text (vì message lỗi của chúng ta là text)
              return response.text().then(text => { throw new Error(text) });
          }
      })
      .then(data => {
          // Xử lý khi đăng ký thành công
          messageDiv.textContent = 'Đăng ký thành công! ID người dùng: ' + data.id;
          messageDiv.className = 'success';
      })
      .catch(error => {
          // Xử lý khi có lỗi xảy ra (lỗi mạng hoặc lỗi từ server)
          messageDiv.textContent = error.message;
          messageDiv.className = 'error';
      });
});