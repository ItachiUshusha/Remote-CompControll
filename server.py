import socket
import os

port = 10000
host = '192.168.0.106'  
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
sock.bind((host, port))
sock.listen(1)
print(f"Сервер слушает на {host}:{port}")
conn, addr = sock.accept()
print(f"Соединен с {addr[0]}:{addr[1]}")

data_out = conn.makefile('w', encoding='utf-8')
data_in = conn.makefile('r', encoding='utf-8')

while True:
    # Получаем данные от клиента
    data = data_in.readline().strip()  # Читаем строку от клиента
    if not data:
        print("Клиент отключился")
        break  # Если данных нет, завершаем соединение

    print(f"Ответ от клиента: {data}")

    match(data):
        case "Hello from android":
            data_out.write(f"Hello from server\n")
            data_out.flush()
        case "Power off":
            data_out.write(f"Power\n")
            os.system('shutdown /s /f /t 1')
            data_out.flush()

conn.close()
