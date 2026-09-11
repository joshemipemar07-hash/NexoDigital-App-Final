import flet as ft
from api.fake_store import FakeStoreAPI

class LoginView(ft.View):
    def __init__(self, on_login_success):
        super().__init__(route="/login")
        self.on_login_success = on_login_success

        self.vertical_alignment = ft.MainAxisAlignment.CENTER
        self.horizontal_alignment = ft.CrossAxisAlignment.CENTER

        self.txt_user = ft.TextField(label="Usuario", width=300, value="johnd")
        self.txt_pass = ft.TextField(label="Contraseña", password=True, can_reveal_password=True, width=300, value="m38rmF$")
        self.lbl_error = ft.Text(color=ft.Colors.RED)
        self.btn_login = ft.ElevatedButton("Iniciar Sesión", on_click=self.login_click)

        self.controls = [
            ft.Text("Iniciar Sesión", size=24, weight=ft.FontWeight.BOLD),
            self.txt_user,
            self.txt_pass,
            self.btn_login,
            self.lbl_error
        ]

    def login_click(self, e):
        self.lbl_error.value = ""
        self.page.update()

        usr = self.txt_user.value.strip()
        pwd = self.txt_pass.value.strip()

        if not usr or not pwd:
            self.lbl_error.value = "Por favor ingrese usuario y contraseña."
            self.page.update()
            return

        if not FakeStoreAPI.verificar_conexion():
            self.lbl_error.value = "Sin conexión a internet o API no disponible."
            self.page.update()
            return

        resultado = FakeStoreAPI.login(usr, pwd)

        if resultado and resultado.get("token"):
            user_data = resultado.get("user", {})
            user_id = user_data.get("id", 1)
            
            if user_id in [1, 2]:
                rol = "Administrador"
            elif user_id == 3:
                rol = "Auditor"
            else:
                rol = "Cliente"

            self.txt_user.value = ""
            self.txt_pass.value = ""
            
            self.on_login_success({
                "token": resultado["token"],
                "role": rol,
                "user_id": user_id,
                "user_details": user_data
            })
        else:
            self.lbl_error.value = "Usuario o contraseña incorrectos en FakeStore API."
            self.page.update()